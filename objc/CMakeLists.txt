add_library(MLDSA SHARED src/MLDSA.m include/MLDSA.h)

target_include_directories(MLDSA PUBLIC ${CMAKE_CURRENT_SOURCE_DIR}/include)

target_link_libraries(MLDSA PRIVATE lib "-framework Foundation")

set_target_properties(MLDSA PROPERTIES
    FRAMEWORK TRUE
    FRAMEWORK_VERSION A
    MACOSX_FRAMEWORK_IDENTIFIER com.mldsa
    PUBLIC_HEADER include/MLDSA.h
)