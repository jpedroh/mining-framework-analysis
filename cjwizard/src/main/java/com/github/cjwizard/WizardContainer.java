package com.github.cjwizard;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.cjwizard.pagetemplates.DefaultPageTemplate;
import com.github.cjwizard.pagetemplates.PageTemplate;
import com.github.cjwizard.utilities.ExceptionUtilities;

/**
 * This is the primary "Wizard" class. It must be instantiated with a
 * PageFactory and then treated as a JPanel.
 * 
 * @author rcreswick
 * 
 * @version 20150127
 * 
 */
public class WizardContainer extends JPanel implements WizardController {
  private static final long serialVersionUID = 20150127L;

  /**
    * Log instance
    */
  private final Logger log = LoggerFactory.getLogger(WizardContainer.class);

  /**
    * Resource to translate GUI elements.
    */
  private static ResourceBundle msg = ResourceBundle.getBundle("com.github.cjwizard.i18n.cjwizard");

  private static final String I18N_NEXT = "NEXT";

  private static final String I18N_PREVIOUS = "PREVIOUS";

  private static final String I18N_FINISH = "FINISH";

  private static final String I18N_CANCEL = "CANCEL";

  /**
    * Storage for all the collected information.
    */
  private WizardSettings _settings;

  /**
    * The path from the start of the dialog to the current location.
    */
  private final List<WizardPage> _path;

  /**
     * In a wizard with a conditional path if after having traversed once the pages
     * I hit back and re-traverse the pages should I store the previously selected
     * path and use it or forget it. By default this is false.
     */
  private boolean forgetTraversedPath = false;

  /**
    * The path of already-visited pages starting from the current page.
    */
  private final Deque<WizardPage> _visitedPath;

  /**
    * List of listeners to update on wizard events.
    */
  private final List<WizardListener> _listeners;

  /**
    * The template to surround the wizard pages of this dialog.
    */
  private PageTemplate _template;

  /**
    * The factory that generates pages for this wizard.
    */
  private final PageFactory _factory;

  /**
    * The panel containing any dynamically-added buttons.
    */
  private JPanel _extraButtonPanel;

  /**
    * Save the statuses of the Next button so we can restore it when going back.
    */
  private Deque<Boolean> _nextStatuses;

  /**
    * Save the statuses of the Finish button so we can restore it when going back.
    */
  private Deque<Boolean> _finishStatuses;

  /**
    * Save the statuses of the Previous button so we can restore it when going
    * back.
    */
  private Deque<Boolean> _prevStatuses;

  /**
    * Save the statuses of the Cancel button so we can restore it when going back.
    */
  private Deque<Boolean> _cancelStatuses;

  /**
    * Save the statuses of the Next button of visited pages.
    */
  private Deque<Boolean> _visitedNextStatuses;

  /**
    * Save the statuses of the Finish button of visited pages.
    */
  private Deque<Boolean> _visitedFinishStatuses;

  /**
    * Save the statuses of the Previous button of visited pages.
    */
  private Deque<Boolean> _visitedPrevStatuses;

  /**
    * Save the statuses of the Cancel button of visited pages.
    */
  private Deque<Boolean> _visitedCancelStatuses;

  private final AbstractAction _prevAction = new AbstractAction(msg.getString(I18N_PREVIOUS)) {
    private static final long serialVersionUID = -3544187996163884815L;

    {
      setEnabled(false);
    }

    @Override public void actionPerformed(ActionEvent e) {
      prev();
    }
  };

  private final AbstractAction _nextAction = new AbstractAction(msg.getString(I18N_NEXT)) {
    private static final long serialVersionUID = 4175292094891534750L;

    @Override public void actionPerformed(ActionEvent e) {
      next();
    }
  };

  private final AbstractAction _finishAction = new AbstractAction(msg.getString(I18N_FINISH)) {
    private static final long serialVersionUID = -1556582296948163049L;

    {
      setEnabled(false);
    }

    @Override public void actionPerformed(ActionEvent e) {
      finish();
    }
  };

