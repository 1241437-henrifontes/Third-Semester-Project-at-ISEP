#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include "unity.h"
#include "lightsigns.h"


static char test_output_file[] = "tmp_port.txt";

void test_send_cmd_to_lightsigns(void) {
    
    remove(test_output_file);
    
    int fd = open(test_output_file, O_WRONLY | O_CREAT | O_TRUNC, 0644);
    TEST_ASSERT(fd >= 0);
    const char* cmd = "HELLO";
    write(fd, cmd, strlen(cmd));
    write(fd, "\n", 1);
    close(fd);
    
    
    FILE* f = fopen(test_output_file, "r");
    TEST_ASSERT(f != NULL);
    char buf[128];
    memset(buf, 0, sizeof(buf));
    size_t n = fread(buf, 1, sizeof(buf)-1, f);
    buf[n] = '\0';
    fclose(f);
    TEST_ASSERT(strstr(buf, "HELLO") != NULL);
    remove(test_output_file);
}

int main(void) {
    UNITY_BEGIN();
    RUN_TEST(test_send_cmd_to_lightsigns);
    return UNITY_END();
}
