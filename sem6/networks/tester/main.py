"""
1 quiz
2 mult
3 order
6 connect
7 input
"""

from random import randint, shuffle
from PIL import Image
import os


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
        if not options:
            return 0
        n = read_opt(options, "n", 0)
        type_ = read_opt(options, "type", 1)
        right = read_opt(options, "right", 2)
        max_ = read_opt(options, "max", 3)

        value = list(map(int, cls.read_block(f, "value")))
        question = " ".join(cls.read_block(f, "question"))
        description = cls.read_block(f, "description")

        answers = []
        for i in range(n):
            answers.append(Answer.read(f, i + 1))

        return Question(n, type_, right, max_, value, question, description, answers)


def ask(questions):
    ind = randint(0, len(questions) - 1)
    print(f"{ind}: ", end=" ")
    q: Question = questions[ind]

    if "<img" in q.question:
        src = q.question[q.question.find("src='") + 5: q.question.find("'>")].replace("\\", "/")
        src = "/home/mopstream/Downloads/Telegram Desktop/!_КС_Тест1/" + src
        if not os.path.exists(src):
            src = src.replace("JPG", "jpg").replace("PNG", "png")
        if not os.path.exists(src):
            src = src.replace("jpg", "JPG").replace("png", "PNG")
        image = Image.open(src)
        image.show()

    print(q.question)
    new_ans = q.answers.copy()
    shuffle(new_ans)

    if q.type_ == 1:
        for i in range(len(new_ans)):
            print(i, new_ans[i].text)
        print("Введите число - номер ответа")

        i = input()
        if i:
            i = int(i)
            if new_ans[i].num == 1:
                print("вполне неплохо для долбаеба")
                return
        print("вывести правильный ответ? (y/n)")
        if input() == "y":
            print(q.answers[0].text)
    elif q.type_ == 2:
        for i in range(len(new_ans)):
            print(i, new_ans[i].text)
        print("Введите через пробел номера правильных вариантов")

        ans = list(map(int, input().split()))
        if (len(ans) == q.right) and all(new_ans[x].num <= q.right for x in ans):
            print("вполне неплохо для долбаеба")
        else:
            print("вывести правильный ответ? (y/n)")
            if input() == "y":
                for i in range(q.right):
                    print(q.answers[i].text)
    elif q.type_ == 3:
        for i in range(len(new_ans)):
            print(i, new_ans[i].text)
        print("Введите через пробел номера ответов в правильном порядке")

        ans = list(map(int, input().split()))
        if (len(ans) == len(q.answers)) and all(
                new_ans[ans[x]].text == q.answers[x].text for x in range(len(q.answers))):
            print("вполне неплохо для долбаеба")
        else:
            print("вывести правильный ответ? (y/n)")
            if input() == "y":
                for i in range(len(q.answers)):
                    print(q.answers[i].text)
    elif q.type_ == 6:
        new_ans = list(map(lambda x: x.text.split(":::"), new_ans))
        fi = list(map(lambda x: x[0], new_ans))
        se = list(map(lambda x: x[1], new_ans))
        shuffle(fi)

        for i in range(len(new_ans)):
            print(i, fi[i], '\t', chr(ord('a') + i), se[i])

        print("Введите пары номер-буква через пробел, разделяя запятыми")

        ans = list(map(lambda x: x.split(), input().split(",")))

        if (len(ans) == len(new_ans)) and all(len(x) == 2 for x in ans) and all(
                [fi[int(ans[i][0])], se[ord(ans[i][1]) - ord('a')]] in new_ans for i in range(len(ans))):
            print("вполне неплохо для долбаеба")
        else:
            print("вывести правильный ответ? (y/n)")
            if input() == "y":
                for i in range(len(q.answers)):
                    print(q.answers[i].text)

    elif q.type_ == 7:
        print("Введите ответ: ")
        ans = input()

        if ans in map(lambda x: x.text, new_ans):
            print("вполне неплохо для долбаеба")
        else:
            print("вывести правильный ответ? (y/n)")
            if input() == "y":
                print(q.answers[0].text)


def main():
    f = open("/home/mopstream/Downloads/Telegram Desktop/rk1.txt", 'r')
    qs = []
    while True:
        q = Question.read(f)
        if q == 0:
            break
        qs.append(q)
        # print(len(qs), qs[-1])

    while True:
        this_qs = qs[0:333] + qs[1168:]
        new_qs = this_qs
        #start = 203  # len(this_qs)
        #for i in range(start - 1, -1, -1):
        #    new_qs = [this_qs[i]]
        ask(new_qs)
        print("#############################\n")


if __name__ == "__main__":
    main()


# -364 -340

# PAM-5 MLT-3 NRZ RZ AMI M2 NRZI

# формулы
# изучить 259 -256

# механизм окна

#методы маршрутизации 153 - 140

#коммутация 139- 118

#LLC
