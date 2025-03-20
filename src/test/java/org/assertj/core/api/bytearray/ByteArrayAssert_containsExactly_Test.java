package org.assertj.core.api.bytearray;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.test.ByteArrays.arrayOf;
import static org.mockito.Mockito.verify;
import org.assertj.core.api.ByteArrayAssert;
import org.assertj.core.api.ByteArrayAssertBaseTest;
import org.assertj.core.util.AbsValueComparator;
import org.junit.jupiter.api.Test;

/**
 * Tests for <code>{@link org.assertj.core.api.ByteArrayAssert#containsExactly(byte...)}</code>.
 * 
 * @author Jean-Christophe Gay
 */
public class ByteArrayAssert_containsExactly_Test extends ByteArrayAssertBaseTest {
  @Override protected ByteArrayAssert invoke_api_method() {
    return assertions.containsExactly((byte) 1, (byte) 2);
  }

  @Override protected void verify_internal_effects() {
    verify(arrays).assertContainsExactly(getInfo(assertions), getActual(assertions), arrayOf(1, 2));
  }

  @Test public void should_honor_the_given_element_comparator() {
    byte[] actual = new byte[] { 1, 2, 3, 4 };
    assertThat(actual).usingElementComparator(new AbsValueComparator<Byte>()).containsExactly((byte) -1, (byte) 2, (byte) 3, (byte) -4);
  }

  @Test public void invoke_api_like_user() {
    assertThat(new byte[] { 1, 2, 3 }).containsExactly((byte) 1, (byte) 2, (byte) 3);
  }
}