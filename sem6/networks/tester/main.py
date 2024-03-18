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

    def __init__(self, num: int, text: str):
        self.num: int = num
        self.text = text

    @classmethod
    def read(cls, f, i: int):
        if f.readline().strip() != "<a_" + str(i) + ">":
            raise Exception

        ans = ""
        while True:
            line = f.readline().strip()
            if line == "</a_" + str(i) + ">":
                return Answer(i, ans)
            ans += line

    def __str__(self):
        return f"{self.num}: {self.text}"


class Question:
    n: int
    type_: int
    right: int
    max_: int
    value: list[int]
    question: list[str]
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

    def __str__(self):
        return f"Question({self.n}, {self.type_}, {self.right}, {self.question}, {self.description}, {self.answers[0]})"

    @staticmethod
    def read_block(f, name: str) -> list[str]:
        line = f.readline().strip()
        if not line:
            return []
        while line == "<Q_TITLE>":
            while True:
                if f.readline().strip() == "</Q_TITLE>":
                    line = f.readline().strip()
                    break
        if line != "<" + name + ">":
            raise Exception
        inner = []
        while True:
            line = f.readline().strip()
            if line == "</" + name + ">":
                return inner
            inner.append(line)

    @classmethod
    def read(cls, f):
        def read_opt(opt: list[str], name: str, i: int) -> int:
            curr = opt[i].split("=")
            if curr[0] == name:
                return int(curr[1]) if curr[1] != "" else 0
            raise Exception

        options = cls.read_block(f, "options")
        if options == []: return 0
        n = read_opt(options, "n", 0)
        type_ = read_opt(options, "type", 1)
        right = read_opt(options, "right", 2)
        max_ = read_opt(options, "max", 3)

        value = list(map(int, cls.read_block(f, "value")))
        question = cls.read_block(f, "question")
        description = cls.read_block(f, "description")

        answers = []
        for i in range(n):
            answers.append(Answer.read(f, i + 1))

        return Question(n, type_, right, max_, value, question, description, answers)


def main():
    f = open("/home/mopstream/Downloads/Telegram Desktop/rk1.txt", 'r')
    qs = []
    while True:
        q = Question.read(f)
        if q == 0: return
        qs.append(q)
        print(qs[-1])


if __name__ == "__main__":
    main()
