#include <stdio.h>
#include <signal.h>
#include <stdlib.h>

int usr1 = 0, usr2 = 0;

void shdl(int sn) {
    switch (sn) {
        case (SIGUSR1): {
            ++usr1;
            break;
        }
        case (SIGUSR2): {
            ++usr2;
            break;
        }
        case (SIGTERM): {
            printf("%d %d\n", usr1, usr2);
            exit(0);
            break;
        }
    }
}

int main(int argc, char *argv[]) {

    signal(SIGUSR1, shdl);
    signal(SIGUSR2, shdl);
    signal(SIGTERM, shdl);

    for (;;) {}

    return 0;
}