parser grammar MASParser;
options{tokenVocab=MASLexer;}

mas:
                environment?
                agentFiles
                launchPolicy
                EOF
            ;
// ENVIRONMENT
environment:
                ENVSECTION CLBR
                    environmentFile
                    (INIT EQUALS SLBR initParams SRBR DOT)?
                CRBR
            ;
environmentFile:
                ENV EQUALS DOUBLESTRING DOT
            ;
initParams:
                initParam (COMMA initParam)*
            ;
initParam:
                ID EQUALS initValue
            ;
initValues:
                initValue (COMMA initValue)*
            ;
initValue: 
                simpleInitValue | functionInitValue | listInitValue
            ;
simpleInitValue:
                ID | INT | FLOAT | SINGLESTRING | DOUBLESTRING
            ;
functionInitValue:
                ID LBR initValues RBR
            ;
listInitValue:
                SLBR initValues SRBR
            ;
// AGENTFILES
agentFiles:
                AGENTSECTION CLBR
                    agentFile*
                CRBR
            ;
agentFile:
                DOUBLESTRING (SLBR agentFileParameters SRBR)? DOT
            ;
agentFileParameters:
                agentFileParameter (COMMA agentFileParameter)*
            ;
agentFileParameter:
                (NAME | LANGUAGE) EQUALS ID
            ;
// LAUNCHPOLICY
launchPolicy:
                LAUNCHSECTION CLBR
                    launchRule*
                CRBR
            ;
launchRule:
                simpleLaunchRule | conditionalLaunchRule
            ;
simpleLaunchRule:
                LAUNCH launchRuleComponents DOT
            ;
launchRuleComponents:
                launchRuleComponent (COMMA launchRuleComponent)*
            ;
launchRuleComponent:
                (((WILDCARD | ID) (SLBR INT SRBR)? AGENTFILENAME) | ID (SLBR INT SRBR)?)
            ;
conditionalLaunchRule:
                WHEN entityDescription ATENV DO simpleLaunchRule
            ;
entityDescription:
                ENTITY | (SLBR entityConstraints SRBR)
            ;
entityConstraints:
                entityConstraint (COMMA entityConstraint)*
            ;
entityConstraint:
                ((NAME | TYPE) EQUALS ID) | (MAX EQUALS INT)
            ;