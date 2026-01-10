package com.mldsa;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Tests specifically for seed-based key generation
 */
@RunWith(AndroidJUnit4.class)
public class MLDSASeedTest {

    @Test
    public void testDeterministicKeyGeneration_Level44() throws MLDSA.MLDSAException {
        byte[] seed = createTestSeed(0x42);

        MLDSA.KeyPair keyPair1 = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_44);
        MLDSA.KeyPair keyPair2 = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_44);

        // Same seed must produce identical keys
        assertArrayEquals("Public keys must be identical", keyPair1.getPublicKey(), keyPair2.getPublicKey());
        assertArrayEquals("Secret keys must be identical", keyPair1.getSecretKey(), keyPair2.getSecretKey());
    }

    @Test
    public void testDeterministicKeyGeneration_Level65() throws MLDSA.MLDSAException {
        byte[] seed = createTestSeed(0x65);

        MLDSA.KeyPair keyPair1 = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_65);
        MLDSA.KeyPair keyPair2 = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_65);

        assertArrayEquals(keyPair1.getPublicKey(), keyPair2.getPublicKey());
        assertArrayEquals(keyPair1.getSecretKey(), keyPair2.getSecretKey());
    }

    @Test
    public void testDeterministicKeyGeneration_Level87() throws MLDSA.MLDSAException {
        byte[] seed = createTestSeed(0x87);

        MLDSA.KeyPair keyPair1 = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_87);
        MLDSA.KeyPair keyPair2 = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_87);

        assertArrayEquals(keyPair1.getPublicKey(), keyPair2.getPublicKey());
        assertArrayEquals(keyPair1.getSecretKey(), keyPair2.getSecretKey());
    }

    @Test
    public void testDifferentSeeds_ProduceDifferentKeys() throws MLDSA.MLDSAException {
        byte[] seed1 = createTestSeed(0x01);
        byte[] seed2 = createTestSeed(0x02);

        MLDSA.KeyPair keyPair1 = MLDSA.generateKeyPairWithSeed(seed1, MLDSA.SecurityLevel.LEVEL_65);
        MLDSA.KeyPair keyPair2 = MLDSA.generateKeyPairWithSeed(seed2, MLDSA.SecurityLevel.LEVEL_65);

        // Different seeds must produce different keys
        assertFalse("Public keys must be different",
                Arrays.equals(keyPair1.getPublicKey(), keyPair2.getPublicKey()));
        assertFalse("Secret keys must be different",
                Arrays.equals(keyPair1.getSecretKey(), keyPair2.getSecretKey()));
    }

    @Test
    public void testSeededKeyPair_CanSignAndVerify() throws MLDSA.MLDSAException {
        byte[] seed = createTestSeed(0xAB);
        MLDSA.KeyPair keyPair = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_65);

        byte[] message = "Testing seeded keypair".getBytes(StandardCharsets.UTF_8);
        byte[] signature = MLDSA.sign(message, keyPair.getSecretKey(), MLDSA.SecurityLevel.LEVEL_65);

        assertTrue("Signature should verify",
                MLDSA.verify(signature, message, keyPair.getPublicKey(), MLDSA.SecurityLevel.LEVEL_65));
    }

    @Test
    public void testSeededKeyPair_SignaturesConsistentAcrossRuns() throws MLDSA.MLDSAException {
        byte[] seed = createTestSeed(0xCD);
        byte[] message = "Consistent message".getBytes(StandardCharsets.UTF_8);

        // Generate keypair and sign in first "run"
        MLDSA.KeyPair keyPair1 = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_65);
        byte[] signature1 = MLDSA.sign(message, keyPair1.getSecretKey(), MLDSA.SecurityLevel.LEVEL_65);

        // Generate same keypair and sign in second "run"
        MLDSA.KeyPair keyPair2 = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_65);

        // The signature from the first run should verify with the second keypair
        assertTrue("Signature from first run should verify with second keypair",
                MLDSA.verify(signature1, message, keyPair2.getPublicKey(), MLDSA.SecurityLevel.LEVEL_65));
    }

    @Test
    public void testSeededKeyPair_AllZeroSeed() throws MLDSA.MLDSAException {
        byte[] zeroSeed = new byte[32]; // All zeros

        MLDSA.KeyPair keyPair1 = MLDSA.generateKeyPairWithSeed(zeroSeed, MLDSA.SecurityLevel.LEVEL_44);
        MLDSA.KeyPair keyPair2 = MLDSA.generateKeyPairWithSeed(zeroSeed, MLDSA.SecurityLevel.LEVEL_44);

        assertArrayEquals(keyPair1.getPublicKey(), keyPair2.getPublicKey());
        assertArrayEquals(keyPair1.getSecretKey(), keyPair2.getSecretKey());

        // Keys should still be valid
        byte[] message = "Test".getBytes(StandardCharsets.UTF_8);
        byte[] signature = MLDSA.sign(message, keyPair1.getSecretKey(), MLDSA.SecurityLevel.LEVEL_44);
        assertTrue(MLDSA.verify(signature, message, keyPair1.getPublicKey(), MLDSA.SecurityLevel.LEVEL_44));
    }

    @Test
    public void testSeededKeyPair_AllOnesSeed() throws MLDSA.MLDSAException {
        byte[] onesSeed = new byte[32];
        Arrays.fill(onesSeed, (byte) 0xFF);

        MLDSA.KeyPair keyPair1 = MLDSA.generateKeyPairWithSeed(onesSeed, MLDSA.SecurityLevel.LEVEL_65);
        MLDSA.KeyPair keyPair2 = MLDSA.generateKeyPairWithSeed(onesSeed, MLDSA.SecurityLevel.LEVEL_65);

        assertArrayEquals(keyPair1.getPublicKey(), keyPair2.getPublicKey());
        assertArrayEquals(keyPair1.getSecretKey(), keyPair2.getSecretKey());

        // Keys should still be valid
        byte[] message = "Test".getBytes(StandardCharsets.UTF_8);
        byte[] signature = MLDSA.sign(message, keyPair1.getSecretKey(), MLDSA.SecurityLevel.LEVEL_65);
        assertTrue(MLDSA.verify(signature, message, keyPair1.getPublicKey(), MLDSA.SecurityLevel.LEVEL_65));
    }

    @Test
    public void testSeededKeyPair_IncrementingSeed() throws MLDSA.MLDSAException {
        byte[] seed = new byte[32];
        for (int i = 0; i < 32; i++) {
            seed[i] = (byte) i;
        }

        MLDSA.KeyPair keyPair1 = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_87);
        MLDSA.KeyPair keyPair2 = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_87);

        assertArrayEquals(keyPair1.getPublicKey(), keyPair2.getPublicKey());
        assertArrayEquals(keyPair1.getSecretKey(), keyPair2.getSecretKey());
    }

    @Test
    public void testSeededKeyPair_DifferentLevelsSameSeed() throws MLDSA.MLDSAException {
        byte[] seed = createTestSeed(0x99);

        MLDSA.KeyPair keyPair44 = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_44);
        MLDSA.KeyPair keyPair65 = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_65);
        MLDSA.KeyPair keyPair87 = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_87);

        // Same seed with different security levels should produce different key sizes
        assertEquals(1312, keyPair44.getPublicKey().length);
        assertEquals(1952, keyPair65.getPublicKey().length);
        assertEquals(2592, keyPair87.getPublicKey().length);

        // Keys should not be equal (different parameter sets)
        assertFalse(Arrays.equals(
                Arrays.copyOf(keyPair44.getPublicKey(), Math.min(keyPair44.getPublicKey().length, keyPair65.getPublicKey().length)),
                Arrays.copyOf(keyPair65.getPublicKey(), Math.min(keyPair44.getPublicKey().length, keyPair65.getPublicKey().length))
        ));
    }

    @Test
    public void testSeededKeyPair_SingleBitDifference() throws MLDSA.MLDSAException {
        byte[] seed1 = createTestSeed(0x00);
        byte[] seed2 = createTestSeed(0x00);
        seed2[0] ^= 0x01; // Flip one bit

        MLDSA.KeyPair keyPair1 = MLDSA.generateKeyPairWithSeed(seed1, MLDSA.SecurityLevel.LEVEL_65);
        MLDSA.KeyPair keyPair2 = MLDSA.generateKeyPairWithSeed(seed2, MLDSA.SecurityLevel.LEVEL_65);

        // Even a single bit difference should produce completely different keys
        assertFalse("Public keys should be different",
                Arrays.equals(keyPair1.getPublicKey(), keyPair2.getPublicKey()));
        assertFalse("Secret keys should be different",
                Arrays.equals(keyPair1.getSecretKey(), keyPair2.getSecretKey()));
    }

    @Test
    public void testSeededVsRandomKeyGeneration() throws MLDSA.MLDSAException {
        // Generate a key with random generation
        MLDSA.KeyPair randomKeyPair1 = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_65);
        MLDSA.KeyPair randomKeyPair2 = MLDSA.generateKeyPair(MLDSA.SecurityLevel.LEVEL_65);

        // Random keys should be different
        assertFalse("Random public keys should be different",
                Arrays.equals(randomKeyPair1.getPublicKey(), randomKeyPair2.getPublicKey()));

        // Generate a key with seed
        byte[] seed = createTestSeed(0x77);
        MLDSA.KeyPair seededKeyPair1 = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_65);
        MLDSA.KeyPair seededKeyPair2 = MLDSA.generateKeyPairWithSeed(seed, MLDSA.SecurityLevel.LEVEL_65);

        // Seeded keys should be identical
        assertArrayEquals("Seeded public keys should be identical",
                seededKeyPair1.getPublicKey(), seededKeyPair2.getPublicKey());
    }

    /**
     * Helper method to create a 32-byte test seed filled with a repeating pattern
     */
    private byte[] createTestSeed(int fillByte) {
        byte[] seed = new byte[32];
        Arrays.fill(seed, (byte) fillByte);
        return seed;
    }
}
