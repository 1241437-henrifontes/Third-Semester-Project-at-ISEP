#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include "unity.h"


int read_int(const char* prompt, int* out);

static int redirect_stdin_with(const char* content) {
    const char* fname = "tmp_stdin.txt";
    FILE* f = fopen(fname, "w");
    if (!f) return -1;
    fputs(content, f);
    fclose(f);

    int saved = dup(fileno(stdin));
    int fd = open(fname, O_RDONLY);
    if (fd < 0) { close(saved); return -1; }
    if (dup2(fd, fileno(stdin)) == -1) { close(fd); close(saved); return -1; }
    close(fd);
    return saved;
}

static void restore_stdin(int saved) {
    dup2(saved, fileno(stdin));
    close(saved);
    remove("tmp_stdin.txt");
}

void test_read_int_simple(void) {
    int saved = redirect_stdin_with("5\n");
    TEST_ASSERT(saved >= 0);
    int out = 0;
    int r = read_int("> ", &out);
    TEST_ASSERT_EQUAL_INT(1, r);
    TEST_ASSERT_EQUAL_INT(5, out);
    restore_stdin(saved);
}

void test_read_int_skip_blank_and_invalid(void) {
    int saved = redirect_stdin_with("\nabc\n  10xyz\n  10\n");
    TEST_ASSERT(saved >= 0);
    int out = 0;
    int r = read_int("> ", &out);
    TEST_ASSERT_EQUAL_INT(1, r);
    TEST_ASSERT_EQUAL_INT(10, out);
    restore_stdin(saved);
}

int main(void) {
    UNITY_BEGIN();
    RUN_TEST(test_read_int_simple);
    RUN_TEST(test_read_int_skip_blank_and_invalid);
    return UNITY_END();
}
