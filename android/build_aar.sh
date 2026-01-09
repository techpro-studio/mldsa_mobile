#!/bin/bash

# Build script for ML-DSA Android AAR library
# This script builds an AAR file that can be included in other Android projects

set -e

echo "Building ML-DSA Android Library (AAR)..."
echo "========================================"

# Check if gradlew exists, otherwise try system gradle
if [ -f "./gradlew" ]; then
    GRADLE_CMD="./gradlew"
else
    echo "Warning: gradlew not found. Trying to use system gradle..."
    if command -v gradle &> /dev/null; then
        GRADLE_CMD="gradle"
    else
        echo "Error: Neither gradlew nor gradle found."
        echo "Please install Gradle or run: gradle wrapper"
        exit 1
    fi
fi

# Clean previous builds
echo "Cleaning previous builds..."
$GRADLE_CMD clean

# Build the release AAR
echo "Building release AAR..."
$GRADLE_CMD :app:assembleRelease

# Check if build was successful
if [ -f "app/build/outputs/aar/app-release.aar" ]; then
    echo ""
    echo "========================================"
    echo "Build successful!"
    echo "AAR file location: android/app/build/outputs/aar/app-release.aar"
    echo ""
    echo "To use this library in your Android project:"
    echo "1. Copy app-release.aar to your project's libs folder"
    echo "2. Add to your app's build.gradle:"
    echo "   dependencies {"
    echo "       implementation files('libs/app-release.aar')"
    echo "   }"
    echo ""
    echo "Or publish to a local Maven repository:"
    echo "   ./gradlew :app:publishToMavenLocal"
    echo "========================================"
else
    echo "Build failed. AAR file not found."
    exit 1
fi
