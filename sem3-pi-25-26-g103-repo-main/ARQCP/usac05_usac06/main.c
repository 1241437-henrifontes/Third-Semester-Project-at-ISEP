#include <stdio.h>
#include <string.h>
#include "asm.h"

int main(void) {
    int passed = 0;

    // Buffer inicial
    int buffer[5];
    int length = 5;
    int nelem, head, tail;

    printf("=== Testes enqueueValue ===\n");

    // Caso 1: Inserção normal
    memcpy(buffer, (int[]){0,0,0,0,0}, sizeof(buffer));
    nelem = 0; head = 0; tail = 0;
    int ret = enqueueValue(buffer, length, &nelem, &tail, &head, 10);
    printf("Caso 1: %s\n", (ret == 1 && buffer[0] == 10 ? "PASS" : "FAIL"));
    if (ret == 1 && buffer[0] == 10) passed++;

    // Caso 2: Inserção com head cheio (deve chamar dequeue)
    memcpy(buffer, (int[]){10,20,30,40,50}, sizeof(buffer));
    nelem = 5; head = 0; tail = 0;
    ret = enqueueValue(buffer, length, &nelem, &tail, &head, 60);
    printf("Caso 2: %s\n", (ret == 1 && buffer[0] == 60 ? "PASS" : "FAIL"));
    if (ret == 1 && buffer[0] == 60) passed++;

    // Caso 3: length = 0 (falha)
    memcpy(buffer, (int[]){0,0,0,0,0}, sizeof(buffer));
    nelem = 0; head = 0; tail = 0;
    ret = enqueueValue(buffer, 0, &nelem, &tail, &head, 10);
    printf("Caso 3: %s\n", (ret == 0 ? "PASS" : "FAIL"));
    if (ret == 0) passed++;

    // Caso 4: head >= length (falha)
    memcpy(buffer, (int[]){0,0,0,0,0}, sizeof(buffer));
    nelem = 0; head = 5; tail = 0;
    ret = enqueueValue(buffer, length, &nelem, &tail, &head, 10);
    printf("Caso 4: %s\n", (ret == 0 ? "PASS" : "FAIL"));
    if (ret == 0) passed++;

    // Caso 5: tail >= length (falha)
    memcpy(buffer, (int[]){0,0,0,0,0}, sizeof(buffer));
    nelem = 0; head = 0; tail = 5;
    ret = enqueueValue(buffer, length, &nelem, &tail, &head, 10);
    printf("Caso 5: %s\n", (ret == 0 ? "PASS" : "FAIL"));
    if (ret == 0) passed++;

    printf("\n=== Testes dequeueValue ===\n");

    int value;

    // Caso 6: Remoção normal
    memcpy(buffer, (int[]){10,20,30,40,50}, sizeof(buffer));
    nelem = 5; tail = 0; head = 4;
    ret = dequeueValue(buffer, length, &nelem, &tail, &head, &value);
    printf("Caso 6: %s\n", (ret == 1 && value == 10 ? "PASS" : "FAIL"));
    if (ret == 1 && value == 10) passed++;

    // Caso 7: Fila vazia (falha)
    memcpy(buffer, (int[]){0,0,0,0,0}, sizeof(buffer));
    nelem = 0; tail = 0; head = 0;
    ret = dequeueValue(buffer, length, &nelem, &tail, &head, &value);
    printf("Caso 7: %s\n", (ret == 0 ? "PASS" : "FAIL"));
    if (ret == 0) passed++;

    // Caso 8: tail >= nelem (falha)
    memcpy(buffer, (int[]){10,20,30,40,50}, sizeof(buffer));
    nelem = 3; tail = 3; head = 4;
    ret = dequeueValue(buffer, length, &nelem, &tail, &head, &value);
    printf("Caso 8: %s\n", (ret == 0 ? "PASS" : "FAIL"));
    if (ret == 0) passed++;

    // Caso 9: Remoção com atualização de head
    memcpy(buffer, (int[]){10,20,30,40,50}, sizeof(buffer));
    nelem = 5; tail = 0; head = 4;
    ret = dequeueValue(buffer, length, &nelem, &tail, &head, &value);
    printf("Caso 9: %s\n", (ret == 1 && head == 3 ? "PASS" : "FAIL"));
    if (ret == 1 && head == 3) passed++;

    // Caso 10: Remoção do último elemento
    memcpy(buffer, (int[]){10,0,0,0,0}, sizeof(buffer));
    nelem = 1; tail = 0; head = 0;
    ret = dequeueValue(buffer, length, &nelem, &tail, &head, &value);
    printf("Caso 10: %s\n", (ret == 1 && nelem == 0 ? "PASS" : "FAIL"));
    if (ret == 1 && nelem == 0) passed++;

    printf("\nTotal testes passados: %d/10\n", passed);

    return 0;
}