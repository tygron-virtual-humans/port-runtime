package goal.parser.goal;

import goal.core.kr.KRlanguage;
import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Query;
import goal.core.kr.language.Term;
import goal.core.kr.language.Update;
import goal.core.program.validation.ValidatorError;
import goal.core.program.validation.agentfile.GOALError;
import goal.parser.EmbeddedKRParser;
import goal.parser.InputStreamPosition;
import goal.parser.WalkerHelper;
import goal.tools.errorhandling.exceptions.ParserException;
import goal.util.ImportCommand;

import java.io.File;
import java.io.StringReader;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.v4.runtime.tree.TerminalNode;

public class WalkerHelperKR extends WalkerHelper {
	protected KRlanguage kr;

	public WalkerHelperKR(File file, KRlanguage kr) {
		super(file);
		this.kr = kr;
	}

	public KRlanguage getKR() {
		return this.kr;
	}

	public void setKR(KRlanguage krlang) {
		this.kr = krlang;
	}

	public static String implode(List<TerminalNode> characters) {
		StringBuilder builder = new StringBuilder();
		for (TerminalNode character : characters) {
			if (character != null) {
				builder.append(character.getText());
			}
		}
		return builder.toString();
	}

	protected EmbeddedKRParser getKRParser(String krcontent) throws Exception {
		StringReader reader = new StringReader(krcontent);
		return this.kr.getParser(new ANTLRReaderStream(reader), this.file);
	}

	public InputStreamPosition getPosition(ParserException error,
			InputStreamPosition original) {
		org.antlr.runtime.Token token = error.token;
		int length = 0;
		if (token != null && token.getText() != null) {
			length = token.getText().length();
		}
		if (error.getSourceFile() != null
				&& !original.getSourceFile().equals(error.getSourceFile())) {
			return new InputStreamPosition(error.line,
					error.charPositionInLine, error.index,
					error.index + length, error.getSourceFile());
		} else {
			final File source = error.getSourceFile() == null ? original
					.getSourceFile() : error.getSourceFile();
			return new InputStreamPosition(error.line
					+ original.getLineNumber() - 1, error.charPositionInLine,
					error.index + original.getStartIndex(), error.index
							+ length + original.getStartIndex(), source);
		}
	}

	private void error(InputStreamPosition source, String message) {
		report(new ValidatorError(GOALError.EXTERNAL_OR_UNKNOWN, source,
				message)); // TODO: refactor Prolog to use enum-errors?
	}

	protected void takeExceptions(EmbeddedKRParser parser,
			InputStreamPosition original) {
		List<ParserException> pErrors = parser.getParserErrors();
		List<ParserException> lErrors = parser.getLexerErrors();
		if (!pErrors.isEmpty() || !lErrors.isEmpty()) {
			if (!lErrors.isEmpty()) {
				if (pErrors.isEmpty()) {
					for (ParserException lError : lErrors) {
						String lMsg = lError.getReason();
						if (lMsg == null || lMsg.isEmpty()) {
							lMsg = lError.getMessage();
						}
						error(getPosition(lError, original), lMsg);
					}
				} else {
					ParserException firstParserEx = pErrors.get(0);
					for (ParserException lError : lErrors) {
						if (lError.occurredBefore(firstParserEx)) {
							String lMsg = lError.getReason();
							if (lMsg == null || lMsg.isEmpty()) {
								lMsg = lError.getMessage();
							}
							error(getPosition(lError, original), lMsg);
						} else {
							break;
						}
					}
				}
			}
			if (!pErrors.isEmpty()) {
				for (ParserException pError : pErrors) {
					String pMsg = pError.getReason();
					if (pMsg == null || pMsg.isEmpty()) {
						pMsg = pError.getMessage();
					}
					error(getPosition(pError, original), pMsg);
				}
			}
		} else {
			final int index = parser.getSourceStream().index();
			final int size = parser.getSourceStream().size();
			if (index > 0 && size > 0 && Math.abs(size - index) > 1) {
				this.error(original, "Unrecognized spurious input");
			}
		}
	}

