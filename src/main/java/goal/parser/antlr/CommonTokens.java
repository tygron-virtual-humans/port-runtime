// Generated from CommonTokens.g4 by ANTLR 4.4
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
public class CommonTokens extends Lexer {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		FLOAT=1, INT=2, COLON=3, PLUS=4, MINUS=5, EQUALS=6, DOT=7, COMMA=8, LBR=9, 
		RBR=10, CLBR=11, CRBR=12, SLBR=13, SRBR=14, RTLARROW=15, LTRARROW=16, 
		SINGLESTRING=17, DOUBLESTRING=18, LINE_COMMENT=19, BLOCK_COMMENT=20, WS=21;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'", "'\\u0005'", 
		"'\\u0006'", "'\\u0007'", "'\b'", "'\t'", "'\n'", "'\\u000B'", "'\f'", 
		"'\r'", "'\\u000E'", "'\\u000F'", "'\\u0010'", "'\\u0011'", "'\\u0012'", 
		"'\\u0013'", "'\\u0014'", "'\\u0015'"
	};
	public static final String[] ruleNames = {
		"ALPHA", "SCORE", "FLOAT", "INT", "DIGITS", "DIGIT", "COLON", "PLUS", 
		"MINUS", "EQUALS", "DOT", "COMMA", "LBR", "RBR", "CLBR", "CRBR", "SLBR", 
		"SRBR", "RTLARROW", "LTRARROW", "SINGLESTRING", "DOUBLESTRING", "LINE_COMMENT", 
		"BLOCK_COMMENT", "WS"
	};


	public CommonTokens(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "CommonTokens.g4"; }

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

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\27\u00aa\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\3\2\3\2\3\3\3\3\3\4\3\4\5\4<\n\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\5\4E\n\4\3\5\3\5\5\5I\n\5\3\5\3\5\3\6\6\6N\n\6\r\6\16\6O\3\7\3"+
		"\7\3\b\3\b\3\t\3\t\3\n\3\n\3\13\3\13\3\f\3\f\3\r\3\r\3\16\3\16\3\17\3"+
		"\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3\24\3\25\3\25\3"+
		"\25\3\26\3\26\3\26\3\26\7\26v\n\26\f\26\16\26y\13\26\3\26\3\26\3\27\3"+
		"\27\3\27\3\27\7\27\u0081\n\27\f\27\16\27\u0084\13\27\3\27\3\27\3\30\3"+
		"\30\7\30\u008a\n\30\f\30\16\30\u008d\13\30\3\30\5\30\u0090\n\30\3\30\3"+
		"\30\3\30\3\30\3\31\3\31\3\31\3\31\7\31\u009a\n\31\f\31\16\31\u009d\13"+
		"\31\3\31\3\31\3\31\3\31\3\31\3\32\6\32\u00a5\n\32\r\32\16\32\u00a6\3\32"+
		"\3\32\5w\u0082\u009b\2\33\3\2\5\2\7\3\t\4\13\2\r\2\17\5\21\6\23\7\25\b"+
		"\27\t\31\n\33\13\35\f\37\r!\16#\17%\20\'\21)\22+\23-\24/\25\61\26\63\27"+
		"\3\2\6\4\2C\\c|\3\2\62;\4\2\f\f\17\17\5\2\13\f\16\17\"\"\u00b3\2\7\3\2"+
		"\2\2\2\t\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2"+
		"\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2"+
		"\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2"+
		"\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3\2\2\2\3\65\3\2\2\2\5\67\3\2\2\2\7D\3"+
		"\2\2\2\tH\3\2\2\2\13M\3\2\2\2\rQ\3\2\2\2\17S\3\2\2\2\21U\3\2\2\2\23W\3"+
		"\2\2\2\25Y\3\2\2\2\27[\3\2\2\2\31]\3\2\2\2\33_\3\2\2\2\35a\3\2\2\2\37"+
		"c\3\2\2\2!e\3\2\2\2#g\3\2\2\2%i\3\2\2\2\'k\3\2\2\2)n\3\2\2\2+q\3\2\2\2"+
		"-|\3\2\2\2/\u0087\3\2\2\2\61\u0095\3\2\2\2\63\u00a4\3\2\2\2\65\66\t\2"+
		"\2\2\66\4\3\2\2\2\678\7a\2\28\6\3\2\2\29<\5\21\t\2:<\5\23\n\2;9\3\2\2"+
		"\2;:\3\2\2\2;<\3\2\2\2<=\3\2\2\2=>\5\13\6\2>?\5\27\f\2?@\5\13\6\2@E\3"+
		"\2\2\2AB\5\27\f\2BC\5\13\6\2CE\3\2\2\2D;\3\2\2\2DA\3\2\2\2E\b\3\2\2\2"+
		"FI\5\21\t\2GI\5\23\n\2HF\3\2\2\2HG\3\2\2\2HI\3\2\2\2IJ\3\2\2\2JK\5\13"+
		"\6\2K\n\3\2\2\2LN\5\r\7\2ML\3\2\2\2NO\3\2\2\2OM\3\2\2\2OP\3\2\2\2P\f\3"+
		"\2\2\2QR\t\3\2\2R\16\3\2\2\2ST\7<\2\2T\20\3\2\2\2UV\7-\2\2V\22\3\2\2\2"+
		"WX\7/\2\2X\24\3\2\2\2YZ\7?\2\2Z\26\3\2\2\2[\\\7\60\2\2\\\30\3\2\2\2]^"+
		"\7.\2\2^\32\3\2\2\2_`\7*\2\2`\34\3\2\2\2ab\7+\2\2b\36\3\2\2\2cd\7}\2\2"+
		"d \3\2\2\2ef\7\177\2\2f\"\3\2\2\2gh\7]\2\2h$\3\2\2\2ij\7_\2\2j&\3\2\2"+
		"\2kl\7>\2\2lm\7/\2\2m(\3\2\2\2no\7/\2\2op\7@\2\2p*\3\2\2\2qw\7)\2\2rs"+
		"\7^\2\2sv\7)\2\2tv\13\2\2\2ur\3\2\2\2ut\3\2\2\2vy\3\2\2\2wx\3\2\2\2wu"+
		"\3\2\2\2xz\3\2\2\2yw\3\2\2\2z{\7)\2\2{,\3\2\2\2|\u0082\7$\2\2}~\7^\2\2"+
		"~\u0081\7$\2\2\177\u0081\13\2\2\2\u0080}\3\2\2\2\u0080\177\3\2\2\2\u0081"+
		"\u0084\3\2\2\2\u0082\u0083\3\2\2\2\u0082\u0080\3\2\2\2\u0083\u0085\3\2"+
		"\2\2\u0084\u0082\3\2\2\2\u0085\u0086\7$\2\2\u0086.\3\2\2\2\u0087\u008b"+
		"\7\'\2\2\u0088\u008a\n\4\2\2\u0089\u0088\3\2\2\2\u008a\u008d\3\2\2\2\u008b"+
		"\u0089\3\2\2\2\u008b\u008c\3\2\2\2\u008c\u008f\3\2\2\2\u008d\u008b\3\2"+
		"\2\2\u008e\u0090\7\17\2\2\u008f\u008e\3\2\2\2\u008f\u0090\3\2\2\2\u0090"+
		"\u0091\3\2\2\2\u0091\u0092\7\f\2\2\u0092\u0093\3\2\2\2\u0093\u0094\b\30"+
		"\2\2\u0094\60\3\2\2\2\u0095\u0096\7\61\2\2\u0096\u0097\7,\2\2\u0097\u009b"+
		"\3\2\2\2\u0098\u009a\13\2\2\2\u0099\u0098\3\2\2\2\u009a\u009d\3\2\2\2"+
		"\u009b\u009c\3\2\2\2\u009b\u0099\3\2\2\2\u009c\u009e\3\2\2\2\u009d\u009b"+
		"\3\2\2\2\u009e\u009f\7,\2\2\u009f\u00a0\7\61\2\2\u00a0\u00a1\3\2\2\2\u00a1"+
		"\u00a2\b\31\2\2\u00a2\62\3\2\2\2\u00a3\u00a5\t\5\2\2\u00a4\u00a3\3\2\2"+
		"\2\u00a5\u00a6\3\2\2\2\u00a6\u00a4\3\2\2\2\u00a6\u00a7\3\2\2\2\u00a7\u00a8"+
		"\3\2\2\2\u00a8\u00a9\b\32\2\2\u00a9\64\3\2\2\2\17\2;DHOuw\u0080\u0082"+
		"\u008b\u008f\u009b\u00a6\3\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}