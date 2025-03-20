package com.cloudbees.plugins.credentials;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.Util;
import hudson.model.AbstractDescribableImpl;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 * A filter of {@link CredentialsProvider} instances used by {@link CredentialsProviderManager} to determine which
 * providers are active.
 *
 * @since 2.0
 */
public abstract class CredentialsProviderFilter extends AbstractDescribableImpl<CredentialsProviderFilter> implements Serializable, ExtensionPoint {
  /**
     * Ensure consistent serialization.
     */
  private static final long serialVersionUID = 1L;

  /**
     * Returns {@code true} if and only if the supplied {@link CredentialsProvider} is permitted to be active.
     *
     * @param provider the {@link CredentialsProvider} to check.
     * @return {@code true} if and only if the supplied {@link CredentialsProvider} is permitted to be active.
     */
  public abstract boolean filter(CredentialsProvider provider);

  /**
     * {@inheritDoc}
     */
  @Override public CredentialsProviderFilterDescriptor getDescriptor() {
    return (CredentialsProviderFilterDescriptor) super.getDescriptor();
  }

  /**
     * {@inheritDoc}
     */
  @Override public abstract int hashCode();

  /**
     * {@inheritDoc}
     */
  @Override public abstract boolean equals(Object obj);

  /**
     * {@inheritDoc}
     */
  @Override public abstract String toString();

  public static class None extends CredentialsProviderFilter {
    /**
         * Ensure consistent serialization.
         */
    private static final long serialVersionUID = 1L;

    /**
         * Our constructor.
         */
    @DataBoundConstructor public None() {
    }

    /**
         * {@inheritDoc}
         */
    @Override public boolean filter(CredentialsProvider provider) {
      return true;
    }

    /**
         * {@inheritDoc}
         */
    @Override public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      return o != null && getClass() == o.getClass();
    }

    /**
         * {@inheritDoc}
         */
    @Override public int hashCode() {
      return None.class.hashCode();
    }

    /**
         * {@inheritDoc}
         */
    @Override public String toString() {
      return "None{}";
    }

    @Extension public static class DescriptorImpl extends CredentialsProviderFilterDescriptor {
      /**
             * {@inheritDoc}
             */
      @NonNull @Override public String getDisplayName() {
        return Messages.CredentialsProviderFilter_None_DisplayName();
      }
    }
  }

  public static class Includes extends CredentialsProviderFilter {
    /**
         * Ensure consistent serialization.
         */
    private static final long serialVersionUID = 1L;

    /**
         * The set of classes that will be allowed.
         */
    @NonNull private final Set<String> classNames;

    /**
         * Our constructor.
         *
         * @param classNames the list of allowed class names.
         */
    @DataBoundConstructor public Includes(@CheckForNull List<String> classNames) {
      this.classNames = new LinkedHashSet<>(Util.fixNull(classNames));
    }

    /**
         * {@inheritDoc}
         */
    @Override public boolean filter(CredentialsProvider provider) {
      return classNames.contains(provider.getId());
    }

    /**
         * Returns the list of allowed {@link Class#getName()}.
         *
         * @return the list of allowed {@link Class#getName()}.
         */
    @NonNull public List<String> getClassNames() {
      return new ArrayList<>(classNames);
    }

    /**
         * {@inheritDoc}
         */
    @Override public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Includes includes = (Includes) o;
      return classNames.equals(includes.classNames);
    }

    /**
         * {@inheritDoc}
         */
    @Override public int hashCode() {
      return classNames.hashCode();
    }

    /**
         * {@inheritDoc}
         */
    @Override public String toString() {
      return "Includes{" + "classes=" + getClassNames() + '}';
    }

    @Extension public static class DescriptorImpl extends CredentialsProviderFilterDescriptor {
      /**
             * {@inheritDoc}
             */
      @NonNull @Override public String getDisplayName() {
        return Messages.CredentialsProviderFilter_Includes_DisplayName();
      }

      /**
             * Gets the full list of available providers without any filtering.
             *
             * @return the full list of available providers without any filtering.
             */
      @SuppressWarnings(value = { "unused" }) @Restricted(value = NoExternalUse.class) public List<CredentialsProvider> getProviderDescriptors() {
        return ExtensionList.lookup(CredentialsProvider.class);
      }
    }
  }

  public static class Excludes extends CredentialsProviderFilter {
    /**
         * Ensure consistent serialization.
         */
    private static final long serialVersionUID = 1L;

    /**
         * The set of classes that will not be allowed.
         */
    @NonNull private final Set<String> classNames;

    /**
         * Our constructor.
         *
         * @param classNames the excluded list of class names.
         */
    @DataBoundConstructor public Excludes(@CheckForNull List<String> classNames) {
      this.classNames = new LinkedHashSet<>(Util.fixNull(classNames));
    }

    /**
         * {@inheritDoc}
         */
    @Override public boolean filter(CredentialsProvider provider) {
      return !classNames.contains(provider.getId());
    }

    /**
         * Returns the list of banned {@link Class#getName()}.
         *
         * @return the list of banned {@link Class#getName()}.
         */
    @NonNull public List<String> getClassNames() {
      return new ArrayList<>(classNames);
    }

    /**
         * {@inheritDoc}
         */
    @Override public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Excludes excludes = (Excludes) o;
      return classNames.equals(excludes.classNames);
    }

    /**
         * {@inheritDoc}
         */
    @Override public int hashCode() {
      return classNames.hashCode();
    }

    /**
         * {@inheritDoc}
         */
    @Override public String toString() {
      return "Excludes{" + "classes=" + getClassNames() + '}';
    }

    @Extension public static class DescriptorImpl extends CredentialsProviderFilterDescriptor {
      /**
             * {@inheritDoc}
             */
      @NonNull @Override public String getDisplayName() {
        return Messages.CredentialsProviderFilter_Excludes_DisplayName();
      }

      /**
             * Gets the full list of available providers without any filtering.
             *
             * @return the full list of available providers without any filtering.
             */
      @SuppressWarnings(value = { "unused" }) @Restricted(value = NoExternalUse.class) public List<CredentialsProvider> getProviderDescriptors() {
        return ExtensionList.lookup(CredentialsProvider.class);
      }
    }
  }
}