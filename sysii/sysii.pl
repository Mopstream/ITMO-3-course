% facts

fraction('Conflux').
fraction('Castle').
fraction('Tower').
fraction('Dungeon').
fraction('Fortress').
fraction('Rampart').
fraction('Inferno').

good('Castle').
good('Tower').
good('Rampart').

evil('Inferno').
evil('Dungeon').


fraction_unit('Pixie', 'Conflux').
fraction_unit('Air elemental', 'Conflux').
fraction_unit('Water elemental', 'Conflux').
fraction_unit('Fire elemental', 'Conflux').
fraction_unit('Earth elemental', 'Conflux').
fraction_unit('Psychic elemental', 'Conflux').
fraction_unit('Firebird', 'Conflux').

fraction_unit('Pikeman', 'Castle').
fraction_unit('Archer', 'Castle').
fraction_unit('Griffin', 'Castle').
fraction_unit('Swordsman', 'Castle').
fraction_unit('Monk', 'Castle').
fraction_unit('Cavalier', 'Castle').
fraction_unit('Angel', 'Castle').

fraction_unit('Gremlin', 'Tower').
fraction_unit('Stone gargoyle', 'Tower').
fraction_unit('Stone golem', 'Tower').
fraction_unit('Mage', 'Tower').
fraction_unit('Genie', 'Tower').
fraction_unit('Naga', 'Tower').
fraction_unit('Giant', 'Tower').

fraction_unit('Troglodyte', 'Dungeon').
fraction_unit('Harpy', 'Dungeon').
fraction_unit('Beholder', 'Dungeon').
fraction_unit('Medusa', 'Dungeon').
fraction_unit('Minotaur', 'Dungeon').
fraction_unit('Manticore', 'Dungeon').
fraction_unit('Red dragon', 'Dungeon').

fraction_unit('Gnoll', 'Fortress').
fraction_unit('Lizardman', 'Fortress').
fraction_unit('Serpent fly', 'Fortress').
fraction_unit('Basilisk', 'Fortress').
fraction_unit('Gorgon', 'Fortress').
fraction_unit('Wyvern', 'Fortress').
fraction_unit('Hydra', 'Fortress').

fraction_unit('Centaur', 'Rampart').
fraction_unit('Dwarf', 'Rampart').
fraction_unit('Wood elf', 'Rampart').
fraction_unit('Pegasus', 'Rampart').
fraction_unit('Dendroid guard', 'Rampart').
fraction_unit('Unicorn', 'Rampart').
fraction_unit('Green dragon', 'Rampart').

fraction_unit('Imp', 'Inferno').
fraction_unit('Gog', 'Inferno').
fraction_unit('Hell hound', 'Inferno').
fraction_unit('Demon', 'Inferno').
fraction_unit('Pit fiend', 'Inferno').
fraction_unit('Efreet', 'Inferno').
fraction_unit('Devil', 'Inferno').


same_fraction_unit(X, X).
same_fraction_unit(X, Y) :- fraction_unit(X, Z), fraction_unit(Y, Z).

enemies(X, Y, fact) :- good(X), evil(Y).
enemies(Y, X) :- enemies(X, Y, fact).

neutral(X) :- not(good(X)), not(evil(X)), fraction(X).

enemies_unit(X,Y) :- fraction_unit(X, Xfrac), fraction_unit(Y, Yfrac), enemies(Xfrac, Yfrac).

good_unit(X) :- fraction_unit(X, Xfrac), good(Xfrac).
evil_unit(X) :- fraction_unit(X, Xfrac), evil(Xfrac).
neutral_unit(X) :- fraction_unit(X, Xfrac), neutral(Xfrac).

