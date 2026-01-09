# ios.toolchain.cmake

# Set the base system
set(CMAKE_SYSTEM_NAME iOS)

# Define the architectures for iOS (e.g., arm64 for newer iPhones)
set(CMAKE_OSX_ARCHITECTURES "arm64")

# Define the minimum iOS version
set(CMAKE_OSX_DEPLOYMENT_TARGET "12.0" CACHE STRING "Minimum iOS deployment version")

# You can add additional flags and settings as needed