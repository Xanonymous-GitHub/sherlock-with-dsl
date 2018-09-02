package uk.ac.warwick.dcs.sherlock.services.fileSystem.filters;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import uk.ac.warwick.dcs.sherlock.services.fileSystem.filters.DirectoryFilter;
class DirectoryFilterTest {
	@Rule
	public TemporaryFolder tempFolder  = new TemporaryFolder();
	@Test
	void acceptDirectory() throws Exception {
		DirectoryFilter filter = new DirectoryFilter();
		Path folder = Files.createTempDirectory("Fake");
		File fake = new File(folder.toString());
		assertTrue(filter.accept(fake));
	}
	@Test
	void rejectNonDirectory() throws Exception {
		DirectoryFilter filter = new DirectoryFilter();
		Path folder = Files.createTempFile("Fake", ".txt");
		File fake = new File(folder.toString());
		assertFalse(filter.accept(fake));
	}
}
