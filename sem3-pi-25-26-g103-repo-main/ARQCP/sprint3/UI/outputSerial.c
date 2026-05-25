#include "serial.h"
#include "asm.h"
#include "config.h"
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/select.h>
#include <string.h>
#include <strings.h>

static int read_with_timeout(int fd, char* buf, int maxLen, int timeout_ms) {
    fd_set rfds; FD_ZERO(&rfds); FD_SET(fd, &rfds);
    struct timeval tv; tv.tv_sec = timeout_ms / 1000; tv.tv_usec = (timeout_ms % 1000) * 1000;
    int r = select(fd + 1, &rfds, NULL, NULL, &tv);
    if (r <= 0) return 0;
    return serial_read_line(fd, buf, maxLen);
}

static int collect_one_sample(int serial_fd, int *humValue, char humUnit[32], int *tempValue, char tempUnit[32]) {
    if (serial_write_line(serial_fd, "GTH") != 0) {
        return -1;
    }

    int haveHum = 0, haveTemp = 0;
    char line[256];

    for (int i = 0; i < 4; ++i) {
        int n = read_with_timeout(serial_fd, line, sizeof(line), 2500);
        if (n <= 0) {
            fprintf(stderr, "Reading timeout.\n");
            continue;
        }

        int okT = extract_data(line, "TEMP", tempUnit, tempValue);
        int okH = extract_data(line, "HUM",  humUnit,  humValue);

        if (okT == 1) haveTemp = 1;
        if (okH == 1) haveHum = 1;

        if (haveTemp && haveHum) break;
    }

    if (!haveTemp && !haveHum) {
        fprintf(stderr, "No value was extracted.\n");
        return -3;
    }

    return 0;
}

static int cb_init(CircularInt *cb, int length) {
    cb->data = (int*)calloc(length, sizeof(int));
    if (!cb->data) return -1;
    cb->length = length;
    cb->nelem  = 0;
    cb->tail   = 0;
    cb->head   = 0;
    return 0;
}

static void cb_free(CircularInt *cb) {
    free(cb->data);
    cb->data = NULL;
    cb->length = cb->nelem = cb->tail = cb->head = 0;
}

static int cb_copy_last(const CircularInt *cb, int win, int *out) {
    int n = cb->nelem;
    if (n == 0) return 0;
    if (win > n) win = n;

    int start = (cb->head - win + cb->length) % cb->length;
    for (int i = 0; i < win; ++i) {
        int idx = (start + i) % cb->length;
        out[i] = cb->data[idx];
    }
    return win;
}

int outputSerial(const Config* cfg) {
    int humCap  = (cfg && cfg->humBufferLength   > 0) ? cfg->humBufferLength   : 11;
    int humWin  = (cfg && cfg->humMedianWindow   > 0) ? cfg->humMedianWindow   : 11;
    int tempCap = (cfg && cfg->tempBufferLength  > 0) ? cfg->tempBufferLength  : 11;
    int tempWin = (cfg && cfg->tempMedianWindow  > 0) ? cfg->tempMedianWindow  : 11;

    if (humWin  > humCap)  humWin  = humCap;
    if (tempWin > tempCap) tempWin = tempCap;

    int samplesPerPress = (humWin > tempWin) ? humWin : tempWin;

    int serial_fd = serial_open("/dev/ttyS0");
    if (serial_fd < 0) return 1;
    serial_configure(serial_fd);

    CircularInt humCB = {0}, tempCB = {0};
    if (cb_init(&humCB, humCap) != 0 || cb_init(&tempCB, tempCap) != 0) {
        fprintf(stderr, "Error allocating buffers.\n");
        if (humCB.data)  cb_free(&humCB);
        if (tempCB.data) cb_free(&tempCB);
        serial_close(serial_fd);
        return 2;
    }

    char humUnitLast[32] = {0};
    char tempUnitLast[32] = {0};

    for (int s = 0; s < samplesPerPress; ++s) {
        int hv = 0, tv = 0;
        char hu[32] = {0}, tu[32] = {0};

        int rc = collect_one_sample(serial_fd, &hv, hu, &tv, tu);
        if (rc == 0) {
            if (hu[0]) {
                cb_push_asm(&humCB, hv);
                strncpy(humUnitLast, hu, sizeof(humUnitLast) - 1);
            } else {
                fprintf(stderr, "Humidity unit not received in this sample.\n");
            }

            if (tu[0]) {
                cb_push_asm(&tempCB, tv);
                strncpy(tempUnitLast, tu, sizeof(tempUnitLast) - 1);
            } else {
                fprintf(stderr, "Temperature unit not received in this sample.\n");
            }
        } else {
            fprintf(stderr, "Error collecting sample (code: %d)\n", rc);
        }

        usleep(1100 * 1000);
    }

    int *H = (int*)malloc(sizeof(int) * humWin);
    int *T = (int*)malloc(sizeof(int) * tempWin);

    int nH = cb_copy_last(&humCB, humWin, H);
    int nT = cb_copy_last(&tempCB, tempWin, T);

    int humMed = 0, tempMed = 0;
    if (nH > 0) median(H, nH, &humMed);
    if (nT > 0) median(T, nT, &tempMed);

    free(H);
    free(T);

    const char* humU  = humUnitLast[0]  ? humUnitLast  : "percentage";
    const char* tempU = tempUnitLast[0] ? tempUnitLast : "celsius";

    printf("Extracted: Humidity = %d %s || Temperature = %d %s\n", humMed, humU, tempMed, tempU);

    cb_free(&humCB);
    cb_free(&tempCB);
    serial_close(serial_fd);
    return 0;
}
