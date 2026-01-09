//
//  include.m
//  include
//
//  Objective-C wrapper for lib-native
//

#import "MLDSA.h"

// Use the multi-level header from lib/
#include "../../lib/include/mldsa_multilevel.h"

// Error domain
static NSString *const MLDSAErrorDomain = @"com.lib.error";

@implementation MLDSAKeyPair

- (instancetype)initWithPublicKey:(NSData *)publicKey
                        secretKey:(NSData *)secretKey
                    securityLevel:(MLDSASecurityLevel)securityLevel {
    if (self = [super init]) {
        _publicKey = [publicKey copy];
        _secretKey = [secretKey copy];
        _securityLevel = securityLevel;
    }
    return self;
}

@end

@implementation MLDSA

+ (NSInteger)publicKeySizeForLevel:(MLDSASecurityLevel)level {
    switch (level) {
        case MLDSASecurityLevel44:
            return MLDSA44_PUBLICKEYBYTES;
        case MLDSASecurityLevel65:
            return MLDSA65_PUBLICKEYBYTES;
        case MLDSASecurityLevel87:
            return MLDSA87_PUBLICKEYBYTES;
    }
}

+ (NSInteger)secretKeySizeForLevel:(MLDSASecurityLevel)level {
    switch (level) {
        case MLDSASecurityLevel44:
            return MLDSA44_SECRETKEYBYTES;
        case MLDSASecurityLevel65:
            return MLDSA65_SECRETKEYBYTES;
        case MLDSASecurityLevel87:
            return MLDSA87_SECRETKEYBYTES;
    }
}

+ (NSInteger)signatureSizeForLevel:(MLDSASecurityLevel)level {
    switch (level) {
        case MLDSASecurityLevel44:
            return MLDSA44_BYTES;
        case MLDSASecurityLevel65:
            return MLDSA65_BYTES;
        case MLDSASecurityLevel87:
            return MLDSA87_BYTES;
    }
}

+ (nullable MLDSAKeyPair *)generateKeyPairWithLevel:(MLDSASecurityLevel)level
                                              error:(NSError **)error {
    NSInteger pkSize = [self publicKeySizeForLevel:level];
    NSInteger skSize = [self secretKeySizeForLevel:level];

    uint8_t *pk = malloc(pkSize);
    uint8_t *sk = malloc(skSize);

    if (!pk || !sk) {
        free(pk);
        free(sk);
        if (error) {
            *error = [NSError errorWithDomain:MLDSAErrorDomain
                                         code:MLDSAErrorOutOfMemory
                                     userInfo:@{NSLocalizedDescriptionKey: @"Failed to allocate memory"}];
        }
        return nil;
    }

    int ret;
    switch (level) {
        case MLDSASecurityLevel44:
            ret = MLDSA44_keypair(pk, sk);
            break;
        case MLDSASecurityLevel65:
            ret = MLDSA65_keypair(pk, sk);
            break;
        case MLDSASecurityLevel87:
            ret = MLDSA87_keypair(pk, sk);
            break;
    }

    if (ret != 0) {
        free(pk);
        free(sk);
        if (error) {
            NSString *desc = [NSString stringWithFormat:@"Key generation failed with code %d", ret];
            *error = [NSError errorWithDomain:MLDSAErrorDomain
                                         code:ret
                                     userInfo:@{NSLocalizedDescriptionKey: desc}];
        }
        return nil;
    }

    NSData *publicKey = [NSData dataWithBytes:pk length:pkSize];
    NSData *secretKey = [NSData dataWithBytes:sk length:skSize];

    // Clear sensitive data
    memset(sk, 0, skSize);
    free(pk);
    free(sk);

    return [[MLDSAKeyPair alloc] initWithPublicKey:publicKey
                                         secretKey:secretKey
                                     securityLevel:level];
}


+ (nullable MLDSAKeyPair *)generateKeyPairWithLevel:(MLDSASecurityLevel)level
                                               seed: (NSData*) seed
                                              error:(NSError **)error {
    NSInteger pkSize = [self publicKeySizeForLevel:level];
    NSInteger skSize = [self secretKeySizeForLevel:level];

    uint8_t *pk = malloc(pkSize);
    uint8_t *sk = malloc(skSize);

    if (!pk || !sk) {
        free(pk);
        free(sk);
        if (error) {
            *error = [NSError errorWithDomain:MLDSAErrorDomain
                                         code:MLDSAErrorOutOfMemory
                                     userInfo:@{NSLocalizedDescriptionKey: @"Failed to allocate memory"}];
        }
        return nil;
    }

    int ret;
    switch (level) {
        case MLDSASecurityLevel44:
            ret = MLDSA44_keypair_internal(pk, sk, [seed bytes]);
            break;
        case MLDSASecurityLevel65:
            ret = MLDSA65_keypair_internal(pk, sk, [seed bytes]);
            break;
        case MLDSASecurityLevel87:
            ret = MLDSA87_keypair_internal(pk, sk, [seed bytes]);
            break;
    }

    if (ret != 0) {
        free(pk);
        free(sk);
        if (error) {
            NSString *desc = [NSString stringWithFormat:@"Key generation failed with code %d", ret];
            *error = [NSError errorWithDomain:MLDSAErrorDomain
                                         code:ret
                                     userInfo:@{NSLocalizedDescriptionKey: desc}];
        }
        return nil;
    }

    NSData *publicKey = [NSData dataWithBytes:pk length:pkSize];
    NSData *secretKey = [NSData dataWithBytes:sk length:skSize];

    // Clear sensitive data
    memset(sk, 0, skSize);
    free(pk);
    free(sk);

    return [[MLDSAKeyPair alloc] initWithPublicKey:publicKey
                                         secretKey:secretKey
                                     securityLevel:level];
}

