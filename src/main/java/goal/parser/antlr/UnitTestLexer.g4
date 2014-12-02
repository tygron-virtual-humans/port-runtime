lexer grammar UnitTestLexer;
import CommonTokens,GOALLexer;

// UNIT TEST TOKENS
UNITTEST:		'masTest'
				{ // HACK: disable KR_BLOCKs
					bmatch=Integer.MIN_VALUE;
				};
MAS:			'mas';
TIMEOUT:		'timeout';
// TESTS TOKENS
IN:				'in';
EVALUATE: 		'evaluate';
ASSERT:			'assert';
ATSTART:		'atstart';
EVENTUALLY:		'eventually';
NEVER:			'never';
ATEND:			'atend';
UNTIL:			'until';
WHILE:			'while';

// Lexical modes are not properly imported (see HACK) :(
mode KRBLOCK;
UNIT_KR_CLBR:    WS? CLBR
				 {
				  	setType(CLBR);
				   	bmatch++;
				   	if(bmatch>1) more();
				 };
UNIT_KR_CRBR:    CRBR WS?
				 { 
				 	setType(CRBR);
				   	bmatch--;
				   	if(bmatch==0) popMode();
				   	if(bmatch>=1) more();
				 };
UNIT_KR_BLOCK:   .
				 { 
				 	setType(KR_BLOCK);
				 };

mode KRSTATEMENT;
UNIT_KR_LBR:     WS? LBR
				 {
				 	setType(LBR);
				   	smatch++;
				   	if(smatch>1) more();
				 };
UNIT_KR_RBR:     RBR WS?
				 {
				 	setType(RBR);
				   	smatch--;
				   	if(smatch==0) popMode();
				   	if(smatch>=1) more();
				 };
UNIT_KR_STATEMENT: .
				 {
				 	setType(KR_STATEMENT);
				 };