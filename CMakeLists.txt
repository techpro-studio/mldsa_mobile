cmake_minimum_required(VERSION 3.10)

project(lib VERSION 1.0.0 LANGUAGES C ASM OBJC)

set(CMAKE_CXX_STANDARD_REQUIRED True)

# Optional: Set global compiler options or definitions here
# e.g., target_compile_options(PeaceCoder PUBLIC -fembed-bitcode)

# Include subdirectories
add_subdirectory(lib)
add_subdirectory(objc)