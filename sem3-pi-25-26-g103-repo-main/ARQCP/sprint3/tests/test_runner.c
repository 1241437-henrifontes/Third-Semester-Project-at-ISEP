#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/wait.h>

#define GREEN   "\033[32m"
#define RED     "\033[31m"
#define YELLOW  "\033[33m"
#define CYAN    "\033[36m"
#define MAGENTA "\033[35m"
#define RESET   "\033[0m"
#define BOLD    "\033[1m"
#define DIM     "\033[2m"


static const char* module_names[] = {
    "log.c",
    "config.c",
    "board.c",
    "lightsigns.c",
    "serial.c",
    "manager.c",
    "menu.c",
    "login.c",
    "setFree.c",
    "setNono.c",
    "assignTrain.c",
    "outputSerial.c",
    "orderOfDeparture.c",
    "expo.c",
    "railwayManager.c",
};

static int run_and_capture(const char* cmd, int index, const char* module_name) {
    char buffer[16384];
    buffer[0] = '\0';

    
    char full_cmd[512];
    snprintf(full_cmd, sizeof(full_cmd), "%s 2>&1", cmd);

    FILE* p = popen(full_cmd, "r");
    if (!p) {
        printf(RED "    ✗ FAIL" RESET " (popen error)\n");
        return 1;
    }

    while (fgets(buffer + strlen(buffer), (int)(sizeof(buffer) - strlen(buffer) - 1), p)) {
        
    }

    int status = pclose(p);
    int exitcode = WIFEXITED(status) ? WEXITSTATUS(status) : status;

    
    printf("\n" CYAN "┌──────────────────────────────────────────────────────────────────┐" RESET "\n");
    printf(CYAN "│" RESET BOLD " [%2d] Module: %-52s" RESET CYAN "│" RESET "\n", index, module_name);
    printf(CYAN "├──────────────────────────────────────────────────────────────────┤" RESET "\n");

    
    int test_count = 0;
    int test_pass = 0;
    int test_fail = 0;
    
    char output_copy[16384];
    strncpy(output_copy, buffer, sizeof(output_copy) - 1);
    output_copy[sizeof(output_copy) - 1] = '\0';

    char* line = strtok(output_copy, "\n");
    while (line) {
        
        if (strstr(line, "Running ")) {
            test_count++;
            char* test_name_start = strstr(line, "Running ");
            if (test_name_start) {
                test_name_start += 8; 
                char test_name[128];
                int i = 0;
                while (test_name_start[i] && test_name_start[i] != '.' && test_name_start[i] != ' ' && i < 127) {
                    test_name[i] = test_name_start[i];
                    i++;
                }
                test_name[i] = '\0';
                
                
                if (strstr(line, "PASS")) {
                    test_pass++;
                    printf(CYAN "│" RESET "   " GREEN "✓ PASS" RESET " %-56s" CYAN "│" RESET "\n", test_name);
                } else if (strstr(line, "FAIL")) {
                    test_fail++;
                    printf(CYAN "│" RESET "   " RED "✗ FAIL" RESET " %-56s" CYAN "│" RESET "\n", test_name);
                    
                    
                    char* expected_ptr = strstr(line, "Expected");
                    if (expected_ptr) {
                        
                        char expected_val[128] = {0};
                        char actual_val[128] = {0};
                        char* but_ptr = strstr(expected_ptr, " but ");
                        
                        if (but_ptr) {
                            
                            char* exp_start = expected_ptr + 9; 
                            int exp_len = but_ptr - exp_start;
                            if (exp_len > 0 && exp_len < 127) {
                                strncpy(expected_val, exp_start, exp_len);
                                expected_val[exp_len] = '\0';
                            }
                            
                            
                            char* act_start = but_ptr + 5; 
                            strncpy(actual_val, act_start, 127);
                            
                            int act_len = strlen(actual_val);
                            while (act_len > 0 && (actual_val[act_len-1] == '\n' || actual_val[act_len-1] == ' ')) {
                                actual_val[--act_len] = '\0';
                            }
                        }
                        
                        printf(CYAN "│" RESET RED "         ├─ Esperado: " RESET "%-42s" CYAN "│" RESET "\n", expected_val);
                        printf(CYAN "│" RESET RED "         └─ Obtido:   " RESET "%-42s" CYAN "│" RESET "\n", actual_val);
                    } else {
                        
                        char* assert_ptr = strstr(line, "Assertion failed");
                        if (assert_ptr) {
                            printf(CYAN "│" RESET RED "         └─ " RESET "%-52s" CYAN "│" RESET "\n", assert_ptr);
                        }
                    }
                }
            }
        }
        line = strtok(NULL, "\n");
    }

    
    if (test_count == 0) {
        if (exitcode == 0) {
            printf(CYAN "│" RESET "   " GREEN "✓ PASS" RESET " (no detailed output)                                " CYAN "│" RESET "\n");
            test_pass = 1;
        } else {
            printf(CYAN "│" RESET "   " RED "✗ FAIL" RESET " (test execution failed)                              " CYAN "│" RESET "\n");
            test_fail = 1;
            
            strncpy(output_copy, buffer, sizeof(output_copy) - 1);
            output_copy[sizeof(output_copy) - 1] = '\0';
            char* err_line = strtok(output_copy, "\n");
            while (err_line) {
                if (strstr(err_line, "error") || strstr(err_line, "FAIL") || 
                    strstr(err_line, "Expected") || strstr(err_line, "Assertion")) {
                    char* exp_ptr = strstr(err_line, "Expected");
                    if (exp_ptr) {
                        char* but_ptr = strstr(exp_ptr, " but ");
                        if (but_ptr) {
                            char expected_val[128] = {0};
                            char actual_val[128] = {0};
                            char* exp_start = exp_ptr + 9;
                            int exp_len = but_ptr - exp_start;
                            if (exp_len > 0 && exp_len < 127) {
                                strncpy(expected_val, exp_start, exp_len);
                            }
                            strncpy(actual_val, but_ptr + 5, 127);
                            printf(CYAN "│" RESET RED "         ├─ Esperado: " RESET "%-42s" CYAN "│" RESET "\n", expected_val);
                            printf(CYAN "│" RESET RED "         └─ Obtido:   " RESET "%-42s" CYAN "│" RESET "\n", actual_val);
                        }
                    } else {
                        printf(CYAN "│" RESET YELLOW "         └─ %.52s" RESET, err_line);
                        int len = strlen(err_line);
                        if (len < 52) {
                            for (int j = len; j < 52; j++) printf(" ");
                        }
                        printf(CYAN "│" RESET "\n");
                    }
                    break;
                }
                err_line = strtok(NULL, "\n");
            }
        }
    }

    
    printf(CYAN "├──────────────────────────────────────────────────────────────────┤" RESET "\n");
    if (test_fail == 0) {
        printf(CYAN "│" RESET "   " DIM "Result:" RESET " " GREEN BOLD "ALL TESTS PASSED" RESET " (%d/%d)                           " CYAN "│" RESET "\n", 
               test_pass, test_pass + test_fail);
    } else {
        printf(CYAN "│" RESET "   " DIM "Result:" RESET " " RED BOLD "%d FAILED" RESET ", " GREEN "%d passed" RESET " of %d tests                       " CYAN "│" RESET "\n", 
               test_fail, test_pass, test_pass + test_fail);
    }
    printf(CYAN "└──────────────────────────────────────────────────────────────────┘" RESET "\n");

    return exitcode != 0 ? 1 : 0;
}

