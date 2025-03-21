package com.googlecode.wicket.kendo.ui.form.autocomplete;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.CallbackParameter;
import org.apache.wicket.util.lang.Args;
import com.googlecode.wicket.jquery.core.JQueryEvent;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.core.ajax.IJQueryAjaxAware;
import com.googlecode.wicket.jquery.core.ajax.JQueryAjaxBehavior;
import com.googlecode.wicket.jquery.core.utils.RequestCycleUtils;
import com.googlecode.wicket.kendo.ui.KendoDataSource;
import com.googlecode.wicket.kendo.ui.KendoUIBehavior;

/**
 * Provides a {@value #METHOD} behavior
 *
 * @author Sebastien Briquet - sebfz1
 *
 */
public abstract class AutoCompleteBehavior extends KendoUIBehavior implements IJQueryAjaxAware {
  private static final long serialVersionUID = 1L;

  public static final String METHOD = "kendoAutoComplete";

  private final IAutoCompleteListener listener;

  private JQueryAjaxBehavior onSelectAjaxBehavior = null;

  private KendoDataSource dataSource;

  /**
	 * Constructor
	 *
	 * @param selector the html selector (ie: "#myId")
	 * @param listener the {@link IAutoCompleteListener}
	 */
  public AutoCompleteBehavior(String selector, IAutoCompleteListener listener) {
    this(selector, new Options(), listener);
  }

  /**
	 * Constructor
	 *
	 * @param selector the html selector (ie: "#myId")
	 * @param options the {@link Options}
	 * @param listener the {@link IAutoCompleteListener}
	 */
  public AutoCompleteBehavior(String selector, Options options, IAutoCompleteListener listener) {
    super(selector, METHOD, options);
    this.listener = Args.notNull(listener, "listener");
  }

  @Override public void bind(Component component) {
    super.bind(component);
    this.dataSource = new KendoDataSource("datasource" + this.selector);
    this.dataSource.set("serverFiltering", true);
    this.add(this.dataSource);
    this.onSelectAjaxBehavior = this.newOnSelectAjaxBehavior(this);
    component.add(this.onSelectAjaxBehavior);
  }

  @Override public boolean isEnabled(Component component) {
    return component.isEnabledInHierarchy();
  }

  protected abstract CharSequence getDataSourceUrl();

  @Override public void onConfigure(Component component) {
    super.onConfigure(component);
    this.setOption("autoBind", true);
    this.setOption("dataSource", this.dataSource.getName());
    this.setOption("select", this.onSelectAjaxBehavior.getCallbackFunction());
    if (this.isEnabled(component)) {
      this.dataSource.setTransportRead(Options.asString(this.getDataSourceUrl()));
    }
  }

  @Override public void onAjax(AjaxRequestTarget target, JQueryEvent event) {
    if (event instanceof SelectEvent) {
      this.listener.onSelect(target, ((SelectEvent) event).getIndex());
    }
  }

  /**
	 * Gets a new {@link JQueryAjaxBehavior} that will be wired to the 'select' event
	 *
	 * @param source the {@link IJQueryAjaxAware}
	 * @return a new {@link OnSelectAjaxBehavior} by default
	 */
  protected JQueryAjaxBehavior newOnSelectAjaxBehavior(IJQueryAjaxAware source) {
    return new OnSelectAjaxBehavior(source);
  }

  protected static class OnSelectAjaxBehavior extends JQueryAjaxBehavior {
    private static final long serialVersionUID = 1L;

    public OnSelectAjaxBehavior(IJQueryAjaxAware source) {
      super(source);
    }

    @Override protected CallbackParameter[] getCallbackParameters() {
      return new CallbackParameter[] { CallbackParameter.context("e"), CallbackParameter.resolved("index", "e.item.index()"), CallbackParameter.resolved("value", "e.item.text") };
    }

    @Override protected JQueryEvent newEvent() {
      return new SelectEvent();
    }
  }

  protected static class SelectEvent extends JQueryEvent {
    private final int index;

    private final String value;

    public SelectEvent() {
      this.index = RequestCycleUtils.getQueryParameterValue("index").toInt(-1);
      this.value = RequestCycleUtils.getQueryParameterValue("value").toString();
    }

    public int getIndex() {
      return this.index;
    }

    public String getValue() {
      return this.value;
    }
  }
}