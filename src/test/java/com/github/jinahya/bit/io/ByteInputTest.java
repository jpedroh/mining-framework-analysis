package com.github.jinahya.bit.io;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Objects;


<<<<<<< /usr/src/app/output/jinahya/bit-io/e4af15fa57fc664cd71b298e85c764d189d5dc44/src/test/java/com/github/jinahya/bit/io/ByteInputTest.java/left.java
@ExtendWith(value = { WeldJunit5Extension.class })
=======
>>>>>>> Unknown file: This is a bug in JDime.
 abstract class ByteInputTest<T extends ByteInput> {
  private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  ByteInputTest(final Class<T> byteInputClass) {
    super();
    this.byteInputClass = Objects.requireNonNull(byteInputClass, "byteInputClass is null");
  }

  @BeforeEach void selectByteInput() {
    byteInput = byteInputInstance.select(byteInputClass).get();
    logger.debug("byteInput: {}", byteInput);
  }

  @Test void testRead() throws IOException {
    final int octet = byteInput.read();
  }

  final Class<T> byteInputClass;

  @Typed @Inject private Instance<ByteInput> byteInputInstance;

  T byteInput;
}