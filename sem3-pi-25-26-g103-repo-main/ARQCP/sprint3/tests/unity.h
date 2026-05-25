#ifndef UNITY_H
#define UNITY_H

#include <stdio.h>
#include <string.h>
#include <stdarg.h>

void UnityBegin(const char* name);
int  UnityEnd(void);
void UnityDefaultTestRun(void (*TestFunc)(void), const char* name, int line);
void UnityFail(const char* file, int line, const char* fmt, ...);

#define UNITY_BEGIN() UnityBegin(__FILE__)
#define UNITY_END()   UnityEnd()
#define RUN_TEST(f)   UnityDefaultTestRun(f, #f, __LINE__)

#define TEST_ASSERT_EQUAL_INT(exp, act) \
    do { if ((exp)!=(act)) { UnityFail(__FILE__, __LINE__, "Expected %d but %d", (int)(exp), (int)(act)); return; } } while(0)

#define TEST_ASSERT_EQUAL_STRING(exp, act) \
    do { if (strcmp((exp),(act))!=0) { UnityFail(__FILE__, __LINE__, "Expected '%s' but '%s'", (exp), (act)); return; } } while(0)

#define TEST_ASSERT_EQUAL_CHAR(exp, act) \
    do { if ((exp)!=(act)) { UnityFail(__FILE__, __LINE__, "Expected char %d but %d", (int)(exp), (int)(act)); return; } } while(0)

#define TEST_ASSERT(x) \
    do { if (!(x)) { UnityFail(__FILE__, __LINE__, "Assertion failed: %s", #x); return; } } while(0)

#endif
