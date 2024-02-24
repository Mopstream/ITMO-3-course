from __future__ import annotations

import sys

from machine.isa import Opcode, Register, StaticMemAddressStub, Word, write_code

from interpreter.parser import AstNode, AstNodeType, parse


class WrongTokenTypeError(Exception):
    def __init__(self, message):
        self.message = message
        super().__init__(self.message)


ast_type2opcode = {
    AstNodeType.EQ: Opcode.JE,
    AstNodeType.GE: Opcode.JGE,
    AstNodeType.GT: Opcode.JG,
    AstNodeType.LT: Opcode.JL,
    AstNodeType.LE: Opcode.JLE,
    AstNodeType.NEQ: Opcode.JNE,
    AstNodeType.PLUS: Opcode.ADD,
    AstNodeType.MINUS: Opcode.SUB,
    AstNodeType.XOR: Opcode.XOR,
    AstNodeType.OR: Opcode.OR,
    AstNodeType.AND: Opcode.AND,
    AstNodeType.SHL: Opcode.SHL,
    AstNodeType.SHR: Opcode.SHR,
}

condition_inverted = {
    Opcode.JE: Opcode.JNE,
    Opcode.JGE: Opcode.JL,
    Opcode.JG: Opcode.JLE,
    Opcode.JL: Opcode.JGE,
    Opcode.JLE: Opcode.JG,
    Opcode.JNE: Opcode.JE,
}


class Program:
    def __init__(self):
        self.machine_code: list[Word] = []
        self.current_command_address = 0
        self.static_mem: list[int] = []
        self.current_static_offset = 0  # оффсет следующей переменной от начала статической памяти
        self.variables: dict[str, int] = {}  # переменные и их оффсет
        self.reg_to_var: dict[Register, str] = {}
        self.var_to_reg: dict[str, Register] = {}
        self.reg_counter = 2
        self.prog_size = 4096
        self.input_buffer_size = 32

    def add_instruction(
        self,
        opcode: Opcode,
        arg1: int | Register | StaticMemAddressStub = 0,
        arg2: int | Register | StaticMemAddressStub = 0,
    ) -> int:
        self.machine_code.append(Word(self.current_command_address, opcode, arg1, arg2))
        self.current_command_address += 1
        return self.current_command_address - 1

    def add_static_data(self, value: int):
        self.static_mem.append(value)
        self.current_static_offset += 1

    def push_variable_in_stack(self, name: str, value: int):
        addr: int = len(self.variables)
        reg = self.clear_register_for_variable()
        self.add_instruction(Opcode.LD_LIT, reg, value)
        self.add_instruction(Opcode.PUSH, reg)
        self.variables[name] = addr
        return addr

    def return_from_block(self, stack_offset_before_block: int) -> None:
        vars_to_del = []
        for name in self.variables:
            if self.get_variable_offset(name) >= stack_offset_before_block:
                self.clear_variable_in_registers(name)
                vars_to_del.append(name)
        for name in vars_to_del:
            self.variables.pop(name)
        self.add_instruction(Opcode.LD_LIT, Register.r15, self.prog_size - 1 - stack_offset_before_block)

    def add_variable_in_static_mem(self, value: str) -> int:
        size = len(value)
        self.add_static_data(size)
        for char in value:
            self.add_static_data(ord(char))
        size += 1
        return self.current_static_offset - size

    def get_variable_offset(self, name: str) -> int | None:
        return self.variables.get(name)

    def add_strings_in_static_mem(self):
        static_mem_start = self.current_command_address
        for instruction in self.machine_code:
            if isinstance(instruction.arg1, StaticMemAddressStub) and instruction.arg1.offset >= 0:
                instruction.arg1 = instruction.arg1.offset + static_mem_start
            if isinstance(instruction.arg2, StaticMemAddressStub) and instruction.arg2.offset >= 0:
                instruction.arg2 = instruction.arg2.offset + static_mem_start

    def resolve_static_mem(self) -> None:
        self.add_strings_in_static_mem()
        for data in self.static_mem:
            self.add_instruction(Opcode.JUMP, data)
        static_mem_end = self.current_command_address - 1
        self.add_instruction(Opcode.JUMP, self.current_command_address + 1)  # begin of read buffer
        for instruction in self.machine_code:
            if isinstance(instruction.arg1, StaticMemAddressStub) and instruction.arg1.offset < 0:
                instruction.arg1 = -instruction.arg1.offset + static_mem_end
            if isinstance(instruction.arg2, StaticMemAddressStub) and instruction.arg2.offset < 0:
                instruction.arg2 = -instruction.arg2.offset + static_mem_end

    def change_reg(self) -> Register:
        self.reg_counter += 1
        if self.reg_counter >= 9:
            self.reg_counter = 2
        return Register(self.reg_counter)

    def clear_register_for_variable(self) -> Register:
        reg = self.change_reg()
        var: str | None = self.reg_to_var.get(reg)
        self.reg_to_var.pop(reg, None)
        self.var_to_reg.pop(var, None)
        return reg

    def load_variable(self, var_name: str) -> Register:
        reg = self.var_to_reg.get(var_name)
        if reg is not None:
            return reg
        reg = self.clear_register_for_variable()
        var_offs = self.get_variable_offset(var_name)
        self.add_instruction(Opcode.LD_STACK, reg, var_offs)
        self.var_to_reg[var_name] = reg
        self.reg_to_var[reg] = var_name
        return reg

    def clear_variable_in_registers(self, name: str) -> None:
        reg: Register | None = self.var_to_reg.get(name)
        if reg is not None:
            self.reg_to_var.pop(reg, None)
            self.var_to_reg.pop(name, None)

    def drop_variables_in_registers(self):
        self.reg_to_var.clear()
        self.var_to_reg.clear()


