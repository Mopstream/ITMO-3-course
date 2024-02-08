#include <stdio.h>
#include <unistd.h>
#include <stdlib.h>
#include "string.h"

int fu(int p_pid) {
    char path[100];
    sprintf(path, "/proc/%d/task/%d/children", p_pid, p_pid);
    FILE *f = fopen(path, "r");
    char buf[1000] = {0};

    fread(buf, 1000, 1, f);
    fclose(f);
    char *pch = strtok(buf, " ");
    int count = 0;
    while (pch != NULL) {
        ++count;
        count += fu(atoi(pch));
        pch = strtok(pch + strlen(pch) + 1, " ");
    }
    return count;
}

int main(int argc, char *argv[]) {

    int parent = atoi(argv[1]);
//    int parent = 849;
    printf("%d\n", fu(parent) + 1);
    return 0;

}