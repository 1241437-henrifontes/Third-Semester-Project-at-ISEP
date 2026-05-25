#include <stdio.h>
#include <string.h>
#include "asm.h"

int main(void) {
    char in[100];
    char out[100];
    int key;
    int ret;
    int passed = 0;

    // Case 1: Normal encryption
    snprintf(in, sizeof(in), "HELLO");
    key = 3;
    ret = encryptData(in, key, out);
    printf("Case 1: Input=%s Key=%d Output=%s Result=%s\n",
           in, key, out, (ret == 1 ? "PASS" : "FAIL"));
    if (ret == 1) passed++;

    // Case 2: Invalid key (too large)
    snprintf(in, sizeof(in), "WORLD");
    key = 30;
    ret = encryptData(in, key, out);
    printf("Case 2: Input=%s Key=%d Output=%s Result=%s\n",
           in, key, out, (ret == 0 ? "PASS" : "FAIL"));
    if (ret == 0) passed++;

    // Case 3: Empty string
    snprintf(in, sizeof(in), "");
    key = 5;
    ret = encryptData(in, key, out);
    printf("Case 3: Input=\"%s\" Key=%d Output=%s Result=%s\n",
           in, key, out, (ret == 0 ? "PASS" : "FAIL"));
    if (ret == 0) passed++;

    // Case 4: Lowercase letters
    snprintf(in, sizeof(in), "hello");
    key = 2;
    ret = encryptData(in, key, out);
    printf("Case 4: Input=%s Key=%d Output=%s Result=%s\n",
           in, key, out, (ret == 1 ? "PASS" : "FAIL"));
    if (ret == 1) passed++;

    // Case 5: Mixed case (should fail if only letters allowed)
    snprintf(in, sizeof(in), "Hello123");
    key = 4;
    ret = encryptData(in, key, out);
    printf("Case 5: Input=%s Key=%d Output=%s Result=%s\n",
           in, key, out, (ret == 0 ? "PASS" : "FAIL"));
    if (ret == 0) passed++;

    // Summary
    printf("\nTests passed: %d/5\n", passed);

    return 0;
}