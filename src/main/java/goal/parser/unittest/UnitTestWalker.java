package goal.parser.unittest;

import goal.core.kr.KRFactory;
import goal.core.kr.KRlanguage;
import goal.core.kr.language.Term;
import goal.core.kr.language.Var;
import goal.core.mas.AgentFile;
import goal.core.mas.Launch;
import goal.core.mas.MASProgram;
import goal.core.mas.MultiLaunch;
import goal.core.program.GOALProgram;
import goal.core.program.Module;
import goal.core.program.Module.TYPE;
import goal.core.program.NameSpace;
import goal.core.program.actions.ActionCombo;
import goal.core.program.actions.ModuleCallAction;
import goal.core.program.actions.UserSpecOrModuleCall;
import goal.core.program.literals.MentalFormula;
import goal.core.program.literals.MentalStateCond;
import goal.core.program.literals.TrueLiteral;
import goal.core.program.rules.IfThenRule;
import goal.core.program.validation.ValidatorError;
import goal.core.program.validation.ValidatorWarning;
import goal.core.program.validation.agentfile.ActionComboValidator;
import goal.core.program.validation.agentfile.GOALError;
import goal.parser.InputStreamPosition;
import goal.parser.WalkerInterface;
import goal.parser.antlr.UnitTestParser.ActionsContext;
import goal.parser.antlr.UnitTestParser.AgentTestContext;
import goal.parser.antlr.UnitTestParser.AgentTestsContext;
import goal.parser.antlr.UnitTestParser.AssertTestContext;
import goal.parser.antlr.UnitTestParser.DoActionsContext;
import goal.parser.antlr.UnitTestParser.EvaluateInContext;
import goal.parser.antlr.UnitTestParser.LtlAlwaysContext;
import goal.parser.antlr.UnitTestParser.LtlAtEndContext;
import goal.parser.antlr.UnitTestParser.LtlAtStartContext;
import goal.parser.antlr.UnitTestParser.LtlContext;
import goal.parser.antlr.UnitTestParser.LtlEventuallyContext;
import goal.parser.antlr.UnitTestParser.LtlModuleContext;
import goal.parser.antlr.UnitTestParser.LtlNeverContext;
import goal.parser.antlr.UnitTestParser.LtlUntilContext;
import goal.parser.antlr.UnitTestParser.LtlWhileContext;
import goal.parser.antlr.UnitTestParser.MasFileContext;
import goal.parser.antlr.UnitTestParser.TestConditionContext;
import goal.parser.antlr.UnitTestParser.TestContext;
import goal.parser.antlr.UnitTestParser.TestSectionContext;
import goal.parser.antlr.UnitTestParser.TimeoutContext;
import goal.parser.antlr.UnitTestParser.UnitTestContext;
import goal.parser.antlr.UnitTestParserBaseVisitor;
import goal.parser.goal.WalkerHelperKR;
import goal.preferences.PMPreferences;
import goal.tools.PlatformManager;
import goal.tools.errorhandling.exceptions.GOALParseException;
import goal.tools.unittest.AgentTest;
import goal.tools.unittest.Test;
import goal.tools.unittest.UnitTest;
import goal.tools.unittest.testsection.AssertTest;
import goal.tools.unittest.testsection.DoActionSection;
import goal.tools.unittest.testsection.EvaluateIn;
import goal.tools.unittest.testsection.TestSection;
import goal.tools.unittest.testsection.testconditions.Always;
import goal.tools.unittest.testsection.testconditions.AtEnd;
import goal.tools.unittest.testsection.testconditions.AtStart;
import goal.tools.unittest.testsection.testconditions.Eventually;
import goal.tools.unittest.testsection.testconditions.Never;
import goal.tools.unittest.testsection.testconditions.TestCondition;
import goal.tools.unittest.testsection.testconditions.Until;
import goal.tools.unittest.testsection.testconditions.While;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTree;

