#include <stdint.h>
#include <stddef.h>

// Function prototype
void randombytes(uint8_t *out, size_t outlen);

#if defined(__APPLE__)
#include <Security/SecRandom.h>
void randombytes(uint8_t *out, size_t outlen) {
    if (SecRandomCopyBytes(kSecRandomDefault, outlen, out) != 0) {
        // In case of error, zero the buffer
        for (size_t i = 0; i < outlen; i++) {
            out[i] = 0;
        }
    }
}

#elif defined(_WIN32)
#include <windows.h>
#include <bcrypt.h>
void randombytes(uint8_t *out, size_t outlen) {
    BCryptGenRandom(NULL, out, (ULONG)outlen, BCRYPT_USE_SYSTEM_PREFERRED_RNG);
}

#elif defined(__linux__) || defined(__ANDROID__)
#include <unistd.h>
#include <fcntl.h>
void randombytes(uint8_t *out, size_t outlen) {
    int fd = open("/dev/urandom", O_RDONLY);
    if(fd < 0) return;
    read(fd, out, outlen);
    close(fd);
}

#else
#error "Unsupported platform — you must implement randombytes()"
#endif
