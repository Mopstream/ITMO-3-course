#include <sys/socket.h>
#include <netinet/ip.h>
#include <arpa/inet.h>
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdbool.h>

struct sockaddr_in local;

int comp (const void * elem1, const void * elem2)
{
    char f = *((char*)elem1);
    char s = *((char*)elem2);
    if (f < s) return 1;
    if (f > s) return -1;
    return 0;
}

int main(int argc, char **argv) {
    int s = socket(AF_INET, SOCK_STREAM, 0);
    inet_aton("127.0.0.1", &local.sin_addr);
    local.sin_port = htons(atoi(argv[1]));
    local.sin_family = AF_INET;

    bind(s, (struct sockaddr*)&local, sizeof(local));
    listen(s,5);

    int cs = accept(s, NULL, NULL);
    char buffer[BUFSIZ];


    const char *text = "OFF\n";
    while (true) {
        ssize_t length = read(cs, buffer, BUFSIZ);
        buffer[length] = 0;
        if (strncmp(buffer, text, sizeof(text)) == 0)
            break;
        qsort(buffer, length, 1, comp);
        write(cs, buffer, length);
        printf("%s\n", buffer);
    }

    return 0;
}