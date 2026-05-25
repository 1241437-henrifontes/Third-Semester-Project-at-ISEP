#include <string.h>
#include <stdio.h>
#include <ctype.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include "Sensor/config.h"
#include "Manager/log.h"


int encrypt_data(char* password, int key, char* out) {
    (void)key;
    
    char tmp[256];
    size_t len = strlen(password);
    if (len >= sizeof(tmp)) len = sizeof(tmp) - 1;
    memcpy(tmp, password, len);
    tmp[len] = '\0';
    memcpy(out, tmp, len + 1);
    return 1;
}


int log_belongs_to_user_asm(const LogEntry* e, const char* username) {
    if (!e || !username) return 0;
    return strcmp(e->username, username) == 0;
}


int format_command(char* op, int n, char *cmd) {
    
    return sprintf(cmd, "%s:%d", op, n) > 0;
}


char last_cmd_sent[256] = {0};
#ifndef USE_REAL_LIGHTSIGNS
void send_cmd_to_lightsigns(const char *cmd) {
    if (!cmd) return;
    strncpy(last_cmd_sent, cmd, sizeof(last_cmd_sent)-1);
    last_cmd_sent[sizeof(last_cmd_sent)-1] = '\0';
    FILE* f = fopen("tmp_port.txt", "a");
    if (f) {
        fprintf(f, "%s\n", cmd);
        fclose(f);
    }
}
#endif


int decrypt_data(char* password, int key, char* out) {
    (void)key;
    
    size_t len = strlen(password);
    memmove(out, password, len + 1);
    return 1;
}


int extract_data(char* str, char* token, char* unit, int* value) {
    if (!str || !token || !unit || !value) return 0;
    char *p = strstr(str, token);
    if (!p) return 0;
    p += strlen(token);
    
    while (*p && (*p < '0' || *p > '9') && *p != '-' ) p++;
    if (!*p) return 0;
    int v = atoi(p);
    *value = v;
    
    char *q = p;
    while (*q && (*q == '-' || (*q >= '0' && *q <= '9') || *q == ' ')) q++;
    size_t i = 0;
    while (*q && !isspace((unsigned char)*q) && i + 1 < 32) unit[i++] = *q++;
    unit[i] = '\0';
    return 1;
}


int cb_push_asm(CircularInt *cb, int value) {
    if (!cb || !cb->data) return -1;
    if (cb->nelem < cb->length) {
        cb->data[cb->head++] = value;
        cb->head %= cb->length;
        cb->nelem++;
        return 0;
    }
    
    cb->data[cb->head++] = value;
    cb->head %= cb->length;
    if (cb->nelem < cb->length) cb->nelem++;
    return 0;
}


int median(int* vec, int length, int *me) {
    if (!vec || length <= 0 || !me) return -1;
    int *tmp = malloc(length * sizeof(int));
    if (!tmp) return -1;
    memcpy(tmp, vec, length * sizeof(int));
    for (int i = 0; i < length - 1; ++i) for (int j = i + 1; j < length; ++j) if (tmp[j] < tmp[i]) { int t = tmp[i]; tmp[i] = tmp[j]; tmp[j] = t; }
    *me = tmp[length/2];
    free(tmp);
    return 0;
}


char test_serial_device[256] = {0};
int serial_open(const char* device) {
    (void)device;
    const char* path = test_serial_device[0] ? test_serial_device : "tmp_serial_device.txt";
    int fd = open(path, O_RDONLY);
    if (fd < 0) return -1;
    return fd;
}

void serial_configure(int fd) { (void)fd; }

int serial_read_line(int fd, char* buffer, int maxLen) {
    if (!buffer || maxLen <= 0) return 0;
    FILE* f = fdopen(dup(fd), "r");
    if (!f) return 0;
    if (!fgets(buffer, maxLen, f)) { fclose(f); return 0; }
    size_t len = strlen(buffer);
    if (len && buffer[len-1] == '\n') buffer[len-1] = '\0';
    fclose(f);
    return (int)strlen(buffer);
}

int serial_write_line(int fd, const char* s) {
    if (fd < 0 || !s) return -1;
    
    int r = write(fd, s, strlen(s));
    write(fd, "\n", 1);
    return r;
}

void serial_close(int fd) { close(fd); }


unsigned int sleep(unsigned int seconds) { (void)seconds; return 0; }
int usleep(useconds_t usec) { (void)usec; return 0; }
