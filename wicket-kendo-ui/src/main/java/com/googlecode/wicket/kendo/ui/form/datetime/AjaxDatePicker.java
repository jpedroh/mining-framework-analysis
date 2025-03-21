package com.googlecode.wicket.kendo.ui.form.datetime;
import java.util.Date;
import java.util.Locale;
import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.JQueryEvent;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.core.ajax.IJQueryAjaxAware;
import com.googlecode.wicket.jquery.core.ajax.JQueryAjaxBehavior;
import com.googlecode.wicket.jquery.core.ajax.JQueryAjaxPostBehavior;
import com.googlecode.wicket.jquery.core.event.IValueChangedListener;
import com.googlecode.wicket.kendo.ui.KendoUIBehavior;
import com.googlecode.wicket.kendo.ui.ajax.OnChangeAjaxBehavior;

/**
 * Provides a Kendo UI date-picker based on a {@link DateTextField}<br/>
 * This ajax version will post the {@link Component}, using a {@link JQueryAjaxPostBehavior}, when the 'change' javascript method is called.
 *
 * @author Sebastien Briquet - sebfz1
 */
public class AjaxDatePicker extends DatePicker implements IJQueryAjaxAware, IValueChangedListener {
  private static final long serialVersionUID = 1L;

  /**
	 * Constructor
	 *
	 * @param id the markup id
	 */
  public AjaxDatePicker(String id) {
    super(id);
  }

  /**
	 * Constructor
	 *
	 * @param id the markup id
	 * @param options the {@link Options}
	 */
  public AjaxDatePicker(String id, Options options) {
    super(id, options);
  }

  /**
	 * Constructor
	 *
	 * @param id the markup id
	 * @param pattern a {@code SimpleDateFormat} pattern
	 */
  public AjaxDatePicker(String id, String pattern) {
    super(id, pattern);
  }

  /**
	 * Main constructor
	 *
	 * @param id the markup id
	 * @param pattern a {@code SimpleDateFormat} pattern
	 * @param options the {@link Options}
	 */
  public AjaxDatePicker(String id, String pattern, Options options) {
    super(id, pattern, options);
  }

  /**
	 * Constructor, which use {@link Locale} and Kendo UI Globalization
	 *
	 * @param id the markup id
	 * @param locale the {@link Locale}
	 */
  public AjaxDatePicker(String id, Locale locale) {
    super(id, locale);
  }

  /**
	 * Constructor, which use {@link Locale} and Kendo UI Globalization
	 *
	 * @param id the markup id
	 * @param locale the {@link Locale}
	 * @param options the {@link Options}
	 */
  public AjaxDatePicker(String id, Locale locale, Options options) {
    super(id, locale, options);
  }

  /**
	 * Constructor
	 *
	 * @param id the markup id
	 * @param model the {@link IModel}
	 */
  public AjaxDatePicker(String id, IModel<Date> model) {
    super(id, model);
  }

  /**
	 * Constructor
	 *
	 * @param id the markup id
	 * @param model the {@link IModel}
	 * @param options the {@link Options}
	 */
  public AjaxDatePicker(String id, IModel<Date> model, Options options) {
    super(id, model, options);
  }

  /**
	 * Constructor
	 *
	 * @param id the markup id
	 * @param model the {@link IModel}
	 * @param pattern a {@code SimpleDateFormat} pattern
	 */
  public AjaxDatePicker(String id, IModel<Date> model, String pattern) {
    super(id, model, pattern);
  }

  /**
	 * Main constructor
	 *
	 * @param id the markup id
	 * @param model the {@link IModel}
	 * @param pattern a {@code SimpleDateFormat} pattern
	 * @param options the {@link Options}
	 */
  public AjaxDatePicker(String id, IModel<Date> model, String pattern, Options options) {
    super(id, model, pattern, options);
  }

  /**
	 * Constructor, which use {@link Locale} and Kendo UI Globalization
	 *
	 * @param id the markup id
	 * @param model the {@link IModel}
	 * @param locale the {@link Locale}
	 */
  public AjaxDatePicker(String id, IModel<Date> model, Locale locale) {
    super(id, model, locale);
  }

  /**
	 * Constructor, which use {@link Locale} and Kendo UI Globalization
	 *
	 * @param id the markup id
	 * @param model the {@link IModel}
	 * @param locale the {@link Locale}
	 * @param options the {@link Options}
	 */
  public AjaxDatePicker(String id, IModel<Date> model, Locale locale, Options options) {
    super(id, model, locale, options);
  }

  /**
	 * {@inheritDoc} <br/>
	 * <i>Not intended to be overridden</i>
	 */
  @Override public void onAjax(AjaxRequestTarget target, JQueryEvent event) {
    this.processInput();
    this.onValueChanged(target);
  }

  @Override public void onValueChanged(AjaxRequestTarget target) {
  }

  @Override public JQueryBehavior newWidgetBehavior(String selector) {
    return new DatePickerBehavior(selector, this.options) {
      private static final long serialVersionUID = 1L;

      @Override public void onAjax(AjaxRequestTarget target, JQueryEvent event) {
        AjaxDatePicker.this.onAjax(target, event);
      }
    };
  }

  protected abstract static class DatePickerBehavior extends KendoUIBehavior implements IJQueryAjaxAware {
    private static final long serialVersionUID = 1L;

    private JQueryAjaxBehavior onChangeAjaxBehavior = null;

    /**
		 * Constructor
		 *
		 * @param selector the html selector (ie: "#myId")
		 */
    public DatePickerBehavior(String selector) {
      this(selector, new Options());
    }

    /**
		 * Constructor
		 *
		 * @param selector the html selector (ie: "#myId")
		 * @param options the {@link Options}
		 */
    public DatePickerBehavior(String selector, Options options) {
      super(selector, DatePicker.METHOD, options);
    }

    @Override public void bind(Component component) {
      super.bind(component);
      if (component instanceof FormComponent<?>) {
        this.onChangeAjaxBehavior = this.newOnChangeAjaxBehavior(this, (FormComponent<?>) component);
        component.add(this.onChangeAjaxBehavior);
      } else {
        throw new WicketRuntimeException(new IllegalArgumentException("\'component\' should be an intance of FormComponent"));
      }
    }

    @Override public void onConfigure(Component component) {
      super.onConfigure(component);
      if (this.onChangeAjaxBehavior != null) {
        this.setOption("change", this.onChangeAjaxBehavior.getCallbackFunction());
      }
    }

    /**
		 * Gets a new {@link JQueryAjaxPostBehavior} that will be wired to the 'change' event
		 *
		 * @param source the {@link IJQueryAjaxAware}
		 * @param component the bound {@link Component}
		 * @return a new {@link OnChangeAjaxBehavior} by default
		 */
    protected JQueryAjaxPostBehavior newOnChangeAjaxBehavior(IJQueryAjaxAware source, FormComponent<?> component) {
      return new OnChangeAjaxBehavior(source, component);
    }
  }
}