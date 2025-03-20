package bdv.tools;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import bdv.cache.CacheControl;
import bdv.export.ProgressWriter;
import bdv.tools.bookmarks.bookmark.DynamicBookmark;
import bdv.util.Prefs;
import bdv.viewer.ViewerPanel;
import bdv.viewer.overlay.ScaleBarOverlayRenderer;
import bdv.viewer.render.MultiResolutionRenderer;
import bdv.viewer.state.ViewerState;
import net.imglib2.realtransform.AffineTransform3D;
import net.imglib2.ui.OverlayRenderer;
import net.imglib2.ui.PainterThread;
import net.imglib2.ui.RenderTarget;

public class RecordMovieDialog extends JDialog implements OverlayRenderer {
  private static final long serialVersionUID = 1L;

  private final ViewerPanel viewer;

  private final int maxTimepoint;

  private final ProgressWriter progressWriter;

  private final JTextField pathTextField;

  private final JSpinner spinnerMinTimepoint;

  private final JSpinner spinnerMaxTimepoint;

  private final JSpinner spinnerWidth;

  private final JSpinner spinnerHeight;

  private final JProgressBar progressBar;

  private volatile boolean 
<<<<<<< /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/left.java
  isRecordThreadRunning
=======
  stopRecording
>>>>>>> /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/right.java
  ;

  private JButton cancelButton;

  public RecordMovieDialog(final Frame owner, final ViewerPanel viewer, final ProgressWriter progressWriter) {
    super(owner, "record movie", false);
    this.viewer = viewer;
    maxTimepoint = viewer.getState().getNumTimepoints() - 1;
    this.progressWriter = progressWriter;
    final JPanel content = new JPanel();
    getContentPane().add(content, BorderLayout.CENTER);
    content.setBorder(new EmptyBorder(5, 10, 5, 5));
    final GridBagLayout layout = new GridBagLayout();
    layout.columnWeights = new double[] { 1 };
    content.setLayout(layout);
    final int gap = 5;
    final GridBagConstraints c = new GridBagConstraints();
    c.fill = GridBagConstraints.HORIZONTAL;
    c.insets = new Insets(0, 0, 0, 0);
    c.gridy = 0;
    final JPanel saveAsPanel = new JPanel();
    saveAsPanel.setLayout(new BorderLayout(5, 0));
    saveAsPanel.add(new JLabel("save to"), BorderLayout.WEST);
    pathTextField = new JTextField("./record/");
    saveAsPanel.add(pathTextField, BorderLayout.CENTER);
    pathTextField.setColumns(20);
    c.gridx = 0;
    c.gridwidth = 5;
    c.insets = new Insets(0, 0, gap, gap);
    content.add(saveAsPanel, c);
    c.gridx += c.gridwidth;
    c.gridwidth = 1;
    c.insets = new Insets(0, 0, gap, 0);
    final JButton browseButton = new JButton("Browse");
    content.add(browseButton, c);
    c.gridy++;
    c.gridx = 1;
    c.insets = new Insets(0, 0, gap, gap);
    content.add(new JLabel("timepoints from"), c);
    c.gridx++;
    spinnerMinTimepoint = new JSpinner();
    spinnerMinTimepoint.setModel(new SpinnerNumberModel(0, 0, maxTimepoint, 1));
    content.add(spinnerMinTimepoint, c);
    c.gridx++;
    content.add(new JLabel("to"), c);
    c.gridx++;
    spinnerMaxTimepoint = new JSpinner();
    spinnerMaxTimepoint.setModel(new SpinnerNumberModel(maxTimepoint, 0, maxTimepoint, 1));
    content.add(spinnerMaxTimepoint, c);
    c.gridy++;
    c.gridx = 1;
    content.add(new JLabel("width"), c);
    c.gridx++;
    spinnerWidth = new JSpinner();
    spinnerWidth.setModel(new SpinnerNumberModel(800, 10, 5000, 1));
    content.add(spinnerWidth, c);
    c.gridy++;
    c.gridx = 1;
    content.add(new JLabel("height"), c);
    c.gridx++;
    spinnerHeight = new JSpinner();
    spinnerHeight.setModel(new SpinnerNumberModel(600, 10, 5000, 1));
    content.add(spinnerHeight, c);
    final JPanel progressPanel = new JPanel();
    progressPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    boxes.add(progressPanel);
    final GridBagLayout gbl_progressPanel = new GridBagLayout();

<<<<<<< /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/left.java
    gbl_progressPanel.columnWidths = new int[] { 332, 0, 0 };
=======
    c.gridy++;
>>>>>>> /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/right.java


<<<<<<< /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/left.java
    gbl_progressPanel.rowHeights = new int[] { 19, 0 }
=======
    c.gridx = 0
>>>>>>> /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/right.java
    ;

<<<<<<< /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/left.java
    gbl_progressPanel.columnWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE }
=======
    c.gridwidth = 5
>>>>>>> /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/right.java
    ;

<<<<<<< /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/left.java
    gbl_progressPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE }
