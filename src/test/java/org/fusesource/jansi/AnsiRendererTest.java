package org.fusesource.jansi;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Attribute.*;
import static org.fusesource.jansi.Ansi.Color.*;
import static org.fusesource.jansi.AnsiRenderer.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for the {@link AnsiRenderer} class.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 */
public class AnsiRendererTest {
  @BeforeAll static void setUp() {
    Ansi.setEnabled(true);
  }

  @Test public void testTest() throws Exception {
    assertFalse(test("foo"));
    assertTrue(test("@|foo|"));
    assertTrue(test("@|foo"));
  }

  @Test public void testRender() {
    String str = render("@|bold foo|@");
    System.out.println(str);
    assertEquals(ansi().a(INTENSITY_BOLD).a("foo").reset().toString(), str);
    assertEquals(ansi().bold().a("foo").reset().toString(), str);
  }

  @Test public void testRenderCodes() {
    String str = renderCodes("bold red");
    System.out.println(str);
    assertEquals(ansi().bold().fg(Color.RED).toString(), str);
  }

  @Test public void testRender2() {
    String str = render("@|bold,red foo|@");
    System.out.println(str);
    assertEquals(Ansi.ansi().a(INTENSITY_BOLD).fg(RED).a("foo").reset().toString(), str);
    assertEquals(Ansi.ansi().bold().fgRed().a("foo").reset().toString(), str);
  }

  @Test public void testRender3() {
    String str = render("@|bold,red foo bar baz|@");
    System.out.println(str);
    assertEquals(ansi().a(INTENSITY_BOLD).fg(RED).a("foo bar baz").reset().toString(), str);
    assertEquals(ansi().bold().fgRed().a("foo bar baz").reset().toString(), str);
  }

  @Test public void testRender4() {
    String str = render("@|bold,red foo bar baz|@ ick @|bold,red foo bar baz|@");
    System.out.println(str);
    assertEquals(ansi().a(INTENSITY_BOLD).fg(RED).a("foo bar baz").reset().a(" ick ").a(INTENSITY_BOLD).fg(RED).a("foo bar baz").reset().toString(), str);
  }

  @Test public void testRender5() {
    String str = ansi().render("@|bold Hello|@").toString();
    System.out.println(str);
    assertEquals(ansi().a(INTENSITY_BOLD).a("Hello").reset().toString(), str);
  }

  @Test public void testRenderNothing() {
    assertEquals("foo", render("foo"));
  }

  @Test public void testRenderInvalidMissingEnd() {
    String str = render("@|bold foo");
    assertEquals("@|bold foo", str);
  }

  @Test public void testRenderInvalidEndBeforeStart() {
    assertThrows(IllegalArgumentException.class, () -> render("@|@"));
  }

  @Test public void testRenderInvalidMissingText() {
    String str = render("@|bold|@");
    assertEquals("@|bold|@", str);
  }
}