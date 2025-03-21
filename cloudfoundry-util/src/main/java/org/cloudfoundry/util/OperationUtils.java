package org.cloudfoundry.util;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Function;

/**
 * Utilities with operations that do not (yet) exist
 */
public final class OperationUtils {
  private OperationUtils() {
  }

  /**
     * Operation to collect a {@code Flux<byte[]>} into a contiguous single byte array, delivered as a single element of a {@code Mono<byte[]>}.
     *
     * @param bytes a Flux of 0 or more byte arrays
     * @return a Mono of a byte array
     */
  public static Mono<byte[]> collectByteArray(Flux<byte[]> bytes) {
    return bytes.reduceWith(ByteArrayOutputStream::new, (prev, next) -> {
      try {
        prev.write(next);
      } catch (IOException e) {
        throw Exceptions.propagate(e);
      }
      return prev;
    }).map(ByteArrayOutputStream::toByteArray);
  }
}