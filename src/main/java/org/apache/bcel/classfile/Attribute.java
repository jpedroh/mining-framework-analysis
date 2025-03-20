package org.apache.bcel.classfile;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.bcel.Const;

/**
 * Abstract super class for <em>Attribute</em> objects. Currently the
 * <em>ConstantValue</em>, <em>SourceFile</em>, <em>Code</em>,
 * <em>Exceptiontable</em>, <em>LineNumberTable</em>,
 * <em>LocalVariableTable</em>, <em>InnerClasses</em> and
 * <em>Synthetic</em> attributes are supported. The <em>Unknown</em>
 * attribute stands for non-standard-attributes.
 *
 * @see ConstantValue
 * @see SourceFile
 * @see Code
 * @see Unknown
 * @see ExceptionTable
 * @see LineNumberTable
 * @see LocalVariableTable
 * @see InnerClasses
 * @see Synthetic
 * @see Deprecated
 * @see Signature
 */
public abstract class Attribute implements Cloneable, Node {
  private static final boolean debug = Boolean.getBoolean(Attribute.class.getCanonicalName() + ".debug");

  private static final Map<String, Object> readers = new HashMap<>();

  /**
     * Empty array.
     *
     * @since 6.6.0
     */
  public static final Attribute[] EMPTY_ATTRIBUTE_ARRAY = new Attribute[0];

  /**
     * Add an Attribute reader capable of parsing (user-defined) attributes
     * named "name". You should not add readers for the standard attributes such
     * as "LineNumberTable", because those are handled internally.
     *
     * @param name the name of the attribute as stored in the class file
     * @param r    the reader object
     * @deprecated (6.0) Use {@link #addAttributeReader(String, UnknownAttributeReader)} instead
     */
  @java.lang.Deprecated public static void addAttributeReader(final String name, final AttributeReader r) {
    readers.put(name, r);
  }

  /**
     * Add an Attribute reader capable of parsing (user-defined) attributes
     * named "name". You should not add readers for the standard attributes such
     * as "LineNumberTable", because those are handled internally.
     *
     * @param name the name of the attribute as stored in the class file
     * @param r    the reader object
     */
  public static void addAttributeReader(final String name, final UnknownAttributeReader r) {
    readers.put(name, r);
  }

  protected static void println(final String msg) {
    if (debug) {
      System.err.println(msg);
    }
  }

  /**
     * Class method reads one attribute from the input data stream. This method
     * must not be accessible from the outside. It is called by the Field and
     * Method constructor methods.
     *
     * @see Field
     * @see Method
     *
     * @param file Input stream
     * @param constant_pool Array of constants
     * @return Attribute
     * @throws IOException
     * @throws ClassFormatException
     * @since 6.0
     */
  public static Attribute readAttribute(final DataInput file, final ConstantPool constant_pool) throws IOException, ClassFormatException {
    return readAttribute(file, constant_pool, false);
  }

