// Generated from MASParser.g4 by ANTLR 4.4
package goal.parser.antlr;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MASParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MASParserVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MASParser#simpleLaunchRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleLaunchRule(@NotNull MASParser.SimpleLaunchRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#initParams}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitParams(@NotNull MASParser.InitParamsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#environmentFile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnvironmentFile(@NotNull MASParser.EnvironmentFileContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#launchPolicy}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLaunchPolicy(@NotNull MASParser.LaunchPolicyContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#functionInitValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionInitValue(@NotNull MASParser.FunctionInitValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#agentFiles}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAgentFiles(@NotNull MASParser.AgentFilesContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#listInitValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitListInitValue(@NotNull MASParser.ListInitValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#initParam}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitParam(@NotNull MASParser.InitParamContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#simpleInitValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleInitValue(@NotNull MASParser.SimpleInitValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#entityDescription}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntityDescription(@NotNull MASParser.EntityDescriptionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#entityConstraint}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntityConstraint(@NotNull MASParser.EntityConstraintContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#environment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEnvironment(@NotNull MASParser.EnvironmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#entityConstraints}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEntityConstraints(@NotNull MASParser.EntityConstraintsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#conditionalLaunchRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConditionalLaunchRule(@NotNull MASParser.ConditionalLaunchRuleContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#launchRuleComponents}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLaunchRuleComponents(@NotNull MASParser.LaunchRuleComponentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#initValue}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitValue(@NotNull MASParser.InitValueContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#agentFileParameter}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAgentFileParameter(@NotNull MASParser.AgentFileParameterContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#agentFile}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAgentFile(@NotNull MASParser.AgentFileContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#launchRuleComponent}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLaunchRuleComponent(@NotNull MASParser.LaunchRuleComponentContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#mas}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMas(@NotNull MASParser.MasContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#initValues}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInitValues(@NotNull MASParser.InitValuesContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#agentFileParameters}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAgentFileParameters(@NotNull MASParser.AgentFileParametersContext ctx);
	/**
	 * Visit a parse tree produced by {@link MASParser#launchRule}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLaunchRule(@NotNull MASParser.LaunchRuleContext ctx);
}