package com.github.javafaker;

import org.junit.Test;

import static com.github.javafaker.matchers.MatchesRegularExpression.matchesRegularExpression;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;


public class NameTest  extends AbstractFakerTest {

    @Test
    public void testName() {
        assertThat(faker.name().name(), matchesRegularExpression("([\\w']+\\.?( )?){2,3}"));
    }

    @Test
    public void testNameWithMiddle() {
        assertThat(faker.name().nameWithMiddle(), matchesRegularExpression("([\\w']+\\.?( )?){3,4}"));
    }

    @Test
    public void testFullName() {
        assertThat(faker.name().fullName(), matchesRegularExpression("([\\w']+\\.?( )?){2,4}"));
    }

    @Test
    public void testFirstName() {
        assertThat(faker.name().firstName(), matchesRegularExpression("\\w+"));
    }

    @Test
    public void testFirstNameLength() {
        for (int i = 2; i < 12; i++) {
            assertThat(faker.name().firstName(i), matchesRegularExpression("\\w{" + i + "}"));
        }
    }
    
    @Test
    public void testFirstNameGivenLength() {
        for (int i = 2; i < 12; i++) {
            String firstName = faker.name().firstName(7);
            System.out.println("First Name: \"" + firstName + "\".");
            assertThat(firstName, matchesRegularExpression("\\w{7}"));
        }
    }
    
    @Test
    public void testLastName() {
        assertThat(faker.name().lastName(), matchesRegularExpression("[A-Za-z']+"));
    }

    @Test
    public void testLastNameLength() {
        for (int i = 4; i < 14; i++) {
            assertThat(faker.name().lastName(i), matchesRegularExpression("[A-Za-z']{" + i +"}"));
        }
    }
    
    @Test
    public void testPrefix() {
        assertThat(faker.name().prefix(), matchesRegularExpression("\\w+\\.?"));
    }

    @Test
    public void testSuffix() {
        assertThat(faker.name().suffix(), matchesRegularExpression("\\w+\\.?"));
    }

    @Test
    public void testTitle() {
        assertThat(faker.name().title(), matchesRegularExpression("(\\w+\\.?( )?){3}"));
    }

    @Test
    public void testUsername() {
        assertThat(faker.name().username(), matchesRegularExpression("^(\\w+)\\.(\\w+)$"));
    }

    @Test
    public void testUsernameWithSpaces() {
        final Name name = spy(new Name(faker));
        doReturn("Compound Name").when(name).firstName();
        doReturn(name).when(faker).name();
        assertThat(faker.name().username(), matchesRegularExpression("^(\\w+)\\.(\\w+)$"));
    }

}
