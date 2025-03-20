package org.cts.registry;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This class parse the nad27 file available in the resources package. It returns
 * the coresponding parameters to a code specified in the registry file.
 *
 * @author Erwan Bocher
 */
public class Nad27Registry extends AbstractProjRegistry {
  static final Pattern NAD27_REGEX = Pattern.compile("\\s+");

  @Override public String getRegistryName() {
    return "nad27";
  }

  @Override public Map<String, String> getParameters(String code) throws RegistryException {
    try {
      Map<String, String> crsParameters = projParser.readParameters(code, NAD27_REGEX);
      return crsParameters;
    } catch (IOException ex) {
      throw new RegistryException("Cannot load the NAD27 registry", ex);
    }
  }

  @Override public Set<String> getSupportedCodes() throws RegistryException {
    try {
      return projParser.getSupportedCodes(NAD27_REGEX);
    } catch (IOException ex) {
      throw new RegistryException("Cannot load the NAD27 registry", ex);
    }
  }
}