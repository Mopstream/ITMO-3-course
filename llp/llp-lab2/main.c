#include "ast.h"
#include "printer.h"
#include "parser.tab.h"

extern int yydebug;
extern ast_node *ast;

int main() {
    if (YYDEBUG == 1) {
        yydebug = 1;
    }
    yyparse();
    print_node(ast, 0);
    return 0;
}