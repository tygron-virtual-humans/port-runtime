package goal.core.kr;

import goal.preferences.PMPreferences;
import goal.tools.errorhandling.Resources;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.WarningStrings;
import goal.tools.errorhandling.exceptions.KRInitFailedException;

import java.util.Hashtable;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Factory for language support.
 *
 * @author W.Pasman 6sept11
 */
public class KRFactory {
	/**
	 * A map of string names to {@link KRlanguage}s that are recognized by GOAL.
	 */
	private static Map<String, KRlanguage> languages = new Hashtable<>();

	static {
		// DD130822: NOTE: Since we do NO LONGER SUPPORT
		// swiprolog.engines.SWIPrologLanguage
		// and DEFAULT now is swiprolog3.engines.SWIPrologLanguage, the name of
		// this KR service
		// has been changed to just 'swiprolog'.

		// Initialize KR support.
		// swipl
		// DD130822: WE NO LONGER SUPPORT swiprolog.engines.SWIPrologLanguage.
		// try {
		// KRFactory.add(swiprolog.engines.SWIPrologLanguage.getInstance());
		// } catch (KRInitFailedException e1) {
		// new Warning("Failed to initialize SWI Prolog language.");
		// }
		// swipl2
		// DD130822 FIXME: swiprolog2.engines.SWIPrologLanguage is in need of
		// serious testing
		// and not currently supported...
		// KRFactory.add(swiprolog2.engines.SWIPrologLanguage.getInstance());
		// swipl3
		try {
			KRFactory.add(swiprolog3.engines.SWIPrologLanguage.getInstance());
		} catch (KRInitFailedException e) {
			new Warning(Resources.get(WarningStrings.FAILED_INIT_SWI3));
		}
	}

	/**
	 * Factory is utility class.
	 */
	private KRFactory() {
	}

	/**
	 * Get an implementation for given language.<br>
	 * We store based on strings because we cannot know the available languages
	 * beforehand.
	 *
	 * @param language
	 *            The name of the language, e.g. "swiprolog" or "pddl" (not
	 *            case-sensitive).
	 * @return an implementation of the given language.
	 * @throws NoSuchElementException
	 *             if language is unknown.
	 */
	public static KRlanguage get(String language) throws NoSuchElementException {
		KRlanguage krLanguage = languages.get(language.toLowerCase());
		if (krLanguage == null) {
			throw new NoSuchElementException("language " + language
					+ " is unknown. Known languages are " + languages.keySet());
		}
		return krLanguage;
	}

	/**
	 * Add a language to the list of known languages.
	 *
	 * @param implementation
	 *            The KRlanguage.
	 */
	public static void add(KRlanguage implementation) {
		if (implementation == null) {
			throw new IllegalArgumentException(
					"language interpretation can't be null");
		}
		languages.put(implementation.getName(), implementation);
	}

	/**
	 * @return All registered KR languages.
	 */
	public static Set<String> getLanguages() {
		return languages.keySet();
	}

	/**
	 * Returns the default KR language.
	 *
	 * @return The default KR language.
	 * @throw {@link NoSuchElementException} if the default language is not
	 *        available.
	 */
	public static KRlanguage getDefaultLanguage() {
		return get(PMPreferences.getDefaultKRLanguage());
	}
}