@SuppressWarnings({ "rawtypes", "synthetic-access" })
public class UnitTestWalker extends UnitTestParserBaseVisitor implements
WalkerInterface {
	private final WalkerHelperKR wh;
	private final File unitTest2g;
	private MASProgram masProgram;

	/**
	 * Creates a walker for a unit test.
	 *
	 * @param unitTest2g
	 *            File that contains unit test.
	 */
	public UnitTestWalker(File unitTest2g) {
		this.unitTest2g = unitTest2g;
		this.wh = new WalkerHelperKR(unitTest2g, KRFactory.get(PMPreferences
				.getDefaultKRLanguage()));
	}

	public void overrideMAS(MASProgram mas2g) {
		this.masProgram = mas2g;
	}

	@Override
	public UnitTest visitUnitTest(UnitTestContext ctx) {
		if (ctx == null) {
			return null;
		} else if (ctx.masFile() == null) {
			error(ctx, "Missing MAS file declaration");
			return null;
		}
		PlatformManager platform = PlatformManager.getCurrent();
		try {
			if (this.masProgram == null) {
				File masFile = visitMasFile(ctx.masFile());
				if (masFile == null) {
					// Error covered by visitor.
					return null;
				}

				// Parse mas program and children.
				try {
					masProgram = platform.parseMASFile(masFile);
					if (!masProgram.isValidated()) {
						error(ctx.masFile(), "MAS file " + masFile
								+ " is not valid");
						return null;
					}
				} catch (GOALParseException e) {
					error(ctx.masFile(), "MAS file " + masFile
							+ " could not be parsed " + e.getMessage());
					return null;
				}
			}

			for (AgentFile agentFile : masProgram.getAgentFiles()) {
				try {
					platform.parseGOALFile(agentFile.getAgentFile(),
							agentFile.getKRLang());
				} catch (GOALParseException e) {
					error(ctx.masFile(),
							"Agent file "
									+ agentFile
									+ " could not be parsed or has parse errors "
									+ e.getMessage());
				}
			}

			Long timeout = 0L;
			if (ctx.timeout() != null) {
				timeout = visitTimeout(ctx.timeout());
				if (timeout == null) {
					return null;
				}
			}

			if (ctx.agentTests() == null) {
				return new UnitTest(this.wh.getFile(), masProgram, timeout);
			}

			AgentTestWalker testWalker = new AgentTestWalker(masProgram);
			List<AgentTests> tests = testWalker.visitAgentTests(ctx
					.agentTests());
			if (tests == null) { // Error covered by visitor.
				return null;
			}

			if (tests.isEmpty() || tests.get(0).isEmpty()) {
				return new UnitTest(wh.getFile(), masProgram,
						new ArrayList<AgentTest>(0), timeout);
			}

			// We now have a list of tests for each agent. These have to be
			// transformed into a list of unit tests containing one test for
			// each agent.
			List<UnitTest> unitTests = new ArrayList<>(tests.get(0).size());
			for (int i = 0; i < tests.get(0).size(); i++) {
				List<AgentTest> agentTests = new ArrayList<>(tests.size());
				for (int j = 0; j < tests.size(); j++) {
					agentTests.add(tests.get(j).get(i));
				}
				unitTests.add(new UnitTest(wh.getFile(), masProgram,
						agentTests, timeout));
			}
			// FIXME: For now only allow 1 unit test. Silently ignores any other
			// unit tests!
			return unitTests.get(0);
		} catch (Exception any) {
			this.wh.report(new ValidatorError(GOALError.EXTERNAL_OR_UNKNOWN,
					this.wh.getPosition(ctx), any.getMessage()));
			return null;
		}
	}

	@Override
	public File visitMasFile(MasFileContext ctx) {
		if (ctx.DOUBLESTRING() == null) {
			error(ctx, "Missing MAS file value");
			return null;
		}

		// FIXME: Parser should strip quotes.
		String masFileName = ctx.DOUBLESTRING().getText().replace("\"", "");
		File masFile = PlatformManager.resolveFileReference(this.wh.getFile()
				.getParent(), masFileName);

		if (masFile == null) {
			error(ctx, "MAS file " + masFileName + " does not exist");
			return null;
		}
		return masFile;
	}

	@Override
	public Long visitTimeout(TimeoutContext ctx) {
		if (ctx.INT() == null) {
			error(ctx, "Expected a number for timeout");
			return null;
		}
		try {
			return Long.parseLong(ctx.INT().getText()) * 1000;
		} catch (NumberFormatException e) {
			error(ctx, "Could not parse " + ctx.INT().getText() + " to number");
			return null;
		}
	}

	private class AgentTests {
		private final List<AgentTest> tests;
		private final String agentBaseName;

		public AgentTests(String agentBaseName, List<AgentTest> tests) {
			this.tests = tests;
			this.agentBaseName = agentBaseName;
		}

		public boolean isEmpty() {
			return tests.isEmpty();
		}

		public AgentTests(String agentName) {
			this(agentName, new ArrayList<AgentTest>(0));
		}

		public String getName() {
			return agentBaseName;
		}

		public int size() {
			return tests.size();
		}

		public AgentTest get(int i) {
			return tests.get(i);
		}
	}

	private class AgentTestWalker extends UnitTestParserBaseVisitor {
		@SuppressWarnings("hiding")
		private final MASProgram masProgram;

		public AgentTestWalker(MASProgram masProgram) {
			this.masProgram = masProgram;
		}

		@Override
		public List<AgentTests> visitAgentTests(AgentTestsContext ctx) {
			List<AgentTests> tests = new ArrayList<>(ctx.agentTest().size());
			for (AgentTestContext testCtx : ctx.agentTest()) {
				AgentTests agentTests = visitAgentTest(testCtx);
				if (agentTests == null) {
					return null;
				}
				for (AgentTests t : tests) {
					// Check duplicates
					if (agentTests.getName().equals(t.getName())) {
						error(testCtx, "Found duplicate test for agent "
								+ agentTests.getName());
						return null;
					}
					// Check size matches other tests
					if (agentTests.size() != t.size()) {
						error(testCtx,
								agentTests.getName() + " has "
										+ agentTests.size() + " tests while "
										+ t.getName() + " has " + t.size());
						return null;
					}
				}
				tests.add(agentTests);
			}
			return tests;
		}

		@Override
		public AgentTests visitAgentTest(AgentTestContext ctx) {
			if (ctx.ID() == null) {
				error(ctx, "Missing agent declaration");
				return null;
			}

			Launch launch = getLaunch(ctx.ID().getText(), ctx);
			if (launch == null) {
				// Error covered by visitor.
				return null;
			}

			String agentName = launch.getAgentBaseName();
			AgentFile agentFile = launch.getAgentFile();

			KRlanguage agentKRlanguage = agentFile.getKRLang();
			File file = agentFile.getAgentFile();
			GOALProgram goalProgram;
			try {
				goalProgram = PlatformManager.getCurrent().parseGOALFile(file,
						agentKRlanguage);
			} catch (GOALParseException e) {
				error(ctx, "Agent program in " + file + " was invalid");
				return null;
			}

			// TODO: No context or empty context? Which one happens when?
			if (ctx.test() == null) {
				return new AgentTests(agentName);
			}

			if (ctx.test().isEmpty()) {
				return new AgentTests(agentName);
			}

			TestWalker programWalker = new TestWalker(goalProgram);
			List<Test> tests = new ArrayList<>(ctx.test().size());
			for (TestContext programCtx : ctx.test()) {
				Test test = programWalker.visitTest(programCtx);
				if (test == null) {
					return null;
				}
				tests.add(test);
			}

			if (tests.isEmpty()) {
				error(ctx, "Could not find a test program for " + agentName);
				return null;
			}

			List<AgentTest> agentTests = new ArrayList<>(tests.size());
			for (Test test : tests) {
				AgentTest agentTest = new AgentTest(agentName, test);
				agentTests.add(agentTest);
			}

			return new AgentTests(agentName, agentTests);
		}

		private Launch getLaunch(String agentName, AgentTestContext ctx) {
			List<MultiLaunch> launches = new LinkedList<>();
			launches.addAll(masProgram.getLaunches());
			launches.addAll(masProgram.getLaunchRules());

			// Find launch rule with our agents name in it.
			List<Launch> matchingLaunches = new LinkedList<>();
			for (MultiLaunch multiLaunch : launches) {
				for (Launch launch : multiLaunch.getLaunches()) {
					if (!launch.takesAgentNameFromEnvironment()
							&& launch.getAgentBaseName().equals(agentName)) {
						matchingLaunches.add(launch);
					}
				}
			}

			if (matchingLaunches.isEmpty()) {
				error(ctx,
						"Agent with name " + agentName
						+ " was not declared in launch rules of "
						+ masProgram.getMASFile());
				return null;
			}

			if (matchingLaunches.size() > 1) {
				File file = null;
				for (Launch launch : matchingLaunches) {
					File other = launch.getAgentFile().getAgentFile();
					if (file == null) {
						file = other;
					} else if (!file.equals(other)) {
						// TODO: We can resolve this all the way up to the point
						// where both .goal files contain the same module.
						// But do we want to?
						// FIXME: this error should be handled by the MAS
						// parser, not here?
						error(ctx,
								"Agent with name "
										+ agentName
										+ " was declared in multiple launch rules of "
										+ masProgram.getMASFile()
										+ " using different .goal files. I'm not yet smart enough to resolve this.");
						return null;
					}
				}
			}
			return matchingLaunches.get(0);
		}
	}

	private class TestWalker extends UnitTestParserBaseVisitor {
		private final GOALProgram program;
		private final WalkerHelperGOAL whg;

		public TestWalker(GOALProgram program) {
			this.program = program;
			this.whg = new WalkerHelperGOAL(unitTest2g, program.getKRLanguage());
		}

		@Override
		public Test visitTest(TestContext testCtx) {
			if (testCtx.ID() == null) {
				error(testCtx, "Missing name for test");
				return null;
			}

			String id = testCtx.ID().getText();

			List<TestSection> testSections = new ArrayList<>(testCtx
					.testSection().size());
			TestSectionWalker testSectionWalker = new TestSectionWalker();
			for (TestSectionContext testSectionContext : testCtx.testSection()) {
				TestSection section = testSectionWalker
						.visitTestSection(testSectionContext);
				if (section == null) {
					continue;
				}
				testSections.add(section);
			}

			// Pass exceptions from helper on
			for (ValidatorError e : whg.getErrors()) {
				wh.report(e);
			}
			for (ValidatorWarning e : whg.getWarnings()) {
				wh.report(e);
			}

			return new Test(id, testSections);
		}

		private class TestSectionWalker extends
		UnitTestParserBaseVisitor<TestSection> {
			@Override
			public TestSection visitTestSection(TestSectionContext ctx) {
				// TODO: There should be a better way to distinguish between or
				// branches.
				ParseTree tree = ctx.getChild(0);
				return tree.accept(this);
			}

			@Override
			public TestSection visitAssertTest(AssertTestContext ctx) {
				if (ctx.conditions() == null) {
					error(ctx, "Missing mental state test");
					return null;
				}

				MentalStateCond condition = whg.parseConditions(ctx
						.conditions().getText(), ctx.conditions());
				if (condition == null) {
					return null;
				} else if (ctx.DOUBLESTRING() != null) {
					return new AssertTest(condition, ctx.DOUBLESTRING()
							.getText());
				} else if (ctx.SINGLESTRING() != null) {
					return new AssertTest(condition, ctx.SINGLESTRING()
							.getText());
				} else {
					return new AssertTest(condition);
				}
			}

			@Override
			public TestSection visitEvaluateIn(EvaluateInContext ctx) {
				if (ctx.EVALUATE() == null) {
					error(ctx, "Expected do");
				}
				if (ctx.IN() == null) {
					error(ctx, "Expected in");
				}

				LtlQueryWalker walker = new LtlQueryWalker();
				List<TestCondition> queries = new ArrayList<>(ctx
						.testCondition().size());
				for (TestConditionContext subCtx : ctx.testCondition()) {
					TestCondition query = null;
					int count = (subCtx.ltl() == null) ? 0 : subCtx.ltl()
							.size();
					if (count >= 1) {
						query = visitLtlQuery(walker, subCtx.ltl(0));
					}
					if (count == 2) {
						query.setNestedCondition(visitLtlQuery(walker,
								subCtx.ltl(1)));
					}
					if (query == null) {
						error(subCtx, "Missing or invalid query");
					} else {
						queries.add(query);
						if (query.hasNestedCondition()) {
							queries.add(query.getNestedCondition());
						}
					}
				}

				if (ctx.doActions() == null) {
					error(ctx, "Missing action");
					return null;
				}
				DoActionSection action = visitDoActions(ctx.doActions());

				TestCondition boundary = null;
				if (ctx.testBoundary() != null) {
					if (ctx.testBoundary().ltlUntil() != null) {
						boundary = walker.visitLtlUntil(ctx.testBoundary()
								.ltlUntil());
					} else if (ctx.testBoundary().ltlWhile() != null) {
						boundary = walker.visitLtlWhile(ctx.testBoundary()
								.ltlWhile());
					}
				}

				return new EvaluateIn(queries, action, boundary, program);
			}

			private TestCondition visitLtlQuery(LtlQueryWalker walker,
					LtlContext ctx) {
				if (ctx.ltlAtStart() != null) {
					return walker.visitLtlAtStart(ctx.ltlAtStart());
				} else if (ctx.ltlAlways() != null) {
					return walker.visitLtlAlways(ctx.ltlAlways());
				} else if (ctx.ltlAtEnd() != null) {
					return walker.visitLtlAtEnd(ctx.ltlAtEnd());
				} else if (ctx.ltlEventually() != null) {
					return walker.visitLtlEventually(ctx.ltlEventually());
				} else if (ctx.ltlNever() != null) {
					return walker.visitLtlNever(ctx.ltlNever());
				} else {
					error(ctx, "Unknown query");
					return null;
				}
			}

			@Override
			public DoActionSection visitDoActions(DoActionsContext ctx) {
				if (ctx.DO() == null) {
					error(ctx, "Missing do statement");
					return null;
				}
				return visitActions(ctx.actions());
			}

			@Override
			public DoActionSection visitActions(ActionsContext ctx) {
				if (ctx == null) {
					error(ctx, "Missing action or module call");
					return null;
				}

				// HACK: Checking for main. GOAL doesn't understand this.
				ActionCombo combo;
				if (ctx.getText().equals("main")) {
					combo = insertMainModuleCall(ctx);
				} else {
					combo = whg.parseActions(ctx.getText(), ctx);
				}

				if (!validateActions(ctx, combo)) {
					return null;
				}

				return new DoActionSection(combo);
			}

			private ActionCombo insertMainModuleCall(ActionsContext ctx) {
				ActionCombo actions;
				Module main = program.getModule("main");
				UserSpecOrModuleCall dummy = new UserSpecOrModuleCall("main",
						new ArrayList<Term>(), wh.getPosition(ctx));
				actions = new ActionCombo(dummy, wh.getPosition(ctx));
				MentalFormula trueLiteral = new TrueLiteral(wh.getPosition(ctx));
				MentalStateCond mentalStateCond = new MentalStateCond(
						Arrays.asList(trueLiteral), wh.getPosition(ctx));
				IfThenRule parenntRule = new IfThenRule(mentalStateCond,
						actions, wh.getPosition(ctx));
				ModuleCallAction mainCall = new ModuleCallAction(main,
						parenntRule, new ArrayList<Term>(), wh.getPosition(ctx));
				actions.setAction(0, mainCall);
				return actions;
			}

			private boolean validateActions(ActionsContext ctx,
					ActionCombo actions) {
				if (actions == null) {
					return false;
				}

				NameSpace nameSpace = program.getNameSpace();
				if (program.hasModuleOfType(TYPE.INIT)) {
					Module init = program.getModuleOfType(TYPE.INIT);
					nameSpace.inherit(init.getNameSpace(), false);
				}

				ActionComboValidator validator = new ActionComboValidator(
						nameSpace, new HashSet<Var>());
				validator.validate(actions, false);
				if (!validator.isValid()) {
					for (ValidatorError error : validator.getErrors()) {
						error(ctx, error.toString());
						// TODO: cannot use these errors directly?!
					}
					return false;
				}

				if (!actions.isClosed()) {
					error(ctx, "Action may not contain free variables");
					return false;
				}
				return true;
			}
		}

		private class LtlQueryWalker extends
		UnitTestParserBaseVisitor<TestCondition> {
			@Override
			public TestCondition visitLtlAtStart(LtlAtStartContext ctx) {
				if (ctx.conditions() == null) {
					error(ctx, "Missing mental state test");
					return null;
				}

				MentalStateCond condition = whg.parseConditions(ctx
						.conditions().getText(), ctx.conditions());
				if (!validateMentalStateCondition(ctx, condition)) {
					return null;
				}

				Module module = null;
				if (ctx.ltlModule() != null) {
					module = visitModule(ctx.ltlModule());
					if (module == null) {
						error(ctx.ltlModule(),
								"Indicated module could not be found");
					}
				}

				return new AtStart(condition, module);
			}

			private Module visitModule(LtlModuleContext ctx) {
				if (ctx.INIT() != null) {
					return program.getModuleOfType(TYPE.INIT);
				} else if (ctx.EVENT() != null) {
					return program.getModuleOfType(TYPE.EVENT);
				} else if (ctx.MAIN() != null) {
					return program.getModuleOfType(TYPE.MAIN);
				} else {
					return program.getModule(ctx.function().getText().trim());
				}
			}

			@Override
			public TestCondition visitLtlAlways(LtlAlwaysContext ctx) {
				if (ctx.conditions() == null) {
					error(ctx, "Missing mental state test");
					return null;
				}

				MentalStateCond condition = whg.parseConditions(ctx
						.conditions().getText(), ctx.conditions());
				if (!validateMentalStateCondition(ctx, condition)) {
					return null;
				}

				return new Always(condition);
			}

			@Override
			public TestCondition visitLtlAtEnd(LtlAtEndContext ctx) {
				if (ctx.conditions() == null) {
					error(ctx, "Missing mental state test");
					return null;
				}

				MentalStateCond condition = whg.parseConditions(ctx
						.conditions().getText(), ctx.conditions());
				if (!validateMentalStateCondition(ctx, condition)) {
					return null;
				}

				Module module = null;
				if (ctx.ltlModule() != null) {
					module = visitModule(ctx.ltlModule());
				}

				return new AtEnd(condition, module);
			}

			@Override
			public TestCondition visitLtlEventually(LtlEventuallyContext ctx) {
				if (ctx.conditions() == null) {
					error(ctx, "Missing mental state test");
					return null;
				}

				MentalStateCond condition = whg.parseConditions(ctx
						.conditions().getText(), ctx.conditions());
				if (!validateMentalStateCondition(ctx, condition)) {
					return null;
				}

				return new Eventually(condition);
			}

			@Override
			public TestCondition visitLtlNever(LtlNeverContext ctx) {
				if (ctx.conditions() == null) {
					error(ctx, "Missing mental state test");
					return null;
				}

				MentalStateCond condition = whg.parseConditions(ctx
						.conditions().getText(), ctx.conditions());
				if (!validateMentalStateCondition(ctx, condition)) {
					return null;
				}

				return new Never(condition);
			}

			@Override
			public TestCondition visitLtlUntil(LtlUntilContext ctx) {
				if (ctx.conditions() == null) {
					error(ctx, "Missing mental state test");
					return null;
				}

				MentalStateCond condition = whg.parseConditions(ctx
						.conditions().getText(), ctx.conditions());
				if (!validateMentalStateCondition(ctx, condition)) {
					return null;
				}

				return new Until(condition);
			}

			@Override
			public TestCondition visitLtlWhile(LtlWhileContext ctx) {
				if (ctx.conditions() == null) {
					return null;
				}

				MentalStateCond condition = whg.parseConditions(ctx
						.conditions().getText(), ctx.conditions());
				if (!validateMentalStateCondition(ctx, condition)) {
					return null;
				}

				return new While(condition);
			}

			private boolean validateMentalStateCondition(ParserRuleContext ctx,
					MentalStateCond condition) {
				if (condition == null) {
					error(ctx, "Illegal mental state test");
					return false;
				}
				return true;
			}
		}
	}

	// TODO: this should be replaced by using a UnitTestError enum or something
	private void error(ParserRuleContext context, String message) {
		this.wh.report(new ValidatorError(GOALError.EXTERNAL_OR_UNKNOWN,
				this.wh.getPosition(context), message));
	}

	// -------------------------------------------------------------
	// WalkerInterface implementation
	// -------------------------------------------------------------

	@Override
	public List<ValidatorError> getErrors() {
		return this.wh.getErrors();
	}

	@Override
	public List<ValidatorWarning> getWarnings() {
		return this.wh.getWarnings();
	}

	@Override
	public void syntaxError(Recognizer<?, ?> recognizer,
			Object offendingSymbol, int line, int charPositionInLine,
			String msg, RecognitionException e) {
		int start = recognizer.getInputStream().index();
		int stop = start;
		if (offendingSymbol != null) {
			CommonToken token = (CommonToken) offendingSymbol;
			start = token.getStartIndex();
			stop = token.getStopIndex();
		}
		final InputStreamPosition pos = new InputStreamPosition(line,
				charPositionInLine, start, stop, this.wh.getFile());
		if (msg == null || msg.isEmpty()) {
			msg = e.getMessage();
		} // TODO: custom errors?!
		this.wh.report(new ValidatorError(GOALError.EXTERNAL_OR_UNKNOWN, pos,
				msg));
	}

	@Override
	public void reportAmbiguity(Parser recognizer, DFA dfa, int startIndex,
			int stopIndex, boolean exact, BitSet ambigAlts, ATNConfigSet configs) {
	}

	@Override
	public void reportAttemptingFullContext(Parser recognizer, DFA dfa,
			int startIndex, int stopIndex, BitSet conflictingAlts,
			ATNConfigSet configs) {
	}

	@Override
	public void reportContextSensitivity(Parser recognizer, DFA dfa,
			int startIndex, int stopIndex, int prediction, ATNConfigSet configs) {
	}

}