	public Map.Entry<List<DatabaseFormula>, List<ImportCommand>> parseProgram(
			String krcontent, InputStreamPosition source) {
		try {
			EmbeddedKRParser parser = getKRParser(krcontent);
			List<DatabaseFormula> formulas = parser.parseProgram();
			if (formulas != null) {
				for (DatabaseFormula formula : formulas) {
					formula.setSource(getCorrectedPosition(formula.getSource(),
							krcontent, source));
				}
			}
			List<ImportCommand> imports = parser.getAllImports();
			if (imports != null) {
				for (ImportCommand importer : imports) {
					importer.setSource(getCorrectedPosition(
							importer.getSource(), krcontent, source));
				}
			}
			takeExceptions(parser, source);
			return new AbstractMap.SimpleEntry<>(formulas, imports);
		} catch (Exception e) {
			error(source, e.getMessage());
			return null;
		}
	}

	public Map.Entry<List<DatabaseFormula>, List<ImportCommand>> parseProgram(
			List<TerminalNode> characters) {
		return parseProgram(implode(characters), getPosition(characters));
	}

	public List<Update> parseGoalSection(String krcontent,
			InputStreamPosition source) {
		try {
			EmbeddedKRParser parser = getKRParser(krcontent);
			List<Update> goals = parser.parseGoals();
			if (goals != null) {
				for (Update goal : goals) {
					goal.setSource(getCorrectedPosition(goal.getSource(),
							krcontent, source));
				}
			}
			takeExceptions(parser, source);
			return goals;
		} catch (Exception e) {
			error(source, e.getMessage());
			return null;
		}
	}

	public List<Update> parseGoalSection(List<TerminalNode> characters) {
		return parseGoalSection(implode(characters), getPosition(characters));
	}

	public Update parseGoalUpdate(String krcontent, InputStreamPosition source) {
		try {
			EmbeddedKRParser parser = getKRParser(krcontent);
			Update term = parser.parseGoal();
			if (term != null) {
				term.setSource(getCorrectedPosition(term.getSource(),
						krcontent, source));
			}
			takeExceptions(parser, source);
			return term;
		} catch (Exception e) {
			error(source, e.getMessage());
			return null;
		}
	}

	public Update parseGoalUpdate(List<TerminalNode> characters) {
		return parseGoalUpdate(implode(characters), getPosition(characters));
	}

	public Update parseBeliefUpdate(String krcontent, InputStreamPosition source) {
		try {
			EmbeddedKRParser parser = getKRParser(krcontent);
			Update term = parser.parseBelief();
			if (term != null) {
				term.setSource(getCorrectedPosition(term.getSource(),
						krcontent, source));
			}
			takeExceptions(parser, source);
			return term;
		} catch (Exception e) {
			error(source, e.getMessage());
			return null;
		}
	}

	public Update parseBeliefUpdate(List<TerminalNode> characters) {
		return parseBeliefUpdate(implode(characters), getPosition(characters));
	}

	public Update parseBeliefUpdateOrEmpty(String krcontent,
			InputStreamPosition source) {
		try {
			EmbeddedKRParser parser = getKRParser(krcontent);
			Update term = parser.parseBeliefOrEmpty();
			if (term != null) {
				term.setSource(getCorrectedPosition(term.getSource(),
						krcontent, source));
			}
			takeExceptions(parser, source);
			return term;
		} catch (Exception e) {
			error(source, e.getMessage());
			return null;
		}
	}

	public Update parseBeliefUpdateOrEmpty(List<TerminalNode> characters) {
		InputStreamPosition pos = null;
		if (!characters.isEmpty()) { // OrEmpty
			pos = getPosition(characters);
		}
		return parseBeliefUpdateOrEmpty(implode(characters), pos);
	}

