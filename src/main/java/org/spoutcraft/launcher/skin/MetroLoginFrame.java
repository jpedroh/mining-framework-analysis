package org.spoutcraft.launcher.skin;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import javax.swing.*;
import net.minecraft.Launcher;
import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.skin.components.BackgroundImage;
import org.spoutcraft.launcher.skin.components.DynamicButton;
import org.spoutcraft.launcher.skin.components.HyperlinkJLabel;
import org.spoutcraft.launcher.skin.components.ImageHyperlinkButton;
import org.spoutcraft.launcher.skin.components.LiteButton;
import org.spoutcraft.launcher.skin.components.LitePasswordBox;
import org.spoutcraft.launcher.skin.components.LiteProgressBar;
import org.spoutcraft.launcher.skin.components.LiteTextBox;
import org.spoutcraft.launcher.skin.components.LoginFrame;
import org.spoutcraft.launcher.skin.components.TransparentJLabel;
import org.spoutcraft.launcher.technic.AddPack;
import org.spoutcraft.launcher.technic.PackInfo;
import org.spoutcraft.launcher.technic.RestInfo;
import org.spoutcraft.launcher.technic.skin.ImageButton;
import org.spoutcraft.launcher.technic.skin.LauncherOptions;
import org.spoutcraft.launcher.technic.skin.ModpackOptions;
import org.spoutcraft.launcher.technic.skin.ModpackSelector;
import org.spoutcraft.launcher.util.Download;
import org.spoutcraft.launcher.util.Download.Result;
import org.spoutcraft.launcher.util.DownloadUtils;
import org.spoutcraft.launcher.util.ImageUtils;
import org.spoutcraft.launcher.util.OperatingSystem;
import org.spoutcraft.launcher.util.ResourceUtils;
import org.spoutcraft.launcher.util.Utils;

public class MetroLoginFrame extends LoginFrame implements ActionListener, KeyListener, MouseWheelListener {
  private static final long serialVersionUID = 1L;

  private static final int FRAME_WIDTH = 880;

  private static final int FRAME_HEIGHT = 520;

  private static final String OPTIONS_ACTION = "options";

  private static final String PACK_OPTIONS_ACTION = "packoptions";

  private static final String PACK_REMOVE_ACTION = "packremove";

  private static final String EXIT_ACTION = "exit";

  private static final String PACK_LEFT_ACTION = "packleft";

  private static final String PACK_RIGHT_ACTION = "packright";

  private static final String LOGIN_ACTION = "login";

  private static final String IMAGE_LOGIN_ACTION = "image_login";

  private static final String REMOVE_USER = "remove";

  private final Map<JButton, DynamicButton> removeButtons = new HashMap<JButton, DynamicButton>();

  private final Map<String, DynamicButton> userButtons = new HashMap<String, DynamicButton>();

  private LiteTextBox name;

  private LitePasswordBox pass;

  private LiteButton login;

  private JCheckBox remember;

  private LiteProgressBar progressBar;

  private LauncherOptions launcherOptions = null;

  private ModpackOptions packOptions = null;

  private ModpackSelector packSelector;

  private BackgroundImage packBackground;

  private ImageButton packOptionsBtn;

  private ImageButton packRemoveBtn;

  private ImageHyperlinkButton platform;

  private JLabel packShadow;

  private JLabel customName;

  private long previous = 0L;

  public MetroLoginFrame() {
    initComponents();
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds((dim.width - FRAME_WIDTH) / 2, (dim.height - FRAME_HEIGHT) / 2, FRAME_WIDTH, FRAME_HEIGHT);
    setResizable(false);
    packBackground = new BackgroundImage(this, FRAME_WIDTH, FRAME_HEIGHT);
    this.addMouseListener(packBackground);
    this.addMouseMotionListener(packBackground);
    this.addMouseWheelListener(this);
    getContentPane().add(packBackground);
    this.setUndecorated(true);
  }

