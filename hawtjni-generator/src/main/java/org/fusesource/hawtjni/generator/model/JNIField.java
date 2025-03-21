package org.fusesource.hawtjni.generator.model;
import org.fusesource.hawtjni.runtime.FieldFlag;

/**
 * 
 * @author <a href="http://hiramchirino.com">Hiram Chirino</a>
 */
public interface JNIField {
  public boolean getFlag(FieldFlag flag);

  public String getName();

  public int getModifiers();

  public JNIType getType();

  public JNIType getType64();

  public JNIClass getDeclaringClass();

  public JNIFieldAccessor getAccessor();

  public String getCast();

  public String getConditional();

  public boolean ignore();

  public boolean isSharedPointer();

  public boolean isPointer();
}