  private final AbstractAction _cancelAction = new AbstractAction(msg.getString(I18N_CANCEL)) {
    private static final long serialVersionUID = 2757580981402133120L;

    @Override public void actionPerformed(ActionEvent e) {
      cancel();
    }
  };

  /**
    * Constructor, uses default PageTemplate and {@link StackWizardSettings}.
    * 
    * @param factory The factory that will create the pages.
    */
  public WizardContainer(PageFactory factory) {
    this(factory, new DefaultPageTemplate(), new StackWizardSettings());
  }

  /**
    * Customized constructor.
    * @param factory The factory which will be used to create the pages.
    * @param template Template to put inside the pages.
    * @param settings The settings to store the values put by the user.
    */
  public WizardContainer(PageFactory factory, PageTemplate template, WizardSettings settings) {
    _factory = factory;
    _template = template;
    _settings = settings;
    _path = new LinkedList<WizardPage>();
    _visitedPath = new ArrayDeque<WizardPage>();
    _listeners = new LinkedList<WizardListener>();
    _cancelStatuses = new ArrayDeque<Boolean>();
    _prevStatuses = new ArrayDeque<Boolean>();
    _nextStatuses = new ArrayDeque<Boolean>();
    _finishStatuses = new ArrayDeque<Boolean>();
    _visitedCancelStatuses = new ArrayDeque<Boolean>();
    _visitedPrevStatuses = new ArrayDeque<Boolean>();
    _visitedNextStatuses = new ArrayDeque<Boolean>();
    _visitedFinishStatuses = new ArrayDeque<Boolean>();
    initComponents();
    _template.registerController(this);
    next();
  }

