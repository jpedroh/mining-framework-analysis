package com.esotericsoftware.kryo;
import static com.esotericsoftware.kryo.ReflectionAssert.*;
import static java.lang.Integer.*;
import static org.junit.jupiter.api.Assertions.*;
import com.esotericsoftware.kryo.SerializationCompatTestData.TestData;
import com.esotericsoftware.kryo.SerializationCompatTestData.TestDataJava8;
import com.esotericsoftware.kryo.io.ByteBufferInput;
import com.esotericsoftware.kryo.io.ByteBufferOutput;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.util.DefaultInstantiatorStrategy;
import com.esotericsoftware.minlog.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.objenesis.strategy.StdInstantiatorStrategy;

/** Test for serialization compatibility: data serialized with an older version (same major version) must be deserializable with
 * this newer (same major) version. Serialization compatibility is checked for each type that has a default serializer
 * (<code>Kryo.defaultSerializers</code>, populated from Kryo's constructor)
 *
 * Because the various {@link Input}/{@link Output} variants are not compatible, this test is done for each of these variants.
 *
 * This test uses previously created "canonical" tests files (one for each IO variant): it deserializes their content and checks
 * if the deserialized object is equals the expected object. The test files were created once with this test (it writes test files
 * if they're not yet existing), serializing an instance of {@link TestData} that contains fields for each default serializer
 * (respectively the related type) and some arbitratry other fields (just to have a class that's not too trivial).
 *
 * If any of these checks fail it may have different reasons: 1) the serialization format of an IO variant has changed 2) the
 * serialization format of a serializer has changed 3) the {@link TestData} structure/fields have changed
 *
 * In cases 1) and 2) the question is if that was intentionally and if it can't/shouldn't be avoided. If it was intentionally
 * probably kryo's major version should be incremented and new test files must be created (more on that later).
 *
 * In case 3) - assuming that this was intentional - new test files have to be created.
 *
 * To create new test files, just delete the existing ones and run this test. It will write new files so that you only have to
 * commit the changes. Depending on the situation you may consider creating new files from the smallest version of the same major
 * version (e.g. for 3.1.4 this is 3.0.0) - to do this just save this test and the {@link SerializationCompatTest}, go back to the
 * related tag and run the test (there's nothing here to automate creation of test files for a different version). */
class SerializationCompatTest extends KryoTestCase {
  private static final boolean DELETE_FAILED_TEST_FILES = false;

  private static final int JAVA_VERSION;

  static {
    String[] strVersions = System.getProperty("java.version").split("\\.");
    if (strVersions.length == 1) {
      JAVA_VERSION = parseInt(strVersions[0]);
    } else {
      int[] versions = new int[] { parseInt(strVersions[0]), parseInt(strVersions[1]) };
      JAVA_VERSION = versions[0] > 1 ? versions[0] : versions[1];
    }
  }

  private static final int EXPECTED_DEFAULT_SERIALIZER_COUNT = 
<<<<<<< /usr/src/app/output/esotericsoftware/kryo/34fbe7de2236f093b3bc1a20f52a56a71e609132/test/com/esotericsoftware/kryo/SerializationCompatTest.java/left.java
  JAVA_VERSION < 11 ? 57 : JAVA_VERSION < 14 ? 67 : 68
=======
  JAVA_VERSION < 11 ? 58 : 68
>>>>>>> /usr/src/app/output/esotericsoftware/kryo/34fbe7de2236f093b3bc1a20f52a56a71e609132/test/com/esotericsoftware/kryo/SerializationCompatTest.java/right.java
  ;

  private static final List<TestDataDescription> TEST_DATAS = new ArrayList<>();

  static {
    TEST_DATAS.add(new TestDataDescription<>(new TestData(), 1940, 1958));
    if (JAVA_VERSION >= 8) {
      TEST_DATAS.add(new TestDataDescription<>(new TestDataJava8(), 2098, 2116));
    }
    if (JAVA_VERSION >= 11) {
      TEST_DATAS.add(new TestDataDescription<>(createTestData(11), 2182, 2210));
    }
    if (JAVA_VERSION >= 14) {
      TEST_DATAS.add(new TestDataDescription<>(createTestData(14), 1948, 1966));
    }
  }



  private static TestData createTestData(int version) {
    try {
      return (TestData) Class.forName("com.esotericsoftware.kryo.TestDataJava" + version).getConstructor().newInstance();
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException("TestDataJava" + version + " could not be instantiated", e);
    }
  }

