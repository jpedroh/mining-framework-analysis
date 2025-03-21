package st.redline.core;
import st.redline.classloader.*;
import static st.redline.compiler.SmalltalkGeneratingVisitor.*;
import static st.redline.core.PrimDoesNotUnderstand.*;
import static st.redline.core.PrimSubclass.*;

public class PrimObject {
  private PrimObject selfClass;

  private Object javaValue;

  public String toString() {
    if (javaValue != null) {
      return javaValue.toString();
    }
    if (selfClass != null && selfClass != this) {
      return selfClass.toString();
    }
    return super.toString();
  }

  public void javaValue(Object object) {
    javaValue = object;
  }

  public Object javaValue() {
    return javaValue;
  }

  public void selfClass(PrimObject primObject) {
    selfClass = primObject;
  }

  public PrimObject selfClass() {
    return selfClass;
  }

  public PrimObject referenceNil() {
    return classLoader().nilInstance();
  }

  public PrimObject referenceTrue() {
    return classLoader().trueInstance();
  }

  public PrimObject referenceFalse() {
    return classLoader().falseInstance();
  }

  public PrimObject reference(String name) {
    return resolveObject(name);
  }

  public PrimObject resolveObject(String name) {
    return findObject(importFor(name));
  }

  public PrimObject smalltalkBlock(Object value, PrimContext homeContext) {
    return instanceOfWith("BlockClosure", new Object[] { value, homeContext });
  }

  public PrimObject smalltalkBlockAnswer(Object value, PrimContext homeContext, String answerClassName) {
    return instanceOfWith("BlockClosure", new Object[] { throwingAnswer(value, answerClassName), homeContext });
  }

  private LambdaBlock throwingAnswer(Object value, String answerClassName) {
    return (self, receiver, context) -> {
      PrimObject answer = ((LambdaBlock) value).apply(self, receiver, context);
      PrimBlockAnswer blockAnswer;
      try {
        blockAnswer = (PrimBlockAnswer) classLoader().loadClass(answerClassName).getConstructor(PrimObject.class).newInstance(answer);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
      throw blockAnswer;
    };
  }

  public PrimObject smalltalkMethod(Object value, PrimContext homeContext) {
    return instanceOfWith("CompiledMethod", value);
  }

  public PrimObject smalltalkString(Object value) {
    return instanceOfWith("String", value);
  }

  public PrimObject smalltalkSymbol(Object value) {
    return instanceOfWith("Symbol", value);
  }

  protected PrimObject instanceOfWith(String type, Object value) {
    PrimObject instance = instanceOf(type);
    instance.javaValue(value);
    return instance;
  }

  protected PrimObject instanceOf(String type) {
    return isBootstrapping() ? new PrimObject() : resolveObject(type).perform("new");
  }

  protected boolean isBootstrapping() {
    return classLoader().isBootstrapping();
  }

  protected PrimObject findObject(String name) {
    return classLoader().findObject(name);
  }

  protected String importFor(String name) {
    return packageName() + "." + name;
  }

  protected void importAll(String packageName) {
    classLoader().importAll(packageName);
  }

  protected SmalltalkClassLoader classLoader() {
    return (SmalltalkClassLoader) Thread.currentThread().getContextClassLoader();
  }

  protected PrimObject sendMessages(PrimObject receiver, PrimContext context) {
    return receiver;
  }

  protected String packageName() {
    return DEFAULT_IMPORTED_PACKAGE;
  }

  public PrimObject perform(String selector) {
    return perform0(selector);
  }

  public PrimObject superPerform(String selector) {
    return perform0s(selector);
  }

  public PrimObject perform(PrimObject arg1, String selector) {
    return perform0(selector, arg1);
  }

  public PrimObject superPerform(PrimObject arg1, String selector) {
    return perform0s(selector, arg1);
  }

  public PrimObject perform(PrimObject arg1, PrimObject arg2, String selector) {
    return perform0(selector, arg1, arg2);
  }

  public PrimObject superPerform(PrimObject arg1, PrimObject arg2, String selector) {
    return perform0s(selector, arg1, arg2);
  }

  public PrimObject perform(PrimObject arg1, PrimObject arg2, PrimObject arg3, String selector) {
    return perform0(selector, arg1, arg2);
  }

  public PrimObject superPerform(PrimObject arg1, PrimObject arg2, PrimObject arg3, String selector) {
    return perform0s(selector, arg1, arg2);
  }

  public PrimObject perform(PrimObject arg1, PrimObject arg2, PrimObject arg3, PrimObject arg4, String selector) {
    return perform0(selector, arg1, arg2);
  }

  public PrimObject superPerform(PrimObject arg1, PrimObject arg2, PrimObject arg3, PrimObject arg4, String selector) {
    return perform0s(selector, arg1, arg2);
  }

  public PrimObject perform(PrimObject arg1, PrimObject arg2, PrimObject arg3, PrimObject arg4, PrimObject arg5, String selector) {
    return perform0(selector, arg1, arg2);
  }

  public PrimObject superPerform(PrimObject arg1, PrimObject arg2, PrimObject arg3, PrimObject arg4, PrimObject arg5, String selector) {
    return perform0s(selector, arg1, arg2);
  }

  protected PrimObject perform0(String selector, PrimObject... arguments) {
    return perform0(selfClass, selector, arguments);
  }

  protected PrimObject perform0s(String selector, PrimObject... arguments) {
    return perform0(selfClass.superclass(), selector, arguments);
  }

  protected PrimObject perform0(PrimObject foundInClass, String selector, PrimObject... arguments) {
    PrimObject cls = foundInClass;
    while (!cls.includesSelector(selector)) {
      cls = cls.superclass();
    }
    return apply(cls.methodFor(selector), cls, selector, arguments);
  }

  protected PrimObject apply(PrimObject method, PrimObject foundInClass, String selector, PrimObject... arguments) {
    PrimObject result = method.invoke(this, new PrimContext(this, foundInClass, selector, arguments));
    return result;
  }

  protected PrimObject invoke(PrimObject receiver, PrimContext context) {
    return this;
  }

  protected PrimObject methodFor(String selector) {
    if ("subclass:".equals(selector)) {
      return PRIM_SUBCLASS;
    }
    return PRIM_DOES_NOT_UNDERSTAND;
  }

  protected PrimObject superclass() {
    throw new IllegalStateException("This receiver should not have received this message.");
  }

  protected boolean includesSelector(String selector) {
    return true;
  }

  public boolean isMeta() {
    return false;
  }

  public PrimObject primitiveSubclass(PrimContext primContext) {
    return PRIM_SUBCLASS.invoke(this, primContext);
  }

  public PrimObject primitiveNew() {
    PrimObject object = new PrimObject();
    object.selfClass(this);
    return object;
  }

  public PrimObject primitiveEval(PrimContext context) {
    Object[] values = (Object[]) javaValue();
    context.homeContext((PrimContext) values[1]);
    return ((LambdaBlock) values[0]).apply(this, this, context);
  }

  public PrimObject primitive110(PrimContext context) {
    if (this.equals(context.argumentAt(0))) {
      return classLoader().trueInstance();
    } else {
      return classLoader().falseInstance();
    }
  }

  public PrimObject primitive111(PrimContext context) {
    return this.selfClass();
  }
}