int main(void) {
    const char* tests[] = {
        "./test_log",
        "./test_config",
        "./test_board",
        "./test_lightsigns",
        "./test_serial",
        "./test_manager",
        "./test_menu",
        "./test_login",
        "./test_setfree",
        "./test_setnono",
        "./test_assigntrain",
        "./test_outputSerial",
        "./test_orderOfDeparture",
        "./test_expo",
        "./test_railwayManager",
    };

    const int n = sizeof(tests)/sizeof(tests[0]);
    int failed = 0;

    printf("\n");
    printf(CYAN "╔══════════════════════════════════════════════════════════════════╗\n");
    printf("║" RESET BOLD "                      SPRINT3 UNIT TESTS                          " RESET CYAN "║\n");
    printf("║" RESET DIM "                    Testing all C modules                         " RESET CYAN "║\n");
    printf("╚══════════════════════════════════════════════════════════════════╝" RESET "\n");

    for (int i = 0; i < n; ++i) {
        if (run_and_capture(tests[i], i + 1, module_names[i]) != 0) failed++;
    }

    int passed = n - failed;
    
    printf("\n");
    printf(CYAN "╔══════════════════════════════════════════════════════════════════╗\n");
    printf("║" RESET BOLD "                       FINAL SUMMARY                              " RESET CYAN "║\n");
    printf("╠══════════════════════════════════════════════════════════════════╣" RESET "\n");
    printf(CYAN "║" RESET "  Total Modules Tested:  " BOLD "%2d" RESET "                                       " CYAN "║\n", n);
    printf(CYAN "║" RESET "  " GREEN "Modules Passed:        %2d" RESET "                                       " CYAN "║\n", passed);
    if (failed > 0) {
        printf(CYAN "║" RESET "  " RED "Modules Failed:        %2d" RESET "                                       " CYAN "║\n", failed);
    } else { 
        printf(CYAN "║" RESET "  Modules Failed:         0                                       " CYAN "║\n");
    }
    printf(CYAN "╚══════════════════════════════════════════════════════════════════╝" RESET "\n\n");

    if (failed == 0) {
        printf(GREEN BOLD "  🎉 All modules passed their tests!" RESET "\n\n");
    } else {
        printf(RED BOLD "  ⚠  %d module(s) have failing tests. Review above for details." RESET "\n\n", failed);
    }

    return failed == 0 ? 0 : 1;
}