  private void initComponents() {
    Font minecraft = getMinecraftFont(12);
    TransparentJLabel loginStrip = new TransparentJLabel();
    loginStrip.setBounds(0, 379, FRAME_WIDTH, 108);
    loginStrip.setTransparency(0.95F);
    loginStrip.setHoverTransparency(0.95F);
    setIcon(loginStrip, "loginstrip.png", loginStrip.getWidth(), loginStrip.getHeight());
    packShadow = new JLabel();
    packShadow.setBounds(FRAME_WIDTH / 2 - (176 / 2), FRAME_HEIGHT / 2 + 45, 176, 38);
    setIcon(packShadow, "packShadow.png", packShadow.getWidth(), packShadow.getHeight());
    name = new LiteTextBox(this, "Username...");
    name.setBounds(620, loginStrip.getY() + loginStrip.getHeight() / 2 - 40, 115, 24);
    name.setFont(minecraft);
    name.addKeyListener(this);
    pass = new LitePasswordBox(this, "Password...");
    pass.setBounds(620, loginStrip.getY() + loginStrip.getHeight() / 2 - 12, 115, 24);
    pass.setFont(minecraft);
    pass.addKeyListener(this);
    remember = new JCheckBox("Remember");
    remember.setBounds(745, loginStrip.getY() + loginStrip.getHeight() / 2 - 12, 115, 24);
    remember.setFont(minecraft);
    remember.setOpaque(false);
    remember.setBorderPainted(false);
    remember.setFocusPainted(false);
    remember.setContentAreaFilled(false);
    remember.setBorder(null);
    remember.setForeground(Color.WHITE);
    remember.setHorizontalTextPosition(SwingConstants.RIGHT);
    remember.setIconTextGap(10);
    remember.addKeyListener(this);
    JLabel logo = new JLabel();
    logo.setBounds(FRAME_WIDTH / 2 - 200, 15, 400, 109);
    setIcon(logo, "techniclauncher.png", logo.getWidth(), logo.getHeight());
    JLabel selectorBackground = new JLabel();
    selectorBackground.setBounds(0, FRAME_HEIGHT / 2 - 84, FRAME_WIDTH, 168);
    setIcon(selectorBackground, "selectorBackground.png", selectorBackground.getWidth(), selectorBackground.getHeight());
    ImageButton switchLeft = new ImageButton(getIcon("selectLeft.png", 22, 168), getIcon("selectLeftInverted.png", 22, 168));
    switchLeft.setBounds(0, FRAME_HEIGHT / 2 - 83, 22, 167);
    switchLeft.setActionCommand(PACK_LEFT_ACTION);
    switchLeft.addActionListener(this);
    ImageButton switchRight = new ImageButton(getIcon("selectRight.png", 22, 168), getIcon("selectRightInverted.png", 22, 168));
    switchRight.setBounds(FRAME_WIDTH - 22, FRAME_HEIGHT / 2 - 83, 22, 167);
    switchRight.setActionCommand(PACK_RIGHT_ACTION);
    switchRight.addActionListener(this);
    progressBar = new LiteProgressBar();
    progressBar.setBounds((FRAME_WIDTH / 2) - (395 / 2), 130, 395, 23);
    progressBar.setVisible(false);
    progressBar.setStringPainted(true);
    progressBar.setOpaque(true);
    progressBar.setTransparency(0.70F);
    progressBar.setHoverTransparency(0.70F);
    progressBar.setFont(minecraft);
    Font largerMinecraft;
    if (OperatingSystem.getOS().isUnix()) {
      largerMinecraft = minecraft.deriveFont((float) 18);
    } else {
      largerMinecraft = minecraft.deriveFont((float) 20);
    }
    HyperlinkJLabel home = new HyperlinkJLabel("Home", "http://www.technicpack.net");
    home.setFont(largerMinecraft);
    home.setBounds(545, 35, 65, 20);
    home.setForeground(Color.WHITE);
    home.setOpaque(false);
    home.setTransparency(0.70F);
    home.setHoverTransparency(1F);
    JButton forums = new ImageHyperlinkButton("http://forums.technicpack.net/");
    forums.setToolTipText("Visit the forums");
    forums.setBounds(9, loginStrip.getY() + 9, 90, 90);
    ImageIcon forumIcon = getIcon("forumsButton.gif");
    forums.setIcon(forumIcon);
    forums.setRolloverIcon(forumIcon);
    forums.setPressedIcon(forumIcon);
    forums.setSelectedIcon(forumIcon);
    forums.setRolloverSelectedIcon(forumIcon);
    JButton donate = new ImageHyperlinkButton("http://www.technicpack.net/donate/");
    donate.setToolTipText("Donate to the modders");
    donate.setBounds(9 + forums.getWidth() + 9, loginStrip.getY() + 9, 90, 90);
    ImageIcon donateIcon = getIcon("donateButton.png");
    donate.setIcon(donateIcon);
    donate.setRolloverIcon(donateIcon);
    donate.setPressedIcon(donateIcon);
    donate.setSelectedIcon(donateIcon);
    donate.setRolloverSelectedIcon(donateIcon);
    HyperlinkJLabel issues = new HyperlinkJLabel("Issues", "http://forums.technicpack.net/forums/bug-reports.81/");
    issues.setFont(largerMinecraft);
    issues.setBounds(733, 35, 85, 20);
    issues.setForeground(Color.WHITE);
    issues.setOpaque(false);
    issues.setTransparency(0.70F);
    issues.setHoverTransparency(1F);
    ImageButton options = new ImageButton(getIcon("gear.png", 28, 28), getIcon("gearInverted.png", 28, 28));
    options.setBounds(FRAME_WIDTH - 34 * 2, 6, 28, 28);
    options.setActionCommand(OPTIONS_ACTION);
    options.addActionListener(this);
    options.addKeyListener(this);
    packOptionsBtn = new ImageButton(getIcon("packOptions.png", 20, 21), getIcon("packOptionsInverted.png", 20, 21));
    packOptionsBtn.setBounds(FRAME_WIDTH / 2 - 90, FRAME_HEIGHT / 2 + 59, 20, 21);
    packOptionsBtn.setActionCommand(PACK_OPTIONS_ACTION);
    packOptionsBtn.addActionListener(this);
    platform = new ImageHyperlinkButton("http://www.beta.technicpack.net/");
    platform.setIcon(getIcon("openPlatformPage.png", 20, 20));
    platform.setBounds(FRAME_WIDTH / 2 - 65, FRAME_HEIGHT / 2 + 59, 20, 20);
    packRemoveBtn = new ImageButton(getIcon("packDelete.png", 20, 21), getIcon("packDeleteInverted.png", 20, 21));
    packRemoveBtn.setBounds(FRAME_WIDTH / 2 + 70, FRAME_HEIGHT / 2 + 59, 20, 21);
    packRemoveBtn.setActionCommand(PACK_REMOVE_ACTION);
    packRemoveBtn.addActionListener(this);
    login = new LiteButton("Launch");
    login.setBounds(745, loginStrip.getY() + loginStrip.getHeight() / 2 - 40, 115, 24);
    login.setFont(minecraft);
    login.setActionCommand(LOGIN_ACTION);
    login.addActionListener(this);
    login.addKeyListener(this);
    ImageButton exit = new ImageButton(getIcon("quit.png", 28, 28), getIcon("quitHover.png", 28, 28));
    exit.setBounds(FRAME_WIDTH - 34, 6, 28, 28);
    exit.setActionCommand(EXIT_ACTION);
    exit.addActionListener(this);
    JButton steam = new ImageHyperlinkButton("http://steamcommunity.com/groups/technic-pack");
    steam.setRolloverIcon(getIcon("steamInverted.png", 28, 28));
    steam.setToolTipText("Game with us on Steam");
    steam.setBounds(6, 6, 28, 28);
    setIcon(steam, "steam.png", 28);
    JButton twitter = new ImageHyperlinkButton("https://twitter.com/TechnicPack");
    twitter.setRolloverIcon(getIcon("twitterInverted.png", 28, 28));
    twitter.setToolTipText("Follow us on Twitter");
    twitter.setBounds(6 + 34 * 3, 6, 28, 28);
    setIcon(twitter, "twitter.png", 28);
    JButton facebook = new ImageHyperlinkButton("https://www.facebook.com/TechnicPack");
    facebook.setRolloverIcon(getIcon("facebookInverted.png", 28, 28));
    facebook.setToolTipText("Like us on Facebook");
    facebook.setBounds(6 + 34 * 2, 6, 28, 28);
    setIcon(facebook, "facebook.png", 28);
    JButton youtube = new ImageHyperlinkButton("http://www.youtube.com/user/kakermix");
    youtube.setRolloverIcon(getIcon("youtubeInverted.png", 28, 28));
    youtube.setToolTipText("Subscribe to our videos");
    youtube.setBounds(6 + 34, 6, 28, 28);
    setIcon(youtube, "youtube.png", 28);
    Container contentPane = getContentPane();
    contentPane.setLayout(null);
    packSelector = new ModpackSelector(this);
    packSelector.setBounds(0, (FRAME_HEIGHT / 2) - 85, FRAME_WIDTH, 170);
    customName = new JLabel("", JLabel.CENTER);
    customName.setBounds(FRAME_WIDTH / 2 - (192 / 2), FRAME_HEIGHT / 2 + (110 / 2) - 30, 192, 30);
    customName.setFont(minecraft.deriveFont(14F));
    customName.setVisible(false);
    customName.setForeground(Color.white);
    java.util.List<String> savedUsers = getSavedUsernames();
    int users = Math.min(5, this.getSavedUsernames().size());
    for (int i = 0; i < users; i++) {
      String accountName = savedUsers.get(i);
      String userName = this.getUsername(accountName);
      ImageIcon image = getIcon("face.png");
      File face = new File(Utils.getAssetsDirectory(), userName + ".png");
      if (face.exists()) {
        image = new ImageIcon(face.getAbsolutePath());
      }
      DynamicButton userButton = new DynamicButton(this, image, 1, accountName, userName);
      userButton.setFont(minecraft.deriveFont(12F));
      userButton.setBounds(FRAME_WIDTH - ((i + 1) * 70), FRAME_HEIGHT - 57, 45, 45);
      contentPane.add(userButton);
      userButton.setActionCommand(IMAGE_LOGIN_ACTION);
      userButton.addActionListener(this);
      setIcon(userButton.getRemoveIcon(), "remove.png", 16);
      userButton.getRemoveIcon().addActionListener(this);
      userButton.getRemoveIcon().setActionCommand(REMOVE_USER);
      removeButtons.put(userButton.getRemoveIcon(), userButton);
      userButtons.put(userName, userButton);
    }
    contentPane.add(switchLeft);
    contentPane.add(switchRight);
    contentPane.add(customName);
    contentPane.add(packOptionsBtn);
    contentPane.add(packRemoveBtn);
    contentPane.add(platform);
    contentPane.add(packSelector);
    contentPane.add(selectorBackground);
    contentPane.add(name);
    contentPane.add(pass);
    contentPane.add(remember);
    contentPane.add(login);
    contentPane.add(steam);
    contentPane.add(twitter);
    contentPane.add(facebook);
    contentPane.add(youtube);
    contentPane.add(forums);
    contentPane.add(donate);
    contentPane.add(logo);
    contentPane.add(loginStrip);
    contentPane.add(options);
    contentPane.add(exit);
    contentPane.add(progressBar);
    setFocusTraversalPolicy(new LoginFocusTraversalPolicy());
  }

