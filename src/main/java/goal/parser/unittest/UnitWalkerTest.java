package goal.parser.unittest;

import goal.parser.antlr.UnitTestLexer;
import goal.parser.antlr.UnitTestParser;

import java.io.File;

import javax.swing.JFileChooser;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class UnitWalkerTest {

	public static void main(String[] args) {
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setMultiSelectionEnabled(false);
		int val = fc.showDialog(null, "Selecteer test2g of map");
		if (val == JFileChooser.APPROVE_OPTION) {
			recursion(fc.getSelectedFile());
		}
	}

	private static void recursion(File file) {
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				recursion(child);
			}
		} else if (file.getName().toLowerCase().endsWith("test2g")) {
			UnitTestWalker interpreter = null;
			try {
				System.out.println("\n" + file.getPath() + ":");
				ANTLRFileStream stream = new ANTLRFileStream(file.getPath());
				UnitTestLexer lexer = new UnitTestLexer(stream);
				CommonTokenStream tokens = new CommonTokenStream(lexer);
				UnitTestParser parser = new UnitTestParser(tokens);
				interpreter = new UnitTestWalker(file);
				parser.removeErrorListeners();
				parser.addErrorListener(interpreter);
				interpreter.visitUnitTest(parser.unitTest());
			} catch (Exception e) {
				e.printStackTrace(System.out);
			} finally {
				if (interpreter != null) {
					System.out.println("errors: " + interpreter.getErrors());
					System.out
					.println("warnings: " + interpreter.getWarnings());
				}
			}
		}
	}
}