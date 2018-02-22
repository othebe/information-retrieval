package core.parser.tokenizer;

import java.util.List;

public interface ITokenizer<I, O> {
    List<O> tokenize(I in);
}
