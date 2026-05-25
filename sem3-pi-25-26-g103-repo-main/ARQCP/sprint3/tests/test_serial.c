#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include "UI/serial.h"
#include "unity.h"

void test_serial_write_line(void) {
    const char* fname = "tmp_serial.txt";
    int fd = open(fname, O_CREAT | O_WRONLY | O_TRUNC, 0666);
    TEST_ASSERT(fd >= 0);
    int ret = serial_write_line(fd, "data\n");
    (void)ret;
    close(fd);
    
    FILE* f = fopen(fname, "r");
    TEST_ASSERT(f != NULL);
    char buf[64];
    fgets(buf, sizeof(buf), f);
    fclose(f);
    TEST_ASSERT_EQUAL_STRING("data\n", buf);
    remove(fname);
}

int main(void) {
    UNITY_BEGIN();
    RUN_TEST(test_serial_write_line);
    return UNITY_END();
}
