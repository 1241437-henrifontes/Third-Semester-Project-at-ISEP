#include "config.h"
#include "manager.h"
#include "funcs.h"
#include <stdio.h>
#include <string.h>

int main(void) {
    Config config;

    loadSetupFile("UI/setupFile.txt", &config);

    Manager manager;
    if (!manager_init(&manager, &config)) {
        printf("Failed to init manager.\n");
        return 1;
    }

    int running = 1;

    while (running) {
        int loginResult = 0;
        int chances = 3;

        while (loginResult == 0 && chances > 0){
            if (chances < 3){
                printf("You have %d chances left.\n", chances);
            } else {
                printf("LOGIN\n");
            }
            loginResult = login(&config, &manager);
            chances--;
        }

        if (loginResult == 2) {
            running = 0;
        } else if (loginResult == 1) {
            for(int i = 0; i < config.trackCount; i++) {
                update_track_light(&config.tracks[i], &manager);
            }

            int menuRet = 0;

           if (strcmp(manager.currentUser, "admin") == 0 ||
                strcmp(manager.currentUser, "Admin") == 0) {
                menuRet = adminMenu(&config, &manager);
            } else {
                menuRet = operatorMenu(&config, &manager);
            }

            if (menuRet == 2) {
                continue;
            } else {
                running = 0;
            }
        } else {
            running = 0;
        }
    }

    return 0;
}