  /**
     * Class method reads one attribute from the input data stream. This method
     * must not be accessible from the outside. It is called by the Field and
     * Method constructor methods.
     *
     * @see Field
     * @see Method
     *
     * @param file Input stream
     * @param constant_pool Array of constants
     * @param isOak If the the class file is oak
     * @return Attribute
     * @throws IOException
     * @throws ClassFormatException
     * @since 6.0
     */
  public static Attribute readAttribute(final DataInput file, final ConstantPool constant_pool, final boolean isOak) throws IOException, ClassFormatException {
    byte tag = Const.ATTR_UNKNOWN;
    final int name_index = file.readUnsignedShort();
    final ConstantUtf8 c = (ConstantUtf8) constant_pool.getConstant(name_index, Const.CONSTANT_Utf8);
    final String name = c.getBytes();
    final int length = file.readInt();
    for (byte i = 0; i < Const.KNOWN_ATTRIBUTES; i++) {
      if (name.equals(Const.getAttributeName(i))) {
        tag = i;
        break;
      }
    }
    switch (tag) {
      case Const.ATTR_UNKNOWN:
      final Object r = readers.get(name);
      if (r instanceof UnknownAttributeReader) {
        return ((UnknownAttributeReader) r).createAttribute(name_index, length, file, constant_pool);
      }
      return new Unknown(name_index, length, file, constant_pool);
      case Const.ATTR_CONSTANT_VALUE:
      return new ConstantValue(name_index, length, file, constant_pool);
      case Const.ATTR_SOURCE_FILE:
      return new SourceFile(name_index, length, file, constant_pool);
      case Const.ATTR_CODE:
      return new Code(name_index, length, file, constant_pool, isOak);
      case Const.ATTR_EXCEPTIONS:
      return new ExceptionTable(name_index, length, file, constant_pool);
      case Const.ATTR_LINE_NUMBER_TABLE:
      return new LineNumberTable(name_index, length, file, constant_pool);
      case Const.ATTR_LOCAL_VARIABLE_TABLE:
      return new LocalVariableTable(name_index, length, file, constant_pool);
      case Const.ATTR_INNER_CLASSES:
      return new InnerClasses(name_index, length, file, constant_pool);
      case Const.ATTR_SYNTHETIC:
      return new Synthetic(name_index, length, file, constant_pool);
      case Const.ATTR_DEPRECATED:
      return new Deprecated(name_index, length, file, constant_pool);
      case Const.ATTR_PMG:
      return new PMGClass(name_index, length, file, constant_pool);
      case Const.ATTR_SIGNATURE:
      return new Signature(name_index, length, file, constant_pool);
      case Const.ATTR_STACK_MAP:
      println("Warning: Obsolete StackMap attribute ignored.");
      return new Unknown(name_index, length, file, constant_pool);
      case Const.ATTR_RUNTIME_VISIBLE_ANNOTATIONS:
      return new RuntimeVisibleAnnotations(name_index, length, file, constant_pool);
      case Const.ATTR_RUNTIME_INVISIBLE_ANNOTATIONS:
      return new RuntimeInvisibleAnnotations(name_index, length, file, constant_pool);
      case Const.ATTR_RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS:
      return new RuntimeVisibleParameterAnnotations(name_index, length, file, constant_pool);
      case Const.ATTR_RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS:
      return new RuntimeInvisibleParameterAnnotations(name_index, length, file, constant_pool);
      case Const.ATTR_ANNOTATION_DEFAULT:
      return new AnnotationDefault(name_index, length, file, constant_pool);
      case Const.ATTR_LOCAL_VARIABLE_TYPE_TABLE:
      return new LocalVariableTypeTable(name_index, length, file, constant_pool);
      case Const.ATTR_ENCLOSING_METHOD:
      return new EnclosingMethod(name_index, length, file, constant_pool);
      case Const.ATTR_STACK_MAP_TABLE:
      return new StackMap(name_index, length, file, constant_pool);
      case Const.ATTR_BOOTSTRAP_METHODS:
      return new BootstrapMethods(name_index, length, file, constant_pool);
      case Const.ATTR_METHOD_PARAMETERS:
      return new MethodParameters(name_index, length, file, constant_pool);
      case Const.ATTR_MODULE:
      return new Module(name_index, length, file, constant_pool);
      case Const.ATTR_MODULE_PACKAGES:
      return new ModulePackages(name_index, length, file, constant_pool);
      case Const.ATTR_MODULE_MAIN_CLASS:
      return new ModuleMainClass(name_index, length, file, constant_pool);
      case Const.ATTR_NEST_HOST:
      return new NestHost(name_index, length, file, constant_pool);
      case Const.ATTR_NEST_MEMBERS:
      return new NestMembers(name_index, length, file, constant_pool);
      default:
      throw new IllegalStateException("Unrecognized attribute type tag parsed: " + tag);
    }
  }

