package com.edgeviewer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    
    private static final int CAMERA_PERMISSION_CODE = 100;
    private CameraGLSurfaceView glSurfaceView;
    
    static {
        System.loadLibrary("native-lib");
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (checkCameraPermission()) {
            initializeCamera();
        } else {
            requestCameraPermission();
        }
    }
    
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }
    
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_CODE);
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeCamera();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    
    private void initializeCamera() {
        glSurfaceView = new CameraGLSurfaceView(this);
        setContentView(glSurfaceView);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (glSurfaceView != null) {
            glSurfaceView.onPause();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (glSurfaceView != null) {
            glSurfaceView.onResume();
        }
    }
    
    // Native methods
    public native void processFrame(byte[] data, int width, int height);
}