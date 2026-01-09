/*
 * Copyright (c) The mlkem-native project authors
 * Copyright (c) The lib-native project authors
 * SPDX-License-Identifier: Apache-2.0 OR ISC OR MIT
 */

/* Three instances of lib-native for all security levels */

/* Mark as internal build before including lib-native */
#define MLD_BUILD_INTERNAL

/* Include level-independent code */
#define MLD_CONFIG_MULTILEVEL_WITH_SHARED 1
/* Keep level-independent headers at the end of monobuild file */
#define MLD_CONFIG_MONOBUILD_KEEP_SHARED_HEADERS
#define MLD_CONFIG_PARAMETER_SET 44
#include "mldsa-native/mldsa/mldsa_native.c"
#undef MLD_CONFIG_MULTILEVEL_WITH_SHARED
#undef MLD_CONFIG_PARAMETER_SET

/* Exclude level-independent code */
#define MLD_CONFIG_MULTILEVEL_NO_SHARED
#define MLD_CONFIG_PARAMETER_SET 65
#include "mldsa-native/mldsa/mldsa_native.c"
/* `#undef` all headers at the and of the monobuild file */
#undef MLD_CONFIG_MONOBUILD_KEEP_SHARED_HEADERS
#undef MLD_CONFIG_PARAMETER_SET

#define MLD_CONFIG_PARAMETER_SET 87
#include "mldsa-native/mldsa/mldsa_native.c"
#undef MLD_CONFIG_PARAMETER_SET