  public void setUser(String name) {
    if (name != null) {
      DynamicButton user = userButtons.get(this.getUsername(name));
      if (user != null) {
        user.doClick();
      }
    }
  }

  public ModpackSelector getSelector() {
    return packSelector;
  }

  public BackgroundImage getBackgroundImage() {
    return packBackground;
  }

  public static ImageIcon getIcon(String iconName) {
    return new ImageIcon(Launcher.class.getResource("/org/spoutcraft/launcher/resources/" + iconName));
  }

  public static ImageIcon getIcon(String iconName, int w, int h) {
    try {
      return new ImageIcon(ImageUtils.scaleImage(ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/" + iconName)), w, h));
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private void setIcon(JButton button, String iconName, int size) {
    try {
      button.setIcon(new ImageIcon(ImageUtils.scaleImage(ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/" + iconName)), size, size)));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void setIcon(JLabel label, String iconName, int w, int h) {
    try {
      label.setIcon(new ImageIcon(ImageUtils.scaleImage(ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/" + iconName)), w, h)));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void updateFaces() {
    for (String user : userButtons.keySet()) {
      BufferedImage image = getUserImage(user);
      if (image != null) {
        userButtons.get(user).updateIcon(new ImageIcon(image));
      }
    }
  }

  private BufferedImage getUserImage(String user) {
    File file = new File(Utils.getAssetsDirectory(), user + ".png");
    try {
      Download download = DownloadUtils.downloadFile("http://skins.technicpack.net/helm/" + user + "/100", file.getAbsolutePath());
      if (download.getResult().equals(Result.SUCCESS)) {
        return ImageIO.read(download.getOutFile());
      }
    } catch (IOException e) {
      if (Utils.getStartupParameters().isDebugMode()) {
        org.spoutcraft.launcher.api.Launcher.getLogger().log(Level.INFO, "Error downloading user face image: " + user, e);
      } else {
        org.spoutcraft.launcher.api.Launcher.getLogger().log(Level.INFO, "Error downloading user face image: " + user);
      }
    }
    return null;
  }

  @Override public void actionPerformed(ActionEvent e) {
    if (e.getSource() instanceof JComponent) {
      action(e.getActionCommand(), (JComponent) e.getSource());
    }
  }

  private void action(String action, JComponent c) {
    if (action.equals(OPTIONS_ACTION)) {
      if (launcherOptions == null || !launcherOptions.isVisible()) {
        launcherOptions = new LauncherOptions();
        launcherOptions.setModal(true);
        launcherOptions.setVisible(true);
      }
    } else {
      if (action.equals(PACK_REMOVE_ACTION)) {
        int result = JOptionPane.showConfirmDialog(c, "Are you sure you want to remove this pack?", "Remove Pack", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
          getSelector().removePack();
        }
      } else {
        if (action.equals(PACK_OPTIONS_ACTION)) {
          if (packOptions == null || !packOptions.isVisible()) {
            packOptions = new ModpackOptions(getSelector().getSelectedPack());
            packOptions.setModal(true);
            packOptions.setVisible(true);
          }
        } else {
          if (action.equals(EXIT_ACTION)) {
            System.exit(0);
          } else {
            if (action.equals(PACK_LEFT_ACTION)) {
              getSelector().selectPreviousPack();
            } else {
              if (action.equals(PACK_RIGHT_ACTION)) {
                getSelector().selectNextPack();
              } else {
                if (action.equals(LOGIN_ACTION)) {
                  PackInfo pack = getSelector().getSelectedPack();
                  if (pack instanceof AddPack) {
                    return;
                  }
                  String pass = new String(this.pass.getPassword());
                  if (getSelectedUser().length() > 0 && pass.length() > 0) {
                    lockLoginButton(false);
                    this.doLogin(getSelectedUser(), pass);
                    if (remember.isSelected()) {
                      saveUsername(getSelectedUser(), pass);
                      Settings.setLastUser(getSelectedUser());
                      Settings.getYAML().save();
                    }
                  }
                } else {
                  if (action.equals(IMAGE_LOGIN_ACTION)) {
                    DynamicButton userButton = (DynamicButton) c;
                    this.name.setText(userButton.getAccount());
                    this.pass.setText(this.getSavedPassword(userButton.getAccount()));
                    this.remember.setSelected(true);
                    pass.setLabelVisible(false);
                    name.setLabelVisible(false);
                  } else {
                    if (action.equals(REMOVE_USER)) {
                      DynamicButton userButton = removeButtons.get((JButton) c);
                      this.removeAccount(userButton.getAccount());
                      userButton.setVisible(false);
                      userButton.setEnabled(false);
                      getContentPane().remove(userButton);
                      c.setVisible(false);
                      c.setEnabled(false);
                      getContentPane().remove(c);
                      removeButtons.remove(c);
                      writeUsernameList();
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

  @Override public void stateChanged(final String status, final float progress) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override public void run() {
        int intProgress = Math.round(progress);
        progressBar.setValue(intProgress);
        String text = status;
        if (text.length() > 60) {
          text = text.substring(0, 60) + "...";
        }
        progressBar.setString(intProgress + "% " + text);
      }
    });
  }

  @Override public JProgressBar getProgressBar() {
    return progressBar;
  }

  @Override public void disableForm() {
  }

  @Override public void enableForm() {
  }

  @Override public String getSelectedUser() {
    return this.name.getText();
  }

  public ImageButton getPackOptionsBtn() {
    return packOptionsBtn;
  }

  public ImageButton getPackRemoveBtn() {
    return packRemoveBtn;
  }

  public JLabel getPackShadow() {
    return packShadow;
  }

  public JLabel getCustomName() {
    return customName;
  }

  public ImageHyperlinkButton getPlatform() {
    return platform;
  }

  public void enableComponent(JComponent component, boolean enable) {
    component.setVisible(enable);
    component.setEnabled(enable);
  }

  public void setCustomName(String packName) {
    customName.setText(packName);
  }

  public void lockLoginButton(boolean unlock) {
    if (unlock) {
      login.setText("Login");
    } else {
      login.setText("Launching...");
    }
    login.setEnabled(unlock);
    packRemoveBtn.setEnabled(unlock);
    packOptionsBtn.setEnabled(unlock);
  }

  public Image newBackgroundImage(RestInfo modpack) {
    try {
      Image image = modpack.getBackground().getScaledInstance(FRAME_WIDTH, FRAME_HEIGHT, Image.SCALE_SMOOTH);
      return image;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private class LoginFocusTraversalPolicy extends FocusTraversalPolicy {
    @Override public Component getComponentAfter(Container con, Component c) {
      if (c == name) {
        return pass;
      } else {
        if (c == pass) {
          return remember;
        } else {
          if (c == remember) {
            return login;
          } else {
            if (c == login) {
              return name;
            }
          }
        }
      }
      return getFirstComponent(con);
    }

    @Override public Component getComponentBefore(Container con, Component c) {
      if (c == name) {
        return login;
      } else {
        if (c == pass) {
          return name;
        } else {
          if (c == remember) {
            return pass;
          } else {
            if (c == login) {
              return remember;
            }
          }
        }
      }
      return getFirstComponent(con);
    }

    @Override public Component getFirstComponent(Container c) {
      return name;
    }

    @Override public Component getLastComponent(Container c) {
      return login;
    }

    @Override public Component getDefaultComponent(Container c) {
      return name;
    }
  }

  @Override public void keyTyped(KeyEvent e) {
  }

  @Override public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
      if (e.getComponent() == login || e.getComponent() == name || e.getComponent() == pass) {
        action(LOGIN_ACTION, (JComponent) e.getComponent());
      } else {
        if (e.getComponent() == remember) {
          remember.setSelected(!remember.isSelected());
        }
      }
    } else {
      if (e.getKeyCode() == KeyEvent.VK_LEFT) {
        action(PACK_LEFT_ACTION, null);
      } else {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
          action(PACK_RIGHT_ACTION, null);
        }
      }
    }
  }

  @Override public void keyReleased(KeyEvent e) {
  }

  @Override public void mouseWheelMoved(MouseWheelEvent e) {
    if (e.getWhen() != previous) {
      if (e.getUnitsToScroll() > 0) {
        getSelector().selectNextPack();
      } else {
        if (e.getUnitsToScroll() < 0) {
          getSelector().selectPreviousPack();
        }
      }
      this.previous = e.getWhen();
    }
  }
}