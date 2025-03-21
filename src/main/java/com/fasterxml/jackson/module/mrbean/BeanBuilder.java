package com.fasterxml.jackson.module.mrbean;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.objectweb.asm.Opcodes.*;

/**
 * Heavy lifter of mr Bean package: class that keeps track of logical POJO properties,
 * and figures out how to create an implementation class.
 */
public class BeanBuilder {
  protected Map<String, POJOProperty> _beanProperties = new LinkedHashMap<String, POJOProperty>();

  protected LinkedHashMap<String, Method> _unsupportedMethods = new LinkedHashMap<String, Method>();

  /**
     * Abstract class or interface that the bean is created to extend or implement.
     */
  protected final Class<?> _implementedType;

  protected final TypeFactory _typeFactory;

  public BeanBuilder(Class<?> implType, TypeFactory tf) {
    _implementedType = implType;
    _typeFactory = tf;
  }

  /**
     * @param failOnUnrecognized If true, and an unrecognized (non-getter, non-setter)
     *   method is encountered, will throw {@link IllegalArgumentException}; if false,
     *   will implement bogus method that will throw {@link UnsupportedOperationException}
     *   if called.
     */
  public BeanBuilder implement(boolean failOnUnrecognized) {
    ArrayList<Class<?>> implTypes = new ArrayList<Class<?>>();
    implTypes.add(_implementedType);
    BeanUtil.findSuperTypes(_implementedType, Object.class, implTypes);
    for (Class<?> impl : implTypes) {
      for (Method m : impl.getDeclaredMethods()) {
        String methodName = m.getName();
        int argCount = m.getParameterTypes().length;
        if (argCount == 0) {
          if (methodName.startsWith("get") || methodName.startsWith("is") && returnsBoolean(m)) {
            addGetter(m);
            continue;
          }
        } else {
          if (argCount == 1 && methodName.startsWith("set")) {
            addSetter(m);
            continue;
          }
        }
        if (BeanUtil.isConcrete(m) || _unsupportedMethods.containsKey(methodName) || hasConcreteOverride(m, _implementedType)) {
          continue;
        }
        if (failOnUnrecognized) {
          throw new IllegalArgumentException("Unrecognized abstract method \'" + methodName + "\' (not a getter or setter) -- to avoid exception, disable AbstractTypeMaterializer.Feature.FAIL_ON_UNMATERIALIZED_METHOD");
        }
        _unsupportedMethods.put(methodName, m);
      }
    }
    return this;
  }

  private boolean hasConcreteOverride(Method m, Class<?> implementedType) {
    try {
      final Method effectiveMethod = implementedType.getMethod(m.getName(), m.getParameterTypes());
      return BeanUtil.isConcrete(effectiveMethod);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Could not determine if method is overriden or not", e);
    }
  }

