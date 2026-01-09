# Building ML-DSA Android Library (AAR)

## Quick Start

The easiest way to build the AAR:

```bash
cd android
./build_aar.sh
```

The AAR file will be at: `app/build/outputs/aar/app-release.aar`

## Prerequisites

- **Android Studio** with NDK installed
- **CMake** 3.18.1 or later
- **Gradle** 8.1+ (or use Android Studio's embedded Gradle)

## Build Structure

The Android build now uses the refactored CMake structure:

- **lib/** - Core ML-DSA native library (shared with iOS/macOS)
  - `mldsa_native_all.c` - Multi-level C wrapper
  - `os_rng.c` - Platform RNG implementation
  - `mldsa_native.S` - Assembly optimizations
  - `multilevel_config.h` - Multi-level configuration

- **android/app/src/main/cpp/** - Android JNI wrapper
  - `mldsa_jni.cpp` - JNI bindings for Java
  - `CMakeLists.txt` - Links against parent lib target

This structure ensures consistency across all platforms (iOS, macOS, Android).

## Build Options

### 1. Using the Build Script (Recommended)

```bash
cd android
./build_aar.sh
```

This script will:
- Check for Gradle installation
- Clean previous builds
- Build the release AAR
- Show the output location

### 2. Using Gradle Directly

If you have Android Studio installed, you can use its Gradle:

```bash
cd android
/Applications/Android\ Studio.app/Contents/gradle/gradle-8.1/bin/gradle :app:assembleRelease
```

Or if you have Gradle in your PATH:

```bash
cd android
gradle :app:assembleRelease
```

### 3. Using Android Studio

1. Open the `android` folder in Android Studio
2. Wait for Gradle sync to complete
3. Click **Build** → **Make Project**
4. The AAR will be in `app/build/outputs/aar/`

### 4. Building with Gradle Wrapper

If you need to create a Gradle wrapper first:

```bash
cd android
gradle wrapper --gradle-version 8.1.1
./gradlew :app:assembleRelease
```

## Publishing Options

### Publish to Local Maven Repository

This is useful for multi-module projects:

```bash
cd android
gradle :app:publishToMavenLocal
```

The library will be available at:
- **Group ID**: `com.mldsa`
- **Artifact ID**: `mldsa-android`
- **Version**: `1.0.0`

### Publish to Local Directory

To publish to a local directory (e.g., for testing):

```bash
cd android
gradle :app:publishReleasePublicationToLocalRepository
```

This publishes to `app/build/repo/`

## Integration into Your Project

### Method 1: Direct AAR File

Copy the AAR to your project:

```bash
cp android/app/build/outputs/aar/app-release.aar /path/to/your/project/app/libs/
```

In your app's `build.gradle`:

```groovy
android {
    // ... other configuration
}

dependencies {
    implementation files('libs/app-release.aar')
}
```

### Method 2: Maven Local

After publishing to Maven Local, add to your app's `build.gradle`:

```groovy
dependencies {
    implementation 'com.mldsa:mldsa-android:1.0.0'
}
```

Make sure `mavenLocal()` is in your repository list:

```groovy
repositories {
    mavenLocal()
    google()
    mavenCentral()
}
```

### Method 3: Project Module (Development)

In your project's `settings.gradle`:

```groovy
include ':app'
include ':mldsa'
project(':mldsa').projectDir = new File('/path/to/ml_dsa/android/app')
```

In your app's `build.gradle`:

```groovy
dependencies {
    implementation project(':mldsa')
}
```

## Troubleshooting

### Gradle not found

If you get "command not found: gradle":

1. Install Gradle: `brew install gradle` (macOS)
2. Or use Android Studio's Gradle (see Build Option 2)
3. Or create a wrapper (see Build Option 4)

### NDK not found

Install the NDK in Android Studio:
1. Open **Android Studio** → **Preferences**
2. Go to **Appearance & Behavior** → **System Settings** → **Android SDK**
3. Click the **SDK Tools** tab
4. Check **NDK (Side by side)**
5. Click **Apply**

### CMake not found

Install CMake in Android Studio (same location as NDK):
1. Check **CMake** in the SDK Tools tab
2. Click **Apply**

### Build fails with "No version of NDK matched"

Edit `android/app/build.gradle` and specify your NDK version:

```groovy
android {
    ndkVersion "26.1.10909125"  // Use your installed version
    // ... rest of config
}
```

Find your NDK version at: `~/Library/Android/sdk/ndk/`

## Customizing the Build

### Changing Target Architectures

In `android/app/build.gradle`, modify the ABI filters:

```groovy
externalNativeBuild {
    cmake {
        cppFlags "-std=c++11 -frtti -fexceptions"
        // Remove architectures you don't need
        abiFilters 'arm64-v8a', 'x86_64'  // 64-bit only
    }
}
```

### Enabling Performance Optimizations

For ARM NEON optimizations, edit `android/app/src/main/cpp/CMakeLists.txt`:

```cmake
# Uncomment for ARM NEON support
add_definitions(-DMLDSA_MOBILE_ENABLE_NEON)
```

### Changing Version Number

In `android/app/build.gradle`, update the Maven publication:

```groovy
afterEvaluate {
    publishing {
        publications {
            release(MavenPublication) {
                // ...
                version = '1.0.1'  // Change version here
            }
        }
    }
}
```

## Output Files

After a successful build, you'll find:

```
android/app/build/
├── outputs/
│   └── aar/
│       └── app-release.aar          # The AAR file
├── intermediates/
│   └── cmake/
│       └── release/
│           └── obj/                  # Native .so files by architecture
│               ├── arm64-v8a/
│               ├── armeabi-v7a/
│               ├── x86/
│               └── x86_64/
└── repo/                             # Maven local publish directory
    └── com/mldsa/mldsa-android/
```

## Additional Resources

- [Android AAR documentation](https://developer.android.com/studio/projects/android-library)
- [Gradle Publishing](https://docs.gradle.org/current/userguide/publishing_maven.html)
- [CMake in Android](https://developer.android.com/ndk/guides/cmake)
