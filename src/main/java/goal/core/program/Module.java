/**
 * GOAL interpreter that facilitates developing and executing GOAL multi-agent
 * programs. Copyright (C) 2011 K.V. Hindriks, W. Pasman
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package goal.core.program;

import goal.core.agent.Agent;
import goal.core.kr.KRlanguage;
import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Substitution;
import goal.core.kr.language.Term;
import goal.core.kr.language.Update;
import goal.core.mentalstate.BASETYPE;
import goal.core.mentalstate.MentalModel;
import goal.core.program.SelectExpression.SelectorType;
import goal.core.program.actions.AdoptAction;
import goal.core.program.actions.ModuleCallAction;
import goal.core.program.dependencygraph.DependencyGraph;
import goal.core.program.literals.AGoalLiteral;
import goal.core.program.literals.GoalLiteral;
import goal.core.program.literals.Macro;
import goal.core.program.rules.Rule;
import goal.core.program.rules.RuleSet;
import goal.core.program.rules.RuleSet.RuleEvaluationOrder;
import goal.core.program.validation.ValidatorError;
import goal.core.program.validation.agentfile.ActionComboValidator;
import goal.core.program.validation.agentfile.GOALError;
import goal.core.program.validation.agentfile.ModuleValidator;
import goal.core.runtime.service.agent.Result;
import goal.core.runtime.service.agent.RunState;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;
import goal.tools.debugger.Channel;
import goal.tools.errorhandling.exceptions.GOALBug;
import goal.util.BracketedOptions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 *
 * DOC
 *
 * @author N.Kraayenbrink
 * @author K.Hindriks
 * @author W.Pasman made Module serializable for Q learning #2246.
 */
public class Module extends ParsedObject {
	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 6847272337331077937L;

	/**
	 * Identifier for this {@link Module}.
	 *
	 * <p>
	 * The identifier 'name' should define a unique ID in combination with the
	 * number of parameters 'nr' of this module. I.e. 'name/nr' should be
	 * unique. {@link ModuleValidator} checks whether this is actually the case.
	 * </p>
	 *
	 * <p>
	 * The following names are reserved and used for built-in module types:
	 * <ul>
	 * <li>init</li>
	 * <li>main</li>
	 * <li>event</li>
	 * </ul>
	 */
	private final String name;
	/**
	 * The parameters of this {@link Module}. The parameter list may be empty.
	 */
	private final List<Term> parameters;
	/**
	 * The {@link TYPE} of this {@link Module}.
	 */
	private TYPE type;
	/**
	 * The KR language used by this module.
	 */
	transient private KRlanguage language;
	/**
	 * Specifies the {@link FocusMethod} that should be used when entering this
	 * {@link Module}.
	 */
	transient private FocusMethod focusMethod = FocusMethod.NONE;
	/**
	 * Condition that specifies when to exit this {@link Module}.
	 */
	transient private ExitCondition exitCondition = ExitCondition.ALWAYS;
	/**
	 * Stores the knowledge, beliefs, goals, action specifications, modules, and
	 * macros defined within this module in the agent program.
	 */
	transient private NameSpace nameSpace = new NameSpace();
	/**
	 * Stores the signatures of the predicates that are queried (used) by this
	 * {@link Module}. A view definition of a module is constructed while
	 * constructing an {@link DependencyGraph} that is used to verify that
	 * predicates are both defined as well as used in the agent program. This
	 * view definition is used to construct a view on the beliefs of the agent
	 * that are used by the {@link Agent} at runtime. The current view,
	 * associated with a particular module, is constructed when a
	 * {@link ModuleCallAction} is performed and kept up to date in the agent's
	 * {@link MentalModel}.
	 */
	transient private Set<String> viewDefinition = new HashSet<>();
	/**
	 * The set of {@link Rule}s of this {@link Module}. Initializes rule
	 * evaluation order by default to linear.
	 * <p>
	 * These are obtained by parsing the program section of a module in an agent
	 * program.
	 * </p>
	 */
	private RuleSet ruleSet; // = new RuleSet(RuleEvaluationOrder.LINEAR, null);
	/**
	 * Channel to report the entry of this {@link Module} on. Should be
	 * {@code null} for anonymous modules (and no reports should be generated).
	 */
	private Channel entrychannel = null;
	/**
	 * Channel to report the exit of this {@link Module} on. Should be
	 * {@code null} for anonymous modules (and no reports should be generated).
	 */
	private Channel exitchannel = null;
	/**
	 * Mark module by default as unused. {@link ActionComboValidator} should set
	 * this flag to true if the module is used in the program (except for the
	 * built-in modules init, main, and event which are always used). Anonymous
	 * modules are also always used.
	 */
	private boolean isUsed = false;
	/**
	 * The last result of an execute-call
	 */
	private Result result;

