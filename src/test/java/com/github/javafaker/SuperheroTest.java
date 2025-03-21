package com.github.javafaker;

import org.junit.Before;
import org.junit.Test;

import static com.github.javafaker.matchers.MatchesRegularExpression.matchesRegularExpression;
import static org.hamcrest.MatcherAssert.assertThat;

public class SuperheroTest {
    private Faker faker;

    @Before
    public void before() {
        faker = new Faker();
    }

    @Test
    public void testName() {
<<<<<<< /usr/src/app/output/dius/java-faker/fc62ba8c8774049f009ced1a75febfccf756d835/src/test/java/com/github/javafaker/SuperheroTest.java/left.java
        assertThat(faker.superhero().name(), matchesRegularExpression("[A-Za-z -/]+"));
||||||| /usr/src/app/output/dius/java-faker/fc62ba8c8774049f009ced1a75febfccf756d835/src/test/java/com/github/javafaker/SuperheroTest.java/base.java
        assertThat(faker.superhero().name(), matchesRegularExpression("[A-Za-z -]+"));
=======
        assertThat(faker.superhero().name(), matchesRegularExpression("[A-Za-z' -]+"));
>>>>>>> /usr/src/app/output/dius/java-faker/fc62ba8c8774049f009ced1a75febfccf756d835/src/test/java/com/github/javafaker/SuperheroTest.java/right.java
    }

    @Test
    public void testPrefix() {
        assertThat(faker.superhero().prefix(), matchesRegularExpression("[A-Za-z -]+"));
    }

    @Test
    public void testSuffix() {
        assertThat(faker.superhero().suffix(), matchesRegularExpression("[A-Za-z -]+"));
    }

    @Test
    public void testPower() {
        assertThat(faker.superhero().power(), matchesRegularExpression("[A-Za-z/ -]+"));
    }

    @Test
    public void testDescriptor() {
        assertThat(faker.superhero().descriptor(), matchesRegularExpression("[A-Za-z' -]+"));
    }
}
