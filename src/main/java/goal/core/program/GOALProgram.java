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

import goal.core.kr.KRlanguage;
import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Update;
import goal.core.program.Module.TYPE;
import goal.core.program.SelectExpression.SelectorType;
import goal.core.program.literals.Macro;
import goal.core.program.literals.MentalLiteral;
import goal.core.program.rules.Rule;
import goal.core.program.rules.RuleSet;
import goal.core.program.rules.RuleSet.RuleEvaluationOrder;
import goal.core.program.validation.agentfile.GOALProgramValidator;
import goal.parser.InputStreamPosition;
import goal.parser.ParsedObject;
import goal.parser.antlr.GOALParser;
import goal.util.ImportCommand;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Container for a parsed GOAL agent program. An agent program consists of a set
 * of modules which in turn consist of a knowledge base, a belief base (only
 * allowed in the <code>init</code> module), a goal base, a program section
 * (which is a set of action rules), and an action specification section. All
 * sections except for the program section are optional; only the
 * <code>init</code> module does not need to have a program section.
 * </p>
 * <p>
 * Use the {@link GOALParser} to create an agent program from a text file. When
 * the parser has finished, call {@link GOALProgramValidator} for validating and
 * post-processing the the program.
 * </p>
 *
 * @author W.Pasman
 * @author K.Hindriks
 * @author N.Kraayenbrink
 */
public class GOALProgram extends ParsedObject {

	/** Auto-generated serial version UID */
	private static final long serialVersionUID = 840226639271804388L;

	/**
	 * A GOAL agent program essentially is a special module that only has
	 * submodules.
	 */
	private final Module module;
	/**
	 * List of imports that occur in the program.
	 *
	 * TODO: move this into module?
	 */
	private final List<ImportCommand> imports = new LinkedList<>();

	private boolean validated = false;

	/**
	 * Creates an empty program container with name and KR language set.
	 *
	 * @param name
	 *            name specified in GOAL program, generic type name; refer to
	 *            MAS file for specific agent names.
	 * @param language
	 *            The KR language used by the new program.
	 * @param source
	 *            The location of the start of the new program's definition. May
	 *            be null if not created by a parser.
	 */
	public GOALProgram(String name, KRlanguage language,
			InputStreamPosition source) {
		super(source);
		this.module = new Module(name, null, TYPE.PROGRAM, source, language);
		// add dummy empty rule set; hacky exception for program module (also
		// needed for init module)
		// because other code expects non-empty rule sets when running an agent
		// (rightly so, we should find
		// other solution for initializing and main program?)
		this.module.setRuleSet(new RuleSet(RuleEvaluationOrder.RANDOMALL,
				source));
	}

	/**
	 * @return The name of the agent program.
	 */
	public String getName() {
		return this.module.getName();
	}

	public Module getModule() {
		return this.module;
	}

	public NameSpace getNameSpace() {
		return this.module.getNameSpace();
	}

