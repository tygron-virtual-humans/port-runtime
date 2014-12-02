package goal.parser.goal;

import goal.core.kr.KRFactory;
import goal.core.program.GOALProgram;
import goal.core.program.validation.agentfile.GOALProgramValidator;

import java.io.File;

import javax.swing.JFileChooser;

public class GOALWalkerTest {

	public static void main(String[] args) {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setMultiSelectionEnabled(false);
		int val = fc.showDialog(null, "Selecteer goal of map");
		if (val == JFileChooser.APPROVE_OPTION) {
			recursion(fc.getSelectedFile());
		}
	}

	private static void recursion(File file) {
		final String name = file.getName().toLowerCase();
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				recursion(child);
			}
		} else if (name.endsWith("goal") || name.endsWith("mod2g")) {
			GOALWalker visitor = null;
			GOALProgramValidator validator = null;
			try {
				System.out.println("\n" + file.getPath() + ":");
				visitor = GOALWalker.getWalker(file,
						KRFactory.getDefaultLanguage());
				GOALProgram program = visitor.getProgram();
				validator = new GOALProgramValidator();
				validator.validate(program.getModule(), name.endsWith("mod2g"));
			} catch (Exception e) {
				e.printStackTrace(System.out);
			} finally {
				if (visitor != null) {
					System.out.println("errors: " + visitor.getErrors());
					System.out.println("warnings: " + visitor.getWarnings());
				}
				if (validator != null) {
					System.out.println("validator: "
							+ validator.getAllReports());
				}
			}
		}
	}
}