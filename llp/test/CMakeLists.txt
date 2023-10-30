cmake_minimum_required(VERSION 3.26)
project(test C)

set(CMAKE_C_STANDARD 11)

add_executable(test main.c
        main.c
        ../../../../CLionProjects/llp-lab1/includes/api/schema.h
        ../../../../CLionProjects/llp-lab1/includes/api/element.h
        ../../../../CLionProjects/llp-lab1/includes/api/query.h)
