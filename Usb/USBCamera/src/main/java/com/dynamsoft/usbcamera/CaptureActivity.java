package com.dynamsoft.usbcamera;

import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class CaptureActivity extends BaseActivity implements CameraDialog.CameraDialogParent {

    private static final boolean DEBUG = true;    // TODO set false on release

    public static final String TAG = "!!!";

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

    private USBMonitor.UsbControlBlock firstBlock;

    private USBMonitor.UsbControlBlock secondBlock;

    private UsbDevice firstCamera;

    private UsbDevice secondCamera;

    private Button firstCameraButton;

    private Button secondCameraButton;

    private UVCCameraHandler mCameraHandler;

    private CameraViewInterface mUVCCameraView;

    private ImageView imageOld;

    private ImageView imageNew;

    private boolean isActive = false;

    private Date startTime;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate:");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_capture);
        imageOld = (ImageView) findViewById(R.id.image_view);
        imageOld.setVisibility(View.INVISIBLE);

        imageNew = (ImageView) findViewById(R.id.image_view_new);

        Button scan = (Button) findViewById(R.id.scan_button);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraHandler.setPreviewCallback(mIFrameCallback);
//                if(bitmap != null){
//                    processImage(bitmap);
//                }
            }
        });

        Button test = (Button) findViewById(R.id.test_button);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraHandler.setPreviewCallback(mIFrameCallback);