	/**
	 * Creates a new instance of an (empty) module. Assumes that {@link RuleSet}
	 * , including options associated with the program section in this module,
	 * is added later.
	 *
	 * @param name
	 *            The name of the {@link Module}. Should be unique but this is
	 *            checked in {@link Validator}s. For an anonymous module, the
	 *            name is defined by appending <code>_subX</code> to the name of
	 *            its parent module, where <code>X</code> is the index of the
	 *            new anonymous module in the set of anonymous child modules of
	 *            the parent module.
	 * @param parameters
	 *            The parameters of the module. May be empty, which indicates
	 *            that no parameters are associated with the module. For
	 *            anonymous modules, the parameters are derived by the parser
	 *            from the variables present in the parent rule.
	 * @param type
	 *            DOC
	 * @param source
	 *            The location of the start of the definition of the new
	 *            {@link Module} in the program text. May be null if the module
	 *            is not created by a parser.
	 * @param language
	 *            the KRLanguage that will be stored in this module. We need it
	 *            to create empty substitutions when running the module.
	 */
	public Module(String name, List<Term> parameters, TYPE type,
			InputStreamPosition source, KRlanguage language) {
		// Store reference to source code location for this module.
		super(source);

		// Set name of this module.
		this.name = name;

		// Set parameters.
		if (parameters == null) { // parser may return null value for
			// parameters.
			this.parameters = new ArrayList<>(0);
		} else {
			this.parameters = parameters;
		}

		// Set type of module.
		this.type = type;
		switch (type) {
		case MAIN:
			this.exitCondition = ExitCondition.NEVER;
			this.entrychannel = Channel.MAIN_MODULE_ENTRY;
			this.exitchannel = Channel.MAIN_MODULE_EXIT;
			break;
		case EVENT:
			this.entrychannel = Channel.EVENT_MODULE_ENTRY;
			this.exitchannel = Channel.EVENT_MODULE_EXIT;
			break;
		case INIT:
			this.entrychannel = Channel.INIT_MODULE_ENTRY;
			this.exitchannel = Channel.INIT_MODULE_EXIT;
			break;
		case USERDEF:
			this.entrychannel = Channel.USER_MODULE_ENTRY;
			this.exitchannel = Channel.USER_MODULE_EXIT;
			break;
		default:
			break;
		}

		// Set KR language used by module;
		// TODO: parser may return null value
		if (language != null) {
			this.language = language;
		} else {
			throw new NullPointerException(
					"The KR language of a module can't be null.");
		}

		// Change isUsed flag to true for built-in modules.
		if (this.type != TYPE.USERDEF) {
			this.isUsed = true;
		}
	}

	/**
	 * Creates a new module. Used in {@link #applySubst}.
	 */
	private Module(String name, InputStreamPosition source) {
		super(source);
		this.name = name;
		this.parameters = new LinkedList<>();
	}

	/**
	 * Returns the name of this {@link Module}.
	 *
	 * @return The name of this module.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the parameters, i.e. a list of {@link Term}s, of this
	 * {@link Module}.
	 *
	 * @return The list of parameters of this module.
	 */
	public List<Term> getParameters() {
		return this.parameters;
	}

	/**
	 * Returns the {@link TYPE} of this {@link Module}.
	 *
	 * @return The type of this module.
	 */
	public TYPE getType() {
		return this.type;
	}

