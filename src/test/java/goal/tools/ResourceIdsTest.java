package goal.tools;

import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.WarningStrings;

import org.junit.Test;

/**
 * Tests that all resources are consistent.
 *
 * @author W.Pasman 22may2014
 *
 */
public class ResourceIdsTest {

	@Test
	public void testWarningStrings() {
		Resources.check(WarningStrings.class);
	}

}
