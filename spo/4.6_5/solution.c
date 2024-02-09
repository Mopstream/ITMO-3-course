#include <stdio.h>
#include <signal.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/shm.h>
#include <string.h>

int main(int argc, char *argv[]) {

    key_t k1 = atol(argv[1]);
    key_t k2 = atol(argv[2]);

    int shmid1 = shmget(k1, 1000, 0);
    int shmid2 = shmget(k2, 1000, 0);

    int *i1 = shmat(shmid1, 0, SHM_RDONLY);
    int *i2 = shmat(shmid2, 0, SHM_RDONLY);

    int *mas = malloc(100 * sizeof(int));
    for (int i = 0; i < 100; ++i) {
        mas[i] = i1[i] + i2[i];
    }

    key_t k3 = ftok(argv[0], 0);
    int shmid3 = shmget(k3, 1000, 0666 | IPC_CREAT);
    int* i3 = shmat(shmid3, NULL, 0);

    memcpy(i3, mas, 100*sizeof(int));

    printf("%d\n", k3);

    return 0;
}
