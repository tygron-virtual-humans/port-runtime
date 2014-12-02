// Generated from MASParser.g4 by ANTLR 4.4
package goal.parser.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class MASParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		LTRARROW=33, LINE_COMMENT=36, ENV=2, RBR=27, LAUNCHSECTION=7, CLBR=28, 
		MAX=14, DO=11, FLOAT=18, INT=19, EQUALS=23, MINUS=22, ID=17, RTLARROW=32, 
		LBR=26, TYPE=13, ENTITY=9, COLON=20, SLBR=30, ATENV=10, DOUBLESTRING=35, 
		SRBR=31, NAME=5, WS=38, LAUNCH=12, LANGUAGE=6, ENVSECTION=1, COMMA=25, 
		WHEN=8, SINGLESTRING=34, WILDCARD=15, AGENTFILENAME=16, BLOCK_COMMENT=37, 
		PLUS=21, AGENTSECTION=4, CRBR=29, DOT=24, INIT=3;
	public static final String[] tokenNames = {
		"<INVALID>", "'environment'", "'env'", "'init'", "'agentfiles'", "'name'", 
		"'language'", "'launchpolicy'", "'when'", "'entity'", "'@env'", "'do'", 
		"'launch'", "'type'", "'max'", "'*'", "AGENTFILENAME", "ID", "FLOAT", 
		"INT", "':'", "'+'", "'-'", "'='", "'.'", "','", "'('", "')'", "'{'", 
		"'}'", "'['", "']'", "'<-'", "'->'", "SINGLESTRING", "DOUBLESTRING", "LINE_COMMENT", 
		"BLOCK_COMMENT", "WS"
	};
	public static final int
		RULE_mas = 0, RULE_environment = 1, RULE_environmentFile = 2, RULE_initParams = 3, 
		RULE_initParam = 4, RULE_initValues = 5, RULE_initValue = 6, RULE_simpleInitValue = 7, 
		RULE_functionInitValue = 8, RULE_listInitValue = 9, RULE_agentFiles = 10, 
		RULE_agentFile = 11, RULE_agentFileParameters = 12, RULE_agentFileParameter = 13, 
		RULE_launchPolicy = 14, RULE_launchRule = 15, RULE_simpleLaunchRule = 16, 
		RULE_launchRuleComponents = 17, RULE_launchRuleComponent = 18, RULE_conditionalLaunchRule = 19, 
		RULE_entityDescription = 20, RULE_entityConstraints = 21, RULE_entityConstraint = 22;
	public static final String[] ruleNames = {
		"mas", "environment", "environmentFile", "initParams", "initParam", "initValues", 
		"initValue", "simpleInitValue", "functionInitValue", "listInitValue", 
		"agentFiles", "agentFile", "agentFileParameters", "agentFileParameter", 
		"launchPolicy", "launchRule", "simpleLaunchRule", "launchRuleComponents", 
		"launchRuleComponent", "conditionalLaunchRule", "entityDescription", "entityConstraints", 
		"entityConstraint"
	};

	@Override
	public String getGrammarFileName() { return "MASParser.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public MASParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class MasContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(MASParser.EOF, 0); }
		public EnvironmentContext environment() {
			return getRuleContext(EnvironmentContext.class,0);
		}
		public AgentFilesContext agentFiles() {
			return getRuleContext(AgentFilesContext.class,0);
		}
		public LaunchPolicyContext launchPolicy() {
			return getRuleContext(LaunchPolicyContext.class,0);
		}
		public MasContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mas; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitMas(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MasContext mas() throws RecognitionException {
		MasContext _localctx = new MasContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_mas);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(47);
			_la = _input.LA(1);
			if (_la==ENVSECTION) {
				{
				setState(46); environment();
				}
			}

			setState(49); agentFiles();
			setState(50); launchPolicy();
			setState(51); match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EnvironmentContext extends ParserRuleContext {
		public InitParamsContext initParams() {
			return getRuleContext(InitParamsContext.class,0);
		}
		public TerminalNode CRBR() { return getToken(MASParser.CRBR, 0); }
		public EnvironmentFileContext environmentFile() {
			return getRuleContext(EnvironmentFileContext.class,0);
		}
		public TerminalNode DOT() { return getToken(MASParser.DOT, 0); }
		public TerminalNode EQUALS() { return getToken(MASParser.EQUALS, 0); }
		public TerminalNode INIT() { return getToken(MASParser.INIT, 0); }
		public TerminalNode CLBR() { return getToken(MASParser.CLBR, 0); }
		public TerminalNode SLBR() { return getToken(MASParser.SLBR, 0); }
		public TerminalNode SRBR() { return getToken(MASParser.SRBR, 0); }
		public TerminalNode ENVSECTION() { return getToken(MASParser.ENVSECTION, 0); }
		public EnvironmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_environment; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitEnvironment(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EnvironmentContext environment() throws RecognitionException {
		EnvironmentContext _localctx = new EnvironmentContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_environment);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(53); match(ENVSECTION);
			setState(54); match(CLBR);
			setState(55); environmentFile();
			setState(63);
			_la = _input.LA(1);
			if (_la==INIT) {
				{
				setState(56); match(INIT);
				setState(57); match(EQUALS);
				setState(58); match(SLBR);
				setState(59); initParams();
				setState(60); match(SRBR);
				setState(61); match(DOT);
				}
			}

			setState(65); match(CRBR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EnvironmentFileContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(MASParser.DOT, 0); }
		public TerminalNode EQUALS() { return getToken(MASParser.EQUALS, 0); }
		public TerminalNode DOUBLESTRING() { return getToken(MASParser.DOUBLESTRING, 0); }
		public TerminalNode ENV() { return getToken(MASParser.ENV, 0); }
		public EnvironmentFileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_environmentFile; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitEnvironmentFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EnvironmentFileContext environmentFile() throws RecognitionException {
		EnvironmentFileContext _localctx = new EnvironmentFileContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_environmentFile);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(67); match(ENV);
			setState(68); match(EQUALS);
			setState(69); match(DOUBLESTRING);
			setState(70); match(DOT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InitParamsContext extends ParserRuleContext {
		public InitParamContext initParam(int i) {
			return getRuleContext(InitParamContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MASParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(MASParser.COMMA, i);
		}
		public List<InitParamContext> initParam() {
			return getRuleContexts(InitParamContext.class);
		}
		public InitParamsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_initParams; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitInitParams(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InitParamsContext initParams() throws RecognitionException {
		InitParamsContext _localctx = new InitParamsContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_initParams);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72); initParam();
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(73); match(COMMA);
				setState(74); initParam();
				}
				}
				setState(79);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InitParamContext extends ParserRuleContext {
		public TerminalNode EQUALS() { return getToken(MASParser.EQUALS, 0); }
		public TerminalNode ID() { return getToken(MASParser.ID, 0); }
		public InitValueContext initValue() {
			return getRuleContext(InitValueContext.class,0);
		}
		public InitParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_initParam; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitInitParam(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InitParamContext initParam() throws RecognitionException {
		InitParamContext _localctx = new InitParamContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_initParam);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80); match(ID);
			setState(81); match(EQUALS);
			setState(82); initValue();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InitValuesContext extends ParserRuleContext {
		public List<TerminalNode> COMMA() { return getTokens(MASParser.COMMA); }
		public InitValueContext initValue(int i) {
			return getRuleContext(InitValueContext.class,i);
		}
		public TerminalNode COMMA(int i) {
			return getToken(MASParser.COMMA, i);
		}
		public List<InitValueContext> initValue() {
			return getRuleContexts(InitValueContext.class);
		}
		public InitValuesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_initValues; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitInitValues(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InitValuesContext initValues() throws RecognitionException {
		InitValuesContext _localctx = new InitValuesContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_initValues);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(84); initValue();
			setState(89);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(85); match(COMMA);
				setState(86); initValue();
				}
				}
				setState(91);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class InitValueContext extends ParserRuleContext {
		public FunctionInitValueContext functionInitValue() {
			return getRuleContext(FunctionInitValueContext.class,0);
		}
		public SimpleInitValueContext simpleInitValue() {
			return getRuleContext(SimpleInitValueContext.class,0);
		}
		public ListInitValueContext listInitValue() {
			return getRuleContext(ListInitValueContext.class,0);
		}
		public InitValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_initValue; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitInitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final InitValueContext initValue() throws RecognitionException {
		InitValueContext _localctx = new InitValueContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_initValue);
		try {
			setState(95);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(92); simpleInitValue();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(93); functionInitValue();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(94); listInitValue();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SimpleInitValueContext extends ParserRuleContext {
		public TerminalNode SINGLESTRING() { return getToken(MASParser.SINGLESTRING, 0); }
		public TerminalNode FLOAT() { return getToken(MASParser.FLOAT, 0); }
		public TerminalNode INT() { return getToken(MASParser.INT, 0); }
		public TerminalNode ID() { return getToken(MASParser.ID, 0); }
		public TerminalNode DOUBLESTRING() { return getToken(MASParser.DOUBLESTRING, 0); }
		public SimpleInitValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleInitValue; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitSimpleInitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SimpleInitValueContext simpleInitValue() throws RecognitionException {
		SimpleInitValueContext _localctx = new SimpleInitValueContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_simpleInitValue);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(97);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ID) | (1L << FLOAT) | (1L << INT) | (1L << SINGLESTRING) | (1L << DOUBLESTRING))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class FunctionInitValueContext extends ParserRuleContext {
		public InitValuesContext initValues() {
			return getRuleContext(InitValuesContext.class,0);
		}
		public TerminalNode ID() { return getToken(MASParser.ID, 0); }
		public TerminalNode RBR() { return getToken(MASParser.RBR, 0); }
		public TerminalNode LBR() { return getToken(MASParser.LBR, 0); }
		public FunctionInitValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_functionInitValue; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitFunctionInitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionInitValueContext functionInitValue() throws RecognitionException {
		FunctionInitValueContext _localctx = new FunctionInitValueContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_functionInitValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99); match(ID);
			setState(100); match(LBR);
			setState(101); initValues();
			setState(102); match(RBR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ListInitValueContext extends ParserRuleContext {
		public InitValuesContext initValues() {
			return getRuleContext(InitValuesContext.class,0);
		}
		public TerminalNode SLBR() { return getToken(MASParser.SLBR, 0); }
		public TerminalNode SRBR() { return getToken(MASParser.SRBR, 0); }
		public ListInitValueContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listInitValue; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitListInitValue(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListInitValueContext listInitValue() throws RecognitionException {
		ListInitValueContext _localctx = new ListInitValueContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_listInitValue);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(104); match(SLBR);
			setState(105); initValues();
			setState(106); match(SRBR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AgentFilesContext extends ParserRuleContext {
		public TerminalNode CRBR() { return getToken(MASParser.CRBR, 0); }
		public List<AgentFileContext> agentFile() {
			return getRuleContexts(AgentFileContext.class);
		}
		public TerminalNode CLBR() { return getToken(MASParser.CLBR, 0); }
		public TerminalNode AGENTSECTION() { return getToken(MASParser.AGENTSECTION, 0); }
		public AgentFileContext agentFile(int i) {
			return getRuleContext(AgentFileContext.class,i);
		}
		public AgentFilesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_agentFiles; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitAgentFiles(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AgentFilesContext agentFiles() throws RecognitionException {
		AgentFilesContext _localctx = new AgentFilesContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_agentFiles);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(108); match(AGENTSECTION);
			setState(109); match(CLBR);
			setState(113);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DOUBLESTRING) {
				{
				{
				setState(110); agentFile();
				}
				}
				setState(115);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(116); match(CRBR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AgentFileContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(MASParser.DOT, 0); }
		public TerminalNode DOUBLESTRING() { return getToken(MASParser.DOUBLESTRING, 0); }
		public TerminalNode SLBR() { return getToken(MASParser.SLBR, 0); }
		public TerminalNode SRBR() { return getToken(MASParser.SRBR, 0); }
		public AgentFileParametersContext agentFileParameters() {
			return getRuleContext(AgentFileParametersContext.class,0);
		}
		public AgentFileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_agentFile; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitAgentFile(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AgentFileContext agentFile() throws RecognitionException {
		AgentFileContext _localctx = new AgentFileContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_agentFile);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(118); match(DOUBLESTRING);
			setState(123);
			_la = _input.LA(1);
			if (_la==SLBR) {
				{
				setState(119); match(SLBR);
				setState(120); agentFileParameters();
				setState(121); match(SRBR);
				}
			}

			setState(125); match(DOT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AgentFileParametersContext extends ParserRuleContext {
		public AgentFileParameterContext agentFileParameter(int i) {
			return getRuleContext(AgentFileParameterContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(MASParser.COMMA); }
		public List<AgentFileParameterContext> agentFileParameter() {
			return getRuleContexts(AgentFileParameterContext.class);
		}
		public TerminalNode COMMA(int i) {
			return getToken(MASParser.COMMA, i);
		}
		public AgentFileParametersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_agentFileParameters; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitAgentFileParameters(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AgentFileParametersContext agentFileParameters() throws RecognitionException {
		AgentFileParametersContext _localctx = new AgentFileParametersContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_agentFileParameters);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127); agentFileParameter();
			setState(132);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(128); match(COMMA);
				setState(129); agentFileParameter();
				}
				}
				setState(134);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AgentFileParameterContext extends ParserRuleContext {
		public TerminalNode EQUALS() { return getToken(MASParser.EQUALS, 0); }
		public TerminalNode ID() { return getToken(MASParser.ID, 0); }
		public TerminalNode NAME() { return getToken(MASParser.NAME, 0); }
		public TerminalNode LANGUAGE() { return getToken(MASParser.LANGUAGE, 0); }
		public AgentFileParameterContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_agentFileParameter; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitAgentFileParameter(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AgentFileParameterContext agentFileParameter() throws RecognitionException {
		AgentFileParameterContext _localctx = new AgentFileParameterContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_agentFileParameter);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135);
			_la = _input.LA(1);
			if ( !(_la==NAME || _la==LANGUAGE) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			setState(136); match(EQUALS);
			setState(137); match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LaunchPolicyContext extends ParserRuleContext {
		public TerminalNode CRBR() { return getToken(MASParser.CRBR, 0); }
		public LaunchRuleContext launchRule(int i) {
			return getRuleContext(LaunchRuleContext.class,i);
		}
		public List<LaunchRuleContext> launchRule() {
			return getRuleContexts(LaunchRuleContext.class);
		}
		public TerminalNode CLBR() { return getToken(MASParser.CLBR, 0); }
		public TerminalNode LAUNCHSECTION() { return getToken(MASParser.LAUNCHSECTION, 0); }
		public LaunchPolicyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_launchPolicy; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitLaunchPolicy(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LaunchPolicyContext launchPolicy() throws RecognitionException {
		LaunchPolicyContext _localctx = new LaunchPolicyContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_launchPolicy);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(139); match(LAUNCHSECTION);
			setState(140); match(CLBR);
			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==WHEN || _la==LAUNCH) {
				{
				{
				setState(141); launchRule();
				}
				}
				setState(146);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(147); match(CRBR);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LaunchRuleContext extends ParserRuleContext {
		public SimpleLaunchRuleContext simpleLaunchRule() {
			return getRuleContext(SimpleLaunchRuleContext.class,0);
		}
		public ConditionalLaunchRuleContext conditionalLaunchRule() {
			return getRuleContext(ConditionalLaunchRuleContext.class,0);
		}
		public LaunchRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_launchRule; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitLaunchRule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LaunchRuleContext launchRule() throws RecognitionException {
		LaunchRuleContext _localctx = new LaunchRuleContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_launchRule);
		try {
			setState(151);
			switch (_input.LA(1)) {
			case LAUNCH:
				enterOuterAlt(_localctx, 1);
				{
				setState(149); simpleLaunchRule();
				}
				break;
			case WHEN:
				enterOuterAlt(_localctx, 2);
				{
				setState(150); conditionalLaunchRule();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class SimpleLaunchRuleContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(MASParser.DOT, 0); }
		public LaunchRuleComponentsContext launchRuleComponents() {
			return getRuleContext(LaunchRuleComponentsContext.class,0);
		}
		public TerminalNode LAUNCH() { return getToken(MASParser.LAUNCH, 0); }
		public SimpleLaunchRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_simpleLaunchRule; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitSimpleLaunchRule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SimpleLaunchRuleContext simpleLaunchRule() throws RecognitionException {
		SimpleLaunchRuleContext _localctx = new SimpleLaunchRuleContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_simpleLaunchRule);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(153); match(LAUNCH);
			setState(154); launchRuleComponents();
			setState(155); match(DOT);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LaunchRuleComponentsContext extends ParserRuleContext {
		public List<TerminalNode> COMMA() { return getTokens(MASParser.COMMA); }
		public List<LaunchRuleComponentContext> launchRuleComponent() {
			return getRuleContexts(LaunchRuleComponentContext.class);
		}
		public TerminalNode COMMA(int i) {
			return getToken(MASParser.COMMA, i);
		}
		public LaunchRuleComponentContext launchRuleComponent(int i) {
			return getRuleContext(LaunchRuleComponentContext.class,i);
		}
		public LaunchRuleComponentsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_launchRuleComponents; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitLaunchRuleComponents(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LaunchRuleComponentsContext launchRuleComponents() throws RecognitionException {
		LaunchRuleComponentsContext _localctx = new LaunchRuleComponentsContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_launchRuleComponents);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(157); launchRuleComponent();
			setState(162);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(158); match(COMMA);
				setState(159); launchRuleComponent();
				}
				}
				setState(164);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LaunchRuleComponentContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(MASParser.INT, 0); }
		public TerminalNode AGENTFILENAME() { return getToken(MASParser.AGENTFILENAME, 0); }
		public TerminalNode ID() { return getToken(MASParser.ID, 0); }
		public TerminalNode SLBR() { return getToken(MASParser.SLBR, 0); }
		public TerminalNode SRBR() { return getToken(MASParser.SRBR, 0); }
		public TerminalNode WILDCARD() { return getToken(MASParser.WILDCARD, 0); }
		public LaunchRuleComponentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_launchRuleComponent; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitLaunchRuleComponent(this);
			else return visitor.visitChildren(this);
		}
	}

	public final LaunchRuleComponentContext launchRuleComponent() throws RecognitionException {
		LaunchRuleComponentContext _localctx = new LaunchRuleComponentContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_launchRuleComponent);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			switch ( getInterpreter().adaptivePredict(_input,13,_ctx) ) {
			case 1:
				{
				{
				setState(165);
				_la = _input.LA(1);
				if ( !(_la==WILDCARD || _la==ID) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(169);
				_la = _input.LA(1);
				if (_la==SLBR) {
					{
					setState(166); match(SLBR);
					setState(167); match(INT);
					setState(168); match(SRBR);
					}
				}

				setState(171); match(AGENTFILENAME);
				}
				}
				break;
			case 2:
				{
				setState(172); match(ID);
				setState(176);
				_la = _input.LA(1);
				if (_la==SLBR) {
					{
					setState(173); match(SLBR);
					setState(174); match(INT);
					setState(175); match(SRBR);
					}
				}

				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConditionalLaunchRuleContext extends ParserRuleContext {
		public TerminalNode DO() { return getToken(MASParser.DO, 0); }
		public TerminalNode ATENV() { return getToken(MASParser.ATENV, 0); }
		public SimpleLaunchRuleContext simpleLaunchRule() {
			return getRuleContext(SimpleLaunchRuleContext.class,0);
		}
		public EntityDescriptionContext entityDescription() {
			return getRuleContext(EntityDescriptionContext.class,0);
		}
		public TerminalNode WHEN() { return getToken(MASParser.WHEN, 0); }
		public ConditionalLaunchRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionalLaunchRule; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitConditionalLaunchRule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionalLaunchRuleContext conditionalLaunchRule() throws RecognitionException {
		ConditionalLaunchRuleContext _localctx = new ConditionalLaunchRuleContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_conditionalLaunchRule);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180); match(WHEN);
			setState(181); entityDescription();
			setState(182); match(ATENV);
			setState(183); match(DO);
			setState(184); simpleLaunchRule();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EntityDescriptionContext extends ParserRuleContext {
		public TerminalNode SLBR() { return getToken(MASParser.SLBR, 0); }
		public TerminalNode SRBR() { return getToken(MASParser.SRBR, 0); }
		public EntityConstraintsContext entityConstraints() {
			return getRuleContext(EntityConstraintsContext.class,0);
		}
		public TerminalNode ENTITY() { return getToken(MASParser.ENTITY, 0); }
		public EntityDescriptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_entityDescription; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitEntityDescription(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EntityDescriptionContext entityDescription() throws RecognitionException {
		EntityDescriptionContext _localctx = new EntityDescriptionContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_entityDescription);
		try {
			setState(191);
			switch (_input.LA(1)) {
			case ENTITY:
				enterOuterAlt(_localctx, 1);
				{
				setState(186); match(ENTITY);
				}
				break;
			case SLBR:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(187); match(SLBR);
				setState(188); entityConstraints();
				setState(189); match(SRBR);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EntityConstraintsContext extends ParserRuleContext {
		public List<TerminalNode> COMMA() { return getTokens(MASParser.COMMA); }
		public EntityConstraintContext entityConstraint(int i) {
			return getRuleContext(EntityConstraintContext.class,i);
		}
		public List<EntityConstraintContext> entityConstraint() {
			return getRuleContexts(EntityConstraintContext.class);
		}
		public TerminalNode COMMA(int i) {
			return getToken(MASParser.COMMA, i);
		}
		public EntityConstraintsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_entityConstraints; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitEntityConstraints(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EntityConstraintsContext entityConstraints() throws RecognitionException {
		EntityConstraintsContext _localctx = new EntityConstraintsContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_entityConstraints);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(193); entityConstraint();
			setState(198);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(194); match(COMMA);
				setState(195); entityConstraint();
				}
				}
				setState(200);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EntityConstraintContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(MASParser.INT, 0); }
		public TerminalNode EQUALS() { return getToken(MASParser.EQUALS, 0); }
		public TerminalNode ID() { return getToken(MASParser.ID, 0); }
		public TerminalNode NAME() { return getToken(MASParser.NAME, 0); }
		public TerminalNode MAX() { return getToken(MASParser.MAX, 0); }
		public TerminalNode TYPE() { return getToken(MASParser.TYPE, 0); }
		public EntityConstraintContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_entityConstraint; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof MASParserVisitor ) return ((MASParserVisitor<? extends T>)visitor).visitEntityConstraint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final EntityConstraintContext entityConstraint() throws RecognitionException {
		EntityConstraintContext _localctx = new EntityConstraintContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_entityConstraint);
		int _la;
		try {
			setState(207);
			switch (_input.LA(1)) {
			case NAME:
			case TYPE:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(201);
				_la = _input.LA(1);
				if ( !(_la==NAME || _la==TYPE) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				setState(202); match(EQUALS);
				setState(203); match(ID);
				}
				}
				break;
			case MAX:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(204); match(MAX);
				setState(205); match(EQUALS);
				setState(206); match(INT);
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3(\u00d4\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\3\2\5\2\62"+
		"\n\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\5\3B\n\3"+
		"\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\7\5N\n\5\f\5\16\5Q\13\5\3\6\3"+
		"\6\3\6\3\6\3\7\3\7\3\7\7\7Z\n\7\f\7\16\7]\13\7\3\b\3\b\3\b\5\bb\n\b\3"+
		"\t\3\t\3\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\f\3\f\3\f\7\fr\n\f\f"+
		"\f\16\fu\13\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\5\r~\n\r\3\r\3\r\3\16\3\16\3"+
		"\16\7\16\u0085\n\16\f\16\16\16\u0088\13\16\3\17\3\17\3\17\3\17\3\20\3"+
		"\20\3\20\7\20\u0091\n\20\f\20\16\20\u0094\13\20\3\20\3\20\3\21\3\21\5"+
		"\21\u009a\n\21\3\22\3\22\3\22\3\22\3\23\3\23\3\23\7\23\u00a3\n\23\f\23"+
		"\16\23\u00a6\13\23\3\24\3\24\3\24\3\24\5\24\u00ac\n\24\3\24\3\24\3\24"+
		"\3\24\3\24\5\24\u00b3\n\24\5\24\u00b5\n\24\3\25\3\25\3\25\3\25\3\25\3"+
		"\25\3\26\3\26\3\26\3\26\3\26\5\26\u00c2\n\26\3\27\3\27\3\27\7\27\u00c7"+
		"\n\27\f\27\16\27\u00ca\13\27\3\30\3\30\3\30\3\30\3\30\3\30\5\30\u00d2"+
		"\n\30\3\30\2\2\31\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 \"$&(*,.\2\6"+
		"\4\2\23\25$%\3\2\7\b\4\2\21\21\23\23\4\2\7\7\17\17\u00ce\2\61\3\2\2\2"+
		"\4\67\3\2\2\2\6E\3\2\2\2\bJ\3\2\2\2\nR\3\2\2\2\fV\3\2\2\2\16a\3\2\2\2"+
		"\20c\3\2\2\2\22e\3\2\2\2\24j\3\2\2\2\26n\3\2\2\2\30x\3\2\2\2\32\u0081"+
		"\3\2\2\2\34\u0089\3\2\2\2\36\u008d\3\2\2\2 \u0099\3\2\2\2\"\u009b\3\2"+
		"\2\2$\u009f\3\2\2\2&\u00b4\3\2\2\2(\u00b6\3\2\2\2*\u00c1\3\2\2\2,\u00c3"+
		"\3\2\2\2.\u00d1\3\2\2\2\60\62\5\4\3\2\61\60\3\2\2\2\61\62\3\2\2\2\62\63"+
		"\3\2\2\2\63\64\5\26\f\2\64\65\5\36\20\2\65\66\7\2\2\3\66\3\3\2\2\2\67"+
		"8\7\3\2\289\7\36\2\29A\5\6\4\2:;\7\5\2\2;<\7\31\2\2<=\7 \2\2=>\5\b\5\2"+
		">?\7!\2\2?@\7\32\2\2@B\3\2\2\2A:\3\2\2\2AB\3\2\2\2BC\3\2\2\2CD\7\37\2"+
		"\2D\5\3\2\2\2EF\7\4\2\2FG\7\31\2\2GH\7%\2\2HI\7\32\2\2I\7\3\2\2\2JO\5"+
		"\n\6\2KL\7\33\2\2LN\5\n\6\2MK\3\2\2\2NQ\3\2\2\2OM\3\2\2\2OP\3\2\2\2P\t"+
		"\3\2\2\2QO\3\2\2\2RS\7\23\2\2ST\7\31\2\2TU\5\16\b\2U\13\3\2\2\2V[\5\16"+
		"\b\2WX\7\33\2\2XZ\5\16\b\2YW\3\2\2\2Z]\3\2\2\2[Y\3\2\2\2[\\\3\2\2\2\\"+
		"\r\3\2\2\2][\3\2\2\2^b\5\20\t\2_b\5\22\n\2`b\5\24\13\2a^\3\2\2\2a_\3\2"+
		"\2\2a`\3\2\2\2b\17\3\2\2\2cd\t\2\2\2d\21\3\2\2\2ef\7\23\2\2fg\7\34\2\2"+
		"gh\5\f\7\2hi\7\35\2\2i\23\3\2\2\2jk\7 \2\2kl\5\f\7\2lm\7!\2\2m\25\3\2"+
		"\2\2no\7\6\2\2os\7\36\2\2pr\5\30\r\2qp\3\2\2\2ru\3\2\2\2sq\3\2\2\2st\3"+
		"\2\2\2tv\3\2\2\2us\3\2\2\2vw\7\37\2\2w\27\3\2\2\2x}\7%\2\2yz\7 \2\2z{"+
		"\5\32\16\2{|\7!\2\2|~\3\2\2\2}y\3\2\2\2}~\3\2\2\2~\177\3\2\2\2\177\u0080"+
		"\7\32\2\2\u0080\31\3\2\2\2\u0081\u0086\5\34\17\2\u0082\u0083\7\33\2\2"+
		"\u0083\u0085\5\34\17\2\u0084\u0082\3\2\2\2\u0085\u0088\3\2\2\2\u0086\u0084"+
		"\3\2\2\2\u0086\u0087\3\2\2\2\u0087\33\3\2\2\2\u0088\u0086\3\2\2\2\u0089"+
		"\u008a\t\3\2\2\u008a\u008b\7\31\2\2\u008b\u008c\7\23\2\2\u008c\35\3\2"+
		"\2\2\u008d\u008e\7\t\2\2\u008e\u0092\7\36\2\2\u008f\u0091\5 \21\2\u0090"+
		"\u008f\3\2\2\2\u0091\u0094\3\2\2\2\u0092\u0090\3\2\2\2\u0092\u0093\3\2"+
		"\2\2\u0093\u0095\3\2\2\2\u0094\u0092\3\2\2\2\u0095\u0096\7\37\2\2\u0096"+
		"\37\3\2\2\2\u0097\u009a\5\"\22\2\u0098\u009a\5(\25\2\u0099\u0097\3\2\2"+
		"\2\u0099\u0098\3\2\2\2\u009a!\3\2\2\2\u009b\u009c\7\16\2\2\u009c\u009d"+
		"\5$\23\2\u009d\u009e\7\32\2\2\u009e#\3\2\2\2\u009f\u00a4\5&\24\2\u00a0"+
		"\u00a1\7\33\2\2\u00a1\u00a3\5&\24\2\u00a2\u00a0\3\2\2\2\u00a3\u00a6\3"+
		"\2\2\2\u00a4\u00a2\3\2\2\2\u00a4\u00a5\3\2\2\2\u00a5%\3\2\2\2\u00a6\u00a4"+
		"\3\2\2\2\u00a7\u00ab\t\4\2\2\u00a8\u00a9\7 \2\2\u00a9\u00aa\7\25\2\2\u00aa"+
		"\u00ac\7!\2\2\u00ab\u00a8\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00ad\3\2"+
		"\2\2\u00ad\u00b5\7\22\2\2\u00ae\u00b2\7\23\2\2\u00af\u00b0\7 \2\2\u00b0"+
		"\u00b1\7\25\2\2\u00b1\u00b3\7!\2\2\u00b2\u00af\3\2\2\2\u00b2\u00b3\3\2"+
		"\2\2\u00b3\u00b5\3\2\2\2\u00b4\u00a7\3\2\2\2\u00b4\u00ae\3\2\2\2\u00b5"+
		"\'\3\2\2\2\u00b6\u00b7\7\n\2\2\u00b7\u00b8\5*\26\2\u00b8\u00b9\7\f\2\2"+
		"\u00b9\u00ba\7\r\2\2\u00ba\u00bb\5\"\22\2\u00bb)\3\2\2\2\u00bc\u00c2\7"+
		"\13\2\2\u00bd\u00be\7 \2\2\u00be\u00bf\5,\27\2\u00bf\u00c0\7!\2\2\u00c0"+
		"\u00c2\3\2\2\2\u00c1\u00bc\3\2\2\2\u00c1\u00bd\3\2\2\2\u00c2+\3\2\2\2"+
		"\u00c3\u00c8\5.\30\2\u00c4\u00c5\7\33\2\2\u00c5\u00c7\5.\30\2\u00c6\u00c4"+
		"\3\2\2\2\u00c7\u00ca\3\2\2\2\u00c8\u00c6\3\2\2\2\u00c8\u00c9\3\2\2\2\u00c9"+
		"-\3\2\2\2\u00ca\u00c8\3\2\2\2\u00cb\u00cc\t\5\2\2\u00cc\u00cd\7\31\2\2"+
		"\u00cd\u00d2\7\23\2\2\u00ce\u00cf\7\20\2\2\u00cf\u00d0\7\31\2\2\u00d0"+
		"\u00d2\7\25\2\2\u00d1\u00cb\3\2\2\2\u00d1\u00ce\3\2\2\2\u00d2/\3\2\2\2"+
		"\23\61AO[as}\u0086\u0092\u0099\u00a4\u00ab\u00b2\u00b4\u00c1\u00c8\u00d1";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}