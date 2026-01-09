# Enable ASM language if not already enabled
enable_language(ASM)

# Include directories
set(MLD_INCLUDE_DIR "${CMAKE_CURRENT_LIST_DIR}/mldsa-native/mldsa")
set(SRC_DIR "${CMAKE_CURRENT_LIST_DIR}")

# Create shared library
add_library(lib STATIC
        src/mldsa_native_all.c
        src/os_rng.c
        mldsa-native/mldsa/mldsa_native.S
)

# Set output name
set_target_properties(lib PROPERTIES
    OUTPUT_NAME "lib"
)

# Include directories for compilation
target_include_directories(lib PRIVATE
    "${CMAKE_CURRENT_LIST_DIR}"
    "${SRC_DIR}"
    "${MLD_INCLUDE_DIR}"
    "${MLD_INCLUDE_DIR}/src"
)

# Public include directory for consumers of the library
target_include_directories(lib PUBLIC
    $<BUILD_INTERFACE:${MLD_INCLUDE_DIR}>
    $<INSTALL_INTERFACE:include>
)

# Export all symbols on Windows
if (WIN32)
  set_target_properties(lib PROPERTIES
      WINDOWS_EXPORT_ALL_SYMBOLS ON
  )
endif (WIN32)


target_compile_definitions(lib PRIVATE
    MLD_CONFIG_FILE="multilevel_config.h"
)

# C compiler flags
target_compile_options(lib PRIVATE
    -Wall
    -Wextra
    -Werror
    -Wmissing-prototypes
    -Wshadow
    -Wpointer-arith
    -Wno-long-long
    -Wno-unknown-pragmas
    -Wredundant-decls
    -Wno-unused-command-line-argument
    -Wno-unused-function
    -fomit-frame-pointer
    -std=c99
    -pedantic
    -O3
)

# Assembly compiler flags for multi-level build
set_source_files_properties(
    mldsa-native/mldsa/mldsa_native.S
    PROPERTIES
    COMPILE_FLAGS "-DMLD_CONFIG_FILE=\\\"multilevel_config.h\\\" -DMLD_CONFIG_MULTILEVEL_WITH_SHARED -Wall -O3"
)


if(APPLE)
    target_link_libraries(lib PRIVATE "-framework Security")
    target_compile_options(lib PRIVATE "-fvisibility=default")
endif()

if(ANDROID)
    target_link_options(lib PRIVATE "-Wl,-z,max-page-size=16384")
endif()

# Installation rules
install(TARGETS lib
    LIBRARY DESTINATION lib
    ARCHIVE DESTINATION lib
    RUNTIME DESTINATION bin
)

# Install the multi-level header that includes lib.h properly
install(FILES include/mldsa_multilevel.h
    DESTINATION include
)

