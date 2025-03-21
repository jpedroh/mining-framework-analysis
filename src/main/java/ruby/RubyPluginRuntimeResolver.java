package ruby;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import jenkins.model.Jenkins;
import org.jenkinsci.jruby.RubyRuntimeResolver;
import org.jruby.Ruby;
import org.jruby.embed.ScriptingContainer;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * Each Ruby plugin gets its own {@link ScriptingContainer}, so put the plugin name
 * as an attribute so that we can correctly unmarshal it back.
 */
class RubyPluginRuntimeResolver extends RubyRuntimeResolver {
  public RubyPluginRuntimeResolver() {
  }

  @Override public Ruby unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
    String pluginid = reader.getAttribute("pluginid");
    RubyPlugin plugin = (RubyPlugin) Jenkins.
<<<<<<< /usr/src/app/output/jenkinsci/ruby-runtime-plugin/39375b570201fcf8b133343bea4d124bf7351210/src/main/java/ruby/RubyPluginRuntimeResolver.java/left.java
    getActiveInstance()
=======
    get()
>>>>>>> /usr/src/app/output/jenkinsci/ruby-runtime-plugin/39375b570201fcf8b133343bea4d124bf7351210/src/main/java/ruby/RubyPluginRuntimeResolver.java/right.java
    .getPlugin(pluginid);
    if (plugin == null) {
      throw new 
<<<<<<< /usr/src/app/output/jenkinsci/ruby-runtime-plugin/39375b570201fcf8b133343bea4d124bf7351210/src/main/java/ruby/RubyPluginRuntimeResolver.java/left.java
      IllegalStateException
=======
      XStreamException
>>>>>>> /usr/src/app/output/jenkinsci/ruby-runtime-plugin/39375b570201fcf8b133343bea4d124bf7351210/src/main/java/ruby/RubyPluginRuntimeResolver.java/right.java
      (
<<<<<<< /usr/src/app/output/jenkinsci/ruby-runtime-plugin/39375b570201fcf8b133343bea4d124bf7351210/src/main/java/ruby/RubyPluginRuntimeResolver.java/left.java
      "Cannot find Ruby plugin with id="
=======
      "no such plugin "
>>>>>>> /usr/src/app/output/jenkinsci/ruby-runtime-plugin/39375b570201fcf8b133343bea4d124bf7351210/src/main/java/ruby/RubyPluginRuntimeResolver.java/right.java
       + pluginid);
    }
    return plugin.getScriptingContainer().getProvider().getRuntime();
  }

  @Override public void marshal(IRubyObject o, HierarchicalStreamWriter writer, MarshallingContext context) {
    RubyPlugin p = RubyPlugin.from(o.getRuntime());
    if (p != null) {
      writer.addAttribute("pluginid", p.getWrapper().getShortName());
    }
  }
}