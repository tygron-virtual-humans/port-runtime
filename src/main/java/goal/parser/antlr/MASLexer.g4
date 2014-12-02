lexer grammar MASLexer;
import CommonTokens;
 
// ENVIRONMENT TOKENS
ENVSECTION:     'environment';
ENV:            'env';
INIT:           'init';
// AGENT TOKENS
AGENTSECTION:   'agentfiles' ;
NAME:           'name'; // used in launch-section as well
LANGUAGE:       'language';
// LAUNCH TOKENS
LAUNCHSECTION:  'launchpolicy';
WHEN:           'when';
ENTITY:         'entity';
ATENV:          '@env';
DO:             'do';
LAUNCH:         'launch';
TYPE:           'type';
MAX:            'max';
WILDCARD:       '*';
AGENTFILENAME:  ':'[ \t]*~[ \t\f\r\n?%*:|"<>.]+;
// GENERAL TOKENS
ID:             (ALPHA | SCORE) (ALPHA | DIGIT | SCORE)*;