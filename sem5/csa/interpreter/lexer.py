from __future__ import annotations

import re
from enum import Enum


class Token(Enum):
    IF = r"if"
    ELSE = r"else"
    WHILE = r"while"
    READ_CHAR = r"read_char"
    READ = r"read"
    PRINT_STR = r"print_str"
    PRINT_INT = r"print_int"
    PRINT_CHAR = r"print_char"
    LET = r"let"
    PLUS = r"\+"
    MINUS = r"-"
    MUL = r"\*"
    DIV = r"/"
    MOD = r"%"
    SHL = r"<<"
    SHR = r">>"
    AND = r"&"
    OR = r"\|"
    XOR = r"\^"
    EQ = r"=="
    GE = r">="
    GT = r">"
    LT = r"<"
    LE = r"<="
    NEQ = r"!="
    LPAREN = r"\("
    RPAREN = r"\)"
    LBRACE = r"{"
    RBRACE = r"}"
    SEMICOLON = r";"
    ASSIGN = r"="
    NAME = r"[a-zA-Z][a-zA-Z0-9]*"
    STRING = r'"[\w\s,.:;!?()\\-]+"'
    NUMBER = r"-?[0-9]+"


def lex(program: str) -> list[tuple[Token, str]]:
    regex = "|".join(f"(?P<{t.name}>{t.value})" for t in Token)
    found_tokens = re.finditer(regex, program)
    tokens: list[tuple[Token, str]] = []
    for token in found_tokens:
        t_type: str = token.lastgroup
        t_value: str = token.group(t_type)
        if t_type == "STRING":
            tokens.append((Token[t_type], t_value[1:-1].replace("\\n", "\n")))
        else:
            tokens.append((Token[t_type], t_value))
    return tokens
