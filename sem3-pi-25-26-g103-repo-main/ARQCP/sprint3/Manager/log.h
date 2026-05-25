#ifndef LOG_H
#define LOG_H

#include <stdint.h>
#include <stddef.h>

typedef enum {
    ACT_ASSIGN_TRACK,
    ACT_EMERGENCY_STOP,
    ACT_SET_NONOP,
    ACT_SET_FREE,
    ACT_DEPARTURE_ORDER,
    ACT_SENSOR_READ,
    ACT_LIGHT_COMMAND,
    ACT_BOARD_UPDATE,
    ACT_LOGIN,
    ACT_LOG_EXPORT
} ActionType;

typedef struct {
    int         id;
    char        username[50];
    ActionType  action;
    int64_t     timestamp_epoch;
    int         trackId;
    int         trainId;
    char        cmd[16];
} LogEntry;

typedef struct {
    LogEntry* entries;
    size_t    count;
    size_t    capacity;
    int       nextId;
} LogStore;

int  log_init(LogStore* ls, int startId);
void log_free(LogStore* ls);
int  log_append(LogStore* ls, const char* username, ActionType act, int64_t ts_epoch, int trackId, int trainId, const char* cmd);
int  log_export_user_txt(const LogStore* ls, const char* username, const char* path, int sortAsc);
const char* action_to_string(ActionType act);
int log_belongs_to_user_asm(const LogEntry* e, const char* username);

#endif