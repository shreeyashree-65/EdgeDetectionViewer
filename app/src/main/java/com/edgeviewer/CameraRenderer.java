package com.edgeviewer;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class CameraRenderer implements GLSurfaceView.Renderer {
    
    private static final String TAG = "CameraRenderer";
    
    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
            "attribute vec2 vTexCoord;" +
            "varying vec2 texCoord;" +
            "void main() {" +
            "  gl_Position = vPosition;" +
            "  texCoord = vTexCoord;" +
            "}";
    
    private final String fragmentShaderCode =
            "precision mediump float;" +
            "varying vec2 texCoord;" +
            "uniform sampler2D uTexture;" +
            "void main() {" +
            "  gl_FragColor = texture2D(uTexture, texCoord);" +
            "}";
    
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureBuffer;
    private int program;
    private int textureId;
    private int[] textures = new int[1];
    
    private byte[] frameData;
    private int frameWidth;
    private int frameHeight;
    private boolean frameReady = false;
    
    // Square coordinates
    private float squareCoords[] = {
        -1.0f,  1.0f, 0.0f,   // top left
        -1.0f, -1.0f, 0.0f,   // bottom left
         1.0f, -1.0f, 0.0f,   // bottom right
         1.0f,  1.0f, 0.0f    // top right
    };
    
    private float textureCoords[] = {
        0.0f, 0.0f,
        0.0f, 1.0f,
        1.0f, 1.0f,
        1.0f, 0.0f
    };
    
    static {
        System.loadLibrary("native-lib");
    }
    
    public CameraRenderer(Context context) {
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);
        
        ByteBuffer tb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        tb.order(ByteOrder.nativeOrder());
        textureBuffer = tb.asFloatBuffer();
        textureBuffer.put(textureCoords);
        textureBuffer.position(0);
    }
    
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        
        // Generate texture
        GLES20.glGenTextures(1, textures, 0);
        textureId = textures[0];
        
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
    }
    
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }
    
    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        
        if (frameReady && frameData != null) {
            // Process frame using native code
            byte[] processed = processFrameNative(frameData, frameWidth, frameHeight);
            
            if (processed != null) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE,
                        frameWidth, frameHeight, 0, GLES20.GL_LUMINANCE,
                        GLES20.GL_UNSIGNED_BYTE, ByteBuffer.wrap(processed));
            }
            
            frameReady = false;
        }
        
        GLES20.glUseProgram(program);
        
        int positionHandle = GLES20.glGetAttribLocation(program, "vPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer);
        
        int texCoordHandle = GLES20.glGetAttribLocation(program, "vTexCoord");
        GLES20.glEnableVertexAttribArray(texCoordHandle);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 8, textureBuffer);
        
        int textureHandle = GLES20.glGetUniformLocation(program, "uTexture");
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glUniform1i(textureHandle, 0);
        
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, 4);
        
        GLES20.glDisableVertexAttribArray(positionHandle);
        GLES20.glDisableVertexAttribArray(texCoordHandle);
    }
    
    public void updateFrame(byte[] data, int width, int height) {
        this.frameData = data;
        this.frameWidth = width;
        this.frameHeight = height;
        this.frameReady = true;
    }
    
    public int getTextureId() {
        return textureId;
    }
    
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
    
    // Native method
    private native byte[] processFrameNative(byte[] data, int width, int height);
}