#include <stdio.h>
#include <string.h>
#include "Board/board.h"
#include "unity.h"

void test_board_draw_tracks(void) {
    Track t[2] = {{1, "FREE", 0}, {2, "OCCUPIED", 7}};
    board_draw_tracks(t, 2);
}

int main(void) {
    UNITY_BEGIN();
    RUN_TEST(test_board_draw_tracks);
    return UNITY_END();
}
