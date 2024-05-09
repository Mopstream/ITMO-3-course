terms = ["a", "b", "c", "eps", "END"]
nonTerms = ["S", "S'", "A", "A''", "B", "B''", "C", "C''"]

table = {
    "S": {
        "a": ["a", "S'"]
    },
    "S'": {
        "a": ["a", "C", "B"],
        "b": ["b", "A"],
        "c": ["c", "A", "B"],
    },
    "A": {
        "a": ["a", "A''"]
    },
    "A''": {
        "a": ["A"],
        "b": ["eps"],
        "END": ["eps"]
    },
    "B": {
        "b": ["b", "B''"]
    },
    "B''": {
        "b": ["B"],
        "END": ["eps"]
    },
    "C": {
        "c": ["c", "C''"]
    },
    "C''": {
        "b": ["eps"],
        "c": ["C"]
    },
}

def analys(stack, str):
    if (stack == str and str == ["END"]): return True
    while (not (stack[-1] == "END")):
        X = stack[-1]
        if (X in terms or X == "END"):
            if (X == str[0]):
                return analys(stack[:-1], str[1:])
            else: return False
        else:
            if X in table.keys():
                if str[0] in table[X].keys():
                    rule = table[X][str[0]]
                    if (rule == ["eps"]):
                        return analys(stack[:-1], str)
                    else:
                        stack = stack[:-1] + rule[::-1]
                        return analys(stack, str)
            return False

str = list(input())
str.append("END")
print(analys(["END", "S"], str))