#include <stdio.h>
#include <getopt.h>
#include <stdbool.h>

int main(int argc, char** args) {
    int opindex = 0, opchar = 0;
    bool isOk = true;

    opterr = 0;

    struct option opts[] = {
            {"query", required_argument, 0, 'q'},
            {"longinformationrequest", no_argument, 0, 'i'},
            {"version", no_argument, 0, 'v'},
            {0, 0, 0, 0},
    };

    while (-1 != (opchar = getopt_long(argc, args, "q:iv", opts, &opindex))) {
        if (opchar == '?') isOk = false;
    }

    if (isOk) printf("+\n");
    else printf("-\n");
}