def ast_to_machine_code(root: AstNode) -> list[Word]:
    program = Program()
    for child in root.children:
        ast_to_machine_code_rec(child, program)
    program.add_instruction(Opcode.HALT)
    program.resolve_static_mem()
    return program.machine_code


def ast_to_machine_code_rec(node: AstNode, program: Program) -> None:
    if node.astType == AstNodeType.WHILE or node.astType == AstNodeType.IF:
        ast_to_machine_code_if_or_while(node, program)
    elif node.astType == AstNodeType.LET:
        ast_to_machine_code_let(node, program)
    elif node.astType == AstNodeType.ASSIGN:
        ast_to_machine_code_assign(node, program)
    elif node.astType == AstNodeType.PRINT_STR or node.astType == AstNodeType.PRINT_INT or node.astType == AstNodeType.PRINT_CHAR:
        ast_to_machine_code_print(node, program)
    else:
        raise WrongTokenTypeError("Invalid ast node type {}".format(node.astType.name))


def ast_to_machine_code_math(node: AstNode, program: Program) -> None:
    if not ast_to_machine_code_math_rec(node, program):
        program.add_instruction(Opcode.POP, Register.r9)


def resolve_register_for_operation(is_left: bool = True) -> Register:
    return Register.r9 if is_left else Register.r10


#  returns True if no arythm
def ast_to_machine_code_math_rec(node: AstNode, program: Program, is_left: bool = True) -> bool:
    if node.astType == AstNodeType.NUMBER:
        program.add_instruction(Opcode.LD_LIT, resolve_register_for_operation(is_left), int(node.value))
        return True
    if node.astType == AstNodeType.NAME:
        reg = program.load_variable(node.value)
        program.add_instruction(Opcode.MV, reg, resolve_register_for_operation(is_left))
        return True
    if ast_to_machine_code_math_rec(node.children[0], program, True):
        program.add_instruction(Opcode.PUSH, Register.r9)
    if ast_to_machine_code_math_rec(node.children[1], program, False):
        program.add_instruction(Opcode.PUSH, Register.r10)
    program.add_instruction(Opcode.POP, Register.r10)
    program.add_instruction(Opcode.POP, Register.r9)
    if not perform_userspace_math(node, program):
        program.add_instruction(ast_type2opcode[node.astType], Register.r9, Register.r10)
    program.add_instruction(Opcode.PUSH, Register.r9)
    return False


def perform_userspace_math(node: AstNode, program: Program) -> bool:
    if node.astType is AstNodeType.DIV:
        ast_to_machine_code_div(node, program)
        return True
    if node.astType is AstNodeType.MOD:
        ast_to_machine_code_mod(node, program)
        return True
    if node.astType is AstNodeType.MUL:
        ast_to_machine_code_mul(program)
        return True
    return False