	/**
	 * Returns the {@link KRlanguage} used in this {@link Module}.
	 *
	 * @return The KR language used in this module.
	 */
	public KRlanguage getKRLanguage() {
		return language;
	}

	/**
	 * Returns the {@link FocusMethod} that is used to focus on this
	 * {@link Module}.
	 *
	 * @return The focus method that is used to focus on this module.
	 */
	public FocusMethod getFocusMethod() {
		return this.focusMethod;
	}

	/**
	 * Returns the {@link ExitCondition} for this {@link Module}.
	 *
	 * @return The exit condition of this module.
	 */
	public ExitCondition getExitCondition() {
		return this.exitCondition;
	}

	/**
	 * Returns the {@link NameSpace} associated with this {@link Module}.
	 *
	 * @return The name space associated with this module.
	 */
	public NameSpace getNameSpace() {
		return nameSpace;
	}

	/**
	 * Returns the definition of this {@link Module}'s view, i.e. a set of
	 * signatures of the form "predicate/arity".
	 *
	 * @return The signatures that define this module's view.
	 */
	public Set<String> getViewDefinition() {
		return viewDefinition;
	}

	/**
	 * Adds a signature, i.e. string of the form "predicate/arity" to this
	 * {@link Module}'s {@link #viewDefinition}.
	 *
	 * @param signature
	 *            The signature to be added to this module's view definition.
	 */
	public void addViewDefinition(String signature) {
		viewDefinition.add(signature);
	}

	/**
	 * Returns the {@link RuleSet} of this {@link Module}.
	 *
	 * @return The rules in the program section of this module.
	 */
	public RuleSet getRuleSet() {
		return this.ruleSet;
	}

	/**
	 * Sets the {@link RuleSet} of this {@link Module}.
	 *
	 * @param ruleSet
	 *            The {@link RuleSet} defining this {@link Module}'s
	 *            <code>program</code> section. Should not be null.
	 */
	public void setRuleSet(RuleSet ruleSet) {
		this.ruleSet = ruleSet;
		// Check whether rule evaluation order has been set;
		// if not, set default rule evaluation order for module.
		if (this.ruleSet.getRuleOrder() == null) {
			switch (this.getType()) {
			case EVENT:
			case INIT:
				this.ruleSet.setRuleOrder(RuleEvaluationOrder.LINEARALL);
				break;
				// default order is linear.
			case PROGRAM:
			case MAIN:
			case USERDEF:
				this.ruleSet.setRuleOrder(RuleEvaluationOrder.LINEAR);
				break;
			case ANONYMOUS: // anonymous modules inherit rule order from parent
				// module; see {@link GOALWalker#visitAnonModule}
				throw new GOALBug(
						"Rule evaluation order not set for anonymous module.");
			default:
			}
		}
	}

	/**
	 * Sets the options for this module.<br>
	 * There are two available options:
	 * <ul>
	 * <li><code>focus</code>, which can have any value from the
	 * {@link FocusMethod} enumeration.</li>
	 * <li><code>exit</code>, which can have any value from the
	 * {@link ExitCondition} enumeration.</li>
	 * </ul>
	 * A warning is generated for every unused option, but are otherwise
	 * ignored.
	 *
	 * @param options
	 *            A parsed list of options.
	 * @return A list of possible errors whilst setting the errors (empty when
	 *         nothing went wrong).
	 */
	public List<ValidatorError> setOptions(BracketedOptions options) {
		// TODO: this should probably be in the validator...
		List<ValidatorError> errors = new LinkedList<>();
		for (BracketedOptions.KEYS key : options.getKeys()) {
			String value = options.getStringValue(key);
			switch (key) {
			case FOCUS:
				if (type != Module.TYPE.USERDEF) {
					errors.add(new ValidatorError(
							GOALError.MODULE_ILLEGAL_FOCUS, value));
				} else {
					try {
						this.focusMethod = FocusMethod.valueOf(value
								.toUpperCase());
					} catch (Exception e) {
						errors.add(new ValidatorError(GOALError.OPTION_UNKNOWN,
								key + "=" + value));
					}
				}
				break;
			case EXIT:
				if (type == Module.TYPE.INIT || type == Module.TYPE.EVENT) {
					errors.add(new ValidatorError(
							GOALError.MODULE_ILLEGAL_EXIT, value));
				} else {
					try {
						this.exitCondition = ExitCondition.valueOf(value
								.toUpperCase());
					} catch (Exception e) {
						errors.add(new ValidatorError(GOALError.OPTION_UNKNOWN,
								key + "=" + value));
					}
				}
				break;
			default:
				errors.add(new ValidatorError(GOALError.OPTION_UNKNOWN, key
						.toString()));
				break;
			}
		}
		return errors;
	}

