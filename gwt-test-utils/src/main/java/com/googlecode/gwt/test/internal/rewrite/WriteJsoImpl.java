package com.googlecode.gwt.test.internal.rewrite;
import com.google.gwt.dev.shell.rewrite.HostedModeClassRewriter.InstanceMethodOracle;
import com.google.gwt.dev.shell.rewrite.HostedModeClassRewriter.SingleJsoImplData;
import com.googlecode.gwt.test.internal.utils.JsoProperties;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Writes the implementation classes for JSO and its subtypes.
 * <p>
 * Changes made by the base class:
 * <ol>
 * <li>The new type has the same name as the old type with a '$' appended.</li>
 * <li>All instance methods in the original type become static methods taking an explicit
 * <code>this</code> parameter. Such methods have the same stack behavior as the original.</li>
 * </ol>
 */
abstract class WriteJsoImpl extends ClassVisitor {
  private static class ForJsoDollar extends WriteJsoImpl {
    private final SingleJsoImplData jsoData;

    /**
         * An unmodifiable set of descriptors containing <code>JavaScriptObject</code> and all
         * subclasses.
         */
    private final Set<String> jsoDescriptors;

    public ForJsoDollar(ClassVisitor cv, Set<String> jsoDescriptors, InstanceMethodOracle mapper, SingleJsoImplData jsoData) {
      super(cv, mapper);
      this.jsoDescriptors = jsoDescriptors;
      this.jsoData = jsoData;
    }

    @Override public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
      ArrayList<String> jsoDescList = new ArrayList<>();
      jsoDescList.addAll(jsoDescriptors);
      interfaces = jsoDescList.toArray(new String[jsoDescList.size()]);
      super.visit(version, access, name, signature, superName, interfaces);
      FieldVisitor fv = visitField(Opcodes.ACC_PROTECTED | Opcodes.ACC_SYNTHETIC, JsoProperties.JSO_PROPERTIES, "Lcom/googlecode/gwt/test/internal/utils/PropertyContainer;", null, null);
      if (fv != null) {
        fv.visitEnd();
      }
      for (String mangledName : jsoData.getMangledNames()) {
        List<Method> declarations = jsoData.getDeclarations(mangledName);
        List<Method> implementations = jsoData.getImplementations(mangledName);
        assert declarations.size() == implementations.size() : "Declaration / implementation size mismatch";
        Iterator<Method> declIterator = declarations.iterator();
        Iterator<Method> implIterator = implementations.iterator();
        while (declIterator.hasNext()) {
          assert implIterator.hasNext();
          writeTrampoline(mangledName, declIterator.next(), implIterator.next());
        }
      }
    }

