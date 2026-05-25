#ifndef CONFIG_H
#define CONFIG_H

#include "log.h"

typedef struct {
    char name[50];
    char username[50];
    char password[50];
    int key;
} User;

typedef struct {
    int id;
    char state[20];
    int trainId;
} Track;

typedef struct {
    int id;
} Train;

typedef struct {
    User* users;
    int userCount;

    Track* tracks;
    int trackCount;

    Train* trains;
    int trainCount;

    int tempBufferLength;
    int tempMedianWindow;
    int humBufferLength;
    int humMedianWindow;

    int logStartId;
} Config;

typedef struct {
    int *data;
    int length;
    int nelem;
    int tail;
    int head;
} CircularInt;

typedef struct {
    Config   cfg;
    LogStore logs;
    char     currentUser[50];
} Manager;

#endif
