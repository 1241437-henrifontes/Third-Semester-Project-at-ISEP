#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <unistd.h>

#include "config.h"
#include "Manager/manager.h"
#include "Board/board.h"

void giveOrderOfDeparture(Config *config, Manager *m){
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

     if (strcmp(config->tracks[trackId - 1].state, "BUSY") == 0) {
        strcpy(config->tracks[trackId - 1].state, "ASSIGNED");
        int trainId = config->tracks[trackId - 1].trainId;
        update_track_light(&config->tracks[trackId - 1], m);
        board_clear_screen();
        board_draw_art_invert();
        board_draw_tracks(config->tracks, config->trackCount);
        train_is_leaving();

        sleep(5);

        strcpy(config->tracks[trackId - 1].state, "FREE");
        config->tracks[trackId - 1].trainId = 0;
        update_track_light(&config->tracks[trackId - 1], m);
        board_clear_screen();
        board_draw_tracks(config->tracks, config->trackCount);
        track_is_available();

        manager_on_action(m, ACT_DEPARTURE_ORDER, trackId, trainId, NULL);

        sleep(2);

    } else if (strcmp(config->tracks[trackId - 1].state, "FREE") == 0) {
        printf("Track %d does not have a train assigned to it.\n", trackId);
    }else {
        printf("Track %d must be in BUSY state to give order of departure.\n", trackId);
    }
}