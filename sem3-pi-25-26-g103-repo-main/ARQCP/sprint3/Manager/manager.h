#ifndef MANAGER_H
#define MANAGER_H

#include "config.h"
#include "log.h"


int manager_init(Manager* m, const Config* cfg);

void manager_on_action(Manager* m, ActionType act, int trackId, int trainId, const char* cmd);

void send_light_command(const char *cmd, int trackId, Manager *m);

void update_track_light(Track *track, Manager *m);

void update_all_track_lights(Config *config, Manager *m);

void update_board(Config *config, Manager *m);

void setTrackNonoperational(Config *config, Manager *m);

void setTrackFree(Config *config, Manager *m);

void assignTrain(Config *config, Manager *m);

void giveOrderOfDeparture(Config *config, Manager *m);

#endif