cmake_minimum_required(VERSION 3.18.1)

project("native-lib")

# Set OpenCV directory
set(OpenCV_DIR "C:/Android/OpenCV-android-sdk/sdk/native/jni")

# Find OpenCV
find_package(OpenCV REQUIRED)

# Add library
add_library(native-lib SHARED
        native-lib.cpp
        edge_processor.cpp)

# Include directories
target_include_directories(native-lib PRIVATE
        ${OpenCV_INCLUDE_DIRS})

# Link libraries
target_link_libraries(native-lib
        ${OpenCV_LIBS}
        log
        android)