package goal.tools;

import goal.tools.adapt.FileLearner;
import goal.tools.errorhandling.Warning;
import goal.tools.errorhandling.exceptions.GOALRunFailedException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import krTools.errors.exceptions.ParserException;
import languageTools.program.mas.MASProgram;
import localmessaging.LocalMessaging;
import nl.tudelft.goal.messaging.Messaging;

/**
 * A Batch will run a batch of {@link MASProgram}s a repeated number of times.
 * Between runs the state of the agents {@link FileLearner} can be persisted by
 * setting a {@link PersistanceHelper}. To inspect the results of each run the
 * {@link ResultInspector} can be used.
 *
 * @author mpkorstanje
 */
public class BatchRun {
	private long repeats = 1;
	private long timeout;

	private final List<File> masFiles;

	/**
	 * Default is to <b>not</b> print any debugger output to console.
	 */
	private boolean debuggerOutput = false;

	/**
	 * Host for messaging service.
	 */
	private Messaging messaging = new LocalMessaging();

	private String messagingHost = "localhost";

	/**
	 * Creates an instance of {@link BatchRun} that can be used to run the
	 * <code>masFile</code>>.
	 *
	 * @param masFile
	 *            to use in this {@link BatchRun}
	 */
	public BatchRun(File... masFile) {
		this(Arrays.asList(masFile));
	}

	/**
	 * Creates an instance of {@link BatchRun} using the the {@link MASProgram}
	 * (s).
	 *
	 * @param masFiles
	 *            to use in this {@link BatchRun}
	 */
	public BatchRun(List<File> masFiles) {
		this.masFiles = masFiles;
	}

	/**
	 * Sets the number of times the {@link BatchRun} is repeated.
	 *
	 * @param times
	 *            the {@link BatchRun} is repeated
	 */
	public void setRepeats(long times) {
		this.repeats = times;
	}

	/**
	 * Sets a timeout for the {@link BatchRun}.
	 *
	 * @param timeout
	 *            the number of seconds we should wait for the {@link BatchRun}
	 *            to terminate; 0 for indefinite.
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * Starts the BatchRun. This will repeat running all {@link MASProgram}s for
	 * a given number of times.
	 *
	 * @throws Exception
	 *             thrown when a run fails; if multiple runs fail, only the last
	 *             exception is thrown, e.g. all runs are executed at all times
	 */
	public void run() throws GOALRunFailedException {
		GOALRunFailedException last = null;
		for (long i = 0; i < this.repeats; i++) {
			for (File masFile : this.masFiles) {
				try {
					SingleRun singleRun;
					try {
						singleRun = new SingleRun(masFile, this.timeout);
					} catch (ParserException e) {
						throw new GOALRunFailedException("could not parse mas "
								+ masFile, e);
					}
					singleRun.setDebuggerOutput(this.debuggerOutput);
					singleRun.setMessaging(this.messaging);
					singleRun.setMessagingHost(this.messagingHost);
					singleRun.run();
				} catch (GOALRunFailedException any) { // top level reporting
					new Warning("Repeat " + i + " of " + masFile
							+ " threw exception", any);
					last = any;
				}
			}
		}
		if (last != null) {
			throw last;
		}
	}

	/**
	 * Returns true if the {@link MASProgram} will be run with a debugger that
	 * logs output.
	 *
	 * @return if true the {@link MASProgram} will be run with a printing
	 *         debugger.
	 */
	public boolean getDebuggerOutput() {
		return this.debuggerOutput;
	}

	/**
	 * Set to true to run the {@link MASProgram} with a debugger that logs
	 * output.
	 *
	 * @param debuggerOutput
	 *            true if the debugger should output to the console
	 */
	public void setDebuggerOutput(boolean debuggerOutput) {
		this.debuggerOutput = debuggerOutput;
	}

	/**
	 * @return the messagingHost
	 */
	public String getMessagingHost() {
		return this.messagingHost;
	}

	/**
	 * @param messagingHost
	 *            the messagingHost to set
	 */
	public void setMessagingHost(String messagingHost) {
		this.messagingHost = messagingHost;
	}

	/**
	 * Set the messaging used.
	 *
	 * @param messaging
	 *            the messaging type to use in this batch run.
	 */
	public void setMessaging(Messaging messaging) {
		this.messaging = messaging;
	}
}