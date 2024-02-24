from __future__ import annotations

import logging
import sys
from enum import Enum

from machine.isa import Opcode, Register, Word, dr, pc, read_code, sp


class Alu:
    neg: bool = False
    zero: bool = False

    def __init__(self):
        self.acc = 0

    min_value = -(2**32)
    max_value = 2**32 - 1

    def set_flags(self, value: int):
        if value > self.max_value:
            value = value & self.min_value
        if value < self.min_value:
            value = value & self.min_value
        self.neg = value < 0
        self.zero = value == 0

    def execute(self, opcode: Opcode, arg1: int, arg2: int = 0) -> int:
        res: int
        operations = {
            Opcode.ADD: lambda a, b: a + b,
            Opcode.ADD_LIT: lambda a, b: a + b,
            Opcode.INC: lambda a, b: a + 1,
            Opcode.DEC: lambda a, b: a - 1,
            Opcode.SHR: lambda a, b: a >> b,
            Opcode.SHL: lambda a, b: 0 if a == 0 else a << b,
            Opcode.XOR: lambda a, b: a ^ b,
            Opcode.AND: lambda a, b: a & b,
            Opcode.OR: lambda a, b: a | b,
            Opcode.NEG: lambda a, b: ~a,
        }
        operation = operations.get(opcode, None)
        if operation is not None:
            res = operation(arg1, arg2)
        else:
            raise ValueError("unknown opcode: {}".format(opcode))
        self.set_flags(res)
        return res


class DataPath:
    registers: dict[Register, int]

    mem_size: int

    memory: list[Word]

    input_ports: dict[int, list[str]]

    output_ports: dict[int, list[str]]

    alu: Alu = None

    def __init__(self, data: list[Word], ports: dict[int, list[str]], mem_size: int = 4096):
        self.registers = {}
        self.output_ports = {}
        self.mem_size = mem_size
        self.memory = data
        self.alu = Alu()
        self.input_ports = ports
        self.output_ports[0] = []
        for reg_num in range(0, 15):
            self.registers[Register(reg_num)] = 0
        self.registers[sp] = self.mem_size - 1

    def latch_reg(self, reg: Register, value: int):
        self.registers[reg] = value

    def load_reg(self, reg: Register) -> int:
        return self.registers[reg]

    def get_instruction(self, addr: int) -> Word:
        return self.memory[addr]

    def memory_perform(self, oe: bool, wr: bool, addr: int) -> Word | None:
        instr: Word = self.get_instruction(addr)
        if oe:
            return instr
        if wr:
            instr.arg1 = self.registers[dr]
        return None

    def perform_arithmetic(self, opcode: Opcode, arg1: int, arg2: int = 0) -> int:
        return self.alu.execute(opcode, arg1, arg2)

    def zero(self) -> bool:
        return self.alu.zero

    def neg(self) -> bool:
        return self.alu.neg

    def pick_char(self, port: int) -> int:
        if len(self.input_ports[port]) == 0:
            return 0
        return ord(self.input_ports[port].pop(0))

    def put_char(self, char: int, port: int):
        self.output_ports[port].append(chr(char))


