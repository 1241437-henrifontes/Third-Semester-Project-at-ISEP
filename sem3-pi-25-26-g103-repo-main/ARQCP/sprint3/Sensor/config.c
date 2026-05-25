#include "config.h"
#include "asm.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

void loadSetupFile(const char* filename, Config* config) {

    FILE* f = fopen(filename, "r");
    if (!f) {
        printf("Erro: não foi possível abrir o ficheiro %s\n", filename);
        exit(1);
    }

    char line[256];
    char key[100];
    char value[100];

    config->userCount = 0;
    config->trackCount = 0;
    config->trainCount = 0;

    config->users = malloc(10 * sizeof(User));
    config->tracks = malloc(20 * sizeof(Track));
    config->trains = malloc(20 * sizeof(Train));

    User tempUser;
    Track tempTrack;

    while (fgets(line, sizeof(line), f)) {

        line[strcspn(line, "\n")] = 0;

        if (strlen(line) == 0)
            continue;

        char *sep = strchr(line, ':');
        if (!sep) continue;

        *sep = '\0';         
        strcpy(key, line);

        char *val = sep + 1; 
        strcpy(value, val);   

        char *start = value;
        while (*start == ' ' || *start == '\t' || *start == '\r') start++;

        char *end = start + strlen(start) - 1;
        while (end >= start && (*end == ' ' || *end == '\t' || *end == '\r')) {
            *end = '\0';
            end--;
        }

        strcpy(value, start);

        if (strcmp(key, "USER_NAME") == 0) {
            strcpy(tempUser.name, value);
        }
        else if (strcmp(key, "USER_USERNAME") == 0) {
            strcpy(tempUser.username, value);
        }
        else if (strcmp(key, "USER_PASSWORD") == 0) {
            strcpy(tempUser.password, value);
        }
        else if (strcmp(key, "USER_KEY") == 0) {
            tempUser.key = atoi(value);

            int ret = encrypt_data(tempUser.password, tempUser.key, tempUser.password);

            if (ret == 0) {
                printf("Erro: chave inválida para o utilizador %s\n", tempUser.username);
                exit(1);
            }

            config->users[config->userCount++] = tempUser;
        }

        else if (strcmp(key, "TEMP_BUFFER_LENGTH") == 0) {
            config->tempBufferLength = atoi(value);
        }
        else if (strcmp(key, "TEMP_MEDIAN_WINDOW") == 0) {
            config->tempMedianWindow = atoi(value);
        }
        else if (strcmp(key, "HUM_BUFFER_LENGTH") == 0) {
            config->humBufferLength = atoi(value);
        }
        else if (strcmp(key, "HUM_MEDIAN_WINDOW") == 0) {
            config->humMedianWindow = atoi(value);
        }

        else if (strcmp(key, "TRACK_ID") == 0) {
            tempTrack.id = atoi(value);
        }
        else if (strcmp(key, "TRACK_STATE") == 0) {
            strcpy(tempTrack.state, value);
        }
        else if (strcmp(key, "TRACK_TRAIN") == 0) {
            if (strlen(value) == 0)
                tempTrack.trainId = 0;  
            else
                tempTrack.trainId = atoi(value);

            config->tracks[config->trackCount++] = tempTrack;
        }
        else if (strcmp(key, "TRAIN_ID") == 0) {
            config->trains[config->trainCount++].id = atoi(value);
        }

        else if (strcmp(key, "LOG_START_ID") == 0) {
            config->logStartId = atoi(value);
        }
    }

    fclose(f);
}
