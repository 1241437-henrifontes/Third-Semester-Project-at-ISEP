#include "serial.h"
#include <fcntl.h>
#include <unistd.h>
#include <stdio.h>
#include <termios.h>
#include <string.h>

int serial_write_line(int fd, const char* s) {
    if (!s) {
        return -1;
    }

    size_t len = strlen(s);

    if (write(fd, s, len) != (size_t) len) {
        return -1;
    }

    if (write(fd, "\n", 1) != 1) {
        return -1;
    }

    return 0;
}

int serial_open(const char* device) {
    int fd = open(device, O_RDWR | O_NOCTTY | O_SYNC);

    if (fd < 0) {
        perror("Error opening serial port");
    }

    return fd;
}

void serial_configure(int fd) {
    struct termios tty;
    memset(&tty, 0, sizeof tty);

    if (tcgetattr(fd, &tty) != 0) {
        perror("Error tcgetattr");
        return;
    }

    cfsetospeed(&tty, B9600);
    cfsetispeed(&tty, B9600);

    tty.c_cflag = (tty.c_cflag & ~CSIZE) | CS8;
    tty.c_cflag |= CLOCAL | CREAD;
    tty.c_cflag &= ~(PARENB | PARODD);
    tty.c_cflag &= ~CSTOPB;
    tty.c_cflag &= ~CRTSCTS;

    tty.c_lflag = 0;
    tty.c_oflag = 0;
    tty.c_iflag = 0;

    tty.c_cc[VMIN]  = 1;
    tty.c_cc[VTIME] = 1;

    tcsetattr(fd, TCSANOW, &tty);
}

int serial_read_line(int fd, char* buffer, int maxLen) {
    char c;
    int i = 0;

    while (i < maxLen - 1) {
        int n = read(fd, &c, 1);
        if (n <= 0)
            continue;

        if (c == '\n')
            break;

        buffer[i++] = c;
    }

    buffer[i] = '\0';
    return i;
}

void serial_close(int fd) {
    close(fd);
}
