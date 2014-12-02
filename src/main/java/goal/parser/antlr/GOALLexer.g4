lexer grammar GOALLexer;
import CommonTokens;
@members{int bmatch=0,smatch=0;}

// MAIN TOKENS
IMPORT:         '#import';
MODULE:         'module';
INIT:           'init';
MAIN:           'main';
EVENT:          'event';
FOCUS:          'focus';
NONE:           'none';
NEW:            'new';
FILTER:         'filter';
SELECT:         'select';
EXITMODULE:     'exit-module'; // up here because of the next token
EXIT:           'exit';
ALWAYS:         'always';
NEVER:			'never';
NOGOALS:        'nogoals';
NOACTION:       'noaction';
KNOWLEDGE:      'knowledge' -> pushMode(KRBLOCK);
BELIEFS:        'beliefs'   -> pushMode(KRBLOCK);
GOALS:          'goals'     -> pushMode(KRBLOCK);
// PROGRAM TOKENS
PROGRAM:        'program';
ORDER:          'order';
LINEARALL:      'linearall';
LINEAR:         'linear';
RANDOMALL:      'randomall';
RANDOM:         'random';
ADAPTIVE:       'adaptive';
DEFINE:         '#define';
IF:             'if';
FORALL:         'forall';
LISTALL:        'listall';
THEN:           'then';
DO:             'do';
NOT:            'not';
TRUE:           'true';
BELIEF:         'bel'       -> pushMode(KRSTATEMENT);
AGOAL:          'a-goal'    -> pushMode(KRSTATEMENT);
GOALA:          'goal-a'    -> pushMode(KRSTATEMENT);
GOAL:           'goal'      -> pushMode(KRSTATEMENT);
ADOPT:          'adopt'     -> pushMode(KRSTATEMENT);
DROP:           'drop'      -> pushMode(KRSTATEMENT);
INSERT:         'insert'    -> pushMode(KRSTATEMENT);
DELETE:         'delete'    -> pushMode(KRSTATEMENT);
LOG:            'log'       -> pushMode(KRSTATEMENT);
PRINT:			'print'     -> pushMode(KRSTATEMENT);
SENDONCE:       'sendonce'  -> pushMode(KRSTATEMENT);
SEND:           'send'      -> pushMode(KRSTATEMENT);
ALLOTHER:       'allother';
ALL:            'all';
SOMEOTHER:      'someother';
SOME:           'some';
SELF:           'self';
THIS:           'this';
// ACTIONSPEC TOKENS
ACTIONSPEC:     'actionspec';
ENVIRONMENTAL:  '@env';
INTERNAL:       '@int';
PRE:            'pre'       -> pushMode(KRBLOCK);
POST:           'post'      -> pushMode(KRBLOCK);
// GENERAL TOKENS
ID:             (ALPHA | SCORE) (ALPHA | DIGIT | SCORE)* 
				{ 
					int IDi=1; // 'hack' for KR parameters
				   	while(true){
				   		final char next = (char)_input.LA(IDi);
				  	 	if(!java.lang.Character.isWhitespace(next)){
				  		 	if(next=='(') pushMode(KRSTATEMENT);
				  		 	break;
				  	 	}
				  	 	IDi++;
				   	}
				 };

mode KRBLOCK;
KR_CLBR:        WS? CLBR 
				{
					setType(CLBR);
				   	bmatch++;
				   	if(bmatch>1) more();
				};
KR_CRBR:        CRBR WS? 
				{ 
					setType(CRBR);
				   	bmatch--;
				   	if(bmatch==0) popMode();
				   	if(bmatch>=1) more();
				};
KR_BLOCK:       .;

mode KRSTATEMENT;
KR_LBR:         WS? LBR 
				{ 
					setType(LBR);
				   	smatch++;
				   	if(smatch>1) more();
				};
KR_RBR:         RBR WS?
				{ 
					setType(RBR);
				   	smatch--;
				   	if(smatch==0) popMode();
				   	if(smatch>=1) more();
				};
KR_STATEMENT:   .;