  /**
    * 
    */
  private void initComponents() {
    final JButton prevBtn = new JButton(_prevAction);
    final JButton nextBtn = new JButton(_nextAction);
    final JButton finishBtn = new JButton(_finishAction);
    final JButton cancelBtn = new JButton(_cancelAction);
    _extraButtonPanel = new JPanel();
    _extraButtonPanel.setLayout(new BoxLayout(_extraButtonPanel, BoxLayout.LINE_AXIS));
    final JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
    buttonPanel.add(_extraButtonPanel);
    buttonPanel.add(Box.createHorizontalGlue());
    buttonPanel.add(prevBtn);
    buttonPanel.add(Box.createHorizontalStrut(5));
    buttonPanel.add(nextBtn);
    buttonPanel.add(Box.createHorizontalStrut(10));
    buttonPanel.add(finishBtn);
    buttonPanel.add(Box.createHorizontalStrut(10));
    buttonPanel.add(cancelBtn);
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    this.setLayout(new BorderLayout());
    this.add(_template, BorderLayout.CENTER);
    this.add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
    * Add additional buttons to the wizard controls. Any previously-added
    * buttons are cleared on each call to this method.
    * 
    * @param buttons
    *           The buttons to add to the wizard controls.
    */
  public void setButtons(JButton... buttons) {
    _extraButtonPanel.removeAll();
    for (JButton button : buttons) {
      _extraButtonPanel.add(button);
      _extraButtonPanel.add(Box.createHorizontalStrut(10));
    }
  }

  /**
    * The PageFactory is not queried for pages when moving *backwards*.
    */
  public void prev() {
    log.debug("prev. page");
    WizardPage removing = _path.get(_path.size() - 1);
    if (!removing.onPrev(getSettings())) {
      return;
    }
    _path.remove(_path.size() - 1);
    _visitedPath.push(removing);
    removing.updateSettings(getSettings());
    getSettings().commit();
    getSettings().rollBack();
    _visitedCancelStatuses.push(_cancelAction.isEnabled());
    _visitedPrevStatuses.push(_prevAction.isEnabled());
    _visitedNextStatuses.push(_nextAction.isEnabled());
    _visitedFinishStatuses.push(_finishAction.isEnabled());
    _cancelAction.setEnabled(_cancelStatuses.pop());
    _prevAction.setEnabled(_prevStatuses.pop());
    _nextAction.setEnabled(_nextStatuses.pop());
    _finishAction.setEnabled(_finishStatuses.pop());
    assert 1 <= _path.size() : "Invalid path size! " + _path.size();
    setPrevEnabled(_path.size() > 1);
    WizardPage curPage = _path.get(_path.size() - 1);
    firePageChanging(curPage, getPath());
    curPage.rendering(getPath(), getSettings());
    _template.setPage(curPage);
    firePageChanged(curPage, getPath());
  }

  /**
    * Called when the page must advance to the next page.
    */
  public void next() {
    log.debug("next page");
    WizardPage lastPage = currentPage();
    if (null != lastPage) {
      if (!lastPage.onNext(getSettings())) {
        return;
      }
      lastPage.updateSettings(getSettings());
      _cancelStatuses.push(_cancelAction.isEnabled());
      _prevStatuses.push(_prevAction.isEnabled());
      _nextStatuses.push(_nextAction.isEnabled());
      _finishStatuses.push(_finishAction.isEnabled());
    }
    WizardPage nextPage = null;
    if (_visitedPath.isEmpty() || _factory.isTransient(getPath(), getSettings()) || forgetTraversedPath) {
      nextPage = _factory.createPage(getPath(), getSettings());
      if (!_visitedPath.isEmpty()) {
        _visitedPath.pop();
        _visitedCancelStatuses.pop();
        _visitedPrevStatuses.pop();
        _visitedNextStatuses.pop();
        _visitedFinishStatuses.pop();
      }
      setPrevEnabled(_path.size() >= 1);
    } else {
      nextPage = _visitedPath.pop();
      _cancelAction.setEnabled(_visitedCancelStatuses.pop());
      _prevAction.setEnabled(_visitedPrevStatuses.pop());
      _nextAction.setEnabled(_visitedNextStatuses.pop());
      _finishAction.setEnabled(_visitedFinishStatuses.pop());
    }
    nextPage.registerController(this);
    _path.add(nextPage);
    getSettings().newPage(nextPage.getId());
    firePageChanging(nextPage, getPath());
    nextPage.rendering(getPath(), getSettings());
    _template.setPage(nextPage);
    firePageChanged(nextPage, getPath());
  }

  public void visitPage(WizardPage page) {
    int idx = _path.indexOf(page);
    WizardPage lastPage = currentPage();
    if (null != lastPage) {
      lastPage.updateSettings(getSettings());
    }
    boolean cancelEnabled = true, previousEnabled = true, nextEnabled = true, finishEnabled = false;
    if (-1 == idx) {
      if (null != lastPage) {
        _cancelStatuses.push(_cancelAction.isEnabled());
        _prevStatuses.push(_prevAction.isEnabled());
        _nextStatuses.push(_nextAction.isEnabled());
        _finishStatuses.push(_finishAction.isEnabled());
      }
      while (!_visitedPath.isEmpty()) {
        cancelEnabled = _visitedCancelStatuses.pop();
        previousEnabled = _visitedPrevStatuses.pop();
        nextEnabled = _visitedNextStatuses.pop();
        finishEnabled = _visitedFinishStatuses.pop();
        WizardPage visited = _visitedPath.pop();
        getPath().add(visited);
        if (visited == page) {
          _cancelAction.setEnabled(cancelEnabled);
          setPrevEnabled(previousEnabled);
          setNextEnabled(nextEnabled);
          setFinishEnabled(finishEnabled);
          break;
        } else {
          _cancelStatuses.push(cancelEnabled);
          _prevStatuses.push(previousEnabled);
          _nextStatuses.push(nextEnabled);
          _finishStatuses.push(finishEnabled);
        }
      }
      if (currentPage() != page) {
        assert _visitedCancelStatuses.isEmpty();
        assert _visitedPrevStatuses.isEmpty();
        assert _visitedNextStatuses.isEmpty();
        assert _visitedFinishStatuses.isEmpty();
        getPath().add(page);
      }
      getSettings().newPage(page.getId());
    } else {
      for (int i = _path.size() - 1; i > idx; i--) {
        getSettings().rollBack();
        _visitedPath.push(_path.remove(i));
        final Boolean cancelStatus = _cancelStatuses.pop();
        final Boolean prevStatus = _prevStatuses.pop();
        final Boolean nextStatus = _nextStatuses.pop();
        final Boolean finishStatus = _finishStatuses.pop();
        _visitedCancelStatuses.push(cancelStatus);
        _visitedPrevStatuses.push(prevStatus);
        _visitedNextStatuses.push(nextStatus);
        _visitedFinishStatuses.push(finishStatus);
        cancelEnabled = cancelStatus;
        previousEnabled = prevStatus;
        nextEnabled = nextStatus;
        finishEnabled = finishStatus;
      }
      _cancelAction.setEnabled(cancelEnabled);
      setPrevEnabled(previousEnabled);
      setNextEnabled(nextEnabled);
      setFinishEnabled(finishEnabled);
    }
    firePageChanging(page, getPath());
    page.rendering(_path, getSettings());
    _template.setPage(page);
    firePageChanged(page, _path);
  }

  /**
    * @param nextPage
    * @param path
    */
  private void firePageChanging(WizardPage curPage, List<WizardPage> path) {
    for (WizardListener l : _listeners) {
      l.onPageChanging(curPage, getPath());
    }
  }

  /**
    * @param nextPage
    * @param path
    */
  private void firePageChanged(WizardPage curPage, List<WizardPage> path) {
    for (WizardListener l : _listeners) {
      l.onPageChanged(curPage, getPath());
    }
  }

  /**
    * 
    */
  public void finish() {
    log.debug("finish");
    WizardPage lastPage = currentPage();
    if (null != lastPage) {
      if (!lastPage.onNext(getSettings())) {
        return;
      }
      getSettings().newPage(lastPage.getId());
      lastPage.updateSettings(getSettings());
    }
    for (WizardListener l : _listeners) {
      l.onFinished(getPath(), getSettings());
    }
  }

  /**
    * 
    */
  public void cancel() {
    log.debug("cancel");
    for (WizardListener l : _listeners) {
      l.onCanceled(getPath(), getSettings());
    }
  }

  public void addWizardListener(WizardListener listener) {
    ExceptionUtilities.checkNull(listener, "listener");
    if (!_listeners.contains(listener)) {
      _listeners.add(listener);
      WizardPage curPage = _path.get(_path.size() - 1);
      listener.onPageChanged(curPage, getPath());
    }
  }

  public void removeWizardListener(WizardListener listener) {
    ExceptionUtilities.checkNull(listener, "listener");
    _listeners.remove(listener);
  }

  public WizardSettings getSettings() {
    return _settings;
  }

  /**
    * Set/load the specified settings map nad re-render the current page.
    * 
    * @param settings
    *           The settings to load.
    */
  public void setSettings(WizardSettings settings) {
    _settings = settings;
    currentPage().rendering(_path, _settings);
  }

  public List<WizardPage> getPath() {
    return _path;
  }

  /**
    * @return The last (current) page of the current {@link #getPath()} or null if
    *         the path is empty.
    */
  public WizardPage currentPage() {
    int lastIdx = _path.size() - 1;
    return (lastIdx < 0) ? null : _path.get(lastIdx);
  }

  public void setNextEnabled(boolean enabled) {
    _nextAction.setEnabled(enabled);
  }

  public void setPrevEnabled(boolean enabled) {
    _prevAction.setEnabled(enabled);
  }

  public void setFinishEnabled(boolean enabled) {
    _finishAction.setEnabled(enabled);
  }

  public void setCancelEnabled(boolean enabled) {
    _cancelAction.setEnabled(enabled);
  }

  /**
     * @param forgetTraversedPath the forgetTraversedPath to set
     */
  public void setForgetTraversedPath(boolean forgetTraversedPath) {
    this.forgetTraversedPath = forgetTraversedPath;
  }
}