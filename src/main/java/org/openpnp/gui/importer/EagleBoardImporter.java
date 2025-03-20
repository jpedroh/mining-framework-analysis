package org.openpnp.gui.importer;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import org.openpnp.gui.support.MessageBoxes;
import org.openpnp.model.Board;
import org.openpnp.model.Board.Side;
import org.openpnp.model.BoardPad;
import org.openpnp.model.Configuration;
import org.openpnp.model.LengthUnit;
import org.openpnp.model.Location;
import org.openpnp.model.Package;
import org.openpnp.model.Pad;
import org.openpnp.model.Part;
import org.openpnp.model.Placement;
import org.openpnp.model.Point;
import org.openpnp.model.eagle.EagleLoader;
import org.openpnp.model.eagle.xml.Element;
import org.openpnp.model.eagle.xml.Layer;
import org.openpnp.model.eagle.xml.Library;
import org.openpnp.model.eagle.xml.Param;
import org.openpnp.model.eagle.xml.Vertex;
import org.openpnp.util.Utils2D;
import com.jgoodies.forms.layout.ColumnSpec;
import org.slf4j.Logger;
import com.jgoodies.forms.layout.FormLayout;
import org.slf4j.LoggerFactory;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

@SuppressWarnings(value = { "serial" }) public class EagleBoardImporter implements BoardImporter {
  private final static Logger logger = LoggerFactory.getLogger(EagleBoardImporter.class);

  private final static String NAME = "CadSoft EAGLE Board";

  private final static String DESCRIPTION = "Import files directly from EAGLE\'s <filename>.brd file.";

  private static Board board;

  private File boardFile;

  static private Double mil_to_mm = 0.0254;

  @Override public String getImporterName() {
    return NAME;
  }

  @Override public String getImporterDescription() {
    return DESCRIPTION;
  }

  @Override public Board importBoard(Frame parent) throws Exception {
    Dlg dlg = new Dlg(parent);
    dlg.setVisible(true);
    return board;
  }

