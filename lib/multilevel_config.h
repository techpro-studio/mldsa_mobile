/*
 * ML-DSA Multi-Level Build Configuration
 * For iOS and Android mobile platforms
 */

#ifndef MLD_MULTILEVEL_CONFIG_H
#define MLD_MULTILEVEL_CONFIG_H

/******************************************************************************
 * Multi-level build configuration for all three parameter sets (44/65/87)
 *****************************************************************************/

/* Enable multi-level build mode */
#define MLD_CONFIG_MULTILEVEL_BUILD

/* Namespace prefix: will be suffixed with parameter set (44/65/87) */
#define MLD_CONFIG_NAMESPACE_PREFIX MLDSA

/* Disable SUPERCOP naming (required for multi-level builds) */
#define MLD_CONFIG_NO_SUPERCOP

/* Keep internal functions static, but export external API */
#define MLD_CONFIG_INTERNAL_API_QUALIFIER static

/******************************************************************************
 * Native backend configuration
 *****************************************************************************/

/* Enable native ARM NEON backends on ARM64 devices */
#if defined(__aarch64__) || defined(__arm64__)
  #define MLD_CONFIG_USE_NATIVE_BACKEND_ARITH
  #define MLD_CONFIG_ARITH_BACKEND_FILE "native/aarch64/meta.h"
  #define MLD_CONFIG_USE_NATIVE_BACKEND_FIPS202
  #define MLD_CONFIG_FIPS202_BACKEND_FILE "fips202/native/aarch64/auto.h"
#endif

/* Force AARCH64 on iOS (if needed) */
#if defined(__APPLE__) && defined(MLD_FORCE_AARCH64)
  #ifndef __aarch64__
    #define __aarch64__ 1
  #endif
  #ifndef MLD_CONFIG_USE_NATIVE_BACKEND_ARITH
    #define MLD_CONFIG_USE_NATIVE_BACKEND_ARITH
    #define MLD_CONFIG_ARITH_BACKEND_FILE "native/aarch64/meta.h"
  #endif
  #ifndef MLD_CONFIG_USE_NATIVE_BACKEND_FIPS202
    #define MLD_CONFIG_USE_NATIVE_BACKEND_FIPS202
    #define MLD_CONFIG_FIPS202_BACKEND_FILE "fips202/native/aarch64/auto.h"
  #endif
#endif

/******************************************************************************
 * Custom randombytes implementation
 *****************************************************************************/

/* Use custom randombytes from os_rng.c */
#define MLD_CONFIG_CUSTOM_RANDOMBYTES
#if !defined(__ASSEMBLER__)
#include <stdint.h>
#include <stddef.h>
#include "src/sys.h"
extern void randombytes(uint8_t *out, size_t outlen);
static MLD_INLINE int mld_randombytes(uint8_t *ptr, size_t len)
{
    randombytes(ptr, len);
    return 0;
}
#endif /* !__ASSEMBLER__ */

/******************************************************************************
 * Platform-specific optimizations
 *****************************************************************************/

/* Standard library functions (default) */
/* Use memcpy, memset, etc. from libc */

#endif /* !MLD_MULTILEVEL_CONFIG_H */
