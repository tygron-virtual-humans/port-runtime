package goal.tools.eclipse;

import goal.tools.PlatformManager;
import goal.tools.SingleRun;
import goal.tools.logging.Loggers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import languageTools.program.mas.MASProgram;

public class UTTool {
	public static void main(final String[] args) {
		try {
			Loggers.addConsoleLogger();
			final File mas2g = new File(args[0]);
			final SingleRun run = new SingleRun(mas2g, 0);
			final MASProgram program = PlatformManager.getCurrent()
					.getMASProgram(mas2g);
			program.setEnvironmentfile(new File(args[1]));
			program.resetInitParameters();
			Map<String, Object> init = parseInit(program.getInitParameters(),
					args[2], args[3]);
			for (final String key : init.keySet()) {
				program.addInitParameter(key, init.get(key));
			}
			run.run();
			System.exit(0);
		} catch (final Exception e) { // outer exception logging
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private static Map<String, Object> parseInit(
			final Map<String, Object> oldInit, final String IP,
			final String team) {
		final Map<String, Object> newInit = new HashMap<>(oldInit.size());
		for (final String key : oldInit.keySet()) {
			final Object newParam = parseParameter(key, oldInit.get(key), IP,
					team);
			if (newParam != null) {
				newInit.put(key, newParam);
			}
		}
		return newInit;
	}

	private static String last = "";

	@SuppressWarnings("unchecked")
	private static Object parseParameter(final String key, final Object value,
			final String IP, final String team) {
		Object newValue = value;
		String stringValue = "";
		if (value instanceof String) {
			stringValue = (String) value;
		} else if (value instanceof Number) {
			stringValue = ((Number) value).toString();
		} else if (value instanceof List) {
			final List<Object> list = (List<Object>) value;
			final List<Object> newList = new ArrayList<>(list.size());
			for (final Object p : list) {
				final Object newParam = parseParameter("", p, IP, team);
				if (newParam != null) {
					newList.add(newParam);
				}
			}
			return newList;
		}
		if (key.equalsIgnoreCase("visualizer")) {
			newValue = "rmi://" + IP + ":1099";
		} else if (key.equalsIgnoreCase("botserver")) {
			newValue = "ut://" + IP + ":3000";
		} else if (key.equalsIgnoreCase("controlserver")) {
			newValue = "ut://" + IP + ":3001";
		} else if (key.equalsIgnoreCase("loglevel")) {
			newValue = "SEVERE";
		} else if (last.equalsIgnoreCase("skill")) {
			newValue = 5;
		} else if (last.equalsIgnoreCase("team")) {
			newValue = team;
		} else if (last.equalsIgnoreCase("loglevel")) {
			newValue = "SEVERE";
		} else if (last.equalsIgnoreCase("startlocation")) {
			newValue = null;
		}
		if (newValue == null) {
			last = "";
		} else {
			last = stringValue;
		}
		return newValue;
	}
}
