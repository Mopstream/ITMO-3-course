#include <stdio.h>
#include <sys/select.h>
#include <unistd.h>
#include <fcntl.h>
#include <stdlib.h>
#include <stdbool.h>

void m_read(int fd) {
    char buf[100];
    int bytes = read(fd, buf, 100);
    buf[bytes] = 0;
    printf("%d\n", atoi(buf));
}


int main(int argc, char *argv[]) {
    int in1 = open("./in1", O_NONBLOCK | O_RDONLY);
    int in2 = open("./in2", O_NONBLOCK | O_RDONLY);

    fd_set read_set;

    bool f1 = true;
    bool f2 = true;

    int cnt = 0;
    while (f1 || f2) {
        FD_ZERO(&read_set);
        FD_SET(in1, &read_set);
        FD_SET(in2, &read_set);

        int res = select(10, &read_set, 0, 0, 0);
        if (res) {
            int fd;
            bool *flag;
            if (f1 && FD_ISSET(in1, &read_set)) {
                fd = in1;
                flag = &f1;
            }
            if (f2 && FD_ISSET(in2, &read_set)) {
                fd = in2;
                flag = &f2;
            }
            char buf[100];
            int bytes = read(fd, buf, 100);
            if (bytes <= 0) *flag = false;
            buf[bytes] = 0;
            cnt += atoi(buf);
        }
    }
    printf("%d\n", cnt);
    return 0;
}