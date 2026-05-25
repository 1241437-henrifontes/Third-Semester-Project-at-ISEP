#include "funcs.h"
#include "config.h"
#include "Board/board.h"
#include "manager/manager.h"
#include "Sensor/expo.h"
#include <string.h>
#include <ctype.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>

int read_int(const char* prompt, int* out) {
    char line[64];
    int printed = 0;

    for (;;) {
        if (!printed) {
            printf("%s", prompt);
            fflush(stdout);
            printed = 1;
        }

        if (!fgets(line, sizeof line, stdin))
            return 0;

        size_t len = strlen(line);
        if (len > 0 && line[len - 1] == '\n')
            line[len - 1] = '\0';

        char *p = line;
        while (*p && isspace((unsigned char)*p))
            p++;

        if (*p == '\0') {
            continue;
        }

        char *end = NULL;
        long v = strtol(p, &end, 10);

        if (end == p) {
            printf("Invalid input. Try again.\n");
        } else {
            while (*end && isspace((unsigned char)*end))
                end++;

            if (*end == '\0') {
                *out = (int)v;
                return 1;
            } else {
                printf("Extra characters after number. Try again.\n");
            }
        }

        printed = 0;
    }
}

int adminMenu(Config *config, Manager *m) {
    int option = -1;

    while (option != 0) {

        printf("\n--- ACTIONS -------------------------------\n");
        printf("  1 - Update Board\n");
        printf("  2 - Railway Manager\n");
        printf("  3 - Export User Actions\n");
        printf("  0 - Exit\n");
        printf("-------------------------------------------\n");

        if (!read_int("> ", &option)) {
            printf("\nClosed input. Exiting...\n");
            break;
        }

        switch (option) {

        case 1:
            update_board(config, m);
            break;

        case 2:
            railwayManagerMenu(config, m);
            break;

        case 3:
            export_user_actions(m);
            break;

        case 0:
            send_light_command("CD", 1, m);
            send_light_command("CD", 2, m);
            return 2;
        }
    }

    return 0;
}

int operatorMenu(Config *config, Manager *m) {
    int option = -1;

    while (option != 0) {
        printf("\n--- ACTIONS -------------------------------\n");
        printf("  1 - Update Board\n");
        printf("  2 - Railway Manager\n");
        printf("  0 - Exit\n");
        printf("-------------------------------------------\n");

        if (!read_int("> ", &option)) {
            printf("\nClosed input. Exiting...\n");
            break;
        }

        switch (option) {

        case 1:
            update_board(config, m);
            break;

        case 2:
            railwayManagerMenu(config, m);
            break;

        case 0:
            send_light_command("CD", 1, m);
            send_light_command("CD", 2, m);
            return 2;
        }
    }

    return 0;
}
