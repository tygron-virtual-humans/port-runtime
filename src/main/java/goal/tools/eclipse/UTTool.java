package goal.tools.eclipse;

import eis.iilang.Identifier;
import eis.iilang.Numeral;
import eis.iilang.Parameter;
import eis.iilang.ParameterList;
import languageTools.program.mas.MASProgram;
import goal.tools.PlatformManager;
import goal.tools.SingleRun;
import goal.tools.logging.Loggers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UTTool {
	public static void main(final String[] args) {
		try {
			Loggers.addConsoleLogger();
			final File mas2g = new File(args[0]);
			final SingleRun run = new SingleRun(mas2g);
			final MASProgram program = PlatformManager.getCurrent()
					.getMASProgram(mas2g);
			final EnvironmentInfo info = program.getEnvironmentInfo();
			info.addJar(args[1]);
			info.setEnvironmentFile(new File(args[1]));
			info.setInitParameters(parseInit(info.getInitParameters(), args[2],
					args[3]));
			run.run();
			System.exit(0);
		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	private static Map<String, Parameter> parseInit(
			final Map<String, Parameter> oldInit, final String IP,
			final String team) {
		final Map<String, Parameter> newInit = new HashMap<>(oldInit.size());
		for (final String key : oldInit.keySet()) {
			final Parameter newParam = parseParameter(key, oldInit.get(key),
					IP, team);
			if (newParam != null) {
				newInit.put(key, newParam);
			}
		}
		return newInit;
	}

	private static String last = "";

	private static Parameter parseParameter(final String key,
			final Parameter value, final String IP, final String team) {
		Parameter newValue = value;
		String stringValue = "";
		if (value instanceof Identifier) {
			stringValue = ((Identifier) value).getValue();
		} else if (value instanceof Numeral) {
			stringValue = ((Numeral) value).getValue().toString();
		} else if (value instanceof ParameterList) {
			final ParameterList list = (ParameterList) value;
			final ParameterList newList = new ParameterList();
			for (final Parameter p : list) {
				final Parameter newParam = parseParameter("", p, IP, team);
				if (newParam != null) {
					newList.add(newParam);
				}
			}
			return newList;
		}
		if (key.equalsIgnoreCase("visualizer")) {
			newValue = new Identifier("rmi://" + IP + ":1099");
		} else if (key.equalsIgnoreCase("botserver")) {
			newValue = new Identifier("ut://" + IP + ":3000");
		} else if (key.equalsIgnoreCase("controlserver")) {
			newValue = new Identifier("ut://" + IP + ":3001");
		} else if (key.equalsIgnoreCase("loglevel")) {
			newValue = new Identifier("SEVERE");
		} else if (last.equalsIgnoreCase("skill")) {
			newValue = new Numeral(5);
		} else if (last.equalsIgnoreCase("team")) {
			newValue = new Identifier(team);
		} else if (last.equalsIgnoreCase("loglevel")) {
			newValue = new Identifier("SEVERE");
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