class ControlUnit:
    data_path: DataPath = None

    tick_cnt: int = 0

    def __init__(self, data_path):
        self.tick_cnt = 0
        self.data_path = data_path

    def tick(self):
        self.tick_cnt += 1

    def current_tick(self):
        return self.tick_cnt

    def decode_and_execute_control_flow_instruction(self, instr, opcode) -> bool:
        if opcode is Opcode.HALT:
            raise StopIteration()

        if opcode is Opcode.JUMP:
            addr = instr.arg1
            self.data_path.latch_reg(pc, addr)
            self.tick()
            return True

        jmp_flag: bool = False

        if (
            opcode is Opcode.JE
            or opcode is Opcode.JNE
            or opcode is Opcode.JL
            or opcode is Opcode.JLE
            or opcode is Opcode.JG
            or opcode is Opcode.JGE
        ):
            match opcode:
                case Opcode.JE:
                    jmp_flag = self.data_path.zero() == 1
                case Opcode.JNE:
                    jmp_flag = self.data_path.zero() == 0
                case Opcode.JG:
                    jmp_flag = self.data_path.neg() == 0
                case Opcode.JGE:
                    jmp_flag = self.data_path.neg() == 0 or self.data_path.zero() == 1
                case Opcode.JL:
                    jmp_flag = self.data_path.neg() == 1
                case Opcode.JLE:
                    jmp_flag = self.data_path.neg() == 1 or self.data_path.zero() == 1
            if jmp_flag:
                self.data_path.latch_reg(pc, instr.arg1)
            else:
                self.data_path.latch_reg(pc, self.data_path.registers[pc] + 1)
            self.tick()
            return True
        return False

    def fetch_instruction(self) -> Word:
        addr: int = self.data_path.registers[pc]
        instr: Word = self.data_path.memory[addr]
        self.data_path.latch_reg(dr, instr.arg1)
        self.tick()
        return instr

    def ld_addr(self, instr: Word):
        addr: int = instr.arg2
        reg: Register = instr.arg1
        data: Word = self.data_path.memory_perform(True, False, addr)
        self.data_path.latch_reg(dr, data.arg1)
        self.tick()
        self.data_path.latch_reg(reg, data.arg1)
        self.tick()

    def ld_lit(self, instr: Word):
        val: int = instr.arg2
        reg: Register = instr.arg1
        self.data_path.latch_reg(reg, val)
        self.tick()

    def ld(self, instr: Word):
        reg_to: Register = instr.arg1
        addr_reg: Register = instr.arg2
        addr_to_read: int = self.data_path.perform_arithmetic(Opcode.ADD, 0, self.data_path.registers[addr_reg])
        self.tick()
        data: Word = self.data_path.memory_perform(True, False, addr_to_read)
        self.data_path.latch_reg(dr, data.arg1)
        self.tick()
        self.data_path.latch_reg(reg_to, data.arg1)
        self.tick()

    def ld_stack(self, instr: Word):
        reg_to: Register = instr.arg1
        addr: int = self.data_path.mem_size - instr.arg2 - 1
        self.tick()
        data: Word = self.data_path.memory_perform(True, False, addr)
        self.data_path.latch_reg(dr, data.arg1)
        self.tick()
        self.data_path.latch_reg(reg_to, data.arg1)
        self.tick()

    def st_addr(self, instr: Word):
        addr: int = instr.arg2
        reg: Register = instr.arg1
        reg_data: int = self.data_path.perform_arithmetic(Opcode.ADD, 0, self.data_path.registers[reg])
        self.tick()
        self.data_path.latch_reg(dr, reg_data)
        self.tick()
        self.data_path.memory_perform(False, True, addr)
        self.tick()

    def st(self, instr: Word):
        data_reg: Register = instr.arg1
        data: int = self.data_path.perform_arithmetic(Opcode.ADD, 0, self.data_path.registers[data_reg])
        self.tick()
        self.data_path.latch_reg(dr, data)
        self.tick()
        addr_reg: Register = instr.arg2
        addr: int = self.data_path.perform_arithmetic(Opcode.ADD, 0, self.data_path.registers[addr_reg])
        self.tick()
        self.data_path.memory_perform(False, True, addr)
        self.tick()

    def st_stack(self, instr: Word):
        reg_from: Register = instr.arg1
        data: int = self.data_path.perform_arithmetic(Opcode.ADD, 0, self.data_path.registers[reg_from])
        self.data_path.latch_reg(dr, data)
        self.tick()
        addr_to: int = self.data_path.mem_size - instr.arg2 - 1
        self.tick()
        self.data_path.memory_perform(False, True, addr_to)
        self.tick()

    def mv(self, instr: Word):
        reg_from: Register = instr.arg1
        reg_from_data: int = self.data_path.perform_arithmetic(Opcode.ADD, 0, self.data_path.registers[reg_from])
        self.tick()
        reg_to: Register = instr.arg2
        self.data_path.latch_reg(reg_to, reg_from_data)
        self.tick()

    def read(self, instr: Word):
        reg: Register = instr.arg1
        port: int = instr.arg2
        data: int = self.data_path.pick_char(port)
        self.data_path.latch_reg(reg, data)
        self.tick()

    def print_symbol(self, instr: Word):
        reg: Register = instr.arg1
        data: int = self.data_path.perform_arithmetic(Opcode.ADD, 0, self.data_path.registers[reg])
        self.tick()
        port: int = instr.arg2
        self.data_path.put_char(data, port)
        self.tick()

    def arythm(self, instr: Word):
        res: int = self.data_path.perform_arithmetic(
            instr.opcode, self.data_path.load_reg(instr.arg1), self.data_path.load_reg(instr.arg2)
        )
        self.data_path.latch_reg(instr.arg1, res)
        self.tick()

    def add_lit(self, instr: Word):
        res: int = self.data_path.perform_arithmetic(instr.opcode, self.data_path.load_reg(instr.arg1), instr.arg2)
        self.data_path.latch_reg(instr.arg1, res)
        self.tick()

    def push(self, instr: Word):
        data_reg: Register = instr.arg1
        data: int = self.data_path.perform_arithmetic(Opcode.ADD, 0, self.data_path.registers[data_reg])
        self.data_path.latch_reg(dr, data)
        self.tick()
        addr_reg: Register = sp
        addr: int = self.data_path.perform_arithmetic(Opcode.ADD, 0, self.data_path.registers[addr_reg])
        self.data_path.memory_perform(False, True, addr)
        self.tick()
        res: int = self.data_path.perform_arithmetic(Opcode.DEC, self.data_path.load_reg(sp))
        self.data_path.latch_reg(sp, res)
        self.tick()

    def pop(self, instr: Word):
        res: int = self.data_path.perform_arithmetic(Opcode.INC, self.data_path.load_reg(sp))
        self.data_path.latch_reg(sp, res)
        self.tick()
        addr: int = self.data_path.perform_arithmetic(Opcode.ADD, self.data_path.registers[sp], 0)
        self.tick()
        data: Word = self.data_path.memory_perform(True, False, addr)
        self.data_path.latch_reg(dr, data.arg1)
        self.tick()
        self.data_path.latch_reg(instr.arg1, data.arg1)
        self.tick()

    def cmp(self, instr: Word):
        inv = self.data_path.perform_arithmetic(Opcode.NEG, self.data_path.load_reg(instr.arg2))
        self.tick()
        inv = self.data_path.perform_arithmetic(Opcode.INC, inv)
        self.tick()
        self.data_path.perform_arithmetic(Opcode.ADD, self.data_path.load_reg(instr.arg1), inv)
        self.tick()

    def sub(self, instr: Word):
        inv = self.data_path.perform_arithmetic(Opcode.NEG, self.data_path.load_reg(instr.arg2))
        self.tick()
        inv = self.data_path.perform_arithmetic(Opcode.INC, inv)
        self.tick()
        res = self.data_path.perform_arithmetic(Opcode.ADD, self.data_path.load_reg(instr.arg1), inv)
        self.data_path.latch_reg(instr.arg1, res)
        self.tick()

    def unary_arythm(self, instr: Word):
        res: int = self.data_path.perform_arithmetic(instr.opcode, self.data_path.load_reg(instr.arg1))
        self.data_path.latch_reg(instr.arg1, res)
        self.tick()

    def decode_and_execute_instruction(self):
        instr: Word = self.fetch_instruction()
        opcode: Opcode = instr.opcode
        opcode_mapping = {
            Opcode.LD_ADDR: self.ld_addr,
            Opcode.LD_LIT: self.ld_lit,
            Opcode.LD: self.ld,
            Opcode.LD_STACK: self.ld_stack,
            Opcode.ST_ADDR: self.st_addr,
            Opcode.ST: self.st,
            Opcode.ST_STACK: self.st_stack,
            Opcode.MV: self.mv,
            Opcode.READ: self.read,
            Opcode.PRINT: self.print_symbol,
            Opcode.ADD: self.arythm,
            Opcode.OR: self.arythm,
            Opcode.AND: self.arythm,
            Opcode.SHL: self.arythm,
            Opcode.SHR: self.arythm,
            Opcode.XOR: self.arythm,
            Opcode.INC: self.unary_arythm,
            Opcode.DEC: self.unary_arythm,
            Opcode.ADD_LIT: self.add_lit,
            Opcode.CMP: self.cmp,
            Opcode.SUB: self.sub,
            Opcode.PUSH: self.push,
            Opcode.POP: self.pop,
            Opcode.NEG: self.unary_arythm,
        }
        if self.decode_and_execute_control_flow_instruction(instr, opcode):
            return
        if opcode in opcode_mapping:
            opcode_mapping[opcode](instr)

        self.data_path.latch_reg(pc, self.data_path.registers[pc] + 1)
        self.tick()

    def print_val_if_enum(self, value):
        if isinstance(value, Enum):
            return value.name
        return value

    def __repr__(self):
        formatted_registers = {f"r{register.value}": value for register, value in self.data_path.registers.items()}
        formatted_string = ", ".join([f"'{key}': {value}" for key, value in formatted_registers.items()])

        state_repr = "TICK: {:3} PC: {:3}  MEM_OUT: {} {} reg: {}".format(
            self.tick_cnt,
            self.data_path.registers[pc],
            self.print_val_if_enum(self.data_path.memory[self.data_path.registers[pc]].arg1),
            self.print_val_if_enum(self.data_path.memory[self.data_path.registers[pc]].arg2),
            formatted_string,
        )

        instr = self.data_path.memory[self.data_path.registers[pc]]
        opcode = str(instr.opcode)

        instr_repr = "  ('{}'@{}:{} {})".format(instr.index, opcode, instr.arg1, instr.arg2)

        return "{} \t{}".format(state_repr, instr_repr)


