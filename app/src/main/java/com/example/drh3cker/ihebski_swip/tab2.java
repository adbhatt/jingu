package com.example.drh3cker.ihebski_swip;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

/**
 * Created by Dr.h3cker on 14/03/2015.
 */
public class tab2 extends Fragment {
    private SurfaceView preview = null;
    private SurfaceHolder previewHolder = null;
    private Camera camera = null;
    private boolean inPreview = false;
    private boolean cameraConfigured = false;
    private ToggleButton flipCamera;
    private Button flash;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2, container, false);
        flipCamera = (ToggleButton) view.findViewById(R.id.flipper);
        preview = (SurfaceView) view.findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        flash = (Button) view.findViewById(R.id.flash);

        flipCamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                restartPreview(isChecked);
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // camera=Camera.open();
        int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
        if (Camera.getNumberOfCameras() > 1
                && camId < Camera.getNumberOfCameras() - 1) {
            // startCamera(camId + 1);
            camera = Camera.open(camId + 1);
        } else {
            // startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
            camera = Camera.open(camId);
        }
        startPreview();
    }

    void restartPreview(boolean isFront) {
        if (inPreview) {
            camera.stopPreview();
        }
        //
        camera.release();

        // camera=null;
        // inPreview=false;
        // /*int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
        // if (Camera.getNumberOfCameras() > 1 && camId <
        // Camera.getNumberOfCameras() - 1) {
        // //startCamera(camId + 1);
        // camera = Camera.open(camId + 1);
        // } else {
        // //startCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
        // camera = Camera.open(camId);
        // }*/
        int camId = Camera.CameraInfo.CAMERA_FACING_BACK;
        if (isFront) {
            camera = Camera.open(camId);
            initPreview(640, 480);
            camera.startPreview();

        } else {
            camera = Camera.open(camId + 1);
            initPreview(640, 480);
            camera.startPreview();

        }
        // startPreview();
    }

    @Override
    public void onPause() {
        if (inPreview) {
            camera.stopPreview();
        }

        camera.release();
        camera = null;
        inPreview = false;

        super.onPause();
    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result = null;
        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return (result);
    }

    private void initPreview(int width, int height) {
        if (camera != null && previewHolder.getSurface() != null) {
            try {
                camera.setPreviewDisplay(previewHolder);
                camera.setDisplayOrientation(90);
            } catch (Throwable t) {
                t.printStackTrace();
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = getBestPreviewSize(width, height, parameters);

                if (size != null) {
                    parameters.setPreviewSize(size.width, size.height);
                    camera.setDisplayOrientation(90);
                    camera.setParameters(parameters);
                    cameraConfigured = true;
                }
            }
        }
    }

    private void startPreview() {
        if (cameraConfigured && camera != null) {
            camera.setDisplayOrientation(90);
            camera.startPreview();
            inPreview = true;
        }
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
        public void surfaceCreated(SurfaceHolder holder) {

            // no-op -- wait until surfaceChanged()
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
            initPreview(width, height);
            startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // no-op
            if (camera != null) {
            /*
             * Call stopPreview() to stop updating the preview surface.
             */
                camera.stopPreview();

            /*
             * Important: Call release() to release the camera for use by
             * other applications. Applications should release the camera
             * immediately in onPause() (and re-open() it in onResume()).
             */
                camera.release();

                camera = null;
            }
        }
    };
}
