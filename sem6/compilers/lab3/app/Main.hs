module Main (main) where

import Data.Map.Strict as Map

data Symb = Term Char | Eps | END | NONTerm String
instance Show Symb where
    show (Term a) = [a]
    show (NONTerm s) = s
    show _ = ""

instance Eq Symb where
    (==) (Term a) (Term b) = a == b
    (==) (NONTerm a) (NONTerm b) = a == b
    (==) END END = True
    (==) Eps Eps = True
    (==) _ _ = False
    

instance Ord Symb where
    (<=) (Term a) (Term b) = a <= b
    (<=) (NONTerm a) (NONTerm b) = a <= b
    (<=) (Term a) (NONTerm b) = [a] <= b
    (<=) (NONTerm a) (Term b) = a <= [b]
    (<=) END _ = True
    (<=) _ END = False
    (<=) Eps _ = True
    (<=) _ Eps = False


data Production  = (:-->) Symb [Symb]

instance Show Production where
    show (left :--> right) = (show left) ++ " --> " ++ (show right)


table :: Map.Map Symb (Map.Map Symb Production)
table = Map.fromList [
    (NONTerm "S", Map.fromList [
            (Term 'a', NONTerm "S" :--> [Term 'a', NONTerm "S'"])
            ]),
            
    (NONTerm "S'", Map.fromList [
            (Term 'a', NONTerm "S'" :--> [Term 'a', NONTerm "C", NONTerm"B"]),
            (Term 'b', NONTerm "S'" :--> [Term 'b',NONTerm "A"]),
            (Term 'c', NONTerm "S'" :--> [Term 'c',NONTerm "A", NONTerm "B"])
            ]),
            
    (NONTerm "A", Map.fromList [
            (Term 'a', NONTerm "A" :--> [Term 'a',NONTerm "A''"])
            ]),
            
    (NONTerm "A''", Map.fromList [
            (Term 'a', NONTerm "A''" :--> [NONTerm "A"]),
            (Term 'b', NONTerm "A''" :--> [Eps]),
            (Eps,      NONTerm "A''" :--> [Eps]),
            (END,      NONTerm "A''" :--> [Eps])
            ]),
            
    (NONTerm "B", Map.fromList [
            (Term 'b', NONTerm "B" :--> [Term 'b',NONTerm "B''"])
            ]),
            
    (NONTerm "B''", Map.fromList [
            (Term 'b', NONTerm "B''" :--> [NONTerm "B"]),
            (Eps,      NONTerm "B''" :--> [Eps]),
            (END,      NONTerm "B''" :--> [Eps])
            ]),
            
    (NONTerm "C", Map.fromList [
            (Term 'c', NONTerm "C" :--> [Term 'c',NONTerm "C''"])
            ]),
            
    (NONTerm "C''", Map.fromList [
            (Term 'b', NONTerm "C''" :--> [Eps]),
            (Term 'c', NONTerm "C''" :--> [NONTerm "C"]),
            (Eps,      NONTerm "C''" :--> [Eps])
            ])
    
    ]


main :: IO ()
main = do
    s <- getLine
    print $ analys $ (Prelude.map (Term) s) ++ [END]


analys :: [Symb] -> Bool
analys = analys' [NONTerm "S", END]

analys' :: [Symb] -> [Symb] -> Bool
analys' (END : []) (s : []) = if (END == s) then True else False
analys' ((Term x) : xs) ((Term s) : rest) = if (x == s) then analys' xs rest else False
analys' (x : xs) str@(s : _) = (help x s) where
    
    help :: Symb -> Symb -> Bool
    help term symb = case prod of
        Just (_ :--> right) -> analys' terms str where
            terms = if (right == [Eps]) then xs else right ++ xs
        Nothing -> False
        where
            row :: Maybe (Map.Map Symb Production)
            row = table !? term
    
            prod :: Maybe Production
            prod = case row of
                Just mapa -> mapa !? symb
                Nothing  -> Nothing
analys' _ _ = False

