package com.github.jinahya.bit.io;
import org.jboss.weld.junit5.WeldJunit5Extension;
import org.junit.jupiter.api.extension.ExtendWith;
import java.io.IOException;

@ExtendWith(value = { WeldJunit5Extension.class }) class ArrayByteInputParameterResolver extends AbstractByteInputParameterResolver<ArrayByteInput, byte[]> {

<<<<<<< Unknown file: This is a bug in JDime.
=======
  private static final int LENGTH = 1024;
>>>>>>> /usr/src/app/output/jinahya/bit-io/e4af15fa57fc664cd71b298e85c764d189d5dc44/src/test/java/com/github/jinahya/bit/io/ArrayByteInputParameterResolver.java/right.java


  ArrayByteInputParameterResolver() {
    super(ArrayByteInput.class, byte[].class);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext) throws ParameterResolutionException {
    return new ArrayByteInput(null, -1, -1) {
      @Override public int read() throws IOException {
        if (source == null) {
          source = new byte[LENGTH];
          limit = source.length;
          index = 0;
        }
        if (index == limit) {
          index = 0;
        }
        return super.read();
      }
    };
  }
>>>>>>> /usr/src/app/output/jinahya/bit-io/e4af15fa57fc664cd71b298e85c764d189d5dc44/src/test/java/com/github/jinahya/bit/io/ArrayByteInputParameterResolver.java/right.java
}