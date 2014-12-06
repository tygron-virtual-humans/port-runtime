package languageTools.program.agent.actions;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import krTools.errors.exceptions.KRInitFailedException;
import krTools.language.Query;
import krTools.language.Substitution;
import krTools.language.Term;
import krTools.language.Update;
import krTools.language.Var;
import languageTools.program.agent.ActionSpecification;
import languageTools.program.agent.actions.UserSpecAction;

import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.junit.Before;
import org.junit.Test;

import swiprolog.language.PrologTerm;
import swiprolog.parser.PrologLexer;
import swiprolog.parser.PrologParser;
import eis.iilang.ParameterList;
import goalhub.krTools.KRFactory;

/**
 *
 * @author K.Hindriks
 */
public class UserSpecActionTest {

	KRlanguage language = KRFactory.get("swiprolog");
	private List<Term> parameters;
	private Query precondition;
	private Update postcondition;

	/**
	 * Get a Prolog parser that will be able to parse input.
	 *
	 * @param input
	 *            The input to be parsed.
	 * @return Prolog parser.
	 */
	public PrologParser getParser(String input) {
		CharStream cs = new ANTLRStringStream(input);
		PrologLexer lexer = new PrologLexer(cs);
		CommonTokenStream tokens = new CommonTokenStream();
		tokens.setTokenSource(lexer);
		PrologParser returned = new PrologParser(tokens);
		returned.setInput(lexer, cs);
		return returned;
	}

	/**
	 * Returns a substitution built from given variable and term.
	 *
	 * @param var
	 * @param term
	 * @return
	 */
	public Substitution getUnifier(Var var, Term term) {
		Substitution unifier = this.language.getSubstitution(null);
		unifier.addBinding(var, term);
		return unifier;
	}

	@Before
	public void setUp() {

		// Setup defaults.

		// No parameters, i.e., empty list.
		this.parameters = new ArrayList<Term>();
		// Precondition "true".
		this.precondition = getParser("true").ParseQuery();
		// Empty postcondition.
		this.postcondition = getParser("").ParseBeliefUpdateOrEmpty();
	}

	/**
	 * Test case: action without parameters.
	 *
	 * @throws KRInitFailedException
	 */
	@Test
	public void test1AddSpecification() throws KRInitFailedException {
		UserSpecAction action = new UserSpecAction("action", this.parameters,
				false, null);
		ActionSpecification actionspec = new ActionSpecification(action,
				this.precondition, this.postcondition, null);

		assertEquals(true, action.addSpecification(actionspec));
	}

	/**
	 * Test case: action with more general parameters than action specification.
	 *
	 * @throws KRInitFailedException
	 */
	@Test
	public void test2AddSpecification() throws KRInitFailedException {
		this.parameters = getParser("X").ParsePrologTerms();
		UserSpecAction action = new UserSpecAction("action", this.parameters,
				false, null);
		this.parameters = getParser("constant").ParsePrologTerms();
		UserSpecAction action4spec = new UserSpecAction("action",
				this.parameters, false, null);
		ActionSpecification actionspec = new ActionSpecification(action4spec,
				this.precondition, this.postcondition, null);

		jpl.Variable var = new jpl.Variable("X");
		jpl.Atom constant = new jpl.Atom("constant");

		assertEquals(
				getUnifier(new VariableTerm(var, null), new PrologTerm(
						constant, null)),
				action.mgu(action4spec, this.language));
		assertEquals(true, action.addSpecification(actionspec));
	}

	/**
	 * Test case: action with parameters that are instantiated more than action
	 * specification.
	 *
	 * @throws KRInitFailedException
	 */
	@Test
	public void test3AddSpecification() throws KRInitFailedException {
		this.parameters = getParser("constant").ParsePrologTerms();
		UserSpecAction action = new UserSpecAction("action", this.parameters,
				false, null);
		this.parameters = getParser("X").ParsePrologTerms();
		UserSpecAction action4spec = new UserSpecAction("action",
				this.parameters, false, null);
		ActionSpecification actionspec = new ActionSpecification(action4spec,
				this.precondition, this.postcondition, null);

		jpl.Variable var = new jpl.Variable("X");
		jpl.Atom constant = new jpl.Atom("constant");

		assertEquals(
				getUnifier(new VariableTerm(var, null), new PrologTerm(
						constant, null)),
				action.mgu(action4spec, this.language));
		assertEquals(true, action.addSpecification(actionspec));
	}

