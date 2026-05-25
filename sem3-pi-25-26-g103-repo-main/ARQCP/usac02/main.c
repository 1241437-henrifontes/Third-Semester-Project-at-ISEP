#include <stdio.h>
#include <string.h>
#include "asm.h"

int main(void) {
    char in[100];
    char out[100];
    int key;
    int ret;
    int passed = 0;

    // Case 1: Normal decryption
    snprintf(in, sizeof(in), "KHOOR"); // Encrypted form of HELLO with key=3
    key = 3;
    ret = decryptData(in, key, out);
    printf("Case 1: Input=%s Key=%d Output=%s Result=%s\n",
           in, key, out, (ret == 1 && strcmp(out, "HELLO") == 0 ? "PASS" : "FAIL"));
    if (ret == 1 && strcmp(out, "HELLO") == 0) passed++;

    // Case 2: Invalid key (too large)
    snprintf(in, sizeof(in), "WORLD");
    key = 30;
    ret = decryptData(in, key, out);
    printf("Case 2: Input=%s Key=%d Output=%s Result=%s\n",
           in, key, out, (ret == 0 ? "PASS" : "FAIL"));
    if (ret == 0) passed++;

    // Case 3: Empty string
    snprintf(in, sizeof(in), "");
    key = 5;
    ret = decryptData(in, key, out);
    printf("Case 3: Input=\"%s\" Key=%d Output=%s Result=%s\n",
           in, key, out, (ret == 0 ? "PASS" : "FAIL"));
    if (ret == 0) passed++;

    // Case 4: Lowercase letters
    snprintf(in, sizeof(in), "jgnnq"); // Encrypted form of hello with key=2
    key = 2;
    ret = decryptData(in, key, out);
    printf("Case 4: Input=%s Key=%d Output=%s Result=%s\n",
           in, key, out, (ret == 1 && strcmp(out, "hello") == 0 ? "PASS" : "FAIL"));
    if (ret == 1 && strcmp(out, "hello") == 0) passed++;

    // Case 5: Mixed input with digits (expected fail)
    snprintf(in, sizeof(in), "Hello123");
    key = 4;
    ret = decryptData(in, key, out);
    printf("Case 5: Input=%s Key=%d Output=%s Result=%s\n",
           in, key, out, (ret == 0 ? "PASS" : "FAIL"));
    if (ret == 0) passed++;

    // Summary
    printf("\nTests passed: %d/5\n", passed);

    return 0;
}