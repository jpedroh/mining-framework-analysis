package org.rdfhdt.hdt.dictionary.impl;
import java.util.Iterator;
import org.rdfhdt.hdt.dictionary.TempDictionary;
import org.rdfhdt.hdt.dictionary.TempDictionarySection;
import org.rdfhdt.hdt.enums.DictionarySectionRole;
import org.rdfhdt.hdt.enums.TripleComponentRole;
import org.rdfhdt.hdt.exceptions.NotImplementedException;
import org.rdfhdt.hdt.options.HDTOptions;
import org.rdfhdt.hdt.triples.TempTriples;

/**
 * This abstract class implements all methods that have implementation
 * common to all modifiable dictionaries (or could apply to)
 * 
 * @author Eugen
 *
 */
public abstract class BaseTempDictionary implements TempDictionary {
  HDTOptions spec;

  protected boolean isOrganized;

  protected TempDictionarySection subjects;

  protected TempDictionarySection predicates;

  protected TempDictionarySection objects;

  protected TempDictionarySection shared;

  public BaseTempDictionary(HDTOptions spec) {
    this.spec = spec;
  }

  @Override public int insert(CharSequence str, TripleComponentRole position) {
    switch (position) {
      case SUBJECT:
      isOrganized = false;
      return ((TempDictionarySection) subjects).add(str);
      case PREDICATE:
      isOrganized = false;
      return ((TempDictionarySection) predicates).add(str);
      case OBJECT:
      isOrganized = false;
      return ((TempDictionarySection) objects).add(str);
      default:
      throw new IllegalArgumentException();
    }
  }

  @Override public void reorganize() {
    Iterator<? extends CharSequence> itSubj = ((TempDictionarySection) subjects).getEntries();
    while (itSubj.hasNext()) {
      CharSequence str = itSubj.next();
      if (str.length() > 0 && str.charAt(0) != '\"' && objects.locate(str) != 0) {
        ((TempDictionarySection) shared).add(str);
      }
    }
    Iterator<? extends CharSequence> itShared = ((TempDictionarySection) shared).getEntries();
    while (itShared.hasNext()) {
      CharSequence sharedStr = itShared.next();
      ((TempDictionarySection) subjects).remove(sharedStr);
      ((TempDictionarySection) objects).remove(sharedStr);
    }
    ((TempDictionarySection) shared).sort();
    ((TempDictionarySection) subjects).sort();
    ((TempDictionarySection) objects).sort();
    ((TempDictionarySection) predicates).sort();
    isOrganized = true;
  }

  /**
	 * This method is used in the one-pass way of working in which case it
	 * should not be used with a disk-backed dictionary because remapping
	 * requires practically a copy of the dictionary which is very bad...
	 * (it is ok for in-memory and they should override and write implementation)
	 */
  @Override public void reorganize(TempTriples triples) {
    throw new NotImplementedException();
  }

  @Override public boolean isOrganized() {
    return isOrganized;
  }

  @Override public void clear() {
    subjects.clear();
    predicates.clear();
    shared.clear();
    objects.clear();
  }

  @Override public TempDictionarySection getSubjects() {
    return subjects;
  }

  @Override public TempDictionarySection getPredicates() {
    return predicates;
  }

  @Override public TempDictionarySection getObjects() {
    return objects;
  }

  @Override public TempDictionarySection getShared() {
    return shared;
  }

  protected int getGlobalId(int id, DictionarySectionRole position) {
    switch (position) {
      case SUBJECT:
      case OBJECT:
      return shared.getNumberOfElements() + id;
      case PREDICATE:
      case SHARED:
      return id;
      default:
      throw new IllegalArgumentException();
    }
  }

  @Override public int stringToId(CharSequence str, TripleComponentRole position) {
    if (str == null || str.length() == 0) {
      return 0;
    }
    int ret = 0;
    switch (position) {
      case SUBJECT:
      ret = shared.locate(str);
      if (ret != 0) {
        return getGlobalId(ret, DictionarySectionRole.SHARED);
      }
      ret = subjects.locate(str);
      if (ret != 0) {
        return getGlobalId(ret, DictionarySectionRole.SUBJECT);
      }
      return -1;
      case PREDICATE:
      ret = predicates.locate(str);
      if (ret != 0) {
        return getGlobalId(ret, DictionarySectionRole.PREDICATE);
      }
      return -1;
      case OBJECT:
      ret = shared.locate(str);
      if (ret != 0) {
        return getGlobalId(ret, DictionarySectionRole.SHARED);
      }
      ret = objects.locate(str);
      if (ret != 0) {
        return getGlobalId(ret, DictionarySectionRole.OBJECT);
      }
      return -1;
      default:
      throw new IllegalArgumentException();
    }
  }
}