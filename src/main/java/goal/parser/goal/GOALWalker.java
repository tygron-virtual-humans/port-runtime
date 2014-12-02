package goal.parser.goal;

import goal.core.kr.KRlanguage;
import goal.core.kr.language.DatabaseFormula;
import goal.core.kr.language.Query;
import goal.core.kr.language.Term;
import goal.core.kr.language.Update;
import goal.core.kr.language.Var;
import goal.core.program.ActionSpecification;
import goal.core.program.GOALProgram;
import goal.core.program.Module;
import goal.core.program.Module.ExitCondition;
import goal.core.program.Module.FocusMethod;
import goal.core.program.Module.TYPE;
import goal.core.program.NameSpace;
import goal.core.program.SelectExpression;
import goal.core.program.SelectExpression.SelectorType;
import goal.core.program.Selector;
import goal.core.program.SentenceMood;
import goal.core.program.actions.Action;
import goal.core.program.actions.ActionCombo;
import goal.core.program.actions.AdoptAction;
import goal.core.program.actions.DeleteAction;
import goal.core.program.actions.DropAction;
import goal.core.program.actions.ExitModuleAction;
import goal.core.program.actions.InsertAction;
import goal.core.program.actions.LogAction;
import goal.core.program.actions.ModuleCallAction;
import goal.core.program.actions.PrintAction;
import goal.core.program.actions.SendAction;
import goal.core.program.actions.SendOnceAction;
import goal.core.program.actions.UserSpecAction;
import goal.core.program.actions.UserSpecOrModuleCall;
import goal.core.program.literals.AGoalLiteral;
import goal.core.program.literals.BelLiteral;
import goal.core.program.literals.GoalALiteral;
import goal.core.program.literals.GoalLiteral;
import goal.core.program.literals.Macro;
import goal.core.program.literals.MentalFormula;
import goal.core.program.literals.MentalLiteral;
import goal.core.program.literals.MentalStateCond;
import goal.core.program.literals.TrueLiteral;
import goal.core.program.rules.ForallDoRule;
import goal.core.program.rules.IfThenRule;
import goal.core.program.rules.ListallDoRule;
import goal.core.program.rules.Rule;
import goal.core.program.rules.RuleSet;
import goal.core.program.rules.RuleSet.RuleEvaluationOrder;
import goal.core.program.validation.ValidatorError;
import goal.core.program.validation.ValidatorWarning;
import goal.core.program.validation.agentfile.GOALError;
import goal.parser.InputStreamPosition;
import goal.parser.WalkerInterface;
import goal.parser.antlr.GOALLexer;
import goal.parser.antlr.GOALParser;
import goal.parser.antlr.GOALParserBaseVisitor;
import goal.util.BracketedOptions;
import goal.util.Extension;
import goal.util.ImportCommand;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.TerminalNode;

