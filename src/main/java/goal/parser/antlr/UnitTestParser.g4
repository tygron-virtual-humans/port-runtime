parser grammar UnitTestParser;
import GOALParser;
options{tokenVocab=UnitTestLexer;}

unitTest:
		UNITTEST CLBR
        	masFile?
            timeout?
            agentTests
		CRBR
	;

masFile:
		MAS EQUALS DOUBLESTRING DOT	
	;

timeout:
		TIMEOUT EQUALS INT DOT
	;

agentTests:
		agentTest*
	;

agentTest:
		ID CLBR // Must be name of agent in MAS file launch rule
        	test*
		CRBR
	;

test:
		ID CLBR // Name of a test that is executed for an agent.
        	testSection*
		CRBR
	;

testSection:
		(doActions | assertTest | evaluateIn) DOT
	;

doActions:
		DO actions
	;

assertTest:
		ASSERT conditions (COLON (SINGLESTRING|DOUBLESTRING))?
	;

evaluateIn:
		EVALUATE CLBR
        	testCondition*
	 	CRBR IN doActions testBoundary?
	;
testCondition:
		ltl (LTRARROW ltl)? DOT
	;
ltl:
		(ltlAtStart | ltlAlways | ltlNever | ltlEventually | ltlAtEnd)
	;
ltlAtStart:
		ATSTART ltlModule? conditions
	;
ltlAlways:
		ALWAYS conditions
	;
ltlNever:
		NEVER conditions
	;
ltlEventually:
		EVENTUALLY conditions
	;
ltlAtEnd:
		ATEND ltlModule? conditions
	;
ltlModule:
		SLBR (function | INIT | MAIN | EVENT) SRBR
	;

testBoundary:
		(ltlUntil | ltlWhile)
	;
ltlUntil:
		UNTIL conditions
	;
ltlWhile:
		WHILE conditions
	;