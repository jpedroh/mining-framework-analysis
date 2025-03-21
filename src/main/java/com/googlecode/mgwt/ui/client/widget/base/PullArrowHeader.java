package com.googlecode.mgwt.ui.client.widget.base;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.dom.client.event.animation.TransitionEndEvent;
import com.googlecode.mgwt.dom.client.event.animation.TransitionEndHandler;
import com.googlecode.mgwt.ui.client.theme.base.PullToRefreshCss;
import com.googlecode.mgwt.ui.client.util.CssUtil;
import com.googlecode.mgwt.ui.client.widget.ProgressIndicator;
import com.googlecode.mgwt.ui.client.widget.base.PullPanel.PullHeader;
import com.googlecode.mgwt.ui.client.widget.event.PullStateChangedEvent.State;

/**
 * A header for a pull panel that shows an arrow
 * 
 * @author Daniel Kurka
 * @version $Id: $
 */
public class PullArrowHeader extends Composite implements PullHeader {
  private FlowPanel main;

  private FlowPanel icon;

  private HTML textContainer;

  private ProgressIndicator indicator;

  private final PullToRefreshCss css;

  /**
	 * Construct a {@link PullArrowHeader} with a given css
	 * 
	 * @param css
	 *            the css to use
	 */
  public PullArrowHeader(PullToRefreshCss css) {
    this.css = css;
    css.ensureInjected();
    main = new FlowPanel();
    main.addStyleName(css.pullToRefresh());
    initWidget(main);
    icon = new FlowPanel();
    icon.addStyleName(css.arrow());
    main.add(icon);
    indicator = new ProgressIndicator();
    indicator.addStyleName(css.spinner());
    indicator.getElement().getStyle().setDisplay(Display.NONE);
    main.add(indicator);
    textContainer = new HTML();
    textContainer.addStyleName(css.text());
    main.add(textContainer);
    addDomHandler(new TransitionEndHandler() {
      @Override public void onTransitionEnd(TransitionEndEvent event) {
        event.preventDefault();
        event.stopPropagation();
      }
    }, TransitionEndEvent.getType());
  }

  /** {@inheritDoc} */
  @Override public Widget asWidget() {
    return this;
  }

  /** {@inheritDoc} */
  @Override public void scrollStart(State state) {
    removeStyles();
    icon.addStyleName(css.arrow());
    icon.setVisible(true);
    indicator.setVisible(false);
  }

  /** {@inheritDoc} */
  @Override public void onScroll(State state, int positionY) {
    int degree = getRotation(positionY);
    CssUtil.rotate(icon.getElement(), degree);
  }

  /** {@inheritDoc} */
  @Override public void onScrollEnd(State state, int positionY, int duration) {
    icon.getElement().setAttribute("style", "");
    if (state == State.PULL_RELEASE) {
      showSpinner();
    } else {
    }
  }

  /**
	 * <p>
	 * showError
	 * </p>
	 */
  public void showError() {
    removeStyles();
    icon.addStyleName(css.error());
    icon.setVisible(true);
    indicator.setVisible(false);
  }

  /** {@inheritDoc} */
  @Override public int getHeight() {
    return 70;
  }

  /** {@inheritDoc} */
  @Override public int getStateSwitchPosition() {
    return 50;
  }

  /** {@inheritDoc} */
  @Override public void setHTML(final String html) {
    String htmlToSet = html;
    if (html == null) {
      htmlToSet = "";
    }
    textContainer.setHTML(htmlToSet);
  }

  /**
	 * <p>
	 * showArrow
	 * </p>
	 */
  protected void showArrow() {
    removeStyles();
    icon.addStyleName(css.arrow());
    icon.setVisible(true);
    indicator.setVisible(false);
  }

  /**
	 * <p>
	 * showSpinner
	 * </p>
	 */
  protected void showSpinner() {
    removeStyles();
    icon.setVisible(false);
    indicator.setVisible(true);
  }

  /**
	 * <p>
	 * getRotation
	 * </p>
	 * 
	 * @param y
	 *            a int.
	 * @return a int.
	 */
  protected int getRotation(int y) {
    int degree = (y - 30) * -10;
    if (degree < -90) {
      degree = -90;
    }
    if (degree > 90) {
      degree = 90;
    }
    return degree;
  }

  /**
	 * <p>
	 * remoteStyles
	 * </p>
	 */
  protected void removeStyles() {
    icon.removeStyleName(css.arrow());
    icon.removeStyleName(css.error());
  }

  public void showSuccess() {
    removeStyles();
    icon.setVisible(false);
    indicator.setVisible(false);
  }
}