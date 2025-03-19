package com.googlecode.guice;
import static com.google.inject.matcher.Matchers.any;
import com.google.common.testing.GcFinalization;
import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.googlecode.guice.BytecodeGenTest.LogCreator;
import com.googlecode.guice.PackageVisibilityTestModule.PublicUserOfPackagePrivate;
import junit.framework.TestCase;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URLClassLoader;
import javax.inject.Inject;

/**
 * This test is in a separate package so we can test package-level visibility
 * with confidence.
 *
 * @author mcculls@gmail.com (Stuart McCulloch)
 */
public class BytecodeGenTest extends TestCase {
  private final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();

  private final Module interceptorModule = new AbstractModule() {
    protected void configure() {
      bindInterceptor(any(), any(), new MethodInterceptor() {
        public Object invoke(MethodInvocation chain) throws Throwable {
          return chain.proceed() + " WORLD";
        }
      });
    }
  };

  private final Module noopInterceptorModule = new AbstractModule() {
    protected void configure() {
      bindInterceptor(any(), any(), new MethodInterceptor() {
        public Object invoke(MethodInvocation chain) throws Throwable {
          return chain.proceed();
        }
      });
    }
  };

  public void testPackageVisibility() {
    Injector injector = Guice.createInjector(new PackageVisibilityTestModule());
    injector.getInstance(PublicUserOfPackagePrivate.class);
  }

  public void testInterceptedPackageVisibility() {
    Injector injector = Guice.createInjector(interceptorModule, new PackageVisibilityTestModule());
    injector.getInstance(PublicUserOfPackagePrivate.class);
  }

  public void testEnhancerNaming() {
    Injector injector = Guice.createInjector(interceptorModule, new PackageVisibilityTestModule());
    PublicUserOfPackagePrivate pupp = injector.getInstance(PublicUserOfPackagePrivate.class);
    assertTrue(pupp.getClass().getName().startsWith(PublicUserOfPackagePrivate.class.getName() + "$$EnhancerByGuice$$"));
  }

  static class TestVisibilityClassLoader extends URLClassLoader {
    final boolean hideInternals;

    TestVisibilityClassLoader(boolean hideInternals) {
      this((URLClassLoader) TestVisibilityClassLoader.class.getClassLoader(), hideInternals);
    }

    TestVisibilityClassLoader(URLClassLoader classloader, boolean hideInternals) {
      super(classloader.getURLs(), classloader);
      this.hideInternals = hideInternals;
    }

