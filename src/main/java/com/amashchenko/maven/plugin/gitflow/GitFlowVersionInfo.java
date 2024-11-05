package com.amashchenko.maven.plugin.gitflow;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.release.policy.PolicyException;
import org.apache.maven.shared.release.policy.version.VersionPolicy;
import org.apache.maven.shared.release.policy.version.VersionPolicyRequest;
import org.apache.maven.shared.release.versions.DefaultVersionInfo;
import org.apache.maven.shared.release.versions.VersionParseException;
import org.codehaus.plexus.util.StringUtils;

public class GitFlowVersionInfo extends DefaultVersionInfo {
  private final VersionPolicy versionPolicy;

  public GitFlowVersionInfo(final String version, final VersionPolicy versionPolicy) throws VersionParseException {
    super(version);
    this.versionPolicy = versionPolicy;
  }

  public GitFlowVersionInfo digitsVersionInfo() throws VersionParseException {
    return new GitFlowVersionInfo(joinDigitString(getDigits()), versionPolicy);
  }

  public static boolean isValidVersion(final String version) {
    return StringUtils.isNotBlank(version) && (ALTERNATE_PATTERN.matcher(version).matches() || STANDARD_PATTERN.matcher(version).matches());
  }

  @Override public String getReleaseVersionString() {
    if (versionPolicy != null) {
      try {
        VersionPolicyRequest request = new VersionPolicyRequest().setVersion(this.toString());
        return versionPolicy.getReleaseVersion(request).getVersion();
      } catch (PolicyException ex) {
        throw new RuntimeException("Unable to get release version from policy.", ex);
      } catch (VersionParseException ex) {
        throw new RuntimeException("Unable to get release version from policy.", ex);
      }
    }
    return super.getReleaseVersionString();
  }

  public String nextSnapshotVersion() {
    return nextSnapshotVersion(null);
  }

  public String nextSnapshotVersion(final Integer index) {
    if (versionPolicy != null) {
      try {
        VersionPolicyRequest request = new VersionPolicyRequest().setVersion(this.toString());
        return versionPolicy.getDevelopmentVersion(request).getVersion();
      } catch (PolicyException ex) {
        throw new RuntimeException("Unable to get development version from policy.", ex);
      } catch (VersionParseException ex) {
        throw new RuntimeException("Unable to get development version from policy.", ex);
      }
    }
    return nextVersion(index, true);
  }

  public String featureVersion(final String featureName) {
    String version = toString();
    if (featureName != null) {
      version = getReleaseVersionString() + "-" + featureName + (isSnapshot() ? "-" + Artifact.SNAPSHOT_VERSION : "");
    }
    return version;
  }

  private String nextVersion(final Integer index, boolean snapshot) {
    List<String> digits = getDigits();
    String nextVersion = null;
    if (digits != null) {
      if (index != null && index >= 0 && index < digits.size()) {
        int origDigitsLength = joinDigitString(digits).length();
        digits.set(index, incrementVersionString(digits.get(index)));
        for (int i = index + 1; i < digits.size(); i++) {
          digits.set(i, "0");
        }
        String digitsStr = joinDigitString(digits);
        nextVersion = digitsStr + (snapshot ? getSnapshotVersionString().substring(origDigitsLength) : getReleaseVersionString().substring(origDigitsLength));
      } else {
        nextVersion = snapshot ? getNextVersion().getSnapshotVersionString() : getNextVersion().getReleaseVersionString();
      }
    } else {
      nextVersion = snapshot ? getSnapshotVersionString() : getReleaseVersionString();
    }
    return nextVersion;
  }

  public String hotfixVersion(boolean preserveSnapshot, final Integer index) {
    return nextVersion(index, (preserveSnapshot && isSnapshot()));
  }
}