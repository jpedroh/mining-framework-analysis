package org.cts.registry;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This class parse the nad83 file available in the resources package. It
 * returns the coresponding parameters to a code specified in the registry file.
 *
 * @author Erwan Bocher
 */
public class Nad83Registry extends AbstractProjRegistry {
  static final Pattern NAD83_REGEX = Pattern.compile("\\s+");

  @Override public String getRegistryName() {
    return "nad83";
  }

  @Override public Map<String, String> getParameters(String code) throws RegistryException {
    try {
      Map<String, String> crsParameters = projParser.readParameters(code, NAD83_REGEX);
      return crsParameters;
    } catch (IOException ex) {
      throw new RegistryException("Cannot load the NAD83 registry", ex);
    }
  }

  @Override public Set<String> getSupportedCodes() throws RegistryException {
    try {
      return projParser.getSupportedCodes(NAD83_REGEX);
    } catch (IOException ex) {
      throw new RegistryException("Cannot load the NAD83 registry", ex);
    }
  }
}