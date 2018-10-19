package model.base.preprocessing;

import model.base.utils.PreprocessorParamsProvider;
import model.base.utils.TestJavaFile;
import model.base.utils.TestUtils;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Token;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import uk.ac.warwick.dcs.sherlock.api.util.IndexedString;
import uk.ac.warwick.dcs.sherlock.model.base.lang.JavaLexer;
import uk.ac.warwick.dcs.sherlock.model.base.preprocessing.StandardStringifier;

import java.io.File;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StandardStringifierTest {

	@ParameterizedTest
	@ArgumentsSource (PreprocessorParamsProvider.class)
	void processTokens(TestJavaFile testJavaFile) throws Exception {
		File file = TestUtils.makeFileWithContents("temp.java", testJavaFile.Original());
		Class<? extends Lexer> lexerClass = JavaLexer.class;
		Lexer lexer = lexerClass.getDeclaredConstructor(CharStream.class).newInstance(CharStreams.fromFileName(file.getName()));
		List<? extends Token> tokensMaster = lexer.getAllTokens();
		List<IndexedString> indexedStrings = (new StandardStringifier()).processTokens(tokensMaster, lexer.getVocabulary());
		Iterator<IndexedString> i = indexedStrings.iterator();
		List<String> ls = new ArrayList<>();
		while (i.hasNext()) {
			ls.add(i.next().getValue());
		}
		FileUtils.forceDelete(file);
		assertEquals(testJavaFile.StandardStringifier(), ls);
	}

	@ParameterizedTest
	@ArgumentsSource (PreprocessorParamsProvider.class)
	void processUnit(TestJavaFile testJavaFile) throws Exception {
		List<? extends Token> tokensMaster = testJavaFile.getTokens();
		List<IndexedString> indexedStrings = (new StandardStringifier()).processTokens(tokensMaster, null);
		Iterator<IndexedString> i = indexedStrings.iterator();
		List<String> ls = new ArrayList<>();
		while (i.hasNext()) {
			ls.add(i.next().getValue());
		}
		assertEquals(testJavaFile.StandardStringifier(), ls);
	}
}