  /**
     * Class method reads one attribute from the input data stream. This method
     * must not be accessible from the outside. It is called by the Field and
     * Method constructor methods.
     *
     * @see Field
     * @see Method
     *
     * @param file Input stream
     * @param constant_pool Array of constants
     * @return Attribute
     * @throws IOException
     * @throws ClassFormatException
     */
  public static Attribute readAttribute(final DataInputStream file, final ConstantPool constant_pool) throws IOException, ClassFormatException {
    return readAttribute((DataInput) file, constant_pool);
  }

  /**
     * Remove attribute reader
     *
     * @param name the name of the attribute as stored in the class file
     */
  public static void removeAttributeReader(final String name) {
    readers.remove(name);
  }

  /**
     * @deprecated (since 6.0) will be made private; do not access directly, use getter/setter
     */
  @java.lang.Deprecated protected int name_index;

  /**
     * @deprecated (since 6.0) (since 6.0) will be made private; do not access directly, use getter/setter
     */
  @java.lang.Deprecated protected int length;

  /**
     * @deprecated (since 6.0) will be made private; do not access directly, use getter/setter
     */
  @java.lang.Deprecated protected byte tag;

  /**
     * @deprecated (since 6.0) will be made private; do not access directly, use getter/setter
     */
  @java.lang.Deprecated protected ConstantPool constant_pool;

  protected Attribute(final byte tag, final int name_index, final int length, final ConstantPool constant_pool) {
    this.tag = tag;
    this.name_index = name_index;
    this.length = length;
    this.constant_pool = constant_pool;
  }

  /**
     * Called by objects that are traversing the nodes of the tree implicitely
     * defined by the contents of a Java class. I.e., the hierarchy of methods,
     * fields, attributes, etc. spawns a tree of objects.
     *
     * @param v
     *            Visitor object
     */
  @Override public abstract void accept(Visitor v);

  /**
     * Use copy() if you want to have a deep copy(), i.e., with all references
     * copied correctly.
     *
     * @return shallow copy of this attribute
     */
  @Override public Object clone() {
    Attribute attr = null;
    try {
      attr = (Attribute) super.clone();
    } catch (final CloneNotSupportedException e) {
      throw new Error("Clone Not Supported");
    }
    return attr;
  }

  /**
     * @return deep copy of this attribute
     */
  public abstract Attribute copy(ConstantPool _constant_pool);

  /**
     * Dump attribute to file stream in binary format.
     *
     * @param file
     *            Output file stream
     * @throws IOException
     */
  public void dump(final DataOutputStream file) throws IOException {
    file.writeShort(name_index);
    file.writeInt(length);
  }

  /**
     * @return Constant pool used by this object.
     * @see ConstantPool
     */
  public final ConstantPool getConstantPool() {
    return constant_pool;
  }

  /**
     * @return Length of attribute field in bytes.
     */
  public final int getLength() {
    return length;
  }

  /**
     * @return Name of attribute
     * @since 6.0
     */
  public String getName() {
    final ConstantUtf8 c = (ConstantUtf8) constant_pool.getConstant(name_index, Const.CONSTANT_Utf8);
    return c.getBytes();
  }

  /**
     * @return Name index in constant pool of attribute name.
     */
  public final int getNameIndex() {
    return name_index;
  }

  /**
     * @return Tag of attribute, i.e., its type. Value may not be altered, thus there is no setTag() method.
     */
  public final byte getTag() {
    return tag;
  }

  /**
     * @param constant_pool Constant pool to be used for this object.
     * @see ConstantPool
     */
  public final void setConstantPool(final ConstantPool constant_pool) {
    this.constant_pool = constant_pool;
  }

  /**
     * @param length length in bytes.
     */
  public final void setLength(final int length) {
    this.length = length;
  }

  /**
     * @param name_index of attribute.
     */
  public final void setNameIndex(final int name_index) {
    this.name_index = name_index;
  }

  /**
     * @return attribute name.
     */
  @Override public String toString() {
    return Const.getAttributeName(tag);
  }
}