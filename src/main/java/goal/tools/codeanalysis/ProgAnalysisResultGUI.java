/**
 * GOAL interpreter that facilitates developing and executing GOAL multi-agent
 * programs. Copyright (C) 2011 K.V. Hindriks, W. Pasman
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */

package goal.tools.codeanalysis;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Shows results of GOAL program analysis in window.
 *
 * @author W.Pasman
 */
public class ProgAnalysisResultGUI implements ChangeListener {
	private final ProgramAnalysis codeAnalysis;
	/**
	 * If verbose is true, then the item labels are also printed. See
	 * {@link CodeAnalysisOverview#getText(boolean, boolean) }
	 */
	private final JCheckBox verboseCheckBox = new JCheckBox("verbose", true);
	/**
	 * If brief is true, then the statistics items are not printed. See
	 * {@link CodeAnalysisOverview#getText(boolean, boolean) }
	 */
	private final JCheckBox briefOverviewCheckBox = new JCheckBox(
			"brief overview", true);
	private final JCheckBox showPredicatesOverviewCheckBox = new JCheckBox(
			"show predicate info", true);
	/**
	 * The overview of the program that is shown.
	 */
	private final CodeAnalysisOverview programOverview;
	/**
	 * The overview of the predicates that is shown.
	 */
	private final CodeAnalysisOverview predicateOverview;
	/**
	 * Text area in the GUI showing the overview results.
	 */
	private final JTextArea textArea = new JTextArea("Performing analysis...",
			40, 40);

	/**
	 * Displays the results of the analysis to the user.
	 *
	 * @param parent
	 *            the parent component (used e.g. for centering of the info)
	 * @param analysis
	 *            is a ProgAnalysis that was made of the program
	 */
	public ProgAnalysisResultGUI(Component parent, ProgramAnalysis analysis) {
		if (analysis == null) {
			throw new NullPointerException("analysis is null");
		}
		codeAnalysis = analysis;
		programOverview = codeAnalysis.getProgramCodeAnalysis();
		predicateOverview = codeAnalysis.getPredicateCodeAnalysis();

		JScrollPane scrolltext = new JScrollPane(textArea);
		scrolltext
		.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

		JPanel settingspanel = new JPanel();
		settingspanel.setLayout(new FlowLayout());
		settingspanel.add(verboseCheckBox, FlowLayout.LEFT);
		settingspanel.add(showPredicatesOverviewCheckBox, FlowLayout.LEFT);
		settingspanel.add(briefOverviewCheckBox, FlowLayout.LEFT);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(scrolltext, BorderLayout.CENTER);
		panel.add(settingspanel, BorderLayout.NORTH);

		verboseCheckBox.addChangeListener(this);
		briefOverviewCheckBox.addChangeListener(this);
		showPredicatesOverviewCheckBox.addChangeListener(this);

		updateText();

		JOptionPane pane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE);
		JDialog dialog = pane.createDialog(
				parent,
				"Code analysis of the GOAL agent file "
						+ codeAnalysis.getFileName());

		dialog.setResizable(true);
		dialog.setVisible(true);
	}

	/**
	 * Called whenever the user changes any of the check boxes.
	 */
	@Override
	public void stateChanged(ChangeEvent changeEvent) {
		AbstractButton abstractButton = (AbstractButton) changeEvent
				.getSource();
		boolean pressed = abstractButton.getModel().isPressed();

		if (pressed) {
			updateText();
		}
	}

	public void updateText() {
		/**
		 * for some reasons this is called more than expected. this results in
		 * unexpected jumping of the scroll bar. Therefore we check if the
		 * verbose flag was really changed.
		 */
		// if (verboseCheckBox.isSelected() == verbose
		// && showPredicatesOverviewCheckBox.isSelected() == showPredicates)
		// return;

		boolean brief = briefOverviewCheckBox.isSelected();
		boolean showPredicates = showPredicatesOverviewCheckBox.isSelected();
		boolean verbose = verboseCheckBox.isSelected();

		String text = programOverview.getText(brief, verbose);

		if (showPredicates) {
			text = text + predicateOverview.getText(brief, verbose);
		}

		textArea.setText(text);
	}
}