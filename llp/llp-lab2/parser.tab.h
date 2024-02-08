/* A Bison parser, made by GNU Bison 3.8.2.  */

/* Bison interface for Yacc-like parsers in C

   Copyright (C) 1984, 1989-1990, 2000-2015, 2018-2021 Free Software Foundation,
   Inc.

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with this program.  If not, see <https://www.gnu.org/licenses/>.  */

/* As a special exception, you may create a larger work that contains
   part or all of the Bison parser skeleton and distribute that work
   under terms of your choice, so long as that work isn't itself a
   parser generator using the skeleton or a modified version thereof
   as a parser skeleton.  Alternatively, if you modify or redistribute
   the parser skeleton itself, you may (at your option) remove this
   special exception, which will cause the skeleton and the resulting
   Bison output files to be licensed under the GNU General Public
   License without this special exception.

   This special exception was added by the Free Software Foundation in
   version 2.2 of Bison.  */

/* DO NOT RELY ON FEATURES THAT ARE NOT DOCUMENTED in the manual,
   especially those whose name start with YY_ or yy_.  They are
   private implementation details that can be changed or removed.  */

#ifndef YY_YY_PARSER_TAB_H_INCLUDED
# define YY_YY_PARSER_TAB_H_INCLUDED
/* Debug traces.  */
#ifndef YYDEBUG
# define YYDEBUG 0
#endif
#if YYDEBUG
extern int yydebug;
#endif

/* Token kinds.  */
#ifndef YYTOKENTYPE
# define YYTOKENTYPE
  enum yytokentype
  {
    YYEMPTY = -2,
    YYEOF = 0,                     /* "end of file"  */
    YYerror = 256,                 /* error  */
    YYUNDEF = 257,                 /* "invalid token"  */
    L_PR = 258,                    /* L_PR  */
    R_PR = 259,                    /* R_PR  */
    L_BR = 260,                    /* L_BR  */
    R_BR = 261,                    /* R_BR  */
    L_BRK = 262,                   /* L_BRK  */
    R_BRK = 263,                   /* R_BRK  */
    COMMA = 264,                   /* COMMA  */
    COLON = 265,                   /* COLON  */
    SELECT_T = 266,                /* SELECT_T  */
    INSERT_T = 267,                /* INSERT_T  */
    UPDATE_T = 268,                /* UPDATE_T  */
    DELETE_T = 269,                /* DELETE_T  */
    INDEX_T = 270,                 /* INDEX_T  */
    INDEX_NAME = 271,              /* INDEX_NAME  */
    NAMES = 272,                   /* NAMES  */
    NODE_T = 273,                  /* NODE_T  */
    LINK_T = 274,                  /* LINK_T  */
    ID = 275,                      /* ID  */
    NODE_FROM = 276,               /* NODE_FROM  */
    NODE_TO = 277,                 /* NODE_TO  */
    VALUES = 278,                  /* VALUES  */
    FILTER_T = 279,                /* FILTER_T  */
    SET_T = 280,                   /* SET_T  */
    FILENAME_T = 281,              /* FILENAME_T  */
    COMPARE_OP = 282,              /* COMPARE_OP  */
    LOGICAL_BOP = 283,             /* LOGICAL_BOP  */
    LOGICAL_UOP = 284,             /* LOGICAL_UOP  */
    BOOL_T = 285,                  /* BOOL_T  */
    INT_T = 286,                   /* INT_T  */
    DOUBLE_T = 287,                /* DOUBLE_T  */
    STRING_T = 288,                /* STRING_T  */
    NAME_T = 289                   /* NAME_T  */
  };
  typedef enum yytokentype yytoken_kind_t;
#endif

/* Value type.  */
#if ! defined YYSTYPE && ! defined YYSTYPE_IS_DECLARED
union YYSTYPE
{
#line 19 "parser.y"

    bool bool_val;
    int32_t int_val;
    double double_val;
    char* str_val;
    ast_node* node;
    attr_t* attr;
    insert_body_t in_body;
    set_t* set_e;
    cmp_t cmp;
    logical_op l_op;

#line 111 "parser.tab.h"

};
typedef union YYSTYPE YYSTYPE;
# define YYSTYPE_IS_TRIVIAL 1
# define YYSTYPE_IS_DECLARED 1
#endif


extern YYSTYPE yylval;


int yyparse (void);


#endif /* !YY_YY_PARSER_TAB_H_INCLUDED  */
