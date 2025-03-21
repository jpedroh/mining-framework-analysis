package com.googlecode.wicket.jquery.ui.interaction.sortable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Args;
import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.core.JQueryGenericContainer;
import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.core.utils.ListUtils;
import com.googlecode.wicket.jquery.core.utils.RequestCycleUtils;

/**
 * Provides a jQuery UI sortable {@link JQueryGenericContainer}.<br>
 * The {@code Sortable} is usually associated to an &lt;UL&gt; element.
 *
 * @param <T> the type of the model object
 * @author Sebastien Briquet - sebfz1
 *
 */
public abstract class Sortable<T extends java.lang.Object> extends JQueryGenericContainer<List<T>> implements ISortableListener<T> {
  private static final long serialVersionUID = 1L;

  protected final Options options;

  /**
	 * The {@link Sortable} that requested to be connected to this {@link Sortable}<br>
	 * In other words, the {@link Sortable} that called {@link #connectWith(Sortable)}
	 */
  private List<Sortable<T>> connectedSortable = new ArrayList<Sortable<T>>();

  /**
	 * Constructor
	 *
	 * @param id the markup id
	 * @param list the list the {@link Sortable} should observe.
	 */
  public Sortable(String id, List<T> list) {
    this(id, Model.ofList(list), new Options());
  }

  /**
	 * Constructor
	 *
	 * @param id the markup id
	 * @param list the list the {@link Sortable} should observe.
	 * @param options the {@link Options}
	 */
  public Sortable(String id, List<T> list, Options options) {
    this(id, Model.ofList(list), options);
  }

  /**
	 * Constructor
	 *
	 * @param id the markup id
	 * @param model the list the {@link Sortable} should observe.
	 */
  public Sortable(String id, IModel<List<T>> model) {
    this(id, model, new Options());
  }

  /**
	 * Constructor
	 *
	 * @param id the markup id
	 * @param model the list the {@link Sortable} should observe.
	 * @param options the {@link Options}
	 */
  public Sortable(String id, IModel<List<T>> model, Options options) {
    super(id, model);
    this.options = Args.notNull(options, "options");
  }

  @Override protected void onInitialize() {
    super.onInitialize();
    this.add(this.newListView(this.getModel()));
  }

  @Override public void onEvent(IEvent<?> event) {
    if (event.getSource() instanceof Sortable<?>) {
      AjaxRequestTarget target = RequestCycleUtils.getAjaxRequestTarget();
      if (target != null) {
        @SuppressWarnings(value = { "unchecked" }) T item = (T) event.getPayload();
        this.onRemove(target, item);
      }
    }
  }

  @Override public void onUpdate(AjaxRequestTarget target, T item, int index) {
    this.modelChanging();
    ListUtils.move(item, index, this.getModelObject());
    this.modelChanged();
  }

  @Override public void onReceive(AjaxRequestTarget target, T item, int index) {
    this.modelChanging();
    this.getModelObject().add(index, item);
    this.modelChanged();
    for (Sortable<T> connected : this.connectedSortable) {
      List<T> list = connected.getModelObject();
      if (list.contains(item)) {
        this.send(connected, Broadcast.EXACT, item);
        break;
      }
    }
  }

  @Override public void onRemove(AjaxRequestTarget target, T item) {
    this.modelChanging();
    this.getModelObject().remove(item);
    this.modelChanged();
  }

  @Override public boolean isOnReceiveEnabled() {
    return this.connectedSortable != null;
  }

  @Override public boolean isOnRemoveEnabled() {
    return false;
  }

  /**
	 * Connects with another {@link Sortable}<br>
	 * The specified {@link Sortable} will keep a reference to the caller ({@code this}).
	 *
	 * @param sortable the {@link Sortable} to connect with
	 * @return this, for chaining
	 */
  public Sortable<T> connectWith(Sortable<T> sortable) {
    Args.notNull(sortable, "sortable");
    sortable.connect(this);
    this.connect(sortable);
    return this.connectAll();
  }

  /**
	 * Sets the connected {@link Sortable} reference.<br>
	 * Supplying a non-null {@link Sortable} will activate {@link #isOnReceiveEnabled()}
	 *
	 * @param sortable the {@link Sortable}
	 * @see #isOnReceiveEnabled()
	 */
  private void connect(Sortable<T> sortable) {
    this.connectedSortable.add(sortable);
  }

  private Sortable<T> connectAll() {
    List<String> selectors = new ArrayList<>();
    for (Sortable<T> connection : this.connectedSortable) {
      selectors.add(JQueryWidget.getSelector(connection));
    }
    this.options.set("connectWith", Options.asString(selectors));
    return this;
  }

  /**
	 * Helper method to locate an item in a list by identifier.<br>
	 * By default, uses item's hashcode as identifier.
	 *
	 * @param id the item id
	 * @param list the list of items
	 * @return the item with that identifier or {@code null} if there is no such
	 * @see SortableBehavior#findItem(String, List)
	 */
  protected T findItem(String id, List<T> list) {
    return ListUtils.fromHash(Integer.parseInt(id), list);
  }

  @Override public JQueryBehavior newWidgetBehavior(String selector) {
    return new SortableBehavior<T>(selector, this.options, this) {
      private static final long serialVersionUID = 1L;

      @Override protected List<T> getItemList() {
        return Sortable.this.getModelObject();
      }

      @Override @Deprecated protected List<T> getConnectedList() {
        if (Sortable.this.connectedSortable.size() > 0) {
          return Sortable.this.connectedSortable.get(0).getModelObject();
        }
        return Collections.emptyList();
      }

      @Override protected List<List<T>> getConnectedLists() {
        return Sortable.this.getConnectedLists();
      }

      @Override protected T findItem(String id, List<T> list) {
        return Sortable.this.findItem(id, list);
      }
    };
  }

  protected List<List<T>> getConnectedLists() {
    List<List<T>> connectedLists = new ArrayList<>();
    for (Sortable<T> connected : this.connectedSortable) {
      connectedLists.add(connected.getModelObject());
    }
    return connectedLists;
  }

  /**
	 * Gets a new {@link HashListView}
	 *
	 * @param model the {@link IModel} that <i>should</i> be used
	 * @return the {@link HashListView}
	 */
  protected abstract HashListView<T> newListView(IModel<List<T>> model);

  public abstract static class HashListView<T extends java.lang.Object> extends ListView<T> {
    private static final long serialVersionUID = 1L;

    /**
		 * Constructor
		 *
		 * @param id the markup id
		 */
    public HashListView(String id) {
      super(id);
    }

    /**
		 * Constructor
		 *
		 * @param id the markup id
		 * @param list the {@link List}
		 */
    public HashListView(String id, List<T> list) {
      super(id, list);
    }

    /**
		 * Constructor
		 *
		 * @param id the markup id
		 * @param model the {@link IModel}
		 */
    public HashListView(String id, IModel<? extends List<T>> model) {
      super(id, model);
    }

    @Override protected void onBeginPopulateItem(ListItem<T> item) {
      super.onBeginPopulateItem(item);
      item.add(AttributeModifier.replace("data-hash", item.getModelObject().hashCode()));
    }
  }
}