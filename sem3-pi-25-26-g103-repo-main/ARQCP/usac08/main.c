#include <stdio.h>
#include <string.h>
#include "ac08.h"

void print_array(int* arr, int length) {
    printf("[");
    for (int i = 0; i < length; i++) {
        printf("%d", arr[i]);
        if (i < length - 1) printf(", ");
    }
    printf("]");
}

int arrays_equal(int* a, int* b, int length) {
    for (int i = 0; i < length; i++) {
        if (a[i] != b[i]) return 0;
    }
    return 1;
}

int main(void) {
    int passed = 0;
    int ret;

    printf("==== TESTES sort_array (USAC08) ====\n\n");

    // CASo 1: Ordenação crescente normal

    {
        int vec[] = {5, 1, 3, 7, 2};
        int expected[] = {1, 2, 3, 5, 7};
        int length = 5;

        ret = sort_array(vec, length, 1);

        printf("Case 1: Ascending Order\n");
        printf("Input:     [5, 1, 3, 7, 2]\n");
        printf("Output:    "); print_array(vec, length); printf("\n");
        printf("Expected:  "); print_array(expected, length); printf("\n");
        printf("Return:    %d\n", ret);

        if (ret == 1 && arrays_equal(vec, expected, length)) {
            printf("Result:    PASSOU\n\n");
            passed++;
        } else {
            printf("Result:    FAIL\n\n");
        }
    }


    // CASo 2: Ordenação decrescente

    {
        int vec[] = {4, 9, 2, 1, 8};
        int expected[] = {9, 8, 4, 2, 1};
        int length = 5;

        ret = sort_array(vec, length, 0);

        printf("Case 2: Descending Order\n");
        printf("Input:     [4, 9, 2, 1, 8]\n");
        printf("Output:    "); print_array(vec, length); printf("\n");
        printf("Expected:  "); print_array(expected, length); printf("\n");
        printf("Return:    %d\n", ret);

        if (ret == 1 && arrays_equal(vec, expected, length)) {
            printf("Result:    PASSOU\n\n");
            passed++;
        } else {
            printf("Result:    FAIL\n\n");
        }
    }


    // CASo 3: length = 0 (deve retornar 0)

    {
        int vec[] = {10, 5, 2};
        int length = 0;

        ret = sort_array(vec, length, 1);

        printf("Case 3: Length = 0\n");
        printf("Return:    %d\n", ret);
        printf("Expected:  0\n");

        if (ret == 0) {
            printf("Result:    PASSOU\n\n");
            passed++;
        } else {
            printf("Result:    FAIL\n\n");
        }
    }


    // CASo 4: length negativo (também deve retornar 0)

    {
        int vec[] = {3, 1, 2};
        int length = -5;

        ret = sort_array(vec, length, 1);

        printf("Case 4: Negative Length\n");
        printf("Return:    %d\n", ret);
        printf("Expected:  0\n");

        if (ret == 0) {
            printf("Result:    PASSOU\n\n");
            passed++;
        } else {
            printf("Result:    FAIL\n\n");
        }
    }


    // CASO 5: array já ordenado (ascending)

    {
        int vec[] = {1, 2, 3, 4};
        int expected[] = {1, 2, 3, 4};
        int length = 4;

        ret = sort_array(vec, length, 1);

        printf("Case 5: Already Sorted (Ascending)\n");
        printf("Output:    "); print_array(vec, length); printf("\n");
        printf("Expected:  "); print_array(expected, length); printf("\n");

        if (ret == 1 && arrays_equal(vec, expected, length)) {
            printf("Result:    PASSOU\n\n");
            passed++;
        } else {
            printf("Result:    FAIL\n\n");
        }
    }


    printf("\nTests passed: %d / 5\n", passed);

    return 0;
}
