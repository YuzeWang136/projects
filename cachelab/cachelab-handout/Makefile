#
# Student makefile for Cache Lab
# Note: requires a 64-bit x86-64 system 
#
CC = gcc
CFLAGS = -g -Wall -Werror -std=c99 -m64

all: csim

csim: csim.c 
	$(CC) $(CFLAGS) -o csim csim.c -lm 

#
# Clean the src dirctory
#
clean:
	rm -rf *.o
	rm -f csim
	rm -f .csim_results .marker
