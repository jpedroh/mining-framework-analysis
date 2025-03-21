package net.javacrumbs.json2xml;

/**
 * Callback for name conversion. Can be used to convert names, that would be invalid as XML element values.
 */
public interface ElementNameConverter {
  /**
     * Converts JSON object name to XML element name.
     * @param name
     * @return
     */
  public String convertName(String name);
}