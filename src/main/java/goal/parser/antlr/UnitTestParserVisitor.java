// Generated from UnitTestParser.g4 by ANTLR 4.4
package goal.parser.antlr;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link UnitTestParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface UnitTestParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#selectExp}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelectExp(@NotNull UnitTestParser.SelectExpContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#selector}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSelector(@NotNull UnitTestParser.SelectorContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#exitOption}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExitOption(@NotNull UnitTestParser.ExitOptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#beliefs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitBeliefs(@NotNull UnitTestParser.BeliefsContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#test}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTest(@NotNull UnitTestParser.TestContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#moduleDef}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModuleDef(@NotNull UnitTestParser.ModuleDefContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#ltl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLtl(@NotNull UnitTestParser.LtlContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#ltlEventually}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLtlEventually(@NotNull UnitTestParser.LtlEventuallyContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#actionSpecs}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActionSpecs(@NotNull UnitTestParser.ActionSpecsContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#moduleImport}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModuleImport(@NotNull UnitTestParser.ModuleImportContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#focusOption}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFocusOption(@NotNull UnitTestParser.FocusOptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#function}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunction(@NotNull UnitTestParser.FunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#actionPre}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActionPre(@NotNull UnitTestParser.ActionPreContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#agentTest}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAgentTest(@NotNull UnitTestParser.AgentTestContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#macro}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMacro(@NotNull UnitTestParser.MacroContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#actionPost}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActionPost(@NotNull UnitTestParser.ActionPostContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#action}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAction(@NotNull UnitTestParser.ActionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#ltlAtStart}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLtlAtStart(@NotNull UnitTestParser.LtlAtStartContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#testCondition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTestCondition(@NotNull UnitTestParser.TestConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#ltlAtEnd}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLtlAtEnd(@NotNull UnitTestParser.LtlAtEndContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#programRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgramRule(@NotNull UnitTestParser.ProgramRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#condition}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCondition(@NotNull UnitTestParser.ConditionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#mentalAtom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMentalAtom(@NotNull UnitTestParser.MentalAtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#orderOption}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitOrderOption(@NotNull UnitTestParser.OrderOptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#mentalAction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMentalAction(@NotNull UnitTestParser.MentalActionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#ltlUntil}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLtlUntil(@NotNull UnitTestParser.LtlUntilContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#conditions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditions(@NotNull UnitTestParser.ConditionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#moduleOption}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModuleOption(@NotNull UnitTestParser.ModuleOptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#actionAtom}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActionAtom(@NotNull UnitTestParser.ActionAtomContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#evaluateIn}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEvaluateIn(@NotNull UnitTestParser.EvaluateInContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#program}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitProgram(@NotNull UnitTestParser.ProgramContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#moduleOptions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModuleOptions(@NotNull UnitTestParser.ModuleOptionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#listallRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListallRule(@NotNull UnitTestParser.ListallRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#knowledge}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitKnowledge(@NotNull UnitTestParser.KnowledgeContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#testSection}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTestSection(@NotNull UnitTestParser.TestSectionContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#ltlNever}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLtlNever(@NotNull UnitTestParser.LtlNeverContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#forallRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitForallRule(@NotNull UnitTestParser.ForallRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#mentalRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMentalRule(@NotNull UnitTestParser.MentalRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#ltlModule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLtlModule(@NotNull UnitTestParser.LtlModuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#assertTest}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssertTest(@NotNull UnitTestParser.AssertTestContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#ltlAlways}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLtlAlways(@NotNull UnitTestParser.LtlAlwaysContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#masFile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMasFile(@NotNull UnitTestParser.MasFileContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#module}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModule(@NotNull UnitTestParser.ModuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#ltlWhile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLtlWhile(@NotNull UnitTestParser.LtlWhileContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#agentTests}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAgentTests(@NotNull UnitTestParser.AgentTestsContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#testBoundary}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTestBoundary(@NotNull UnitTestParser.TestBoundaryContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#goals}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGoals(@NotNull UnitTestParser.GoalsContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#unitTest}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnitTest(@NotNull UnitTestParser.UnitTestContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#anonModule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnonModule(@NotNull UnitTestParser.AnonModuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#actionSpec}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActionSpec(@NotNull UnitTestParser.ActionSpecContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#ifRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIfRule(@NotNull UnitTestParser.IfRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#modules}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitModules(@NotNull UnitTestParser.ModulesContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#doActions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDoActions(@NotNull UnitTestParser.DoActionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#actions}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitActions(@NotNull UnitTestParser.ActionsContext ctx);
	/**
	 * Visit a parse tree produced by {@link UnitTestParser#timeout}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitTimeout(@NotNull UnitTestParser.TimeoutContext ctx);
}