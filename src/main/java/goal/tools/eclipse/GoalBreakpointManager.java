package goal.tools.eclipse;

import goal.tools.debugger.BreakPoint;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class GoalBreakpointManager {
	private final static String DELIMITER = "|";
	private final static Map<String, GoalBreakpointManager> breakManagers = new HashMap<String, GoalBreakpointManager>();
	private final File file;
	private final Map<Integer, BreakPoint> breakpoints;

	public static GoalBreakpointManager getGoalBreakpointManager(final File f) {
		return breakManagers.get(f.getPath());
	}

	public static GoalBreakpointManager getOrCreateGoalBreakpointManager(
			final File f) {
		final GoalBreakpointManager existing = getGoalBreakpointManager(f);
		if (existing == null) {
			final GoalBreakpointManager created = new GoalBreakpointManager(f);
			breakManagers.put(f.getPath(), created);
			return created;
		} else {
			return existing;
		}
	}

	private GoalBreakpointManager(final File f) {
		this.file = f;
		this.breakpoints = new HashMap<>();
	}

	public File getFile() {
		return this.file;
	}

	public void addBreakpoint(final int line, final BreakPoint.Type type) {
		this.breakpoints.put(line, new BreakPoint(this.file, line, type));
	}

	public void removeBreakpoint(final int line) {
		this.breakpoints.remove(line);
	}

	public Set<BreakPoint> getBreakPoints() {
		return new HashSet<>(this.breakpoints.values());
	}

	@Override
	public String toString() {
		final StringBuffer buffer = new StringBuffer();
		buffer.append(this.file.getPath()).append(DELIMITER)
				.append(this.breakpoints.size());
		for (final int i : this.breakpoints.keySet()) {
			final BreakPoint point = this.breakpoints.get(i);
			buffer.append(DELIMITER).append(point.getLine()).append(DELIMITER)
					.append(point.getType().name());
		}
		return buffer.toString();
	}

	public static GoalBreakpointManager fromString(final String string) {
		final String[] s = string.split("\\" + DELIMITER);
		final File f = new File(s[0]);
		final GoalBreakpointManager manager = new GoalBreakpointManager(f);
		final int size = Integer.parseInt(s[1]) * 2;
		for (int i = 2; i <= (size + 1); i += 2) {
			manager.addBreakpoint(Integer.parseInt(s[i]),
					BreakPoint.Type.valueOf(s[i + 1]));
		}
		return manager;
	}

	public static String saveAll() {
		final StringBuffer buffer = new StringBuffer();
		for (final GoalBreakpointManager manager : breakManagers.values()) {
			buffer.append(DELIMITER).append(DELIMITER)
					.append(manager.toString());
		}
		return buffer.toString();
	}

	public static void loadAll(final String input) {
		final String[] split = input.split("\\" + DELIMITER + "\\" + DELIMITER);
		for (final String s1 : split) {
			final String s = s1.trim();
			if (!s.isEmpty()) {
				final GoalBreakpointManager manager = fromString(s);
				breakManagers.put(manager.getFile().getPath(), manager);
			}
		}
	}
}
