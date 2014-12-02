package goal.tools.unittest.result.testsection;

import goal.tools.unittest.Test;
import goal.tools.unittest.result.ResultFormatter;

/**
 * Result of evaluating a test section in a {@link Test}. A result should accept
 * a formatter to provide information about its outcome status.
 *
 * @see TestSectionFailed
 * @see TestSectionInterupted
 *
 * @author mpkorstanje
 */
public interface TestSectionResult {
	/**
	 * @param formatter
	 * @return
	 */
	public <T> T accept(ResultFormatter<T> formatter);
}