def simulation(mem: list[Word], input_tokens: list[str], limit: int):
    ports: dict[int, list[str]] = {}
    ports[0] = input_tokens
    data_path = DataPath(mem, ports)
    control_unit = ControlUnit(data_path)
    instr_counter = 0

    logging.debug("%s", control_unit)
    try:
        while instr_counter < limit:
            control_unit.decode_and_execute_instruction()
            instr_counter += 1
            logging.debug("%s", control_unit)
    except EOFError:
        logging.warning("Input buffer is empty!")
    except StopIteration:
        pass

    if instr_counter >= limit:
        logging.warning("Limit exceeded!")
    logging.info("output_buffer: %s", repr("".join(data_path.output_ports[0])))
    return "".join(data_path.output_ports[0]), instr_counter, control_unit.current_tick()


def main(code_file, input_file):
    code: list[Word] = read_code(code_file)
    with open(input_file, encoding="utf-8") as file:
        input_text = file.read()
        input_token = []
        for char in input_text:
            input_token.append(char)

    output, instr_counter, ticks = simulation(
        code,
        input_tokens=input_token,
        limit=100000,
    )

    print("".join(output))
    print("instr_counter: ", instr_counter, "ticks:", ticks)


if __name__ == "__main__":
    logging.getLogger().setLevel(logging.DEBUG)
    assert len(sys.argv) == 3, "Wrong arguments: emulator.py <code_file> <input_file>"
    _, code_file, input_file = sys.argv
    main(code_file, input_file)