	/**
	 * Test case: action(a, X) and action4spec(Y, Y). (actual parameters are
	 * instantiated more than formal parameters)
	 *
	 * @throws KRInitFailedException
	 */
	@Test
	public void test4AddSpecification() throws KRInitFailedException {
		this.parameters = getParser("a, X").ParsePrologTerms();
		UserSpecAction action = new UserSpecAction("action", this.parameters,
				false, null);
		this.parameters = getParser("Y, Y").ParsePrologTerms();
		UserSpecAction action4spec = new UserSpecAction("action",
				this.parameters, false, null);
		ActionSpecification actionspec = new ActionSpecification(action4spec,
				this.precondition, this.postcondition, null);

		jpl.Variable varX = new jpl.Variable("X");
		jpl.Variable varY = new jpl.Variable("Y");
		jpl.Atom constant = new jpl.Atom("a");

		Substitution unifier = getUnifier(new VariableTerm(varY, null),
				new PrologTerm(constant, null));
		unifier.addBinding(new VariableTerm(varX, null), new PrologTerm(
				constant, null));
		assertEquals(unifier, action.mgu(action4spec, this.language));

		assertEquals(true, action.addSpecification(actionspec));
	}

	/**
	 * Test case: action with parameters that are instantiated more than action
	 * specification.
	 *
	 * @throws KRInitFailedException
	 */
	@Test
	public void test5AddSpecification() throws KRInitFailedException {
		this.parameters = getParser("f(a,X)").ParsePrologTerms();
		UserSpecAction action = new UserSpecAction("action", this.parameters,
				false, null);
		this.parameters = getParser("f(Y,Z)").ParsePrologTerms();
		UserSpecAction action4spec = new UserSpecAction("action",
				this.parameters, false, null);
		ActionSpecification actionspec = new ActionSpecification(action4spec,
				this.precondition, this.postcondition, null);

		jpl.Variable varX = new jpl.Variable("X");
		jpl.Variable varY = new jpl.Variable("Y");
		jpl.Variable varZ = new jpl.Variable("Z");
		jpl.Atom constant = new jpl.Atom("a");

		Substitution unifier = getUnifier(new VariableTerm(varY, null),
				new PrologTerm(constant, null));
		unifier.addBinding(new VariableTerm(varX, null), new VariableTerm(varZ,
				null));
		// action(f(a,X)) and action(f(Y,Z)) -> Y=a, X=Z
		assertEquals(unifier, action.mgu(action4spec, this.language));

		assertEquals(true, action.addSpecification(actionspec));
	}

	// @Test
	// public void testGetOptions() {
	// fail("Not yet implemented");
	// }

	@Test
	public void testConvert() {
		// TODO: how to make this language independent?
		jpl.Atom arg1 = new jpl.Atom("a");
		Term term1 = new PrologTerm(arg1, null);
		jpl.Atom arg2 = new jpl.Atom("b");
		Term term2 = new PrologTerm(arg2, null);
		List<Term> termList = new ArrayList<Term>();
		termList.add(term1);
		termList.add(term2);

		Term list = null;
		try {
			// create [a,b]
			list = SWIPrologLanguage.getInstance().makeList(termList);
		} catch (KRInitFailedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<Term> listTermlist = new ArrayList<Term>();
		listTermlist.add(list);

		UserSpecAction action = new UserSpecAction("testAction", listTermlist,
				false, null);

		eis.iilang.Action eisAction = action.convert();

		eis.iilang.Parameter par1 = new eis.iilang.Identifier("a");
		eis.iilang.Parameter par2 = new eis.iilang.Identifier("b");
		eis.iilang.ParameterList parList = new ParameterList(par1, par2);

		assertEquals(action.getName(), eisAction.getName());
		assertEquals(parList, eisAction.getParameters().get(0));
	}

}
