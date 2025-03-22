package com.github.jinahya.bit.io;

import org.jboss.weld.junit5.WeldJunit5Extension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import java.io.IOException;

@ExtendWith({WeldJunit5Extension.class})
class ArrayByteInputParameterResolver extends AbstractByteInputParameterResolver<ArrayByteInput, byte[]> {

    // -----------------------------------------------------------------------------------------------------------------
    ArrayByteInputParameterResolver() {
        super(ArrayByteInput.class, byte[].class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    ArrayByteInputParameterResolver() {
        super(ArrayByteInput.class);
    }

    // -----------------------------------------------------------------------------------------------------------------

    @Override
    public Object resolveParameter(final ParameterContext parameterContext, final ExtensionContext extensionContext)
            throws ParameterResolutionException {
        //return new ArrayByteInput(new byte[LENGTH], 0, LENGTH);
        return new ArrayByteInput(null, -1, -1) {
            @Override
            public int read() throws IOException {
                if (source == null) {
                    source = new byte[LENGTH];
                    limit = source.length;
                    index = 0;
                }
                if (index == limit) {
                    // normally, drain the array here.
                    index = 0;
                }
                return super.read();
            }
        };
    }
}
