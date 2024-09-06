package com.example.metiscameras.activities;

import static com.example.metiscameras.api.Utils.toBitmap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Surface;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.metiscameras.R;
import com.example.metiscameras.api.TableTopPatternApi;
import com.example.metiscameras.api.TableTopApi;
import com.example.metiscameras.api.responses.FindPatternResponse;
import com.example.metiscameras.models.BitmapWrapper;
import com.example.metiscameras.models.ColorsAdapter;
import com.example.metiscameras.models.RGB;
import com.serenegiant.common.BaseActivity;
import com.serenegiant.usb_libuvccamera.CameraDialog;
import com.serenegiant.usb_libuvccamera.IFrameCallback;
import com.serenegiant.usb_libuvccamera.LibUVCCameraUSBMonitor;
import com.serenegiant.usbcameracommon.UVCCameraHandler;
import com.serenegiant.usb_libuvccamera.LibUVCCameraUSBMonitor.UsbControlBlock;
import com.serenegiant.usb_libuvccamera.LibUVCCameraUSBMonitor.OnDeviceConnectListener;
import com.serenegiant.widget.CameraViewInterface;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;


public final class MainActivity extends BaseActivity implements CameraDialog.CameraDialogParent {
    private static final boolean DEBUG = true;    // FIXME set false when production
    private static final String TAG = "!";
    private static final int GALLERY_REQUEST = 4;

    private static final int PREVIEW_WIDTH = 1920;
    private static final int PREVIEW_HEIGHT = 1080;

    private LibUVCCameraUSBMonitor usbMonitor;

    private UVCCameraHandler mainHandler;
    private CameraViewInterface mainView;
    private Surface mainSurface;
    private ImageView mainImage;
    private UsbDevice mainCamera;

    private UVCCameraHandler sideHandler;
    private CameraViewInterface sideView;
    private Surface sideSurface;
    private ImageView sideImage;
    private UsbDevice sideCamera;

    /**
     * Кнопка подтверждения паттерна. Изначально не отображается.
     * На клик - запрос на добавление TableTop
     */
    private Button confirmPattern;
    /**
     * Кнопка отклонения паттерна. Изначально не отображается.
     * На клик - запрос на нахождение паттерна findPattern
     */
    private Button cancelPattern;
    private int cancelCount;

    private boolean isFindPattern = false;

    private FindPatternResponse pattern;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Log.i(TAG, "onCreate");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        confirmPattern = (Button) findViewById(R.id.confirm_pattern_button);
        confirmPattern.setActivated(false);
        confirmPattern.setOnClickListener(view -> {
            confirmPattern.setActivated(false);
            cancelPattern.setActivated(false);
            TableTopApi.addTableTop(pattern);
        });

        cancelPattern = (Button) findViewById(R.id.cancel_pattern_button);
        cancelPattern.setActivated(false);
        cancelPattern.setOnClickListener(view -> {
            if(cancelCount > 2) {
                // TODO логика вызова бригадира
                return;
            }
            confirmPattern.setActivated(false);
            cancelPattern.setActivated(false);
            findPattern();
            cancelCount++;
        });

        Button scan = (Button) findViewById(R.id.scan_button);
        scan.setOnClickListener(view -> {
            if (DEBUG) Log.d(TAG, "scan");

            /*
            // для выбора из галереи
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, GALLERY_REQUEST);
             */

            isFindPattern = true;
            mainHandler.setPreviewCallback(mainFrameCallback);
        });

        Button open = (Button) findViewById(R.id.first_button);
        open.setOnClickListener(view -> {
            if (DEBUG) Log.d(TAG, "open");
            openCameras();
        });

        Button test = (Button) findViewById(R.id.test_button);
        test.setOnClickListener(view -> {
            if (DEBUG) Log.i(TAG, "test");


            isFindPattern = false;
            mainHandler.setPreviewCallback(mainFrameCallback);

//            mainHandler.setPreviewCallback(mainFrameCallback);
//            sideHandler.setPreviewCallback(sideFrameCallback);

        });

        Button stop = (Button) findViewById(R.id.second_button);
        stop.setOnClickListener(view -> {
            onStop();
            onDestroy();
        });

        mainView = (CameraViewInterface) findViewById(R.id.camera_view_main);
        mainImage = (ImageView) findViewById(R.id.image_view_main);
        mainHandler = UVCCameraHandler.createHandler(this, mainView, PREVIEW_WIDTH, PREVIEW_HEIGHT, 0.5f);


