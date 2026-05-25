#include <stdio.h>
#include <string.h>
#include <dirent.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include "unity.h"
#include "Manager/manager.h"

int export_user_actions(Manager* m);

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

void test_export_user_actions_creates_file(void) {
    
    Manager m; memset(&m,0,sizeof m);
    log_init(&m.logs, 1);
    log_append(&m.logs, "jdoe", ACT_LOGIN, 1609459200LL, 1, 0, "cmd");

    int saved = redirect_stdin_with("jdoe\n\n\n");
    TEST_ASSERT(saved >= 0);

    
    system("rm -rf exports");

    int ok = export_user_actions(&m);
    TEST_ASSERT_EQUAL_INT(1, ok);

    
    int found = 0;
    DIR* d = opendir("exports");
    TEST_ASSERT(d != NULL);
    struct dirent* de;
    while ((de = readdir(d)) != NULL) {
        if (strstr(de->d_name, "user_jdoe_") == de->d_name) { found = 1; break; }
    }
    closedir(d);
    TEST_ASSERT(found == 1);

    
    system("rm -rf exports");
    restore_stdin(saved);
}

int main(void) {
    UNITY_BEGIN();
    RUN_TEST(test_export_user_actions_creates_file);
    return UNITY_END();
}
