#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "unity.h"
#include "Manager/manager.h"
#include "Sensor/config.h"

void test_manager_init_and_log_append(void) {
    Config cfg = {0};
    cfg.trackCount = 0;
    cfg.logStartId = 10;

    Manager m;
    TEST_ASSERT_EQUAL_INT(1, manager_init(&m, &cfg));
    TEST_ASSERT_EQUAL_INT(0, m.logs.count);
}

void test_update_track_light_calls_send(void) {
    Config cfg = {0};
    cfg.trackCount = 1;
    cfg.tracks = malloc(sizeof(Track));
    cfg.tracks[0].id = 3;
    strcpy(cfg.tracks[0].state, "FREE");
    cfg.tracks[0].trainId = 0;

    Manager m;
    manager_init(&m, &cfg);

    
    remove("tmp_port.txt");

    update_all_track_lights(&cfg, &m);

    
    FILE* f = fopen("tmp_port.txt", "r");
    TEST_ASSERT(f != NULL);
    char buf[128];
    fgets(buf, sizeof(buf), f);
    fclose(f);
    TEST_ASSERT(strstr(buf, "GE:") != NULL || strstr(buf, "GE") != NULL);

    free(cfg.tracks);
}

int main(void) {
    UNITY_BEGIN();
    RUN_TEST(test_manager_init_and_log_append);
    RUN_TEST(test_update_track_light_calls_send);
    return UNITY_END();
}
