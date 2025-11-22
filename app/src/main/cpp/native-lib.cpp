#include <jni.h>
#include <string>
#include <android/log.h>
#include "edge_processor.h"

#define LOG_TAG "NativeLib"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

EdgeProcessor edgeProcessor;

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_edgeviewer_CameraRenderer_processFrameNative(
        JNIEnv* env,
        jobject /* this */,
        jbyteArray data,
        jint width,
        jint height) {
    
    jbyte* frameData = env->GetByteArrayElements(data, nullptr);
    jsize dataLen = env->GetArrayLength(data);
    
    // Process frame
    std::vector<uint8_t> processedData = edgeProcessor.processFrame(
            reinterpret_cast<uint8_t*>(frameData), width, height);
    
    env->ReleaseByteArrayElements(data, frameData, 0);
    
    // Create Java byte array
    jbyteArray result = env->NewByteArray(processedData.size());
    env->SetByteArrayRegion(result, 0, processedData.size(),
                           reinterpret_cast<jbyte*>(processedData.data()));
    
    return result;
}