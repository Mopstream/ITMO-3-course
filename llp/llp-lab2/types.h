#ifndef LLP_LAB2_TYPES_H
#define LLP_LAB2_TYPES_H

#include "stdint.h"
#include "stdbool.h"

typedef struct {
    uint32_t size;
    char* str;
} string_t;

typedef union {
    int32_t i;
    double d;
    string_t str;
    bool b;
} value_t;

typedef enum {
    INT,
    DOUBLE,
    STRING,
    BOOL
} val_type_t;

typedef struct {
    val_type_t type;
    value_t val;
} attr_t;

typedef enum {
    GT,
    LT,
    EQ,
    GT_EQ,
    LT_EQ,
    SUBSTR,
} cmp_t;

#endif
