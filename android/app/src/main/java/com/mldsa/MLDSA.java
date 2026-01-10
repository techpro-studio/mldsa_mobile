package com.mldsa;
public class MLDSA {

    static {
        System.loadLibrary("mldsa-jni");
    }

    /**
     * Constants for ML-DSA
     */
    public static final int SEEDBYTES = 32;
    public static final int MAX_CONTEXT_LENGTH = 255;

    public enum SecurityLevel {
        LEVEL_44(44),
        LEVEL_65(65),
        LEVEL_87(87);

        private final int value;

        SecurityLevel(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static SecurityLevel fromValue(int value) {
            for (SecurityLevel level : values()) {
                if (level.value == value) {
                    return level;
                }
            }
            throw new IllegalArgumentException("Invalid security level: " + value);
        }
    }

    public static class KeyPair {
        private final byte[] publicKey;
        private final byte[] secretKey;
        private final SecurityLevel securityLevel;

        public KeyPair(byte[] publicKey, byte[] secretKey, SecurityLevel securityLevel) {
            this.publicKey = publicKey;
            this.secretKey = secretKey;
            this.securityLevel = securityLevel;
        }

        public byte[] getPublicKey() {
            return publicKey;
        }

        public byte[] getSecretKey() {
            return secretKey;
        }

        public SecurityLevel getSecurityLevel() {
            return securityLevel;
        }
    }

    public static class MLDSAException extends Exception {
        private final int errorCode;

        public MLDSAException(String message, int errorCode) {
            super(message);
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }

    /**
     * Get the public key size in bytes for a given security level
     */
    public static native int getPublicKeySize(int level);

    /**
     * Get the secret key size in bytes for a given security level
     */
    public static native int getSecretKeySize(int level);

    /**
     * Get the signature size in bytes for a given security level
     */
    public static native int getSignatureSize(int level);

    /**
     * Generate a new keypair for the specified security level
     *
     * @param level The security level (44, 65, or 87)
     * @return A new KeyPair
     * @throws MLDSAException if key generation fails
     */
    public static KeyPair generateKeyPair(SecurityLevel level) throws MLDSAException {
        int levelValue = level.getValue();
        int pkSize = getPublicKeySize(levelValue);
        int skSize = getSecretKeySize(levelValue);

        byte[] publicKey = new byte[pkSize];
        byte[] secretKey = new byte[skSize];

        int result = nativeGenerateKeyPair(levelValue, publicKey, secretKey);
        if (result != 0) {
            throw new MLDSAException("Key generation failed with code: " + result, result);
        }

        return new KeyPair(publicKey, secretKey, level);
    }

    /**
     * Generate a keypair deterministically using a seed (internal API)
     *
     * @param seed The seed for key generation (must be 32 bytes)
     * @param level The security level (44, 65, or 87)
     * @return A new KeyPair
     * @throws MLDSAException if key generation fails or seed is invalid
     */
    public static KeyPair generateKeyPairWithSeed(byte[] seed, SecurityLevel level) throws MLDSAException {
        if (seed == null || seed.length != 32) {
            throw new MLDSAException("Seed must be exactly 32 bytes", -100);
        }

        int levelValue = level.getValue();
        int pkSize = getPublicKeySize(levelValue);
        int skSize = getSecretKeySize(levelValue);

        byte[] publicKey = new byte[pkSize];
        byte[] secretKey = new byte[skSize];

        int result = nativeGenerateKeyPairWithSeed(levelValue, seed, publicKey, secretKey);
        if (result != 0) {
            throw new MLDSAException("Key generation with seed failed with code: " + result, result);
        }

        return new KeyPair(publicKey, secretKey, level);
    }

    /**
     * Validate context string length
     *
     * @param context The context to validate
     * @throws MLDSAException if context is too long
     */
    public static void validateContext(byte[] context) throws MLDSAException {
        if (context != null && context.length > MAX_CONTEXT_LENGTH) {
            throw new MLDSAException("Context length must be <= " + MAX_CONTEXT_LENGTH + " bytes, got " + context.length, -100);
        }
    }


    /**
     * Sign a message with a secret key
     *
     * @param message The message to sign
     * @param secretKey The secret key
     * @param context Optional context string (can be null, max 255 bytes)
     * @param level The security level
     * @return The signature
     * @throws MLDSAException if signing fails
     */
    public static byte[] sign(byte[] message, byte[] secretKey, byte[] context, SecurityLevel level)
            throws MLDSAException {
        if (message == null || secretKey == null) {
            throw new MLDSAException("Message and secret key are required", -100);
        }

        validateContext(context);

        int levelValue = level.getValue();
        int sigSize = getSignatureSize(levelValue);
        byte[] signature = new byte[sigSize];

        int result = nativeSign(levelValue, message, secretKey, context, signature);
        if (result < 0) {
            throw new MLDSAException("Signing failed with code: " + result, result);
        }

        // Trim signature to actual size
        if (result < sigSize) {
            byte[] trimmed = new byte[result];
            System.arraycopy(signature, 0, trimmed, 0, result);
            return trimmed;
        }

        return signature;
    }

    /**
     * Sign a message without context
     */
    public static byte[] sign(byte[] message, byte[] secretKey, SecurityLevel level)
            throws MLDSAException {
        return sign(message, secretKey, null, level);
    }
    /**
     * Verify a signature
     *
     * @param signature The signature to verify
     * @param message The original message
     * @param publicKey The public key
     * @param context Optional context string (can be null, max 255 bytes)
     * @param level The security level
     * @return true if the signature is valid, false otherwise
     * @throws MLDSAException if verification encounters an error (other than invalid signature)
     */
    public static boolean verify(byte[] signature, byte[] message, byte[] publicKey,
                                  byte[] context, SecurityLevel level) throws MLDSAException {
        if (signature == null || message == null || publicKey == null) {
            throw new MLDSAException("Signature, message, and public key are required", -100);
        }

        validateContext(context);

        int levelValue = level.getValue();
        int result = nativeVerify(levelValue, signature, message, publicKey, context);

        if (result == 0) {
            return true;
        } else if (result == -1) {
            // Signature verification failed (invalid signature)
            return false;
        } else {
            throw new MLDSAException("Verification failed with code: " + result, result);
        }
    }

    /**
     * Verify a signature without context
     */
    public static boolean verify(byte[] signature, byte[] message, byte[] publicKey, SecurityLevel level)
            throws MLDSAException {
        return verify(signature, message, publicKey, null, level);
    }

    // Native methods
    private static native int nativeGenerateKeyPair(int level, byte[] publicKey, byte[] secretKey);
    private static native int nativeGenerateKeyPairWithSeed(int level, byte[] seed, byte[] publicKey, byte[] secretKey);
    private static native int nativeSign(int level, byte[] message, byte[] secretKey, byte[] context, byte[] signature);
    private static native int nativeVerify(int level, byte[] signature, byte[] message, byte[] publicKey, byte[] context);
}
