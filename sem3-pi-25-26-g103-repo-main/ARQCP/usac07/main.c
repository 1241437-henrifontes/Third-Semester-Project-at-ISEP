#include <stdio.h>
#include "ac07.h"

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

    printf("==== TESTES move_n_to_array (USAC07) ====\n\n");

    // CASE 1: Remover 3 elementos
    {
        int buffer[5] = {10, 20, 30, 40, 50};
        int length = 5;
        int nelem = 5;
        int tail = 0;
        int head = 5;
        int array[3];
        int expected[3] = {10, 20, 30};

        ret = move_n_to_array(buffer, length, &nelem, &tail, &head, 3, array);

        printf("Case 1: Remove 3 elements\n");
        printf("Output:    "); print_array(array, 3); printf("\n");
        printf("Expected:  "); print_array(expected, 3); printf("\n");
        printf("Return:    %d\n", ret);

        if (ret == 1 && arrays_equal(array, expected, 3)) {
            printf("Result:    PASS\n\n");
            passed++;
        } else {
            printf("Result:    FAIL\n\n");
        }
    }

    // CASE 2: Remover mais do que existe
    {
        int buffer[4] = {1, 2, 3, 4};
        int length = 4;
        int nelem = 2;
        int tail = 0;
        int head = 2;
        int array[3];

        ret = move_n_to_array(buffer, length, &nelem, &tail, &head, 3, array);

        printf("Case 2: Remove more than available\n");
        printf("Return:    %d\n", ret);
        printf("Expected:  0\n");

        if (ret == 0) {
            printf("Result:    PASS\n\n");
            passed++;
        } else {
            printf("Result:    FAIL\n\n");
        }
    }


    printf("\nTests passed: %d / 2\n", passed);

    return 0;
}
