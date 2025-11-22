#include "edge_processor.h"
#include <android/log.h>

#define LOG_TAG "EdgeProcessor"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

EdgeProcessor::EdgeProcessor() {
    LOGI("EdgeProcessor initialized");
}

EdgeProcessor::~EdgeProcessor() {
    LOGI("EdgeProcessor destroyed");
}

std::vector<uint8_t> EdgeProcessor::processFrame(uint8_t* data, int width, int height) {
    // Convert YUV420 (NV21) to grayscale
    cv::Mat yuv(height + height/2, width, CV_8UC1, data);
    cv::Mat gray(height, width, CV_8UC1);
    
    // Extract Y channel (grayscale)
    gray = yuv(cv::Rect(0, 0, width, height));
    
    // Apply Canny edge detection
    cv::Mat edges = applyCannyEdge(gray);
    
    // Convert to vector
    std::vector<uint8_t> result(edges.data, edges.data + (width * height));
    
    return result;
}

cv::Mat EdgeProcessor::applyCannyEdge(const cv::Mat& input) {
    cv::Mat blurred, edges;
    
    // Reduce noise with Gaussian blur
    cv::GaussianBlur(input, blurred, cv::Size(5, 5), 1.5);
    
    // Apply Canny edge detection
    cv::Canny(blurred, edges, 50, 150);
    
    return edges;
}