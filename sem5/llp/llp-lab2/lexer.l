%option yylineno noyywrap nounput noinput

%{
#include <stdio.h>
#include <stdbool.h>
#include "ast.h"
#include "parser.tab.h"
%}

%%
\(                          { return L_PR; }
\)                          { return R_PR; }
\{                          { return L_BR; }
\}                          { return R_BR; }
\[                          { return L_BRK; }
\]                          { return R_BRK; }
,                           { return COMMA; }
:                           { return COLON; }
select                      { return SELECT_T; }
insert                      { return INSERT_T; }
update                      { return UPDATE_T; }
delete                      { return DELETE_T; }
index                       { return INDEX_T; }
index_name                  { return INDEX_NAME; }
names                       { return NAMES; }
node                        { return NODE_T; }
link                        { return LINK_T; }
values                      { return VALUES; }
id                          { return ID; }
node_from                   { return NODE_FROM; }
node_to                     { return NODE_TO; }
filter                      { return FILTER_T; }
set                         { return SET_T; }
eq                          { yylval.cmp = EQ; return COMPARE_OP; }
gt                          { yylval.cmp = GT; return COMPARE_OP; }
lt                          { yylval.cmp = LT; return COMPARE_OP; }
gt_eq                       { yylval.cmp = GT_EQ; return COMPARE_OP; }
lt_eq                       { yylval.cmp = LT_EQ; return COMPARE_OP; }
substr                      { yylval.cmp = SUBSTR; return COMPARE_OP; }
and                         { yylval.l_op = AND; return LOGICAL_BOP; }
or                          { yylval.l_op = OR; return LOGICAL_BOP; }
not                         { yylval.l_op = NOT; return LOGICAL_UOP; }
true                        { yylval.bool_val = true; return BOOL_T; }
false                       { yylval.bool_val = false; return BOOL_T; }
[+-]?[0-9]+                 { yylval.int_val = atoi(yytext); return INT_T; }
[+-]?[0-9]*\.[0-9]*         { yylval.double_val = atof(yytext); return DOUBLE_T; }
\"[^\"]*\"                  { yylval.str_val = strdup(yytext); return STRING_T; }
[A-Za-z_][A-Za-z0-9_]*\.db  { yylval.str_val = strdup(yytext); return FILENAME_T; }
[A-Za-z_][A-Za-z0-9_]*      { yylval.str_val = strdup(yytext); return NAME_T; }
\n                          { }
.                           { }
%%