  private static List<Placement> parseFile(File file, Side side, boolean createMissingParts) throws Exception {
    String dimensionLayer = "";
    String topLayer = "";
    String bottomLayer = "";
    String tCreamLayer = "";
    String bCreamLayer = "";
    String mmMinCreamFrame_string;
    double mmMinCreamFrame_number = 0;
    String mmMaxCreamFrame_string;
    double mmMaxCreamFrame_number = 0;
    String libraryId = "";
    String packageId = "";
    Part part = null;
    List<BoardPad> pads = new ArrayList<BoardPad>();
    ArrayList<Placement> placements = new ArrayList<Placement>();
    EagleLoader boardToProcess = new EagleLoader(file);
    if (boardToProcess.board != null) {
      for (Layer layer : boardToProcess.layers.getLayer()) {
        if (layer.getName().equalsIgnoreCase("Dimension")) {
          dimensionLayer = layer.getNumber();
        } else {
          if (layer.getName().equalsIgnoreCase("Top")) {
            topLayer = layer.getNumber();
          } else {
            if (layer.getName().equalsIgnoreCase("Bottom")) {
              bottomLayer = layer.getNumber();
            } else {
              if (layer.getName().equalsIgnoreCase("tCream")) {
                tCreamLayer = layer.getNumber();
              } else {
                if (layer.getName().equalsIgnoreCase("bCream")) {
                  bCreamLayer = layer.getNumber();
                }
              }
            }
          }
        }
      }
      Double x_boundary = 0.0;
      for (Object e : boardToProcess.board.getPlain().getPolygonOrWireOrTextOrDimensionOrCircleOrRectangleOrFrameOrHole()) {
        if (e instanceof org.openpnp.model.eagle.xml.Wire) {
          if (((org.openpnp.model.eagle.xml.Wire) e).getLayer().equalsIgnoreCase(dimensionLayer)) {
            x_boundary = Math.max(x_boundary, Double.parseDouble(((org.openpnp.model.eagle.xml.Wire) e).getX1()));
            x_boundary = Math.max(x_boundary, Double.parseDouble(((org.openpnp.model.eagle.xml.Wire) e).getX2()));
          }
        }
      }
      Point center = new Point(x_boundary / 2, 0);
      for (Param params : boardToProcess.board.getDesignrules().getParam()) {
        if (params.getName().compareToIgnoreCase("mlMinCreamFrame") == 0) {
          mmMinCreamFrame_string = params.getValue().replaceAll("[A-Za-z ]", "");
          if (params.getValue().toUpperCase().endsWith("MIL")) {
            mmMinCreamFrame_number = Double.parseDouble(mmMinCreamFrame_string) * mil_to_mm;
          } else {
            if (params.getValue().toUpperCase().endsWith("MM")) {
              mmMinCreamFrame_number = Double.parseDouble(mmMinCreamFrame_string) * mil_to_mm;
            } else {
              throw new Exception("mlMinCream must either be in mil or mm");
            }
          }
        }
        if (params.getName().compareToIgnoreCase("mlMaxCreamFrame") == 0) {
          mmMaxCreamFrame_string = params.getValue().replaceAll("[A-Za-z ]", "");
          if (params.getValue().toUpperCase().endsWith("MIL")) {
            mmMaxCreamFrame_number = Double.parseDouble(mmMaxCreamFrame_string) * mil_to_mm;
          } else {
            if (params.getValue().toUpperCase().endsWith("MM")) {
              mmMaxCreamFrame_number = Double.parseDouble(mmMaxCreamFrame_string);
            } else {
              throw new Exception("mlMaxCream must either be in mil or mm");
            }
          }
        }
      }
      if (!boardToProcess.board.getElements().getElement().isEmpty()) {
        for (Element element : boardToProcess.board.getElements().getElement()) {
          Side element_side;
          String rot = element.getRot();
          if (rot.toUpperCase().startsWith("M")) {
            element_side = Side.Bottom;
          } else {
            element_side = Side.Top;
          }
          if (side != null) {
            if (side != element_side) {
              continue;
            }
          }
          String rot_number = rot.replaceAll("[A-Za-z ]", "");
          Placement placement = new Placement(element.getName());
          double rotation = Double.parseDouble(rot_number);
          double x = Double.parseDouble(element.getX());
          double y = Double.parseDouble(element.getY());
          placement.setLocation(new Location(LengthUnit.Millimeters, x, y, 0, rotation));
          Configuration cfg = Configuration.get();
          if (cfg != null && createMissingParts) {
            String value = element.getValue();
            packageId = element.getPackage();
            libraryId = element.getLibrary();
            String pkgId = libraryId + "-" + packageId;
            String partId = libraryId + "-" + packageId;
            if (value.trim().length() > 0) {
              partId += "-" + value;
            }
            part = cfg.getPart(partId);
            Package pkg = cfg.getPackage(pkgId);
            if ((part == null) || (pkg == null)) {
              if (pkg == null) {
                pkg = new Package(pkgId);
                cfg.addPackage(pkg);
                if (part != null) {
                  cfg.removePart(part);
                  part = null;
                }
              }
              if (part == null) {
                part = new Part(partId);
                part.setPackage(pkg);
                cfg.addPart(part);
              }
              cfg.addPart(part);
            }
          }
          placement.setPart(part);
          if (!boardToProcess.board.getLibraries().getLibrary().isEmpty()) {
            for (Library library : boardToProcess.board.getLibraries().getLibrary()) {
              if (library.getName().equalsIgnoreCase(libraryId)) {
                if (!library.getPackages().getPackage().isEmpty()) {
                  ListIterator<org.openpnp.model.eagle.xml.Package> it = library.getPackages().getPackage().listIterator();
                  while (it.hasNext()) {
                    org.openpnp.model.eagle.xml.Package pak = (org.openpnp.model.eagle.xml.Package) it.next();
                    if (pak.getName().equalsIgnoreCase(packageId)) {
                      for (Object e : pak.getPolygonOrWireOrTextOrDimensionOrCircleOrRectangleOrFrameOrHoleOrPadOrSmd()) {
                        if (e instanceof org.openpnp.model.eagle.xml.Smd) {
                          if (!((org.openpnp.model.eagle.xml.Smd) e).getCream().equalsIgnoreCase("No")) {
                            Pad.RoundRectangle pad = new Pad.RoundRectangle();
                            pad.setUnits(LengthUnit.Millimeters);
                            pad.setHeight(Double.parseDouble(((org.openpnp.model.eagle.xml.Smd) e).getDx()) - (mmMaxCreamFrame_number - mmMinCreamFrame_number) / 2);
                            pad.setWidth(Double.parseDouble(((org.openpnp.model.eagle.xml.Smd) e).getDy()) - (mmMaxCreamFrame_number - mmMinCreamFrame_number) / 2);
                            pad.setRoundness(0);
                            pad.setRoundness(Double.parseDouble(((org.openpnp.model.eagle.xml.Smd) e).getRoundness()));
                            Double pad_rotation = Double.parseDouble(rot_number);
                            pad_rotation += Double.parseDouble(((org.openpnp.model.eagle.xml.Smd) e).getRot().replaceAll("[A-Za-z ]", "")) % 360;
                            Point A = new Point(Double.parseDouble(((org.openpnp.model.eagle.xml.Smd) e).getX()) + x, Double.parseDouble(((org.openpnp.model.eagle.xml.Smd) e).getY()) + y);
                            Point part_center = new Point(x, y);
                            if (element_side == Side.Top) {
                              if (rotation > 180) {
                                A = Utils2D.rotateTranslateCenterPoint(A, rotation, 0, 0, part_center);
                              } else {
                                A = Utils2D.rotateTranslateCenterPoint(A, -rotation, 0, 0, part_center);
                              }
                            } else {
                              if (element_side == Side.Bottom) {
                                if (rotation > 180) {
                                  A = Utils2D.rotateTranslateCenterPoint(A, rotation, 0, 0, part_center);
                                } else {
                                  A = Utils2D.rotateTranslateCenterPoint(A, -(180 - rotation), 0, 0, part_center);
                                }
                                if (A.getX() < center.getX()) {
                                  Double offset = center.getX() - A.getX();
                                  A.setX(center.getX() + offset);
                                } else {
                                  Double offset = A.getX() - center.getX();
                                  A.setX(center.getX() - offset);
                                }
                                if (A.getY() < y) {
                                  Double offset = y - A.getY();
                                  A.setY(y + offset);
                                } else {
                                  Double offset = A.getY() - y;
                                  A.setY(y - offset);
                                }
                              }
                            }
                            BoardPad boardPad = new BoardPad(pad, new Location(LengthUnit.Millimeters, A.getX(), A.getY(), 0, pad_rotation));
                            boardPad.setName(element.getName() + "-" + ((org.openpnp.model.eagle.xml.Smd) e).getName());
                            if (((org.openpnp.model.eagle.xml.Smd) e).getLayer().equalsIgnoreCase(topLayer)) {
                              if (element_side == Side.Top) {
                                boardPad.setSide(Side.Top);
                              } else {
                                boardPad.setSide(Side.Bottom);
                              }
                            } else {
                              if (((org.openpnp.model.eagle.xml.Smd) e).getLayer().equalsIgnoreCase(bottomLayer)) {
                                if (element_side == Side.Top) {
                                  boardPad.setSide(Side.Bottom);
                                } else {
                                  boardPad.setSide(Side.Top);
                                }
                              } else {
                                logger.info("Warning: " + file + "contains a SMD pad that is not on a topLayer or bottomLayer");
                              }
                            }
                            pads.add(boardPad);
                            board.addSolderPastePad(boardPad);
                          }
                        } else {
                          if (e instanceof org.openpnp.model.eagle.xml.Pad) {
                          } else {
                            if (e instanceof org.openpnp.model.eagle.xml.Polygon) {
                              if (((org.openpnp.model.eagle.xml.Polygon) e).getLayer().equalsIgnoreCase(tCreamLayer) || ((org.openpnp.model.eagle.xml.Polygon) e).getLayer().equalsIgnoreCase(bCreamLayer)) {
                                logger.info("Warning: " + file + " contains a Polygon pad - this functionality has been implmented as the smallest bounded rectangle and may over paste the area");
                                logger.info("Layer" + ((org.openpnp.model.eagle.xml.Polygon) e).getLayer().toString());
                                Double vertex_x_min = 0.0;
                                Double vertex_x_max = 0.0;
                                Double vertex_y_min = 0.0;
                                Double vertex_y_max = 0.0;
                                ListIterator<org.openpnp.model.eagle.xml.Vertex> vertex_it = ((org.openpnp.model.eagle.xml.Polygon) e).getVertex().listIterator();
                                while (vertex_it.hasNext()) {
                                  org.openpnp.model.eagle.xml.Vertex vertex = (Vertex) vertex_it.next();
                                  vertex_x_min = Math.min(vertex_x_min, Double.parseDouble(vertex.getX()));
                                  vertex_x_max = Math.max(vertex_x_max, Double.parseDouble(vertex.getX()));
                                  vertex_y_min = Math.min(vertex_y_min, Double.parseDouble(vertex.getY()));
                                  vertex_y_max = Math.max(vertex_y_max, Double.parseDouble(vertex.getY()));
                                  logger.info("Vertex: X=" + vertex.getX() + " y=" + vertex.getY());
                                }
                                Pad.RoundRectangle pad = new Pad.RoundRectangle();
                                pad.setUnits(LengthUnit.Millimeters);
                                pad.setRoundness(0);
                                pad.setHeight((vertex_y_max - vertex_y_min));
                                pad.setWidth((vertex_x_max - vertex_x_min));
                                BoardPad boardPad = new BoardPad(pad, new Location(LengthUnit.Millimeters, x + (vertex_x_max + vertex_x_min) / 2, y + (vertex_y_max + vertex_y_min) / 2, 0, 0));
                                logger.info("Pad generated width is " + pad.getWidth() + " height " + pad.getHeight() + " centered at x = " + boardPad.getLocation().getX() + " y = " + boardPad.getLocation().getY());
                                boardPad.setName("Polygon ");
                                if (((org.openpnp.model.eagle.xml.Polygon) e).getLayer().equalsIgnoreCase(tCreamLayer)) {
                                  boardPad.setSide(Side.Top);
                                } else {
                                  boardPad.setSide(Side.Bottom);
                                }
                                pads.add(boardPad);
                                board.addSolderPastePad(boardPad);
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          placement.setSide(element_side);
          placements.add(placement);
          board.addPlacement(placement);
        }
      }
    }
    if (boardToProcess.library != null) {
    }
    if (boardToProcess.schematic != null) {
    }
    return placements;
  }

  class Dlg extends JDialog {
    private JTextField textFieldBoardFile;

    private final Action browseBoardFileAction = new SwingAction();

    private final Action importAction = new SwingAction_2();

    private final Action cancelAction = new SwingAction_3();

    private JCheckBox chckbxCreateMissingParts;

    private JCheckBox chckbxImportTop;

    private JCheckBox chckbxImportBottom;

    public Dlg(Frame parent) {
      super(parent, DESCRIPTION, true);
      getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
      JPanel panel = new JPanel();
      panel.setBorder(new TitledBorder(null, "Files", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      getContentPane().add(panel);
      panel.setLayout(new FormLayout(new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC, FormSpecs.RELATED_GAP_COLSPEC, ColumnSpec.decode("default:grow"), FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC }, new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC }));
      JLabel lblBoardFilebrd = new JLabel("Eagle PCB Board File (.brd)");
      panel.add(lblBoardFilebrd, "2, 2, right, default");
      textFieldBoardFile = new JTextField();
      panel.add(textFieldBoardFile, "4, 2, fill, default");
      textFieldBoardFile.setColumns(10);
      JButton btnBrowse = new JButton("Browse");
      btnBrowse.setAction(browseBoardFileAction);
      panel.add(btnBrowse, "6, 2");
      JPanel panel_1 = new JPanel();
      panel_1.setBorder(new TitledBorder(null, "Options", TitledBorder.LEADING, TitledBorder.TOP, null, null));
      getContentPane().add(panel_1);
      panel_1.setLayout(new FormLayout(new ColumnSpec[] { FormSpecs.RELATED_GAP_COLSPEC, FormSpecs.DEFAULT_COLSPEC }, new RowSpec[] { FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC, FormSpecs.RELATED_GAP_ROWSPEC, FormSpecs.DEFAULT_ROWSPEC }));
      chckbxCreateMissingParts = new JCheckBox("Create Missing Parts");
      chckbxCreateMissingParts.setSelected(true);
      panel_1.add(chckbxCreateMissingParts, "2, 2");
      chckbxImportTop = new JCheckBox("Import Parts on the Top of the board");
      chckbxImportTop.setSelected(true);
      panel_1.add(chckbxImportTop, "2, 4");
      chckbxImportBottom = new JCheckBox("Import Parts on the Bottom of the board");
      chckbxImportBottom.setSelected(true);
      panel_1.add(chckbxImportBottom, "2, 6");
      JSeparator separator = new JSeparator();
      getContentPane().add(separator);
      JPanel panel_2 = new JPanel();
      FlowLayout flowLayout = (FlowLayout) panel_2.getLayout();
      flowLayout.setAlignment(FlowLayout.RIGHT);
      getContentPane().add(panel_2);
      JButton btnCancel = new JButton("Cancel");
      btnCancel.setAction(cancelAction);
      panel_2.add(btnCancel);
      JButton btnImport = new JButton("Import");
      btnImport.setAction(importAction);
      panel_2.add(btnImport);
      setSize(400, 400);
      setLocationRelativeTo(parent);
      JRootPane rootPane = getRootPane();
      KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
      InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
      inputMap.put(stroke, "ESCAPE");
      rootPane.getActionMap().put("ESCAPE", cancelAction);
    }

    private class SwingAction extends AbstractAction {
      public SwingAction() {
        putValue(NAME, "Browse");
        putValue(SHORT_DESCRIPTION, "Browse");
      }

      public void actionPerformed(ActionEvent e) {
        FileDialog fileDialog = new FileDialog(Dlg.this);
        fileDialog.setFilenameFilter(new FilenameFilter() {
          @Override public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".brd");
          }
        });
        fileDialog.setVisible(true);
        if (fileDialog.getFile() == null) {
          return;
        }
        File file = new File(new File(fileDialog.getDirectory()), fileDialog.getFile());
        textFieldBoardFile.setText(file.getAbsolutePath());
      }
    }

    private class SwingAction_2 extends AbstractAction {
      public SwingAction_2() {
        putValue(NAME, "Import");
        putValue(SHORT_DESCRIPTION, "Import");
      }

      public void actionPerformed(ActionEvent e) {
        boardFile = new File(textFieldBoardFile.getText());
        board = new Board();
        List<Placement> placements = new ArrayList<Placement>();
        try {
          if (boardFile.exists()) {
            if (chckbxImportTop.isSelected() && chckbxImportBottom.isSelected()) {
              placements.addAll(parseFile(boardFile, null, chckbxCreateMissingParts.isSelected()));
            } else {
              if (chckbxImportTop.isSelected()) {
                placements.addAll(parseFile(boardFile, Side.Top, chckbxCreateMissingParts.isSelected()));
              } else {
                if (chckbxImportBottom.isSelected()) {
                  placements.addAll(parseFile(boardFile, Side.Bottom, chckbxCreateMissingParts.isSelected()));
                }
              }
            }
          }
        } catch (Exception e1) {
          MessageBoxes.errorBox(Dlg.this, "Import Error", e1);
          return;
        }
        setVisible(false);
      }
    }

    private class SwingAction_3 extends AbstractAction {
      public SwingAction_3() {
        putValue(NAME, "Cancel");
        putValue(SHORT_DESCRIPTION, "Cancel");
      }

      public void actionPerformed(ActionEvent e) {
        setVisible(false);
      }
    }
  }
}