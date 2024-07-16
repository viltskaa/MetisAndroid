package com.example.metis;

import static android.content.Context.CAMERA_SERVICE;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;

import java.util.Arrays;

public class CameraUtils {
    final Context context;
    final CameraManager cameraManager;

    public CameraUtils(Context context) {
        this.context = context;
        this.cameraManager = (CameraManager)context.getSystemService(CAMERA_SERVICE);
    }

    /**
     * Get the IDs of all available cameras.
     */
    public String[] getCameraIds() throws CameraAccessException {
        System.out.println("!!!!" + Arrays.toString(this.cameraManager.getCameraIdList()));
        return this.cameraManager.getCameraIdList();
    }

    /**
     * Get the "lens facing" for a particular camera ID returned by `getCameraIds()`.
     */
    public Integer getLensFacing(String cameraId) throws CameraAccessException {
        CameraCharacteristics characteristics = this.cameraManager.getCameraCharacteristics(cameraId);

        // This will return one of CameraMetadata.LENS_FACING_FRONT,
        // CameraMetadata.LENS_FACING_BACK or CameraMetadata.LENS_FACING_EXTERNAL.
        return characteristics.get(CameraCharacteristics.LENS_FACING);
    }

    /**
     * Return true if this kernel supports external cameras, false otherwise.
     */
    public boolean supportsExternalCameras() {
        return this.context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_EXTERNAL);
    }
}