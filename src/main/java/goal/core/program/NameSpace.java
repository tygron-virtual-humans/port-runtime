package goal.core.program;

import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Update;
import goal.core.program.literals.Macro;
import goal.core.program.validation.Validator;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A name space has two main purposes:
 * <ul>
 * <li>It is used as a <b>container</b> to store various program components,
 * such as knowledge, beliefs, goals, action specifications, child modules, and
 * macros. Modules are said to <i>own</i> these containers and as containers it
 * should be possible to reconstruct the code structure of the module (up to
 * semantic equivalence).</li>
 * <li>It is used to keep track of the <b>scope</b> of <i>knowledge</i>,
 * <i>action specifications</i>, <i>modules</i>, and <i>macros</i>. The idea is
 * that each program item 'lives' in a particular name space and can only refer
 * to items in that name space. For example, a rule can only use actions or call
 * modules that are part of the name space that it 'lives' in.</li>
 * <p>
 * The intended use is as follows:
 * <ul>
 * <li>First, create a scope local to a particular module by adding the
 * knowledge, action specifications, modules, and macros defined within the
 * module's context.</li>
 * <li>Second, call the method {@link #inherit(NameSpace, boolean)} to inherit
 * items from a context (e.g. the global program or the parent module context).
 * Two cases should be distinguished: (i) The global agent program scope which
 * inherits definitions from the <code>init</code> module (in this case no
 * 'overriding' takes place) and (ii) the scope of a module that inherits
 * definitions from its parent module (in this case, however, local definitions
 * 'override' more global definitions; i.e. local scope takes precedence over
 * more global scope).</li>
 * </ul>
 *
 * @author K.Hindriks
 *
 */
public class NameSpace {

	/**
	 * Knowledge available in this {@link NameSpace}.
	 * <p>
	 * Even though knowledge can be defined locally, within the scope of a
	 * module, knowledge has global scope. That is, all knowledge present in an
	 * agent program is available in every name space.
	 * </p>
	 */
	private final List<DatabaseFormula> knowledge = new LinkedList<>();

	/**
	 * Beliefs available in this {@link NameSpace}.
	 * <p>
	 * Initial beliefs can only be defined within the <code>init</code> module.
	 * </p>
	 */
	private final List<DatabaseFormula> beliefs = new LinkedList<>();

	/**
	 * Goals available in this {@link NameSpace}.
	 */
	private final List<Update> goals = new LinkedList<>();

	/**
	 * Action specifications available in this {@link NameSpace} stored in a
	 * {@link Store}.
	 * <p>
	 * May contain multiple specifications for one and the same action
	 * (identified by its name and number of parameters). Multiple
	 * specifications for the same action are allowed and deciding which one
	 * will be executed may only be possible at runtime.
	 * </p>
	 */
	private Store<ActionSpecification> actionSpecs = new Store<>();

	/**
	 * Modules available in this {@link NameSpace}.
	 * <p>
	 * May contain multiple modules with the same signature (see
	 * {@link Module#getSignature()}), even though it is not possible to execute
	 * programs that have such duplicate modules because such duplication
	 * introduces name clashes which cannot be resolved. {@link Validator}s
	 * check for name clashes and report any errors.
	 * </p>
	 */
	private Store<Module> modules = new Store<>();

	/**
	 * Macros available in this {@link NameSpace}.
	 * <p>
	 * May contain multiple macros with the same signature (see
	 * {@link Macro#getSignature()}), even though it is not possible to execute
	 * programs that have such duplicate macros because such duplication
	 * introduces name clashes which cannot be resolved. {@link Validator}s
	 * check for name clashes and report any errors.
	 * </p>
	 */
	private Store<Macro> macros = new Store<>();

	/**
	 * Creates an empty name space and sets its owner. This {@link NameSpace}
	 * defines the scope of its owner (a {@link Module}) and determines to which
	 * items its owner has access.
	 */
	public NameSpace() {

	}

	/**
	 * Returns a clone of a name space. This clone only provides copied
	 * references to the same items in the name space, i.e. the items themselves
	 * should not be modified.
	 *
	 * @return The cloned name space.
	 */
	@Override
	public NameSpace clone() {
		NameSpace clone = new NameSpace();

		clone.knowledge.addAll(this.getKnowledge());
		clone.beliefs.addAll(this.getBeliefs());
		clone.goals.addAll(this.getGoals());
		clone.actionSpecs = this.actionSpecs.clone();
		clone.modules = this.modules.clone();
		clone.macros = this.macros.clone();

		return clone;
	}

	/**
	 * Returns the knowledge, i.e. a list of {@link DatabaseFormula}s, present
	 * in this {@link NameSpace}.
	 *
	 * @return The knowledge present in this name space.
	 */
	public List<DatabaseFormula> getKnowledge() {
		return this.knowledge;
	}

	/**
	 * Adds knowledge, i.e. a list of {@link DatabaseFormula}s, to the knowledge
	 * already present in this name space.
	 *
	 * @param knowledge
	 *            The knowledge to be added.
	 */
	public void addKnowledge(List<DatabaseFormula> knowledge) {
		this.knowledge.addAll(knowledge);
	}

	/**
	 * Returns the beliefs, i.e. a list of {@link DatabaseFormula}s, present in
	 * this {@link NameSpace}.
	 *
	 * @return The beliefs present in this name space.
	 */
	public List<DatabaseFormula> getBeliefs() {
		return this.beliefs;
	}

	/**
	 * Adds beliefs, i.e. a list of {@link DatabaseFormula}s, to the beliefs
	 * already present in this name space.
	 *
	 * @param beliefs
	 *            The beliefs to be added.
	 */
	public void addBeliefs(List<DatabaseFormula> beliefs) {
		this.beliefs.addAll(beliefs);
	}

	/**
	 * Returns the goals, i.e. a list of {@link Update}s, present in this
	 * {@link NameSpace}.
	 *
	 * @return The goals present in this name space.
	 */
	public List<Update> getGoals() {
		return this.goals;
	}

	/**
	 * Adds goals, i.e. a list of {@link Update}s, to the goals already present
	 * in this name space.
	 *
	 * @param goals
	 *            The goals to be added.
	 */
	public void addGoals(List<Update> goals) {
		this.goals.addAll(goals);
	}

	/**
	 * Returns the {@link ActionSpecification}s present in this
	 * {@link NameSpace} in the form of a {@link LinkedHashMap}. Keys are
	 * strings of the form "name/nr" where "name" is the name and "nr" is the
	 * number of parameters of the action.
	 *
	 * @return The action specifications present in this name space.
	 */
	public Store<ActionSpecification> getActionSpecifications() {
		return this.actionSpecs;
	}

	/**
	 * Adds an {@link ActionSpecification} to those present in this
	 * {@link NameSpace}.
	 * <p>
	 * If the action specified is already present in the name space (identified
	 * by its name and number of parameters), the action specification is added
	 * to the list of action specifications associated with that action.
	 * </p>
	 *
	 * @param actionSpec
	 *            The action specification to be added.
	 */
	public void addActionSpecification(ActionSpecification actionSpec) {
		this.actionSpecs.add(actionSpec, actionSpec.getSignature());
	}

	/**
	 * Returns the {@link Module}s present in this {@link NameSpace} in the form
	 * of a {@link LinkedHashMap}. Keys are strings of the form "name/nr" where
	 * "name" is the name and "nr" is the number of parameters of the module.
	 *
	 * @return The modules present in this name space.
	 */
	public Store<Module> getModules() {
		return this.modules;
	}

	/**
	 * Adds a {@link Module} to those present in this {@link NameSpace}.
	 * <p>
	 * If the module is already present in the name space (identified by its
	 * name and number of parameters), the module is added to the list of
	 * modules associated with that module. {@link Validator}s check for name
	 * clashes and report any issues.
	 * </p>
	 *
	 * @param module
	 *            The module to be added.
	 */
	public void addModule(Module module) {
		this.modules.add(module, module.getSignature());
	}

	/**
	 * Returns the {@link Macro}s present in this {@link NameSpace} in the form
	 * of a {@link LinkedHashMap}. Keys are strings of the form "name/nr" where
	 * "name" is the name and "nr" is the number of parameters of the macro.
	 *
	 * @return The macros present in this name space.
	 */
	public Store<Macro> getMacros() {
		return this.macros;
	}

	/**
	 * Adds a {@link Macro} to those present in this {@link NameSpace}.
	 * <p>
	 * If the macro is already present in the name space (identified by its name
	 * and number of parameters), the macro is added to the list of macros
	 * associated with that macro. {@link Validator}s check for name clashes and
	 * report any issues.
	 * </p>
	 *
	 * @param macro
	 *            The macro to be added.
	 */
	public void addMacro(Macro macro) {
		this.macros.add(macro, macro.getSignature());
	}

	/**
	 * Inherits definitions from other {@link NameSpace}. Local definitions from
	 * this name space override definitions from given (parameter) name space if
	 * override parameter is set to true.
	 *
	 * @param nameSpace
	 *            The name space from which definitions are inherited.
	 * @param override
	 *            If set to true, definitions from this name space take
	 *            precedence and those from given (parameter) name space with
	 *            signature already present are ignored; otherwise all
	 *            definitions are combined.
	 */
	public void inherit(NameSpace nameSpace, boolean override) {
		// Ignore knowledge; knowledge is global and must be dealt with in
		// another way...
		// Ignore beliefs, which can be defined only in the <code>init</code>
		// module.
		// Ignore goals, which cannot be inherited.

		// Inherit action specifications.
		this.actionSpecs.inherit(
				nameSpace.getActionSpecifications().getItems(), override);
		// Inherit modules.
		this.modules.inherit(nameSpace.getModules().getItems(), override);
		// Inherit macros.
		this.macros.inherit(nameSpace.getMacros().getItems(), override);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append(this.knowledge.toString()).append("\n");
		builder.append(this.beliefs.toString()).append("\n");
		builder.append(this.goals.toString()).append("\n");
		builder.append(this.actionSpecs.toString()).append("\n");
		builder.append(this.modules.toString()).append("\n");
		builder.append(this.macros.toString()).append("\n");

		return builder.toString();
	}

	/**
	 * Stores items of type T in a {@link LinkedHashMap}. The intended use is to
	 * store action specifications, modules, and macros in the store using their
	 * signatures as keys.
	 *
	 * @author K.Hindriks
	 *
	 * @param <T>
	 *            The type of the items stored in the store.
	 */
	public class Store<T> {

		/**
		 * A {@link LinkedHashMap} in which all items are stored.
		 */
		private Map<String, List<T>> store = new LinkedHashMap<>();

		/**
		 * Returns a clone of this {@link Store}. Note that items in the store
		 * are not cloned!
		 *
		 * @return A clone of this store.
		 */
		@Override
		public Store<T> clone() {
			Store<T> clone = new Store<>();
			clone.store = new LinkedHashMap<>();
			clone.store.putAll(this.getItems());
			return clone;
		}

		/**
		 * Returns the map of items.
		 *
		 * @return A map of all items in the store.
		 */
		public Map<String, List<T>> getItems() {
			return store;
		}

		/**
		 * Returns an iterator over the items in this {@link Store}.
		 *
		 * @return Iterator over the items in this store.
		 */
		public List<T> getItemList() {
			List<T> itemList = new LinkedList<>();
			for (String key : store.keySet()) {
				itemList.addAll(store.get(key));
			}
			return itemList;
		}

		/**
		 * Adds an item to the store.
		 *
		 * @param item
		 *            The item to be added.
		 * @param signature
		 *            The signature of the item, i.e. string of the form {item
		 *            name}/{nr of parameters of item}.
		 */
		public void add(T item, String signature) {
			List<T> temp;

			if (this.store.containsKey(signature)) {
				// Add the item to the list associated with the key.
				temp = this.store.get(signature);
			} else {
				// Create a new item list to be added to the map.
				temp = new LinkedList<>();
			}
			// Add the item first to the list and then to the map.
			temp.add(item);
			this.store.put(signature, temp);
		}

		/**
		 * See {@link NameSpace#inherit(NameSpace, boolean)}.
		 *
		 * @param content
		 *            The content that is to be inherited by this store.
		 * @param override
		 *            Flag that indicates whether content of this store
		 *            overrides that of the given (parameter) content.
		 */
		public void inherit(Map<String, List<T>> content, boolean override) {
			for (String key : content.keySet()) {
				// Add item with signature key if the signature key does
				// not match with a signature in this store.
				if (!this.store.containsKey(key)) {
					this.store.put(key, content.get(key));
				} else if (!override) {
					// Add item with signature key if override is false.
					List<T> items = this.store.get(key);
					items.addAll(content.get(key));
					store.put(key, items);
				}
			}
		}

		/**
		 * Returns a string representation of this {@link Store}.
		 *
		 * @return A string representation of this store.
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();

			for (String key : store.keySet()) {
				builder.append(key).append(":\n");
				for (T item : store.get(key)) {
					builder.append(item.toString()).append("\n");
				}
			}

			return builder.toString();
		}

	}

}
