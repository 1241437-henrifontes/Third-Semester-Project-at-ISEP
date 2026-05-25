#include "expo.h"
#include "Manager/log.h"
#include "Manager/manager.h"
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <sys/stat.h>
#include <errno.h>

static int ensure_exports_dir(void) {
    struct stat st;
    if (stat("exports", &st) == 0) {
    #ifdef _WIN32
        return (st.st_mode & _S_IFDIR) ? 1 : 0;
    #else
        return S_ISDIR(st.st_mode) ? 1 : 0;
    #endif
    }
    #ifdef _WIN32
    if (mkdir("exports") == 0) return 1;
    #else
    if (mkdir("exports", 0755) == 0) return 1;
    #endif
    if (errno == EEXIST) return 1;
    return 0;
}

static int read_line(const char* prompt, char* buf, size_t len) {
    printf("%s", prompt);
    if (!fgets(buf, (int)len, stdin)) return 0;
    buf[strcspn(buf, "\r\n")] = 0;
    return 1;
}

int export_user_actions(Manager* m) {
    if (!m) { printf("Invalid Manager.\n"); return 0; }

    char username[64] = {0};
    char path[256]    = {0};
    char sort[16]     = {0};

    if (!read_line("Username: ", username, sizeof(username)) || username[0] == '\0') {
        printf("Invalid Username.\n");
        return 0;
    }
    if (!read_line("Path (ENTER = default): ", path, sizeof(path))) {
        printf("Invalid input.\n");
        return 0;
    }
    if (!read_line("Order (asc/desc, ENTER=asc): ", sort, sizeof(sort))) {
        printf("Invalid input.\n");
        return 0;
    }

    const int sortAsc = (strcmp(sort, "desc") == 0) ? 0 : 1;

    if (!ensure_exports_dir()) {
        printf("It wasn't possible to guarantee the directory 'exports/'.\n");
        return 0;
    }

    char localBuf[256];
    const char* pathUsed = path[0] ? path : NULL;
    if (!pathUsed) {
        time_t now = time(NULL);
        struct tm tmv; gmtime_r(&now, &tmv);
        char stamp[32]; strftime(stamp, sizeof(stamp), "%Y%m%d_%H%M%S", &tmv);
        snprintf(localBuf, sizeof(localBuf), "./exports/user_%s_%s.txt", username, stamp);
        pathUsed = localBuf;
    }

    int ok = log_export_user_txt(&m->logs, username, pathUsed, sortAsc);

    manager_on_action(m, ACT_LOG_EXPORT, 0, 0, NULL);

    if (ok) printf("Successful export: %s\n", pathUsed);
    else    printf("Fail exporting to: %s\n", pathUsed);
    return ok;
}