=======
    c.gridheight = 2
>>>>>>> /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/right.java
    ;
    progressPanel.setLayout(gbl_progressPanel);
    progressBar = new JProgressBar();
    progressBar.setStringPainted(true);

<<<<<<< /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/left.java
    final GridBagConstraints gbc_progressBar = new GridBagConstraints();
=======
    content.add(progressBar, c);
>>>>>>> /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/right.java

    c.gridheight = 1;
    c.gridx += c.gridwidth;

<<<<<<< /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/left.java
    gbc_progressBar.fill = GridBagConstraints.HORIZONTAL
=======
    c.gridwidth = 1
>>>>>>> /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/right.java
    ;

<<<<<<< /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/left.java
    gbc_progressBar
=======
    c
>>>>>>> /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/right.java
    .insets = new Insets(0, 0, gap, 0, 5);
    gbc_progressBar.gridx = 0;
    gbc_progressBar.gridy = 0;
    progressPanel.add(progressBar, gbc_progressBar);
    cancelButton = new JButton("Cancel");
    cancelButton.setEnabled(false);
    final 
<<<<<<< /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/left.java
    GridBagConstraints
=======
    JButton
>>>>>>> /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/right.java
     
<<<<<<< /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/left.java
    gbc_cancelButton = new GridBagConstraints()
=======
    cancelButton = new JButton("Cancel")
>>>>>>> /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/right.java
    ;
    gbc_cancelButton.gridx = 1;

<<<<<<< /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/left.java
    gbc_cancelButton.gridy = 0;
=======
    cancelButton.setEnabled(false);
>>>>>>> /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/right.java


<<<<<<< /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/left.java
    progressPanel
=======
    content
>>>>>>> /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/right.java
    .add(cancelButton, 
<<<<<<< /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/left.java
    gbc_cancelButton
=======
    c
>>>>>>> /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/right.java
    );
    cancelButton.addActionListener(new ActionListener() {
      @Override public void actionPerformed(final ActionEvent e) {
        isRecordThreadRunning = false;
      }
    });
    c.gridy++;

<<<<<<< /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/left.java
    buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5))
=======
    c.insets = new Insets(0, 0, 0, 0)