	/**
	 * @return {@code false} if there is absolutely no way the agent can focus
	 *         on this module. This may not be true the other way around, as it
	 *         is only checked by checking if there is any focus action that
	 *         targets this module, or if this is the main or event module.
	 */
	public boolean isUsed() {
		return this.isUsed;
	}

	/**
	 * Marks this module as used.
	 */
	public void markUsed() {
		this.isUsed = true;
	}

	/**
	 * Returns whether this {@link Module} is an anonymous module.
	 *
	 * @return {@code true} iff this module is anonymous. If {@code true},
	 *         {@link GOALProgram#getModule} should never return this
	 *         {@link Module}.
	 */
	public boolean isAnonymous() {
		return this.type == TYPE.ANONYMOUS;
	}

	@SuppressWarnings("unchecked")
	public Result executeFully(final RunState<?> runState,
			final Substitution substitution) {
		Callable<Callable<?>> call = execute(runState, substitution, true);
		while (call != null) {
			try {
				call = (Callable<Callable<?>>) call.call();
			} catch (Exception e) {
				break;
			}
		}
		return result;
	}

	/**
	 * Executes one step of the {@link Module}, and returns a Runnable for
	 * executing the next.
	 *
	 * @param runState
	 *            The current run state of the agent.
	 * @param substitution
	 *            The substitution that has been passed on to this module when
	 *            it was called, already with the variables renamed according to
	 *            the focus call so that the module can use it without renaming.
	 * @return {@link Runnable} for continuing to execute this module. Null when
	 *         we should stop.
	 */
	public Callable<Callable<?>> execute(final RunState<?> runState,
			final Substitution substitution) {
		return execute(runState, substitution, true);
	}

	private Callable<Callable<?>> execute(final RunState<?> runState,
			final Substitution substitution, final boolean first) {
		if (first) {
			// Push (non-anonymous) modules that were just entered onto stack
			// that keeps track of modules that have been entered but not yet
			// exited again.
			runState.enteredModule(this);

			// Add all initial beliefs defined in the beliefs section of this
			// module
			// to the agent's belief base.
			for (DatabaseFormula belief : this.getBeliefs()) {
				runState.getMentalState().insert(belief, BASETYPE.BELIEFBASE,
						runState.getDebugger(), runState.getId());
			}

			// Add all goals defined in the goals section of this module to the
			// current attention set.
			for (Update goal : this.getGoals()) {
				AdoptAction adopt = new AdoptAction(new Selector(
						SelectorType.THIS, null),
						goal.applySubst(substitution), null);
				adopt = adopt.evaluatePrecondition(runState.getMentalState(),
						runState.getDebugger(), false);
				if (adopt != null) {
					adopt.run(runState, substitution, runState.getDebugger(),
							false);
				}
			}

			// Report entry of non-anonymous module on debug channel.
			if (!isAnonymous()) {
				runState.getDebugger().breakpoint(this.entrychannel, this,
						"Entering " + getNamePhrase());
			}
		}

		// Evaluate and apply the rules of this module
		result = this.ruleSet.run(runState, substitution);

		// exit module if {@link ExitModuleAction} has been performed.
		boolean exit = result.isModuleTerminated();

		// Evaluate module's exit condition.
		switch (exitCondition) {
		case NOGOALS:
			exit |= runState.getMentalState().getAttentionSet().isEmpty();
			break;
		case NOACTION:
			exit |= !result.hasPerformedAction();
			break;
		case ALWAYS:
			exit = true;
			break;
		default:
		case NEVER:
			// exit whenever module has been terminated (see above)
			break;
		}

		// Check whether we need to start a new cycle. We do so if we do NOT
		// exit this module, NO action has been performed while evaluating the
		// module's rules (otherwise a new cycle would already have been
		// initiated), and we're currently running within the main module's
		// context (never start a new cycle when running the init/event or a
		// module called from either of these two modules).
		if (!exit && !result.hasPerformedAction()
				&& runState.isMainModuleRunning()) {
			runState.startCycle(result.hasPerformedAction());
		}
		if (exit) {
			// If module termination flag has been set, reset it except when
			// this is an anonymous module. In that case, module termination
			// needs to be propagated to enclosing module(s).
			if (!this.isAnonymous()) {
				result.setModuleTerminated(false);
			}

			// Report module entry on module's debug channel.
			if (!isAnonymous()) {
				runState.getDebugger().breakpoint(this.exitchannel, this,
						"Exiting " + getNamePhrase());
			}

			// Remove module again from stack of modules that have been entered
			// and possibly update top level context in which we run
			runState.exitModule(this);

			return null;
		} else {
			return new Callable<Callable<?>>() {
				@Override
				public Callable<?> call() throws Exception {
					return execute(runState, substitution, false);
				}
			};
		}
	}

