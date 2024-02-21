#include <stdio.h>
#include <unistd.h>


int main(int argc, char *argv[]) {

    printf("%d\n", getpid());
    setsid();
    daemon(0,0);
    sleep(1000);
    return 0;
}