def ast_to_machine_code_mul(program: Program):
    program.add_instruction(Opcode.PUSH, Register.r3)  # to store 1 to check last bit
    program.add_instruction(Opcode.PUSH, Register.r4)  # to store the result of multiplication by the power of 2
    program.add_instruction(Opcode.PUSH, Register.r5)  # counter
    program.add_instruction(Opcode.PUSH, Register.r11)  # res
    program.add_instruction(Opcode.PUSH, Register.r12)  # to check the last bit value
    program.add_instruction(Opcode.LD_LIT, Register.r11, 0)
    program.add_instruction(Opcode.LD_LIT, Register.r5, 0)
    program.add_instruction(Opcode.LD_LIT, Register.r3, 1)

    loop_start = program.add_instruction(Opcode.CMP, Register.r10, Register.r0)
    loop_jump = program.add_instruction(Opcode.JE, 0)
    program.add_instruction(Opcode.MV, Register.r10, Register.r12)
    program.add_instruction(Opcode.ADD, Register.r12, Register.r3)
    program.add_instruction(Opcode.CMP, Register.r12, Register.r0)
    if_statement = program.add_instruction(Opcode.JE, 0)
    program.add_instruction(Opcode.MV, Register.r9, Register.r4)
    program.add_instruction(Opcode.SHL, Register.r4, Register.r5)
    program.add_instruction(Opcode.ADD, Register.r11, Register.r4)
    program.add_instruction(Opcode.INC, Register.r5)
    after_if = program.add_instruction(Opcode.SHR, Register.r10, Register.r3)
    program.machine_code[if_statement].arg1 = after_if
    program.add_instruction(Opcode.JUMP, loop_start)

    after_loop = program.add_instruction(Opcode.MV, Register.r11, Register.r9)
    program.machine_code[loop_jump].arg1 = after_loop
    program.add_instruction(Opcode.POP, Register.r12)
    program.add_instruction(Opcode.POP, Register.r11)
    program.add_instruction(Opcode.POP, Register.r5)
    program.add_instruction(Opcode.POP, Register.r4)
    program.add_instruction(Opcode.POP, Register.r3)


def ast_to_machine_code_div(node: AstNode, program: Program) -> int:
    prog_begin = program.add_instruction(Opcode.PUSH, Register.r2)  # r_addr
    program.add_instruction(Opcode.PUSH, Register.r3)  # q_addr
    program.add_instruction(Opcode.PUSH, Register.r4)  # bits_num_addr
    program.add_instruction(Opcode.PUSH, Register.r11)  # tmp
    program.add_instruction(Opcode.PUSH, Register.r12)  # tmp

    program.add_instruction(Opcode.LD_LIT, Register.r12, 1)  # надо для вычисения 1 бита
    program.add_instruction(Opcode.LD_LIT, Register.r2, 0)
    program.add_instruction(Opcode.LD_LIT, Register.r3, 0)
    program.add_instruction(Opcode.LD_LIT, Register.r4, 0)

    program.add_instruction(Opcode.PUSH, Register.r9)

    loop_start = program.add_instruction(Opcode.CMP, Register.r9, Register.r0)  # in r4 количество бит в N
    program.add_instruction(Opcode.JE, loop_start + 5)
    program.add_instruction(Opcode.SHR, Register.r9, Register.r12)
    program.add_instruction(Opcode.INC, Register.r4)
    program.add_instruction(Opcode.JUMP, loop_start)

    program.add_instruction(Opcode.DEC, Register.r4)
    program.add_instruction(Opcode.POP, Register.r9)
    loop_start = program.add_instruction(Opcode.CMP, Register.r4, Register.r0)
    program.add_instruction(Opcode.JL, loop_start + 16)
    program.add_instruction(Opcode.SHL, Register.r2, Register.r12)
    program.add_instruction(Opcode.MV, Register.r9, Register.r11)
    program.add_instruction(Opcode.SHR, Register.r11, Register.r4)
    program.add_instruction(Opcode.AND, Register.r11, Register.r12)
    program.add_instruction(Opcode.ADD, Register.r2, Register.r11)

    if_begin = program.add_instruction(Opcode.CMP, Register.r2, Register.r10)
    program.add_instruction(Opcode.JL, if_begin + 6)
    program.add_instruction(Opcode.SUB, Register.r2, Register.r10)
    program.add_instruction(Opcode.SHL, Register.r3, Register.r12)
    program.add_instruction(Opcode.ADD, Register.r3, Register.r12)
    program.add_instruction(Opcode.JUMP, if_begin + 7)
    program.add_instruction(Opcode.SHL, Register.r3, Register.r12)  # else
    program.add_instruction(Opcode.DEC, Register.r4)
    program.add_instruction(Opcode.JUMP, loop_start)
    program.add_instruction(Opcode.MV, Register.r3, Register.r9)
    program.add_instruction(Opcode.MV, Register.r2, Register.r10)

    program.add_instruction(Opcode.POP, Register.r12)
    program.add_instruction(Opcode.POP, Register.r11)
    program.add_instruction(Opcode.POP, Register.r4)
    program.add_instruction(Opcode.POP, Register.r3)
    program.add_instruction(Opcode.POP, Register.r2)
    return prog_begin


