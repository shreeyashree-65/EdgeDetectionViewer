package com.edgeviewer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.util.Log;
import java.io.IOException;

public class CameraGLSurfaceView extends GLSurfaceView implements SurfaceTexture.OnFrameAvailableListener {
    
    private static final String TAG = "CameraGLSurfaceView";
    private Camera camera;
    private SurfaceTexture surfaceTexture;
    private CameraRenderer renderer;
    
    public CameraGLSurfaceView(Context context) {
        super(context);
        
        setEGLContextClientVersion(2);
        renderer = new CameraRenderer(context);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
    
    @Override
    public void onResume() {
        super.onResume();
        openCamera();
    }
    
    @Override
    public void onPause() {
        super.onPause();
        closeCamera();
    }
    
    private void openCamera() {
        try {
            camera = Camera.open();
            Camera.Parameters params = camera.getParameters();
            params.setPreviewSize(640, 480);
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            camera.setParameters(params);
            
            surfaceTexture = new SurfaceTexture(renderer.getTextureId());
            surfaceTexture.setOnFrameAvailableListener(this);
            
            camera.setPreviewTexture(surfaceTexture);
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    if (data != null) {
                        Camera.Size size = camera.getParameters().getPreviewSize();
                        renderer.updateFrame(data, size.width, size.height);
                    }
                }
            });
            
            camera.startPreview();
            Log.d(TAG, "Camera opened successfully");
            
        } catch (IOException e) {
            Log.e(TAG, "Error opening camera", e);
        }
    }
    
    private void closeCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
        if (surfaceTexture != null) {
            surfaceTexture.release();
            surfaceTexture = null;
        }
    }
    
    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }
}