	// NAMESPACE RELATED AUXILIARY METHODS

	/**
	 * Returns the {@link Macro}s present in this {@link Module}.
	 *
	 * @return The macros present in this module.
	 */
	public List<Macro> getMacros() {
		List<Macro> macros = new LinkedList<>();
		for (String key : this.nameSpace.getMacros().getItems().keySet()) {
			macros.addAll(this.nameSpace.getMacros().getItems().get(key));
		}
		return macros;
	}

	/**
	 * Returns the knowledge defined in this {@link Module}.
	 *
	 * @return The knowledge defined in this module.
	 */
	public List<DatabaseFormula> getKnowledge() {
		return nameSpace.getKnowledge();
	}

	/**
	 * Returns the initial beliefs defined in this {@link Module}.
	 *
	 * @return The initial beliefs defined in this module.
	 */
	public List<DatabaseFormula> getBeliefs() {
		return this.nameSpace.getBeliefs();
	}

	/**
	 * Returns the initial goals defined in this {@link Module}.
	 *
	 * @return The initial goals defined in this module.
	 */
	public List<Update> getGoals() {
		return this.nameSpace.getGoals();
	}

	/**
	 * Returns the action specification map maintained in this {@link Module}.
	 *
	 * @return All action specifications defined locally in this module in a
	 *         map.
	 */
	public Map<String, List<ActionSpecification>> getActionSpecificationMap() {
		return this.nameSpace.getActionSpecifications().getItems();
	}

	/**
	 * Returns all action specifications local to this {@link Module}.
	 *
	 * @return The action specifications locally defined within this
	 *         {@link Module}.
	 */
	public List<ActionSpecification> getActionSpecifications() {
		return this.nameSpace.getActionSpecifications().getItemList();
	}

	/**
	 * @return A map with the child modules of this {@link Module}.
	 */
	public Map<String, List<Module>> getModules() {
		return this.nameSpace.getModules().getItems();
	}

	/**
	 * Returns true if a module of (parameter) type has been defined; false
	 * otherwise.
	 *
	 * @param type
	 *            The {@link Module.TYPE} that is searched for.
	 * @return true if a module of (parameter) type has been defined; false
	 *         otherwise.
	 */
	public boolean hasModuleOfType(Module.TYPE type) {
		return getModuleOfType(type) != null;
	}

	/**
	 * Searches for and, if found, returns a module of a given (parameter) type.
	 *
	 * @param type
	 *            The type the module to be returned should have.
	 * @return A module with the paramaeter type, if any; null otherwise.
	 */
	public Module getModuleOfType(Module.TYPE type) {
		for (Module module : this.getNameSpace().getModules().getItemList()) {
			if (module.getType().equals(type)) {
				return module;
			}
		}
		return null;
	}