@SuppressWarnings("rawtypes")
public class GOALWalker extends GOALParserBaseVisitor implements
		WalkerInterface {
	protected GOALParser parser;
	protected WalkerHelperKR wh;
	protected GOALProgram program;
	protected final Map<Module, Integer> anonymous;

	public GOALWalker(File goal, GOALParser parser, GOALLexer lexer,
			KRlanguage kr) {
		this.wh = new WalkerHelperKR(goal, kr);
		this.anonymous = new LinkedHashMap<>();
		parser.removeErrorListeners();
		parser.addErrorListener(this);
		this.parser = parser;
		lexer.removeErrorListeners();
		lexer.addErrorListener(this);
		this.program = null;
	}

	public GOALProgram getProgram() {
		return visitModules(this.parser.modules());
	}

	public void setProgram(GOALProgram program) {
		this.program = program;
	}

	public WalkerHelperKR getWalkerHelper() {
		return this.wh;
	}

	public void setWalkerHelper(WalkerHelperKR helper) {
		this.wh = helper;
	}

	public GOALParser getParser() {
		return this.parser;
	}

	public void setParser(GOALParser parser) {
		this.parser = parser;
	}

	@Override
	public GOALProgram visitModules(GOALParser.ModulesContext ctx) {
		if (this.program == null) {
			try {
				this.program = new GOALProgram(this.wh.getFile().getName(),
						this.wh.getKR(), this.wh.getPosition(ctx));
			} catch (Exception any) {
				this.wh.report(new ValidatorError(
						GOALError.EXTERNAL_OR_UNKNOWN,
						this.wh.getPosition(ctx), any.getMessage()));
				return null;
			}
		}
		for (GOALParser.ModuleImportContext moduleImport : ctx.moduleImport()) {
			try {
				ImportCommand importer = visitModuleImport(moduleImport);
				if (importer != null) {
					this.program.addImport(importer);
				}
			} catch (Exception any) {
				this.wh.report(new ValidatorError(
						GOALError.EXTERNAL_OR_UNKNOWN,
						this.wh.getPosition(ctx), any.getMessage()));
			}
		}
		for (GOALParser.ModuleContext module : ctx.module()) {
			try {
				Module mod = visitModule(module);
				if (mod != null) {
					this.program.addModule(mod);
				}
			} catch (Exception any) {
				this.wh.report(new ValidatorError(
						GOALError.EXTERNAL_OR_UNKNOWN,
						this.wh.getPosition(ctx), any.getMessage()));
			}
		}
		return this.program;
	}

	@Override
	public ImportCommand visitModuleImport(GOALParser.ModuleImportContext ctx) {
		ImportCommand importer = null;
		if (ctx.DOUBLESTRING() != null) {
			String path = ctx.DOUBLESTRING().getText().trim().replace("\"", "");
			importer = new ImportCommand(this.wh.getFile(), path,
					Extension.MODULES, this.wh.getPosition(ctx));
			parseSubFile(importer.getFile(), this.wh.getPosition(ctx));
		} else {
			this.wh.report(new ValidatorError(GOALError.IMPORT_MISSING_FILE,
					this.wh.getPosition(ctx), ctx.getText()));
		}
		return importer;
	}

	@Override
	public Module visitModule(GOALParser.ModuleContext ctx) {
		Module module = null;
		if (ctx.moduleDef() != null) {
			module = visitModuleDef(ctx.moduleDef());
		} else {
			this.wh.report(new ValidatorError(GOALError.MODULE_MISSING_NAME,
					this.wh.getPosition(ctx)));
		}
		if (module == null) {
			return null;
		}
		this.anonymous.put(module, 0); // push on the stack
		if (ctx.moduleOptions() != null) {
			BracketedOptions options = visitModuleOptions(ctx.moduleOptions());
			if (options != null) {
				for (ValidatorError error : module.setOptions(options)) {
					this.wh.report(error);
				}
			}
		}
		NameSpace namespace = module.getNameSpace();
		if (ctx.knowledge() != null) {
			Map.Entry<List<DatabaseFormula>, List<ImportCommand>> base = visitKnowledge(ctx
					.knowledge());
			if (base != null) {
				List<DatabaseFormula> knowledge = base.getKey();
				if (knowledge != null) {
					namespace.addKnowledge(knowledge);
				}
				List<ImportCommand> imports = base.getValue();
				if (imports != null) {
					for (final ImportCommand importer : imports) {
						this.program.addImport(importer);
					}
				}
			}
		}
		if (ctx.beliefs() != null) {
			Map.Entry<List<DatabaseFormula>, List<ImportCommand>> base = visitBeliefs(ctx
					.beliefs());
			if (base != null) {
				List<DatabaseFormula> beliefs = base.getKey();
				if (beliefs != null) {
					namespace.addBeliefs(base.getKey());
				}
				List<ImportCommand> imports = base.getValue();
				if (imports != null) {
					for (final ImportCommand importer : imports) {
						this.program.addImport(importer);
					}
				}
			}
		}
		if (ctx.goals() != null) {
			List<Update> goals = visitGoals(ctx.goals());
			if (goals != null) {
				namespace.addGoals(goals);
			}
		}
		if (ctx.program() != null) {
			Map.Entry<RuleSet, List<Macro>> rules = visitProgram(ctx.program());
			if (rules != null) {
				module.setRuleSet(rules.getKey());
				for (Macro macro : rules.getValue()) {
					namespace.addMacro(macro);
				}
			}
		}
		if (ctx.actionSpecs() != null) {
			List<ActionSpecification> specs = visitActionSpecs(ctx
					.actionSpecs());
			for (ActionSpecification spec : specs) {
				namespace.addActionSpecification(spec);
			}
		}
		this.anonymous.remove(module); // pop off the stack

		return module;
	}

	@Override
	public BracketedOptions visitModuleOptions(
			GOALParser.ModuleOptionsContext ctx) {
		BracketedOptions options = new BracketedOptions();
		for (GOALParser.ModuleOptionContext moduleOption : ctx.moduleOption()) {
			Map.Entry<BracketedOptions.KEYS, String> option = visitModuleOption(moduleOption);
			if (option != null) {
				String key = option.getKey().name();
				String value = option.getValue();
				ValidatorError error = options.addOption(key, value,
						this.wh.getPosition(moduleOption));
				if (error != null) {
					this.wh.report(error);
				}
			}
		}
		return options;
	}

	@Override
	public Map.Entry<BracketedOptions.KEYS, String> visitModuleOption(
			GOALParser.ModuleOptionContext ctx) {
		if (ctx.focusOption() != null) {
			FocusMethod method = visitFocusOption(ctx.focusOption());
			if (method != null) {
				return new AbstractMap.SimpleEntry<>(
						BracketedOptions.KEYS.FOCUS, method.name());
			} else {
				return null; // error thrown in visitFocusOption
			}
		} else if (ctx.exitOption() != null) {
			ExitCondition condition = visitExitOption(ctx.exitOption());
			if (condition != null) {
				return new AbstractMap.SimpleEntry<>(
						BracketedOptions.KEYS.EXIT, condition.name());
			} else {
				return null; // error thrown in visitExitOption
			}
		} else {
			this.wh.report(new ValidatorError(GOALError.OPTION_UNKNOWN, this.wh
					.getPosition(ctx), ctx.getText()));
			return null;
		}
	}

	@Override
	public FocusMethod visitFocusOption(GOALParser.FocusOptionContext ctx) {
		if (ctx.NONE() != null) {
			return FocusMethod.NONE;
		} else if (ctx.NEW() != null) {
			return FocusMethod.NEW;
		} else if (ctx.SELECT() != null) {
			return FocusMethod.SELECT;
		} else if (ctx.FILTER() != null) {
			return FocusMethod.FILTER;
		} else {
			this.wh.report(new ValidatorError(GOALError.OPTION_FOCUS_INVALID,
					this.wh.getPosition(ctx)));
			return null;
		}
	}

	@Override
	public ExitCondition visitExitOption(GOALParser.ExitOptionContext ctx) {
		if (ctx.ALWAYS() != null) {
			return ExitCondition.ALWAYS;
		} else if (ctx.NEVER() != null) {
			return ExitCondition.NEVER;
		} else if (ctx.NOGOALS() != null) {
			return ExitCondition.NOGOALS;
		} else if (ctx.NOACTION() != null) {
			return ExitCondition.NOACTION;
		} else {
			this.wh.report(new ValidatorError(GOALError.OPTION_EXIT_INVALID,
					this.wh.getPosition(ctx)));
			return null;
		}
	}

	@Override
	public Module visitModuleDef(GOALParser.ModuleDefContext ctx) {
		if (ctx.function() != null) {
			Map.Entry<String, List<Term>> function = visitFunction(ctx
					.function());
			if (function != null) {
				String id = function.getKey();
				List<Term> parameters = function.getValue();
				return new Module(id, parameters, Module.TYPE.USERDEF,
						this.wh.getPosition(ctx), this.wh.getKR());
			} else {
				return null; // error thrown in visitFunction
			}
		} else if (ctx.INIT() != null) {
			Module.TYPE type = Module.TYPE.INIT;
			Module returned = new Module(type.getDisplayName(), null, type,
					this.wh.getPosition(ctx), this.wh.getKR());
			// add dummy empty rule set; hacky exception for program module
			// (also needed for init module)
			// because other code expects non-empty rule sets when running an
			// agent (rightly so, we should find other solution for initializing
			// and main program?)
			returned.setRuleSet(new RuleSet(RuleEvaluationOrder.RANDOMALL,
					this.wh.getPosition(ctx)));
			return returned;
		} else if (ctx.MAIN() != null) {
			Module.TYPE type = Module.TYPE.MAIN;
			return new Module(type.getDisplayName(), null, type,
					this.wh.getPosition(ctx), this.wh.getKR());
		} else if (ctx.EVENT() != null) {
			Module.TYPE type = Module.TYPE.EVENT;
			return new Module(type.getDisplayName(), null, type,
					this.wh.getPosition(ctx), this.wh.getKR());
		} else {
			this.wh.report(new ValidatorError(GOALError.MODULE_MISSING_NAME,
					this.wh.getPosition(ctx)));
			return null;
		}
	}

	// -------------------------------------------------------------
	// KNOWLEDGE/BELIEFS/GOALS
	// -------------------------------------------------------------
	@Override
	public Map.Entry<List<DatabaseFormula>, List<ImportCommand>> visitKnowledge(
			GOALParser.KnowledgeContext ctx) {
		if (ctx.KR_BLOCK() != null && !ctx.KR_BLOCK().isEmpty()) {
			return this.wh.parseProgram(ctx.KR_BLOCK());
		} else {
			return null;
		}
	}

	@Override
	public Map.Entry<List<DatabaseFormula>, List<ImportCommand>> visitBeliefs(
			GOALParser.BeliefsContext ctx) {
		if (ctx.KR_BLOCK() != null && !ctx.KR_BLOCK().isEmpty()) {
			return this.wh.parseProgram(ctx.KR_BLOCK());
		} else {
			return null;
		}
	}

	@Override
	public List<Update> visitGoals(GOALParser.GoalsContext ctx) {
		if (ctx.KR_BLOCK() != null && !ctx.KR_BLOCK().isEmpty()) {
			return this.wh.parseGoalSection(ctx.KR_BLOCK());
		} else {
			return null;
		}
	}

	// -------------------------------------------------------------
	// PROGRAM section
	// -------------------------------------------------------------
	@Override
	public Map.Entry<RuleSet, List<Macro>> visitProgram(
			GOALParser.ProgramContext ctx) {
		List<Macro> macros = new ArrayList<>(ctx.macro().size());
		for (GOALParser.MacroContext macroRule : ctx.macro()) {
			Macro macro = visitMacro(macroRule);
			if (macro != null) { // error thrown in visitMacro
				macros.add(macro);
			}
		}

		Module parent = null;
		for (Module module : this.anonymous.keySet()) {
			parent = module; // get the last one (which is the current one)
		}
		if (parent == null) {
			this.wh.report(new ValidatorError(GOALError.PROGRAM_NOT_IN_MODULE,
					this.wh.getPosition(ctx)));
			return null;
		}
		RuleEvaluationOrder order = null;
		if (ctx.orderOption() != null) { // error thrown in visitOrderOption
			order = visitOrderOption(ctx.orderOption());
		}
		RuleSet rules = new RuleSet(order, this.wh.getPosition(ctx));
		parent.setRuleSet(rules);
		for (GOALParser.ProgramRuleContext programRule : ctx.programRule()) {
			Rule rule = visitProgramRule(programRule);
			if (rule != null) { // error thrown by visitProgramRule
				rules.addRule(rule);
			}
		}

		return new AbstractMap.SimpleEntry<>(rules, macros);
	}

	@Override
	public Macro visitMacro(GOALParser.MacroContext ctx) {
		if (ctx.function() != null) {
			Map.Entry<String, List<Term>> function = visitFunction(ctx
					.function());
			if (function != null) {
				String id = function.getKey();
				List<Term> parameters = function.getValue();
				if (ctx.conditions() != null) {
					MentalStateCond definition = visitConditions(ctx
							.conditions());
					return new Macro(id, parameters, definition,
							this.wh.getPosition(ctx));
				} else {
					this.wh.report(new ValidatorError(GOALError.MACRO_INVALID,
							this.wh.getPosition(ctx)));
					return null;
				}
			} else { // error thrown in visitFunction
				return null;
			}
		} else {
			this.wh.report(new ValidatorError(GOALError.MACRO_MISSING_NAME,
					this.wh.getPosition(ctx)));
			return null;
		}
	}

	@Override
	public RuleEvaluationOrder visitOrderOption(
			GOALParser.OrderOptionContext ctx) {
		if (ctx.LINEAR() != null) {
			return RuleEvaluationOrder.LINEAR;
		} else if (ctx.LINEARALL() != null) {
			return RuleEvaluationOrder.LINEARALL;
		} else if (ctx.RANDOM() != null) {
			return RuleEvaluationOrder.RANDOM;
		} else if (ctx.RANDOMALL() != null) {
			return RuleEvaluationOrder.RANDOMALL;
		} else if (ctx.ADAPTIVE() != null) {
			return RuleEvaluationOrder.ADAPTIVE;
		} else {
			this.wh.report(new ValidatorError(GOALError.OPTION_ORDER_INVALID,
					this.wh.getPosition(ctx)));
			return null;
		}
	}

	@Override
	public Rule visitProgramRule(GOALParser.ProgramRuleContext ctx) {
		Rule returned = null;
		if (ctx.ifRule() != null) {
			returned = visitIfRule(ctx.ifRule());
		} else if (ctx.forallRule() != null) {
			returned = visitForallRule(ctx.forallRule());
		} else if (ctx.listallRule() != null) {
			returned = visitListallRule(ctx.listallRule());
		} else {
			this.wh.report(new ValidatorError(GOALError.RULE_INVALID, this.wh
					.getPosition(ctx)));
		}

		// #2882 this is hacky but apparently required?
		if (returned != null && returned.getAction() != null) {
			for (final Action action : returned.getAction().getActions()) {
				if (action instanceof UserSpecOrModuleCall) {
					((UserSpecOrModuleCall) action).setRule(returned);
				}
			}
		}
		return returned;
	}

	@Override
	public IfThenRule visitIfRule(GOALParser.IfRuleContext ctx) {
		MentalStateCond condition = null;
		if (ctx.conditions() != null) {
			condition = visitConditions(ctx.conditions()); // never null
		} else {
			this.wh.report(new ValidatorError(GOALError.RULE_MISSING_CONDITION,
					this.wh.getPosition(ctx)));
			return null;
		}
		if (ctx.actions() != null) {
			ActionCombo action = visitActions(ctx.actions());
			if (action != null) {
				return new IfThenRule(condition, action,
						this.wh.getPosition(ctx));
			} else { // error thrown by visitActions
				return null;
			}
		} else if (ctx.anonModule() != null) {
			Module module = visitAnonModule(ctx.anonModule());
			if (module != null) {
				IfThenRule returned = new IfThenRule(condition, null,
						this.wh.getPosition(ctx));
				ModuleCallAction act = new ModuleCallAction(module, returned,
						null, this.wh.getPosition(ctx.anonModule()));
				ActionCombo combo = new ActionCombo(act,
						this.wh.getPosition(ctx.anonModule()));
				returned.setAction(combo);
				return returned;
			} else { // error thrown by visitAnonModule
				return null;
			}
		} else {
			this.wh.report(new ValidatorError(GOALError.RULE_MISSING_BODY,
					this.wh.getPosition(ctx)));
			return null;
		}
	}

	@Override
	public ForallDoRule visitForallRule(GOALParser.ForallRuleContext ctx) {
		MentalStateCond condition = null;
		if (ctx.conditions() != null) {
			condition = visitConditions(ctx.conditions()); // never null
		} else {
			this.wh.report(new ValidatorError(GOALError.RULE_MISSING_CONDITION,
					this.wh.getPosition(ctx)));
			return null;
		}
		if (ctx.actions() != null) {
			ActionCombo action = visitActions(ctx.actions());
			if (action != null) {
				return new ForallDoRule(condition, action,
						this.wh.getPosition(ctx));
			} else { // error thrown in visitActions
				return null;
			}
		} else if (ctx.anonModule() != null) {
			Module module = visitAnonModule(ctx.anonModule());
			if (module != null) {
				ForallDoRule returned = new ForallDoRule(condition, null,
						this.wh.getPosition(ctx));
				ModuleCallAction act = new ModuleCallAction(module, returned,
						null, this.wh.getPosition(ctx.anonModule()));
				ActionCombo combo = new ActionCombo(act,
						this.wh.getPosition(ctx.anonModule()));
				returned.setAction(combo);
				return returned;
			} else { // error thrown in visitAnonModule
				return null;
			}
		} else {
			this.wh.report(new ValidatorError(GOALError.RULE_MISSING_BODY,
					this.wh.getPosition(ctx)));
			return null;
		}
	}

	@Override
	public ListallDoRule visitListallRule(GOALParser.ListallRuleContext ctx) {
		boolean ltr = true;
		if (ctx.RTLARROW() != null) {
			ltr = false;
		}
		MentalStateCond condition = null;
		if (ctx.conditions() != null) {
			condition = visitConditions(ctx.conditions()); // never null
		} else {
			this.wh.report(new ValidatorError(GOALError.RULE_MISSING_CONDITION,
					this.wh.getPosition(ctx)));
			return null;
		}
		Term var = null;
		if (ctx.ID() != null) {
			List<TerminalNode> singleList = new ArrayList<>(1);
			singleList.add(ctx.ID());
			var = this.wh.parseTerm(singleList);
		}
		if (var != null && var instanceof Var) {
			Var variable = (Var) var;
			if (ctx.actions() != null) {
				ActionCombo action = visitActions(ctx.actions());
				if (action != null) {
					return new ListallDoRule(condition, variable, action,
							this.wh.getPosition(ctx), ltr);
				} else { // error thrown in visitActions
					return null;
				}
			} else if (ctx.anonModule() != null) {
				Module module = visitAnonModule(ctx.anonModule());
				if (module != null) {
					ListallDoRule returned = new ListallDoRule(condition,
							variable, null, this.wh.getPosition(ctx), ltr);
					ModuleCallAction act = new ModuleCallAction(module,
							returned, null, this.wh.getPosition(ctx
									.anonModule()));
					ActionCombo combo = new ActionCombo(act,
							this.wh.getPosition(ctx.anonModule()));
					returned.setAction(combo);
					return returned;
				} else { // error thrown in visitAnonModule
					return null;
				}
			} else {
				this.wh.report(new ValidatorError(GOALError.RULE_MISSING_BODY,
						this.wh.getPosition(ctx)));
				return null;
			}
		} else {
			this.wh.report(new ValidatorError(GOALError.LISTALL_MISSING_VAR,
					this.wh.getPosition(ctx)));
			return null;
		}
	}

	@Override
	public MentalStateCond visitConditions(GOALParser.ConditionsContext ctx) {
		List<MentalFormula> formulas = new ArrayList<>(ctx.condition().size());
		for (GOALParser.ConditionContext condition : ctx.condition()) {
			MentalFormula formula = visitCondition(condition);
			if (formula != null) { // error thrown in visitCondition
				formulas.add(formula);
			}
		}
		MentalStateCond mentalstate = new MentalStateCond(formulas,
				this.wh.getPosition(ctx));
		return mentalstate;
	}

	@Override
	public MentalFormula visitCondition(GOALParser.ConditionContext ctx) {
		if (ctx.mentalRule() != null) {
			MentalFormula returned = visitMentalRule(ctx.mentalRule());
			if (ctx.NOT() != null) {
				if (returned instanceof MentalLiteral) {
					MentalLiteral literal = (MentalLiteral) returned;
					literal.setPolarity(false);
					return literal;
				} else {
					this.wh.report(new ValidatorError(
							GOALError.CONDITION_INVALID_NOT, this.wh
									.getPosition(ctx)));
					return null;
				}
			} else { // error thrown by visitMentalRule
				return returned;
			}
		} else if (ctx.TRUE() != null) {
			return new TrueLiteral(this.wh.getPosition(ctx));
		} else {
			this.wh.report(new ValidatorError(GOALError.CONDITION_INVALID,
					this.wh.getPosition(ctx)));
			return null;
		}
	}

	@Override
	public MentalFormula visitMentalRule(GOALParser.MentalRuleContext ctx) {
		if (ctx.function() != null) {
			Map.Entry<String, List<Term>> function = visitFunction(ctx
					.function());
			if (function != null) {
				String id = function.getKey();
				List<Term> parameters = function.getValue();
				return new Macro(id, parameters, null, this.wh.getPosition(ctx));
			} else { // error thrown in visitFunction
				return null;
			}
		} else if (ctx.mentalAction() != null) {
			return visitMentalAction(ctx.mentalAction());
		} else {
			this.wh.report(new ValidatorError(GOALError.CONDITION_INVALID,
					this.wh.getPosition(ctx)));
			return null;
		}
	}

	@Override
	public MentalLiteral visitMentalAction(GOALParser.MentalActionContext ctx) {
		MentalLiteral literal = null;
		if (ctx.mentalAtom() != null) { // error thrown by visitMentalAtomn
			literal = visitMentalAtom(ctx.mentalAtom());
		} else {
			this.wh.report(new ValidatorError(GOALError.CONDITION_INVALID,
					this.wh.getPosition(ctx)));
			return null;
		}
		if (literal != null) {
			Selector selector = new Selector(SelectorType.THIS,
					this.wh.getPosition(ctx)); // default
			if (ctx.selector() != null) {
				selector = visitSelector(ctx.selector()); // never null
			}
			literal.setSelector(selector);
			Query formula = null;
			if (ctx.KR_STATEMENT() != null && !ctx.KR_STATEMENT().isEmpty()
					&& ctx.LBR() != null && ctx.RBR() != null) {
				List<TerminalNode> krcontent = new ArrayList<>(ctx
						.KR_STATEMENT().size() + 2);
				krcontent.add(ctx.LBR());
				krcontent.addAll(ctx.KR_STATEMENT());
				krcontent.add(ctx.RBR());
				formula = this.wh.parseQuery(krcontent);
			}
			if (formula != null) {
				literal.setFormula(formula);
				return literal;
			} else {
				this.wh.report(new ValidatorError(
						GOALError.CONDITION_MISSING_BODY, this.wh
								.getPosition(ctx)));
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public MentalLiteral visitMentalAtom(GOALParser.MentalAtomContext ctx) {
		if (ctx.BELIEF() != null) {
			return new BelLiteral(this.wh.getPosition(ctx));
		} else if (ctx.GOAL() != null) {
			return new GoalLiteral(this.wh.getPosition(ctx));
		} else if (ctx.AGOAL() != null) {
			return new AGoalLiteral(this.wh.getPosition(ctx));
		} else if (ctx.GOALA() != null) {
			return new GoalALiteral(this.wh.getPosition(ctx));
		} else {
			this.wh.report(new ValidatorError(GOALError.CONDITION_INVALID,
					this.wh.getPosition(ctx)));
			return null;
		}
	}

	@Override
	public ActionCombo visitActions(GOALParser.ActionsContext ctx) {
		List<Action> actions = new ArrayList<>(ctx.action().size());
		for (GOALParser.ActionContext action : ctx.action()) {
			Action act = visitAction(action);
			if (act != null) { // error thrown by visitAction
				actions.add(act);
			}
		}
		try {
			ActionCombo combo = new ActionCombo(actions,
					this.wh.getPosition(ctx));
			return combo;
		} catch (Exception e) {
			this.wh.report(new ValidatorError(GOALError.ACTION_EMPTY, this.wh
					.getPosition(ctx)));
			return null;
		}
	}

	@Override
	public Action visitAction(GOALParser.ActionContext ctx) {
		if (ctx.EXITMODULE() != null) {
			return new ExitModuleAction(this.wh.getPosition(ctx));
		} else if (ctx.function() != null) {
			Map.Entry<String, List<Term>> function = visitFunction(ctx
					.function());
			if (function != null) {
				String id = function.getKey();
				List<Term> parameters = function.getValue();
				return new UserSpecOrModuleCall(id, parameters,
						this.wh.getPosition(ctx.function()));
			} else { // error thrown by visitFunction
				return null;
			}
		} else if (ctx.INIT() != null) {
			return new UserSpecOrModuleCall(ctx.INIT().getText(),
					new LinkedList<Term>(), this.wh.getPosition(ctx));
		} else if (ctx.EVENT() != null) {
			return new UserSpecOrModuleCall(ctx.EVENT().getText(),
					new LinkedList<Term>(), this.wh.getPosition(ctx));
		} else if (ctx.MAIN() != null) {
			return new UserSpecOrModuleCall(ctx.MAIN().getText(),
					new LinkedList<Term>(), this.wh.getPosition(ctx));
		} else if (ctx.actionAtom() != null) {
			Selector selector = new Selector(SelectorType.THIS,
					this.wh.getPosition(ctx)); // default
			boolean defaultSelector = false;
			if (ctx.actionAtom().selector() == null) {
				defaultSelector = true;
			} else {
				selector = visitSelector(ctx.actionAtom().selector()); // never
				// null
			}
			if (ctx.KR_STATEMENT() != null && !ctx.KR_STATEMENT().isEmpty()
					&& ctx.LBR() != null && ctx.RBR() != null) {
				List<TerminalNode> krcontent = new ArrayList<>(ctx
						.KR_STATEMENT().size() + 2);
				krcontent.add(ctx.LBR());
				krcontent.addAll(ctx.KR_STATEMENT());
				krcontent.add(ctx.RBR());
				if (ctx.actionAtom().ADOPT() != null) {
					Update goalu = this.wh.parseGoalUpdate(krcontent);
					if (goalu != null) {
						return new AdoptAction(selector, goalu,
								this.wh.getPosition(ctx.actionAtom()));
					} else {
						this.wh.report(new ValidatorError(
								GOALError.ATOM_NO_CONTENT, this.wh
										.getPosition(ctx), ctx.getText()));
						return null;
					}
				} else if (ctx.actionAtom().DELETE() != null) {
					Update belief = this.wh.parseBeliefUpdate(krcontent);
					if (belief != null) {
						return new DeleteAction(selector,
								belief.filterMailUpdates(false),
								belief.filterMailUpdates(true),
								this.wh.getPosition(ctx.actionAtom()));
					} else {
						this.wh.report(new ValidatorError(
								GOALError.ATOM_NO_CONTENT, this.wh
										.getPosition(ctx), ctx.getText()));
						return null;
					}
				} else if (ctx.actionAtom().DROP() != null) {
					Update goalu = this.wh.parseGoalUpdate(krcontent);
					if (goalu != null) {
						return new DropAction(selector, goalu,
								this.wh.getPosition(ctx.actionAtom()));
					} else {
						this.wh.report(new ValidatorError(
								GOALError.ATOM_NO_CONTENT, this.wh
										.getPosition(ctx), ctx.getText()));
						return null;
					}
				} else if (ctx.actionAtom().INSERT() != null) {
					Update belief = this.wh.parseBeliefUpdate(krcontent);
					if (belief != null) {
						return new InsertAction(selector,
								belief.filterMailUpdates(false),
								belief.filterMailUpdates(true),
								this.wh.getPosition(ctx.actionAtom()));
					} else {
						this.wh.report(new ValidatorError(
								GOALError.ATOM_NO_CONTENT, this.wh
										.getPosition(ctx), ctx.getText()));
						return null;
					}
				} else if (ctx.actionAtom().SEND() != null) {
					if (defaultSelector) {
						this.wh.report(new ValidatorError(
								GOALError.SEND_MISSING_SELECTOR, this.wh
										.getPosition(ctx), ctx.getText()));
						return null;
					} else {
						String send = WalkerHelperKR
								.implode(ctx.KR_STATEMENT()).trim()
								+ ctx.RBR().getText();
						SentenceMood mood = SentenceMood.INDICATIVE;
						if (send.startsWith("!")) {
							mood = SentenceMood.IMPERATIVE;
							send = send.substring(1);
						} else if (send.startsWith("?")) {
							mood = SentenceMood.INTERROGATIVE;
							send = send.substring(1);
						} else if (send.startsWith(":")) {
							send = send.substring(1);
						}
						DatabaseFormula content = this.wh.parseMood(ctx.LBR()
								.getText() + send,
								this.wh.getPosition(ctx.KR_STATEMENT()));
						if (content != null) {
							return new SendAction(selector, mood, content,
									this.wh.getPosition(ctx));
						} else {
							this.wh.report(new ValidatorError(
									GOALError.ATOM_NO_CONTENT, this.wh
											.getPosition(ctx), ctx.getText()));
							return null;
						}
					}
				} else if (ctx.actionAtom().SENDONCE() != null) {
					if (defaultSelector) {
						this.wh.report(new ValidatorError(
								GOALError.SEND_MISSING_SELECTOR, this.wh
										.getPosition(ctx), ctx.getText()));
						return null;
					} else {
						String send = WalkerHelperKR
								.implode(ctx.KR_STATEMENT()).trim()
								+ ctx.RBR().getText();
						SentenceMood mood = SentenceMood.INDICATIVE;
						if (send.startsWith("!")) {
							mood = SentenceMood.IMPERATIVE;
							send = send.substring(1);
						} else if (send.startsWith("?")) {
							mood = SentenceMood.INTERROGATIVE;
							send = send.substring(1);
						} else if (send.startsWith(":")) {
							send = send.substring(1);
						}
						DatabaseFormula content = this.wh.parseMood(ctx.LBR()
								.getText() + send,
								this.wh.getPosition(ctx.KR_STATEMENT()));
						if (content != null) {
							return new SendOnceAction(selector, mood, content,
									this.wh.getPosition(ctx));
						} else {
							this.wh.report(new ValidatorError(
									GOALError.ATOM_NO_CONTENT, this.wh
											.getPosition(ctx), ctx.getText()));
							return null;
						}
					}
				} else if (ctx.actionAtom().LOG() != null) {
					String content = WalkerHelperKR.implode(ctx.KR_STATEMENT())
							.trim();
					return new LogAction(selector, content,
							this.wh.getPosition(ctx));
				} else if (ctx.actionAtom().PRINT() != null) {
					Term argument = this.wh.parseTerm(ctx.KR_STATEMENT());
					return new PrintAction(argument, this.wh.getPosition(ctx));
				} else {
					this.wh.report(new ValidatorError(
							GOALError.ACTION_USED_NEVER_DEFINED, this.wh
									.getPosition(ctx.actionAtom()), ctx
									.actionAtom().getText()));
					return null;
				}
			} else {
				this.wh.report(new ValidatorError(GOALError.ATOM_NO_CONTENT,
						this.wh.getPosition(ctx), ctx.getText()));
				return null;
			}
		} else {
			this.wh.report(new ValidatorError(
					GOALError.ACTION_USED_NEVER_DEFINED, this.wh
							.getPosition(ctx), ctx.getText()));
			return null;
		}
	}

	@Override
	public Module visitAnonModule(GOALParser.AnonModuleContext ctx) {
		Module parent = null;
		for (Module module : this.anonymous.keySet()) {
			parent = module; // get the last one (which is the current one)
		}
		if (parent != null) {
			int count = this.anonymous.get(parent);
			String name = parent.getName() + "_sub" + count;
			this.anonymous.put(parent, count + 1);
			Module returned = new Module(name, null, TYPE.ANONYMOUS,
					this.wh.getPosition(ctx), this.wh.getKR());
			RuleSet rules = new RuleSet(parent.getRuleSet().getRuleOrder(),
					this.wh.getPosition(ctx));
			for (GOALParser.ProgramRuleContext programRule : ctx.programRule()) {
				Rule rule = visitProgramRule(programRule);
				if (rule != null) { // error thrown by visitProgramRule
					rules.addRule(rule);
				}
			}
			returned.setRuleSet(rules);
			parent.getNameSpace().addModule(returned);
			return returned;
		} else {
			this.wh.report(new ValidatorError(
					GOALError.ANONYMOUS_MODULE_NOT_IN_MODULE, this.wh
							.getPosition(ctx)));
			return null;
		}
	}

	@Override
	public Selector visitSelector(GOALParser.SelectorContext ctx) {
		Selector selector = new Selector(this.wh.getPosition(ctx));
		for (GOALParser.SelectExpContext selectExp : ctx.selectExp()) {
			SelectExpression select = visitSelectExp(selectExp);
			if (select != null) { // error thrown by visitSelectExp
				selector.add(select);
			}
		}
		return selector;
	}

	@Override
	public SelectExpression visitSelectExp(GOALParser.SelectExpContext ctx) {
		if (ctx.ALL() != null) {
			return new SelectExpression(SelectorType.ALL);
		} else if (ctx.ALLOTHER() != null) {
			return new SelectExpression(SelectorType.ALLOTHER);
		} else if (ctx.SELF() != null) {
			return new SelectExpression(SelectorType.SELF);
		} else if (ctx.SOME() != null) {
			return new SelectExpression(SelectorType.SOME);
		} else if (ctx.SOMEOTHER() != null) {
			return new SelectExpression(SelectorType.SOMEOTHER);
		} else if (ctx.THIS() != null) {
			return new SelectExpression(SelectorType.THIS);
		} else if (ctx.ID() != null) {
			List<TerminalNode> singleList = new ArrayList<>(1);
			singleList.add(ctx.ID());
			Term term = this.wh.parseTerm(singleList);
			if (term instanceof Var) {
				return new SelectExpression(SelectorType.VARIABLE, term);
			} else {
				return new SelectExpression(SelectorType.CONSTANT, term);
			}
		} else {
			this.wh.report(new ValidatorError(GOALError.SELECTOR_INVALID,
					this.wh.getPosition(ctx)));
			return null;
		}
	}

	// -------------------------------------------------------------
	// ACTIONSPECS section
	// -------------------------------------------------------------
	@Override
	public List<ActionSpecification> visitActionSpecs(
			GOALParser.ActionSpecsContext ctx) {
		List<ActionSpecification> specs = new ArrayList<>(ctx.actionSpec()
				.size());
		for (GOALParser.ActionSpecContext actionspec : ctx.actionSpec()) {
			ActionSpecification spec = visitActionSpec(actionspec);
			if (spec != null) { // error thrown by visitActionSpec
				specs.add(spec);
			}
		}
		return specs;
	}

	@Override
	public ActionSpecification visitActionSpec(GOALParser.ActionSpecContext ctx) {
		Map.Entry<String, List<Term>> action = null;
		if (ctx.function() != null) { // error thrown by visitFunction
			action = visitFunction(ctx.function());
		} else {
			this.wh.report(new ValidatorError(GOALError.FUNCTION_MISSING_NAME,
					this.wh.getPosition(ctx)));
			return null;
		}
		if (action != null) {
			boolean external = true;
			if (ctx.INTERNAL() != null) {
				external = false;
			}
			UserSpecAction userAction = new UserSpecAction(action.getKey(),
					action.getValue(), external, this.wh.getPosition(ctx
							.function()));
			Query pre = null;
			if (ctx.actionPre() != null) {
				pre = visitActionPre(ctx.actionPre());
			}
			if (pre == null) {
				this.wh.report(new ValidatorError(
						GOALError.ACTIONSPEC_MISSING_PRE, this.wh
								.getPosition(ctx)));
				return null;
			}
			Update post = null;
			if (ctx.actionPost() != null) {
				post = visitActionPost(ctx.actionPost());
			}
			if (post == null) {
				this.wh.report(new ValidatorError(
						GOALError.ACTIONSPEC_MISSING_POST, this.wh
								.getPosition(ctx)));
				return null;
			}
			return new ActionSpecification(userAction, pre, post,
					this.wh.getPosition(ctx));
		} else {
			return null;
		}
	}

	@Override
	public Query visitActionPre(GOALParser.ActionPreContext ctx) {
		if (ctx.KR_BLOCK() != null) {
			return this.wh.parseQueryOrEmpty(ctx.KR_BLOCK());
		} else {
			return null;
		}
	}

	@Override
	public Update visitActionPost(GOALParser.ActionPostContext ctx) {
		if (ctx.KR_BLOCK() != null) {
			return this.wh.parseBeliefUpdateOrEmpty(ctx.KR_BLOCK());
		} else {
			return null;
		}
	}

	@Override
	public Map.Entry<String, List<Term>> visitFunction(
			GOALParser.FunctionContext ctx) {
		if (ctx.ID() != null) {
			String name = ctx.ID().getText().trim();
			List<Term> parameters = new LinkedList<>();
			if (ctx.KR_STATEMENT() != null && !ctx.KR_STATEMENT().isEmpty()
					&& ctx.LBR() != null && ctx.RBR() != null) {
				List<TerminalNode> krcontent = new ArrayList<>(ctx
						.KR_STATEMENT().size() + 2);
				krcontent.add(ctx.LBR());
				krcontent.addAll(ctx.KR_STATEMENT());
				krcontent.add(ctx.RBR());
				parameters = this.wh.parseTerms(krcontent);
			}
			return new AbstractMap.SimpleEntry<>(name, parameters);
		} else {
			this.wh.report(new ValidatorError(GOALError.FUNCTION_MISSING_NAME,
					this.wh.getPosition(ctx)));
			return null;
		}
	}

	// -------------------------------------------------------------
	// Initialization helpers
	// -------------------------------------------------------------
	public static GOALWalker getWalker(File file, KRlanguage language)
			throws Exception {
		ANTLRFileStream antlrStream = new ANTLRFileStream(file.getPath());
		GOALLexer lexer = new GOALLexer(antlrStream);
		CommonTokenStream stream = new CommonTokenStream(lexer);
		GOALParser parser = new GOALParser(stream);
		GOALWalker walker = new GOALWalker(file, parser, lexer, language);
		return walker;
	}

	protected GOALProgram parseSubFile(File file, InputStreamPosition source) {
		try {
			GOALWalker subwalker = getWalker(file, this.wh.getKR());
			subwalker.setProgram(this.program);
			GOALProgram subprogram = subwalker.getProgram();
			this.wh.merge(subwalker.getWalkerHelper());
			return subprogram;
		} catch (Exception e) {
			this.wh.report(new ValidatorError(GOALError.EXTERNAL_OR_UNKNOWN,
					source, e.getMessage()));
			return null;
		}
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
