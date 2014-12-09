package goal.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import goal.util.Extension;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import krTools.errors.exceptions.ParserException;
import languageTools.program.mas.MASProgram;

import org.junit.Test;

public class PlatformManagerTest {
	@Test
	public void testLoadMASFiles_File() throws ParserException {
		File file = new File("src/test/resources/goal/tools/testselect.mas2g");
		MASProgram mas = PlatformManager.createNew().parseMASFile(file);

		assertTrue(mas != null);
		assertEquals(file, mas.getSourceFile());
	}

	@Test
	public void testLoadMASFiles_Directory() throws ParserException {
		File folder = new File("src/test/resources/goal/tools/");
		List<MASProgram> masFileRegistries = new LinkedList<MASProgram>();
		for (final File mas2g : PlatformManager.getMASFiles(folder, false)) {
			final MASProgram mas = PlatformManager.getCurrent().parseMASFile(
					mas2g);
			if (mas != null) {
				masFileRegistries.add(mas);
			}
		}

		assertTrue(masFileRegistries.size() == 3);
	}

	@Test
	public void testCreateNewPL() throws IOException {
		File newFile = File.createTempFile("test", "pl");

		PlatformManager.createfile(newFile, Extension.PROLOG);
		assertTrue(newFile.exists());
	}

	@Test
	public void testCreateNewMAS() throws IOException {
		File newFile = File.createTempFile("test", "mas2g");

		PlatformManager.createfile(newFile, Extension.MAS);
		assertTrue(newFile.exists());
	}

	@Test
	public void testCreateNewGOAL() throws IOException {
		File newFile = File.createTempFile("test", "goal");

		PlatformManager.createfile(newFile, Extension.GOAL);
		assertTrue(newFile.exists());
	}

	@Test
	public void testCreateNewModule() throws IOException {
		File newFile = File.createTempFile("test", "mod2g");

		PlatformManager.createfile(newFile, Extension.MODULES);
		assertTrue(newFile.exists());
	}
}
