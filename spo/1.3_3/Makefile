all:
	gcc solution.c -fPIC -ldl -o solution

lib:
	gcc -shared lib.c -fPIC -o lib.so

clean:
	rm solution lib.so

run: all
	./solution lib.so jopa 2