    /**
     * Classic parent-delegating classloaders are meant to override findClass.
     * However, non-delegating classloaders (as used in OSGi) instead override
     * loadClass to provide support for "class-space" separation.
     */
    @Override protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
      synchronized (this) {
        final Class<?> clazz = findLoadedClass(name);
        if (clazz != null) {
          return clazz;
        }
      }
      if (name.startsWith("java.")) {
        return super.loadClass(name, resolve);
      } else {
        if (!name.contains(".internal.") && !name.contains(".cglib.")) {
          final Class<?> clazz = findClass(name);
          if (resolve) {
            resolveClass(clazz);
          }
          return clazz;
        }
      }
      if (hideInternals) {
        throw new ClassNotFoundException();
      }
      return super.loadClass(name, resolve);
    }
  }

  /** as loaded by another class loader */
  private Class<ProxyTest> proxyTestClass;

  private Class<ProxyTestImpl> realClass;

  private Module testModule;

  @SuppressWarnings(value = { "unchecked" }) protected void setUp() throws Exception {
    super.setUp();
    ClassLoader testClassLoader = new TestVisibilityClassLoader(true);
    proxyTestClass = (Class<ProxyTest>) testClassLoader.loadClass(ProxyTest.class.getName());
    realClass = (Class<ProxyTestImpl>) testClassLoader.loadClass(ProxyTestImpl.class.getName());
    testModule = new AbstractModule() {
      public void configure() {
        bind(proxyTestClass).to(realClass);
      }
    };
  }

  interface ProxyTest {
    String sayHello();
  }

  public static class ProxyTestImpl implements ProxyTest {


    public String sayHello() {
      return "HELLO";
    }
  }

  public void testProxyClassLoading() throws Exception {
    Object testObject = Guice.createInjector(interceptorModule, testModule).getInstance(proxyTestClass);
    Method m = realClass.getMethod("sayHello");
    assertEquals("HELLO WORLD", m.invoke(testObject));
  }

  public void testSystemClassLoaderIsUsedIfProxiedClassUsesIt() {
    ProxyTest testProxy = Guice.createInjector(interceptorModule, new Module() {
      public void configure(Binder binder) {
        binder.bind(ProxyTest.class).to(ProxyTestImpl.class);
      }
    }).getInstance(ProxyTest.class);
    if (ProxyTest.class.getClassLoader() == systemClassLoader) {
      assertSame(testProxy.getClass().getClassLoader(), systemClassLoader);
    } else {
      assertNotSame(testProxy.getClass().getClassLoader(), systemClassLoader);
    }
  }

  private WeakReference<Class<?>> getWeakReference() {
    Object testObject = Guice.createInjector(interceptorModule, testModule).getInstance(proxyTestClass);
    assertNotNull(testObject.getClass().getClassLoader());
    assertNotSame(testObject.getClass().getClassLoader(), systemClassLoader);
    WeakReference<Class<?>> clazzRef = new WeakReference<Class<?>>(testObject.getClass());
    assertNotNull(clazzRef.get());
    testObject = null;
    return clazzRef;
  }

  public void testProxyClassUnloading() {

<<<<<<< Unknown file: This is a bug in JDime.
=======
    WeakReference<Class<?>> clazzRef = new WeakReference<Class<?>>(testObject.getClass());
>>>>>>> /usr/src/app/output/google/guice/8f227682888fdd3fa656bb2b244cae95f710e442/core/test/com/googlecode/guice/BytecodeGenTest.java/right.java

    GcFinalization.awaitClear(
<<<<<<< /usr/src/app/output/google/guice/8f227682888fdd3fa656bb2b244cae95f710e442/core/test/com/googlecode/guice/BytecodeGenTest.java/left.java
    getWeakReference()
=======
    clazzRef
>>>>>>> /usr/src/app/output/google/guice/8f227682888fdd3fa656bb2b244cae95f710e442/core/test/com/googlecode/guice/BytecodeGenTest.java/right.java
    );
  }

  public void testProxyingPackagePrivateMethods() {
    Injector injector = Guice.createInjector(interceptorModule);
    assertEquals("HI WORLD", injector.getInstance(PackageClassPackageMethod.class).sayHi());
    assertEquals("HI WORLD", injector.getInstance(PublicClassPackageMethod.class).sayHi());
    assertEquals("HI WORLD", injector.getInstance(ProtectedClassProtectedMethod.class).sayHi());
  }

  static class PackageClassPackageMethod {
    String sayHi() {
      return "HI";
    }
  }

  public static class PublicClassPackageMethod {
    String sayHi() {
      return "HI";
    }
  }

  protected static class ProtectedClassProtectedMethod {
    protected String sayHi() {
      return "HI";
    }
  }

  static class Hidden {
  }

  public static class HiddenMethodReturn {
    public Hidden method() {
      return new Hidden();
    }
  }

  public static class HiddenMethodParameter {
    public void method(Hidden h) {
    }
  }

  public void testClassLoaderBridging() throws Exception {
    ClassLoader testClassLoader = new TestVisibilityClassLoader(false);
    Class hiddenMethodReturnClass = testClassLoader.loadClass(HiddenMethodReturn.class.getName());
    Class hiddenMethodParameterClass = testClassLoader.loadClass(HiddenMethodParameter.class.getName());
    Injector injector = Guice.createInjector(noopInterceptorModule);
    Class hiddenClass = testClassLoader.loadClass(Hidden.class.getName());
    Constructor ctor = hiddenClass.getDeclaredConstructor();
    ctor.setAccessible(true);
    Object o1 = injector.getInstance(hiddenMethodParameterClass);
    o1.getClass().getDeclaredMethod("method", hiddenClass).invoke(o1, ctor.newInstance());
    Object o2 = injector.getInstance(hiddenMethodReturnClass);
    o2.getClass().getDeclaredMethod("method").invoke(o2);
  }

  public void testFastClassUsesBridgeClassloader() throws Throwable {
    Injector injector = Guice.createInjector();
    injector.getInstance(PublicInject.class).assertIsFastClassInvoked();
    injector.getInstance(ProtectedInject.class).assertIsFastClassInvoked();
    injector.getInstance(PackagePrivateInject.class).assertIsFastClassInvoked();
    injector.getInstance(PrivateInject.class).assertIsReflectionInvoked();
    MultipleVersionsOfGuiceClassLoader fakeLoader = new MultipleVersionsOfGuiceClassLoader();
    injector.getInstance(fakeLoader.loadLogCreatorType(PublicInject.class)).assertIsFastClassInvoked();
    injector.getInstance(fakeLoader.loadLogCreatorType(ProtectedInject.class)).assertIsReflectionInvoked();
    injector.getInstance(fakeLoader.loadLogCreatorType(PackagePrivateInject.class)).assertIsReflectionInvoked();
    injector.getInstance(fakeLoader.loadLogCreatorType(PrivateInject.class)).assertIsReflectionInvoked();
  }

  static class MultipleVersionsOfGuiceClassLoader extends URLClassLoader {
    MultipleVersionsOfGuiceClassLoader() {
      this((URLClassLoader) MultipleVersionsOfGuiceClassLoader.class.getClassLoader());
    }

    MultipleVersionsOfGuiceClassLoader(URLClassLoader classloader) {
      super(classloader.getURLs(), classloader);
    }

    public Class<? extends LogCreator> loadLogCreatorType(Class<? extends LogCreator> cls) throws ClassNotFoundException {
      return loadClass(cls.getName()).asSubclass(LogCreator.class);
    }

    /**
     * Classic parent-delegating classloaders are meant to override findClass.
     * However, non-delegating classloaders (as used in OSGi) instead override
     * loadClass to provide support for "class-space" separation.
     */
    @Override protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
      synchronized (this) {
        final Class<?> clazz = findLoadedClass(name);
        if (clazz != null) {
          return clazz;
        }
      }
      if (name.startsWith("java.") || name.startsWith("javax.") || name.equals(LogCreator.class.getName()) || (!name.startsWith("com.google.inject.") && !name.contains(".cglib.") && !name.startsWith("com.googlecode.guice"))) {
        return super.loadClass(name, resolve);
      } else {
        final Class<?> clazz = findClass(name);
        if (resolve) {
          resolveClass(clazz);
        }
        return clazz;
      }
    }
  }

  public static class LogCreator {
    final Throwable caller;

    public LogCreator() {
      this.caller = new Throwable();
    }

    void assertIsFastClassInvoked() throws Throwable {
      if (!caller.getStackTrace()[2].getClassName().contains("$$FastClassByGuice$$")) {
        throw new AssertionError("Caller was not FastClass").initCause(caller);
      }
    }

    void assertIsReflectionInvoked() throws Throwable {
      for (StackTraceElement element : caller.getStackTrace()) {
        if (element.getClassName().equals(BytecodeGenTest.class.getName())) {
          break;
        }
        if (element.getClassName().equals(Constructor.class.getName()) && element.getMethodName().equals("newInstance")) {
          return;
        }
      }
      throw new AssertionError("Caller was not Constructor.newInstance").initCause(caller);
    }
  }

  public static class PublicInject extends LogCreator {
    @Inject public PublicInject() {
    }
  }

  static class PackagePrivateInject extends LogCreator {
    @Inject PackagePrivateInject() {
    }
  }

  protected static class ProtectedInject extends LogCreator {
    @Inject protected ProtectedInject() {
    }
  }

  private static class PrivateInject extends LogCreator {
    @Inject private PrivateInject() {
    }
  }
}