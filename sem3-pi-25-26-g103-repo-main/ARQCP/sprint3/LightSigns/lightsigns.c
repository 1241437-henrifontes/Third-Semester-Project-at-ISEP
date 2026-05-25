#include <stdio.h>
#include <fcntl.h>
#include <unistd.h>
#include <string.h>
#include "lightsigns.h"

#ifndef SERIAL_PORT
#define SERIAL_PORT "/dev/ttyS0"
#endif

void send_cmd_to_lightsigns(const char *cmd) {
    int fd = open(SERIAL_PORT, O_WRONLY | O_NOCTTY);
    if (fd < 0) {
        perror("Error opening serial port");
        return;
    }

    write(fd, cmd, strlen(cmd));
    write(fd, "\n", 1);
    close(fd);
}
