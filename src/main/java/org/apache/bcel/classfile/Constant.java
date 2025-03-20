package org.apache.bcel.classfile;
import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.function.Function;
import org.apache.bcel.Const;
import org.apache.bcel.util.BCELComparator;

/**
 * Abstract superclass for classes to represent the different constant types
 * in the constant pool of a class file. The classes keep closely to
 * the JVM specification.
 */
public abstract class Constant implements Cloneable, Node {
  private static BCELComparator bcelComparator = new BCELComparator() {
    @Override public boolean equals(final Object o1, final Object o2) {
      final Constant THIS = (Constant) o1;
      final Constant THAT = (Constant) o2;
      return Objects.equals(THIS.toString(), THAT.toString());
    }

    @Override public int hashCode(final Object o) {
      final Constant THIS = (Constant) o;
      return THIS.toString().hashCode();
    }
  };

  private static final Function<String, ConstantUtf8> constantUtf8Generator = Integer.getInteger(ConstantUtf8.CONSTANT_UTF8_MAX_CACHED_SIZE_KEY, 0) > 0 ? ConstantUtf8::getCachedInstance : ConstantUtf8::getInstance;

  /**
     * @deprecated (since 6.0) will be made private; do not access directly, use getter/setter
     */
  @java.lang.Deprecated protected byte tag;

  Constant(final byte tag) {
    this.tag = tag;
  }

  /**
     * Called by objects that are traversing the nodes of the tree implicitely
     * defined by the contents of a Java class. I.e., the hierarchy of methods,
     * fields, attributes, etc. spawns a tree of objects.
     *
     * @param v Visitor object
     */
  @Override public abstract void accept(Visitor v);

  public abstract void dump(DataOutputStream file) throws IOException;

  /**
     * @return Tag of constant, i.e., its type. No setTag() method to avoid
     * confusion.
     */
  public final byte getTag() {
    return tag;
  }

  /**
     * @return String representation.
     */
  @Override public String toString() {
    return Const.getConstantName(tag) + "[" + tag + "]";
  }

  /**
     * @return deep copy of this constant
     */
  public Constant copy() {
    try {
      return (Constant) super.clone();
    } catch (final CloneNotSupportedException e) {
    }
    return null;
  }

  @Override public Object clone() {
    try {
      return super.clone();
    } catch (final CloneNotSupportedException e) {
      throw new Error("Clone Not Supported");
    }
  }

  /**
     * Reads one constant from the given input, the type depends on a tag byte.
     *
     * @param dataInput Input stream
     * @return Constant object
     * @throws IOException if an I/O error occurs reading from the given {@code dataInput}.
     * @throws ClassFormatException if the next byte is not recognized
     * @since 6.0 made public
     */
  public static Constant readConstant(final DataInput dataInput) throws IOException, ClassFormatException {
    final byte b = dataInput.readByte();
    switch (b) {
      case Const.CONSTANT_Class:
      return new ConstantClass(dataInput);
      case Const.CONSTANT_Fieldref:
      return new ConstantFieldref(dataInput);
      case Const.CONSTANT_Methodref:
      return new ConstantMethodref(dataInput);
      case Const.CONSTANT_InterfaceMethodref:
      return new ConstantInterfaceMethodref(dataInput);
      case Const.CONSTANT_String:
      return new ConstantString(dataInput);
      case Const.CONSTANT_Integer:
      return new ConstantInteger(dataInput);
      case Const.CONSTANT_Float:
      return new ConstantFloat(dataInput);
      case Const.CONSTANT_Long:
      return new ConstantLong(dataInput);
      case Const.CONSTANT_Double:
      return new ConstantDouble(dataInput);
      case Const.CONSTANT_NameAndType:
      return new ConstantNameAndType(dataInput);
      case Const.CONSTANT_Utf8:
      return constantUtf8Generator.apply(dataInput.readUTF());
      case Const.CONSTANT_MethodHandle:
      return new ConstantMethodHandle(dataInput);
      case Const.CONSTANT_MethodType:
      return new ConstantMethodType(dataInput);
      case Const.CONSTANT_Dynamic:
      return new ConstantDynamic(dataInput);
      case Const.CONSTANT_InvokeDynamic:
      return new ConstantInvokeDynamic(dataInput);
      case Const.CONSTANT_Module:
      return new ConstantModule(dataInput);
      case Const.CONSTANT_Package:
      return new ConstantPackage(dataInput);
      default:
      throw new ClassFormatException("Invalid byte tag in constant pool: " + b);
    }
  }

  /**
     * @return Comparison strategy object
     */
  public static BCELComparator getComparator() {
    return bcelComparator;
  }

  /**
     * @param comparator Comparison strategy object
     */
  public static void setComparator(final BCELComparator comparator) {
    bcelComparator = comparator;
  }

  /**
     * Returns value as defined by given BCELComparator strategy.
     * By default two Constant objects are said to be equal when
     * the result of toString() is equal.
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
  @Override public boolean equals(final Object obj) {
    return bcelComparator.equals(this, obj);
  }

  /**
     * Returns value as defined by given BCELComparator strategy.
     * By default return the hashcode of the result of toString().
     *
     * @see java.lang.Object#hashCode()
     */
  @Override public int hashCode() {
    return bcelComparator.hashCode(this);
  }
}