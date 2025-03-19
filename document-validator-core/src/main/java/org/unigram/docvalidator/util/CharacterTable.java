package org.unigram.docvalidator.util;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Configuration table of characters used in DocumentValidator.
 */
public final class CharacterTable {
  /**
   * Constructor.
   */
  public CharacterTable() {
    super();
    characterDictionary = new HashMap<String, DVCharacter>();
  }

  /**
   * Return the size of character Dictionary.
   *
   * @return size of registered character
   */
  public int getSizeDictionarySize() {
    return this.characterDictionary.size();
  }

  /**
   * Get the character names in the dictionary.
   *
   * @return names of characters
   */
  public Set<String> getNames() {
    return this.characterDictionary.keySet();
  }

  /**
   * Get the character specified with the name.
   *
   * @param name character name
   * @return character containing the settings
   */
  public DVCharacter getCharacter(String name) {
    return this.characterDictionary.get(name);
  }

  /**
   * Get all elements of character dictionary.
   *
   * @return character dictionary
   */
  public Map<String, DVCharacter> getCharacterDictionary() {
    return characterDictionary;
  }

  /**
   * Detect the specified character is exit in the dictionary.
   *
   * @param name character name
   * @return character when exist, null when the specified character does not exist
   */
  public boolean isContainCharacter(String name) {
    return this.characterDictionary.get(name) != null;
  }

  private final Map<String, DVCharacter> characterDictionary;
}