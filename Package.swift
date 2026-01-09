// swift-tools-version: 5.9
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "MLDSA",
    platforms: [
        .macOS(.v10_15),
        .iOS(.v13),
    ],
    products: [
        // The main Objective-C library (equivalent to MLDSA CMake target)
        // Using static linking for SPM to avoid code signing and dyld issues
        .library(
            name: "MLDSA",
            targets: ["MLDSA"]
        ),
        // The underlying C library (equivalent to lib CMake target)
        .library(
            name: "MLDSACore",
            targets: ["MLDSACore"]
        ),
    ],
    targets: [
        // Core C library target (equivalent to lib CMake target)
        .target(
            name: "MLDSACore",
            dependencies: [],
            path: "lib",
            exclude: [
                "CMakeLists.txt",
                "mldsa-native/test",
                "mldsa-native/.github",
                "mldsa-native/scripts",
                "mldsa-native/.git",
            ],
            sources: [
                "src/mldsa_native_all.c",
                "src/os_rng.c",
                "mldsa-native/mldsa/mldsa_native.S",
            ],
            publicHeadersPath: "include",
            cSettings: [
                // Compiler defines
                .define("MLD_CONFIG_FILE", to: "\"multilevel_config.h\""),

                // Include paths
                .headerSearchPath("."),
                .headerSearchPath("mldsa-native/mldsa"),
                .headerSearchPath("mldsa-native/mldsa/src"),
            ],
            linkerSettings: [
                // Link Security framework on Apple platforms
                .linkedFramework("Security", .when(platforms: [.macOS, .iOS])),
                // Android-specific linker flags (SPM doesn't support Android, but keeping for reference)
                // .unsafeFlags(["-Wl,-z,max-page-size=16384"], .when(platforms: [.android])),
            ]
        ),

        // Objective-C wrapper framework (equivalent to MLDSA CMake target)
        .target(
            name: "MLDSA",
            dependencies: ["MLDSACore"],
            path: "objc",
            exclude: [
                "CMakeLists.txt",
            ],
            sources: [
                "src/MLDSA.m",
            ],
            publicHeadersPath: "include",
            cSettings: [
                .headerSearchPath("include"),
            ],
            linkerSettings: [
                .linkedFramework("Foundation", .when(platforms: [.macOS, .iOS])),
            ]
        ),
    ],
    cLanguageStandard: .c99,
    cxxLanguageStandard: .cxx17
)
