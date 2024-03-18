"""
1 quiz
2 mult
3 order
6 connect
7 input
"""

class Answer:
    num: int
    text: str
    is_true: bool

    def __init__(self, text: str, is_true: bool):
        self.text = text
        self.is_true: bool = is_true

    @classmethod
    def read(cls, f):


class Question:
    n: int
    type_: int
    right: int
    max_: int
    value: list[int]
    question: str
    description: list[str]
    answers: list[Answer]

    def __init__(self, n, type_, right, max_, value, question, description, answers):
        self.n = n
        self.type_ = type_
        self.right = right
        self.max_ = max_
        self.value = value
        self.question = question
        self.description = description
        self.answers = answers

    @staticmethod
    def read_block(f, name: str) -> list[str]:
        if f.readline() != "<" + name + ">":
            raise Exception
        inner = []
        while True:
            line = f.readline()
            if line != "</" + name + ">":
                return inner
            inner.append(line)

    @classmethod
    def read(cls, f):
        def read_opt(opt: list[str], name: str, i: int) -> int:
            curr = opt[i].split("=")
            if curr[0] == name:
                return int(curr[1])
            raise Exception

        options = cls.read_block(f, "options")
        n = read_opt(options, "n", 0)
        type_ = read_opt(options, "type", 1)
        right = read_opt(options, "right", 2)
        max_ = read_opt(options, "max", 3)

        value = list(map(int, cls.read_block(f, "value")))
        question = cls.read_block(f, "question")[0]
        description = cls.read_block(f, "description")

        answers = []
        for i in range(n):
            answers.append(Answer.read())

        return Question(n, type_, right, max_, value, question, description, answers)


def read_value(f):
    if f.readline() != "<value>":
        raise Exception
    while f.readline() != "</value>": pass


def main():
    f = open("/home/mopstream/Downloads/Telegram Desktop/rk1.txt", 'r')
    qs = []
    while True:
        line = f.readline()
        if not line:
            break
        qs.append(Question.read(f))


if __name__ == "__main__":
    main()
