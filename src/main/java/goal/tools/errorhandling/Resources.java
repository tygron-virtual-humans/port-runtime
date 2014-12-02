package goal.tools.errorhandling;

import goal.tools.errorhandling.exceptions.GOALBug;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Util class. Takes an (enumeration) element from a {@link ResourceId}, and
 * finds the resource associated with it.
 *
 * @author W.Pasman 21may2014
 *
 */
public class Resources {
	/**
	 * Get the given resource associated with the given resourceId
	 *
	 * @param res
	 * @param args
	 * @return
	 * @throws MissingResourceException
	 */
	public static String get(ResourceId res) throws MissingResourceException {
		ResourceBundle bundle = res.getBundle();
		return bundle.getString(res.toString());

	}

	/**
	 * Check that the given resource is consistent
	 *
	 * @param resources
	 */
	public static void check(Class<? extends Enum<?>> resources) {
		Object[] values = resources.getEnumConstants();
		if (values.length == 0) {
			return; // we can't check empty resources.
		}
		if (!(values[0] instanceof ResourceId)) {
			throw new IllegalArgumentException("value " + values[0]
					+ " is not instance of ResourceId");
		}
		try {
			// check that all enum values are in the resource bundle
			// we iterate so that we can give nice error message if not.
			for (Object v : values) {
				ResourceId val = (ResourceId) v;
				Resources.get(val);
			}
		} catch (MissingResourceException e) {
			throw new GOALBug("Missing resource in " + resources, e);
		}
	}

}
