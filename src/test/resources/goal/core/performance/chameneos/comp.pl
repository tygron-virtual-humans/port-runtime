complement(blue,   red,    yellow).
complement(blue,   yellow, red).
complement(red,    blue,   yellow).
complement(red,    yellow, blue).
complement(yellow, blue,   red).
complement(yellow, red,    blue).
complement(C, C, C).

spell(N, L) :- N < 10, nth0(N,[' zero', ' one', ' two', ' three', ' four', ' five', ' six', ' seven', ' eight', ' nine'],L).
spell(N, L) :- N >= 10, K is N mod 10, Y is N // 10, spell(Y, L1), spell(K, L2), string_concat(L1, L2, L).
