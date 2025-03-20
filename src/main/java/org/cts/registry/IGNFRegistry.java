package org.cts.registry;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * This class parse the ignf file available in the resources package. It returns
 * the coresponding parameters to a code specified in the registry file.
 *
 * @author Erwan Bocher
 */
public class IGNFRegistry extends AbstractProjRegistry {
  static final Pattern IGNF_REGEX = Pattern.compile("[ ]\\+|\\s<>");

  @Override public String getRegistryName() {
    return "ignf";
  }

  @Override public Map<String, String> getParameters(String code) throws RegistryException {
    try {
      Map<String, String> crsParameters = projParser.readParameters(code, IGNF_REGEX);
      return crsParameters;
    } catch (IOException ex) {
      throw new RegistryException("Cannot load the IGNF registry", ex);
    }
  }

  @Override public Set<String> getSupportedCodes() throws RegistryException {
    try {
      return projParser.getSupportedCodes(IGNF_REGEX);
    } catch (IOException ex) {
      throw new RegistryException("Cannot load the IGNF registry", ex);
    }
  }
}