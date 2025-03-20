package org.apache.bcel.generic;
import java.util.ArrayList;
import java.util.List;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.AccessFlags;
import org.apache.bcel.classfile.Attribute;

/**
 * Super class for FieldGen and MethodGen objects, since they have
 * some methods in common!
 *
 */
public abstract class FieldGenOrMethodGen extends AccessFlags implements NamedAndTyped, Cloneable {
  /**
     * @deprecated (since 6.0) will be made private; do not access directly, use getter/setter
     */
  @Deprecated protected String name;

  /**
     * @deprecated (since 6.0) will be made private; do not access directly, use getter/setter
     */
  @Deprecated protected Type type;

  /**
     * @deprecated (since 6.0) will be made private; do not access directly, use getter/setter
     */
  @Deprecated protected ConstantPoolGen cp;

  private final List<Attribute> attributeList = new ArrayList<>();

  private final List<AnnotationEntryGen> annotationList = new ArrayList<>();

  protected FieldGenOrMethodGen() {
  }

  /**
     * @since 6.0
     */
  protected FieldGenOrMethodGen(final int access_flags) {
    super(access_flags);
  }

  @Override public void setType(final Type type) {
    if (type.getType() == Const.T_ADDRESS) {
      throw new IllegalArgumentException("Type can not be " + type);
    }
    this.type = type;
  }

  @Override public Type getType() {
    return type;
  }

  /** @return name of method/field.
     */
  @Override public String getName() {
    return name;
  }

  @Override public void setName(final String name) {
    this.name = name;
  }

  public ConstantPoolGen getConstantPool() {
    return cp;
  }

  public void setConstantPool(final ConstantPoolGen cp) {
    this.cp = cp;
  }

  /**
     * Add an attribute to this method. Currently, the JVM knows about
     * the `Code', `ConstantValue', `Synthetic' and `Exceptions'
     * attributes. Other attributes will be ignored by the JVM but do no
     * harm.
     *
     * @param a attribute to be added
     */
  public void addAttribute(final Attribute a) {
    attributeList.add(a);
  }

  /**
     * @since 6.0
     */
  public void addAnnotationEntry(final AnnotationEntryGen ag) {
    annotationList.add(ag);
  }

  /**
     * Remove an attribute.
     */
  public void removeAttribute(final Attribute a) {
    attributeList.remove(a);
  }

  /**
     * @since 6.0
     */
  public void removeAnnotationEntry(final AnnotationEntryGen ag) {
    annotationList.remove(ag);
  }

  /**
     * Remove all attributes.
     */
  public void removeAttributes() {
    attributeList.clear();
  }

  /**
     * @since 6.0
     */
  public void removeAnnotationEntries() {
    annotationList.clear();
  }

  /**
     * @return all attributes of this method.
     */
  public Attribute[] getAttributes() {
    final Attribute[] attributes = new Attribute[attributeList.size()];
    attributeList.toArray(attributes);
    return attributes;
  }

  public AnnotationEntryGen[] getAnnotationEntries() {
    final AnnotationEntryGen[] annotations = new AnnotationEntryGen[annotationList.size()];
    annotationList.toArray(annotations);
    return annotations;
  }

  /** @return signature of method/field.
     */
  public abstract String getSignature();

  @Override public Object clone() {
    try {
      return super.clone();
    } catch (final CloneNotSupportedException e) {
      throw new Error("Clone Not Supported");
    }
  }
}