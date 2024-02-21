from pyswip import Prolog

prolog = Prolog()
prolog.consult("../sysii.pl")

good_units = set(map(lambda x: x['X'], list(prolog.query("good_unit(X)"))))
evil_units = set(map(lambda x: x['X'], list(prolog.query("evil_unit(X)"))))
neutral_units = set(map(lambda x: x['X'], list(prolog.query("neutral_unit(X)"))))


def query(msg: str) -> bool:
    return list(prolog.query(msg))


def validate_input(units: list[str]) -> set[str]:
    val = set()
    for u in units:
        if not query(f"fraction_unit('{u}', X)"):
            print(f"{u} is not a unit, check your input")
        else:
            val.add(u)
    return val


def correct(units: set[str]) -> (set[str], set[str], set[str]):
    good = set()
    evil = set()
    neutral = set()
    for u in units:
        if query(f"good_unit('{u}')"):
            good.add(u)
        elif query(f"evil_unit('{u}')"):
            evil.add(u)
        else:
            neutral.add(u)
    if len(good) > len(evil):
        to_replace = evil
        side = good_units
    elif len(good) < len(evil):
        to_replace = good
        side = evil_units
    else:
        to_replace = set()
        side = set()
    return to_replace, side, neutral


print("Enter your army (units separated by a space) to correcting: ", end="")
units = input().split()
validated = validate_input(units)

to_replace, side, neutral = correct(validated)
if len(to_replace) > 0:
    print(f"You must replace {to_replace} by {side}, alco you can replace some of {neutral} by that units")
elif len(side) > 0 and len(neutral) > 0:
    print(f"You can replace {neutral} by {side}")
else:
    print("Congratulations, you have a friendly army without some conflicts of interests, you don't need to do anything")
