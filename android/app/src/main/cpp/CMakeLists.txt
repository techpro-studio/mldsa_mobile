cmake_minimum_required(VERSION 3.18.1)

project(mldsa-jni VERSION 1.0.0 LANGUAGES C CXX ASM)

# Reference to parent lib directory
set(LIB_ROOT_DIR ${CMAKE_CURRENT_SOURCE_DIR}/../../../../../lib)

# Include the lib subdirectory to build MLDSACore static library
add_subdirectory(${LIB_ROOT_DIR} ${CMAKE_CURRENT_BINARY_DIR}/lib)

# Add JNI wrapper library
add_library(mldsa-jni SHARED
    ${CMAKE_CURRENT_SOURCE_DIR}/mldsa_jni.cpp
)

# Include directories
target_include_directories(mldsa-jni PRIVATE
    ${LIB_ROOT_DIR}/include
    ${LIB_ROOT_DIR}
    ${CMAKE_CURRENT_SOURCE_DIR}
)

# Link against the lib static library
target_link_libraries(mldsa-jni PRIVATE lib)

# Optimization flags
target_compile_options(mldsa-jni PRIVATE
    -O3
    -fvisibility=hidden
)

# Find and link log library
find_library(log-lib log)
target_link_libraries(mldsa-jni PRIVATE ${log-lib})

# Strip unused symbols in release builds
if(CMAKE_BUILD_TYPE STREQUAL "Release")
    target_link_options(mldsa-jni PRIVATE
        -Wl,--gc-sections
        -Wl,--strip-all
    )
endif()
