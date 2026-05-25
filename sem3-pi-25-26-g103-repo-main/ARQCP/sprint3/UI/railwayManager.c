#include "funcs.h"
#include "config.h"
#include "Board/board.h"
#include "manager/manager.h"
#include "Sensor/expo.h"
#include <stdio.h>
#include <stdbool.h>

void railwayManagerMenu(Config *config, Manager *m) {
    int option = -1;

    while (option != 0) {

        printf("\n--- ACTIONS -------------------------------\n");
        printf("  1 - Assign Train\n");
        printf("  2 - Set Track Nonoperational\n");
        printf("  3 - Set Track Free\n");
        printf("  4 - Give Order of Departure\n");
        printf("  0 - Exit\n");
        printf("-------------------------------------------\n");

        bool validInput = false;
        while (!validInput) {
            printf("> ");

            if (scanf("%d", &option) != 1) {
                printf("Invalid input! Please enter a number.\n");

                while (getchar() != '\n');

                continue;
            }

            if (option < 0 || option > 4) {
                printf("Option must be between 0 and 4.\n");
                continue;
            }

            validInput = true;
        }

        switch (option) {

        case 1:
            assignTrain(config, m);
            break;

        case 2:
            setTrackNonoperational(config, m);
            break;

        case 3:
            setTrackFree(config, m);
            break;

        case 4:
            giveOrderOfDeparture(config, m);
            break;

        case 0:
           return;

        default:
            printf("Unknown option.\n");
            break;
        }
        
    }
}
