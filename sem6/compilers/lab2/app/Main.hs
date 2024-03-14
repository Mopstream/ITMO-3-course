module Main (main) where

data State = S1 | S2 | S3 | S4 | S5 deriving Eq

start :: State
start = S1

finish :: State
finish = S5


main :: IO ()
main = do
    s <- getLine
    print $ dfa start s 


dfa :: State -> String -> Bool
dfa state [] = if (state == finish) then True else False
dfa state s@(x : rest) = dfa newState rest where
    newState = case (state, x) of
        (S1, 'a') -> S2
        (S1, 'b') -> S3
        (S1, 'c') -> S4
        (S2, 'b') -> S1
        (S3, 'c') -> S1
        (S4, 'b') -> S5
        _         -> error $ "Unexpected input " ++ (x : " in ") ++ s
        