	/**
	 * Returns true if a module of (parameter) type has been defined; false
	 * otherwise.
	 *
	 * @param id
	 *            the name of the module.
	 * @return true if a module has been defined; false otherwise.
	 */
	public boolean hasModule(String id) {
		return getModule(id) != null;
	}

	/**
	 * Searches for and, if found, returns a module of a given id type.
	 *
	 * @param id
	 *            of module to be returned
	 * @return A module with the id, if any; null otherwise.
	 */
	public Module getModule(String id) {
		for (Module module : this.getNameSpace().getModules().getItemList()) {
			if (module.getName().equals(id)) {
				return module;
			}
		}
		return null;
	}

	// Equals and toString methods

	/**
	 * CHECK Two modules are considered equal if the names are the same.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Module)) {
			return false;
		}
		return this.getName().equals(((Module) o).getName());
	}

	@Override
	public int hashCode() {
		return this.getName().hashCode();
	}

	/**
	 * @return A short text representation of this module.
	 */
	public String toShortString() {
		String parameterList = "";

		if (!this.parameters.isEmpty()) {
			for (Term term : this.parameters) {
				if (parameterList.length() == 0) {
					parameterList += term.toString();
				} else {
					parameterList += "," + term.toString();
				}
			}
			parameterList = "(" + parameterList + ")";
		}
		return this.getName() + parameterList;
	}

	/**
	 * Pre- or post-fixes 'module' to name and parameters.
	 *
	 * @return "module <name(parlist)>" or "<name(parlist)> module".
	 */
	public String getNamePhrase() {
		switch (type) {
		case EVENT:
		case INIT:
		case MAIN:
			return name + " module";
		case PROGRAM:
			return name + " program";
		case USERDEF:
			return "module " + name;
		default:
			return "";
		}
	}

	@Override
	public String toString() {
		return this.toString("");
	}

	/**
	 * Generalization of {@link Module#toString()}, allowing the returned string
	 * to be indented at various levels.
	 *
	 * @param linePrefix
	 *            What to prefix every line with. generally some tabs.
	 * @return A string-representation of this module.g
	 */
	public String toString(String linePrefix) {
		StringBuilder sbuild = new StringBuilder();
		sbuild.append(linePrefix + "module " + this.getName()
				+ this.getParameters() + " {\n");

		// Add knowledge, if any.
		if (!this.getKnowledge().isEmpty()) {
			sbuild.append(linePrefix + "\tknowledge {\n");
			for (DatabaseFormula fact : this.getKnowledge()) {
				sbuild.append(linePrefix + "\t\t" + fact.toString() + ".\n");
			}
			sbuild.append(linePrefix + "\t}\n");
		}

		// Add beliefs, if any.
		if (!this.getBeliefs().isEmpty()) {
			sbuild.append(linePrefix + "\tbeliefs {\n");
			for (DatabaseFormula belief : this.getBeliefs()) {
				sbuild.append(linePrefix + "\t\t" + belief.toString() + ".\n");
			}
			sbuild.append(linePrefix + "\t}\n");
		}

		// Add goals, if any.
		if (!this.getGoals().isEmpty()) {
			sbuild.append(linePrefix + "\tgoals {\n");
			for (Update g : this.getGoals()) {
				sbuild.append(linePrefix + "\t\t" + g.toString() + ".\n");
			}
			sbuild.append(linePrefix + "\t}\n");
		}

		// Add macros, if any.
		if (!this.getMacros().isEmpty()) {
			for (Macro macro : this.getMacros()) {
				sbuild.append(macro.toString()).append("\n");
			}
		}

		// Add program section, if non-empty.
		if (!this.ruleSet.isEmpty()) {
			sbuild.append(this.ruleSet.toString(linePrefix + "\t"));
		}

		// Add modules, if any.
		for (Module module : this.getNameSpace().getModules().getItemList()) {
			sbuild.append(module.toString(linePrefix));
			sbuild.append("\n");
		}

		// Add action specifications, if any.
		if (!this.getActionSpecifications().isEmpty()) {
			sbuild.append(linePrefix + "\tactionspec {\n");
			for (ActionSpecification as : this.getActionSpecifications()) {
				sbuild.append(as.toString(linePrefix + "\t\t"));
			}
			sbuild.append(linePrefix + "\t}\n");
		}

		sbuild.append(linePrefix + "}\n");
		return sbuild.toString();
	}

