package org.simmetrics.tokenizers;
import java.util.HashSet;
import java.util.Set;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

/**
 * Convenience tokenizer. Provides default implementation to tokenize to set and
 * multiset that calls {@link Tokenizer#tokenizeToList(String)}.
 */
public abstract class AbstractTokenizer implements Tokenizer {
  @Override public Set<String> tokenizeToSet(final String input) {
    return new HashSet<>(tokenizeToList(input));
  }

  @Override public Multiset<String> tokenizeToMultiset(final String input) {
    return HashMultiset.create(tokenizeToList(input));
  }
}