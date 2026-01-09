#!/bin/bash
set -e

# Build directories
BUILD_DIR_IOS="build/ios"
BUILD_DIR_IOS_SIM="build/ios-sim"
BUILD_DIR_MACOS="build/macos"

# Toolchains
IOS_TOOLCHAIN_FILE="toolchains/ios.toolchain.cmake"
IOS_SIM_TOOLCHAIN_FILE="toolchains/ios-simulator.toolchain.cmake"
MACOS_TOOLCHAIN_FILE="toolchains/macos.toolchain.cmake"

# Clean
rm -rf build
rm -rf test/libs

# Generic build function
build_libs() {
    platform=$1
    build_dir=$2
    toolchain_file=$3

    echo "🔨 Building for $platform..."
    cmake -B "$build_dir" \
          -GXcode \
          -DCMAKE_TOOLCHAIN_FILE="$toolchain_file"

    cmake --build "$build_dir" --config Release
    echo "✅ Build for $platform completed."
}

# Build all targets
build_libs "iOS Device" "$BUILD_DIR_IOS" "$IOS_TOOLCHAIN_FILE"
build_libs "iOS Simulator" "$BUILD_DIR_IOS_SIM" "$IOS_SIM_TOOLCHAIN_FILE"
build_libs "macOS" "$BUILD_DIR_MACOS" "$MACOS_TOOLCHAIN_FILE"

# Create XCFramework
build_xcframework() {
  FRAMEWORK="MLDSA"
  PROJECT_FOLDER_NAME="objc"

  echo "📦 Creating XCFramework..."

  xcodebuild -create-xcframework \
      -framework "$BUILD_DIR_IOS/$PROJECT_FOLDER_NAME/Release-iphoneos/$FRAMEWORK.framework" \
      -framework "$BUILD_DIR_IOS_SIM/$PROJECT_FOLDER_NAME/Release-iphonesimulator/$FRAMEWORK.framework" \
      -framework "$BUILD_DIR_MACOS/$PROJECT_FOLDER_NAME/Release/$FRAMEWORK.framework" \
      -output "test/libs/MLDSA.xcframework"

  echo "✅ XCFramework created."
}

build_xcframework

echo "🎉 All done!"