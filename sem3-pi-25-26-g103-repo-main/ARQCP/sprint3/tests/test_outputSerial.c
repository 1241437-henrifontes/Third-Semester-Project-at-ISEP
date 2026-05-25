#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "unity.h"
#include "Sensor/config.h"

int outputSerial(const Config* cfg);

void test_outputSerial_basic(void) {
    
    const char* dev = "tmp_serial_device.txt";
    FILE* f = fopen(dev, "w");
    TEST_ASSERT(f != NULL);
    for (int i = 0; i < 10; ++i) {
        fprintf(f, "TEMP: %d C\n", 20 + i);
        fprintf(f, "HUM: %d %%\n", 40 + i);
    }
    fclose(f);

    
    extern char test_serial_device[256];
    strncpy(test_serial_device, dev, 255);
    test_serial_device[255] = '\0';

    Config cfg = {0};
    cfg.humBufferLength = 3;
    cfg.humMedianWindow = 3;
    cfg.tempBufferLength = 3;
    cfg.tempMedianWindow = 3;

    int r = outputSerial(&cfg);
    TEST_ASSERT_EQUAL_INT(0, r);
    remove(dev);
}

int main(void) {
    UNITY_BEGIN();
    RUN_TEST(test_outputSerial_basic);
    return UNITY_END();
}
