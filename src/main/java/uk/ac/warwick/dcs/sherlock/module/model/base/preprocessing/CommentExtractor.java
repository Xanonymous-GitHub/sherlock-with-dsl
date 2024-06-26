package uk.ac.warwick.dcs.sherlock.module.model.base.preprocessing;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.IGeneralPreProcessor;
import uk.ac.warwick.dcs.sherlock.api.model.preprocessing.ILexerSpecification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CommentExtractor implements IGeneralPreProcessor {

    @Override
    public ILexerSpecification getLexerSpecification() {
        return new StandardLexerSpecification();
    }

    /**
     * Extracts the comments from a source file
     *
     * @param tokens list of tokens to process
     * @param vocab  Lexer vocabulary
     * @param lang   language of source file being processed
     * @return stream of tokens containing comments
     */
    @Override
    public List<? extends Token> process(List<? extends Token> tokens, Vocabulary vocab, String lang) {
        //Parallel version: return tokens.parallelStream().filter(t -> StandardLexerSpecification.channels.values()[t.getChannel()] == StandardLexerSpecification.channels.COMMENT).collect(Collectors.toList());

        List<Token> result = new ArrayList<>();

        for (Token t : tokens) {

            if (Objects.requireNonNull(StandardLexerSpecification.channels.values()[t.getChannel()]) == StandardLexerSpecification.channels.COMMENT) {
                result.add(t);
            }
        }

        return result;
    }

}
