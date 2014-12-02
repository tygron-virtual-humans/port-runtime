// Generated from GOALParser.g4 by ANTLR 4.4
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
public class GOALParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		INTERNAL=55, BELIEF=35, MAIN=4, FOCUS=6, ORDER=21, RBR=68, LOG=43, KR_CRBR=81, 
		CLBR=69, NEW=8, DO=32, EQUALS=64, FLOAT=59, PRE=56, RANDOM=25, NOT=33, 
		KR_LBR=83, ID=58, DEFINE=27, RTLARROW=73, SENDONCE=45, LBR=67, IF=28, 
		BELIEFS=18, DOUBLESTRING=76, SRBR=72, LINEAR=23, IMPORT=1, ALLOTHER=47, 
		THEN=31, INSERT=41, ALWAYS=13, COMMA=66, SOME=50, ALL=48, THIS=52, KNOWLEDGE=17, 
		BLOCK_COMMENT=78, PLUS=62, SEND=46, ADOPT=39, KR_STATEMENT=85, DOT=65, 
		ENVIRONMENTAL=54, SELECT=10, ACTIONSPEC=53, EXITMODULE=11, GOAL=38, SOMEOTHER=49, 
		LINEARALL=22, EXIT=12, KR_BLOCK=82, LTRARROW=74, LINE_COMMENT=77, AGOAL=36, 
		LISTALL=30, FORALL=29, GOALS=19, KR_CLBR=80, INT=60, DELETE=42, MINUS=63, 
		NOGOALS=15, MODULE=2, EVENT=5, KR_RBR=84, TRUE=34, PRINT=44, COLON=61, 
		SLBR=71, WS=79, ADAPTIVE=26, DROP=40, SINGLESTRING=75, NOACTION=16, NONE=7, 
		FILTER=9, POST=57, PROGRAM=20, RANDOMALL=24, CRBR=70, NEVER=14, SELF=51, 
		INIT=3, GOALA=37;
	public static final String[] tokenNames = {
		"<INVALID>", "'#import'", "'module'", "'init'", "'main'", "'event'", "'focus'", 
		"'none'", "'new'", "'filter'", "'select'", "'exit-module'", "'exit'", 
		"'always'", "'never'", "'nogoals'", "'noaction'", "'knowledge'", "'beliefs'", 
		"'goals'", "'program'", "'order'", "'linearall'", "'linear'", "'randomall'", 
		"'random'", "'adaptive'", "'#define'", "'if'", "'forall'", "'listall'", 
		"'then'", "'do'", "'not'", "'true'", "'bel'", "'a-goal'", "'goal-a'", 
		"'goal'", "'adopt'", "'drop'", "'insert'", "'delete'", "'log'", "'print'", 
		"'sendonce'", "'send'", "'allother'", "'all'", "'someother'", "'some'", 
		"'self'", "'this'", "'actionspec'", "'@env'", "'@int'", "'pre'", "'post'", 
		"ID", "FLOAT", "INT", "':'", "'+'", "'-'", "'='", "'.'", "','", "'('", 
		"')'", "'{'", "'}'", "'['", "']'", "'<-'", "'->'", "SINGLESTRING", "DOUBLESTRING", 
		"LINE_COMMENT", "BLOCK_COMMENT", "WS", "KR_CLBR", "KR_CRBR", "KR_BLOCK", 
		"KR_LBR", "KR_RBR", "KR_STATEMENT"
	};
	public static final int
		RULE_modules = 0, RULE_moduleImport = 1, RULE_module = 2, RULE_moduleDef = 3, 
		RULE_moduleOptions = 4, RULE_moduleOption = 5, RULE_exitOption = 6, RULE_focusOption = 7, 
		RULE_knowledge = 8, RULE_beliefs = 9, RULE_goals = 10, RULE_actionSpecs = 11, 
		RULE_actionSpec = 12, RULE_actionPre = 13, RULE_actionPost = 14, RULE_function = 15, 
		RULE_program = 16, RULE_macro = 17, RULE_orderOption = 18, RULE_programRule = 19, 
		RULE_ifRule = 20, RULE_forallRule = 21, RULE_listallRule = 22, RULE_conditions = 23, 
		RULE_condition = 24, RULE_mentalRule = 25, RULE_mentalAction = 26, RULE_mentalAtom = 27, 
		RULE_actions = 28, RULE_action = 29, RULE_actionAtom = 30, RULE_selector = 31, 
		RULE_selectExp = 32, RULE_anonModule = 33;
	public static final String[] ruleNames = {
		"modules", "moduleImport", "module", "moduleDef", "moduleOptions", "moduleOption", 
		"exitOption", "focusOption", "knowledge", "beliefs", "goals", "actionSpecs", 
		"actionSpec", "actionPre", "actionPost", "function", "program", "macro", 
		"orderOption", "programRule", "ifRule", "forallRule", "listallRule", "conditions", 
		"condition", "mentalRule", "mentalAction", "mentalAtom", "actions", "action", 
		"actionAtom", "selector", "selectExp", "anonModule"
	};

	@Override
	public String getGrammarFileName() { return "GOALParser.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public GOALParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ModulesContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(GOALParser.EOF, 0); }
		public ModuleContext module(int i) {
			return getRuleContext(ModuleContext.class,i);
		}
		public List<ModuleContext> module() {
			return getRuleContexts(ModuleContext.class);
		}
		public List<ModuleImportContext> moduleImport() {
			return getRuleContexts(ModuleImportContext.class);
		}
		public ModuleImportContext moduleImport(int i) {
			return getRuleContext(ModuleImportContext.class,i);
		}
		public ModulesContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modules; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitModules(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModulesContext modules() throws RecognitionException {
		ModulesContext _localctx = new ModulesContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_modules);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(70); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				setState(70);
				switch (_input.LA(1)) {
				case IMPORT:
					{
					setState(68); moduleImport();
					}
					break;
				case MODULE:
				case INIT:
				case MAIN:
				case EVENT:
					{
					setState(69); module();
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				}
				setState(72); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IMPORT) | (1L << MODULE) | (1L << INIT) | (1L << MAIN) | (1L << EVENT))) != 0) );
			setState(74); match(EOF);
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

	public static class ModuleImportContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(GOALParser.DOT, 0); }
		public TerminalNode IMPORT() { return getToken(GOALParser.IMPORT, 0); }
		public TerminalNode DOUBLESTRING() { return getToken(GOALParser.DOUBLESTRING, 0); }
		public ModuleImportContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleImport; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitModuleImport(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModuleImportContext moduleImport() throws RecognitionException {
		ModuleImportContext _localctx = new ModuleImportContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_moduleImport);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(76); match(IMPORT);
			setState(77); match(DOUBLESTRING);
			setState(78); match(DOT);
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

	public static class ModuleContext extends ParserRuleContext {
		public TerminalNode CRBR() { return getToken(GOALParser.CRBR, 0); }
		public ActionSpecsContext actionSpecs() {
			return getRuleContext(ActionSpecsContext.class,0);
		}
		public GoalsContext goals() {
			return getRuleContext(GoalsContext.class,0);
		}
		public ProgramContext program() {
			return getRuleContext(ProgramContext.class,0);
		}
		public BeliefsContext beliefs() {
			return getRuleContext(BeliefsContext.class,0);
		}
		public KnowledgeContext knowledge() {
			return getRuleContext(KnowledgeContext.class,0);
		}
		public TerminalNode CLBR() { return getToken(GOALParser.CLBR, 0); }
		public TerminalNode SLBR() { return getToken(GOALParser.SLBR, 0); }
		public TerminalNode SRBR() { return getToken(GOALParser.SRBR, 0); }
		public ModuleDefContext moduleDef() {
			return getRuleContext(ModuleDefContext.class,0);
		}
		public ModuleOptionsContext moduleOptions() {
			return getRuleContext(ModuleOptionsContext.class,0);
		}
		public ModuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_module; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitModule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModuleContext module() throws RecognitionException {
		ModuleContext _localctx = new ModuleContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_module);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(80); moduleDef();
			setState(85);
			_la = _input.LA(1);
			if (_la==SLBR) {
				{
				setState(81); match(SLBR);
				setState(82); moduleOptions();
				setState(83); match(SRBR);
				}
			}

			setState(87); match(CLBR);
			setState(89);
			_la = _input.LA(1);
			if (_la==KNOWLEDGE) {
				{
				setState(88); knowledge();
				}
			}

			setState(92);
			_la = _input.LA(1);
			if (_la==BELIEFS) {
				{
				setState(91); beliefs();
				}
			}

			setState(95);
			_la = _input.LA(1);
			if (_la==GOALS) {
				{
				setState(94); goals();
				}
			}

			setState(98);
			_la = _input.LA(1);
			if (_la==PROGRAM) {
				{
				setState(97); program();
				}
			}

			setState(101);
			_la = _input.LA(1);
			if (_la==ACTIONSPEC) {
				{
				setState(100); actionSpecs();
				}
			}

			setState(103); match(CRBR);
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

	public static class ModuleDefContext extends ParserRuleContext {
		public TerminalNode MODULE() { return getToken(GOALParser.MODULE, 0); }
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public TerminalNode EVENT() { return getToken(GOALParser.EVENT, 0); }
		public TerminalNode INIT() { return getToken(GOALParser.INIT, 0); }
		public TerminalNode MAIN() { return getToken(GOALParser.MAIN, 0); }
		public ModuleDefContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleDef; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitModuleDef(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModuleDefContext moduleDef() throws RecognitionException {
		ModuleDefContext _localctx = new ModuleDefContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_moduleDef);
		try {
			setState(113);
			switch (_input.LA(1)) {
			case MODULE:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(105); match(MODULE);
				setState(106); function();
				}
				}
				break;
			case INIT:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(107); match(INIT);
				setState(108); match(MODULE);
				}
				}
				break;
			case MAIN:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(109); match(MAIN);
				setState(110); match(MODULE);
				}
				}
				break;
			case EVENT:
				enterOuterAlt(_localctx, 4);
				{
				{
				setState(111); match(EVENT);
				setState(112); match(MODULE);
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

	public static class ModuleOptionsContext extends ParserRuleContext {
		public ModuleOptionContext moduleOption(int i) {
			return getRuleContext(ModuleOptionContext.class,i);
		}
		public List<ModuleOptionContext> moduleOption() {
			return getRuleContexts(ModuleOptionContext.class);
		}
		public List<TerminalNode> COMMA() { return getTokens(GOALParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(GOALParser.COMMA, i);
		}
		public ModuleOptionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleOptions; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitModuleOptions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModuleOptionsContext moduleOptions() throws RecognitionException {
		ModuleOptionsContext _localctx = new ModuleOptionsContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_moduleOptions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(115); moduleOption();
			setState(120);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(116); match(COMMA);
				setState(117); moduleOption();
				}
				}
				setState(122);
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

	public static class ModuleOptionContext extends ParserRuleContext {
		public ExitOptionContext exitOption() {
			return getRuleContext(ExitOptionContext.class,0);
		}
		public FocusOptionContext focusOption() {
			return getRuleContext(FocusOptionContext.class,0);
		}
		public ModuleOptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_moduleOption; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitModuleOption(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ModuleOptionContext moduleOption() throws RecognitionException {
		ModuleOptionContext _localctx = new ModuleOptionContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_moduleOption);
		try {
			setState(125);
			switch (_input.LA(1)) {
			case EXIT:
				enterOuterAlt(_localctx, 1);
				{
				setState(123); exitOption();
				}
				break;
			case FOCUS:
				enterOuterAlt(_localctx, 2);
				{
				setState(124); focusOption();
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

	public static class ExitOptionContext extends ParserRuleContext {
		public TerminalNode NOACTION() { return getToken(GOALParser.NOACTION, 0); }
		public TerminalNode EQUALS() { return getToken(GOALParser.EQUALS, 0); }
		public TerminalNode NOGOALS() { return getToken(GOALParser.NOGOALS, 0); }
		public TerminalNode NEVER() { return getToken(GOALParser.NEVER, 0); }
		public TerminalNode EXIT() { return getToken(GOALParser.EXIT, 0); }
		public TerminalNode ALWAYS() { return getToken(GOALParser.ALWAYS, 0); }
		public ExitOptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_exitOption; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitExitOption(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ExitOptionContext exitOption() throws RecognitionException {
		ExitOptionContext _localctx = new ExitOptionContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_exitOption);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(127); match(EXIT);
			setState(128); match(EQUALS);
			setState(129);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ALWAYS) | (1L << NEVER) | (1L << NOGOALS) | (1L << NOACTION))) != 0)) ) {
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

	public static class FocusOptionContext extends ParserRuleContext {
		public TerminalNode FOCUS() { return getToken(GOALParser.FOCUS, 0); }
		public TerminalNode NEW() { return getToken(GOALParser.NEW, 0); }
		public TerminalNode NONE() { return getToken(GOALParser.NONE, 0); }
		public TerminalNode EQUALS() { return getToken(GOALParser.EQUALS, 0); }
		public TerminalNode FILTER() { return getToken(GOALParser.FILTER, 0); }
		public TerminalNode SELECT() { return getToken(GOALParser.SELECT, 0); }
		public FocusOptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_focusOption; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitFocusOption(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FocusOptionContext focusOption() throws RecognitionException {
		FocusOptionContext _localctx = new FocusOptionContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_focusOption);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(131); match(FOCUS);
			setState(132); match(EQUALS);
			setState(133);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << NONE) | (1L << NEW) | (1L << FILTER) | (1L << SELECT))) != 0)) ) {
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

	public static class KnowledgeContext extends ParserRuleContext {
		public TerminalNode CRBR() { return getToken(GOALParser.CRBR, 0); }
		public TerminalNode KNOWLEDGE() { return getToken(GOALParser.KNOWLEDGE, 0); }
		public TerminalNode KR_BLOCK(int i) {
			return getToken(GOALParser.KR_BLOCK, i);
		}
		public TerminalNode CLBR() { return getToken(GOALParser.CLBR, 0); }
		public List<TerminalNode> KR_BLOCK() { return getTokens(GOALParser.KR_BLOCK); }
		public KnowledgeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_knowledge; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitKnowledge(this);
			else return visitor.visitChildren(this);
		}
	}

	public final KnowledgeContext knowledge() throws RecognitionException {
		KnowledgeContext _localctx = new KnowledgeContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_knowledge);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(135); match(KNOWLEDGE);
			setState(136); match(CLBR);
			setState(140);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==KR_BLOCK) {
				{
				{
				setState(137); match(KR_BLOCK);
				}
				}
				setState(142);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(143); match(CRBR);
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

	public static class BeliefsContext extends ParserRuleContext {
		public TerminalNode BELIEFS() { return getToken(GOALParser.BELIEFS, 0); }
		public TerminalNode CRBR() { return getToken(GOALParser.CRBR, 0); }
		public TerminalNode KR_BLOCK(int i) {
			return getToken(GOALParser.KR_BLOCK, i);
		}
		public TerminalNode CLBR() { return getToken(GOALParser.CLBR, 0); }
		public List<TerminalNode> KR_BLOCK() { return getTokens(GOALParser.KR_BLOCK); }
		public BeliefsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_beliefs; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitBeliefs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final BeliefsContext beliefs() throws RecognitionException {
		BeliefsContext _localctx = new BeliefsContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_beliefs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(145); match(BELIEFS);
			setState(146); match(CLBR);
			setState(150);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==KR_BLOCK) {
				{
				{
				setState(147); match(KR_BLOCK);
				}
				}
				setState(152);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(153); match(CRBR);
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

	public static class GoalsContext extends ParserRuleContext {
		public TerminalNode CRBR() { return getToken(GOALParser.CRBR, 0); }
		public TerminalNode KR_BLOCK(int i) {
			return getToken(GOALParser.KR_BLOCK, i);
		}
		public TerminalNode CLBR() { return getToken(GOALParser.CLBR, 0); }
		public List<TerminalNode> KR_BLOCK() { return getTokens(GOALParser.KR_BLOCK); }
		public TerminalNode GOALS() { return getToken(GOALParser.GOALS, 0); }
		public GoalsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_goals; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitGoals(this);
			else return visitor.visitChildren(this);
		}
	}

	public final GoalsContext goals() throws RecognitionException {
		GoalsContext _localctx = new GoalsContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_goals);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(155); match(GOALS);
			setState(156); match(CLBR);
			setState(160);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==KR_BLOCK) {
				{
				{
				setState(157); match(KR_BLOCK);
				}
				}
				setState(162);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(163); match(CRBR);
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

	public static class ActionSpecsContext extends ParserRuleContext {
		public TerminalNode ACTIONSPEC() { return getToken(GOALParser.ACTIONSPEC, 0); }
		public TerminalNode CRBR() { return getToken(GOALParser.CRBR, 0); }
		public List<ActionSpecContext> actionSpec() {
			return getRuleContexts(ActionSpecContext.class);
		}
		public TerminalNode CLBR() { return getToken(GOALParser.CLBR, 0); }
		public ActionSpecContext actionSpec(int i) {
			return getRuleContext(ActionSpecContext.class,i);
		}
		public ActionSpecsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actionSpecs; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitActionSpecs(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActionSpecsContext actionSpecs() throws RecognitionException {
		ActionSpecsContext _localctx = new ActionSpecsContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_actionSpecs);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(165); match(ACTIONSPEC);
			setState(166); match(CLBR);
			setState(170);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==ID) {
				{
				{
				setState(167); actionSpec();
				}
				}
				setState(172);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(173); match(CRBR);
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

	public static class ActionSpecContext extends ParserRuleContext {
		public ActionPreContext actionPre() {
			return getRuleContext(ActionPreContext.class,0);
		}
		public TerminalNode CRBR() { return getToken(GOALParser.CRBR, 0); }
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public TerminalNode ENVIRONMENTAL() { return getToken(GOALParser.ENVIRONMENTAL, 0); }
		public TerminalNode CLBR() { return getToken(GOALParser.CLBR, 0); }
		public TerminalNode INTERNAL() { return getToken(GOALParser.INTERNAL, 0); }
		public ActionPostContext actionPost() {
			return getRuleContext(ActionPostContext.class,0);
		}
		public ActionSpecContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actionSpec; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitActionSpec(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActionSpecContext actionSpec() throws RecognitionException {
		ActionSpecContext _localctx = new ActionSpecContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_actionSpec);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(175); function();
			setState(177);
			_la = _input.LA(1);
			if (_la==ENVIRONMENTAL || _la==INTERNAL) {
				{
				setState(176);
				_la = _input.LA(1);
				if ( !(_la==ENVIRONMENTAL || _la==INTERNAL) ) {
				_errHandler.recoverInline(this);
				}
				consume();
				}
			}

			setState(179); match(CLBR);
			setState(180); actionPre();
			setState(181); actionPost();
			setState(182); match(CRBR);
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

	public static class ActionPreContext extends ParserRuleContext {
		public TerminalNode CRBR() { return getToken(GOALParser.CRBR, 0); }
		public TerminalNode KR_BLOCK(int i) {
			return getToken(GOALParser.KR_BLOCK, i);
		}
		public TerminalNode PRE() { return getToken(GOALParser.PRE, 0); }
		public TerminalNode CLBR() { return getToken(GOALParser.CLBR, 0); }
		public List<TerminalNode> KR_BLOCK() { return getTokens(GOALParser.KR_BLOCK); }
		public ActionPreContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actionPre; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitActionPre(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActionPreContext actionPre() throws RecognitionException {
		ActionPreContext _localctx = new ActionPreContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_actionPre);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(184); match(PRE);
			setState(185); match(CLBR);
			setState(189);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==KR_BLOCK) {
				{
				{
				setState(186); match(KR_BLOCK);
				}
				}
				setState(191);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(192); match(CRBR);
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

	public static class ActionPostContext extends ParserRuleContext {
		public TerminalNode CRBR() { return getToken(GOALParser.CRBR, 0); }
		public TerminalNode POST() { return getToken(GOALParser.POST, 0); }
		public TerminalNode KR_BLOCK(int i) {
			return getToken(GOALParser.KR_BLOCK, i);
		}
		public TerminalNode CLBR() { return getToken(GOALParser.CLBR, 0); }
		public List<TerminalNode> KR_BLOCK() { return getTokens(GOALParser.KR_BLOCK); }
		public ActionPostContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actionPost; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitActionPost(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActionPostContext actionPost() throws RecognitionException {
		ActionPostContext _localctx = new ActionPostContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_actionPost);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(194); match(POST);
			setState(195); match(CLBR);
			setState(199);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==KR_BLOCK) {
				{
				{
				setState(196); match(KR_BLOCK);
				}
				}
				setState(201);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(202); match(CRBR);
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

	public static class FunctionContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(GOALParser.ID, 0); }
		public TerminalNode RBR() { return getToken(GOALParser.RBR, 0); }
		public TerminalNode KR_STATEMENT(int i) {
			return getToken(GOALParser.KR_STATEMENT, i);
		}
		public List<TerminalNode> KR_STATEMENT() { return getTokens(GOALParser.KR_STATEMENT); }
		public TerminalNode LBR() { return getToken(GOALParser.LBR, 0); }
		public FunctionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitFunction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final FunctionContext function() throws RecognitionException {
		FunctionContext _localctx = new FunctionContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_function);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(204); match(ID);
			setState(212);
			_la = _input.LA(1);
			if (_la==LBR) {
				{
				setState(205); match(LBR);
				setState(207); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(206); match(KR_STATEMENT);
					}
					}
					setState(209); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==KR_STATEMENT );
				setState(211); match(RBR);
				}
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

	public static class ProgramContext extends ParserRuleContext {
		public List<ProgramRuleContext> programRule() {
			return getRuleContexts(ProgramRuleContext.class);
		}
		public ProgramRuleContext programRule(int i) {
			return getRuleContext(ProgramRuleContext.class,i);
		}
		public TerminalNode CRBR() { return getToken(GOALParser.CRBR, 0); }
		public OrderOptionContext orderOption() {
			return getRuleContext(OrderOptionContext.class,0);
		}
		public List<MacroContext> macro() {
			return getRuleContexts(MacroContext.class);
		}
		public TerminalNode CLBR() { return getToken(GOALParser.CLBR, 0); }
		public TerminalNode SLBR() { return getToken(GOALParser.SLBR, 0); }
		public TerminalNode PROGRAM() { return getToken(GOALParser.PROGRAM, 0); }
		public TerminalNode SRBR() { return getToken(GOALParser.SRBR, 0); }
		public MacroContext macro(int i) {
			return getRuleContext(MacroContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitProgram(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(214); match(PROGRAM);
			setState(219);
			_la = _input.LA(1);
			if (_la==SLBR) {
				{
				setState(215); match(SLBR);
				setState(216); orderOption();
				setState(217); match(SRBR);
				}
			}

			setState(221); match(CLBR);
			setState(225);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==DEFINE) {
				{
				{
				setState(222); macro();
				}
				}
				setState(227);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(231);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IF) | (1L << FORALL) | (1L << LISTALL))) != 0)) {
				{
				{
				setState(228); programRule();
				}
				}
				setState(233);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(234); match(CRBR);
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

	public static class MacroContext extends ParserRuleContext {
		public ConditionsContext conditions() {
			return getRuleContext(ConditionsContext.class,0);
		}
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public TerminalNode DEFINE() { return getToken(GOALParser.DEFINE, 0); }
		public TerminalNode DOT() { return getToken(GOALParser.DOT, 0); }
		public MacroContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_macro; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitMacro(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MacroContext macro() throws RecognitionException {
		MacroContext _localctx = new MacroContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_macro);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(236); match(DEFINE);
			setState(237); function();
			setState(238); conditions();
			setState(239); match(DOT);
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

	public static class OrderOptionContext extends ParserRuleContext {
		public TerminalNode ADAPTIVE() { return getToken(GOALParser.ADAPTIVE, 0); }
		public TerminalNode EQUALS() { return getToken(GOALParser.EQUALS, 0); }
		public TerminalNode ORDER() { return getToken(GOALParser.ORDER, 0); }
		public TerminalNode RANDOMALL() { return getToken(GOALParser.RANDOMALL, 0); }
		public TerminalNode LINEAR() { return getToken(GOALParser.LINEAR, 0); }
		public TerminalNode RANDOM() { return getToken(GOALParser.RANDOM, 0); }
		public TerminalNode LINEARALL() { return getToken(GOALParser.LINEARALL, 0); }
		public OrderOptionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_orderOption; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitOrderOption(this);
			else return visitor.visitChildren(this);
		}
	}

	public final OrderOptionContext orderOption() throws RecognitionException {
		OrderOptionContext _localctx = new OrderOptionContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_orderOption);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(241); match(ORDER);
			setState(242); match(EQUALS);
			setState(243);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LINEARALL) | (1L << LINEAR) | (1L << RANDOMALL) | (1L << RANDOM) | (1L << ADAPTIVE))) != 0)) ) {
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

	public static class ProgramRuleContext extends ParserRuleContext {
		public IfRuleContext ifRule() {
			return getRuleContext(IfRuleContext.class,0);
		}
		public ListallRuleContext listallRule() {
			return getRuleContext(ListallRuleContext.class,0);
		}
		public ForallRuleContext forallRule() {
			return getRuleContext(ForallRuleContext.class,0);
		}
		public ProgramRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_programRule; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitProgramRule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ProgramRuleContext programRule() throws RecognitionException {
		ProgramRuleContext _localctx = new ProgramRuleContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_programRule);
		try {
			setState(248);
			switch (_input.LA(1)) {
			case IF:
				enterOuterAlt(_localctx, 1);
				{
				setState(245); ifRule();
				}
				break;
			case FORALL:
				enterOuterAlt(_localctx, 2);
				{
				setState(246); forallRule();
				}
				break;
			case LISTALL:
				enterOuterAlt(_localctx, 3);
				{
				setState(247); listallRule();
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

	public static class IfRuleContext extends ParserRuleContext {
		public TerminalNode THEN() { return getToken(GOALParser.THEN, 0); }
		public ConditionsContext conditions() {
			return getRuleContext(ConditionsContext.class,0);
		}
		public TerminalNode IF() { return getToken(GOALParser.IF, 0); }
		public TerminalNode DOT() { return getToken(GOALParser.DOT, 0); }
		public AnonModuleContext anonModule() {
			return getRuleContext(AnonModuleContext.class,0);
		}
		public ActionsContext actions() {
			return getRuleContext(ActionsContext.class,0);
		}
		public IfRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ifRule; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitIfRule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final IfRuleContext ifRule() throws RecognitionException {
		IfRuleContext _localctx = new IfRuleContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_ifRule);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(250); match(IF);
			setState(251); conditions();
			setState(252); match(THEN);
			setState(257);
			switch (_input.LA(1)) {
			case INIT:
			case MAIN:
			case EVENT:
			case EXITMODULE:
			case ADOPT:
			case DROP:
			case INSERT:
			case DELETE:
			case LOG:
			case PRINT:
			case SENDONCE:
			case SEND:
			case ALLOTHER:
			case ALL:
			case SOMEOTHER:
			case SOME:
			case SELF:
			case THIS:
			case ID:
			case SLBR:
				{
				{
				setState(253); actions();
				setState(254); match(DOT);
				}
				}
				break;
			case CLBR:
				{
				setState(256); anonModule();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class ForallRuleContext extends ParserRuleContext {
		public ConditionsContext conditions() {
			return getRuleContext(ConditionsContext.class,0);
		}
		public TerminalNode DOT() { return getToken(GOALParser.DOT, 0); }
		public TerminalNode DO() { return getToken(GOALParser.DO, 0); }
		public TerminalNode FORALL() { return getToken(GOALParser.FORALL, 0); }
		public AnonModuleContext anonModule() {
			return getRuleContext(AnonModuleContext.class,0);
		}
		public ActionsContext actions() {
			return getRuleContext(ActionsContext.class,0);
		}
		public ForallRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forallRule; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitForallRule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ForallRuleContext forallRule() throws RecognitionException {
		ForallRuleContext _localctx = new ForallRuleContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_forallRule);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(259); match(FORALL);
			setState(260); conditions();
			setState(261); match(DO);
			setState(266);
			switch (_input.LA(1)) {
			case INIT:
			case MAIN:
			case EVENT:
			case EXITMODULE:
			case ADOPT:
			case DROP:
			case INSERT:
			case DELETE:
			case LOG:
			case PRINT:
			case SENDONCE:
			case SEND:
			case ALLOTHER:
			case ALL:
			case SOMEOTHER:
			case SOME:
			case SELF:
			case THIS:
			case ID:
			case SLBR:
				{
				{
				setState(262); actions();
				setState(263); match(DOT);
				}
				}
				break;
			case CLBR:
				{
				setState(265); anonModule();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class ListallRuleContext extends ParserRuleContext {
		public ConditionsContext conditions() {
			return getRuleContext(ConditionsContext.class,0);
		}
		public TerminalNode DOT() { return getToken(GOALParser.DOT, 0); }
		public TerminalNode DO() { return getToken(GOALParser.DO, 0); }
		public TerminalNode RTLARROW() { return getToken(GOALParser.RTLARROW, 0); }
		public TerminalNode ID() { return getToken(GOALParser.ID, 0); }
		public AnonModuleContext anonModule() {
			return getRuleContext(AnonModuleContext.class,0);
		}
		public TerminalNode LTRARROW() { return getToken(GOALParser.LTRARROW, 0); }
		public ActionsContext actions() {
			return getRuleContext(ActionsContext.class,0);
		}
		public TerminalNode LISTALL() { return getToken(GOALParser.LISTALL, 0); }
		public ListallRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_listallRule; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitListallRule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ListallRuleContext listallRule() throws RecognitionException {
		ListallRuleContext _localctx = new ListallRuleContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_listallRule);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(268); match(LISTALL);
			setState(276);
			switch ( getInterpreter().adaptivePredict(_input,26,_ctx) ) {
			case 1:
				{
				{
				setState(269); match(ID);
				setState(270); match(RTLARROW);
				setState(271); conditions();
				}
				}
				break;
			case 2:
				{
				{
				setState(272); conditions();
				setState(273); match(LTRARROW);
				setState(274); match(ID);
				}
				}
				break;
			}
			setState(278); match(DO);
			setState(283);
			switch (_input.LA(1)) {
			case INIT:
			case MAIN:
			case EVENT:
			case EXITMODULE:
			case ADOPT:
			case DROP:
			case INSERT:
			case DELETE:
			case LOG:
			case PRINT:
			case SENDONCE:
			case SEND:
			case ALLOTHER:
			case ALL:
			case SOMEOTHER:
			case SOME:
			case SELF:
			case THIS:
			case ID:
			case SLBR:
				{
				{
				setState(279); actions();
				setState(280); match(DOT);
				}
				}
				break;
			case CLBR:
				{
				setState(282); anonModule();
				}
				break;
			default:
				throw new NoViableAltException(this);
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

	public static class ConditionsContext extends ParserRuleContext {
		public List<ConditionContext> condition() {
			return getRuleContexts(ConditionContext.class);
		}
		public ConditionContext condition(int i) {
			return getRuleContext(ConditionContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(GOALParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(GOALParser.COMMA, i);
		}
		public ConditionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditions; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitConditions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionsContext conditions() throws RecognitionException {
		ConditionsContext _localctx = new ConditionsContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_conditions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(285); condition();
			setState(290);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(286); match(COMMA);
				setState(287); condition();
				}
				}
				setState(292);
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

	public static class ConditionContext extends ParserRuleContext {
		public TerminalNode TRUE() { return getToken(GOALParser.TRUE, 0); }
		public TerminalNode RBR() { return getToken(GOALParser.RBR, 0); }
		public TerminalNode NOT() { return getToken(GOALParser.NOT, 0); }
		public MentalRuleContext mentalRule() {
			return getRuleContext(MentalRuleContext.class,0);
		}
		public TerminalNode LBR() { return getToken(GOALParser.LBR, 0); }
		public ConditionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_condition; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitCondition(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConditionContext condition() throws RecognitionException {
		ConditionContext _localctx = new ConditionContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_condition);
		try {
			setState(300);
			switch (_input.LA(1)) {
			case TRUE:
				enterOuterAlt(_localctx, 1);
				{
				setState(293); match(TRUE);
				}
				break;
			case BELIEF:
			case AGOAL:
			case GOALA:
			case GOAL:
			case ALLOTHER:
			case ALL:
			case SOMEOTHER:
			case SOME:
			case SELF:
			case THIS:
			case ID:
			case SLBR:
				enterOuterAlt(_localctx, 2);
				{
				setState(294); mentalRule();
				}
				break;
			case NOT:
				enterOuterAlt(_localctx, 3);
				{
				{
				setState(295); match(NOT);
				setState(296); match(LBR);
				setState(297); mentalRule();
				setState(298); match(RBR);
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

	public static class MentalRuleContext extends ParserRuleContext {
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public MentalActionContext mentalAction() {
			return getRuleContext(MentalActionContext.class,0);
		}
		public MentalRuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mentalRule; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitMentalRule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MentalRuleContext mentalRule() throws RecognitionException {
		MentalRuleContext _localctx = new MentalRuleContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_mentalRule);
		try {
			setState(304);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(302); mentalAction();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(303); function();
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

	public static class MentalActionContext extends ParserRuleContext {
		public TerminalNode DOT() { return getToken(GOALParser.DOT, 0); }
		public TerminalNode RBR() { return getToken(GOALParser.RBR, 0); }
		public MentalAtomContext mentalAtom() {
			return getRuleContext(MentalAtomContext.class,0);
		}
		public TerminalNode KR_STATEMENT(int i) {
			return getToken(GOALParser.KR_STATEMENT, i);
		}
		public List<TerminalNode> KR_STATEMENT() { return getTokens(GOALParser.KR_STATEMENT); }
		public SelectorContext selector() {
			return getRuleContext(SelectorContext.class,0);
		}
		public TerminalNode LBR() { return getToken(GOALParser.LBR, 0); }
		public MentalActionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mentalAction; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitMentalAction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MentalActionContext mentalAction() throws RecognitionException {
		MentalActionContext _localctx = new MentalActionContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_mentalAction);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(309);
			_la = _input.LA(1);
			if (((((_la - 47)) & ~0x3f) == 0 && ((1L << (_la - 47)) & ((1L << (ALLOTHER - 47)) | (1L << (ALL - 47)) | (1L << (SOMEOTHER - 47)) | (1L << (SOME - 47)) | (1L << (SELF - 47)) | (1L << (THIS - 47)) | (1L << (ID - 47)) | (1L << (SLBR - 47)))) != 0)) {
				{
				setState(306); selector();
				setState(307); match(DOT);
				}
			}

			setState(311); mentalAtom();
			setState(312); match(LBR);
			setState(314); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(313); match(KR_STATEMENT);
				}
				}
				setState(316); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( _la==KR_STATEMENT );
			setState(318); match(RBR);
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

	public static class MentalAtomContext extends ParserRuleContext {
		public TerminalNode GOALA() { return getToken(GOALParser.GOALA, 0); }
		public TerminalNode GOAL() { return getToken(GOALParser.GOAL, 0); }
		public TerminalNode BELIEF() { return getToken(GOALParser.BELIEF, 0); }
		public TerminalNode AGOAL() { return getToken(GOALParser.AGOAL, 0); }
		public MentalAtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_mentalAtom; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitMentalAtom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final MentalAtomContext mentalAtom() throws RecognitionException {
		MentalAtomContext _localctx = new MentalAtomContext(_ctx, getState());
		enterRule(_localctx, 54, RULE_mentalAtom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(320);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << BELIEF) | (1L << AGOAL) | (1L << GOALA) | (1L << GOAL))) != 0)) ) {
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

	public static class ActionsContext extends ParserRuleContext {
		public List<ActionContext> action() {
			return getRuleContexts(ActionContext.class);
		}
		public List<TerminalNode> PLUS() { return getTokens(GOALParser.PLUS); }
		public TerminalNode PLUS(int i) {
			return getToken(GOALParser.PLUS, i);
		}
		public ActionContext action(int i) {
			return getRuleContext(ActionContext.class,i);
		}
		public ActionsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actions; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitActions(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActionsContext actions() throws RecognitionException {
		ActionsContext _localctx = new ActionsContext(_ctx, getState());
		enterRule(_localctx, 56, RULE_actions);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(322); action();
			setState(327);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==PLUS) {
				{
				{
				setState(323); match(PLUS);
				setState(324); action();
				}
				}
				setState(329);
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

	public static class ActionContext extends ParserRuleContext {
		public FunctionContext function() {
			return getRuleContext(FunctionContext.class,0);
		}
		public ActionAtomContext actionAtom() {
			return getRuleContext(ActionAtomContext.class,0);
		}
		public TerminalNode EXITMODULE() { return getToken(GOALParser.EXITMODULE, 0); }
		public TerminalNode EVENT() { return getToken(GOALParser.EVENT, 0); }
		public TerminalNode INIT() { return getToken(GOALParser.INIT, 0); }
		public TerminalNode RBR() { return getToken(GOALParser.RBR, 0); }
		public TerminalNode KR_STATEMENT(int i) {
			return getToken(GOALParser.KR_STATEMENT, i);
		}
		public List<TerminalNode> KR_STATEMENT() { return getTokens(GOALParser.KR_STATEMENT); }
		public TerminalNode LBR() { return getToken(GOALParser.LBR, 0); }
		public TerminalNode MAIN() { return getToken(GOALParser.MAIN, 0); }
		public ActionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_action; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitAction(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActionContext action() throws RecognitionException {
		ActionContext _localctx = new ActionContext(_ctx, getState());
		enterRule(_localctx, 58, RULE_action);
		int _la;
		try {
			setState(344);
			switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				{
				setState(330); actionAtom();
				setState(331); match(LBR);
				setState(333); 
				_errHandler.sync(this);
				_la = _input.LA(1);
				do {
					{
					{
					setState(332); match(KR_STATEMENT);
					}
					}
					setState(335); 
					_errHandler.sync(this);
					_la = _input.LA(1);
				} while ( _la==KR_STATEMENT );
				setState(337); match(RBR);
				}
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(339); function();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(340); match(EXITMODULE);
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(341); match(INIT);
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(342); match(MAIN);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(343); match(EVENT);
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

	public static class ActionAtomContext extends ParserRuleContext {
		public TerminalNode PRINT() { return getToken(GOALParser.PRINT, 0); }
		public TerminalNode DOT() { return getToken(GOALParser.DOT, 0); }
		public TerminalNode LOG() { return getToken(GOALParser.LOG, 0); }
		public TerminalNode SEND() { return getToken(GOALParser.SEND, 0); }
		public TerminalNode ADOPT() { return getToken(GOALParser.ADOPT, 0); }
		public TerminalNode DELETE() { return getToken(GOALParser.DELETE, 0); }
		public TerminalNode INSERT() { return getToken(GOALParser.INSERT, 0); }
		public TerminalNode SENDONCE() { return getToken(GOALParser.SENDONCE, 0); }
		public SelectorContext selector() {
			return getRuleContext(SelectorContext.class,0);
		}
		public TerminalNode DROP() { return getToken(GOALParser.DROP, 0); }
		public ActionAtomContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_actionAtom; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitActionAtom(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ActionAtomContext actionAtom() throws RecognitionException {
		ActionAtomContext _localctx = new ActionAtomContext(_ctx, getState());
		enterRule(_localctx, 60, RULE_actionAtom);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(349);
			_la = _input.LA(1);
			if (((((_la - 47)) & ~0x3f) == 0 && ((1L << (_la - 47)) & ((1L << (ALLOTHER - 47)) | (1L << (ALL - 47)) | (1L << (SOMEOTHER - 47)) | (1L << (SOME - 47)) | (1L << (SELF - 47)) | (1L << (THIS - 47)) | (1L << (ID - 47)) | (1L << (SLBR - 47)))) != 0)) {
				{
				setState(346); selector();
				setState(347); match(DOT);
				}
			}

			setState(351);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ADOPT) | (1L << DROP) | (1L << INSERT) | (1L << DELETE) | (1L << LOG) | (1L << PRINT) | (1L << SENDONCE) | (1L << SEND))) != 0)) ) {
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

	public static class SelectorContext extends ParserRuleContext {
		public TerminalNode COMMA() { return getToken(GOALParser.COMMA, 0); }
		public List<SelectExpContext> selectExp() {
			return getRuleContexts(SelectExpContext.class);
		}
		public TerminalNode SLBR() { return getToken(GOALParser.SLBR, 0); }
		public TerminalNode SRBR() { return getToken(GOALParser.SRBR, 0); }
		public SelectExpContext selectExp(int i) {
			return getRuleContext(SelectExpContext.class,i);
		}
		public SelectorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selector; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitSelector(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectorContext selector() throws RecognitionException {
		SelectorContext _localctx = new SelectorContext(_ctx, getState());
		enterRule(_localctx, 62, RULE_selector);
		int _la;
		try {
			setState(362);
			switch (_input.LA(1)) {
			case ALLOTHER:
			case ALL:
			case SOMEOTHER:
			case SOME:
			case SELF:
			case THIS:
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(353); selectExp();
				}
				break;
			case SLBR:
				enterOuterAlt(_localctx, 2);
				{
				{
				setState(354); match(SLBR);
				setState(355); selectExp();
				setState(358);
				_la = _input.LA(1);
				if (_la==COMMA) {
					{
					setState(356); match(COMMA);
					setState(357); selectExp();
					}
				}

				setState(360); match(SRBR);
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

	public static class SelectExpContext extends ParserRuleContext {
		public TerminalNode SOME() { return getToken(GOALParser.SOME, 0); }
		public TerminalNode ALL() { return getToken(GOALParser.ALL, 0); }
		public TerminalNode SOMEOTHER() { return getToken(GOALParser.SOMEOTHER, 0); }
		public TerminalNode SELF() { return getToken(GOALParser.SELF, 0); }
		public TerminalNode ID() { return getToken(GOALParser.ID, 0); }
		public TerminalNode THIS() { return getToken(GOALParser.THIS, 0); }
		public TerminalNode ALLOTHER() { return getToken(GOALParser.ALLOTHER, 0); }
		public SelectExpContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_selectExp; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitSelectExp(this);
			else return visitor.visitChildren(this);
		}
	}

	public final SelectExpContext selectExp() throws RecognitionException {
		SelectExpContext _localctx = new SelectExpContext(_ctx, getState());
		enterRule(_localctx, 64, RULE_selectExp);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(364);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << ALLOTHER) | (1L << ALL) | (1L << SOMEOTHER) | (1L << SOME) | (1L << SELF) | (1L << THIS) | (1L << ID))) != 0)) ) {
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

	public static class AnonModuleContext extends ParserRuleContext {
		public List<ProgramRuleContext> programRule() {
			return getRuleContexts(ProgramRuleContext.class);
		}
		public ProgramRuleContext programRule(int i) {
			return getRuleContext(ProgramRuleContext.class,i);
		}
		public TerminalNode CRBR() { return getToken(GOALParser.CRBR, 0); }
		public TerminalNode CLBR() { return getToken(GOALParser.CLBR, 0); }
		public AnonModuleContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_anonModule; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof GOALParserVisitor ) return ((GOALParserVisitor<? extends T>)visitor).visitAnonModule(this);
			else return visitor.visitChildren(this);
		}
	}

	public final AnonModuleContext anonModule() throws RecognitionException {
		AnonModuleContext _localctx = new AnonModuleContext(_ctx, getState());
		enterRule(_localctx, 66, RULE_anonModule);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(366); match(CLBR);
			setState(368); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(367); programRule();
				}
				}
				setState(370); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << IF) | (1L << FORALL) | (1L << LISTALL))) != 0) );
			setState(372); match(CRBR);
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3W\u0179\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\3\2\3\2\6\2I\n\2\r\2\16\2J\3\2\3\2\3\3\3\3\3\3\3\3\3"+
		"\4\3\4\3\4\3\4\3\4\5\4X\n\4\3\4\3\4\5\4\\\n\4\3\4\5\4_\n\4\3\4\5\4b\n"+
		"\4\3\4\5\4e\n\4\3\4\5\4h\n\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\5"+
		"\5t\n\5\3\6\3\6\3\6\7\6y\n\6\f\6\16\6|\13\6\3\7\3\7\5\7\u0080\n\7\3\b"+
		"\3\b\3\b\3\b\3\t\3\t\3\t\3\t\3\n\3\n\3\n\7\n\u008d\n\n\f\n\16\n\u0090"+
		"\13\n\3\n\3\n\3\13\3\13\3\13\7\13\u0097\n\13\f\13\16\13\u009a\13\13\3"+
		"\13\3\13\3\f\3\f\3\f\7\f\u00a1\n\f\f\f\16\f\u00a4\13\f\3\f\3\f\3\r\3\r"+
		"\3\r\7\r\u00ab\n\r\f\r\16\r\u00ae\13\r\3\r\3\r\3\16\3\16\5\16\u00b4\n"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\7\17\u00be\n\17\f\17\16\17"+
		"\u00c1\13\17\3\17\3\17\3\20\3\20\3\20\7\20\u00c8\n\20\f\20\16\20\u00cb"+
		"\13\20\3\20\3\20\3\21\3\21\3\21\6\21\u00d2\n\21\r\21\16\21\u00d3\3\21"+
		"\5\21\u00d7\n\21\3\22\3\22\3\22\3\22\3\22\5\22\u00de\n\22\3\22\3\22\7"+
		"\22\u00e2\n\22\f\22\16\22\u00e5\13\22\3\22\7\22\u00e8\n\22\f\22\16\22"+
		"\u00eb\13\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23\3\24\3\24\3\24\3\24\3"+
		"\25\3\25\3\25\5\25\u00fb\n\25\3\26\3\26\3\26\3\26\3\26\3\26\3\26\5\26"+
		"\u0104\n\26\3\27\3\27\3\27\3\27\3\27\3\27\3\27\5\27\u010d\n\27\3\30\3"+
		"\30\3\30\3\30\3\30\3\30\3\30\3\30\5\30\u0117\n\30\3\30\3\30\3\30\3\30"+
		"\3\30\5\30\u011e\n\30\3\31\3\31\3\31\7\31\u0123\n\31\f\31\16\31\u0126"+
		"\13\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\5\32\u012f\n\32\3\33\3\33\5"+
		"\33\u0133\n\33\3\34\3\34\3\34\5\34\u0138\n\34\3\34\3\34\3\34\6\34\u013d"+
		"\n\34\r\34\16\34\u013e\3\34\3\34\3\35\3\35\3\36\3\36\3\36\7\36\u0148\n"+
		"\36\f\36\16\36\u014b\13\36\3\37\3\37\3\37\6\37\u0150\n\37\r\37\16\37\u0151"+
		"\3\37\3\37\3\37\3\37\3\37\3\37\3\37\5\37\u015b\n\37\3 \3 \3 \5 \u0160"+
		"\n \3 \3 \3!\3!\3!\3!\3!\5!\u0169\n!\3!\3!\5!\u016d\n!\3\"\3\"\3#\3#\6"+
		"#\u0173\n#\r#\16#\u0174\3#\3#\3#\2\2$\2\4\6\b\n\f\16\20\22\24\26\30\32"+
		"\34\36 \"$&(*,.\60\62\64\668:<>@BD\2\t\3\2\17\22\3\2\t\f\3\289\3\2\30"+
		"\34\3\2%(\3\2)\60\4\2\61\66<<\u0186\2H\3\2\2\2\4N\3\2\2\2\6R\3\2\2\2\b"+
		"s\3\2\2\2\nu\3\2\2\2\f\177\3\2\2\2\16\u0081\3\2\2\2\20\u0085\3\2\2\2\22"+
		"\u0089\3\2\2\2\24\u0093\3\2\2\2\26\u009d\3\2\2\2\30\u00a7\3\2\2\2\32\u00b1"+
		"\3\2\2\2\34\u00ba\3\2\2\2\36\u00c4\3\2\2\2 \u00ce\3\2\2\2\"\u00d8\3\2"+
		"\2\2$\u00ee\3\2\2\2&\u00f3\3\2\2\2(\u00fa\3\2\2\2*\u00fc\3\2\2\2,\u0105"+
		"\3\2\2\2.\u010e\3\2\2\2\60\u011f\3\2\2\2\62\u012e\3\2\2\2\64\u0132\3\2"+
		"\2\2\66\u0137\3\2\2\28\u0142\3\2\2\2:\u0144\3\2\2\2<\u015a\3\2\2\2>\u015f"+
		"\3\2\2\2@\u016c\3\2\2\2B\u016e\3\2\2\2D\u0170\3\2\2\2FI\5\4\3\2GI\5\6"+
		"\4\2HF\3\2\2\2HG\3\2\2\2IJ\3\2\2\2JH\3\2\2\2JK\3\2\2\2KL\3\2\2\2LM\7\2"+
		"\2\3M\3\3\2\2\2NO\7\3\2\2OP\7N\2\2PQ\7C\2\2Q\5\3\2\2\2RW\5\b\5\2ST\7I"+
		"\2\2TU\5\n\6\2UV\7J\2\2VX\3\2\2\2WS\3\2\2\2WX\3\2\2\2XY\3\2\2\2Y[\7G\2"+
		"\2Z\\\5\22\n\2[Z\3\2\2\2[\\\3\2\2\2\\^\3\2\2\2]_\5\24\13\2^]\3\2\2\2^"+
		"_\3\2\2\2_a\3\2\2\2`b\5\26\f\2a`\3\2\2\2ab\3\2\2\2bd\3\2\2\2ce\5\"\22"+
		"\2dc\3\2\2\2de\3\2\2\2eg\3\2\2\2fh\5\30\r\2gf\3\2\2\2gh\3\2\2\2hi\3\2"+
		"\2\2ij\7H\2\2j\7\3\2\2\2kl\7\4\2\2lt\5 \21\2mn\7\5\2\2nt\7\4\2\2op\7\6"+
		"\2\2pt\7\4\2\2qr\7\7\2\2rt\7\4\2\2sk\3\2\2\2sm\3\2\2\2so\3\2\2\2sq\3\2"+
		"\2\2t\t\3\2\2\2uz\5\f\7\2vw\7D\2\2wy\5\f\7\2xv\3\2\2\2y|\3\2\2\2zx\3\2"+
		"\2\2z{\3\2\2\2{\13\3\2\2\2|z\3\2\2\2}\u0080\5\16\b\2~\u0080\5\20\t\2\177"+
		"}\3\2\2\2\177~\3\2\2\2\u0080\r\3\2\2\2\u0081\u0082\7\16\2\2\u0082\u0083"+
		"\7B\2\2\u0083\u0084\t\2\2\2\u0084\17\3\2\2\2\u0085\u0086\7\b\2\2\u0086"+
		"\u0087\7B\2\2\u0087\u0088\t\3\2\2\u0088\21\3\2\2\2\u0089\u008a\7\23\2"+
		"\2\u008a\u008e\7G\2\2\u008b\u008d\7T\2\2\u008c\u008b\3\2\2\2\u008d\u0090"+
		"\3\2\2\2\u008e\u008c\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u0091\3\2\2\2\u0090"+
		"\u008e\3\2\2\2\u0091\u0092\7H\2\2\u0092\23\3\2\2\2\u0093\u0094\7\24\2"+
		"\2\u0094\u0098\7G\2\2\u0095\u0097\7T\2\2\u0096\u0095\3\2\2\2\u0097\u009a"+
		"\3\2\2\2\u0098\u0096\3\2\2\2\u0098\u0099\3\2\2\2\u0099\u009b\3\2\2\2\u009a"+
		"\u0098\3\2\2\2\u009b\u009c\7H\2\2\u009c\25\3\2\2\2\u009d\u009e\7\25\2"+
		"\2\u009e\u00a2\7G\2\2\u009f\u00a1\7T\2\2\u00a0\u009f\3\2\2\2\u00a1\u00a4"+
		"\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a2\u00a3\3\2\2\2\u00a3\u00a5\3\2\2\2\u00a4"+
		"\u00a2\3\2\2\2\u00a5\u00a6\7H\2\2\u00a6\27\3\2\2\2\u00a7\u00a8\7\67\2"+
		"\2\u00a8\u00ac\7G\2\2\u00a9\u00ab\5\32\16\2\u00aa\u00a9\3\2\2\2\u00ab"+
		"\u00ae\3\2\2\2\u00ac\u00aa\3\2\2\2\u00ac\u00ad\3\2\2\2\u00ad\u00af\3\2"+
		"\2\2\u00ae\u00ac\3\2\2\2\u00af\u00b0\7H\2\2\u00b0\31\3\2\2\2\u00b1\u00b3"+
		"\5 \21\2\u00b2\u00b4\t\4\2\2\u00b3\u00b2\3\2\2\2\u00b3\u00b4\3\2\2\2\u00b4"+
		"\u00b5\3\2\2\2\u00b5\u00b6\7G\2\2\u00b6\u00b7\5\34\17\2\u00b7\u00b8\5"+
		"\36\20\2\u00b8\u00b9\7H\2\2\u00b9\33\3\2\2\2\u00ba\u00bb\7:\2\2\u00bb"+
		"\u00bf\7G\2\2\u00bc\u00be\7T\2\2\u00bd\u00bc\3\2\2\2\u00be\u00c1\3\2\2"+
		"\2\u00bf\u00bd\3\2\2\2\u00bf\u00c0\3\2\2\2\u00c0\u00c2\3\2\2\2\u00c1\u00bf"+
		"\3\2\2\2\u00c2\u00c3\7H\2\2\u00c3\35\3\2\2\2\u00c4\u00c5\7;\2\2\u00c5"+
		"\u00c9\7G\2\2\u00c6\u00c8\7T\2\2\u00c7\u00c6\3\2\2\2\u00c8\u00cb\3\2\2"+
		"\2\u00c9\u00c7\3\2\2\2\u00c9\u00ca\3\2\2\2\u00ca\u00cc\3\2\2\2\u00cb\u00c9"+
		"\3\2\2\2\u00cc\u00cd\7H\2\2\u00cd\37\3\2\2\2\u00ce\u00d6\7<\2\2\u00cf"+
		"\u00d1\7E\2\2\u00d0\u00d2\7W\2\2\u00d1\u00d0\3\2\2\2\u00d2\u00d3\3\2\2"+
		"\2\u00d3\u00d1\3\2\2\2\u00d3\u00d4\3\2\2\2\u00d4\u00d5\3\2\2\2\u00d5\u00d7"+
		"\7F\2\2\u00d6\u00cf\3\2\2\2\u00d6\u00d7\3\2\2\2\u00d7!\3\2\2\2\u00d8\u00dd"+
		"\7\26\2\2\u00d9\u00da\7I\2\2\u00da\u00db\5&\24\2\u00db\u00dc\7J\2\2\u00dc"+
		"\u00de\3\2\2\2\u00dd\u00d9\3\2\2\2\u00dd\u00de\3\2\2\2\u00de\u00df\3\2"+
		"\2\2\u00df\u00e3\7G\2\2\u00e0\u00e2\5$\23\2\u00e1\u00e0\3\2\2\2\u00e2"+
		"\u00e5\3\2\2\2\u00e3\u00e1\3\2\2\2\u00e3\u00e4\3\2\2\2\u00e4\u00e9\3\2"+
		"\2\2\u00e5\u00e3\3\2\2\2\u00e6\u00e8\5(\25\2\u00e7\u00e6\3\2\2\2\u00e8"+
		"\u00eb\3\2\2\2\u00e9\u00e7\3\2\2\2\u00e9\u00ea\3\2\2\2\u00ea\u00ec\3\2"+
		"\2\2\u00eb\u00e9\3\2\2\2\u00ec\u00ed\7H\2\2\u00ed#\3\2\2\2\u00ee\u00ef"+
		"\7\35\2\2\u00ef\u00f0\5 \21\2\u00f0\u00f1\5\60\31\2\u00f1\u00f2\7C\2\2"+
		"\u00f2%\3\2\2\2\u00f3\u00f4\7\27\2\2\u00f4\u00f5\7B\2\2\u00f5\u00f6\t"+
		"\5\2\2\u00f6\'\3\2\2\2\u00f7\u00fb\5*\26\2\u00f8\u00fb\5,\27\2\u00f9\u00fb"+
		"\5.\30\2\u00fa\u00f7\3\2\2\2\u00fa\u00f8\3\2\2\2\u00fa\u00f9\3\2\2\2\u00fb"+
		")\3\2\2\2\u00fc\u00fd\7\36\2\2\u00fd\u00fe\5\60\31\2\u00fe\u0103\7!\2"+
		"\2\u00ff\u0100\5:\36\2\u0100\u0101\7C\2\2\u0101\u0104\3\2\2\2\u0102\u0104"+
		"\5D#\2\u0103\u00ff\3\2\2\2\u0103\u0102\3\2\2\2\u0104+\3\2\2\2\u0105\u0106"+
		"\7\37\2\2\u0106\u0107\5\60\31\2\u0107\u010c\7\"\2\2\u0108\u0109\5:\36"+
		"\2\u0109\u010a\7C\2\2\u010a\u010d\3\2\2\2\u010b\u010d\5D#\2\u010c\u0108"+
		"\3\2\2\2\u010c\u010b\3\2\2\2\u010d-\3\2\2\2\u010e\u0116\7 \2\2\u010f\u0110"+
		"\7<\2\2\u0110\u0111\7K\2\2\u0111\u0117\5\60\31\2\u0112\u0113\5\60\31\2"+
		"\u0113\u0114\7L\2\2\u0114\u0115\7<\2\2\u0115\u0117\3\2\2\2\u0116\u010f"+
		"\3\2\2\2\u0116\u0112\3\2\2\2\u0117\u0118\3\2\2\2\u0118\u011d\7\"\2\2\u0119"+
		"\u011a\5:\36\2\u011a\u011b\7C\2\2\u011b\u011e\3\2\2\2\u011c\u011e\5D#"+
		"\2\u011d\u0119\3\2\2\2\u011d\u011c\3\2\2\2\u011e/\3\2\2\2\u011f\u0124"+
		"\5\62\32\2\u0120\u0121\7D\2\2\u0121\u0123\5\62\32\2\u0122\u0120\3\2\2"+
		"\2\u0123\u0126\3\2\2\2\u0124\u0122\3\2\2\2\u0124\u0125\3\2\2\2\u0125\61"+
		"\3\2\2\2\u0126\u0124\3\2\2\2\u0127\u012f\7$\2\2\u0128\u012f\5\64\33\2"+
		"\u0129\u012a\7#\2\2\u012a\u012b\7E\2\2\u012b\u012c\5\64\33\2\u012c\u012d"+
		"\7F\2\2\u012d\u012f\3\2\2\2\u012e\u0127\3\2\2\2\u012e\u0128\3\2\2\2\u012e"+
		"\u0129\3\2\2\2\u012f\63\3\2\2\2\u0130\u0133\5\66\34\2\u0131\u0133\5 \21"+
		"\2\u0132\u0130\3\2\2\2\u0132\u0131\3\2\2\2\u0133\65\3\2\2\2\u0134\u0135"+
		"\5@!\2\u0135\u0136\7C\2\2\u0136\u0138\3\2\2\2\u0137\u0134\3\2\2\2\u0137"+
		"\u0138\3\2\2\2\u0138\u0139\3\2\2\2\u0139\u013a\58\35\2\u013a\u013c\7E"+
		"\2\2\u013b\u013d\7W\2\2\u013c\u013b\3\2\2\2\u013d\u013e\3\2\2\2\u013e"+
		"\u013c\3\2\2\2\u013e\u013f\3\2\2\2\u013f\u0140\3\2\2\2\u0140\u0141\7F"+
		"\2\2\u0141\67\3\2\2\2\u0142\u0143\t\6\2\2\u01439\3\2\2\2\u0144\u0149\5"+
		"<\37\2\u0145\u0146\7@\2\2\u0146\u0148\5<\37\2\u0147\u0145\3\2\2\2\u0148"+
		"\u014b\3\2\2\2\u0149\u0147\3\2\2\2\u0149\u014a\3\2\2\2\u014a;\3\2\2\2"+
		"\u014b\u0149\3\2\2\2\u014c\u014d\5> \2\u014d\u014f\7E\2\2\u014e\u0150"+
		"\7W\2\2\u014f\u014e\3\2\2\2\u0150\u0151\3\2\2\2\u0151\u014f\3\2\2\2\u0151"+
		"\u0152\3\2\2\2\u0152\u0153\3\2\2\2\u0153\u0154\7F\2\2\u0154\u015b\3\2"+
		"\2\2\u0155\u015b\5 \21\2\u0156\u015b\7\r\2\2\u0157\u015b\7\5\2\2\u0158"+
		"\u015b\7\6\2\2\u0159\u015b\7\7\2\2\u015a\u014c\3\2\2\2\u015a\u0155\3\2"+
		"\2\2\u015a\u0156\3\2\2\2\u015a\u0157\3\2\2\2\u015a\u0158\3\2\2\2\u015a"+
		"\u0159\3\2\2\2\u015b=\3\2\2\2\u015c\u015d\5@!\2\u015d\u015e\7C\2\2\u015e"+
		"\u0160\3\2\2\2\u015f\u015c\3\2\2\2\u015f\u0160\3\2\2\2\u0160\u0161\3\2"+
		"\2\2\u0161\u0162\t\7\2\2\u0162?\3\2\2\2\u0163\u016d\5B\"\2\u0164\u0165"+
		"\7I\2\2\u0165\u0168\5B\"\2\u0166\u0167\7D\2\2\u0167\u0169\5B\"\2\u0168"+
		"\u0166\3\2\2\2\u0168\u0169\3\2\2\2\u0169\u016a\3\2\2\2\u016a\u016b\7J"+
		"\2\2\u016b\u016d\3\2\2\2\u016c\u0163\3\2\2\2\u016c\u0164\3\2\2\2\u016d"+
		"A\3\2\2\2\u016e\u016f\t\b\2\2\u016fC\3\2\2\2\u0170\u0172\7G\2\2\u0171"+
		"\u0173\5(\25\2\u0172\u0171\3\2\2\2\u0173\u0174\3\2\2\2\u0174\u0172\3\2"+
		"\2\2\u0174\u0175\3\2\2\2\u0175\u0176\3\2\2\2\u0176\u0177\7H\2\2\u0177"+
		"E\3\2\2\2*HJW[^adgsz\177\u008e\u0098\u00a2\u00ac\u00b3\u00bf\u00c9\u00d3"+
		"\u00d6\u00dd\u00e3\u00e9\u00fa\u0103\u010c\u0116\u011d\u0124\u012e\u0132"+
		"\u0137\u013e\u0149\u0151\u015a\u015f\u0168\u016c\u0174";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}