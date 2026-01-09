/*
 * JNI wrapper for lib-native
 */

#include <jni.h>
#include <cstring>
#include <android/log.h>

extern "C" {
#include "mldsa_multilevel.h"
}

extern "C" {

JNIEXPORT jint JNICALL
Java_com_mldsa_MLDSA_getPublicKeySize(JNIEnv *env, jclass clazz, jint level) {
    switch (level) {
        case 44:
            return MLDSA44_PUBLICKEYBYTES;
        case 65:
            return MLDSA65_PUBLICKEYBYTES;
        case 87:
            return MLDSA87_PUBLICKEYBYTES;
        default:
            return -1;
    }
}

JNIEXPORT jint JNICALL
Java_com_mldsa_MLDSA_getSecretKeySize(JNIEnv *env, jclass clazz, jint level) {
    switch (level) {
        case 44:
            return MLDSA44_SECRETKEYBYTES;
        case 65:
            return MLDSA65_SECRETKEYBYTES;
        case 87:
            return MLDSA87_SECRETKEYBYTES;
        default:
            return -1;
    }
}

JNIEXPORT jint JNICALL
Java_com_mldsa_MLDSA_getSignatureSize(JNIEnv *env, jclass clazz, jint level) {
    switch (level) {
        case 44:
            return MLDSA44_BYTES;
        case 65:
            return MLDSA65_BYTES;
        case 87:
            return MLDSA87_BYTES;
        default:
            return -1;
    }
}

JNIEXPORT jint JNICALL
Java_com_mldsa_MLDSA_nativeGenerateKeyPair(JNIEnv *env, jclass clazz,
                                             jint level,
                                             jbyteArray publicKey,
                                             jbyteArray secretKey) {
    jbyte *pk = env->GetByteArrayElements(publicKey, nullptr);
    jbyte *sk = env->GetByteArrayElements(secretKey, nullptr);

    if (pk == nullptr || sk == nullptr) {
        if (pk != nullptr) env->ReleaseByteArrayElements(publicKey, pk, JNI_ABORT);
        if (sk != nullptr) env->ReleaseByteArrayElements(secretKey, sk, JNI_ABORT);
        return -2; // Out of memory
    }

    int ret;
    switch (level) {
        case 44:
            ret = MLDSA44_keypair((uint8_t *)pk, (uint8_t *)sk);
            break;
        case 65:
            ret = MLDSA65_keypair((uint8_t *)pk, (uint8_t *)sk);
            break;
        case 87:
            ret = MLDSA87_keypair((uint8_t *)pk, (uint8_t *)sk);
            break;
        default:
            ret = -100; // Invalid parameter
    }

    if (ret == 0) {
        env->ReleaseByteArrayElements(publicKey, pk, 0);
        env->ReleaseByteArrayElements(secretKey, sk, 0);
    } else {
        env->ReleaseByteArrayElements(publicKey, pk, JNI_ABORT);
        // Clear sensitive data before releasing
        memset(sk, 0, env->GetArrayLength(secretKey));
        env->ReleaseByteArrayElements(secretKey, sk, 0);
    }

    return ret;
}

JNIEXPORT jint JNICALL
Java_com_mldsa_MLDSA_nativeSign(JNIEnv *env, jclass clazz,
                                 jint level,
                                 jbyteArray message,
                                 jbyteArray secretKey,
                                 jbyteArray context,
                                 jbyteArray signature) {
    jsize mlen = env->GetArrayLength(message);
    jsize sklen = env->GetArrayLength(secretKey);
    jsize ctxlen = (context != nullptr) ? env->GetArrayLength(context) : 0;

    jbyte *m = env->GetByteArrayElements(message, nullptr);
    jbyte *sk = env->GetByteArrayElements(secretKey, nullptr);
    jbyte *ctx = (context != nullptr) ? env->GetByteArrayElements(context, nullptr) : nullptr;
    jbyte *sig = env->GetByteArrayElements(signature, nullptr);

    if (m == nullptr || sk == nullptr || sig == nullptr) {
        if (m != nullptr) env->ReleaseByteArrayElements(message, m, JNI_ABORT);
        if (sk != nullptr) env->ReleaseByteArrayElements(secretKey, sk, JNI_ABORT);
        if (ctx != nullptr) env->ReleaseByteArrayElements(context, ctx, JNI_ABORT);
        if (sig != nullptr) env->ReleaseByteArrayElements(signature, sig, JNI_ABORT);
        return -2; // Out of memory
    }

    size_t siglen;
    int ret;

    switch (level) {
        case 44:
            ret = MLDSA44_signature((uint8_t *)sig, &siglen,
                                    (const uint8_t *)m, mlen,
                                    (const uint8_t *)ctx, ctxlen,
                                    (const uint8_t *)sk);
            break;
        case 65:
            ret = MLDSA65_signature((uint8_t *)sig, &siglen,
                                    (const uint8_t *)m, mlen,
                                    (const uint8_t *)ctx, ctxlen,
                                    (const uint8_t *)sk);
            break;
        case 87:
            ret = MLDSA87_signature((uint8_t *)sig, &siglen,
                                    (const uint8_t *)m, mlen,
                                    (const uint8_t *)ctx, ctxlen,
                                    (const uint8_t *)sk);
            break;
        default:
            ret = -100; // Invalid parameter
            siglen = 0;
    }

    env->ReleaseByteArrayElements(message, m, JNI_ABORT);
    env->ReleaseByteArrayElements(secretKey, sk, JNI_ABORT);
    if (ctx != nullptr) env->ReleaseByteArrayElements(context, ctx, JNI_ABORT);

    if (ret == 0) {
        env->ReleaseByteArrayElements(signature, sig, 0);
        return (jint)siglen;
    } else {
        env->ReleaseByteArrayElements(signature, sig, JNI_ABORT);
        return ret;
    }
}

JNIEXPORT jint JNICALL
Java_com_mldsa_MLDSA_nativeVerify(JNIEnv *env, jclass clazz,
                                   jint level,
                                   jbyteArray signature,
                                   jbyteArray message,
                                   jbyteArray publicKey,
                                   jbyteArray context) {
    jsize siglen = env->GetArrayLength(signature);
    jsize mlen = env->GetArrayLength(message);
    jsize pklen = env->GetArrayLength(publicKey);
    jsize ctxlen = (context != nullptr) ? env->GetArrayLength(context) : 0;

    jbyte *sig = env->GetByteArrayElements(signature, nullptr);
    jbyte *m = env->GetByteArrayElements(message, nullptr);
    jbyte *pk = env->GetByteArrayElements(publicKey, nullptr);
    jbyte *ctx = (context != nullptr) ? env->GetByteArrayElements(context, nullptr) : nullptr;

    if (sig == nullptr || m == nullptr || pk == nullptr) {
        if (sig != nullptr) env->ReleaseByteArrayElements(signature, sig, JNI_ABORT);
        if (m != nullptr) env->ReleaseByteArrayElements(message, m, JNI_ABORT);
        if (pk != nullptr) env->ReleaseByteArrayElements(publicKey, pk, JNI_ABORT);
        if (ctx != nullptr) env->ReleaseByteArrayElements(context, ctx, JNI_ABORT);
        return -2; // Out of memory
    }

    int ret;

    switch (level) {
        case 44:
            ret = MLDSA44_verify((const uint8_t *)sig, siglen,
                                 (const uint8_t *)m, mlen,
                                 (const uint8_t *)ctx, ctxlen,
                                 (const uint8_t *)pk);
            break;
        case 65:
            ret = MLDSA65_verify((const uint8_t *)sig, siglen,
                                 (const uint8_t *)m, mlen,
                                 (const uint8_t *)ctx, ctxlen,
                                 (const uint8_t *)pk);
            break;
        case 87:
            ret = MLDSA87_verify((const uint8_t *)sig, siglen,
                                 (const uint8_t *)m, mlen,
                                 (const uint8_t *)ctx, ctxlen,
                                 (const uint8_t *)pk);
            break;
        default:
            ret = -100; // Invalid parameter
    }

    env->ReleaseByteArrayElements(signature, sig, JNI_ABORT);
    env->ReleaseByteArrayElements(message, m, JNI_ABORT);
    env->ReleaseByteArrayElements(publicKey, pk, JNI_ABORT);
    if (ctx != nullptr) env->ReleaseByteArrayElements(context, ctx, JNI_ABORT);

    return ret;
}

} // extern "C"
