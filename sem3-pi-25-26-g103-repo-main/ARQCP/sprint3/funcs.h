#include "config.h"
#include "Manager/manager.h"

void loadSetupFile(const char* filename, Config* config);
int login(Config* config, Manager* m);
int outputSerial(Config* config);
int adminMenu(Config* config, Manager *manager);
int operatorMenu(Config* config, Manager *manager);
void railwayManagerMenu(Config *config, Manager *m);