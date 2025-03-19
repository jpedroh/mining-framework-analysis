package rubah.bytecode.transformers;
import java.lang.ThreadLocal;
import java.lang.ref.Reference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import rubah.Rubah;
import rubah.framework.Namespace;
import rubah.framework.Type;
import rubah.runtime.Version;
import sun.misc.Unsafe;

public class AddTraverseMethod extends RubahTransformer {
  private static final String OBJECT_INTERNAL_NAME = Type.getType(Object.class).getInternalName();

  private static final int TRAVERSE_METHOD_ACC_FLAGS = ACC_PUBLIC | ACC_SYNTHETIC;

  protected static final String TRAVERSE_METHOD_DESC = Type.getMethodDescriptor(Type.VOID_TYPE);

  public static final String TRAVERSE_METHOD_NAME = "$traverse";

  private static final String REGISTER_METHOD_DESC = Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(Object.class), Type.getType(java.lang.reflect.Field.class));

  protected static final String REGISTER_METHOD_SIMPLE_DESC = Type.getMethodDescriptor(Type.getType(Object.class), Type.getType(Object.class));

  protected static final String REGISTER_METHOD_NAME = "registerTraversed";

  protected static final String REGISTER_METHOD_OWNER_NAME = Type.getType(Rubah.class).getInternalName();

  private static final String[] blackListedPackages = { Method.class.getPackage().getName(), Unsafe.class.getName(), "java.nio.charset", Throwable.class.getName(), Rubah.class.getPackage().getName() + ".", Object.class.getPackage().getName(), "sun.security", "sun.nio.ch.SocketChannelImpl", "sun.nio.ch.SocketAdaptor", "com.sun.proxy" };

  public static boolean isAllowed(String fqn) {
    if (fqn.startsWith(Object.class.getName()) || fqn.startsWith(Class.class.getName()) || fqn.startsWith(Reference.class.getPackage().getName()) || fqn.startsWith(ThreadLocal.class.getName())) {
      return true;
    }
    for (String disallowedPak : blackListedPackages) {
      if (fqn.startsWith(disallowedPak)) {
        return false;
      }
    }
    return true;
  }

  private boolean hasParent;

  private String parentInternalName;

  protected Version version;

  public AddTraverseMethod(HashMap<String, Object> objectsMap, Namespace namespace, ClassVisitor cv) {
    super(objectsMap, namespace, cv);
  }

  public AddTraverseMethod(HashMap<String, Object> objectsMap, Version version, ClassVisitor cv) {
    super(objectsMap, version.getNamespace(), cv);
    this.version = version;
  }

  @Override public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
    this.hasParent = !name.equals(OBJECT_INTERNAL_NAME);
    if (this.hasParent) {
      this.parentInternalName = superName;
    }
    super.visit(version, access, name, signature, superName, interfaces);
  }

  protected boolean isFieldInteresting(int access, Type fieldType) {
    return !Modifier.isStatic(access) && !fieldType.isPrimitive() && (!fieldType.isArray() || !fieldType.getElementType().isPrimitive());
  }

  protected String getTraverseMethodName() {
    return TRAVERSE_METHOD_NAME;
  }

  protected int getTraverseMethodAccess() {
    return TRAVERSE_METHOD_ACC_FLAGS;
  }

  protected void generateTraverseMethodPreamble(MethodVisitor mv) {
    if (this.hasParent) {
      if (!isAllowed(this.parentInternalName.replace('/', '.'))) {
        return;
      }
      mv.visitVarInsn(ALOAD, 0);
      mv.visitMethodInsn(INVOKESPECIAL, this.parentInternalName, TRAVERSE_METHOD_NAME, TRAVERSE_METHOD_DESC, false);
    }
  }

  protected void getFieldOwner(MethodVisitor mv) {
    mv.visitVarInsn(ALOAD, 0);
  }
}