>>>>>>> /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/right.java
    ;
    final JButton recordButton = new JButton("Record");
    content.add(recordButton, c);
    spinnerMinTimepoint.addChangeListener((e) -> {
      final int min = (Integer) spinnerMinTimepoint.getValue();
      final int max = (Integer) spinnerMaxTimepoint.getValue();
      if (max < min) {
        spinnerMaxTimepoint.setValue(min);
      }
    });
    spinnerMaxTimepoint.addChangeListener((e) -> {
      final int min = (Integer) spinnerMinTimepoint.getValue();
      final int max = (Integer) spinnerMaxTimepoint.getValue();
      if (min > max) {
        spinnerMinTimepoint.setValue(max);
      }
    });
    final JFileChooser fileChooser = new JFileChooser();
    fileChooser.setMultiSelectionEnabled(false);
    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    browseButton.addActionListener((e) -> {
      fileChooser.setSelectedFile(new File(pathTextField.getText()));
      final int returnVal = fileChooser.showSaveDialog(null);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        final File file = fileChooser.getSelectedFile();
        pathTextField.setText(file.getAbsolutePath());
      }
    });
    cancelButton.addActionListener((e) -> {
      stopRecording = true;
    });
    recordButton.addActionListener(
<<<<<<< /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/left.java
    new ActionListener() {
      @Override public void actionPerformed(final ActionEvent e) {
        final String dirname = pathTextField.getText();
        final File dir = new File(dirname);
        if (!dir.exists()) {
          dir.mkdirs();
        }
        if (!dir.exists() || !dir.isDirectory()) {
          System.err.println("Invalid export directory " + dirname);
          return;
        }
        final int minTimepointIndex = (Integer) spinnerMinTimepoint.getValue();
        final int maxTimepointIndex = (Integer) spinnerMaxTimepoint.getValue();
        final int width = (Integer) spinnerWidth.getValue();
        final int height = (Integer) spinnerHeight.getValue();
        new Thread() {
          @Override public void run() {
            try {
              isRecordThreadRunning = true;
              recordButton.setEnabled(false);
              cancelButton.setEnabled(true);
              recordMovie(width, height, minTimepointIndex, maxTimepointIndex, dir);
              progressBar.setValue(0);
              recordButton.setEnabled(true);
              cancelButton.setEnabled(false);
              isRecordThreadRunning = false;
            } catch (final Exception ex) {
              ex.printStackTrace();
            }
          }
        }.start();
      }
    }
=======
    (e) -> {
      final String dirname = pathTextField.getText();
      final File dir = new File(dirname);
      if (!dir.exists()) {
        dir.mkdirs();
      }
      if (!dir.exists() || !dir.isDirectory()) {
        System.err.println("Invalid export directory " + dirname);
        return;
      }
      final int minTimepointIndex = (Integer) spinnerMinTimepoint.getValue();
      final int maxTimepointIndex = (Integer) spinnerMaxTimepoint.getValue();
      final int width = (Integer) spinnerWidth.getValue();
      final int height = (Integer) spinnerHeight.getValue();
      new Thread() {
        @Override public void run() {
          try {
            stopRecording = false;
            recordButton.setEnabled(false);
            cancelButton.setEnabled(true);
            recordMovie(width, height, minTimepointIndex, maxTimepointIndex, dir);
            progressBar.setValue(0);
            recordButton.setEnabled(true);
            cancelButton.setEnabled(false);
            stopRecording = true;
          } catch (final Exception ex) {
            ex.printStackTrace();
          }
        }
      }.start();
    }
>>>>>>> /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/right.java
    );
    final ActionMap am = getRootPane().getActionMap();
    final InputMap im = getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    final Object hideKey = new Object();
    final Action hideAction = new AbstractAction() {
      @Override public void actionPerformed(final ActionEvent e) {
        setVisible(false);
      }

      private static final long serialVersionUID = 1L;
    };
    im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), hideKey);
    am.put(hideKey, hideAction);
    pack();
    setMinimumSize(getPreferredSize());
    setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
  }

  public void recordMovie(final int width, final int height, final int minTimepointIndex, final int maxTimepointIndex, final File dir) throws IOException {
    final ViewerState renderState = viewer.getState();
    final int canvasW = viewer.getDisplay().getWidth();
    final int canvasH = viewer.getDisplay().getHeight();
    final ScaleBarOverlayRenderer scalebar = Prefs.showScaleBarInMovie() ? new ScaleBarOverlayRenderer() : null;
    class MyTarget implements RenderTarget {
      BufferedImage bi;

      @Override public BufferedImage setBufferedImage(final BufferedImage bufferedImage) {
        bi = bufferedImage;
        return null;
      }

      @Override public int getWidth() {
        return width;
      }

      @Override public int getHeight() {
        return height;
      }
    }
    final MyTarget target = new MyTarget();
    final MultiResolutionRenderer renderer = new MultiResolutionRenderer(target, new PainterThread(null), new double[] { 1 }, 0, false, 1, null, false, viewer.getOptionValues().getAccumulateProjectorFactory(), new CacheControl.Dummy());
    setProgress(0);
    for (int timepoint = minTimepointIndex; timepoint <= maxTimepointIndex; ++timepoint) {

<<<<<<< /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/left.java
      if (!isRecordThreadRunning) {
        break;
      }
=======
      if (stopRecording) {
        break;
      }
>>>>>>> /usr/src/app/output/tpietzsch/spimviewer/df89915fd4c7e1ddef4ebeb94e3e68fe76e49be0/src/main/java/bdv/tools/RecordMovieDialog.java/right.java

      final AffineTransform3D affine = getTransformation(renderState, canvasW, canvasH, timepoint);
      affine.scale((double) width / canvasW);
      affine.set(affine.get(0, 3) + width / 2, 0, 3);
      affine.set(affine.get(1, 3) + height / 2, 1, 3);
      renderState.setViewerTransform(affine);
      renderState.setCurrentTimepoint(timepoint);
      renderer.requestRepaint();
      renderer.paint(renderState);
      if (Prefs.showScaleBarInMovie()) {
        final Graphics2D g2 = target.bi.createGraphics();
        g2.setClip(0, 0, width, height);
        scalebar.setViewerState(renderState);
        scalebar.paint(g2);
      }
      ImageIO.write(target.bi, "png", new File(String.format("%s/img-%03d.png", dir, timepoint)));
      setProgress((double) (timepoint - minTimepointIndex + 1) / (maxTimepointIndex - minTimepointIndex + 1));
    }
  }

  private synchronized void setProgress(final double progress) {
    progressWriter.setProgress(progress);
    progressBar.setValue((int) (progress * 100));
  }

  @Override public void drawOverlays(final Graphics g) {
  }

  @Override public void setCanvasSize(final int width, final int height) {
    spinnerWidth.setValue(width);
    spinnerHeight.setValue(height);
  }

  private AffineTransform3D getTransformation(final ViewerState renderState, final int canvasW, final int canvasH, final int currentTimepoint) {
    if (renderState.getActiveBookmark() instanceof DynamicBookmark) {
      final DynamicBookmark dynamicBookmark = (DynamicBookmark) renderState.getActiveBookmark();
      final AffineTransform3D affine = dynamicBookmark.getInterpolatedTransform(currentTimepoint, canvasW / 2, canvasH);
      return affine;
    } else {
      final AffineTransform3D affine = new AffineTransform3D();
      renderState.getViewerTransform(affine);
      affine.set(affine.get(0, 3) - canvasW / 2, 0, 3);
      affine.set(affine.get(1, 3) - canvasH / 2, 1, 3);
      return affine;
    }
  }
}