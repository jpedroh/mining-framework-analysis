package fiji.plugin.trackmate.providers;
import fiji.plugin.trackmate.visualization.ViewFactory;

public class ViewProvider extends AbstractProvider<ViewFactory> {
  public ViewProvider() {
    super(ViewFactory.class);
  }

  public static void main(final String[] args) {
    final ViewProvider provider = new ViewProvider();
    System.out.println(provider.echo());
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  protected void registerViews() {
    final Context context = TMUtils.getContext();
    final LogService log = context.getService(LogService.class);
    final PluginService pluginService = context.getService(PluginService.class);
    final List<PluginInfo<ViewFactory>> infos = pluginService.getPluginsOfType(ViewFactory.class);
    for (final PluginInfo<ViewFactory> info : infos) {
      try {
        final ViewFactory view = info.createInstance();
        registerView(view.getKey(), view, info.isVisible());
      } catch (final InstantiableException e) {
        log.error("Could not instantiate " + info.getClassName(), e);
      }
    }
  }
>>>>>>> /usr/src/app/output/fiji/trackmate/5e16de8e9ffcc87470a5817ae5ae92ecbf6ab30b/src/main/java/fiji/plugin/trackmate/providers/ViewProvider.java/right.java
}