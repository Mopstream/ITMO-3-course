from __future__ import annotations

from enum import Enum

from interpreter.lexer import Token, lex


class AstNodeType(Enum):
    IF = "if"
    ELSE = "else"
    WHILE = "while"
    READ = "read"
    READ_CHAR = "read_char"
    PRINT_STR = "print_str"
    PRINT_INT = "print_int"
    PRINT_CHAR = "print_char"
    LET = "let"
    EQ = "eq"
    GE = "ge"
    GT = "gt"
    LT = "lt"
    LE = "ne"
    NEQ = "neq"
    PLUS = "plus"
    MINUS = "minus"
    MUL = "mul"
    DIV = "div"
    SHL = "shl"
    SHR = "shr"
    AND = "and"
    OR = "or"
    XOR = "xor"
    STRING = "string"
    NUMBER = "number"
    NAME = "name"
    ROOT = "root"
    BLOCK = "block"
    ASSIGN = "assign"
    CMP = "cmp"
    MOD = "mod"


class InvalidStatementError(Exception):
    def __init__(self, message):
        self.message = message
        super().__init__(self.message)


def map_token_to_type(token: Token) -> AstNodeType:
    t2t: dict[Token, AstNodeType] = {
        getattr(Token, node_type.name): node_type for node_type in AstNodeType if hasattr(Token, node_type.name)
    }
    assert token in t2t
    return t2t[token]


class AstNode:
    def __init__(self, ast_type: AstNodeType, value: str = ""):
        self.astType = ast_type
        self.children: list[AstNode] = []
        self.value = value

    @classmethod
    def from_token(cls, token: Token, value: str = "") -> AstNode:
        return cls(map_token_to_type(token), value)

    def add_child(self, node: AstNode) -> None:
        self.children.append(node)


def found_at_list(tokens: list[tuple[Token, str]], t_list: list[Token]) -> None:
    assert tokens[0][0] in t_list


def found_at_list_and_pop(tokens: list[tuple[Token, str]], t_list: list[Token]) -> tuple[Token, str]:
    found_at_list(tokens, t_list)
    return tokens.pop(0)


def parse_math_expression(tokens: list[tuple[Token, str]]) -> AstNode:
    return parse_first_priority_operation(tokens)


def parse_first_priority_operation(tokens: list[tuple[Token, str]]) -> AstNode:
    left_node: AstNode = parse_second_priority_operations(tokens)
    node: AstNode = left_node

    while tokens and tokens[0][0] in [Token.PLUS, Token.MINUS]:
        node = AstNode.from_token(tokens[0][0])
        tokens.pop(0)
        node.add_child(left_node)
        node.add_child(parse_second_priority_operations(tokens))
        left_node = node
    return node


def parse_second_priority_operations(tokens: list[tuple[Token, str]]) -> AstNode:
    left_node: AstNode = parse_literal_or_name(tokens)
    node: AstNode = left_node
    while tokens and tokens[0][0] in [
        Token.MUL,
        Token.DIV,
        Token.AND,
        Token.OR,
        Token.XOR,
        Token.SHL,
        Token.SHR,
        Token.MOD,
    ]:
        node = AstNode.from_token(tokens[0][0])
        tokens.pop(0)
        node.add_child(left_node)
        node.add_child(parse_literal_or_name(tokens))
        left_node = node
    return node


def parse_literal_or_name(tokens: list[tuple[Token, str]]) -> AstNode:
    if tokens[0][0] == Token.NAME or tokens[0][0] == Token.NUMBER:
        node: AstNode = AstNode.from_token(tokens[0][0], tokens[0][1])
        tokens.pop(0)
        return node
    found_at_list_and_pop(tokens, [Token.LPAREN])
    expression: AstNode = parse_first_priority_operation(tokens)
    found_at_list_and_pop(tokens, [Token.RPAREN])
    return expression


def parse_operand(tokens: list[tuple[Token, str]]) -> AstNode:
    if tokens[0][0] == Token.STRING:
        node: AstNode = AstNode.from_token(tokens[0][0], tokens[0][1])
        tokens.pop(0)
        return node
    if tokens[0][0] == Token.READ or tokens[0][0] == Token.READ_CHAR:
        return parse_read(tokens)
    node: AstNode = parse_math_expression(tokens)
    return node


