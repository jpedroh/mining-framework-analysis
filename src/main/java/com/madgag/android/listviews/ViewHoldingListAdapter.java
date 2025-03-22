package com.madgag.android.listviews;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.Arrays;
import java.util.List;
import static java.util.Arrays.asList;

public class ViewHoldingListAdapter<T extends java.lang.Object> extends BaseAdapter {
  private List<T> itemList;

  private final ViewFactory<T> viewFactory, dropDownViewFactory;

  public ViewHoldingListAdapter(List<T> itemList, ViewFactory<T> viewFactory, ViewFactory<T> dropDownViewFactory) {
    this.itemList = itemList;
    this.viewFactory = viewFactory;
    this.dropDownViewFactory = dropDownViewFactory;
  }

  public ViewHoldingListAdapter(List<T> itemList, ViewFactory<T> viewFactory) {
    this(itemList, viewFactory, viewFactory);
  }

  public ViewHoldingListAdapter(List<T> itemList, ViewCreator c, ViewHolderFactory<T> vhf) {
    this(itemList, new ViewFactory<T>(c, vhf));
  }

  public ViewHoldingListAdapter(T[] items, ViewFactory<T> viewFactory) {
    this(asList(items), viewFactory);
  }

  public ViewHoldingListAdapter(T[] items, ViewCreator c, ViewHolderFactory<T> vhf) {
    this(asList(items), c, vhf);
  }

  @Override public boolean hasStableIds() {
    return true;
  }

  public void setList(List<T> itemList) {
    this.itemList = itemList;
    notifyDataSetChanged();
  }

  public int getCount() {
    return itemList.size();
  }

  public T getItem(int index) {
    return itemList.get(index);
  }

  public long getItemId(int i) {
    return getItem(i).hashCode();
  }

  public View getView(int index, View convertView, ViewGroup parent) {
    return viewFactory.getView(convertView, itemList.get(index));
  }

  /**
     * Declared by SpinnerAdapter, this method allows your spinner to show
     * different views in your drop down vs the 'closed' spinned box.
     */
  public View getDropDownView(int index, View convertView, ViewGroup parent) {
    return dropDownViewFactory.getView(convertView, itemList.get(index));
  }
}