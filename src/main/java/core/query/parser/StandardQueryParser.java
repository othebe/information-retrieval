package core.query.parser;

import core.parser.Parser;
import core.parser.processor.*;
import core.parser.processor.string.*;
import core.query.Query;
import core.query.node.And;
import core.query.node.Leaf;
import core.query.node.Or;
import core.query.node.Phrase;

import java.util.regex.Pattern;

import static core.query.node.And.TOKEN_AND;
import static core.query.node.Or.TOKEN_OR;
import static core.query.node.Phrase.TOKEN_PHRASE;

public class StandardQueryParser extends Parser<String, Query> {
    private static final String[] ALLOWED_TOKENS = {
            Pattern.quote(TOKEN_AND.toString()),
            Pattern.quote(TOKEN_OR.toString()),
            Pattern.quote(TOKEN_PHRASE.toString())
    };

    public StandardQueryParser() {
        super(
                new IProcessor[] { new PunctuationProcessor(ALLOWED_TOKENS), new Lowercaser() },
                new StandardQueryTokenizer(),
                new IProcessor[] { new PostProcessor() }
        );
    }


    private static class PostProcessor implements IProcessor<Query> {
        IProcessor[] processors = { new DigitFilter(), new StopWordFilter(), new Stemmer() };

        @Override
        public Query process(Query in) {
            if (in instanceof Leaf) {
                String term = in.getTerm();
                for (IProcessor<String> postProcessor : processors) {
                    term = postProcessor.process(term);
                }

                return new Leaf(term);
            }

            if (in instanceof And) {
                return new And(process(in.getLeft()), process(in.getRight()));
            }

            if (in instanceof Or) {
                return new Or(process(in.getLeft()), process(in.getRight()));
            }

            if (in instanceof Phrase) {
                return new Phrase(process(in.getLeft()), process(in.getRight()));
            }

            return in;
        }
    }
}
