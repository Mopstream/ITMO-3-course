#include <stdlib.h>
#include <stdio.h>
#include <dlfcn.h>


int (*f)(int);

int main(int argc, char *argv[]) {
    char *lib = argv[1];
    char *f_name = argv[2];
    int arg = atoi(argv[3]);

    void *hdl = dlopen(lib, RTLD_LAZY);

    f = (int (*)(int)) dlsym(hdl, f_name);
    printf("%d\n", f(arg));
    return 0;
}