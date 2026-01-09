# toolchain-macos.cmake

# Set the system name
set(CMAKE_SYSTEM_NAME Darwin)

# Set macOS deployment target
# (Change the version to the minimum macOS version you want to support)
set(CMAKE_OSX_DEPLOYMENT_TARGET "10.15" CACHE STRING "Minimum OS X deployment version")
# Set other macOS specific settings if needed