package jenkins.plugins.ui_samples;
import com.google.common.collect.ImmutableList;
import hudson.Extension;
import hudson.XmlFile;
import hudson.model.AbstractDescribableImpl;
import hudson.model.Descriptor;
import hudson.util.FormApply;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import javax.servlet.ServletException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Extension public final class HeteroList extends UISample {
  @Override public String getDescription() {
    return "Show a heterogeneous list of subitems with different data bindings for radio buttons and checkboxes";
  }

  @Extension public static final class DescriptorImpl extends UISampleDescriptor {
  }

  public XmlFile getConfigFile() {
    return new XmlFile(new File(Jenkins.getInstance().getRootDir(), "stuff.xml"));
  }

  private Config config;

  public HeteroList() throws IOException {
    XmlFile xml = getConfigFile();
    if (xml.exists()) {
      xml.unmarshal(this);
    }
  }

  public Config getConfig() {
    return config;
  }

  public void setConfig(Config config) {
    this.config = config;
  }

  public HttpResponse doConfigSubmit(StaplerRequest req) throws ServletException, IOException {
    config = null;
    req.bindJSON(this, req.getSubmittedForm());
    getConfigFile().write(this);
    return FormApply.success(".");
  }

  public static final class Config extends AbstractDescribableImpl<Config> {
    private final List<Entry> entries;

    @DataBoundConstructor public Config(List<Entry> entries) {
      this.entries = entries != null ? new ArrayList<Entry>(entries) : Collections.<Entry>emptyList();
    }

    public List<Entry> getEntries() {
      return Collections.unmodifiableList(entries);
    }

    @Extension public static class DescriptorImpl extends Descriptor<Config> {
    }
  }

  public static abstract class Entry extends AbstractDescribableImpl<Entry> {
  }

  public static final class SimpleEntry extends Entry {
    private final String text;

    @DataBoundConstructor public SimpleEntry(String text) {
      this.text = text;
    }

    public String getText() {
      return text;
    }

    @Extension public static class DescriptorImpl extends Descriptor<Entry> {
      @Override public String getDisplayName() {
        return "Simple Entry";
      }
    }
  }

  public static final class ChoiceEntry extends Entry {
    private final String choice;

    @DataBoundConstructor public ChoiceEntry(String choice) {
      this.choice = choice;
    }

    public String getChoice() {
      return choice;
    }

    @Extension public static class DescriptorImpl extends Descriptor<Entry> {
      @Override public String getDisplayName() {
        return "Choice Entry";
      }

      public ListBoxModel doFillChoiceItems() {
        return new ListBoxModel().add("good").add("bad").add("ugly");
      }
    }
  }

  public static final class HeteroRadioEntry extends Entry {
    private final Entry entry;

    @DataBoundConstructor public HeteroRadioEntry(Entry entry) {
      this.entry = entry;
    }

    public Entry getEntry() {
      return entry;
    }

    @Extension public static class DescriptorImpl extends Descriptor<Entry> {
      @Override public String getDisplayName() {
        return "Hetero-Radio";
      }

      public List<Descriptor> getEntryDescriptors() {
        Jenkins jenkins = Jenkins.get();
        return ImmutableList.of(jenkins.getDescriptorOrDie(ChoiceEntry.class), jenkins.getDescriptorOrDie(SimpleEntry.class));
      }
    }
  }
}