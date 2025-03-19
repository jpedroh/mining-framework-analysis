package org.rdfhdt.hdt.dictionary;
import org.rdfhdt.hdt.dictionary.impl.FourSectionDictionary;
import org.rdfhdt.hdt.dictionary.impl.FourSectionDictionaryBig;
import org.rdfhdt.hdt.dictionary.impl.HashDictionary;
import org.rdfhdt.hdt.exceptions.IllegalFormatException;
import org.rdfhdt.hdt.hdt.HDTFactory;
import org.rdfhdt.hdt.hdt.HDTVocabulary;
import org.rdfhdt.hdt.options.ControlInfo;
import org.rdfhdt.hdt.options.HDTOptions;
import org.rdfhdt.hdt.options.HDTSpecification;

/**
 * Factory that creates Dictionary objects
 * 
 */
public class DictionaryFactory {
  public static final String MOD_DICT_IMPL_HASH = "hash";

  public static final String DICTIONARY_TYPE_FOUR_SECTION_BIG = "dictionaryFourBig";

  private DictionaryFactory() {
  }

  /**
	 * Creates a default dictionary (HashDictionary)
	 * 
	 * @return Dictionary
	 */
  public static Dictionary createDefaultDictionary() throws IllegalArgumentException {
    return new FourSectionDictionary(new HDTSpecification());
  }

  /**
	 * Creates a default dictionary (HashDictionary)
	 * 
	 * @return Dictionary
	 */
  public static TempDictionary createTempDictionary(HDTOptions spec) {
    String dictImpl = spec.get("tempDictionary.impl");
    if (dictImpl == null || "".equals(dictImpl) || MOD_DICT_IMPL_HASH.equals(dictImpl)) {
      return new HashDictionary(spec);
    }
    return HDTFactory.getTempFactory().getDictionary(spec);
  }

  public static DictionaryPrivate createDictionary(HDTOptions spec) {
    String name = spec.get("dictionary.type");
    if (name == null || HDTVocabulary.DICTIONARY_TYPE_FOUR_SECTION.equals(name)) {
      return new FourSectionDictionary(spec);
    } else {
      if (DICTIONARY_TYPE_FOUR_SECTION_BIG.equals(name)) {
        return new FourSectionDictionaryBig(spec);
      }
    }
    throw new IllegalFormatException("Implementation of ditionary not found for " + name);
  }

  public static DictionaryPrivate createDictionary(ControlInfo ci) {
    String name = ci.getFormat();
    if (HDTVocabulary.DICTIONARY_TYPE_FOUR_SECTION.equals(name)) {
      return new FourSectionDictionary(new HDTSpecification());
    }
    throw new IllegalFormatException("Implementation of ditionary not found for " + name);
  }
}