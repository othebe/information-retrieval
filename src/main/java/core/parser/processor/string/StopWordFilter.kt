package core.parser.processor.string

import core.parser.processor.IProcessor

class StopWordFilter : IProcessor<String> {
    companion object {
        private val STOPWORDS = setOf(
                "a",
                "an",
                "and",
                "are",
                "as",
                "at",
                "be",
                "because",
                "but",
                "by",
                "could",
                "did",
                "do",
                "does",
                "done",
                "for",
                "had",
                "has",
                "have",
                "having",
                "he",
                "her",
                "here",
                "herself",
                "him",
                "himself",
                "his",
                "how",
                "however",
                "i",
                "if",
                "in",
                "into",
                "is",
                "it",
                "its",
                "itself",
                "me",
                "mr",
                "mrs",
                "my",
                "of",
                "or",
                "our",
                "she",
                "the",
                "their",
                "them",
                "then",
                "there",
                "therefore",
                "these",
                "they",
                "thing",
                "things",
                "this",
                "those",
                "though",
                "through",
                "thus",
                "to",
                "us",
                "was",
                "we",
                "were",
                "what",
                "when",
                "where",
                "whether",
                "which",
                "while",
                "who",
                "whose",
                "why",
                "will",
                "with",
                "would",
                "you",
                "your",
                "yours"
        )
    }

    override fun process(`in`: String): String {
        return if (`in`.length < 2 || STOPWORDS.contains(`in`)) "" else `in`
    }
}
