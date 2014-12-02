package goal.parser.mas;

import goal.core.mas.MASProgram;
import goal.core.program.validation.masfile.MASProgramValidator;

import java.io.File;

import javax.swing.JFileChooser;

public class MASWalkerTest {

	public static void main(String[] args) {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setMultiSelectionEnabled(false);
		int val = fc.showDialog(null, "Selecteer mas2g of map");
		if (val == JFileChooser.APPROVE_OPTION) {
			recursion(fc.getSelectedFile());
		}
	}

	private static void recursion(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				recursion(child);
			}
		} else if (file.getName().toLowerCase().endsWith("mas2g")) {
			MASWalker visitor = null;
			MASProgramValidator validator = null;
			try {
				System.out.println("\n" + file.getPath() + ":");
				visitor = MASWalker.getWalker(file);
				MASProgram program = visitor.getProgram();
				System.out.println("errors: " + visitor.getErrors());
				System.out.println("warnings: " + visitor.getWarnings());
				validator = new MASProgramValidator();
				validator.validate(program, false);
				System.out.println("validator: " + validator.getAllReports());
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