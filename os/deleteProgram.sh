#!/bin/bash
rm /home/mopstream/Downloads/xv6/xv6-riscv/user/*$1*
sed -i "/_$1/d" /home/mopstream/Downloads/xv6/xv6-riscv/Makefile
