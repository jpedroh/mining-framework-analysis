package com.github.javafaker;
import static com.github.javafaker.matchers.MatchesRegularExpression.matchesRegularExpression;
import org.junit.Test;
import static org.junit.Assert.assertThat;

public class WitcherTest extends AbstractFakerTest {
  @Test public void testCharacter() {
    assertThat(faker.witcher().character(), matchesRegularExpression("[A-Za-z\' -\u00e9\u00fa\u00ef]+"));
  }

  @Test public void testWitcher() {
    assertThat(faker.witcher().witcher(), matchesRegularExpression("[A-Za-z -\u00eb\u00fa\u00ef]+"));
  }

  @Test public void testSchool() {
    assertThat(faker.witcher().school(), matchesRegularExpression("[A-Za-z]+"));
  }

  @Test public void testLocation() {
    assertThat(faker.witcher().location(), matchesRegularExpression("[A-Za-z -\u00e1\u00e2\u00e9]+"));
  }

  @Test public void testQuote() {
    assertThat(faker.witcher().quote(), matchesRegularExpression("[-A-Za-z0-9 \u2014;\u2026\\?\\!\\.\u2019\u2018\'\u201d\u201c,\\[\\]]+"));
  }

  @Test public void testMonster() {
    assertThat(faker.witcher().monster(), matchesRegularExpression("[A-Za-z -]+"));
  }
}