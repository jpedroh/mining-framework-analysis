package org.spoutcraft.launcher.technic.skin;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.JComponent;
import org.apache.commons.io.FileUtils;
import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.skin.MetroLoginFrame;
import org.spoutcraft.launcher.technic.AddPack;
import org.spoutcraft.launcher.technic.InstalledCustom;
import org.spoutcraft.launcher.technic.InstalledPack;
import org.spoutcraft.launcher.technic.InstalledRest;
import org.spoutcraft.launcher.technic.rest.RestAPI;
import org.spoutcraft.launcher.technic.rest.info.CustomInfo;
import org.spoutcraft.launcher.technic.rest.info.RestInfo;

public class ModpackSelector extends JComponent implements ActionListener {
  private static final long serialVersionUID = 1L;

  private static final String PACK_SELECT_ACTION = "packselect";

  private ImportOptions importOptions = null;

  private final MetroLoginFrame frame;

  private List<InstalledPack> installedPacks = new ArrayList<InstalledPack>();

  private List<PackButton> buttons = new ArrayList<PackButton>(7);

  private final int height = 170;

  private final int width = 880;

  private final int bigWidth = 180;

  private final int bigHeight = 110;

  private final float smallScale = 0.7F;

  private final int spacing = 15;

  private final int smallWidth = (int) (bigWidth * smallScale);

  private final int smallHeight = (int) (bigHeight * smallScale);

  private final int bigX = (width / 2) - (bigWidth / 2);

  private final int bigY = (height / 2) - (bigHeight / 2);

  private final int smallY = (height / 2) - (smallHeight / 2);

  private int index;

  public ModpackSelector(MetroLoginFrame frame) {
    this.frame = frame;
    this.index = -1;
    for (int i = 0; i < 7; i++) {
      PackButton button = new PackButton();
      buttons.add(button);
      button.setActionCommand(PACK_SELECT_ACTION);
      button.addActionListener(this);
      if (i == 3) {
        button.setBounds(bigX, bigY, bigWidth, bigHeight);
        button.setIndex(0);
      } else {
        if (i < 3) {
          int smallX = bigX - ((i + 1) * (smallWidth + spacing));
          button.setBounds(smallX, smallY, smallWidth, smallHeight);
          button.setIndex((i + 1) * -1);
        } else {
          if (i > 3) {
            int smallX = bigX + bigWidth + spacing + ((i - 4) * (smallWidth + spacing));
            button.setBounds(smallX, smallY, smallWidth, smallHeight);
            button.setIndex(i - 3);
          }
        }
      }
      this.add(button);
    }
  }

  public void setupModpackButtons() throws IOException {
    List<RestInfo> modpacks = RestAPI.getModpacks();
    for (RestInfo info : modpacks) {
      installedPacks.add(new InstalledRest(info));
    }
    for (String pack : Settings.getInstalledPacks()) {
      if (Settings.isPackCustom(pack)) {
        try {
          CustomInfo info = RestAPI.getCustomModpack(Settings.getCustomURL(pack));
          installedPacks.add(new InstalledCustom(info));
        } catch (RestfulAPIException e) {
        }
      }
    }
    installedPacks.add(new AddPack());
    selectPack(0);
  }

  public void addPack(CustomInfo info) {
    try {
      int loc = installedPacks.size() - 1;
      installedPacks.add(loc, new InstalledCustom(info));
      selectPack(loc);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void removePack() {
    if (getSelectedPack() instanceof InstalledCustom) {
      InstalledPack pack = installedPacks.remove(getIndex());
      String dir = Settings.getPackDirectory(pack.getName());
      File file = new File(dir);
      if (file.exists()) {
        try {
          FileUtils.deleteDirectory(file);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      selectPack(0);
    }
  }

  public int getIndex() {
    return this.index;
  }

  public void selectPack(String pack) {
    for (int i = 0; i < installedPacks.size(); i++) {
      InstalledPack installed = installedPacks.get(i);
      if (installed.getName().equals(pack)) {
        selectPack(i);
      }
    }
  }

  public void selectPack(int index) {
    if (index >= installedPacks.size()) {
      selectPack(index - installedPacks.size());
    } else {
      if (index < 0) {
        selectPack(installedPacks.size() + index);
      } else {
        this.index = index;
      }
    }
    InstalledPack selected = installedPacks.get(getIndex());
    frame.getBackgroundImage().setIcon(selected.getBackground());
    frame.setIconImage(selected.getIcon());
    frame.setTitle(selected.getDisplayName());
    buttons.get(3).setIcon(selected.getLogo(bigWidth, bigHeight));
    ListIterator<InstalledPack> iterator = installedPacks.listIterator(getIndex());
    for (int i = 0; i < 3; i++) {
      if (!iterator.hasPrevious()) {
        iterator = installedPacks.listIterator(installedPacks.size());
      }
      InstalledPack pack = iterator.previous();
      buttons.get(i).setIcon(pack.getLogo(smallWidth, smallHeight));
    }
    iterator = installedPacks.listIterator(getIndex() + 1);
    for (int i = 4; i < 7; i++) {
      if (!iterator.hasNext()) {
        iterator = installedPacks.listIterator(0);
      }
      InstalledPack pack = iterator.next();
      buttons.get(i).setIcon(pack.getLogo(smallWidth, smallHeight));
    }
    if (getSelectedPack() instanceof AddPack) {
      frame.setButtonEnable(frame.getPackOptionsBtn(), false);
      frame.setButtonEnable(frame.getPackRemoveBtn(), false);
      frame.setLabelVisible(frame.getPackShadow(), false);
      frame.setLabelVisible(frame.getCustomName(), false);
    } else {
      if (getSelectedPack() instanceof InstalledRest) {
        frame.setButtonEnable(frame.getPackOptionsBtn(), true);
        frame.setButtonEnable(frame.getPackRemoveBtn(), false);
        frame.setLabelVisible(frame.getPackShadow(), true);
        frame.setLabelVisible(frame.getCustomName(), false);
      } else {
        if (((InstalledCustom) getSelectedPack()).getLogoUrl().equals("")) {
          frame.setCustomName(getSelectedPack().getDisplayName());
          frame.setLabelVisible(frame.getCustomName(), true);
        } else {
          frame.setLabelVisible(frame.getCustomName(), false);
        }
        frame.setButtonEnable(frame.getPackOptionsBtn(), true);
        frame.setButtonEnable(frame.getPackRemoveBtn(), true);
        frame.setLabelVisible(frame.getPackShadow(), true);
      }
    }
    this.repaint();
  }

  public void selectNextPack() {
    selectPack(getIndex() + 1);
  }

  public void selectPreviousPack() {
    selectPack(getIndex() - 1);
  }

  public InstalledPack getSelectedPack() {
    return installedPacks.get(index);
  }

  @Override public void actionPerformed(ActionEvent e) {
    if (e.getSource() instanceof JComponent) {
      action(e.getActionCommand(), (JComponent) e.getSource());
    }
  }

  public void action(String action, JComponent c) {
    if (action.equals(PACK_SELECT_ACTION) && c instanceof PackButton) {
      PackButton button = (PackButton) c;
      if (button.getIndex() == 0 && getSelectedPack() instanceof AddPack) {
        if (importOptions == null || !importOptions.isVisible()) {
          importOptions = new ImportOptions();
          importOptions.setModal(true);
          importOptions.setVisible(true);
        }
      } else {
        selectPack(getIndex() + button.getIndex());
      }
    }
  }
}