    @Override public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
      if (isCtor(name)) {
        access &= ~(Opcodes.ACC_PRIVATE | Opcodes.ACC_PROTECTED);
        access |= Opcodes.ACC_PUBLIC;
      }
      return super.visitMethod(access, name, desc, signature, exceptions);
    }

    /**
         * JSO methods are implemented as flyweight style, with the instance being passed as the first
         * parameter. This loop create instance methods on JSO$ for all of the mangled SingleJsoImpl
         * interface method names. These instance methods simply turn around and call the
         * static-dispatch methods. In Java, it might look like:
         * <p>
         * <pre>
         * interface Interface {
         *   String someMethod(int a, double b);
         * }
         *
         * class J extends JSO implements I {
         *   public String com_google_Interface_someMethod(int a, double b) {
         *     return com.google.MyJso$.someMethod$(this, a, b);
         *   }
         * }
         * </pre>
         *
         * @param mangledName        {@code com_google_gwt_sample_hello_client_Interface_a}
         * @param interfaceMethod    {@code java.lang.String a(int, double)}
         * @param implementingMethod {@code static final java.lang.String
         *                           a$(com.google.gwt.sample.hello.client.Jso, ...);}
         */
    private void writeTrampoline(String mangledName, Method interfaceMethod, Method implementingMethod) {
      assert implementingMethod.getArgumentTypes().length > 0;
      String localDescriptor = interfaceMethod.getDescriptor();
      Method localMethod = new Method(mangledName, localDescriptor);
      Type implementingType = Type.getType("L" + implementingMethod.getArgumentTypes()[0].getInternalName() + "$;");
      MethodVisitor mv = visitMethodNoRewrite(Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC, localMethod.getName(), localMethod.getDescriptor(), null, null);
      if (mv != null) {
        mv.visitCode();
        int var = 0;
        int size = 0;
        for (Type t : implementingMethod.getArgumentTypes()) {
          size += t.getSize();
          mv.visitVarInsn(t.getOpcode(Opcodes.ILOAD), var);
          var += t.getSize();
        }
        size = Math.max(size, implementingMethod.getReturnType().getSize());
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, implementingType.getInternalName(), implementingMethod.getName(), implementingMethod.getDescriptor(), false);
        mv.visitInsn(localMethod.getReturnType().getOpcode(Opcodes.IRETURN));
        mv.visitMaxs(size, var);
        mv.visitEnd();
      }
    }
  }

  private static class ForJsoInterface extends WriteJsoImpl {
    public ForJsoInterface(ClassVisitor cv, InstanceMethodOracle mapper) {
      super(cv, mapper);
    }

    @Override public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
      superName += '$';
      interfaces = null;
      super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
      boolean isCtor = isCtor(name);
      if (isCtor) {
        return null;
      }
      return super.visitMethod(access, name, desc, signature, exceptions);
    }
  }

  /**
     * Creates a ClassVisitor to implement a JavaScriptObject subtype. This will select between a
     * simple implementation for user-defined JSO subtypes and the complex implementation for
     * implementing JavaScriptObject$.
     */
  public static ClassVisitor create(ClassVisitor cv, String classDescriptor, Set<String> jsoDescriptors, InstanceMethodOracle mapper, SingleJsoImplData singleJsoImplData) {
    if (classDescriptor.equals(OverlayTypesRewriter.JAVASCRIPTOBJECT_IMPL_DESC)) {
      return new ForJsoDollar(cv, jsoDescriptors, mapper, singleJsoImplData);
    }
    return new ForJsoInterface(cv, mapper);
  }

  /**
     * Maps methods to the class in which they are declared.
     */
  private final InstanceMethodOracle mapper;

  /**
     * The original name of the class being visited.
     */
  private String originalName;

  /**
     * Construct a new rewriter instance.
     *
     * @param cv             the visitor to chain to
     * @param jsoDescriptors an unmodifiable set of descriptors containing
     *                       <code>JavaScriptObject</code> and all subclasses
     * @param mapper         maps methods to the class in which they are declared
     */
  private WriteJsoImpl(ClassVisitor cv, InstanceMethodOracle mapper) {
    super(Opcodes.ASM5, cv);
    this.mapper = mapper;
  }

  /**
     * Records the original name and resets access opcodes.
     */
  @Override public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    originalName = name;
    super.visit(version, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC, name + '$', signature, superName, interfaces);
  }

  /**
     * Mangle all instance methods declared in JavaScriptObject types.
     */
  @Override public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
    boolean isCtor = isCtor(name);
    if (!isCtor && !isStatic(access) && !isObjectMethod(name + desc)) {
      access |= Opcodes.ACC_STATIC;
      desc = OverlayTypesRewriter.addSyntheticThisParam(getOriginalName(), desc);
      name = name + "$";
    }
    return super.visitMethod(access, name, desc, signature, exceptions);
  }

  protected String getOriginalName() {
    return originalName;
  }

  protected boolean isCtor(String name) {
    return "<init>".equals(name);
  }

  protected boolean isObjectMethod(String signature) {
    return "java/lang/Object".equals(mapper.findOriginalDeclaringClass(originalName, signature));
  }

  protected boolean isStatic(int access) {
    return (access & Opcodes.ACC_STATIC) != 0;
  }

  /**
     * Allows access to an unmodified visitMethod call.
     */
  protected MethodVisitor visitMethodNoRewrite(int access, String name, String desc, String signature, String[] exceptions) {
    return super.visitMethod(access, name, desc, signature, exceptions);
  }
}