//                if(bitmap != null){
//                    processImage(bitmap);
//                }
            }
        });


        firstCameraButton = (Button) findViewById(R.id.first_button);
        firstCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.d(TAG, "firstB");

                try {
                    if(firstBlock == null)
                        firstBlock = mUSBMonitor.openDevice(firstCamera);

                    startPreview(firstBlock);
                }catch (Exception ex){
                    Log.w(TAG, "EXCEPTION in OnClickListener first_button! " + ex.getMessage());
                }

            }
        });

        secondCameraButton = (Button) findViewById(R.id.second_button);
        secondCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Log.d(TAG, "secondB");

                try {
                    if(secondBlock == null)
                        secondBlock = mUSBMonitor.openDevice(secondCamera);

                    startPreview(secondBlock);

                }catch (Exception ex){
                    Log.w(TAG, "EXCEPTION in OnClickListener second_button! " + ex.getMessage());
                }
            }
        });

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
        Log.v(TAG, "onDestroy:");
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
            Log.d(TAG,"ATTACHED " + device.getDeviceName() );
            Toast.makeText(CaptureActivity.this, "ATTACHED " + device.getDeviceName(), Toast.LENGTH_SHORT).show();

            if(firstCamera == null)
                firstCamera = device;
            else
                secondCamera = device;

            mUSBMonitor.requestPermission(device);
        }

        @Override
        public void onConnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock, final boolean createNew) {
            if (DEBUG) Log.v(TAG, "onConnect: " + device.getDeviceName());

            try {
                synchronized (mSync) {
                    if (firstBlock == null && device.getDeviceName().equals(firstCamera.getDeviceName())) {
                        firstBlock = ctrlBlock;
                    }
                    else if (secondBlock == null && device.getDeviceName().equals(secondCamera.getDeviceName())) {
                        secondBlock = ctrlBlock;
                    }

                    startPreview(ctrlBlock);
                }
            }catch (Exception ex){
                Log.w(TAG, Objects.requireNonNull(ex.getMessage()));
            }
        }

        @Override
        public void onDisconnect(final UsbDevice device, final USBMonitor.UsbControlBlock ctrlBlock) {
            if (DEBUG) Log.v(TAG, "onDisconnect:");

            synchronized (mSync) {
                if (mCameraHandler != null) {
                    try {
                        // maybe throw java.lang.IllegalStateException: already released
                        mCameraHandler.setPreviewCallback(null); //zhf
                        mCameraHandler.close();

                        if(ctrlBlock == firstBlock){
                            Log.d(TAG, "ctrlBlock == secondBlock");
                            firstBlock = null;
                        }

                        if(ctrlBlock == secondBlock){
                            Log.d(TAG, "ctrlBlock == secondBlock");
                            secondBlock = null;
                        }

                    } catch (Exception ex) {
                        Log.w(TAG, Objects.requireNonNull(ex.getMessage()));
                    }
                }
            }
        }

        @Override
        public void onDettach(final UsbDevice device) {
            Toast.makeText(CaptureActivity.this, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();

            if(device != null) {
                if (device.getDeviceName().equals(firstCamera.getDeviceName()))
                    firstCamera = null;

                if (device.getDeviceName().equals(secondCamera.getDeviceName()))
                    secondCamera = null;
            }
        }

        @Override
        public void onCancel(final UsbDevice device) {
            if (device.getDeviceName().equals(firstCamera.getDeviceName()))
                firstCamera = null;

            if (device.getDeviceName().equals(secondCamera.getDeviceName()))
                secondCamera = null;
        }
    };


    private void startPreview(USBMonitor.UsbControlBlock ctrlBlock){
        synchronized (mSync) {
            try {
                Log.d(TAG, "startPreview");

                if(isActive){
                    Log.d(TAG, "Уже есть активная камера");
                    return;
                }

                if (mCameraHandler != null) {

                    if(mCameraHandler.isPreviewing())
                        mCameraHandler.stopPreview();

                    mCameraHandler.open(ctrlBlock);
//                    mCameraHandler1.setPreviewCallback(mIFrameCallback);
                    mCameraHandler.startPreview(new Surface(mUVCCameraView.getSurfaceTexture()));
                    isActive = true;
                }
            }catch (Exception ex){
                Log.w(TAG, Objects.requireNonNull(ex.getMessage()));
            }
        }
    }


    private void captureStillImage() {
        if (mCameraHandler != null && mCameraHandler.isOpened()) {
            // Генерация уникального имени файла на основе времени
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "IMG_" + timeStamp + ".jpg";

            // Путь к папке для сохранения изображений
            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyAppImages");
            if (!storageDir.exists()) {
                storageDir.mkdirs(); // Создание папки, если она не существует
            }

            // Полный путь к файлу
            String filePath = new File(storageDir, fileName).getAbsolutePath();

            // Захват изображения и сохранение по указанному пути
            mCameraHandler.captureStill(fileName);

            mCameraHandler.captureStill();

            Toast.makeText(this, "Image captured: " + filePath, Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Image captured: " + filePath);
        } else {
            Toast.makeText(this, "Camera is not opened", Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Camera is not opened");
        }
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

    Bitmap bitmapOld;
    Bitmap bitmapNew;

    BitmapWrapper mainBitmap;
    BitmapWrapper sideBitmap;

    Bitmap bitmap = Bitmap.createBitmap(PREVIEW_WIDTH, PREVIEW_HEIGHT, Bitmap.Config.RGB_565);

    boolean check = false;

    private final Timer timer = new Timer();

    private final IFrameCallback mIFrameCallback = new IFrameCallback() {
        @Override
        public void onFrame(final ByteBuffer frame) {
            Log.d(TAG, "OnFrame");
            frame.clear();

            if(mainBitmap == null)
                mainBitmap = new BitmapWrapper(PREVIEW_WIDTH, PREVIEW_HEIGHT, frame);

            else if(sideBitmap == null)
                sideBitmap = new BitmapWrapper(PREVIEW_WIDTH, PREVIEW_HEIGHT, frame);

            else{
                mCameraHandler.setPreviewCallback(null);
                processImages();
            }
        }
    };

    private final IFrameCallback mIFrameCallback_one_send = new IFrameCallback() {
        @Override
        public void onFrame(final ByteBuffer frame) {
            Log.d(TAG, "OnFrame");
            mCameraHandler.setPreviewCallback(null);
            frame.clear();

            bitmap.copyPixelsFromBuffer(frame);

//            imageNew.post(mUpdateImageTask);

            processImage(bitmap);
        }
    };

    private final IFrameCallback mIFrameCallback_old_new = new IFrameCallback() {
        @Override
        public void onFrame(final ByteBuffer frame) {
            Log.d(TAG, "OnFrame");
            mCameraHandler.setPreviewCallback(null);
            frame.clear();

            if(!check) {
                check = true;
                startTime = new Date();
                timer.schedule(new TimerTask() {
                    public void run() {
                        if(mCameraHandler.isOpened() && mCameraHandler != null){
                            Log.d(TAG, "TASK");
                            mCameraHandler.setPreviewCallback(mIFrameCallback);
                        }
                    }
                }, startTime, 100);
            }

            if(bitmapOld == null) {
                Log.d(TAG, "bitmapOld == null");
                bitmapOld = Bitmap.createBitmap(PREVIEW_WIDTH, PREVIEW_HEIGHT, Bitmap.Config.RGB_565);
                bitmapOld.copyPixelsFromBuffer(frame);
                imageOld.setImageBitmap(bitmapOld);
            }

            else if (bitmapNew == null){
                Log.d(TAG, "bitmapNew == null");
                bitmapNew = Bitmap.createBitmap(PREVIEW_WIDTH, PREVIEW_HEIGHT, Bitmap.Config.RGB_565);
                bitmapNew.copyPixelsFromBuffer(frame);
                imageNew.setImageBitmap(bitmapNew);
            }
            else{
                Log.d(TAG, "else");
                bitmapOld = Bitmap.createBitmap(bitmapNew);
                imageOld.setImageBitmap(bitmapOld);

                bitmapNew.copyPixelsFromBuffer(frame);
                imageNew.setImageBitmap(bitmapNew);
            }
        }
    };

    private final Runnable mUpdateImageTask = new Runnable() {
        @Override
        public void run() {
            synchronized (bitmap) {
                imageNew.setImageBitmap(bitmap);
            }
        }
    };

    private void processImages(){
        PythonApi.processImages(this, mainBitmap.getBitmap(), sideBitmap.getBitmap(), imageNew, () -> {
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

    private void processImage(Bitmap bitmap){
        PythonApi.processImage(this, bitmap, imageNew, () -> {
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