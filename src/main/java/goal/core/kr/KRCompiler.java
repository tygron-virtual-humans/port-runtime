package goal.core.kr;

import goal.core.kr.language.CompiledQuery;
import goal.core.program.literals.MentalStateCond;

/**
 * interface for KR specific compilation options.
 *
 * @author W.Pasman 12jan2012
 *
 */
public interface KRCompiler {

	/**
	 * Compile a mental state condition into a KR query. The resulting query is
	 * still independent of the agent (as compilation usually takes place before
	 * the agent name is known).
	 *
	 * @param condition
	 *            is the MSC to be compiled
	 * @param agentselector
	 *            is the agent selector applied to the
	 * @return compiled query. This query returns all solutions for the complete
	 *         MSC in one go.
	 */
	public CompiledQuery compile(MentalStateCond condition);
}
