package goal.parser.unittest;

import goal.core.kr.KRlanguage;
import goal.core.program.actions.ActionCombo;
import goal.core.program.literals.MentalStateCond;
import goal.core.program.validation.ValidatorError;
import goal.core.program.validation.ValidatorWarning;
import goal.core.program.validation.agentfile.GOALError;
import goal.parser.InputStreamPosition;
import goal.parser.WalkerHelper;
import goal.parser.antlr.GOALLexer;
import goal.parser.antlr.GOALParser;
import goal.parser.antlr.UnitTestParser.ActionsContext;
import goal.parser.antlr.UnitTestParser.ConditionsContext;
import goal.parser.goal.GOALWalker;

import java.io.File;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class WalkerHelperGOAL extends WalkerHelper {
	private final KRlanguage kr;

	public WalkerHelperGOAL(File file, KRlanguage kr) {
		super(file);
		this.kr = kr;
	}

	protected void takeExceptions(GOALWalker parser,
			InputStreamPosition original) {
		for (ValidatorError e : parser.getErrors()) {
			e.setPosition(addPosition(original, e.getPosition()));
			report(e);
		}
		for (ValidatorWarning e : parser.getWarnings()) {
			e.setPosition(addPosition(original, e.getPosition()));
			report(e);
		}
	}

	private InputStreamPosition addPosition(InputStreamPosition a,
			InputStreamPosition b) {
		int line = b.getLineNumber() + a.getLineNumber() - 1;
		int characterPosition = b.getCharacterPosition()
				+ a.getCharacterPosition() - 1;
		int start = b.getStartIndex() + a.getStartIndex();
		int stop = b.getStopIndex() + a.getStopIndex();
		InputStreamPosition sum = new InputStreamPosition(line,
				characterPosition, start, stop, file);
		return sum;
	}

	protected GOALWalker getGOALWalker(String goalContent) throws Exception {
		CharStream cs = new ANTLRInputStream(goalContent);
		GOALLexer lexer = new GOALLexer(cs);
		CommonTokenStream stream = new CommonTokenStream(lexer);
		GOALParser parser = new GOALParser(stream);
		GOALWalker walker = new GOALWalker(file, parser, lexer, kr);
		return walker;
	}

	public ActionCombo parseActions(String goalContent, ActionsContext context) {
		return parseActions(goalContent, getPosition(context));
	}

	public ActionCombo parseActions(String goalContent,
			InputStreamPosition source) {
		try {
			GOALWalker walker = getGOALWalker(goalContent);

			ActionCombo comboaction = walker.visitActions(walker.getParser()
					.actions());
			takeExceptions(walker, source);

			return comboaction;
		} catch (Exception e) {
			report(new ValidatorError(GOALError.EXTERNAL_OR_UNKNOWN, source,
					e.getMessage()));
			return null;
		}
	}

	public MentalStateCond parseConditions(String goalContent,
			InputStreamPosition source) {
		try {
			GOALWalker walker = getGOALWalker(goalContent);
			MentalStateCond mentalStateCond = walker.visitConditions(walker
					.getParser().conditions());
			takeExceptions(walker, source);

			return mentalStateCond;
		} catch (Exception e) {
			report(new ValidatorError(GOALError.EXTERNAL_OR_UNKNOWN, source,
					e.getMessage()));
			return null;
		}
	}

	public MentalStateCond parseConditions(String goalContent,
			ConditionsContext context) {
		return parseConditions(goalContent, getPosition(context));

	}

}
