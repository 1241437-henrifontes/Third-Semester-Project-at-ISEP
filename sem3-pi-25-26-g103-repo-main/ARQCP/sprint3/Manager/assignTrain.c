#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <unistd.h>

#include "config.h"
#include "Manager/manager.h"
#include "Board/board.h"


void assignTrain(Config *config, Manager *m) {
    int availableTrains[config->trainCount];
    int availableTrainCount = 0;

    printf("Available Trains:\n");

    for (int i = 0; i < config->trainCount; i++) {
        bool assigned = false;

        for (int j = 0; j < config->trackCount; j++) {
            if (config->tracks[j].trainId == config->trains[i].id) {
                assigned = true;
            }
        }

        if (!assigned) {
            availableTrains[availableTrainCount] = i;
            availableTrainCount++;
            printf("%d - Train %d\n", availableTrainCount, config->trains[i].id);
        }
    }

    if (availableTrainCount == 0) {
        printf("No free trains available.\n");
        return;
    }

    int choice;
    bool valid = false;

    while (!valid) {
        printf("> ");

        if (scanf("%d", &choice) != 1) {
            while (getchar() != '\n');
        } else if (choice >= 1 && choice <= availableTrainCount) {
            valid = true;
        } else {
            printf("Invalid option.\n");
        }
    }

    int trainIndex = availableTrains[choice - 1];
    int trainId = config->trains[trainIndex].id;

    int *availableTracks = malloc(config->trackCount * sizeof(int));
    int availableTrackCount = 0;

    for (int i = 0; i < config->trackCount; i++) {
        if (strcmp(config->tracks[i].state, "FREE") == 0) {
            availableTracks[availableTrackCount] = i;
            availableTrackCount++;
        }
    }

    if (availableTrackCount == 0) {
        free(availableTracks);
        board_clear_screen();
        board_emergency_stop();
        manager_on_action(m, ACT_EMERGENCY_STOP, 0, 0, NULL);
        return;
    }

    printf("Enter Track ID to assign Train %d:\n", trainId);
    for (int i = 0; i < availableTrackCount; i++) {
        printf("Track %d\n", config->tracks[availableTracks[i]].id);
    }

    int trackId;
    valid = false;

    while (!valid) {
        printf("> ");

        if (scanf("%d", &trackId) != 1) {
            while (getchar() != '\n');
        } else {
            bool found = false;
            int i = 0;

            while (i < availableTrackCount) {
                if (config->tracks[availableTracks[i]].id == trackId) {
                    strcpy(config->tracks[availableTracks[i]].state, "ASSIGNED");
                    config->tracks[availableTracks[i]].trainId = trainId;
                    update_track_light(&config->tracks[availableTracks[i]], m);
                    manager_on_action(m, ACT_ASSIGN_TRACK, trackId, trainId, NULL);
                    board_clear_screen();
                    board_draw_tracks(config->tracks, config->trackCount);
                    train_is_coming();
                    found = true;
                    valid = true;

                    sleep(5);

                    strcpy(config->tracks[availableTracks[i]].state, "BUSY");
                    update_track_light(&config->tracks[availableTracks[i]], m);
                    board_clear_screen();
                    board_draw_art();
                    board_draw_tracks(config->tracks, config->trackCount);
                    train_has_arrived();

                    sleep(2);
                }
                i++;
            }

            if (!found) {
                printf("Invalid track selection.\n");
            }
        }
    }

    free(availableTracks);
}