	public Query parseQuery(String krcontent, InputStreamPosition source) {
		try {
			EmbeddedKRParser parser = getKRParser(krcontent);
			Query term = parser.parseQuery();
			if (term != null) {
				term.setSource(getCorrectedPosition(term.getSource(),
						krcontent, source));
			}
			takeExceptions(parser, source);
			return term;
		} catch (Exception e) {
			error(source, e.getMessage());
			return null;
		}
	}

	public Query parseQuery(List<TerminalNode> characters) {
		return parseQuery(implode(characters), getPosition(characters));
	}

	private Query parseQueryOrEmpty(String krcontent, InputStreamPosition source) {
		try {
			EmbeddedKRParser parser = getKRParser(krcontent);
			Query term = parser.parseQueryOrEmpty();
			if (term != null) {
				term.setSource(getCorrectedPosition(term.getSource(),
						krcontent, source));
			}
			takeExceptions(parser, source);
			return term;
		} catch (Exception e) {
			error(source, e.getMessage());
			return null;
		}
	}

	public Query parseQueryOrEmpty(List<TerminalNode> characters) {
		InputStreamPosition pos = null;
		if (!characters.isEmpty()) { // OrEmpty
			pos = getPosition(characters);
		}
		return parseQueryOrEmpty(implode(characters), pos);
	}

	private Term parseTerm(String krcontent, InputStreamPosition source) {
		try {
			EmbeddedKRParser parser = getKRParser(krcontent);
			Term term = parser.parseTerm();
			if (term != null) {
				term.setSource(getCorrectedPosition(term.getSource(),
						krcontent, source));
			}
			takeExceptions(parser, source);
			return term;
		} catch (Exception e) {
			error(source, e.getMessage());
			return null;
		}
	}

	public Term parseTerm(List<TerminalNode> characters) {
		return parseTerm(implode(characters), getPosition(characters));
	}

	public List<Term> parseTerms(String krcontent, InputStreamPosition source) {
		try {
			EmbeddedKRParser parser = getKRParser(krcontent);
			List<Term> terms = parser.parseTerms();
			if (terms != null) {
				for (Term term : terms) {
					term.setSource(getCorrectedPosition(term.getSource(),
							krcontent, source));
				}
			}
			takeExceptions(parser, source);
			return terms;
		} catch (Exception e) {
			error(source, e.getMessage());
			return null;
		}
	}

	public List<Term> parseTerms(List<TerminalNode> characters) {
		return parseTerms(implode(characters), getPosition(characters));
	}

	public DatabaseFormula parseMood(String krcontent,
			InputStreamPosition source) {
		try {
			EmbeddedKRParser parser = getKRParser(krcontent);
			DatabaseFormula term = parser.parseMood();
			if (term != null) {
				term.setSource(getCorrectedPosition(term.getSource(),
						krcontent, source));
			}
			takeExceptions(parser, source);
			return term;
		} catch (Exception e) {
			error(source, e.getMessage());
			return null;
		}
	}

	public DatabaseFormula parseMood(List<TerminalNode> characters) {
		return parseMood(implode(characters), getPosition(characters));
	}

	private static InputStreamPosition getCorrectedPosition(
			InputStreamPosition KR, String content, InputStreamPosition original) {
		final String[] split = content.split("\n");
		List<Integer> lines = new ArrayList<>(split.length);
		int curr = 0;
		for (String line : split) {
			lines.add(curr);
			curr += line.length() + 1;
		}
		File file = null;
		int line = 0, column = 0, index = 0, length = 0;
		if (KR != null) {
			file = KR.getSourceFile();
			line = KR.getLineNumber() - 1;
			column = KR.getCharacterPosition();
			index = KR.getStartIndex();
			length = KR.getStopIndex() - index;
		} else if (original != null) {
			file = original.getSourceFile();
			line = original.getLineNumber() - 1;
			column = original.getCharacterPosition();
			index = original.getStartIndex();
			length = original.getStopIndex() - index;
		}
		if (lines.size() > line) {
			index = lines.get(line) + column;
		}
		InputStreamPosition returned = new InputStreamPosition(line, column,
				index, index + length, file);
		returned.add(original);
		return returned;
	}
}
