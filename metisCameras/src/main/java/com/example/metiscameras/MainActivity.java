package com.example.metiscameras;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.metiscameras.models.BitmapWrapper;
import com.serenegiant.common.BaseActivity;
import com.serenegiant.usb_libuvccamera.CameraDialog;
import com.serenegiant.usb_libuvccamera.IFrameCallback;
import com.serenegiant.usb_libuvccamera.LibUVCCameraUSBMonitor;
import com.serenegiant.usbcameracommon.UVCCameraHandler;
import com.serenegiant.usb_libuvccamera.LibUVCCameraUSBMonitor.UsbControlBlock;
import com.serenegiant.usb_libuvccamera.LibUVCCameraUSBMonitor.OnDeviceConnectListener;
import com.serenegiant.widget.CameraViewInterface;

import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

public final class MainActivity extends BaseActivity implements CameraDialog.CameraDialogParent {
    private static final boolean DEBUG = false;	// FIXME set false when production
    private static final String TAG = "!";

    private static final int PREVIEW_WIDTH = 1920;

    private static final int PREVIEW_HEIGHT = 1080;

    private LibUVCCameraUSBMonitor usbMonitor;

    private UVCCameraHandler mainHandler;
    private CameraViewInterface mainView;
    private Surface mainSurface;
    private ImageView mainImage;

    private UVCCameraHandler sideHandler;
    private CameraViewInterface sideView;
    private Surface sideSurface;
    private ImageView sideImage;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sideView = (CameraViewInterface)findViewById(R.id.camera_view_L);
        sideImage = (ImageView) findViewById(R.id.image_view_side);
        sideHandler = UVCCameraHandler.createHandler(this, sideView, PREVIEW_WIDTH, PREVIEW_HEIGHT, 0.5f);

        mainView = (CameraViewInterface)findViewById(R.id.camera_view_R);
        mainHandler = UVCCameraHandler.createHandler(this, mainView, PREVIEW_WIDTH, PREVIEW_HEIGHT, 0.5f);
        usbMonitor = new LibUVCCameraUSBMonitor(this, mOnDeviceConnectListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissionCamera();
        usbMonitor.register();
        if (mainView != null)
            mainView.onResume();
        if (sideView != null)
            sideView.onResume();
    }

    @Override
    protected void onStop() {
        mainHandler.close();
        if (mainView != null)
            mainView.onPause();
        sideHandler.close();

        if (sideView != null)
            sideView.onPause();
        usbMonitor.unregister();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mainHandler != null) {
            mainHandler = null;
        }
        if (sideHandler != null) {
            sideHandler = null;
        }
        if (usbMonitor != null) {
            usbMonitor.destroy();
            usbMonitor = null;
        }
        mainView = null;
        sideView = null;
        super.onDestroy();
    }

    private final OnDeviceConnectListener mOnDeviceConnectListener = new OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            if (DEBUG) { Log.v(TAG, "onAttach:" + device); }
            Toast.makeText(MainActivity.this, "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnect(final UsbDevice device, final UsbControlBlock ctrlBlock, final boolean createNew) {
            if (DEBUG) { Log.v(TAG, "onConnect:" + device); }
            if (!sideHandler.isOpened()) {
                sideHandler.open(ctrlBlock);
                final SurfaceTexture st = sideView.getSurfaceTexture();
                sideHandler.startPreview(new Surface(st));

            } else if (!mainHandler.isOpened()) {
                mainHandler.open(ctrlBlock);
                final SurfaceTexture st = mainView.getSurfaceTexture();
                mainHandler.startPreview(new Surface(st));

            }
        }

        @Override
        public void onDisconnect(final UsbDevice device, final UsbControlBlock ctrlBlock) {
            if (DEBUG) { Log.v(TAG, "onDisconnect:" + device); }
            if ((sideHandler != null) && !sideHandler.isEqual(device)) {
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        sideHandler.close();
                        if (sideSurface != null) {
                            sideSurface.release();
                            sideSurface = null;
                        }
                    }
                }, 0);
            } else if ((mainHandler != null) && !mainHandler.isEqual(device)) {
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mainHandler.close();
                        if (mainSurface != null) {
                            mainSurface.release();
                            mainSurface = null;
                        }
                    }
                }, 0);
            }
        }

        @Override
        public void onDettach(final UsbDevice device) {
            if (DEBUG) { Log.v(TAG, "onDettach:" + device); }
            Toast.makeText(MainActivity.this, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(final UsbDevice device) {
            if (DEBUG) { Log.v(TAG, "onCancel:"); }
        }
    };

    BitmapWrapper mainBitmap;
    BitmapWrapper sideBitmap;

    private final Bitmap bitmap = Bitmap.createBitmap(PREVIEW_WIDTH, PREVIEW_HEIGHT, Bitmap.Config.RGB_565);

    private final Timer timer = new Timer();

    private boolean isSide = false;

    private final IFrameCallback mIFrameCallback = new IFrameCallback() {
        @Override
        public void onFrame(final ByteBuffer frame) {
            Log.d(TAG, "OnFrame");
            frame.clear();

            if(mainBitmap == null) {
                mainBitmap = new BitmapWrapper(PREVIEW_WIDTH, PREVIEW_HEIGHT, frame);
                mainImage.post(() -> mainImage.setImageBitmap(mainBitmap.getBitmap()));
            }
            else if(sideBitmap == null) {
                if(!isSide){
                    mainBlock.close();
                }
                else {
                    sideBitmap = new BitmapWrapper(PREVIEW_WIDTH, PREVIEW_HEIGHT, frame);
                    sideImage.post(() -> sideImage.setImageBitmap(sideBitmap.getBitmap()));
                }
            }
            else{
                mCameraHandler.setPreviewCallback(null);
                processImages();
            }
        }
    };

    private final Runnable mUpdateImageTask = new Runnable() {
        @Override
        public void run() {
            synchronized (bitmap) {
                mainImage.setImageBitmap(bitmap);
            }
        }
    };

    private void processImages(){
        Log.d(TAG, "processImages");
//        PythonApi.processImages(this, mainBitmap.getBitmap(), sideBitmap.getBitmap(), imageNew, () -> {
//            Log.d(TAG, "CallBAck");
//            timer.schedule(new TimerTask() {
//                public void run() {
//                    if(mCameraHandler.isOpened() && mCameraHandler != null){
//                        Log.d(TAG, "TASK");
//                        mCameraHandler.setPreviewCallback(mIFrameCallback);
//                    }
//                }
//            }, 10 * 1000);
//        });
    }

    private void processImage(Bitmap bitmap){
        PythonApi.processImage(this, bitmap, mainImage, () -> {
            Log.d(TAG, "CallBAck");
            timer.schedule(new TimerTask() {
                public void run() {
                    if(mCameraHandler.isOpened() && mCameraHandler != null){
                        Log.d(TAG, "TASK");
                        mCameraHandler.setPreviewCallback(mIFrameCallback);
                    }
                }
            }, 10 * 1000);
        });
    }

    @Override
    public LibUVCCameraUSBMonitor getUSBMonitor() {
        return usbMonitor;
    }

    @Override
    public void onDialogResult(boolean canceled) {
    }
}