  @BeforeEach public void setUp() throws Exception {
    super.setUp();
    kryo.setInstantiatorStrategy(new DefaultInstantiatorStrategy(new StdInstantiatorStrategy()));
    kryo.setReferences(true);
    kryo.setRegistrationRequired(false);
    kryo.register(EnumSet.class);
  }

  @Test void testDefaultSerializers() throws Exception {
    Field defaultSerializersField = Kryo.class.getDeclaredField("defaultSerializers");
    defaultSerializersField.setAccessible(true);
    List defaultSerializers = (List) defaultSerializersField.get(kryo);
    assertEquals(EXPECTED_DEFAULT_SERIALIZER_COUNT, defaultSerializers.size(), "The registered default serializers have changed.\n" + "Because serialization compatibility shall be checked for default serializers, you must extend " + "SerializationCompatTestData.TestData to have a field for the type of the new default serializer.\n" + "After that\'s done, you must create new versions of \'test/resources/data*\' because the new TestData instance will " + "no longer be equals the formerly written/serialized one.");
  }

  @Test void testStandard() throws Exception {
    runTests("standard", new Function1<File, Input>() {
      public Input apply(File file) throws FileNotFoundException {
        return new Input(new FileInputStream(file));
      }
    }, new Function1<File, Output>() {
      public Output apply(File file) throws Exception {
        return new Output(new FileOutputStream(file));
      }
    });
  }

  @Test void testByteBuffer() throws Exception {
    runTests("bytebuffer", new Function1<File, Input>() {
      public Input apply(File file) throws FileNotFoundException {
        return new ByteBufferInput(new FileInputStream(file));
      }
    }, new Function1<File, Output>() {
      public Output apply(File file) throws Exception {
        return new ByteBufferOutput(new FileOutputStream(file));
      }
    });
  }

  private void runTests(String variant, Function1<File, Input> inputFactory, Function1<File, Output> outputFactory) throws Exception {
    setUp();
    for (TestDataDescription description : TEST_DATAS) {
      runTest(description, variant, inputFactory, outputFactory);
    }
  }

  private void runTest(TestDataDescription description, String variant, Function1<File, Input> inputFactory, Function1<File, Output> outputFactory) throws Exception {
    File testDir = new File("test");
    if (!testDir.exists()) {
      testDir = new File("../test");
    }
    File file = new File(testDir, "resources/" + description.classSimpleName() + "-" + variant + ".ser");
    file.getParentFile().mkdirs();
    if (file.exists()) {
      Log.info("Reading and testing " + description.classSimpleName() + " with mode \'" + variant + "\' from file " + file.getAbsolutePath());
      Input in = inputFactory.apply(file);
      try {
        readAndRunTest(description, in);
      } catch (Throwable ex) {
        if (DELETE_FAILED_TEST_FILES) {
          System.out.println("Failed: " + file.getAbsolutePath());
          in.close();
          file.delete();
        } else {
          throw ex;
        }
      }
      in.close();
    } else {
      Log.info("Testing and writing " + description.classSimpleName() + " with mode \'" + variant + "\' to file " + file.getAbsolutePath());
      Output out = outputFactory.apply(file);
      try {
        runTestAndWrite(description, out);
        out.close();
      } catch (Exception e) {
        out.close();
        file.delete();
        throw e;
      }
    }
  }

  private void readAndRunTest(TestDataDescription<?> description, Input in) throws FileNotFoundException {
    TestData actual = kryo.readObject(in, description.testDataClass());
    roundTrip(description.length, description.noGenericsLength, actual);
    try {
      assertReflectionEquals(actual, description.testData);
    } catch (AssertionError e) {
      Log.info("Serialization format is broken, please check " + getClass().getSimpleName() + "\'s class doc to see" + " what this means and how to proceed.");
      throw e;
    }
  }

  private void runTestAndWrite(TestDataDescription description, Output out) throws FileNotFoundException {
    roundTrip(description.length, description.noGenericsLength, description.testData);
    kryo.writeObject(out, description.testData);
  }

  protected void doAssertEquals(final Object one, final Object another) {
    try {
      assertReflectionEquals(one, another);
    } catch (Exception e) {
      fail("Test failed: " + e);
    }
  }

  private interface Function1<A extends java.lang.Object, B extends java.lang.Object> {
    B apply(A input) throws Exception;
  }

  private static class TestDataDescription<T extends TestData> {
    final T testData;

    final int length;

    final int noGenericsLength;

    TestDataDescription(T testData, int length, int noGenericsLength) {
      this.testData = testData;
      this.length = length;
      this.noGenericsLength = noGenericsLength;
    }

    Class<T> testDataClass() {
      return (Class<T>) testData.getClass();
    }

    String classSimpleName() {
      return testData.getClass().getSimpleName();
    }
  }
}