def ast_to_machine_code_mod(node: AstNode, program: Program):
    ast_to_machine_code_div(node, program)
    program.add_instruction(Opcode.MV, Register.r10, Register.r9)


def ast_to_machine_code_block(node: AstNode, program: Program) -> int:
    for child in node.children:
        ast_to_machine_code_rec(child, program)
    return program.current_command_address


def ast_to_machine_code_if_or_while(node: AstNode, program: Program) -> None:
    program.drop_variables_in_registers()
    block_begin_stack_pointer: int = len(program.variables)
    comparator = node.children[0]
    block_begin: int = program.current_command_address
    addr_left = parse_expression(comparator.children[0], program)
    if addr_left is None:
        program.add_instruction(Opcode.MV, Register.r9, Register.r12)
    else:
        program.add_instruction(Opcode.LD_STACK, Register.r12, addr_left)
    addr_right = parse_expression(comparator.children[1], program)
    if addr_right is not None:
        program.add_instruction(Opcode.LD_STACK, Register.r9, addr_right)

    program.add_instruction(Opcode.CMP, Register.r12, Register.r9)
    comp_instr_addr = program.add_instruction(condition_inverted[ast_type2opcode[comparator.astType]], -1)
    block_end = ast_to_machine_code_block(node.children[1], program)

    if node.astType == AstNodeType.WHILE:
        block_end = program.add_instruction(Opcode.JUMP, block_begin)
        program.machine_code[comp_instr_addr].arg1 = block_end + 1
    else:
        if len(node.children) == 3 and node.children[2].astType == AstNodeType.ELSE:
            addr_before_else_block = program.add_instruction(Opcode.JUMP, -1)
            else_block_end = ast_to_machine_code_block(node.children[1], program)
            program.machine_code[comp_instr_addr].arg1 = addr_before_else_block + 1  # else start address
            program.machine_code[addr_before_else_block].arg1 = else_block_end + 1
        else:
            program.machine_code[comp_instr_addr].arg1 = block_end + 1
    program.drop_variables_in_registers()
    program.return_from_block(block_begin_stack_pointer)


def ast_to_machine_code_read(program: Program) -> None:
    program.add_instruction(Opcode.PUSH, Register.r8)
    program.add_instruction(Opcode.LD_LIT, Register.r9, 0)  # прочитанный символ
    program.add_instruction(Opcode.LD_LIT, Register.r11, 0)  # счетчик
    program.add_instruction(Opcode.LD_ADDR, Register.r8, StaticMemAddressStub(-1))
    program.add_instruction(Opcode.MV, Register.r8, Register.r12)  # в r8 адрес начала буфера
    do_while_start = program.current_command_address
    program.add_instruction(Opcode.READ, Register.r9, 0)
    program.add_instruction(Opcode.CMP, Register.r9, Register.r0)
    program.add_instruction(Opcode.JE, do_while_start + 7)
    program.add_instruction(Opcode.INC, Register.r11)
    program.add_instruction(Opcode.INC, Register.r12)
    program.add_instruction(Opcode.ST, Register.r9, Register.r12)
    program.add_instruction(Opcode.JUMP, do_while_start)
    program.add_instruction(Opcode.ST, Register.r11, Register.r8)
    program.add_instruction(Opcode.MV, Register.r8, Register.r9)  # save read string address in r9
    program.add_instruction(Opcode.ADD, Register.r8, Register.r11)
    program.add_instruction(Opcode.INC, Register.r8)
    program.add_instruction(Opcode.ST_ADDR, Register.r8, StaticMemAddressStub(-1))
    program.add_instruction(Opcode.POP, Register.r8)


def ast_to_machine_code_read_char(program: Program) -> None:
    program.add_instruction(Opcode.READ, Register.r9, 0)


# util func, сохранение значения по адресу(оба не в регистрах)
def st_literal_by_stack_offset(program: Program, value: int | StaticMemAddressStub, var_addr: int):
    reg: Register = program.clear_register_for_variable()
    program.add_instruction(Opcode.LD_LIT, reg, value)
    program.add_instruction(Opcode.ST_STACK, reg, var_addr)


