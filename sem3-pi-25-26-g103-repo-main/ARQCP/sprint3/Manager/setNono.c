#include <stdio.h>
#include <stdbool.h>
#include <string.h>

#include "config.h"
#include "Manager/manager.h"
#include "Board/board.h"

void setTrackNonoperational(Config *config, Manager *m){
    int trackId;
    bool valid = false;

    printf("Enter Track ID to set as NONOPERATIONAL:\n");

    for (int i = 0; i < config->trackCount; i++) {
        printf("Track ID: %d\n", config->tracks[i].id);
    }

    while (!valid) {
        printf("> ");

        if (scanf("%d", &trackId) != 1) {
            while (getchar() != '\n');
        } else {
            for (int i = 0; i < config->trackCount; i++) {
                if (config->tracks[i].id == trackId) {
                    valid = true;
                }
            }

            if (!valid) {
                printf("Invalid Track ID.\n");
            }
        }
    }

     if (strcmp(config->tracks[trackId - 1].state, "EMERGENCY") != 0) {
        strcpy(config->tracks[trackId - 1].state, "EMERGENCY");
        config->tracks[trackId - 1].trainId = 0;
        update_track_light(&config->tracks[trackId - 1], m);
        manager_on_action(m, ACT_SET_NONOP, trackId, 0, NULL);
        board_clear_screen();
        board_draw_art();
        board_draw_tracks(config->tracks, config->trackCount);
    } else {
        printf("Track %d is already NONOPERATIONAL.\n", trackId);
    }
}