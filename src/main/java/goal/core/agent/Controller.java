package goal.core.agent;

import goal.preferences.PMPreferences;
import goal.tools.AbstractRun;

import java.rmi.activation.UnknownObjectException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import krTools.errors.exceptions.KRDatabaseException;
import krTools.errors.exceptions.KRInitFailedException;
import krTools.errors.exceptions.KRQueryFailedException;

/**
 * Controller for an {@link Agent}.
 *
 * The Controller starts a process that can be used by subclasses to drive the
 * agents actions. This process can be stopped, started and reset through the
 * agent.
 *
 * @author mpkorstanje
 */
public abstract class Controller {
	private final static Executor pool = Executors
			.newFixedThreadPool(PMPreferences.getThreadPoolSize());

	/**
	 * The agent controlled by the controller.
	 */
	protected Agent<? extends Controller> agent;
	/**
	 * A throwable that can be set by an implementation of this class upon any
	 * unexpected error.
	 */
	protected Throwable throwable = null;
	// Private booleans...
	private volatile boolean running = false;
	private volatile boolean terminated = true;
	private boolean disposeOnTermination = false;

	/**
	 * Initializes the controller with the agent it controls. Subclasses can
	 * override this method to do their own initialization against the agent.
	 * Subclasses should take care to call super method.
	 *
	 * @param agent
	 *            controlled by this controller.
	 * @throws KRInitFailedException
	 *             when the controller could not initialize the KRInterface.
	 */
	protected void initalizeController(Agent<? extends Controller> agent)
			throws KRInitFailedException {
		this.agent = agent;
	}

	/**
	 * @return any uncaught throwable caught during the execution of the agent.
	 */
	public final Throwable getUncaughtThrowable() {
		return this.throwable;
	}

	/**
	 * The agent can be disposed after its process terminates. This releases any
	 * resources held by the agent, which can increase performance in systems
	 * with many ephemeral agents.
	 *
	 * By default the agent is not disposed; this functions enables the
	 * disposing upon termination.
	 */
	public final void setDisposeOnTermination() {
		this.disposeOnTermination = true;
	}

	/**
	 * @return true when the agent is running.
	 */
	public final boolean isRunning() {
		return this.running;
	}

	/**
	 * @return true when the agent has completely finished running (cleaned-up
	 *         all its resources and such).
	 */
	public final boolean isTerminated() {
		return this.terminated;
	}

	/**
	 * Starts the agent. Only external classes should call this.
	 */
	public final void run() {
		if (!this.running) {
			this.running = true;
			this.terminated = false;
			pool.execute(getRunnable(pool, null));
		}
	}

	/**
	 * Stops the agent. Only external classes should call this. Currently, the
	 * termination is not immediate, but it prevents an agent from submitting
	 * its next task to the thread pool, after which it cleans up itself. Use
	 * awaitTermination to ensure the agent has really terminated itself.
	 */
	public final void terminate() {
		if (this.running) {
			this.running = false;
			onTerminate();
		}
	}

	/**
	 * This function can be implemented by clients; it does nothing by default.
	 * It is run upon a call to terminate.
	 */
	protected void onTerminate() {
	}

	/**
	 * Flag other processes that we are really done running, and dispose our
	 * resources when disposeOnTermination is true. An implementing class should
	 * always call this: it sees running=false upon which it cleans up itself
	 * and then calls this function afterwards.
	 */
	protected final void setTerminated() {
		if (!this.terminated) {
			terminate(); // just to be sure
			this.terminated = true;
			if (this.disposeOnTermination) {
				try {
					dispose();
				} catch (InterruptedException ignore) {
				}
			}
		}
	}

	/**
	 * Resets the agent. To reset the agent, it is first stopped and then
	 * started again.
	 *
	 * @throws InterruptedException
	 *             when interrupted while waiting for the agent to stop.
	 * @throws KRInitFailedException
	 *             when unable to initialize the KRInterface when restarting the
	 *             agent.
	 * @throws KRDatabaseException
	 * @throws KRQueryFailedException
	 * @throws UnknownObjectException
	 */
	public final void reset() throws InterruptedException,
	KRInitFailedException, KRDatabaseException, KRQueryFailedException,
	UnknownObjectException {
		terminate();
		awaitTermination(AbstractRun.TIMEOUT_FIRST_AGENT_SECONDS);
		onReset();
		run();
	}

	/**
	 * This function can be implemented by clients; it does nothing by default.
	 * It is run upon a call to reset, right after an agent is stopped and
	 * before it runs again, to allow external resources to reset as well.
	 *
	 * @throws InterruptedException
	 *             when interrupted while waiting for the agent to reset.
	 * @throws KRInitFailedException
	 *             when unable to initialize the KRInterface when restarting the
	 *             agent.
	 * @throws KRDatabaseException
	 * @throws KRQueryFailedException
	 * @throws UnknownObjectException
	 */
	protected void onReset() throws InterruptedException,
	KRInitFailedException, KRDatabaseException, KRQueryFailedException,
	UnknownObjectException {
	}

	/**
	 * Waits for the agent to terminate. This method will only return once the
	 * agent has stopped.
	 *
	 * @param timeout
	 * @throws InterruptedException
	 *             when interrupted while waiting for the agent to stop
	 */
	public final void awaitTermination(long timeout)
			throws InterruptedException {
		if (timeout <= 0) {
			timeout = Long.MAX_VALUE;
		} else {
			timeout = System.currentTimeMillis() + (timeout * 1000L);
		}
		while (!this.terminated) {
			Thread.sleep(100);
			if (System.currentTimeMillis() > timeout) {
				break;
			}
		}
	}

	/**
	 * Open to client implementation; simply calls terminate and
	 * awaitTermination by default. Called upon stop with disposeOnTermination
	 * set to true, or by an external class (which does not necessarily enforce
	 * disposeOnTermination and thus the clean-up of other resources).
	 *
	 * @throws InterruptedException
	 *             when interrupted while waiting for the agent to stop
	 */
	public void dispose() throws InterruptedException {
		terminate();
		awaitTermination(AbstractRun.TIMEOUT_FIRST_AGENT_SECONDS);
	}

	/**
	 * Runs the agent by submitting tasks to the thread pool. Subclasses should
	 * implement their agents logic here. This logic should always check
	 * isRunning, and stop the logic when this becomes false, after which it can
	 * clean-up itself and should call setTerminated when it is really done.
	 * Care should be taken with regards to thread-safety and responsiveness
	 * (e.g. don't ignore interrupted exceptions).
	 *
	 * @param pool
	 *            The available thread pool to submit tasks to
	 * @param in
	 *            The task to run (initially null; the implementation should
	 *            create the first task in this case)
	 *
	 * @return A runnable that implements all of the features mentioned in the
	 *         description.
	 */
	protected abstract Runnable getRunnable(final Executor pool,
			final Callable<Callable<?>> in);
}
