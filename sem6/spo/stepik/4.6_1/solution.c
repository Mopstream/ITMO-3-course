#include <stdio.h>
#include <unistd.h>


int main(int argc, char *argv[]) {
    char *cmd = argv[1];
    char *param = argv[2];
    char buf[100];
    sprintf(buf, "%s %s", cmd, param);
    FILE *f = popen(buf, "r");
    char c;
    int cnt = 0;
    do {
        c = fgetc(f);
        if (c == '0') {
            ++cnt;
        }
    } while (c != EOF);
    pclose(f);
    printf("%d\n", cnt);
    return 0;
}