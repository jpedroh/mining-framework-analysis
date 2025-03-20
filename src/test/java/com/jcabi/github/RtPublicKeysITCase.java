package com.jcabi.github;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assume;
import org.junit.Test;

/**
 * Test case for {@link RtPublicKeys}.
 *
 * @author Carlos Miranda (miranda.cma@gmail.com)
 * @version $Id$
 * @todo #551 RtPublicKeysITCase is disabled since it doesn't work
 *  with real Github account. Let's fix it and remove all
 *  Ignore annotations from all its methods.
 */
public class RtPublicKeysITCase {
  /**
     * The test rsa key.
     */
  private static final String RSAKEY = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDS+TF7+bae4UKj6nec1oipiP9Ysc6mBPszB80z13tMZBlsPCOiLVAMO2ER/wpnKHd/VylmYr5c6wc3kSj88846VHUhQDN7fLd/km06KTdW4+9db7HBfvr0063eDdi1lg8jlnccegeeqKsG39+iVQban7ugcPyJtjQE9k7JjYBT+SOgupWkYPVO+5Z3xF6VJL8gUTIMgoovgTabFx60t5h5UPtNaGbdcSlHhLOlWn8I7tHvwbYdhZVqlCC450rieXo8PpjndG3crcuHPZPDVSSXyqRpguIxVEVjXd3B/0vrhXJQJC4u0ukOOytLNL6Gzz3oK7SIB0mqWJ4Mo0Wp+zeX jac.wshmstr@gmail.com";

  /**
     * RtPublicKeys should be able to retrieve its keys.
     *
     * @throws Exception If a problem occurs.
     */
  @Test public final void retrievesKeys() throws Exception {
    final PublicKeys keys = this.keys();
    final PublicKey key = keys.create("key", RSAKEY);
    MatcherAssert.assertThat(keys.iterate(), Matchers.hasItem(key));
    keys.remove(key.number());
  }

  /**
     * Create and return PublicKeys object to test.
     * @return PublicKeys
     */
  private PublicKeys keys() {
    final String key = System.getProperty("failsafe.github.key");
    Assume.assumeThat(key, Matchers.notNullValue());
    return new RtGithub(key).users().self().keys();
  }

  /**
     * RtPublicKeys should be able to retrieve a single key.
     *
     * @throws Exception If a problem occurs.
     */
  @Test public final void retrievesSingleKey() throws Exception {
    final PublicKeys keys = this.keys();
    final PublicKey key = keys.create("Title", "Key");
    MatcherAssert.assertThat(keys.get(key.number()), Matchers.equalTo(key));
    keys.remove(key.number());
  }

  /**
     * RtPublicKeys should be able to remove a key.
     *
     * @throws Exception If a problem occurs.
     */
  @Test public final void removesKey() throws Exception {
    final PublicKeys keys = this.keys();
    final PublicKey key = keys.create("", "");
    MatcherAssert.assertThat(keys.iterate(), Matchers.hasItem(key));
    keys.remove(key.number());
    MatcherAssert.assertThat(keys.iterate(), Matchers.not(Matchers.hasItem(key)));
  }

  /**
     * RtPublicKeys should be able to create a key.
     *
     * @throws Exception If a problem occurs.
     */
  @Test @Ignore public final void createsKey() throws Exception {
    final PublicKeys keys = this.keys();
    final PublicKey key = keys.create("rsa", RSAKEY);
    try {
      MatcherAssert.assertThat(keys.iterate(), Matchers.hasItem(key));
      MatcherAssert.assertThat(key.user(), Matchers.equalTo(keys.user()));
    }  finally {
      keys.remove(key.number());
    }
    MatcherAssert.assertThat(keys.iterate(), Matchers.not(Matchers.hasItem(key)));
  }
}