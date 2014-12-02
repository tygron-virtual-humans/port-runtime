// Generated from UnitTestLexer.g4 by ANTLR 4.4
package goal.parser.antlr;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class UnitTestLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		UNITTEST=1, MAS=2, TIMEOUT=3, IN=4, EVALUATE=5, ASSERT=6, ATSTART=7, EVENTUALLY=8, 
		NEVER=9, ATEND=10, UNTIL=11, WHILE=12, FLOAT=13, INT=14, COLON=15, PLUS=16, 
		MINUS=17, EQUALS=18, DOT=19, COMMA=20, LBR=21, RBR=22, CLBR=23, CRBR=24, 
		SLBR=25, SRBR=26, RTLARROW=27, LTRARROW=28, SINGLESTRING=29, DOUBLESTRING=30, 
		LINE_COMMENT=31, BLOCK_COMMENT=32, WS=33, IMPORT=34, MODULE=35, INIT=36, 
		MAIN=37, EVENT=38, FOCUS=39, NONE=40, NEW=41, FILTER=42, SELECT=43, EXITMODULE=44, 
		EXIT=45, ALWAYS=46, NOGOALS=47, NOACTION=48, KNOWLEDGE=49, BELIEFS=50, 
		GOALS=51, PROGRAM=52, ORDER=53, LINEARALL=54, LINEAR=55, RANDOMALL=56, 
		RANDOM=57, ADAPTIVE=58, DEFINE=59, IF=60, FORALL=61, LISTALL=62, THEN=63, 
		DO=64, NOT=65, TRUE=66, BELIEF=67, AGOAL=68, GOALA=69, GOAL=70, ADOPT=71, 
		DROP=72, INSERT=73, DELETE=74, LOG=75, PRINT=76, SENDONCE=77, SEND=78, 
		ALLOTHER=79, ALL=80, SOMEOTHER=81, SOME=82, SELF=83, THIS=84, ACTIONSPEC=85, 
		ENVIRONMENTAL=86, INTERNAL=87, PRE=88, POST=89, ID=90, KR_CLBR=91, KR_CRBR=92, 
		KR_BLOCK=93, KR_LBR=94, KR_RBR=95, KR_STATEMENT=96, UNIT_KR_CLBR=97, UNIT_KR_CRBR=98, 
		UNIT_KR_BLOCK=99, UNIT_KR_LBR=100, UNIT_KR_RBR=101, UNIT_KR_STATEMENT=102;
	public static final int KRBLOCK = 1;
	public static final int KRSTATEMENT = 2;
	public static String[] modeNames = {
		"DEFAULT_MODE", "KRBLOCK", "KRSTATEMENT"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'", "'\\u0005'", 
		"'\\u0006'", "'\\u0007'", "'\b'", "'\t'", "'\n'", "'\\u000B'", "'\f'", 
		"'\r'", "'\\u000E'", "'\\u000F'", "'\\u0010'", "'\\u0011'", "'\\u0012'", 
		"'\\u0013'", "'\\u0014'", "'\\u0015'", "'\\u0016'", "'\\u0017'", "'\\u0018'", 
		"'\\u0019'", "'\\u001A'", "'\\u001B'", "'\\u001C'", "'\\u001D'", "'\\u001E'", 
		"'\\u001F'", "' '", "'!'", "'\"'", "'#'", "'$'", "'%'", "'&'", "'''", 
		"'('", "')'", "'*'", "'+'", "','", "'-'", "'.'", "'/'", "'0'", "'1'", 
		"'2'", "'3'", "'4'", "'5'", "'6'", "'7'", "'8'", "'9'", "':'", "';'", 
		"'<'", "'='", "'>'", "'?'", "'@'", "'A'", "'B'", "'C'", "'D'", "'E'", 
		"'F'", "'G'", "'H'", "'I'", "'J'", "'K'", "'L'", "'M'", "'N'", "'O'", 
		"'P'", "'Q'", "'R'", "'S'", "'T'", "'U'", "'V'", "'W'", "'X'", "'Y'", 
		"'Z'", "'['", "'\\'", "']'", "'^'", "'_'", "'`'", "'a'", "'b'", "'c'", 
		"'d'", "'e'", "'f'"
	};
	public static final String[] ruleNames = {
		"UNITTEST", "MAS", "TIMEOUT", "IN", "EVALUATE", "ASSERT", "ATSTART", "EVENTUALLY", 
		"NEVER", "ATEND", "UNTIL", "WHILE", "ALPHA", "SCORE", "FLOAT", "INT", 
		"DIGITS", "DIGIT", "COLON", "PLUS", "MINUS", "EQUALS", "DOT", "COMMA", 
		"LBR", "RBR", "CLBR", "CRBR", "SLBR", "SRBR", "RTLARROW", "LTRARROW", 
		"SINGLESTRING", "DOUBLESTRING", "LINE_COMMENT", "BLOCK_COMMENT", "WS", 
		"IMPORT", "MODULE", "INIT", "MAIN", "EVENT", "FOCUS", "NONE", "NEW", "FILTER", 
		"SELECT", "EXITMODULE", "EXIT", "ALWAYS", "NOGOALS", "NOACTION", "KNOWLEDGE", 
		"BELIEFS", "GOALS", "PROGRAM", "ORDER", "LINEARALL", "LINEAR", "RANDOMALL", 
		"RANDOM", "ADAPTIVE", "DEFINE", "IF", "FORALL", "LISTALL", "THEN", "DO", 
		"NOT", "TRUE", "BELIEF", "AGOAL", "GOALA", "GOAL", "ADOPT", "DROP", "INSERT", 
		"DELETE", "LOG", "PRINT", "SENDONCE", "SEND", "ALLOTHER", "ALL", "SOMEOTHER", 
		"SOME", "SELF", "THIS", "ACTIONSPEC", "ENVIRONMENTAL", "INTERNAL", "PRE", 
		"POST", "ID", "KR_CLBR", "KR_CRBR", "KR_BLOCK", "KR_LBR", "KR_RBR", "KR_STATEMENT", 
		"UNIT_KR_CLBR", "UNIT_KR_CRBR", "UNIT_KR_BLOCK", "UNIT_KR_LBR", "UNIT_KR_RBR", 
		"UNIT_KR_STATEMENT"
	};

	int bmatch=0,smatch=0;

	public UnitTestLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "UnitTestLexer.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	@Override
	public void action(RuleContext _localctx, int ruleIndex, int actionIndex) {
		switch (ruleIndex) {
		case 0: UNITTEST_action((RuleContext)_localctx, actionIndex); break;
		case 93: ID_action((RuleContext)_localctx, actionIndex); break;
		case 94: KR_CLBR_action((RuleContext)_localctx, actionIndex); break;
		case 95: KR_CRBR_action((RuleContext)_localctx, actionIndex); break;
		case 97: KR_LBR_action((RuleContext)_localctx, actionIndex); break;
		case 98: KR_RBR_action((RuleContext)_localctx, actionIndex); break;
		case 100: UNIT_KR_CLBR_action((RuleContext)_localctx, actionIndex); break;
		case 101: UNIT_KR_CRBR_action((RuleContext)_localctx, actionIndex); break;
		case 102: UNIT_KR_BLOCK_action((RuleContext)_localctx, actionIndex); break;
		case 103: UNIT_KR_LBR_action((RuleContext)_localctx, actionIndex); break;
		case 104: UNIT_KR_RBR_action((RuleContext)_localctx, actionIndex); break;
		case 105: UNIT_KR_STATEMENT_action((RuleContext)_localctx, actionIndex); break;
		}
	}
	private void UNIT_KR_RBR_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 10: 
						 	setType(RBR);
						   	smatch--;
						   	if(smatch==0) popMode();
						   	if(smatch>=1) more();
						  break;
		}
	}
	private void UNIT_KR_LBR_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 9: 
						 	setType(LBR);
						   	smatch++;
						   	if(smatch>1) more();
						  break;
		}
	}
	private void KR_CLBR_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 2: 
							setType(CLBR);
						   	bmatch++;
						   	if(bmatch>1) more();
						 break;
		}
	}
	private void UNITTEST_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 0:  // HACK: disable KR_BLOCKs
							bmatch=Integer.MIN_VALUE;
						 break;
		}
	}
	private void UNIT_KR_STATEMENT_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 11: 
						 	setType(KR_STATEMENT);
						  break;
		}
	}
	private void KR_LBR_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 4:  
							setType(LBR);
						   	smatch++;
						   	if(smatch>1) more();
						 break;
		}
	}
	private void ID_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 1:  
							int IDi=1; // 'hack' for KR parameters
						   	while(true){
						   		final char next = (char)_input.LA(IDi);
						  	 	if(!java.lang.Character.isWhitespace(next)){
						  		 	if(next=='(') pushMode(KRSTATEMENT);
						  		 	break;
						  	 	}
						  	 	IDi++;
						   	}
						  break;
		}
	}
	private void UNIT_KR_CLBR_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 6: 
						  	setType(CLBR);
						   	bmatch++;
						   	if(bmatch>1) more();
						  break;
		}
	}
	private void KR_RBR_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 5:  
							setType(RBR);
						   	smatch--;
						   	if(smatch==0) popMode();
						   	if(smatch>=1) more();
						 break;
		}
	}
	private void KR_CRBR_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 3:  
							setType(CRBR);
						   	bmatch--;
						   	if(bmatch==0) popMode();
						   	if(bmatch>=1) more();
						 break;
		}
	}
	private void UNIT_KR_CRBR_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 7:  
						 	setType(CRBR);
						   	bmatch--;
						   	if(bmatch==0) popMode();
						   	if(bmatch>=1) more();
						  break;
		}
	}
	private void UNIT_KR_BLOCK_action(RuleContext _localctx, int actionIndex) {
		switch (actionIndex) {
		case 8:  
						 	setType(KR_BLOCK);
						  break;
		}
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2h\u0379\b\1\b\1\b"+
		"\1\4\2\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n"+
		"\t\n\4\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21"+
		"\4\22\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30"+
		"\4\31\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37"+
		"\4 \t \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t"+
		"*\4+\t+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63"+
		"\4\64\t\64\4\65\t\65\4\66\t\66\4\67\t\67\48\t8\49\t9\4:\t:\4;\t;\4<\t"+
		"<\4=\t=\4>\t>\4?\t?\4@\t@\4A\tA\4B\tB\4C\tC\4D\tD\4E\tE\4F\tF\4G\tG\4"+
		"H\tH\4I\tI\4J\tJ\4K\tK\4L\tL\4M\tM\4N\tN\4O\tO\4P\tP\4Q\tQ\4R\tR\4S\t"+
		"S\4T\tT\4U\tU\4V\tV\4W\tW\4X\tX\4Y\tY\4Z\tZ\4[\t[\4\\\t\\\4]\t]\4^\t^"+
		"\4_\t_\4`\t`\4a\ta\4b\tb\4c\tc\4d\td\4e\te\4f\tf\4g\tg\4h\th\4i\ti\4j"+
		"\tj\4k\tk\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\4"+
		"\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\6\3\7\3\7\3\7\3\7\3\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\b\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\n\3\n\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r"+
		"\3\r\3\16\3\16\3\17\3\17\3\20\3\20\5\20\u0134\n\20\3\20\3\20\3\20\3\20"+
		"\3\20\3\20\3\20\5\20\u013d\n\20\3\21\3\21\5\21\u0141\n\21\3\21\3\21\3"+
		"\22\6\22\u0146\n\22\r\22\16\22\u0147\3\23\3\23\3\24\3\24\3\25\3\25\3\26"+
		"\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3\34\3\34\3\35"+
		"\3\35\3\36\3\36\3\37\3\37\3 \3 \3 \3!\3!\3!\3\"\3\"\3\"\3\"\7\"\u016e"+
		"\n\"\f\"\16\"\u0171\13\"\3\"\3\"\3#\3#\3#\3#\7#\u0179\n#\f#\16#\u017c"+
		"\13#\3#\3#\3$\3$\7$\u0182\n$\f$\16$\u0185\13$\3$\5$\u0188\n$\3$\3$\3$"+
		"\3$\3%\3%\3%\3%\7%\u0192\n%\f%\16%\u0195\13%\3%\3%\3%\3%\3%\3&\6&\u019d"+
		"\n&\r&\16&\u019e\3&\3&\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3\'\3(\3(\3(\3(\3("+
		"\3(\3(\3)\3)\3)\3)\3)\3*\3*\3*\3*\3*\3+\3+\3+\3+\3+\3+\3,\3,\3,\3,\3,"+
		"\3,\3-\3-\3-\3-\3-\3.\3.\3.\3.\3/\3/\3/\3/\3/\3/\3/\3\60\3\60\3\60\3\60"+
		"\3\60\3\60\3\60\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61\3\61"+
		"\3\61\3\62\3\62\3\62\3\62\3\62\3\63\3\63\3\63\3\63\3\63\3\63\3\63\3\64"+
		"\3\64\3\64\3\64\3\64\3\64\3\64\3\64\3\65\3\65\3\65\3\65\3\65\3\65\3\65"+
		"\3\65\3\65\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66\3\66"+
		"\3\67\3\67\3\67\3\67\3\67\3\67\3\67\3\67\3\67\3\67\38\38\38\38\38\38\3"+
		"8\38\39\39\39\39\39\39\39\39\3:\3:\3:\3:\3:\3:\3;\3;\3;\3;\3;\3;\3;\3"+
		";\3;\3;\3<\3<\3<\3<\3<\3<\3<\3=\3=\3=\3=\3=\3=\3=\3=\3=\3=\3>\3>\3>\3"+
		">\3>\3>\3>\3?\3?\3?\3?\3?\3?\3?\3?\3?\3@\3@\3@\3@\3@\3@\3@\3@\3A\3A\3"+
		"A\3B\3B\3B\3B\3B\3B\3B\3C\3C\3C\3C\3C\3C\3C\3C\3D\3D\3D\3D\3D\3E\3E\3"+
		"E\3F\3F\3F\3F\3G\3G\3G\3G\3G\3H\3H\3H\3H\3H\3H\3I\3I\3I\3I\3I\3I\3I\3"+
		"I\3I\3J\3J\3J\3J\3J\3J\3J\3J\3J\3K\3K\3K\3K\3K\3K\3K\3L\3L\3L\3L\3L\3"+
		"L\3L\3L\3M\3M\3M\3M\3M\3M\3M\3N\3N\3N\3N\3N\3N\3N\3N\3N\3O\3O\3O\3O\3"+
		"O\3O\3O\3O\3O\3P\3P\3P\3P\3P\3P\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3Q\3R\3R\3R\3R\3"+
		"R\3R\3R\3R\3R\3R\3R\3S\3S\3S\3S\3S\3S\3S\3T\3T\3T\3T\3T\3T\3T\3T\3T\3"+
		"U\3U\3U\3U\3V\3V\3V\3V\3V\3V\3V\3V\3V\3V\3W\3W\3W\3W\3W\3X\3X\3X\3X\3"+
		"X\3Y\3Y\3Y\3Y\3Y\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3Z\3[\3[\3[\3[\3[\3\\\3"+
		"\\\3\\\3\\\3\\\3]\3]\3]\3]\3]\3]\3^\3^\3^\3^\3^\3^\3^\3_\3_\5_\u0334\n"+
		"_\3_\3_\3_\7_\u0339\n_\f_\16_\u033c\13_\3_\3_\3`\5`\u0341\n`\3`\3`\3`"+
		"\3a\3a\5a\u0348\na\3a\3a\3b\3b\3c\5c\u034f\nc\3c\3c\3c\3d\3d\5d\u0356"+
		"\nd\3d\3d\3e\3e\3f\5f\u035d\nf\3f\3f\3f\3g\3g\5g\u0364\ng\3g\3g\3h\3h"+
		"\3h\3i\5i\u036c\ni\3i\3i\3i\3j\3j\5j\u0373\nj\3j\3j\3k\3k\3k\5\u016f\u017a"+
		"\u0193\2l\5\3\7\4\t\5\13\6\r\7\17\b\21\t\23\n\25\13\27\f\31\r\33\16\35"+
		"\2\37\2!\17#\20%\2\'\2)\21+\22-\23/\24\61\25\63\26\65\27\67\309\31;\32"+
		"=\33?\34A\35C\36E\37G I!K\"M#O$Q%S&U\'W(Y)[*]+_,a-c.e/g\60i\61k\62m\63"+
		"o\64q\65s\66u\67w8y9{:};\177<\u0081=\u0083>\u0085?\u0087@\u0089A\u008b"+
		"B\u008dC\u008fD\u0091E\u0093F\u0095G\u0097H\u0099I\u009bJ\u009dK\u009f"+
		"L\u00a1M\u00a3N\u00a5O\u00a7P\u00a9Q\u00abR\u00adS\u00afT\u00b1U\u00b3"+
		"V\u00b5W\u00b7X\u00b9Y\u00bbZ\u00bd[\u00bf\\\u00c1]\u00c3^\u00c5_\u00c7"+
		"`\u00c9a\u00cbb\u00cdc\u00cfd\u00d1e\u00d3f\u00d5g\u00d7h\5\2\3\4\6\4"+
		"\2C\\c|\3\2\62;\4\2\f\f\17\17\5\2\13\f\16\17\"\"\u038c\2\5\3\2\2\2\2\7"+
		"\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2"+
		"\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2"+
		"!\3\2\2\2\2#\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61"+
		"\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2"+
		"\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I"+
		"\3\2\2\2\2K\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2Q\3\2\2\2\2S\3\2\2\2\2U\3\2"+
		"\2\2\2W\3\2\2\2\2Y\3\2\2\2\2[\3\2\2\2\2]\3\2\2\2\2_\3\2\2\2\2a\3\2\2\2"+
		"\2c\3\2\2\2\2e\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\2m\3\2\2\2\2o"+
		"\3\2\2\2\2q\3\2\2\2\2s\3\2\2\2\2u\3\2\2\2\2w\3\2\2\2\2y\3\2\2\2\2{\3\2"+
		"\2\2\2}\3\2\2\2\2\177\3\2\2\2\2\u0081\3\2\2\2\2\u0083\3\2\2\2\2\u0085"+
		"\3\2\2\2\2\u0087\3\2\2\2\2\u0089\3\2\2\2\2\u008b\3\2\2\2\2\u008d\3\2\2"+
		"\2\2\u008f\3\2\2\2\2\u0091\3\2\2\2\2\u0093\3\2\2\2\2\u0095\3\2\2\2\2\u0097"+
		"\3\2\2\2\2\u0099\3\2\2\2\2\u009b\3\2\2\2\2\u009d\3\2\2\2\2\u009f\3\2\2"+
		"\2\2\u00a1\3\2\2\2\2\u00a3\3\2\2\2\2\u00a5\3\2\2\2\2\u00a7\3\2\2\2\2\u00a9"+
		"\3\2\2\2\2\u00ab\3\2\2\2\2\u00ad\3\2\2\2\2\u00af\3\2\2\2\2\u00b1\3\2\2"+
		"\2\2\u00b3\3\2\2\2\2\u00b5\3\2\2\2\2\u00b7\3\2\2\2\2\u00b9\3\2\2\2\2\u00bb"+
		"\3\2\2\2\2\u00bd\3\2\2\2\2\u00bf\3\2\2\2\2\u00c1\3\2\2\2\2\u00c3\3\2\2"+
		"\2\2\u00c5\3\2\2\2\2\u00c7\3\2\2\2\2\u00c9\3\2\2\2\2\u00cb\3\2\2\2\3\u00cd"+
		"\3\2\2\2\3\u00cf\3\2\2\2\3\u00d1\3\2\2\2\4\u00d3\3\2\2\2\4\u00d5\3\2\2"+
		"\2\4\u00d7\3\2\2\2\5\u00d9\3\2\2\2\7\u00e3\3\2\2\2\t\u00e7\3\2\2\2\13"+
		"\u00ef\3\2\2\2\r\u00f2\3\2\2\2\17\u00fb\3\2\2\2\21\u0102\3\2\2\2\23\u010a"+
		"\3\2\2\2\25\u0115\3\2\2\2\27\u011b\3\2\2\2\31\u0121\3\2\2\2\33\u0127\3"+
		"\2\2\2\35\u012d\3\2\2\2\37\u012f\3\2\2\2!\u013c\3\2\2\2#\u0140\3\2\2\2"+
		"%\u0145\3\2\2\2\'\u0149\3\2\2\2)\u014b\3\2\2\2+\u014d\3\2\2\2-\u014f\3"+
		"\2\2\2/\u0151\3\2\2\2\61\u0153\3\2\2\2\63\u0155\3\2\2\2\65\u0157\3\2\2"+
		"\2\67\u0159\3\2\2\29\u015b\3\2\2\2;\u015d\3\2\2\2=\u015f\3\2\2\2?\u0161"+
		"\3\2\2\2A\u0163\3\2\2\2C\u0166\3\2\2\2E\u0169\3\2\2\2G\u0174\3\2\2\2I"+
		"\u017f\3\2\2\2K\u018d\3\2\2\2M\u019c\3\2\2\2O\u01a2\3\2\2\2Q\u01aa\3\2"+
		"\2\2S\u01b1\3\2\2\2U\u01b6\3\2\2\2W\u01bb\3\2\2\2Y\u01c1\3\2\2\2[\u01c7"+
		"\3\2\2\2]\u01cc\3\2\2\2_\u01d0\3\2\2\2a\u01d7\3\2\2\2c\u01de\3\2\2\2e"+
		"\u01ea\3\2\2\2g\u01ef\3\2\2\2i\u01f6\3\2\2\2k\u01fe\3\2\2\2m\u0207\3\2"+
		"\2\2o\u0213\3\2\2\2q\u021d\3\2\2\2s\u0225\3\2\2\2u\u022d\3\2\2\2w\u0233"+
		"\3\2\2\2y\u023d\3\2\2\2{\u0244\3\2\2\2}\u024e\3\2\2\2\177\u0255\3\2\2"+
		"\2\u0081\u025e\3\2\2\2\u0083\u0266\3\2\2\2\u0085\u0269\3\2\2\2\u0087\u0270"+
		"\3\2\2\2\u0089\u0278\3\2\2\2\u008b\u027d\3\2\2\2\u008d\u0280\3\2\2\2\u008f"+
		"\u0284\3\2\2\2\u0091\u0289\3\2\2\2\u0093\u028f\3\2\2\2\u0095\u0298\3\2"+
		"\2\2\u0097\u02a1\3\2\2\2\u0099\u02a8\3\2\2\2\u009b\u02b0\3\2\2\2\u009d"+
		"\u02b7\3\2\2\2\u009f\u02c0\3\2\2\2\u00a1\u02c9\3\2\2\2\u00a3\u02cf\3\2"+
		"\2\2\u00a5\u02d7\3\2\2\2\u00a7\u02e2\3\2\2\2\u00a9\u02e9\3\2\2\2\u00ab"+
		"\u02f2\3\2\2\2\u00ad\u02f6\3\2\2\2\u00af\u0300\3\2\2\2\u00b1\u0305\3\2"+
		"\2\2\u00b3\u030a\3\2\2\2\u00b5\u030f\3\2\2\2\u00b7\u031a\3\2\2\2\u00b9"+
		"\u031f\3\2\2\2\u00bb\u0324\3\2\2\2\u00bd\u032a\3\2\2\2\u00bf\u0333\3\2"+
		"\2\2\u00c1\u0340\3\2\2\2\u00c3\u0345\3\2\2\2\u00c5\u034b\3\2\2\2\u00c7"+
		"\u034e\3\2\2\2\u00c9\u0353\3\2\2\2\u00cb\u0359\3\2\2\2\u00cd\u035c\3\2"+
		"\2\2\u00cf\u0361\3\2\2\2\u00d1\u0367\3\2\2\2\u00d3\u036b\3\2\2\2\u00d5"+
		"\u0370\3\2\2\2\u00d7\u0376\3\2\2\2\u00d9\u00da\7o\2\2\u00da\u00db\7c\2"+
		"\2\u00db\u00dc\7u\2\2\u00dc\u00dd\7V\2\2\u00dd\u00de\7g\2\2\u00de\u00df"+
		"\7u\2\2\u00df\u00e0\7v\2\2\u00e0\u00e1\3\2\2\2\u00e1\u00e2\b\2\2\2\u00e2"+
		"\6\3\2\2\2\u00e3\u00e4\7o\2\2\u00e4\u00e5\7c\2\2\u00e5\u00e6\7u\2\2\u00e6"+
		"\b\3\2\2\2\u00e7\u00e8\7v\2\2\u00e8\u00e9\7k\2\2\u00e9\u00ea\7o\2\2\u00ea"+
		"\u00eb\7g\2\2\u00eb\u00ec\7q\2\2\u00ec\u00ed\7w\2\2\u00ed\u00ee\7v\2\2"+
		"\u00ee\n\3\2\2\2\u00ef\u00f0\7k\2\2\u00f0\u00f1\7p\2\2\u00f1\f\3\2\2\2"+
		"\u00f2\u00f3\7g\2\2\u00f3\u00f4\7x\2\2\u00f4\u00f5\7c\2\2\u00f5\u00f6"+
		"\7n\2\2\u00f6\u00f7\7w\2\2\u00f7\u00f8\7c\2\2\u00f8\u00f9\7v\2\2\u00f9"+
		"\u00fa\7g\2\2\u00fa\16\3\2\2\2\u00fb\u00fc\7c\2\2\u00fc\u00fd\7u\2\2\u00fd"+
		"\u00fe\7u\2\2\u00fe\u00ff\7g\2\2\u00ff\u0100\7t\2\2\u0100\u0101\7v\2\2"+
		"\u0101\20\3\2\2\2\u0102\u0103\7c\2\2\u0103\u0104\7v\2\2\u0104\u0105\7"+
		"u\2\2\u0105\u0106\7v\2\2\u0106\u0107\7c\2\2\u0107\u0108\7t\2\2\u0108\u0109"+
		"\7v\2\2\u0109\22\3\2\2\2\u010a\u010b\7g\2\2\u010b\u010c\7x\2\2\u010c\u010d"+
		"\7g\2\2\u010d\u010e\7p\2\2\u010e\u010f\7v\2\2\u010f\u0110\7w\2\2\u0110"+
		"\u0111\7c\2\2\u0111\u0112\7n\2\2\u0112\u0113\7n\2\2\u0113\u0114\7{\2\2"+
		"\u0114\24\3\2\2\2\u0115\u0116\7p\2\2\u0116\u0117\7g\2\2\u0117\u0118\7"+
		"x\2\2\u0118\u0119\7g\2\2\u0119\u011a\7t\2\2\u011a\26\3\2\2\2\u011b\u011c"+
		"\7c\2\2\u011c\u011d\7v\2\2\u011d\u011e\7g\2\2\u011e\u011f\7p\2\2\u011f"+
		"\u0120\7f\2\2\u0120\30\3\2\2\2\u0121\u0122\7w\2\2\u0122\u0123\7p\2\2\u0123"+
		"\u0124\7v\2\2\u0124\u0125\7k\2\2\u0125\u0126\7n\2\2\u0126\32\3\2\2\2\u0127"+
		"\u0128\7y\2\2\u0128\u0129\7j\2\2\u0129\u012a\7k\2\2\u012a\u012b\7n\2\2"+
		"\u012b\u012c\7g\2\2\u012c\34\3\2\2\2\u012d\u012e\t\2\2\2\u012e\36\3\2"+
		"\2\2\u012f\u0130\7a\2\2\u0130 \3\2\2\2\u0131\u0134\5+\25\2\u0132\u0134"+
		"\5-\26\2\u0133\u0131\3\2\2\2\u0133\u0132\3\2\2\2\u0133\u0134\3\2\2\2\u0134"+
		"\u0135\3\2\2\2\u0135\u0136\5%\22\2\u0136\u0137\5\61\30\2\u0137\u0138\5"+
		"%\22\2\u0138\u013d\3\2\2\2\u0139\u013a\5\61\30\2\u013a\u013b\5%\22\2\u013b"+
		"\u013d\3\2\2\2\u013c\u0133\3\2\2\2\u013c\u0139\3\2\2\2\u013d\"\3\2\2\2"+
		"\u013e\u0141\5+\25\2\u013f\u0141\5-\26\2\u0140\u013e\3\2\2\2\u0140\u013f"+
		"\3\2\2\2\u0140\u0141\3\2\2\2\u0141\u0142\3\2\2\2\u0142\u0143\5%\22\2\u0143"+
		"$\3\2\2\2\u0144\u0146\5\'\23\2\u0145\u0144\3\2\2\2\u0146\u0147\3\2\2\2"+
		"\u0147\u0145\3\2\2\2\u0147\u0148\3\2\2\2\u0148&\3\2\2\2\u0149\u014a\t"+
		"\3\2\2\u014a(\3\2\2\2\u014b\u014c\7<\2\2\u014c*\3\2\2\2\u014d\u014e\7"+
		"-\2\2\u014e,\3\2\2\2\u014f\u0150\7/\2\2\u0150.\3\2\2\2\u0151\u0152\7?"+
		"\2\2\u0152\60\3\2\2\2\u0153\u0154\7\60\2\2\u0154\62\3\2\2\2\u0155\u0156"+
		"\7.\2\2\u0156\64\3\2\2\2\u0157\u0158\7*\2\2\u0158\66\3\2\2\2\u0159\u015a"+
		"\7+\2\2\u015a8\3\2\2\2\u015b\u015c\7}\2\2\u015c:\3\2\2\2\u015d\u015e\7"+
		"\177\2\2\u015e<\3\2\2\2\u015f\u0160\7]\2\2\u0160>\3\2\2\2\u0161\u0162"+
		"\7_\2\2\u0162@\3\2\2\2\u0163\u0164\7>\2\2\u0164\u0165\7/\2\2\u0165B\3"+
		"\2\2\2\u0166\u0167\7/\2\2\u0167\u0168\7@\2\2\u0168D\3\2\2\2\u0169\u016f"+
		"\7)\2\2\u016a\u016b\7^\2\2\u016b\u016e\7)\2\2\u016c\u016e\13\2\2\2\u016d"+
		"\u016a\3\2\2\2\u016d\u016c\3\2\2\2\u016e\u0171\3\2\2\2\u016f\u0170\3\2"+
		"\2\2\u016f\u016d\3\2\2\2\u0170\u0172\3\2\2\2\u0171\u016f\3\2\2\2\u0172"+
		"\u0173\7)\2\2\u0173F\3\2\2\2\u0174\u017a\7$\2\2\u0175\u0176\7^\2\2\u0176"+
		"\u0179\7$\2\2\u0177\u0179\13\2\2\2\u0178\u0175\3\2\2\2\u0178\u0177\3\2"+
		"\2\2\u0179\u017c\3\2\2\2\u017a\u017b\3\2\2\2\u017a\u0178\3\2\2\2\u017b"+
		"\u017d\3\2\2\2\u017c\u017a\3\2\2\2\u017d\u017e\7$\2\2\u017eH\3\2\2\2\u017f"+
		"\u0183\7\'\2\2\u0180\u0182\n\4\2\2\u0181\u0180\3\2\2\2\u0182\u0185\3\2"+
		"\2\2\u0183\u0181\3\2\2\2\u0183\u0184\3\2\2\2\u0184\u0187\3\2\2\2\u0185"+
		"\u0183\3\2\2\2\u0186\u0188\7\17\2\2\u0187\u0186\3\2\2\2\u0187\u0188\3"+
		"\2\2\2\u0188\u0189\3\2\2\2\u0189\u018a\7\f\2\2\u018a\u018b\3\2\2\2\u018b"+
		"\u018c\b$\3\2\u018cJ\3\2\2\2\u018d\u018e\7\61\2\2\u018e\u018f\7,\2\2\u018f"+
		"\u0193\3\2\2\2\u0190\u0192\13\2\2\2\u0191\u0190\3\2\2\2\u0192\u0195\3"+
		"\2\2\2\u0193\u0194\3\2\2\2\u0193\u0191\3\2\2\2\u0194\u0196\3\2\2\2\u0195"+
		"\u0193\3\2\2\2\u0196\u0197\7,\2\2\u0197\u0198\7\61\2\2\u0198\u0199\3\2"+
		"\2\2\u0199\u019a\b%\3\2\u019aL\3\2\2\2\u019b\u019d\t\5\2\2\u019c\u019b"+
		"\3\2\2\2\u019d\u019e\3\2\2\2\u019e\u019c\3\2\2\2\u019e\u019f\3\2\2\2\u019f"+
		"\u01a0\3\2\2\2\u01a0\u01a1\b&\3\2\u01a1N\3\2\2\2\u01a2\u01a3\7%\2\2\u01a3"+
		"\u01a4\7k\2\2\u01a4\u01a5\7o\2\2\u01a5\u01a6\7r\2\2\u01a6\u01a7\7q\2\2"+
		"\u01a7\u01a8\7t\2\2\u01a8\u01a9\7v\2\2\u01a9P\3\2\2\2\u01aa\u01ab\7o\2"+
		"\2\u01ab\u01ac\7q\2\2\u01ac\u01ad\7f\2\2\u01ad\u01ae\7w\2\2\u01ae\u01af"+
		"\7n\2\2\u01af\u01b0\7g\2\2\u01b0R\3\2\2\2\u01b1\u01b2\7k\2\2\u01b2\u01b3"+
		"\7p\2\2\u01b3\u01b4\7k\2\2\u01b4\u01b5\7v\2\2\u01b5T\3\2\2\2\u01b6\u01b7"+
		"\7o\2\2\u01b7\u01b8\7c\2\2\u01b8\u01b9\7k\2\2\u01b9\u01ba\7p\2\2\u01ba"+
		"V\3\2\2\2\u01bb\u01bc\7g\2\2\u01bc\u01bd\7x\2\2\u01bd\u01be\7g\2\2\u01be"+
		"\u01bf\7p\2\2\u01bf\u01c0\7v\2\2\u01c0X\3\2\2\2\u01c1\u01c2\7h\2\2\u01c2"+
		"\u01c3\7q\2\2\u01c3\u01c4\7e\2\2\u01c4\u01c5\7w\2\2\u01c5\u01c6\7u\2\2"+
		"\u01c6Z\3\2\2\2\u01c7\u01c8\7p\2\2\u01c8\u01c9\7q\2\2\u01c9\u01ca\7p\2"+
		"\2\u01ca\u01cb\7g\2\2\u01cb\\\3\2\2\2\u01cc\u01cd\7p\2\2\u01cd\u01ce\7"+
		"g\2\2\u01ce\u01cf\7y\2\2\u01cf^\3\2\2\2\u01d0\u01d1\7h\2\2\u01d1\u01d2"+
		"\7k\2\2\u01d2\u01d3\7n\2\2\u01d3\u01d4\7v\2\2\u01d4\u01d5\7g\2\2\u01d5"+
		"\u01d6\7t\2\2\u01d6`\3\2\2\2\u01d7\u01d8\7u\2\2\u01d8\u01d9\7g\2\2\u01d9"+
		"\u01da\7n\2\2\u01da\u01db\7g\2\2\u01db\u01dc\7e\2\2\u01dc\u01dd\7v\2\2"+
		"\u01ddb\3\2\2\2\u01de\u01df\7g\2\2\u01df\u01e0\7z\2\2\u01e0\u01e1\7k\2"+
		"\2\u01e1\u01e2\7v\2\2\u01e2\u01e3\7/\2\2\u01e3\u01e4\7o\2\2\u01e4\u01e5"+
		"\7q\2\2\u01e5\u01e6\7f\2\2\u01e6\u01e7\7w\2\2\u01e7\u01e8\7n\2\2\u01e8"+
		"\u01e9\7g\2\2\u01e9d\3\2\2\2\u01ea\u01eb\7g\2\2\u01eb\u01ec\7z\2\2\u01ec"+
		"\u01ed\7k\2\2\u01ed\u01ee\7v\2\2\u01eef\3\2\2\2\u01ef\u01f0\7c\2\2\u01f0"+
		"\u01f1\7n\2\2\u01f1\u01f2\7y\2\2\u01f2\u01f3\7c\2\2\u01f3\u01f4\7{\2\2"+
		"\u01f4\u01f5\7u\2\2\u01f5h\3\2\2\2\u01f6\u01f7\7p\2\2\u01f7\u01f8\7q\2"+
		"\2\u01f8\u01f9\7i\2\2\u01f9\u01fa\7q\2\2\u01fa\u01fb\7c\2\2\u01fb\u01fc"+
		"\7n\2\2\u01fc\u01fd\7u\2\2\u01fdj\3\2\2\2\u01fe\u01ff\7p\2\2\u01ff\u0200"+
		"\7q\2\2\u0200\u0201\7c\2\2\u0201\u0202\7e\2\2\u0202\u0203\7v\2\2\u0203"+
		"\u0204\7k\2\2\u0204\u0205\7q\2\2\u0205\u0206\7p\2\2\u0206l\3\2\2\2\u0207"+
		"\u0208\7m\2\2\u0208\u0209\7p\2\2\u0209\u020a\7q\2\2\u020a\u020b\7y\2\2"+
		"\u020b\u020c\7n\2\2\u020c\u020d\7g\2\2\u020d\u020e\7f\2\2\u020e\u020f"+
		"\7i\2\2\u020f\u0210\7g\2\2\u0210\u0211\3\2\2\2\u0211\u0212\b\66\4\2\u0212"+
		"n\3\2\2\2\u0213\u0214\7d\2\2\u0214\u0215\7g\2\2\u0215\u0216\7n\2\2\u0216"+
		"\u0217\7k\2\2\u0217\u0218\7g\2\2\u0218\u0219\7h\2\2\u0219\u021a\7u\2\2"+
		"\u021a\u021b\3\2\2\2\u021b\u021c\b\67\4\2\u021cp\3\2\2\2\u021d\u021e\7"+
		"i\2\2\u021e\u021f\7q\2\2\u021f\u0220\7c\2\2\u0220\u0221\7n\2\2\u0221\u0222"+
		"\7u\2\2\u0222\u0223\3\2\2\2\u0223\u0224\b8\4\2\u0224r\3\2\2\2\u0225\u0226"+
		"\7r\2\2\u0226\u0227\7t\2\2\u0227\u0228\7q\2\2\u0228\u0229\7i\2\2\u0229"+
		"\u022a\7t\2\2\u022a\u022b\7c\2\2\u022b\u022c\7o\2\2\u022ct\3\2\2\2\u022d"+
		"\u022e\7q\2\2\u022e\u022f\7t\2\2\u022f\u0230\7f\2\2\u0230\u0231\7g\2\2"+
		"\u0231\u0232\7t\2\2\u0232v\3\2\2\2\u0233\u0234\7n\2\2\u0234\u0235\7k\2"+
		"\2\u0235\u0236\7p\2\2\u0236\u0237\7g\2\2\u0237\u0238\7c\2\2\u0238\u0239"+
		"\7t\2\2\u0239\u023a\7c\2\2\u023a\u023b\7n\2\2\u023b\u023c\7n\2\2\u023c"+
		"x\3\2\2\2\u023d\u023e\7n\2\2\u023e\u023f\7k\2\2\u023f\u0240\7p\2\2\u0240"+
		"\u0241\7g\2\2\u0241\u0242\7c\2\2\u0242\u0243\7t\2\2\u0243z\3\2\2\2\u0244"+
		"\u0245\7t\2\2\u0245\u0246\7c\2\2\u0246\u0247\7p\2\2\u0247\u0248\7f\2\2"+
		"\u0248\u0249\7q\2\2\u0249\u024a\7o\2\2\u024a\u024b\7c\2\2\u024b\u024c"+
		"\7n\2\2\u024c\u024d\7n\2\2\u024d|\3\2\2\2\u024e\u024f\7t\2\2\u024f\u0250"+
		"\7c\2\2\u0250\u0251\7p\2\2\u0251\u0252\7f\2\2\u0252\u0253\7q\2\2\u0253"+
		"\u0254\7o\2\2\u0254~\3\2\2\2\u0255\u0256\7c\2\2\u0256\u0257\7f\2\2\u0257"+
		"\u0258\7c\2\2\u0258\u0259\7r\2\2\u0259\u025a\7v\2\2\u025a\u025b\7k\2\2"+
		"\u025b\u025c\7x\2\2\u025c\u025d\7g\2\2\u025d\u0080\3\2\2\2\u025e\u025f"+
		"\7%\2\2\u025f\u0260\7f\2\2\u0260\u0261\7g\2\2\u0261\u0262\7h\2\2\u0262"+
		"\u0263\7k\2\2\u0263\u0264\7p\2\2\u0264\u0265\7g\2\2\u0265\u0082\3\2\2"+
		"\2\u0266\u0267\7k\2\2\u0267\u0268\7h\2\2\u0268\u0084\3\2\2\2\u0269\u026a"+
		"\7h\2\2\u026a\u026b\7q\2\2\u026b\u026c\7t\2\2\u026c\u026d\7c\2\2\u026d"+
		"\u026e\7n\2\2\u026e\u026f\7n\2\2\u026f\u0086\3\2\2\2\u0270\u0271\7n\2"+
		"\2\u0271\u0272\7k\2\2\u0272\u0273\7u\2\2\u0273\u0274\7v\2\2\u0274\u0275"+
		"\7c\2\2\u0275\u0276\7n\2\2\u0276\u0277\7n\2\2\u0277\u0088\3\2\2\2\u0278"+
		"\u0279\7v\2\2\u0279\u027a\7j\2\2\u027a\u027b\7g\2\2\u027b\u027c\7p\2\2"+
		"\u027c\u008a\3\2\2\2\u027d\u027e\7f\2\2\u027e\u027f\7q\2\2\u027f\u008c"+
		"\3\2\2\2\u0280\u0281\7p\2\2\u0281\u0282\7q\2\2\u0282\u0283\7v\2\2\u0283"+
		"\u008e\3\2\2\2\u0284\u0285\7v\2\2\u0285\u0286\7t\2\2\u0286\u0287\7w\2"+
		"\2\u0287\u0288\7g\2\2\u0288\u0090\3\2\2\2\u0289\u028a\7d\2\2\u028a\u028b"+
		"\7g\2\2\u028b\u028c\7n\2\2\u028c\u028d\3\2\2\2\u028d\u028e\bH\5\2\u028e"+
		"\u0092\3\2\2\2\u028f\u0290\7c\2\2\u0290\u0291\7/\2\2\u0291\u0292\7i\2"+
		"\2\u0292\u0293\7q\2\2\u0293\u0294\7c\2\2\u0294\u0295\7n\2\2\u0295\u0296"+
		"\3\2\2\2\u0296\u0297\bI\5\2\u0297\u0094\3\2\2\2\u0298\u0299\7i\2\2\u0299"+
		"\u029a\7q\2\2\u029a\u029b\7c\2\2\u029b\u029c\7n\2\2\u029c\u029d\7/\2\2"+
		"\u029d\u029e\7c\2\2\u029e\u029f\3\2\2\2\u029f\u02a0\bJ\5\2\u02a0\u0096"+
		"\3\2\2\2\u02a1\u02a2\7i\2\2\u02a2\u02a3\7q\2\2\u02a3\u02a4\7c\2\2\u02a4"+
		"\u02a5\7n\2\2\u02a5\u02a6\3\2\2\2\u02a6\u02a7\bK\5\2\u02a7\u0098\3\2\2"+
		"\2\u02a8\u02a9\7c\2\2\u02a9\u02aa\7f\2\2\u02aa\u02ab\7q\2\2\u02ab\u02ac"+
		"\7r\2\2\u02ac\u02ad\7v\2\2\u02ad\u02ae\3\2\2\2\u02ae\u02af\bL\5\2\u02af"+
		"\u009a\3\2\2\2\u02b0\u02b1\7f\2\2\u02b1\u02b2\7t\2\2\u02b2\u02b3\7q\2"+
		"\2\u02b3\u02b4\7r\2\2\u02b4\u02b5\3\2\2\2\u02b5\u02b6\bM\5\2\u02b6\u009c"+
		"\3\2\2\2\u02b7\u02b8\7k\2\2\u02b8\u02b9\7p\2\2\u02b9\u02ba\7u\2\2\u02ba"+
		"\u02bb\7g\2\2\u02bb\u02bc\7t\2\2\u02bc\u02bd\7v\2\2\u02bd\u02be\3\2\2"+
		"\2\u02be\u02bf\bN\5\2\u02bf\u009e\3\2\2\2\u02c0\u02c1\7f\2\2\u02c1\u02c2"+
		"\7g\2\2\u02c2\u02c3\7n\2\2\u02c3\u02c4\7g\2\2\u02c4\u02c5\7v\2\2\u02c5"+
		"\u02c6\7g\2\2\u02c6\u02c7\3\2\2\2\u02c7\u02c8\bO\5\2\u02c8\u00a0\3\2\2"+
		"\2\u02c9\u02ca\7n\2\2\u02ca\u02cb\7q\2\2\u02cb\u02cc\7i\2\2\u02cc\u02cd"+
		"\3\2\2\2\u02cd\u02ce\bP\5\2\u02ce\u00a2\3\2\2\2\u02cf\u02d0\7r\2\2\u02d0"+
		"\u02d1\7t\2\2\u02d1\u02d2\7k\2\2\u02d2\u02d3\7p\2\2\u02d3\u02d4\7v\2\2"+
		"\u02d4\u02d5\3\2\2\2\u02d5\u02d6\bQ\5\2\u02d6\u00a4\3\2\2\2\u02d7\u02d8"+
		"\7u\2\2\u02d8\u02d9\7g\2\2\u02d9\u02da\7p\2\2\u02da\u02db\7f\2\2\u02db"+
		"\u02dc\7q\2\2\u02dc\u02dd\7p\2\2\u02dd\u02de\7e\2\2\u02de\u02df\7g\2\2"+
		"\u02df\u02e0\3\2\2\2\u02e0\u02e1\bR\5\2\u02e1\u00a6\3\2\2\2\u02e2\u02e3"+
		"\7u\2\2\u02e3\u02e4\7g\2\2\u02e4\u02e5\7p\2\2\u02e5\u02e6\7f\2\2\u02e6"+
		"\u02e7\3\2\2\2\u02e7\u02e8\bS\5\2\u02e8\u00a8\3\2\2\2\u02e9\u02ea\7c\2"+
		"\2\u02ea\u02eb\7n\2\2\u02eb\u02ec\7n\2\2\u02ec\u02ed\7q\2\2\u02ed\u02ee"+
		"\7v\2\2\u02ee\u02ef\7j\2\2\u02ef\u02f0\7g\2\2\u02f0\u02f1\7t\2\2\u02f1"+
		"\u00aa\3\2\2\2\u02f2\u02f3\7c\2\2\u02f3\u02f4\7n\2\2\u02f4\u02f5\7n\2"+
		"\2\u02f5\u00ac\3\2\2\2\u02f6\u02f7\7u\2\2\u02f7\u02f8\7q\2\2\u02f8\u02f9"+
		"\7o\2\2\u02f9\u02fa\7g\2\2\u02fa\u02fb\7q\2\2\u02fb\u02fc\7v\2\2\u02fc"+
		"\u02fd\7j\2\2\u02fd\u02fe\7g\2\2\u02fe\u02ff\7t\2\2\u02ff\u00ae\3\2\2"+
		"\2\u0300\u0301\7u\2\2\u0301\u0302\7q\2\2\u0302\u0303\7o\2\2\u0303\u0304"+
		"\7g\2\2\u0304\u00b0\3\2\2\2\u0305\u0306\7u\2\2\u0306\u0307\7g\2\2\u0307"+
		"\u0308\7n\2\2\u0308\u0309\7h\2\2\u0309\u00b2\3\2\2\2\u030a\u030b\7v\2"+
		"\2\u030b\u030c\7j\2\2\u030c\u030d\7k\2\2\u030d\u030e\7u\2\2\u030e\u00b4"+
		"\3\2\2\2\u030f\u0310\7c\2\2\u0310\u0311\7e\2\2\u0311\u0312\7v\2\2\u0312"+
		"\u0313\7k\2\2\u0313\u0314\7q\2\2\u0314\u0315\7p\2\2\u0315\u0316\7u\2\2"+
		"\u0316\u0317\7r\2\2\u0317\u0318\7g\2\2\u0318\u0319\7e\2\2\u0319\u00b6"+
		"\3\2\2\2\u031a\u031b\7B\2\2\u031b\u031c\7g\2\2\u031c\u031d\7p\2\2\u031d"+
		"\u031e\7x\2\2\u031e\u00b8\3\2\2\2\u031f\u0320\7B\2\2\u0320\u0321\7k\2"+
		"\2\u0321\u0322\7p\2\2\u0322\u0323\7v\2\2\u0323\u00ba\3\2\2\2\u0324\u0325"+
		"\7r\2\2\u0325\u0326\7t\2\2\u0326\u0327\7g\2\2\u0327\u0328\3\2\2\2\u0328"+
		"\u0329\b]\4\2\u0329\u00bc\3\2\2\2\u032a\u032b\7r\2\2\u032b\u032c\7q\2"+
		"\2\u032c\u032d\7u\2\2\u032d\u032e\7v\2\2\u032e\u032f\3\2\2\2\u032f\u0330"+
		"\b^\4\2\u0330\u00be\3\2\2\2\u0331\u0334\5\35\16\2\u0332\u0334\5\37\17"+
		"\2\u0333\u0331\3\2\2\2\u0333\u0332\3\2\2\2\u0334\u033a\3\2\2\2\u0335\u0339"+
		"\5\35\16\2\u0336\u0339\5\'\23\2\u0337\u0339\5\37\17\2\u0338\u0335\3\2"+
		"\2\2\u0338\u0336\3\2\2\2\u0338\u0337\3\2\2\2\u0339\u033c\3\2\2\2\u033a"+
		"\u0338\3\2\2\2\u033a\u033b\3\2\2\2\u033b\u033d\3\2\2\2\u033c\u033a\3\2"+
		"\2\2\u033d\u033e\b_\6\2\u033e\u00c0\3\2\2\2\u033f\u0341\5M&\2\u0340\u033f"+
		"\3\2\2\2\u0340\u0341\3\2\2\2\u0341\u0342\3\2\2\2\u0342\u0343\59\34\2\u0343"+
		"\u0344\b`\7\2\u0344\u00c2\3\2\2\2\u0345\u0347\5;\35\2\u0346\u0348\5M&"+
		"\2\u0347\u0346\3\2\2\2\u0347\u0348\3\2\2\2\u0348\u0349\3\2\2\2\u0349\u034a"+
		"\ba\b\2\u034a\u00c4\3\2\2\2\u034b\u034c\13\2\2\2\u034c\u00c6\3\2\2\2\u034d"+
		"\u034f\5M&\2\u034e\u034d\3\2\2\2\u034e\u034f\3\2\2\2\u034f\u0350\3\2\2"+
		"\2\u0350\u0351\5\65\32\2\u0351\u0352\bc\t\2\u0352\u00c8\3\2\2\2\u0353"+
		"\u0355\5\67\33\2\u0354\u0356\5M&\2\u0355\u0354\3\2\2\2\u0355\u0356\3\2"+
		"\2\2\u0356\u0357\3\2\2\2\u0357\u0358\bd\n\2\u0358\u00ca\3\2\2\2\u0359"+
		"\u035a\13\2\2\2\u035a\u00cc\3\2\2\2\u035b\u035d\5M&\2\u035c\u035b\3\2"+
		"\2\2\u035c\u035d\3\2\2\2\u035d\u035e\3\2\2\2\u035e\u035f\59\34\2\u035f"+
		"\u0360\bf\13\2\u0360\u00ce\3\2\2\2\u0361\u0363\5;\35\2\u0362\u0364\5M"+
		"&\2\u0363\u0362\3\2\2\2\u0363\u0364\3\2\2\2\u0364\u0365\3\2\2\2\u0365"+
		"\u0366\bg\f\2\u0366\u00d0\3\2\2\2\u0367\u0368\13\2\2\2\u0368\u0369\bh"+
		"\r\2\u0369\u00d2\3\2\2\2\u036a\u036c\5M&\2\u036b\u036a\3\2\2\2\u036b\u036c"+
		"\3\2\2\2\u036c\u036d\3\2\2\2\u036d\u036e\5\65\32\2\u036e\u036f\bi\16\2"+
		"\u036f\u00d4\3\2\2\2\u0370\u0372\5\67\33\2\u0371\u0373\5M&\2\u0372\u0371"+
		"\3\2\2\2\u0372\u0373\3\2\2\2\u0373\u0374\3\2\2\2\u0374\u0375\bj\17\2\u0375"+
		"\u00d6\3\2\2\2\u0376\u0377\13\2\2\2\u0377\u0378\bk\20\2\u0378\u00d8\3"+
		"\2\2\2\34\2\3\4\u0133\u013c\u0140\u0147\u016d\u016f\u0178\u017a\u0183"+
		"\u0187\u0193\u019e\u0333\u0338\u033a\u0340\u0347\u034e\u0355\u035c\u0363"+
		"\u036b\u0372\21\3\2\2\2\3\2\7\3\2\7\4\2\3_\3\3`\4\3a\5\3c\6\3d\7\3f\b"+
		"\3g\t\3h\n\3i\13\3j\f\3k\r";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}