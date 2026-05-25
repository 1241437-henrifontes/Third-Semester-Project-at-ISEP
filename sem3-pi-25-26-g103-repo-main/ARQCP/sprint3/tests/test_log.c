#include <string.h>
#include <stdio.h>
#include "Manager/log.h"
#include "unity.h"

void test_action_to_string(void) {
    TEST_ASSERT_EQUAL_STRING("ASSIGN_TRACK", action_to_string(ACT_ASSIGN_TRACK));
    TEST_ASSERT_EQUAL_STRING("UNKNOWN", action_to_string(-1));
}

void test_log_init_append_export(void) {
    LogStore ls;
    TEST_ASSERT_EQUAL_INT(1, log_init(&ls, 100));
    TEST_ASSERT_EQUAL_INT(0, ls.count);

    TEST_ASSERT_EQUAL_INT(1, log_append(&ls, "u1", ACT_LOGIN, 1609459200LL, 1, 2, "cmd"));
    TEST_ASSERT_EQUAL_INT(1, log_append(&ls, "u2", ACT_ASSIGN_TRACK, 1609462800LL, 2, 0, ""));
    TEST_ASSERT_EQUAL_INT(2, ls.count);
    TEST_ASSERT_EQUAL_INT(100, ls.entries[0].id);
    TEST_ASSERT_EQUAL_STRING("u1", ls.entries[0].username);

    
    TEST_ASSERT_EQUAL_INT(1, log_export_user_txt(&ls, "u1", "tmp_export_u1.txt", 1));

    
    FILE* f = fopen("tmp_export_u1.txt", "r");
    TEST_ASSERT(f != NULL);
    char buf[256];
    int found = 0;
    while (fgets(buf, sizeof(buf), f)) {
        if (strstr(buf, "ASSIGN_TRACK") || strstr(buf, "LOGIN")) found++;
    }
    fclose(f);
    TEST_ASSERT(found >= 1);

    log_free(&ls);
}

int main(void) {
    UNITY_BEGIN();
    RUN_TEST(test_action_to_string);
    RUN_TEST(test_log_init_append_export);
    return UNITY_END();
}
