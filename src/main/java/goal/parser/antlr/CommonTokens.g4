lexer grammar CommonTokens;

fragment ALPHA: [a-zA-Z];
fragment SCORE: '_';
FLOAT:          (PLUS | MINUS)? (DIGITS DOT DIGITS) | (DOT DIGITS);
INT:            (PLUS | MINUS)? DIGITS;
fragment DIGITS:DIGIT+;
fragment DIGIT: [0-9];
COLON:			':';
PLUS:           '+';
MINUS:          '-';
EQUALS:         '=';
DOT:            '.';
COMMA:          ',';
LBR:            '(';
RBR:            ')';
CLBR:           '{';
CRBR:           '}';
SLBR:           '[';
SRBR:           ']';
RTLARROW:       '<-';
LTRARROW:       '->';
SINGLESTRING:	('\'' ('\\\'' | .)*? '\'');
DOUBLESTRING:   ('"' ('\\"' | .)*? '"');
// SPECIAL TOKENS
LINE_COMMENT:   '%' ~[\r\n]* '\r'? '\n' -> channel(HIDDEN);
BLOCK_COMMENT:  '/*' .*? '*/' -> channel(HIDDEN);
WS:             [ \t\f\r\n]+ -> channel(HIDDEN);