	/**
	 * Returns a string representing the signature of this {@link Module}.
	 *
	 * @return A string of the format {action name}/{number of parameters}.
	 */
	public String getSignature() {
		return this.getName().concat("/")
				.concat(String.valueOf(this.getParameters().size()));
	}

	// TYPE, FocusMethod, and ExitCondition enum classes.

	/**
	 * Types for distinguishing built-in modules from user-defined modules.
	 * <p>
	 * The options are:
	 * <ul>
	 * <li>{@link #PROGRAM}: The main agent program.
	 * <li>{@link #INIT}: The <code>init</code> module.</li>
	 * <li>{@link #MAIN}: The <code>main</code> module.</li>
	 * <li>{@link #EVENT}: The <code>event</code> module.</li>
	 * <li>{@link #ANONYMOUS}: An anonymous module, i.e. <code>{ ... }</code>.</li>
	 * <li>{@link #USERDEF}: A <code>user-defined</code> module.</li>
	 * </ul>
	 * </p>
	 */
	public enum TYPE {
		PROGRAM("program"),
		INIT("init"),
		MAIN("main"),
		EVENT("event"),
		ANONYMOUS("anonymous"),
		USERDEF("user-defined");

		private String displayName;

		private TYPE(String displayName) {
			this.displayName = displayName;
		}

		public String getDisplayName() {
			return this.displayName;
		}

		@Override
		public String toString() {
			return this.displayName;
		}
	}

	/**
	 * Different methods for creating an attention set associated with a module
	 * that is called at runtime.
	 * <p>
	 * The options are:
	 * <ul>
	 * <li>{@link #FILTER}: goal from rule condition that acts like filter is
	 * inserted into the module's attention set.</li>
	 * <li>{@link #SELECT}: one of the agent's current goals that satisfies the
	 * rule condition is inserted into the module's attention set.</li>
	 * <li>{@link #NONE}: no new attention set associated with the module is
	 * created.</li>
	 * <li>{@link #NEW}: creates a new and empty attention set.</li>
	 * </ul>
	 * </p>
	 */
	public enum FocusMethod {
		/**
		 * After focusing, the agents gets a single goal for each of the
		 * positive {@link GoalLiteral} and {@link AGoalLiteral}s in the
		 * instantiated precondition of the rule that focuses on the module.
		 */
		FILTER,
		/**
		 * After focusing, the agent gets a single goal from the current
		 * attention set, which validates the 'context' of the module.
		 */
		SELECT,
		/**
		 * After focusing, the agent will have the same attention set as before.
		 * The same goal base is re-used. Goals in the <code>goals { }</code>
		 * section are simply added to that attention set. This is the default
		 * value.
		 */
		NONE,
		/**
		 * After focusing, the agent will have no goals in its attention set,
		 * aside from those defined in the Module's <code>goals { }</code>
		 * -section.
		 */
		NEW
	}

	/**
	 * The various exit conditions of a Module: NOGOALS, NOACTION, ALWAYS, and
	 * NEVER. These conditions are checked each time <i>after</i> the rules of
	 * the module have been evaluated. Note that modules can also be exited
	 * using the <code>exit-module</code> action.
	 */
	public static enum ExitCondition {
		/**
		 * The module should be exited once there are no goals left in the
		 * current attention set at the end of an evaluation step.
		 */
		NOGOALS,
		/**
		 * The module should be exited once an evaluation step produced no
		 * executed actions.
		 */
		NOACTION,
		/**
		 * The module should always be exited after an evaluation step.<br>
		 * This is the default value.
		 */
		ALWAYS,

		/**
		 * The module never exits. This is the default for the main program. If
		 * the main program exits, the agent dies.
		 */
		NEVER;
	}

}
