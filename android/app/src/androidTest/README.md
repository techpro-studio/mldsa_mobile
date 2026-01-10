# ML-DSA Android Tests

This directory contains instrumented tests for the ML-DSA Android library.

## Test Files

### MLDSATest.java
Comprehensive tests covering:
- Key generation for all security levels (44, 65, 87)
- Signing and verifying messages
- Context strings
- Invalid signatures and tampered data
- Edge cases (empty messages, large messages, null inputs)
- Multiple signatures and keypair independence

### MLDSASeedTest.java
Focused tests for seed-based deterministic key generation:
- Deterministic key generation with fixed seeds
- Different seeds producing different keys
- Edge cases (all-zero seed, all-ones seed)
- Single-bit differences
- Comparison with random key generation

## Running Tests

### Using Android Studio
1. Open the project in Android Studio
2. Connect an Android device or start an emulator
3. Right-click on `androidTest` directory
4. Select "Run 'All Tests'"

### Using Command Line

Run all instrumented tests:
```bash
cd /Users/alex/dev/ml_dsa/android
./gradlew connectedAndroidTest
```

Run specific test class:
```bash
./gradlew connectedAndroidTest --tests "com.mldsa.MLDSATest"
./gradlew connectedAndroidTest --tests "com.mldsa.MLDSASeedTest"
```

Run specific test method:
```bash
./gradlew connectedAndroidTest --tests "com.mldsa.MLDSATest.testSignAndVerify_SimpleMessage"
```

### View Test Results

After running tests, view the HTML report at:
```
android/app/build/reports/androidTests/connected/index.html
```

## Test Coverage

The test suite covers:

✓ **Key Generation**
  - All security levels (ML-DSA-44, ML-DSA-65, ML-DSA-87)
  - Random key generation
  - Deterministic key generation with seed
  - Key size validation
  - Multiple independent keypairs

✓ **Signing**
  - Simple messages
  - Empty messages
  - Large messages (1MB)
  - Messages with context strings
  - Multiple signatures for same message (randomized)

✓ **Verification**
  - Valid signatures
  - Invalid/tampered signatures
  - Invalid/tampered messages
  - Wrong public keys
  - Context string validation

✓ **Error Handling**
  - Null inputs
  - Invalid seed lengths
  - Invalid security levels

✓ **Seed-Based Key Generation**
  - Deterministic generation
  - Reproducibility
  - Seed variations
  - Compatibility with signing/verification

## Requirements

- Android device or emulator (API level 21+)
- Native library built for the target architecture
- JUnit and AndroidX Test dependencies (automatically included)

## Notes

- These are **instrumented tests** that run on an Android device/emulator
- Tests require the native ML-DSA library to be compiled and loaded
- Each test is independent and can run in any order
- Tests use secure practices (proper cleanup, error handling)
