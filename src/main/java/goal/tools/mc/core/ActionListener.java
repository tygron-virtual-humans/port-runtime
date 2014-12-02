package goal.tools.mc.core;

import java.awt.event.ActionEvent;

/**
 * Provides listener mechanism. Introduced to separate the GUI from the core.
 *
 * @author W.Pasman #2873 27may14
 *
 */
public interface ActionListener {

	/**
	 * called when the {@link Controller} completed an action
	 *
	 * @param actionEvent
	 */
	void actionPerformed(ActionEvent actionEvent);

}
