package org.simmetrics.simplifiers;

@SuppressWarnings(value = { "javadoc" }) @Deprecated public class MetaphoneTest extends SimplifierTest {
  @Override protected Simplifier getSimplifier() {
    return new Metaphone();
  }

  @Override protected T[] getTests() {
    return new T[] { new T("Tannhauser", "TNHS"), new T("James", "JMS"), new T("", ""), new T("Travis", "TRFS"), new T("Marcus", "MRKS"), new T("Ozymandias", "OSMN"), new T("Jones", "JNS"), new T("Jenkins", "JNKN"), new T("Trevor", "TRFR"), new T("Marinus", "MRNS") };
  }
}