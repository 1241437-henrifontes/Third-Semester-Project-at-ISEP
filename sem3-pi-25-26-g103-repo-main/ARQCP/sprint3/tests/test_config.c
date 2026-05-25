#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Sensor/config.h"
#include "funcs.h"
#include "unity.h"

void test_loadSetupFile_basic(void) {
    const char* fname = "tmp_test_setup.txt";
    
    
    FILE* f = fopen(fname, "w");
    TEST_ASSERT(f != NULL);
    fprintf(f, "USER_NAME:John Doe\n");
    fprintf(f, "USER_USERNAME:jdoe\n");
    fprintf(f, "USER_PASSWORD:secret\n");
    fprintf(f, "USER_KEY:1\n");
    fprintf(f, "\n");
    fprintf(f, "USER_NAME:Jane Smith\n");
    fprintf(f, "USER_USERNAME:jsmith\n");
    fprintf(f, "USER_PASSWORD:pass123\n");
    fprintf(f, "USER_KEY:2\n");
    fprintf(f, "\n");
    fprintf(f, "TEMP_BUFFER_LENGTH:5\n");
    fprintf(f, "TEMP_MEDIAN_WINDOW:3\n");
    fprintf(f, "HUM_BUFFER_LENGTH:8\n");
    fprintf(f, "HUM_MEDIAN_WINDOW:4\n");
    fprintf(f, "\n");
    fprintf(f, "TRACK_ID:10\n");
    fprintf(f, "TRACK_STATE:FREE\n");
    fprintf(f, "TRACK_TRAIN:\n");
    fprintf(f, "\n");
    fprintf(f, "TRACK_ID:20\n");
    fprintf(f, "TRACK_STATE:OCCUPIED\n");
    fprintf(f, "TRACK_TRAIN:101\n");
    fprintf(f, "\n");
    fprintf(f, "TRAIN_ID:101\n");
    fprintf(f, "TRAIN_ID:202\n");
    fprintf(f, "TRAIN_ID:303\n");
    fprintf(f, "\n");
    fprintf(f, "LOG_START_ID:42\n");
    fclose(f);

    Config cfg;
    memset(&cfg, 0, sizeof(cfg));
    loadSetupFile(fname, &cfg);

    TEST_ASSERT_EQUAL_INT(2, cfg.userCount);
    TEST_ASSERT_EQUAL_STRING("jdoe", cfg.users[0].username);
    TEST_ASSERT_EQUAL_STRING("John Doe", cfg.users[0].name);
    TEST_ASSERT_EQUAL_STRING("jsmith", cfg.users[1].username);
    TEST_ASSERT_EQUAL_STRING("Jane Smith", cfg.users[1].name);
    TEST_ASSERT_EQUAL_INT(5, cfg.tempBufferLength);
    TEST_ASSERT_EQUAL_INT(3, cfg.tempMedianWindow);
    TEST_ASSERT_EQUAL_INT(8, cfg.humBufferLength);
    TEST_ASSERT_EQUAL_INT(4, cfg.humMedianWindow);
    TEST_ASSERT_EQUAL_INT(2, cfg.trackCount);
    TEST_ASSERT_EQUAL_INT(10, cfg.tracks[0].id);
    TEST_ASSERT_EQUAL_STRING("FREE", cfg.tracks[0].state);
    TEST_ASSERT_EQUAL_INT(20, cfg.tracks[1].id);
    TEST_ASSERT_EQUAL_STRING("OCCUPIED", cfg.tracks[1].state);
    TEST_ASSERT_EQUAL_INT(101, cfg.tracks[1].trainId);
    TEST_ASSERT_EQUAL_INT(3, cfg.trainCount);
    TEST_ASSERT_EQUAL_INT(101, cfg.trains[0].id);
    TEST_ASSERT_EQUAL_INT(202, cfg.trains[1].id);
    TEST_ASSERT_EQUAL_INT(303, cfg.trains[2].id);
    TEST_ASSERT_EQUAL_INT(42, cfg.logStartId);

    
    free(cfg.users);
    free(cfg.tracks);
    free(cfg.trains);
    remove(fname);
}

int main(void) {
    UNITY_BEGIN();
    RUN_TEST(test_loadSetupFile_basic);
    return UNITY_END();
}