def ast_to_machine_code_assign(node: AstNode, program: Program) -> None:
    name: str = node.children[0].value
    assert node.children[0].astType == AstNodeType.NAME
    if node.astType == AstNodeType.ASSIGN or node.astType == AstNodeType.LET:
        addr = program.get_variable_offset(name)
        assert addr is not None
        if node.children[1].astType == AstNodeType.STRING:
            addr_new = program.add_variable_in_static_mem(node.children[1].value)
            st_literal_by_stack_offset(program, StaticMemAddressStub(addr_new), addr)
            program.clear_variable_in_registers(name)
        elif node.children[1].astType == AstNodeType.READ:
            ast_to_machine_code_read(program)
            program.add_instruction(Opcode.ST_STACK, Register.r9, addr)  # update var value
            program.clear_variable_in_registers(name)
        elif node.children[1].astType == AstNodeType.READ_CHAR:
            ast_to_machine_code_read_char(program)
            program.add_instruction(Opcode.ST_STACK, Register.r9, addr)
            program.clear_variable_in_registers(name)
        else:
            ast_to_machine_code_math(node.children[1], program)
            program.add_instruction(Opcode.ST_STACK, Register.r9, addr)
            program.clear_variable_in_registers(name)


def ast_to_machine_code_let(node: AstNode, program: Program) -> None:
    name: str = node.children[0].value
    if node.astType == AstNodeType.LET:
        assert program.get_variable_offset(name) is None
        program.push_variable_in_stack(name, 0)
        ast_to_machine_code_assign(node, program)


def ast_to_machine_code_print(node: AstNode, program: Program) -> None:
    if node.astType == AstNodeType.PRINT_INT:
        ast_to_machine_code_math(node.children[0], program)
        program.add_instruction(Opcode.PUSH, Register.r11)
        program.add_instruction(Opcode.LD_LIT, Register.r11, 1)
        loop_begin = program.add_instruction(Opcode.LD_LIT, Register.r10, 10)
        ast_to_machine_code_div(node, program)
        program.add_instruction(Opcode.ADD_LIT, Register.r10, 48)
        cmp_addr = program.add_instruction(Opcode.CMP, Register.r9, Register.r0)
        program.add_instruction(Opcode.JE, cmp_addr + 5)
        program.add_instruction(Opcode.INC, Register.r11)
        program.add_instruction(Opcode.PUSH, Register.r10)
        program.add_instruction(Opcode.JUMP, loop_begin)

        program.add_instruction(Opcode.PUSH, Register.r10)  # даже если 0, последний остаток надо записывать

        loop_begin = program.add_instruction(Opcode.CMP, Register.r11, Register.r0)
        program.add_instruction(Opcode.JE, loop_begin + 6)
        program.add_instruction(Opcode.POP, Register.r10)
        program.add_instruction(Opcode.PRINT, Register.r10, 0)
        program.add_instruction(Opcode.DEC, Register.r11)
        program.add_instruction(Opcode.JUMP, loop_begin)
        program.add_instruction(Opcode.POP, Register.r11)
    if node.astType == AstNodeType.PRINT_STR:
        if node.children[0].astType == AstNodeType.STRING:
            str_addr = program.add_variable_in_static_mem(node.children[0].value)
            program.add_instruction(Opcode.LD_LIT, Register.r9, StaticMemAddressStub(str_addr))
        else:
            var_addr: int | None = program.get_variable_offset(node.children[0].value)
            assert var_addr is not None
            program.add_instruction(Opcode.LD_STACK, Register.r9, var_addr)
        program.add_instruction(Opcode.MV, Register.r9, Register.r11)
        program.add_instruction(Opcode.INC, Register.r11)  # первый байт данных
        program.add_instruction(Opcode.LD, Register.r9, Register.r9)  # теперь в r9 размер
        program.add_instruction(Opcode.LD_LIT, Register.r10, 0)  # счётчик
        while_start: int = program.current_command_address
        program.add_instruction(Opcode.CMP, Register.r9, Register.r10)
        program.add_instruction(Opcode.JE, while_start + 7)
        program.add_instruction(Opcode.INC, Register.r10)
        program.add_instruction(Opcode.LD, Register.r12, Register.r11)
        program.add_instruction(Opcode.PRINT, Register.r12, 0)
        program.add_instruction(Opcode.INC, Register.r11)
        program.add_instruction(Opcode.JUMP, while_start)
    if node.astType == AstNodeType.PRINT_CHAR:
        ast_to_machine_code_math(node.children[0], program)
        program.add_instruction(Opcode.PRINT, Register.r9, 0)


def parse_expression(node: AstNode, program: Program) -> int | None:
    if node.astType == AstNodeType.NAME:
        return program.get_variable_offset(node.value)
    if node.astType == AstNodeType.STRING:
        return program.add_variable_in_static_mem(node.value)
    return ast_to_machine_code_math(node, program)


def main(source, target):
    with open(source, encoding="utf-8") as f:
        source = f.read()
    ast = parse(source)
    write_code(target, ast_to_machine_code(ast))


if __name__ == "__main__":
    assert len(sys.argv) == 3, "Wrong arguments: translator.py <input_file> <target_file>"
    _, source, target = sys.argv
    main(source, target)