def parse_comparison(tokens: list[tuple[Token, str]]) -> AstNode:
    left_node: AstNode = parse_math_expression(tokens)
    found_at_list(tokens, [Token.GE, Token.GT, Token.LE, Token.LT, Token.NEQ, Token.EQ])
    comp: AstNode = AstNode.from_token(tokens[0][0])
    tokens.pop(0)
    right_node: AstNode = parse_math_expression(tokens)
    comp.add_child(left_node)
    comp.add_child(right_node)
    return comp


def parse_while_or_if(tokens: list[tuple[Token, str]]) -> AstNode:
    node: AstNode = AstNode.from_token(tokens[0][0])
    found_at_list_and_pop(tokens, [Token.IF, Token.WHILE])
    found_at_list_and_pop(tokens, [Token.LPAREN])
    node.add_child(parse_comparison(tokens))
    found_at_list_and_pop(tokens, [Token.RPAREN])
    node.add_child(parse_block(tokens))
    if tokens and tokens[0][0] == Token.ELSE:
        found_at_list_and_pop(tokens, [Token.ELSE])
        node.add_child(parse_block(tokens))
    return node


def parse_allocation_or_assignment(tokens: list[tuple[Token, str]]) -> AstNode:
    if tokens[0][0] == Token.LET:
        node: AstNode = AstNode.from_token(Token.LET)
        found_at_list_and_pop(tokens, [Token.LET])
    else:
        node: AstNode = AstNode.from_token(Token.ASSIGN)
    found_at_list(tokens, [Token.NAME])
    node.add_child(AstNode.from_token(Token.NAME, tokens[0][1]))
    tokens.pop(0)
    found_at_list_and_pop(tokens, [Token.ASSIGN])
    node.add_child(parse_operand(tokens))
    found_at_list_and_pop(tokens, [Token.SEMICOLON])
    return node


def parse_print(tokens: list[tuple[Token, str]]) -> AstNode:
    node: AstNode = AstNode.from_token(tokens[0][0])
    found_at_list_and_pop(tokens, [Token.PRINT_STR, Token.PRINT_INT, Token.PRINT_CHAR])
    found_at_list_and_pop(tokens, [Token.LPAREN])
    node.add_child(parse_operand(tokens))
    if node.astType == AstNodeType.PRINT_INT:
        assert node.children[0].astType != AstNodeType.STRING
    if node.astType == AstNodeType.PRINT_STR:
        assert node.children[0].astType == AstNodeType.STRING or node.children[0].astType == AstNodeType.NAME
    if node.astType == AstNodeType.PRINT_CHAR:
        assert node.children[0].astType == AstNodeType.NAME
    found_at_list_and_pop(tokens, [Token.RPAREN])
    found_at_list_and_pop(tokens, [Token.SEMICOLON])
    return node


def parse_read(tokens: list[tuple[Token, str]]) -> AstNode:
    node: AstNode = AstNode.from_token(tokens[0][0])
    found_at_list_and_pop(tokens, [Token.READ, Token.READ_CHAR])
    found_at_list_and_pop(tokens, [Token.LPAREN])
    found_at_list_and_pop(tokens, [Token.RPAREN])
    return node


def parse_block(tokens: list[tuple[Token, str]]) -> AstNode:
    node: AstNode = AstNode(AstNodeType.BLOCK)
    found_at_list_and_pop(tokens, [Token.LBRACE])
    while tokens[0][0] != Token.RBRACE:
        node.add_child(parse_statement(tokens))
    found_at_list_and_pop(tokens, [Token.RBRACE])
    return node


def parse_statement(tokens: list[tuple[Token, str]]) -> AstNode:
    if tokens[0][0] == Token.WHILE or tokens[0][0] == Token.IF:
        return parse_while_or_if(tokens)
    if tokens[0][0] == Token.LET or tokens[0][0] == Token.NAME:
        return parse_allocation_or_assignment(tokens)
    if tokens[0][0] == Token.PRINT_STR or tokens[0][0] == Token.PRINT_INT or tokens[0][0] == Token.PRINT_CHAR:
        return parse_print(tokens)
    if tokens[0][0] == Token.READ or tokens[0][0] == Token.READ_CHAR:
        return parse_read(tokens)
    raise InvalidStatementError("Invalid statement {}".format(tokens[0][0].name))


def parse_program(tokens: list[tuple[Token, str]]) -> AstNode:
    node: AstNode = AstNode(AstNodeType.ROOT)
    while tokens:
        node.add_child(parse_statement(tokens))
    return node


def parse(program: str) -> AstNode:
    return parse_program(lex(program))
