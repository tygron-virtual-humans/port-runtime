package goal.core.runtime.events;

import java.io.Serializable;

/**
 * Events that can be sent to a RuntimeServiceManager
 *
 * @author wouter
 */
public interface RemoteRuntimeListener extends Serializable {
	public void remoteRuntimeEventOccured(RemoteRuntimeEvent event);
}
