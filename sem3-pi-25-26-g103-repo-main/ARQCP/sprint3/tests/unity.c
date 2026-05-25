#include "unity.h"
#include <string.h>
#include <stdarg.h>

static int tests_run = 0;
static int tests_failed = 0;
static int current_test_failed = 0;
static char current_fail_msg[512] = {0};

void UnityBegin(const char* name) {
    (void)name;
    tests_run = tests_failed = 0;
    current_test_failed = 0;
    current_fail_msg[0] = '\0';
}

int UnityEnd(void) {
    printf("[UNITY] Tests run: %d, Passed: %d, Failed: %d\n", tests_run, tests_run - tests_failed, tests_failed);
    return tests_failed == 0 ? 0 : 1;
}

void UnityFail(const char* file, int line, const char* fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    
    vsnprintf(current_fail_msg, sizeof(current_fail_msg), fmt, ap);
    va_end(ap);
    current_test_failed = 1;
    (void)file;
    (void)line;
}

void UnityDefaultTestRun(void (*TestFunc)(void), const char* name, int line) {
    (void)line;
    tests_run++;
    current_test_failed = 0;
    current_fail_msg[0] = '\0';
    
    TestFunc();
    
    if (current_test_failed) {
        tests_failed++;
        printf("[UNITY] Running %s... FAIL %s\n", name, current_fail_msg);
    } else {
        printf("[UNITY] Running %s... PASS\n", name);
    }
}
