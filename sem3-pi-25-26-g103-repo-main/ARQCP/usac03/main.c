#include <stdio.h>
#include "asm1.h"

int main() {
    char str[] = "TEMP&unit:celsius&value:32#HUM&unit:percentage&value:80";
    char unit[20];
    int value;

    char token1[] = "TEMP"; //First token
    int res = extract_data(str, token1, unit, &value);
    printf("%d:%s,%d\n", res, unit, value); // 1:celsius,32

    char token2[] = "HUM"; //Token found in the second part of the string
    res = extract_data(str, token2, unit, &value);
    printf("%d:%s,%d\n", res, unit, value); // 1:percentage,80

    char token3[] = "WIND"; //Non-existent token
    res = extract_data(str, token3, unit, &value);
    printf("%d:%s,%d\n", res, unit, value); // 0: ,0

    char token4[] = " "; //Empty token
    res = extract_data(str, token4, unit, &value);
    printf("%d:%s,%d\n", res, unit, value); // 0: ,0

    char token5[] = "temp"; //Lowercase token
    res = extract_data(str, token5, unit, &value);
    printf("%d:%s,%d\n", res, unit, value); // 0: ,0

    char token6[] = "EMP"; //Incomplete token
    res = extract_data(str, token6, unit, &value);;
    printf("%d:%s,%d\n", res, unit, value); // 0: ,0

    return 0;
}
