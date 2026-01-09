//
//  include.h
//  include
//
//  Objective-C wrapper for lib-native
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger, MLDSASecurityLevel) {
    MLDSASecurityLevel44 = 44,
    MLDSASecurityLevel65 = 65,
    MLDSASecurityLevel87 = 87
};

typedef NS_ENUM(NSInteger, MLDSAError) {
    MLDSAErrorNone = 0,
    MLDSAErrorFail = -1,
    MLDSAErrorOutOfMemory = -2,
    MLDSAErrorRNGFail = -3,
    MLDSAErrorInvalidParameter = -100
};

@interface MLDSAKeyPair : NSObject

@property (nonatomic, readonly) NSData *publicKey;
@property (nonatomic, readonly) NSData *secretKey;
@property (nonatomic, readonly) MLDSASecurityLevel securityLevel;

- (instancetype)initWithPublicKey:(NSData *)publicKey
                        secretKey:(NSData *)secretKey
                    securityLevel:(MLDSASecurityLevel)securityLevel;

@end

@interface MLDSA : NSObject

/**
 * Get the public key size in bytes for a given security level
 */
+ (NSInteger)publicKeySizeForLevel:(MLDSASecurityLevel)level;

/**
 * Get the secret key size in bytes for a given security level
 */
+ (NSInteger)secretKeySizeForLevel:(MLDSASecurityLevel)level;

/**
 * Get the signature size in bytes for a given security level
 */
+ (NSInteger)signatureSizeForLevel:(MLDSASecurityLevel)level;

/**
 * Generate a new keypair for the specified security level
 *
 * @param level The security level (44, 65, or 87)
 * @param error Output parameter for error information
 * @return A new keypair, or nil if an error occurred
 */
+ (nullable MLDSAKeyPair *)generateKeyPairWithLevel:(MLDSASecurityLevel)level
                                              error:(NSError **)error;


+ (nullable MLDSAKeyPair *)generateKeyPairWithLevel:(MLDSASecurityLevel)level
                                               seed: (NSData*) seed
                                              error:(NSError **)error;

/**
 * Sign a message with a secret key
 *
 * @param message The message to sign
 * @param secretKey The secret key
 * @param context Optional context string (can be nil)
 * @param level The security level
 * @param error Output parameter for error information
 * @return The signature, or nil if an error occurred
 */
+ (nullable NSData *)signMessage:(NSData *)message
                   withSecretKey:(NSData *)secretKey
                         context:(nullable NSData *)context
                           level:(MLDSASecurityLevel)level
                           error:(NSError **)error;

/**
 * Verify a signature
 *
 * @param signature The signature to verify
 * @param message The original message
 * @param publicKey The public key
 * @param context Optional context string (can be nil)
 * @param level The security level
 * @param error Output parameter for error information
 * @return YES if the signature is valid, NO otherwise
 */
+ (BOOL)verifySignature:(NSData *)signature
             forMessage:(NSData *)message
          withPublicKey:(NSData *)publicKey
                context:(nullable NSData *)context
                  level:(MLDSASecurityLevel)level
                  error:(NSError **)error;

@end

NS_ASSUME_NONNULL_END
