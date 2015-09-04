package org.openzal.zal.lib;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ExtensionJarValidatorTest
{
  private ExtensionJarValidator mValidator;
  private JarAccessor           mJar;

  @Before
  public void setup() throws Exception
  {
    mJar = mock(JarAccessor.class);

    mValidator = new ExtensionJarValidator();
  }

  @Test
  public void validating_same_zal_version() throws Exception
  {
    when(mJar.getAttributeInManifest("ZAL-Required-Version")).thenReturn("1.2.3");

    mValidator.validate(mJar, new Version(1,2,3));
  }

  @Test
  public void validating_greater_zal_micro_version() throws Exception
  {
    when(mJar.getAttributeInManifest("ZAL-Required-Version")).thenReturn("1.2.3");

    mValidator.validate(mJar, new Version(1,2,4));
  }

  @Test ( expected = RuntimeException.class )
  public void validating_lower_zal_micro_version_fails() throws Exception
  {
    when(mJar.getAttributeInManifest("ZAL-Required-Version")).thenReturn("1.2.3");

    mValidator.validate(mJar, new Version(1,2,2));
  }

  @Test ( expected = RuntimeException.class )
  public void validating_different_zal_minor_version_fails() throws Exception
  {
    when(mJar.getAttributeInManifest("ZAL-Required-Version")).thenReturn("1.2.3");

    mValidator.validate(mJar, new Version(1,3,3));
  }

  @Test ( expected = RuntimeException.class )
  public void validating_different_zal_major_version_fails() throws Exception
  {
    when(mJar.getAttributeInManifest("ZAL-Required-Version")).thenReturn("1.2.3");

    mValidator.validate(mJar, new Version(2,2,3));
  }

  @Test ( expected = RuntimeException.class )
  public void validating_different_zal_major_and_minor_version_fails() throws Exception
  {
    when(mJar.getAttributeInManifest("ZAL-Required-Version")).thenReturn("1.2.3");

    mValidator.validate(mJar, new Version(2,3,3));
  }

  @Test
  public void validating_doesnt_force_digest_validation() throws Exception
  {
    mValidator.validate(mJar, new Version(1,2,3));
    verify(mJar, times(1)).validateDigest(false);
  }

  @Test
  public void validating_force_digest_validation() throws Exception
  {
    mValidator.validateForceDigestValidation(mJar, new Version(1,2,3));
    verify(mJar, times(1)).validateDigest(true);
  }
}