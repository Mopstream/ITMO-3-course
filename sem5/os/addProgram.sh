#!/bin/bash
cp $1$2.c /home/mopstream/Downloads/xv6/xv6-riscv/user/
sed -i "134a\\\t\$U/_$2\\\\" /home/mopstream/Downloads/xv6/xv6-riscv/Makefile
