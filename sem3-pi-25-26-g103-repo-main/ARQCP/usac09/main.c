#include <stdio.h>
#include <string.h>
#include "asm.h"

void print_vec(int *v, int n) {
    printf("[");
    for (int i = 0; i < n; i++) {
        printf("%d", v[i]);
        if (i < n - 1) printf(", ");
    }
    printf("]\n");
}

int main(void) {
    int passed = 0;
    int me;

    printf("=== Testes median (com vetores DESORGANIZADOS) ===\n");

    // Caso 1: Vetor normal (misturado)
    int vec1[5] = {60, 10, 15, 20, 33};
    int ret = median(vec1, 5, &me);
    printf("Caso 1: %s (Resultado=%d)\n", (ret == 1 && me == 20 ? "PASS" : "FAIL"), me);
    printf("Vetor ordenado: ");
    print_vec(vec1, 5);
    if (ret == 1 && me == 30) passed++;

    // Caso 2: Vetor com um elemento (não muda)
    int vec2[1] = {100};
    ret = median(vec2, 1, &me);
    printf("Caso 2: %s (Resultado=%d)\n", (ret == 1 && me == 100 ? "PASS" : "FAIL"), me);
    printf("Vetor ordenado: ");
    print_vec(vec2, 1);
    if (ret == 1 && me == 100) passed++;

    // Caso 3: length = 0 (falha)
    int vec3[3] = {30, 10, 20};
    ret = median(vec3, 0, &me);
    printf("Caso 3: %s\n", (ret == 0 ? "PASS" : "FAIL"));
    printf("Vetor ficou (não deve ser ordenado): ");
    print_vec(vec3, 3);
    if (ret == 0) passed++;

    // Caso 4: Vetor com números negativos (misturado)
    int vec4[4] = {-22, -7, -45, -34};
    ret = median(vec4, 4, &me);
    printf("Caso 4: %s (Resultado=%d)\n", (ret == 1 && me == -28 ? "PASS" : "FAIL"), me);
    printf("Vetor ordenado: ");
    print_vec(vec4, 4);
    if (ret == 1 && me == -25) passed++;

    // Caso 5: Vetor com valores grandes (misturado)
    int vec5[3] = {3000, 1000, 2000};
    ret = median(vec5, 3, &me);
    printf("Caso 5: %s (Resultado=%d)\n", (ret == 1 && me == 2000 ? "PASS" : "FAIL"), me);
    printf("Vetor ordenado: ");
    print_vec(vec5, 3);
    if (ret == 1 && me == 2000) passed++;

    printf("\nTotal testes passados: %d/5\n", passed);

    return 0;
}
