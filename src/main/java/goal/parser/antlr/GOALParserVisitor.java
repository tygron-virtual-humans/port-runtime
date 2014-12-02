// Generated from GOALParser.g4 by ANTLR 4.4
package goal.parser.antlr;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link GOALParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface GOALParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link GOALParser#selectExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectExp(@NotNull GOALParser.SelectExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#selector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelector(@NotNull GOALParser.SelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#exitOption}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExitOption(@NotNull GOALParser.ExitOptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#listallRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListallRule(@NotNull GOALParser.ListallRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#knowledge}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKnowledge(@NotNull GOALParser.KnowledgeContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#beliefs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBeliefs(@NotNull GOALParser.BeliefsContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#moduleDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModuleDef(@NotNull GOALParser.ModuleDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#actionSpecs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActionSpecs(@NotNull GOALParser.ActionSpecsContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#moduleImport}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModuleImport(@NotNull GOALParser.ModuleImportContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#focusOption}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFocusOption(@NotNull GOALParser.FocusOptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(@NotNull GOALParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#actionPre}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActionPre(@NotNull GOALParser.ActionPreContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#macro}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMacro(@NotNull GOALParser.MacroContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#actionPost}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActionPost(@NotNull GOALParser.ActionPostContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#mentalRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMentalRule(@NotNull GOALParser.MentalRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#forallRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForallRule(@NotNull GOALParser.ForallRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction(@NotNull GOALParser.ActionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#module}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule(@NotNull GOALParser.ModuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition(@NotNull GOALParser.ConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#programRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgramRule(@NotNull GOALParser.ProgramRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#mentalAtom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMentalAtom(@NotNull GOALParser.MentalAtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#orderOption}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrderOption(@NotNull GOALParser.OrderOptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#mentalAction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMentalAction(@NotNull GOALParser.MentalActionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#conditions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditions(@NotNull GOALParser.ConditionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#goals}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGoals(@NotNull GOALParser.GoalsContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#moduleOption}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModuleOption(@NotNull GOALParser.ModuleOptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#actionAtom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActionAtom(@NotNull GOALParser.ActionAtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(@NotNull GOALParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#anonModule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnonModule(@NotNull GOALParser.AnonModuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#actionSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActionSpec(@NotNull GOALParser.ActionSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#ifRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfRule(@NotNull GOALParser.IfRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#modules}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModules(@NotNull GOALParser.ModulesContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#actions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActions(@NotNull GOALParser.ActionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link GOALParser#moduleOptions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModuleOptions(@NotNull GOALParser.ModuleOptionsContext ctx);
}