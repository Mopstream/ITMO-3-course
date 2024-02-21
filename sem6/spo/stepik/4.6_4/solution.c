#include <stdio.h>
#include <signal.h>
#include <stdlib.h>
#include <unistd.h>

int usr1 = 0, usr2 = 0;

void shdl(int sn) {
    exit(0);
}

int main(int argc, char *argv[]) {

    printf("%d\n", getpid());
    setsid();
    daemon(0, 0);
    signal(SIGURG, shdl);
    for (;;) {}
    return 0;
}
