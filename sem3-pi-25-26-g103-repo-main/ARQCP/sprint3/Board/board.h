#ifndef BOARD_H
#define BOARD_H

#include "config.h"

void board_draw_art(void);
void board_draw_art_invert();
void board_draw_tracks(Track *tracks, int count);
void board_draw_sensors(Config *config, Manager *manager);
void board_draw_footer(void);
void board_clear_screen(void);
void board_emergency_stop();
void train_is_coming();
void train_has_arrived();
void train_is_leaving();
void track_is_available();

#endif
