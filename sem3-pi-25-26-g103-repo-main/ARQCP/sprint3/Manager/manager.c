#include <string.h>
#include <stdio.h>
#include <time.h>
#include "manager.h"
#include "asm.h"
#include "LightSigns/lightsigns.h"
#include "Board/board.h"
#include "config.h"
#include "log.h"
#include "Sensor/expo.h"



int manager_init(Manager* m, const Config* cfg) {
    m->cfg = *cfg;
    if (!log_init(&m->logs, cfg->logStartId)) return 0;
    m->currentUser[0] = '\0';
    return 1;
}

void manager_on_action(Manager* m, ActionType act, int trackId, int trainId, const char* cmd) {
    int64_t now = (int64_t) time (NULL);
    log_append(&m->logs, m->currentUser[0] ? m->currentUser : "unknown", act, now, trackId, trainId, cmd);
}

void update_track_light(Track *track, Manager *m) {
    if (strcmp(track->state, "FREE") == 0) {
        send_light_command("GE", track->id, m);
    }
    else if (strcmp(track->state, "BUSY") == 0) {
        send_light_command("RE", track->id, m);
    }
    else if (strcmp(track->state, "ASSIGNED") == 0) {
        send_light_command("YE", track->id, m);
    }
    else if (strcmp(track->state, "EMERGENCY") == 0) {
        send_light_command("RB", track->id, m);
    }
    else {
        printf("Unknown state '%s' for track %d\n", track->state, track->id);
    }
}

void send_light_command(const char *cmd, int trackId, Manager *m) {
    char formattedCmd[20];

    if (!format_command((char *)cmd, trackId, formattedCmd)) {
        printf("Failed to format command for track %d\n", trackId);
        return;
    }

    send_cmd_to_lightsigns(formattedCmd);

    manager_on_action(m, ACT_LIGHT_COMMAND, trackId, 0, cmd);
}

void update_all_track_lights(Config *config, Manager *m) {
    for (int i = 0; i < config->trackCount; i++) {
        update_track_light(&config->tracks[i], m);
    }
}

void update_board(Config *config, Manager *m) {
    board_clear_screen();
    board_draw_art();
    board_draw_tracks(config->tracks, config->trackCount);
    board_draw_sensors(config, m);
    board_draw_footer();

    manager_on_action(m, ACT_BOARD_UPDATE, 0, 0, NULL);
}
