#include <stdio.h>
#include <string.h>
#include "config.h"
#include "asm.h"
#include "Manager/manager.h"

int login(Config* config, Manager* m) {
    char inputUsername[50];
    char inputPassword[50];

    printf("Name or Username (0 to Exit): ");
    scanf("%s", inputUsername);
    if (strcmp(inputUsername, "0") == 0) {
        printf("Exiting...\n");
        return 2;
    }
    printf("Password: ");
    scanf("%s", inputPassword);

    int userFound = 0;
    for (int i = 0; i < config->userCount; i++) {
        if (strcmp(config->users[i].username, inputUsername) == 0 || strcmp(config->users[i].name, inputUsername) == 0) {
            userFound = 1;
            char decryptedPassword[50];
            int ret = decrypt_data(config->users[i].password, config->users[i].key, decryptedPassword);

            if (ret == 0) {
                printf("Error on decrypt password.\n");
                manager_on_action(m, ACT_LOGIN, 0, 0, "DECRYPT_ERROR");
                return 0;
            }

            if (strcmp(decryptedPassword, inputPassword) == 0) {
                printf("Successful Login.\n");

                strncpy(m->currentUser, config->users[i].username, sizeof(m->currentUser)-1);
                m->currentUser[sizeof(m->currentUser)-1] = '\0';

                manager_on_action(m, ACT_LOGIN, 0, 0, NULL);

                return 1;
            } else {
                printf("Wrong password for user %s.\n", inputUsername);

                manager_on_action(m, ACT_LOGIN, 0, 0, "FAIL");

                return 0;
            }
        }
    }
    if (userFound == 0) {
            printf("User %s not found.\n", inputUsername);
            manager_on_action(m, ACT_LOGIN, 0, 0, "NOT_FOUND");
    }

    return 0;
}
