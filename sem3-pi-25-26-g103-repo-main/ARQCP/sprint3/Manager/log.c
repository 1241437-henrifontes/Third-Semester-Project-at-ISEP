#include "log.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

static int ensure_capacity(LogStore* ls, size_t minCap) {
    if (ls->capacity >= minCap) return 1;
    size_t newCap = ls->capacity ? ls->capacity * 2 : 64;
    while (newCap < minCap) newCap *= 2;
    LogEntry* p = (LogEntry*)realloc(ls->entries, newCap * sizeof(LogEntry));
    if (!p) return 0;
    ls->entries = p;
    ls->capacity = newCap;
    return 1;
}

const char* action_to_string(ActionType act) {
    switch (act) {
        case ACT_ASSIGN_TRACK:   return "ASSIGN_TRACK";
        case ACT_EMERGENCY_STOP: return "EMERGENCY_STOP";
        case ACT_SET_NONOP:      return "SET_NONOP";
        case ACT_SET_FREE:       return "SET_FREE";
        case ACT_DEPARTURE_ORDER:return "DEPARTURE_ORDER";
        case ACT_SENSOR_READ:    return "SENSOR_READ";
        case ACT_LIGHT_COMMAND:  return "LIGHT_COMMAND";
        case ACT_BOARD_UPDATE:   return "BOARD_UPDATE";
        case ACT_LOGIN:          return "LOGIN";
        case ACT_LOG_EXPORT:     return "LOG_EXPORT";
        default:                 return "UNKNOWN";
    }
}

static void epoch_to_iso(int64_t epoch, char* buf, size_t len) {
    time_t t = (time_t)epoch;
    struct tm tmv;
    gmtime_r(&t, &tmv);
    strftime(buf, len, "%Y-%m-%d %H:%M:%S", &tmv);
}

int log_init(LogStore* ls, int startId) {
    if (!ls) return 0;
    ls->entries = NULL;
    ls->count = 0;
    ls->capacity = 0;
    ls->nextId = startId;
    return 1;
}

void log_free(LogStore* ls) {
    if (!ls) return;
    free(ls->entries);
    ls->entries = NULL;
    ls->count = ls->capacity = 0;
}

int log_append(LogStore* ls, const char* username, ActionType act, int64_t ts_epoch, int trackId, int trainId, const char* cmd) {
    if (!ls || !username) return 0;
    if (!ensure_capacity(ls, ls->count + 1)) return 0;
    LogEntry* e = &ls->entries[ls->count++];
    e->id = ls->nextId++;
    strncpy(e->username, username, sizeof(e->username)-1);
    e->username[sizeof(e->username)-1] = '\0';
    e->action = act;
    e->timestamp_epoch = ts_epoch;
    e->trackId = trackId;
    e->trainId = trainId;
    if (cmd) {
        strncpy(e->cmd, cmd, sizeof(e->cmd)-1);
        e->cmd[sizeof(e->cmd)-1] = '\0';
    } else {
        e->cmd[0] = '\0';
    }
    return 1;
}

int log_belongs_to_user_asm(const LogEntry* e, const char* username);

static int log_belongs_to_user_c(const LogEntry* e, const char* username) {
    return e && username && strcmp(e->username, username) == 0;
}

static int cmp_ts_asc(const void* a, const void* b) {
    const LogEntry* ea = (const LogEntry*)a;
    const LogEntry* eb = (const LogEntry*)b;
    if (ea->timestamp_epoch < eb->timestamp_epoch) return -1;
    if (ea->timestamp_epoch > eb->timestamp_epoch) return  1;
    return (ea->id - eb->id);
}

static int cmp_ts_desc(const void* a, const void* b) {
    return -cmp_ts_asc(a,b);
}

int log_export_user_txt(const LogStore* ls, const char* username, const char* path, int sortAsc) {
    if (!ls || !username || !path) return 0;

    size_t n = 0;
    for (size_t i = 0; i < ls->count; ++i) {
        const LogEntry* e = &ls->entries[i];
        int belongs = log_belongs_to_user_asm(e, username);
        if (belongs != 0 && belongs != 1) belongs = log_belongs_to_user_c(e, username);
        if (belongs) ++n;
    }

    LogEntry* tmp = NULL;
    if (n > 0) {
        tmp = (LogEntry*)malloc(n * sizeof(LogEntry));
        if (!tmp) return 0;
        size_t k = 0;
        for (size_t i = 0; i < ls->count; ++i) {
            const LogEntry* e = &ls->entries[i];
            int belongs = log_belongs_to_user_asm(e, username);
            if (belongs != 0 && belongs != 1) belongs = log_belongs_to_user_c(e, username);
            if (belongs) tmp[k++] = *e;
        }
        qsort(tmp, n, sizeof(LogEntry), sortAsc ? cmp_ts_asc : cmp_ts_desc);
    }

    FILE* f = fopen(path, "w");
    if (!f) { free(tmp); return 0; }

    time_t now = time(NULL);
    char gen[32];
    epoch_to_iso((int64_t)now, gen, sizeof(gen));
    fprintf(f, "# StationMngt — User Actions Export\n");
    fprintf(f, "# User: %s\n", username);
    fprintf(f, "# GeneratedAt: %s\n", gen);
    fprintf(f, "# Count: %zu\n\n", n);

    for (size_t i = 0; i < n; ++i) {
        char iso[32];
        epoch_to_iso(tmp[i].timestamp_epoch, iso, sizeof(iso));
        fprintf(f, "%s | logId=%d | action=%s", iso, tmp[i].id, action_to_string(tmp[i].action));
        if (tmp[i].trackId) fprintf(f, " | trackId=%d", tmp[i].trackId);
        if (tmp[i].trainId) fprintf(f, " | trainId=%d", tmp[i].trainId);
        if (tmp[i].cmd[0])  fprintf(f, " | cmd=%s", tmp[i].cmd);
        fputc('\n', f);
    }

    fclose(f);
    free(tmp);
    return 1;
}
