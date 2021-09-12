#
# Sample Makefile for the shared buffer lab.
#

#
# Ask for debugging and warnings, and enable pthreads.
#
CFLAGS  =  -g -Og -Wall -pthread

all:    sharedbuf

sharedbuf:    sharedbuf.c
	$(CC) $(CFLAGS) -o sharedbuf sharedbuf.c

clean:
	rm -f sharedbuf
