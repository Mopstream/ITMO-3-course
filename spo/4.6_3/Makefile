all:
	gcc solution.c -o solution

clean:
	rm solution

# 1 3
test:
	./solution &
	pkill solution -USR1
	pkill solution -USR2
	pkill solution -USR2
	pkill solution -USR2
	pkill solution -TERM