	public boolean isValidated() {
		return this.validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	/**
	 * Returns the {@link KRlanguage} used in this {@link GOALProgram}.
	 *
	 * @return The KR language used in this program.
	 */
	public KRlanguage getKRLanguage() {
		return this.module.getKRLanguage();
	}

	/**
	 * @return All knowledge specified in the entire program, including that in
	 *         all modules.
	 */
	public Set<DatabaseFormula> getAllKnowledge() {
		return getAllKnowledge(this.module);
	}

	public Set<DatabaseFormula> getAllKnowledge(Module module) {
		Set<DatabaseFormula> allFormulas = new LinkedHashSet<>();

		allFormulas.addAll(module.getKnowledge());
		// Add the knowledge from the child modules; includes duplicate modules
		// with same signature.
		for (Module child : module.getNameSpace().getModules().getItemList()) {
			allFormulas.addAll(getAllKnowledge(child));
		}

		return allFormulas;
	}

	/**
	 * @return The initial beliefs an agent that uses this program will have.
	 */
	public Set<DatabaseFormula> getBeliefs() {
		Set<DatabaseFormula> beliefs = new LinkedHashSet<>();
		if (this.module.hasModuleOfType(TYPE.INIT)) {
			beliefs.addAll(this.module.getModuleOfType(TYPE.INIT).getBeliefs());
		}
		return beliefs;
	}

	/**
	 * Returns the set of goals all agents using this {@link GOALProgram}
	 * initially (should) have. The initial goals are defined in the
	 * <code>init</code> and/or <code>main</code> module.
	 *
	 * @return The set of goals all agents using this program initially have.
	 */
	public List<Update> getInitialGoals() {
		List<Update> initialGoals = new LinkedList<>();
		if (this.module.hasModuleOfType(TYPE.INIT)) {
			initialGoals.addAll(this.module.getModuleOfType(TYPE.INIT)
					.getGoals());
		}
		if (this.module.hasModuleOfType(TYPE.MAIN)) {
			initialGoals.addAll(this.module.getModuleOfType(TYPE.MAIN)
					.getGoals());
		}
		return initialGoals;
	}

	/**
	 * @return All goals specified in the entire program, including that in all
	 *         modules.
	 */
	public List<Update> getAllGoals() {
		return getAllGoals(this.module);
	}

	public List<Update> getAllGoals(Module module) {
		List<Update> allGoals = new LinkedList<>();

		// Add all goals in the parameter module.
		allGoals.addAll(module.getGoals());
		// Add the goals from the child modules; includes duplicate modules with
		// same signature.
		for (Module child : module.getNameSpace().getModules().getItemList()) {
			allGoals.addAll(getAllGoals(child));
		}

		return allGoals;
	}

	/**
	 * Returns the {@link Macro}s present at the {@link GOALProgram} level (i.e.
	 * top level).
	 *
	 * @return The macros present in at the top program level, if any; the empty
	 *         list otherwise.
	 */
	public List<Macro> getMacros() {
		if (this.module.hasModuleOfType(TYPE.INIT)) {
			return this.module.getModuleOfType(TYPE.INIT).getMacros();
		} else {
			return new ArrayList<>(0);
		}
	}

	/**
	 * Returns the action specifications defined in the init module.
	 *
	 * @return The action specifications defined in the init module.
	 */
	public Map<String, List<ActionSpecification>> getActionSpecifications() {
		return this.module.getActionSpecificationMap();
	}

	/**
	 * @return All action specifications in the entire program, including the
	 *         ones in the modules.
	 */
	public List<ActionSpecification> getAllActionSpecs() {
		return getAllActionSpecs(this.module);
	}

	public List<ActionSpecification> getAllActionSpecs(Module module) {
		List<ActionSpecification> allSpecs = new LinkedList<>();

		// Add all specs in the parameter module.
		allSpecs.addAll(module.getActionSpecifications());
		// Add the specifications from the child modules; includes duplicate
		// modules with same signature.
		for (Module child : module.getNameSpace().getModules().getItemList()) {
			allSpecs.addAll(getAllActionSpecs(child));
		}

		return allSpecs;
	}

	/**
	 * Adds a module to the set of modules defined in this program.
	 *
	 * @param module
	 *            The {@link Module} to add to this program.
	 */
	public void addModule(Module module) {
		this.module.getNameSpace().addModule(module);
	}

	/**
	 * Returns the top-level modules.
	 *
	 * @return The top-level modules in this {@link GOALProgram}.
	 */
	public Map<String, List<Module>> getModules() {
		return this.module.getModules();
	}

	/**
	 * Returns all modules that are part of the GOAL agent program, including
	 * (recursively) all child modules.
	 *
	 * @return The collection of all modules defined throughout the entire GOAL
	 *         agent program.
	 */
	public List<Module> getAllModules() {
		return getAllModules(this.module);
	}

	public List<Module> getAllModules(Module module) {
		List<Module> allModules = new LinkedList<>();

		allModules.add(module);
		// Add the child modules; includes duplicate modules with same
		// signature.
		for (Module child : module.getNameSpace().getModules().getItemList()) {
			allModules.addAll(getAllModules(child));
		}

		return allModules;
	}

	public Module getModule(String id) {
		return this.module.getModule(id);
	}

	public boolean hasModule(String id) {
		return this.module.hasModule(id);
	}

	/**
	 * Return files that are imported by this program.
	 *
	 * @return The imported files.
	 */
	public Set<File> getImports() {
		Set<File> files = new HashSet<>(this.imports.size());
		for (ImportCommand command : this.imports) {
			files.add(command.getFile());
		}
		return files;
	}

	public void addImport(ImportCommand command) {
		this.imports.add(command);
	}

	/**
	 * Returns a string representation of this {@link GOALProgram} including
	 * imported files.
	 *
	 * @param imports
	 *            The imports to be included.
	 * @return A string representation of this program.
	 */
	public String toString(List<ImportCommand> imports) {
		return this.toString("", imports);
	}

	/**
	 * Generalization of {@link GOALProgram#toString()}, allowing the returned
	 * string to be indented at various levels.
	 *
	 * @param linePrefix
	 *            What to prefix every line with, generally some tabs.
	 * @param imports
	 *            Additional import commands FIXME: ?!
	 * @return A string-representation of this GOAL program
	 */
	public String toString(String linePrefix, List<ImportCommand> imports) {
		StringBuilder sbuild = new StringBuilder();

		List<ParsedObject> contents = new LinkedList<>();
		contents.addAll(imports);
		for (Macro m : this.getMacros()) {
			contents.add(m);
		}
		for (String key : this.module.getModules().keySet()) {
			for (Module m : this.module.getModules().get(key)) {
				contents.add(m);
			}
		}
		Collections.sort(contents);

		for (ParsedObject obj : contents) {
			// only modules need the special toString method for correct
			// indentation. Macros and imports are one-liners.
			if (obj instanceof Module) {
				sbuild.append(((Module) obj).toString(linePrefix));
			} else {
				sbuild.append(linePrefix);
				sbuild.append(obj.toString());
			}
			sbuild.append("\n");
		}

		return sbuild.toString();
	}

	/**
	 * Determines if this agent program uses mental model queries. An agent uses
	 * mental model queries if it has a mental state condition with an agent
	 * expression of a type other than self or this (so variable, quantor,
	 * constant).
	 *
	 * @return true iff this agent program does mental model queries.
	 */
	public boolean usesMentalModels() {
		for (Module m : this.getAllModules()) {
			if (m.getRuleSet() == null) {
				continue;
			}
			for (Rule rule : m.getRuleSet()) {
				for (MentalLiteral literal : rule.getCondition().getLiterals()) {
					for (SelectExpression agentExpression : literal
							.getSelector().getExpressions()) {
						if (agentExpression.getType() != SelectorType.SELF
								&& agentExpression.getType() != SelectorType.THIS) {
							return true;
						}
					}
				}
			}
		}

		return false;
	}

	public boolean hasModuleOfType(TYPE type) {
		return module.hasModuleOfType(type);
	}

	public Module getModuleOfType(TYPE type) {
		return module.getModuleOfType(type);
	}

}