        sideView = (CameraViewInterface) findViewById(R.id.camera_view_side);
        sideImage = (ImageView) findViewById(R.id.image_view_side);
        sideHandler = UVCCameraHandler.createHandler(this, sideView, PREVIEW_WIDTH, PREVIEW_HEIGHT, 0.5f);


        usbMonitor = new LibUVCCameraUSBMonitor(this, mOnDeviceConnectListener);
    }

    private Bitmap photo = null;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null) {

            if (requestCode == GALLERY_REQUEST) {
                try {
                    photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (photo != null & requestCode == GALLERY_REQUEST) {
//                TableTopPatternApi.addPattern(photo,
//                        photo);

                TableTopPatternApi.findPattern(
                        this,
                        photo,
                        photo);
            }
        }
    }

    @Override
    protected void onStart() {
        if (DEBUG) Log.i(TAG, "onStart");
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
        if (DEBUG) Log.i(TAG, "onStop");
        mainHandler.close();
        sideHandler.close();

        if (mainView != null)
            mainView.onPause();

        if (sideView != null)
            sideView.onPause();
        usbMonitor.unregister();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (DEBUG) Log.i(TAG, "onDestroy");
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

    @Override
    protected void onResume() {
        super.onResume();
//        openCameras();
    }

    private void openCameras(){
        try {
            mainCamera = usbMonitor.getDeviceList().get(0);
            sideCamera = usbMonitor.getDeviceList().get(1);

            queueEvent(() -> usbMonitor.requestPermission(mainCamera), 0);

            queueEvent(() -> usbMonitor.requestPermission(sideCamera), 3 * 1000);

        } catch (Exception ex) {
            Toast.makeText(MainActivity.this, "Камеры не подключены, или подключена только одна", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "EXCEPTION while opening! " + ex.getMessage());
        }
    }

    private final OnDeviceConnectListener mOnDeviceConnectListener = new OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            if (DEBUG) Log.d(TAG, "onAttach:" + device.getDeviceId());

            // TODO в будущем будет проверка по меткам

            Toast.makeText(MainActivity.this, "ATTACHED " + device.getDeviceId(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnect(final UsbDevice device, final UsbControlBlock ctrlBlock, final boolean createNew) {
            if (DEBUG) Log.i(TAG, "onConnect:" + device.getDeviceId());
            try {
                if (mainHandler != null && !mainHandler.isOpened() && device.getDeviceId() == mainCamera.getDeviceId()) {
                    if (DEBUG) Log.d(TAG, "StartMain");
                    mainHandler.open(ctrlBlock);
                    final SurfaceTexture st = mainView.getSurfaceTexture();
                    mainHandler.startPreview(new Surface(st));

                } else if (sideHandler != null && !sideHandler.isOpened() && device.getDeviceId() == sideCamera.getDeviceId()) {
                    if (DEBUG) Log.d(TAG, "StartSide");
                    sideHandler.open(ctrlBlock);
                    final SurfaceTexture st = sideView.getSurfaceTexture();
                    sideHandler.startPreview(new Surface(st));
                }
            } catch (Exception ex) {
                Log.e(TAG, "EXCEPTION in onConnect! " + ex.getMessage());
            }
        }

        @Override
        public void onDisconnect(final UsbDevice device, final UsbControlBlock ctrlBlock) {
            if (DEBUG) Log.i(TAG, "onDisconnect:" + device.getDeviceId());
//            if ((sideHandler != null) && !sideHandler.isEqual(device)) {
            if (sideCamera.getDeviceId() == device.getDeviceId()) {
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        sideCamera = null;
                        if(sideHandler != null)
                            sideHandler.close();
                        sideView.onPause();
                        if (sideSurface != null) {
                            sideSurface.release();
                            sideSurface = null;
                        }
                    }
                }, 0);
            }
//            else if ((mainHandler != null) && !mainHandler.isEqual(device)) {
            else if (mainCamera.getDeviceId() == device.getDeviceId()) {
                queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        mainCamera = null;
                        if(mainHandler != null)
                            mainHandler.close();
                        mainView.onPause();
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
            if (DEBUG) Log.i(TAG, "onDetach:" + device.getDeviceId());
            Toast.makeText(MainActivity.this, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(final UsbDevice device) {
            if (DEBUG) {
                Log.v(TAG, "onCancel:");
            }
        }
    };

    BitmapWrapper mainBitmap;
    private boolean mainSaved;
    BitmapWrapper sideBitmap;
    private boolean sideSaved;

    private final Bitmap bitmap = Bitmap.createBitmap(PREVIEW_WIDTH, PREVIEW_HEIGHT, Bitmap.Config.RGB_565);

    private final Timer timer = new Timer();

    private final IFrameCallback mainFrameCallback = new IFrameCallback() {
        @Override
        public void onFrame(final ByteBuffer frame) {
            Log.d(TAG, "mainFrameCallback");
            frame.clear();
            mainHandler.setPreviewCallback(null);

            if (mainBitmap == null)
                mainBitmap = new BitmapWrapper(PREVIEW_WIDTH, PREVIEW_HEIGHT, frame);
            else
                mainBitmap.updateBitmap(frame);

            if (isFindPattern) {
                findPattern();
                return;
            }

            addPattern();
//            mainSaved = true;
//            mainImage.post(() -> mainImage.setImageBitmap(mainBitmap.getBitmap()));
//
//            checkBitmaps();
        }
    };

    private void findPattern() {
        TableTopPatternApi.findPattern(this, mainBitmap.getBitmap(), mainBitmap.getBitmap());
    }

    private void addPattern() {
        TableTopPatternApi.addPattern(mainBitmap.getBitmap(), mainBitmap.getBitmap());
    }

    private final IFrameCallback sideFrameCallback = new IFrameCallback() {
        @Override
        public void onFrame(final ByteBuffer frame) {
            Log.d(TAG, "sideFrameCallback");
            frame.clear();
            sideHandler.setPreviewCallback(null);

            if (sideBitmap == null)
                sideBitmap = new BitmapWrapper(PREVIEW_WIDTH, PREVIEW_HEIGHT, frame);
            else
                sideBitmap.updateBitmap(frame);

            sideSaved = true;
            sideImage.post(() -> sideImage.setImageBitmap(sideBitmap.cropAndResult(0, 0, 500, 500)));

            checkBitmaps();
        }
    };

    private void checkBitmaps() {
        if (!mainSaved || !sideSaved)
            return;

        processImages();
    }

    private void processImages() {
        Log.d(TAG, "processImages");
//        PythonApi.processImages(
//                this,
//                mainBitmap.getBitmap(),
//                sideBitmap.cropAndResult(0, 0, 100, 100),
//                mainImage,
//                () -> {
//                    Log.d(TAG, "CallBAck");
//                    timer.schedule(new TimerTask() {
//                        public void run() {
//                            Log.d(TAG, "TASK");
//
//                            mainSaved = false;
//                            sideSaved = false;
//                            if (mainHandler != null && mainHandler.isOpened()) {
//                                mainBitmap = null;
//                                mainHandler.setPreviewCallback(mainFrameCallback);
//                            }
//                            if (sideHandler != null && sideHandler.isOpened()) {
//                                mainBitmap = null;
//                                sideHandler.setPreviewCallback(sideFrameCallback);
//                            }
//                        }
//                    }, 10 * 1000);
//                });
    }

    public void setPattern(FindPatternResponse pattern,
                           String article,
                           String name,
                           String material){
        this.pattern = pattern;
        confirmPattern.setActivated(true);
        cancelPattern.setActivated(true);
        if(article != null)
            ((TextView) findViewById(R.id.article_view)).setText(article);

        if(name != null)
            ((TextView) findViewById(R.id.name_view)).setText(name);

        if(material != null)
            ((TextView) findViewById(R.id.material_view)).setText(material);

        // вывод картинки
        sideImage.setImageBitmap(toBitmap(pattern.getTableTopImage()));

        // вывод цветов
        ListView colors = (ListView) findViewById(R.id.colors);
        List<RGB> rgb = new ArrayList<>();
        for (int i = 0; i < pattern.getColors().size(); i++) {
            rgb.add(new RGB(pattern.getColors().get(i)));
        }

        ColorsAdapter adapter = new ColorsAdapter(this, R.id.colors, rgb);
        colors.setAdapter(adapter);

    }

    @Override
    public LibUVCCameraUSBMonitor getUSBMonitor() {
        return usbMonitor;
    }

    @Override
    public void onDialogResult(boolean canceled) {
    }

}