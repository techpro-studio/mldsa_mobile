/*
 * ML-DSA Multi-Level API Header
 * Includes lib.h three times for all security levels
 */

#ifndef MLDSA_MULTILEVEL_H
#define MLDSA_MULTILEVEL_H

/* Include lib.h three times, once per security level */
/* ML-DSA-44 */
#undef MLD_H
#define MLD_CONFIG_API_PARAMETER_SET 44
#define MLD_CONFIG_API_NAMESPACE_PREFIX MLDSA44
#define MLD_CONFIG_API_NO_SUPERCOP
#include "../mldsa-native/mldsa/mldsa_native.h"

/* ML-DSA-65 */
#undef MLD_H
#undef MLD_CONFIG_API_PARAMETER_SET
#undef MLD_CONFIG_API_NAMESPACE_PREFIX
#define MLD_CONFIG_API_PARAMETER_SET 65
#define MLD_CONFIG_API_NAMESPACE_PREFIX MLDSA65
#define MLD_CONFIG_API_NO_SUPERCOP
#include "../mldsa-native/mldsa/mldsa_native.h"

/* ML-DSA-87 */
#undef MLD_H
#undef MLD_CONFIG_API_PARAMETER_SET
#undef MLD_CONFIG_API_NAMESPACE_PREFIX
#define MLD_CONFIG_API_PARAMETER_SET 87
#define MLD_CONFIG_API_NAMESPACE_PREFIX MLDSA87
#define MLD_CONFIG_API_NO_SUPERCOP
#include "../mldsa-native/mldsa/mldsa_native.h"


#endif /* MLDSA_MULTILEVEL_H */
