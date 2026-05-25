#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <stdlib.h>
#include "unity.h"
#include "Sensor/config.h"
#include "Manager/manager.h"

void setTrackNonoperational(Config *config, Manager *m);

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

void test_setTrackNonoperational_changes_state(void) {
    const char* input = "1\n";
    int saved = redirect_stdin_with(input);
    TEST_ASSERT(saved >= 0);

    Config cfg = {0};
    cfg.trackCount = 1;
    cfg.tracks = malloc(sizeof(Track));
    cfg.tracks[0].id = 1;
    strcpy(cfg.tracks[0].state, "FREE");
    cfg.tracks[0].trainId = 0;

    Manager m; memset(&m,0,sizeof m);

    setTrackNonoperational(&cfg, &m);

    TEST_ASSERT_EQUAL_STRING("EMERGENCY", cfg.tracks[0].state);
    TEST_ASSERT_EQUAL_INT(0, cfg.tracks[0].trainId);

    free(cfg.tracks);
    restore_stdin(saved);
}

int main(void) {
    UNITY_BEGIN();
    RUN_TEST(test_setTrackNonoperational_changes_state);
    return UNITY_END();
}
