package com.mldsa;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Instrumented tests for ML-DSA library
 */
@RunWith(AndroidJUnit4.class)
public class MLDSATest {

    @Test
    public void testGetKeySizes() {
        // Test ML-DSA-44
        assertEquals(1312, MLDSA.getPublicKeySize(44));
        assertEquals(2560, MLDSA.getSecretKeySize(44));
        assertEquals(2420, MLDSA.getSignatureSize(44));

        // Test ML-DSA-65
        assertEquals(1952, MLDSA.getPublicKeySize(65));
        assertEquals(4032, MLDSA.getSecretKeySize(65));
        assertEquals(3309, MLDSA.getSignatureSize(65));

        // Test ML-DSA-87
        assertEquals(2592, MLDSA.getPublicKeySize(87));
        assertEquals(4896, MLDSA.getSecretKeySize(87));
        assertEquals(4627, MLDSA.getSignatureSize(87));

        // Test invalid level
        assertEquals(-1, MLDSA.getPublicKeySize(99));
    }

    @Test
    public void testKeyGeneration_Level44() throws MLDSA.MLDSAException {
        MLDSA.KeyPair keyPair = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_44);

        assertNotNull(keyPair);
        assertNotNull(keyPair.getPublicKey());
        assertNotNull(keyPair.getSecretKey());
        assertEquals(MLDSA.SecurityLevel.LEVEL_44, keyPair.getSecurityLevel());
        assertEquals(1312, keyPair.getPublicKey().length);
        assertEquals(2560, keyPair.getSecretKey().length);
    }

    @Test
    public void testKeyGeneration_Level65() throws MLDSA.MLDSAException {
        MLDSA.KeyPair keyPair = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_65);

        assertNotNull(keyPair);
        assertNotNull(keyPair.getPublicKey());
        assertNotNull(keyPair.getSecretKey());
        assertEquals(MLDSA.SecurityLevel.LEVEL_65, keyPair.getSecurityLevel());
        assertEquals(1952, keyPair.getPublicKey().length);
        assertEquals(4032, keyPair.getSecretKey().length);
    }

    @Test
    public void testKeyGeneration_Level87() throws MLDSA.MLDSAException {
        MLDSA.KeyPair keyPair = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_87);

        assertNotNull(keyPair);
        assertNotNull(keyPair.getPublicKey());
        assertNotNull(keyPair.getSecretKey());
        assertEquals(MLDSA.SecurityLevel.LEVEL_87, keyPair.getSecurityLevel());
        assertEquals(2592, keyPair.getPublicKey().length);
        assertEquals(4896, keyPair.getSecretKey().length);
    }

    @Test
    public void testKeyGenerationWithSeed() throws MLDSA.MLDSAException {
        // Create a fixed seed for deterministic key generation
        byte[] seed = new byte[32];
        for (int i = 0; i < seed.length; i++) {
            seed[i] = (byte) i;
        }

        MLDSA.KeyPair keyPair1 = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_65);
        MLDSA.KeyPair keyPair2 = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_65);

        // Same seed should produce same keys
        assertArrayEquals(keyPair1.getPublicKey(), keyPair2.getPublicKey());
        assertArrayEquals(keyPair1.getSecretKey(), keyPair2.getSecretKey());
    }

    @Test
    public void testKeyGenerationWithSeed_InvalidSeedLength() {
        // Test with seed too short
        byte[] shortSeed = new byte[16];
        try {
            MLDSA.generateKeyPairWithSeed(shortSeed, MLDSA.SecurityLevel.LEVEL_44);
            fail("Should throw exception for invalid seed length");
        } catch (MLDSA.MLDSAException e) {
            assertEquals(-100, e.getErrorCode());
            assertTrue(e.getMessage().contains("32 bytes"));
        }

        // Test with seed too long
        byte[] longSeed = new byte[64];
        try {
            MLDSA.generateKeyPairWithSeed(longSeed, MLDSA.SecurityLevel.LEVEL_44);
            fail("Should throw exception for invalid seed length");
        } catch (MLDSA.MLDSAException e) {
            assertEquals(-100, e.getErrorCode());
        }

        // Test with null seed
        try {
            MLDSA.generateKeyPairWithSeed(null, MLDSA.SecurityLevel.LEVEL_44);
            fail("Should throw exception for null seed");
        } catch (MLDSA.MLDSAException e) {
            assertEquals(-100, e.getErrorCode());
        }
    }

    @Test
    public void testSignAndVerify_SimpleMessage() throws MLDSA.MLDSAException {
        // Generate keypair
        MLDSA.KeyPair keyPair = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_65);

        // Message to sign
        String messageStr = "Hello, ML-DSA!";
        byte[] message = messageStr.getBytes(StandardCharsets.UTF_8);

        // Sign the message
        byte[] signature = MLDSA.sign(message, keyPair.getSecretKey(), MLDSA.SecurityLevel.LEVEL_65);

        assertNotNull(signature);
        assertTrue(signature.length > 0);
        assertTrue(signature.length <= MLDSA.getSignatureSize(65));

        // Verify the signature
        boolean isValid = MLDSA.verify(signature, message, keyPair.getPublicKey(), MLDSA.SecurityLevel.LEVEL_65);
        assertTrue("Signature should be valid", isValid);
    }

    @Test
    public void testSignAndVerify_AllLevels() throws MLDSA.MLDSAException {
        byte[] message = "Test message for all security levels".getBytes(StandardCharsets.UTF_8);

        // Test Level 44
        MLDSA.KeyPair keyPair44 = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_44);
        byte[] sig44 = MLDSA.sign(message, keyPair44.getSecretKey(), MLDSA.SecurityLevel.LEVEL_44);
        assertTrue(MLDSA.verify(sig44, message, keyPair44.getPublicKey(), MLDSA.SecurityLevel.LEVEL_44));

        // Test Level 65
        MLDSA.KeyPair keyPair65 = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_65);
        byte[] sig65 = MLDSA.sign(message, keyPair65.getSecretKey(), MLDSA.SecurityLevel.LEVEL_65);
        assertTrue(MLDSA.verify(sig65, message, keyPair65.getPublicKey(), MLDSA.SecurityLevel.LEVEL_65));

        // Test Level 87
        MLDSA.KeyPair keyPair87 = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_87);
        byte[] sig87 = MLDSA.sign(message, keyPair87.getSecretKey(), MLDSA.SecurityLevel.LEVEL_87);
        assertTrue(MLDSA.verify(sig87, message, keyPair87.getPublicKey(), MLDSA.SecurityLevel.LEVEL_87));
    }

    @Test
    public void testSignAndVerify_WithContext() throws MLDSA.MLDSAException {
        MLDSA.KeyPair keyPair = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_65);

        byte[] message = "Message with context".getBytes(StandardCharsets.UTF_8);
        byte[] context = "test-context".getBytes(StandardCharsets.UTF_8);

        // Sign with context
        byte[] signature = MLDSA.sign(message, keyPair.getSecretKey(), context, MLDSA.SecurityLevel.LEVEL_65);

        // Verify with same context should succeed
        assertTrue(MLDSA.verify(signature, message, keyPair.getPublicKey(), context, MLDSA.SecurityLevel.LEVEL_65));

        // Verify with different context should fail
        byte[] differentContext = "different-context".getBytes(StandardCharsets.UTF_8);
        assertFalse(MLDSA.verify(signature, message, keyPair.getPublicKey(), differentContext, MLDSA.SecurityLevel.LEVEL_65));

        // Verify without context should fail
        assertFalse(MLDSA.verify(signature, message, keyPair.getPublicKey(), MLDSA.SecurityLevel.LEVEL_65));
    }

    @Test
    public void testVerify_InvalidSignature() throws MLDSA.MLDSAException {
        MLDSA.KeyPair keyPair = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_65);

        byte[] message = "Original message".getBytes(StandardCharsets.UTF_8);
        byte[] signature = MLDSA.sign(message, keyPair.getSecretKey(), MLDSA.SecurityLevel.LEVEL_65);

        // Tamper with the message
        byte[] tamperedMessage = "Tampered message".getBytes(StandardCharsets.UTF_8);
        assertFalse("Tampered message should fail verification",
                MLDSA.verify(signature, tamperedMessage, keyPair.getPublicKey(), MLDSA.SecurityLevel.LEVEL_65));

        // Tamper with the signature
        byte[] tamperedSignature = Arrays.copyOf(signature, signature.length);
        tamperedSignature[0] ^= 0x01; // Flip a bit
        assertFalse("Tampered signature should fail verification",
                MLDSA.verify(tamperedSignature, message, keyPair.getPublicKey(), MLDSA.SecurityLevel.LEVEL_65));
    }

    @Test
    public void testVerify_WrongPublicKey() throws MLDSA.MLDSAException {
        MLDSA.KeyPair keyPair1 = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_65);
        MLDSA.KeyPair keyPair2 = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_65);

        byte[] message = "Test message".getBytes(StandardCharsets.UTF_8);
        byte[] signature = MLDSA.sign(message, keyPair1.getSecretKey(), MLDSA.SecurityLevel.LEVEL_65);

        // Verify with wrong public key should fail
        assertFalse("Verification with wrong key should fail",
                MLDSA.verify(signature, message, keyPair2.getPublicKey(), MLDSA.SecurityLevel.LEVEL_65));
    }

    @Test
    public void testSign_EmptyMessage() throws MLDSA.MLDSAException {
        MLDSA.KeyPair keyPair = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_65);

        byte[] emptyMessage = new byte[0];
        byte[] signature = MLDSA.sign(emptyMessage, keyPair.getSecretKey(), MLDSA.SecurityLevel.LEVEL_65);

        assertNotNull(signature);
        assertTrue(MLDSA.verify(signature, emptyMessage, keyPair.getPublicKey(), MLDSA.SecurityLevel.LEVEL_65));
    }

    @Test
    public void testSign_LargeMessage() throws MLDSA.MLDSAException {
        MLDSA.KeyPair keyPair = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_65);

        // Create a large message (1 MB)
        byte[] largeMessage = new byte[1024 * 1024];
        new SecureRandom().nextBytes(largeMessage);

        byte[] signature = MLDSA.sign(largeMessage, keyPair.getSecretKey(), MLDSA.SecurityLevel.LEVEL_65);

        assertNotNull(signature);
        assertTrue(MLDSA.verify(signature, largeMessage, keyPair.getPublicKey(), MLDSA.SecurityLevel.LEVEL_65));
    }

    @Test
    public void testSign_NullInputs() throws MLDSA.MLDSAException {
        MLDSA.KeyPair keyPair = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_65);
        byte[] message = "Test".getBytes(StandardCharsets.UTF_8);

        // Null message
        try {
            MLDSA.sign(null, keyPair.getSecretKey(), MLDSA.SecurityLevel.LEVEL_65);
            fail("Should throw exception for null message");
        } catch (MLDSA.MLDSAException e) {
            assertEquals(-100, e.getErrorCode());
        }

        // Null secret key
        try {
            MLDSA.sign(message, null, MLDSA.SecurityLevel.LEVEL_65);
            fail("Should throw exception for null secret key");
        } catch (MLDSA.MLDSAException e) {
            assertEquals(-100, e.getErrorCode());
        }
    }

    @Test
    public void testVerify_NullInputs() throws MLDSA.MLDSAException {
        MLDSA.KeyPair keyPair = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_65);
        byte[] message = "Test".getBytes(StandardCharsets.UTF_8);
        byte[] signature = MLDSA.sign(message, keyPair.getSecretKey(), MLDSA.SecurityLevel.LEVEL_65);

        // Null signature
        try {
            MLDSA.verify(null, message, keyPair.getPublicKey(), MLDSA.SecurityLevel.LEVEL_65);
            fail("Should throw exception for null signature");
        } catch (MLDSA.MLDSAException e) {
            assertEquals(-100, e.getErrorCode());
        }

        // Null message
        try {
            MLDSA.verify(signature, null, keyPair.getPublicKey(), MLDSA.SecurityLevel.LEVEL_65);
            fail("Should throw exception for null message");
        } catch (MLDSA.MLDSAException e) {
            assertEquals(-100, e.getErrorCode());
        }

        // Null public key
        try {
            MLDSA.verify(signature, message, null, MLDSA.SecurityLevel.LEVEL_65);
            fail("Should throw exception for null public key");
        } catch (MLDSA.MLDSAException e) {
            assertEquals(-100, e.getErrorCode());
        }
    }

    @Test
    public void testSecurityLevelEnum() {
        assertEquals(44, MLDSA.SecurityLevel.LEVEL_44.getValue());
        assertEquals(65, MLDSA.SecurityLevel.LEVEL_65.getValue());
        assertEquals(87, MLDSA.SecurityLevel.LEVEL_87.getValue());

        assertEquals(MLDSA.SecurityLevel.LEVEL_44, MLDSA.SecurityLevel.fromValue(44));
        assertEquals(MLDSA.SecurityLevel.LEVEL_65, MLDSA.SecurityLevel.fromValue(65));
        assertEquals(MLDSA.SecurityLevel.LEVEL_87, MLDSA.SecurityLevel.fromValue(87));

        try {
            MLDSA.SecurityLevel.fromValue(99);
            fail("Should throw exception for invalid security level");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("Invalid security level"));
        }
    }

    @Test
    public void testMultipleSignatures_SameMessage() throws MLDSA.MLDSAException {
        MLDSA.KeyPair keyPair = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_65);
        byte[] message = "Same message".getBytes(StandardCharsets.UTF_8);

        // Generate multiple signatures for the same message
        // (ML-DSA is randomized, so signatures should be different)
        byte[] sig1 = MLDSA.sign(message, keyPair.getSecretKey(), MLDSA.SecurityLevel.LEVEL_65);
        byte[] sig2 = MLDSA.sign(message, keyPair.getSecretKey(), MLDSA.SecurityLevel.LEVEL_65);

        // Signatures should be different (randomized)
        assertFalse("Signatures should be different due to randomization",
                Arrays.equals(sig1, sig2));

        // But both should verify correctly
        assertTrue(MLDSA.verify(sig1, message, keyPair.getPublicKey(), MLDSA.SecurityLevel.LEVEL_65));
        assertTrue(MLDSA.verify(sig2, message, keyPair.getPublicKey(), MLDSA.SecurityLevel.LEVEL_65));
    }

    @Test
    public void testKeyPairIndependence() throws MLDSA.MLDSAException {
        // Generate multiple keypairs
        MLDSA.KeyPair keyPair1 = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_65);
        MLDSA.KeyPair keyPair2 = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_65);
        MLDSA.KeyPair keyPair3 = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_65);

        // All public keys should be different
        assertFalse(Arrays.equals(keyPair1.getPublicKey(), keyPair2.getPublicKey()));
        assertFalse(Arrays.equals(keyPair1.getPublicKey(), keyPair3.getPublicKey()));
        assertFalse(Arrays.equals(keyPair2.getPublicKey(), keyPair3.getPublicKey()));

        // All secret keys should be different
        assertFalse(Arrays.equals(keyPair1.getSecretKey(), keyPair2.getSecretKey()));
        assertFalse(Arrays.equals(keyPair1.getSecretKey(), keyPair3.getSecretKey()));
        assertFalse(Arrays.equals(keyPair2.getSecretKey(), keyPair3.getSecretKey()));
    }
}