  /**
     * Method that generates byte code for class that implements abstract
     * types requested so far.
     * 
     * @param className Fully-qualified name of the class to generate
     * @return Byte code Class instance built by this builder
     */
  public byte[] build(String className) {
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    String internalClass = getInternalClassName(className);
    String implName = getInternalClassName(_implementedType.getName());
    String superName;
    if (_implementedType.isInterface()) {
      superName = "java/lang/Object";
      cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, internalClass, null, superName, new String[] { implName });
    } else {
      superName = implName;
      cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, internalClass, null, implName, null);
    }
    cw.visitSource(className + ".java", null);
    BeanBuilder.generateDefaultConstructor(cw, superName);
    for (POJOProperty prop : _beanProperties.values()) {
      TypeDescription type = prop.selectType(_typeFactory);
      createField(cw, prop, type);
      if (!prop.hasConcreteGetter()) {
        createGetter(cw, internalClass, prop, type);
      }
      if (!prop.hasConcreteSetter()) {
        createSetter(cw, internalClass, prop, type);
      }
    }
    for (Method m : _unsupportedMethods.values()) {
      createUnimplementedMethod(cw, internalClass, m);
    }
    cw.visitEnd();
    return cw.toByteArray();
  }

  private static String getPropertyName(String methodName) {
    int prefixLen = methodName.startsWith("is") ? 2 : 3;
    String body = methodName.substring(prefixLen);
    StringBuilder sb = new StringBuilder(body);
    sb.setCharAt(0, Character.toLowerCase(body.charAt(0)));
    return sb.toString();
  }

  private static String buildGetterName(String fieldName) {
    StringBuilder sb = new StringBuilder(fieldName.length());
    return sb.append("get").append(Character.toUpperCase(fieldName.charAt(0))).append(fieldName.substring(1)).toString();
  }

  private static String buildSetterName(String fieldName) {
    StringBuilder sb = new StringBuilder(fieldName.length());
    return sb.append("set").append(Character.toUpperCase(fieldName.charAt(0))).append(fieldName.substring(1)).toString();
  }

  private static String getInternalClassName(String className) {
    return className.replace(".", "/");
  }

  private void addGetter(Method m) {
    POJOProperty prop = findProperty(getPropertyName(m.getName()));
    if (prop.getGetter() == null) {
      prop.setGetter(m);
    }
  }

  private void addSetter(Method m) {
    POJOProperty prop = findProperty(getPropertyName(m.getName()));
    if (prop.getSetter() == null) {
      prop.setSetter(m);
    }
  }

  private POJOProperty findProperty(String propName) {
    POJOProperty prop = _beanProperties.get(propName);
    if (prop == null) {
      prop = new POJOProperty(propName, _implementedType);
      _beanProperties.put(propName, prop);
    }
    return prop;
  }

  private final static boolean returnsBoolean(Method m) {
    Class<?> rt = m.getReturnType();
    return (rt == Boolean.class || rt == Boolean.TYPE);
  }

  protected static void generateDefaultConstructor(ClassWriter cw, String superName) {
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, superName, "<init>", "()V", false);
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
  }

  private static void createField(ClassWriter cw, POJOProperty prop, TypeDescription type) {
    String sig = type.hasGenerics() ? type.genericSignature() : null;
    String desc = type.erasedSignature();
    FieldVisitor fv = cw.visitField(ACC_PUBLIC, prop.getFieldName(), desc, sig, null);
    fv.visitEnd();
  }

  private static void createSetter(ClassWriter cw, String internalClassName, POJOProperty prop, TypeDescription propertyType) {
    String methodName;
    String desc;
    Method setter = prop.getSetter();
    if (setter != null) {
      desc = Type.getMethodDescriptor(setter);
      methodName = setter.getName();
    } else {
      desc = "(" + propertyType.erasedSignature() + ")V";
      methodName = buildSetterName(prop.getName());
    }
    String sig = propertyType.hasGenerics() ? ("(" + propertyType.genericSignature() + ")V") : null;
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, desc, sig, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(propertyType.getLoadOpcode(), 1);
    mv.visitFieldInsn(PUTFIELD, internalClassName, prop.getFieldName(), propertyType.erasedSignature());
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
  }

  private static void createGetter(ClassWriter cw, String internalClassName, POJOProperty prop, TypeDescription propertyType) {
    String methodName;
    String desc;
    Method getter = prop.getGetter();
    if (getter != null) {
      desc = Type.getMethodDescriptor(getter);
      methodName = getter.getName();
    } else {
      desc = "()" + propertyType.erasedSignature();
      methodName = buildGetterName(prop.getName());
    }
    String sig = propertyType.hasGenerics() ? ("()" + propertyType.genericSignature()) : null;
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, methodName, desc, sig, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, internalClassName, prop.getFieldName(), propertyType.erasedSignature());
    mv.visitInsn(propertyType.getReturnOpcode());
    mv.visitMaxs(0, 0);
    mv.visitEnd();
  }

  /**
     * Builder for methods that just throw an exception, basically "unsupported
     * operation" implementation.
     */
  private static void createUnimplementedMethod(ClassWriter cw, String internalClassName, Method method) {
    String exceptionName = getInternalClassName(UnsupportedOperationException.class.getName());
    String sig = Type.getMethodDescriptor(method);
    String name = method.getName();
    MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, name, sig, null, null);
    mv.visitTypeInsn(NEW, exceptionName);
    mv.visitInsn(DUP);
    mv.visitLdcInsn("Unimplemented method \'" + name + "\' (not a setter/getter, could not materialize)");
    mv.visitMethodInsn(INVOKESPECIAL, exceptionName, "<init>", "(Ljava/lang/String;)V", false);
    mv.visitInsn(ATHROW);
    mv.visitMaxs(0, 0);
    mv.visitEnd();
  }

  static class TypeDescription {
    private final Type _asmType;

    private JavaType _jacksonType;

    public TypeDescription(JavaType type) {
      _jacksonType = type;
      _asmType = Type.getType(type.getRawClass());
    }

    public Class<?> getRawClass() {
      return _jacksonType.getRawClass();
    }

    public String erasedSignature() {
      return _jacksonType.getErasedSignature();
    }

    public String genericSignature() {
      return _jacksonType.getGenericSignature();
    }

    /**
         * @return True if type has direct generic declaration (which may need
         *   to be copied)
         */
    public boolean hasGenerics() {
      return _jacksonType.hasGenericTypes();
    }

    public int getLoadOpcode() {
      return _asmType.getOpcode(ILOAD);
    }

    public int getReturnOpcode() {
      return _asmType.getOpcode(IRETURN);
    }

    @Override public String toString() {
      return _jacksonType.toString();
    }

    public static TypeDescription moreSpecificType(TypeDescription desc1, TypeDescription desc2) {
      Class<?> c1 = desc1.getRawClass();
      Class<?> c2 = desc2.getRawClass();
      if (c1.isAssignableFrom(c2)) {
        return desc2;
      }
      if (c2.isAssignableFrom(c1)) {
        return desc1;
      }
      return null;
    }
  }
}