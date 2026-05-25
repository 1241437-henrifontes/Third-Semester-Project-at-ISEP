#ifndef ASM_H
#define ASM_H

#ifdef __ASSEMBLER__
typedef unsigned int size_t;
#else
#include <stddef.h>
#include "config.h"
#endif

int encrypt_data(char* in, int key, char* out);
int decrypt_data(char* in, int key, char* out);
int extract_data(char* str, char* token, char* unit, int* value);
size_t str_len(const char* s);
char* str_find(char* str, char* sub);
void str_copy_until_delim(char* src, char* dst);
int str_to_int(char* src);
int cb_push_asm(CircularInt *cb, int value);
int median(int* vec, int length, int *me);
int sort_array(int* vec, int length, char order);
int format_command(char* op, int n, char *cmd);

#endif