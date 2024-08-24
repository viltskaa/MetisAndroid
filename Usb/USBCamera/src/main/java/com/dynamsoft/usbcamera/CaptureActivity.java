package com.dynamsoft.usbcamera;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.dynamsoft.usbcamera.models.BitmapWrapper;
import com.serenegiant.common.BaseActivity;
import com.serenegiant.usb.CameraDialog;
import com.serenegiant.usb.IFrameCallback;
import com.serenegiant.usb.USBMonitor;
import com.serenegiant.usbcameracommon.UVCCameraHandler;
import com.serenegiant.widget.CameraViewInterface;

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class CaptureActivity extends BaseActivity implements CameraDialog.CameraDialogParent {

    private static final boolean DEBUG = true;    // TODO set false on release

    public static final String TAG = "!";

    private final Object mSync = new Object();

    /**
     * set true if you want to record movie using MediaSurfaceEncoder
     * (writing frame data into Surface camera from MediaCodec
     * by almost same way as USBCameratest2)
     * set false if you want to record movie using MediaVideoEncoder
     */
    private static final boolean USE_SURFACE_ENCODER = false;

    private static final int PREVIEW_WIDTH = 1920;

    private static final int PREVIEW_HEIGHT = 1080;

    /**
     * preview mode
     * 0:YUYV, other:MJPEG
     */
    private static final int PREVIEW_MODE = 1;

    private USBMonitor mUSBMonitor;

    private USBMonitor.UsbControlBlock mainBlock;

    private USBMonitor.UsbControlBlock sideBlock;

    private UsbDevice mainCamera;

    private UsbDevice sideCamera;

    private UVCCameraHandler mCameraHandler;

    private CameraViewInterface mUVCCameraView;

    private ImageView sideImage;

    private ImageView mainImage;

    private boolean isActive = false;

    private Date startTime;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Log.d(TAG, "onCreate");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_capture);
        sideImage = (ImageView) findViewById(R.id.image_view_side);
