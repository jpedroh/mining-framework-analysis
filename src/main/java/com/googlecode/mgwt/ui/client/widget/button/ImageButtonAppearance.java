package com.googlecode.mgwt.ui.client.widget.button;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;

/**
 * The appearance for all ImageButtons.
 */
public interface ImageButtonAppearance extends ButtonBaseAppearance {
  interface ImageButtonCss extends ButtonBaseCss {
    @Override @ClassName(value = "mgwt-ImageButton") String button();

    @Override @ClassName(value = "mgwt-ImageButton-active") String active();

    @ClassName(value = "mgwt-ImageButton-disabled") String disabled();

    @ClassName(value = "mgwt-ImageButton-image") String image();

    @ClassName(value = "mgwt-ImageButton-text") String text();

    @ClassName(value = "mgwt-ImageButton-small") String small();

    @ClassName(value = "mgwt-ImageButton-reverse-order") String reverseOrder();

    String ICON_BACKGROUND_COLOR();

    String ICON_BACKGROUND_COLOR_ACTIVE();
  }

  ImageButtonCss css();

  @Override UiBinder<Element, ImageButton> uiBinder();
}