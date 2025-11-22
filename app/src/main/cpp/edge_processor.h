#ifndef EDGE_PROCESSOR_H
#define EDGE_PROCESSOR_H

#include <opencv2/opencv.hpp>
#include <vector>

class EdgeProcessor {
public:
    EdgeProcessor();
    ~EdgeProcessor();
    
    std::vector<uint8_t> processFrame(uint8_t* data, int width, int height);
    
private:
    cv::Mat applyCannyEdge(const cv::Mat& input);
};

#endif // EDGE_PROCESSOR_H