//        sideImage.setVisibility(View.INVISIBLE);

        mainImage = (ImageView) findViewById(R.id.image_view_new);

        Button scan = (Button) findViewById(R.id.scan_button);
        scan.setOnClickListener(view -> {
            mCameraHandler.setPreviewCallback(mIFrameCallback);
//                if(bitmap != null){
//                    processImage(bitmap);
//                }
        });

        Button test = (Button) findViewById(R.id.test_button);
        test.setOnClickListener(view -> {
            mCameraHandler.setPreviewCallback(mIFrameCallback);
//                if(bitmap != null){
//                    processImage(bitmap);
//                }
        });


        Button mainCameraButton = (Button) findViewById(R.id.first_button);
        mainCameraButton.setOnClickListener(view -> previewMainCamera());

        Button sideCameraButton = (Button) findViewById(R.id.second_button);
        sideCameraButton.setOnClickListener(view -> previewSideCamera());

        mUVCCameraView = (CameraViewInterface) findViewById(R.id.camera_view);
        mUVCCameraView.setAspectRatio(PREVIEW_WIDTH / (double) PREVIEW_HEIGHT);

        synchronized (mSync) {
            mUSBMonitor = new USBMonitor(this, mOnDeviceConnectListener);

            mCameraHandler = UVCCameraHandler.createHandler(this, mUVCCameraView,
                    USE_SURFACE_ENCODER ? 0 : 1, PREVIEW_WIDTH, PREVIEW_HEIGHT, PREVIEW_MODE);

            mCameraHandler.addCallback(callback);

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (DEBUG) Log.i(TAG, "onStart");

        synchronized (mSync) {
            mUSBMonitor.register();
        }
        if (mUVCCameraView != null) {
            mUVCCameraView.onResume();
        }

    }

    @Override
    protected void onStop() {
        if (DEBUG) Log.i(TAG, "onStop");

        synchronized (mSync) {
            mCameraHandler.close();
            mUSBMonitor.unregister();
        }
        if (mUVCCameraView != null)
            mUVCCameraView.onPause();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy");
        synchronized (mSync) {
            if (mCameraHandler != null) {
                mCameraHandler.setPreviewCallback(null);
                mCameraHandler.release();
                mCameraHandler = null;
            }

            if (mUSBMonitor != null) {
                mUSBMonitor.destroy();
                mUSBMonitor = null;
            }
        }
        super.onDestroy();
    }


    private final USBMonitor.OnDeviceConnectListener mOnDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            if (DEBUG) Log.d(TAG,"ATTACHED " + device.getDeviceId() );
            Toast.makeText(CaptureActivity.this, "ATTACHED " + device.getDeviceId(), Toast.LENGTH_SHORT).show();

            if(mainCamera == null && device.getDeviceId() % 2 == 1)
                mainCamera = device;
            else if (sideCamera == null && device.getDeviceId() % 2 == 0)
                sideCamera = device;

            mUSBMonitor.requestPermission(device);
        }

        @Override
        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
            if (DEBUG) Log.d(TAG, "onConnect: " + device.getDeviceId());
            if (DEBUG) Log.d(TAG, "createNew " + createNew);

            try {
                synchronized (mSync) {
                    if (mainBlock == null && mainCamera != null && device.getDeviceId() == mainCamera.getDeviceId()) {
                        mainBlock = ctrlBlock;
                        startPreview(ctrlBlock);
                    }
                    else if (sideBlock == null && sideCamera != null && device.getDeviceId() == sideCamera.getDeviceId()) {
                        sideBlock = ctrlBlock;
                    }
                }
            }catch (Exception ex){
                Log.e(TAG, "EXCEPTION in onConnect! " + ex.getMessage());
            }
        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
            if (DEBUG) Log.d(TAG, "onDisconnect: " + device.getDeviceId());
            if (DEBUG) Log.d(TAG, " ctrlBlock.getDeviceId()" + ctrlBlock.getDeviceId());

            synchronized (mSync) {
                if (mCameraHandler != null) {
                    try {
                        // maybe throw java.lang.IllegalStateException: already released
                        mCameraHandler.setPreviewCallback(null); //zhf

                        mCameraHandler.close();

                        if(ctrlBlock.getDeviceId() == mainBlock.getDeviceId()){
                            if (DEBUG) Log.d(TAG, "ctrlBlock == secondBlock");
                            mainBlock = null;
                        }

                        if(ctrlBlock.getDeviceId() == sideBlock.getDeviceId()){
                            if (DEBUG) Log.d(TAG, "ctrlBlock == secondBlock");
                            sideBlock = null;
                        }

                    } catch (Exception ex) {
                        Log.w(TAG, "EXCEPTION in onDisconnect! " + ex.getMessage());
                    }
                }
            }
        }

        @Override
        public void onDettach(final UsbDevice device) {
            Toast.makeText(CaptureActivity.this, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();

            if(device != null) {
                if (device.getDeviceId() == mainCamera.getDeviceId())
                    mainCamera = null;

                if (device.getDeviceId() == sideCamera.getDeviceId())
                    sideCamera = null;
            }
        }

        @Override
        public void onCancel(final UsbDevice device) {
            if (device.getDeviceId() == mainCamera.getDeviceId())
                mainCamera = null;

            if (device.getDeviceId() == sideCamera.getDeviceId())
                sideCamera = null;
        }
    };

    private void previewMainCamera(){
        if (DEBUG) Log.d(TAG, "previewMainCamera");

        try {
            if(mainBlock == null)
                mainBlock = mUSBMonitor.openDevice(mainCamera);

            startPreview(mainBlock);

        }catch (Exception ex){
            Log.w(TAG, "EXCEPTION in previewMainCamera! " + ex.getMessage());
        }
    }

    private void previewSideCamera(){
        if (DEBUG) Log.d(TAG, "previewSideCamera");

        try {
            if(sideBlock == null)
                sideBlock = mUSBMonitor.openDevice(sideCamera);

            startPreview(sideBlock);
            isSide = true;
            if (DEBUG) Log.d(TAG, "isSide = true");
            mCameraHandler.setPreviewCallback(mIFrameCallback);
        }catch (Exception ex){
            Log.w(TAG, "EXCEPTION in previewSideCamera! " + ex.getMessage());
        }
    }

    private void startPreview(USBMonitor.UsbControlBlock ctrlBlock){
        synchronized (mSync) {
            try {
                Log.d(TAG, "startPreview: " + ctrlBlock.getDeviceId());

                if (mCameraHandler != null) {
                    Log.d(TAG, "1: ");

//                    ctrlBlock.close();
//                    mCameraHandler.stopPreview();
                    Log.d(TAG, "2: ");

                    mCameraHandler.open(ctrlBlock);
                    Log.d(TAG, "3: ");
//                    mCameraHandler1.setPreviewCallback(mIFrameCallback);

                    SurfaceTexture surfaceTexture = mUVCCameraView.getSurfaceTexture();
                    if(surfaceTexture == null){
                        Log.d(TAG, "surfaceTexture == null");
                        return;
                    }
                    Log.d(TAG, "4: ");
                    Surface surface = new Surface(surfaceTexture);

                    mCameraHandler.startPreview(surface);
                    Log.d(TAG, "5: ");

                    isActive = true;
                }
            }catch (Exception ex){
                Log.e(TAG, "EXCEPTION in startPreview! " + ex.getMessage());
            }
        }
    }


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

    private final UVCCameraHandler.CameraCallback callback = new UVCCameraHandler.CameraCallback(){

        @Override
        public void onOpen() {

        }

        @Override
        public void onClose() {

        }

        @Override
        public void onStartPreview() {

        }

        @Override
        public void onStopPreview() {

        }

        @Override
        public void onStartRecording() {

        }

        @Override
        public void onStopRecording() {

        }

        @Override
        public void onError(Exception e) {
            Log.d(TAG, "CameraCallback : onError " + e.getMessage());
        }
    };

    @Override
    public USBMonitor getUSBMonitor() {
        synchronized (mSync) {
            return mUSBMonitor;
        }
    }

    @Override
    public void onDialogResult(boolean canceled) {
        if (DEBUG) Log.v(TAG, "onDialogResult:canceled=" + canceled);
    }
}