+ (nullable NSData *)signMessage:(NSData *)message
                   withSecretKey:(NSData *)secretKey
                         context:(nullable NSData *)context
                           level:(MLDSASecurityLevel)level
                           error:(NSError **)error {
    if (!message || !secretKey) {
        if (error) {
            *error = [NSError errorWithDomain:MLDSAErrorDomain
                                         code:MLDSAErrorInvalidParameter
                                     userInfo:@{NSLocalizedDescriptionKey: @"Message and secret key are required"}];
        }
        return nil;
    }

    NSInteger sigSize = [self signatureSizeForLevel:level];
    NSInteger skSize = [self secretKeySizeForLevel:level];

    if (secretKey.length != skSize) {
        if (error) {
            *error = [NSError errorWithDomain:MLDSAErrorDomain
                                         code:MLDSAErrorInvalidParameter
                                     userInfo:@{NSLocalizedDescriptionKey: @"Invalid secret key size"}];
        }
        return nil;
    }

    uint8_t *sig = malloc(sigSize);
    if (!sig) {
        if (error) {
            *error = [NSError errorWithDomain:MLDSAErrorDomain
                                         code:MLDSAErrorOutOfMemory
                                     userInfo:@{NSLocalizedDescriptionKey: @"Failed to allocate memory"}];
        }
        return nil;
    }

    size_t siglen;
    const uint8_t *ctx = context ? context.bytes : NULL;
    size_t ctxlen = context ? context.length : 0;

    int ret;
    switch (level) {
        case MLDSASecurityLevel44:
            ret = MLDSA44_signature(sig, &siglen, message.bytes, message.length,
                                    ctx, ctxlen, secretKey.bytes);
            break;
        case MLDSASecurityLevel65:
            ret = MLDSA65_signature(sig, &siglen, message.bytes, message.length,
                                    ctx, ctxlen, secretKey.bytes);
            break;
        case MLDSASecurityLevel87:
            ret = MLDSA87_signature(sig, &siglen, message.bytes, message.length,
                                    ctx, ctxlen, secretKey.bytes);
            break;
    }

    if (ret != 0) {
        free(sig);
        if (error) {
            NSString *desc = [NSString stringWithFormat:@"Signing failed with code %d", ret];
            *error = [NSError errorWithDomain:MLDSAErrorDomain
                                         code:ret
                                     userInfo:@{NSLocalizedDescriptionKey: desc}];
        }
        return nil;
    }

    NSData *signature = [NSData dataWithBytes:sig length:siglen];
    free(sig);

    return signature;
}

+ (BOOL)verifySignature:(NSData *)signature
             forMessage:(NSData *)message
          withPublicKey:(NSData *)publicKey
                context:(nullable NSData *)context
                  level:(MLDSASecurityLevel)level
                  error:(NSError **)error {
    if (!signature || !message || !publicKey) {
        if (error) {
            *error = [NSError errorWithDomain:MLDSAErrorDomain
                                         code:MLDSAErrorInvalidParameter
                                     userInfo:@{NSLocalizedDescriptionKey: @"Signature, message, and public key are required"}];
        }
        return NO;
    }

    NSInteger pkSize = [self publicKeySizeForLevel:level];

    if (publicKey.length != pkSize) {
        if (error) {
            *error = [NSError errorWithDomain:MLDSAErrorDomain
                                         code:MLDSAErrorInvalidParameter
                                     userInfo:@{NSLocalizedDescriptionKey: @"Invalid public key size"}];
        }
        return NO;
    }

    const uint8_t *ctx = context ? context.bytes : NULL;
    size_t ctxlen = context ? context.length : 0;

    int ret;
    switch (level) {
        case MLDSASecurityLevel44:
            ret = MLDSA44_verify(signature.bytes, signature.length,
                                 message.bytes, message.length,
                                 ctx, ctxlen, publicKey.bytes);
            break;
        case MLDSASecurityLevel65:
            ret = MLDSA65_verify(signature.bytes, signature.length,
                                 message.bytes, message.length,
                                 ctx, ctxlen, publicKey.bytes);
            break;
        case MLDSASecurityLevel87:
            ret = MLDSA87_verify(signature.bytes, signature.length,
                                 message.bytes, message.length,
                                 ctx, ctxlen, publicKey.bytes);
            break;
    }

    if (ret != 0) {
        if (error) {
            NSString *desc = [NSString stringWithFormat:@"Verification failed with code %d", ret];
            *error = [NSError errorWithDomain:MLDSAErrorDomain
                                         code:ret
                                     userInfo:@{NSLocalizedDescriptionKey: desc}];
        }
        return NO;
    }

    return YES;
}

@end
