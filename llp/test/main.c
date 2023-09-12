#include <stdio.h>
#include <inttypes.h>

#define BLOCK_SIZE 10

struct my {
    int64_t a;
    char b;
    int64_t c;
};


int main(int argc, char **argv) {
    FILE *file = fopen("../file", "r+");

    if (file == NULL) {
        return 0;
    }
    char a[BLOCK_SIZE];

    struct my x;
    fread(&x, BLOCK_SIZE, 1, file);

    printf("%d\n%c\n%d", x.a, x.b, x.c);
    /*
    for(size_t i = 0; i < BLOCK_SIZE; ++i){
        printf("%c ",a[i]);
    }*/
    fclose(file);
    return 0;

}