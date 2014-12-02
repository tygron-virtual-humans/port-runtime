package goal.core.agent;

import goal.core.program.Message;

import java.util.HashSet;
import java.util.Set;

/**
 * NOP implementation for messaging. No messages will be received, all send
 * messages disappear.
 *
 * @author M.P. Korstanje
 */
public class NoMessagingCapabilities implements MessagingCapabilities {

	@Override
	public void reset() {
		// Does nothing.
	}

	@Override
	public Set<Message> getAllMessages() {
		return new HashSet<>(0);
	}

	@Override
	public void postMessage(Message message) {
		// Does nothing.
	}

	@Override
	public void dispose() {
		// Does nothing.
	}
}
