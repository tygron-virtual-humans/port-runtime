parser grammar GOALParser;
options{tokenVocab=GOALLexer;}

modules:        (moduleImport | module)+ EOF
            ;

// MAIN
moduleImport:   IMPORT DOUBLESTRING DOT
            ;

module:         moduleDef (SLBR moduleOptions SRBR)? CLBR
                    knowledge?
                    beliefs?
                    goals?
                    program?
                    actionSpecs?
                CRBR
            ;

moduleDef:      (MODULE function) | (INIT MODULE) | (MAIN MODULE) | (EVENT MODULE)
            ;

moduleOptions:  moduleOption (COMMA moduleOption)*
            ;

moduleOption:   exitOption | focusOption
            ;

exitOption:     EXIT EQUALS (ALWAYS | NEVER | NOGOALS | NOACTION)
            ;

focusOption:    FOCUS EQUALS (NONE | NEW | SELECT | FILTER)
            ;

// KNOWLEDGE, BELIEFS, GOALS
knowledge:      KNOWLEDGE CLBR
                    KR_BLOCK*
                CRBR
            ;

beliefs:        BELIEFS CLBR
                    KR_BLOCK*
                CRBR
            ;

goals:          GOALS CLBR
                    KR_BLOCK*
                CRBR
            ;

// ACTIONSPECS
actionSpecs:    ACTIONSPEC CLBR
                    actionSpec*
                CRBR
            ;

actionSpec:     function (INTERNAL | ENVIRONMENTAL)? CLBR
                    actionPre
                    actionPost
                CRBR
            ;

actionPre:      PRE CLBR KR_BLOCK* CRBR
            ;

actionPost:     POST CLBR KR_BLOCK* CRBR
            ;

function:		ID (LBR KR_STATEMENT+ RBR)?
            ;

// PROGRAM
program:        PROGRAM (SLBR orderOption SRBR)? CLBR
                    macro*
                    programRule*
                CRBR
            ;

macro:          DEFINE function conditions DOT
            ;

orderOption:    ORDER EQUALS (LINEAR | LINEARALL | RANDOM | RANDOMALL | ADAPTIVE)
            ;

programRule:    ifRule | forallRule | listallRule
            ;

ifRule:         IF conditions THEN ((actions DOT)|anonModule)
            ;

forallRule:     FORALL conditions DO ((actions DOT)|anonModule)
            ;

listallRule:    LISTALL ((ID RTLARROW conditions) | (conditions LTRARROW ID)) DO ((actions DOT)|anonModule)
            ;

conditions:     condition (COMMA condition)*
            ;

condition:      TRUE | mentalRule | (NOT LBR mentalRule RBR)
            ;

mentalRule:     mentalAction | function
            ;

mentalAction:   (selector DOT)? mentalAtom LBR KR_STATEMENT+ RBR
            ;

mentalAtom:     BELIEF | GOAL | AGOAL | GOALA
            ;

actions:        action (PLUS action)*
            ;

action:         (actionAtom LBR KR_STATEMENT+ RBR) | function | EXITMODULE | INIT | MAIN | EVENT
            ;

actionAtom:     (selector DOT)? (ADOPT | DROP | INSERT | DELETE | SEND | SENDONCE | LOG | PRINT)
            ;

selector:       selectExp | (SLBR selectExp (COMMA selectExp)? SRBR)
            ;

selectExp:      SELF | ALL | ALLOTHER | SOME | SOMEOTHER | THIS | ID
            ;

anonModule:     CLBR programRule+ CRBR
            ;