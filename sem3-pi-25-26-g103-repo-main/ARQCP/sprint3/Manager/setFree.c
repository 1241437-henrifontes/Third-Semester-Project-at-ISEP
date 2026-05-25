#include <stdio.h>
#include <string.h>
#include <stdbool.h>

#include "config.h"
#include "Manager/manager.h"
#include "Board/board.h"

void setTrackFree(Config *config, Manager *m){
    int trackId;
    bool valid = false;

    printf("Enter Track ID to set as FREE:\n");

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

     if (strcmp(config->tracks[trackId - 1].state, "EMERGENCY") == 0) {
        strcpy(config->tracks[trackId - 1].state, "FREE");
        config->tracks[trackId - 1].trainId = 0;        
        update_track_light(&config->tracks[trackId - 1], m);
        manager_on_action(m, ACT_SET_FREE, trackId, 0, NULL);
        board_clear_screen();
        board_draw_art();
        board_draw_tracks(config->tracks, config->trackCount);
    } else if (strcmp(config->tracks[trackId - 1].state, "FREE") == 0) {
        printf("Track %d is already FREE.\n", trackId);
    }else {
        printf("Track %d must be in NONOPERATIONAL state to use this function to set FREE.\n", trackId);
    }
}