#ifndef SERIAL_H
#define SERIAL_H

int serial_write_line(int fd, const char* s);
int serial_open(const char* device);
void serial_configure(int fd);
int serial_read_line(int fd, char* buffer, int maxLen);
void serial_close(int fd);

#endif
