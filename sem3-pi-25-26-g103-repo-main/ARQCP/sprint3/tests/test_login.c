#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include "unity.h"
#include "Sensor/config.h"
#include "Manager/manager.h"

int login(Config* config, Manager* m);

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

void test_login_success(void) {
    const char* input = "jdoe\nsecret\n";
    int saved = redirect_stdin_with(input);
    TEST_ASSERT(saved >= 0);

    Config cfg = {0};
    cfg.users = malloc(sizeof(User));
    cfg.userCount = 1;
    strcpy(cfg.users[0].username, "jdoe");
    strcpy(cfg.users[0].password, "secret");
    cfg.users[0].key = 1;

    Manager m;
    memset(&m,0,sizeof m);

    int res = login(&cfg, &m);
    TEST_ASSERT_EQUAL_INT(1, res);
    TEST_ASSERT_EQUAL_STRING("jdoe", m.currentUser);

    free(cfg.users);
    restore_stdin(saved);
}

void test_login_wrong_password(void) {
    const char* input = "jdoe\nwrongpwd\n";
    int saved = redirect_stdin_with(input);
    TEST_ASSERT(saved >= 0);

    Config cfg = {0};
    cfg.users = malloc(sizeof(User));
    cfg.userCount = 1;
    strcpy(cfg.users[0].username, "jdoe");
    strcpy(cfg.users[0].password, "secret");
    cfg.users[0].key = 1;

    Manager m; memset(&m,0,sizeof m);
    int res = login(&cfg, &m);
    TEST_ASSERT_EQUAL_INT(0, res);

    free(cfg.users);
    restore_stdin(saved);
}

int main(void) {
    UNITY_BEGIN();
    RUN_TEST(test_login_success);
    RUN_TEST(test_login_wrong_password);
    return UNITY_END();
}
