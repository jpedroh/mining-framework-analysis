package org.spoutcraft.launcher.skin;
import static org.spoutcraft.launcher.util.ResourceUtils.getResourceAsStream;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.*;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
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
import org.spoutcraft.launcher.technic.ModpackInfo;
import org.spoutcraft.launcher.technic.TechnicRestAPI;
import org.spoutcraft.launcher.technic.skin.ImageButton;
import org.spoutcraft.launcher.technic.skin.ModpackOptions;
import org.spoutcraft.launcher.technic.skin.ModpackSelector;
import org.spoutcraft.launcher.util.ImageUtils;
import org.spoutcraft.launcher.util.OperatingSystem;
import org.spoutcraft.launcher.util.ResourceUtils;

public class MetroLoginFrame extends LoginFrame implements ActionListener, KeyListener {
  private static final long serialVersionUID = 1L;

  private static final int FRAME_WIDTH = 880;

  private static final int FRAME_HEIGHT = 520;

  private static final String OPTIONS_ACTION = "options";

  private static final String PACKOPTIONS_ACTION = "packoptions";

  private static final String EXIT_ACTION = "exit";

  private static final String PACKLEFT_ACTION = "packleft";

  private static final String PACKRIGHT_ACTION = "packright";

  private static final String LOGIN_ACTION = "login";

  private static final String IMAGE_LOGIN_ACTION = "image_login";

  private static final String REMOVE_USER = "remove";

  private final Map<JButton, DynamicButton> removeButtons = new HashMap<JButton, DynamicButton>();

  private LiteTextBox name;

  private LitePasswordBox pass;

  private LiteButton login;

  private JCheckBox remember;

  private LiteProgressBar progressBar;

  private OptionsMenu optionsMenu = null;

  private ModpackOptions packOptions = null;

  private ModpackSelector packSelector;

  private BackgroundImage packBackground;

  public MetroLoginFrame() {
    initComponents();
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    setBounds((dim.width - FRAME_WIDTH) / 2, (dim.height - FRAME_HEIGHT) / 2, FRAME_WIDTH, FRAME_HEIGHT);
    setResizable(false);
    packBackground = new BackgroundImage(this, FRAME_WIDTH, FRAME_HEIGHT);
    this.addMouseListener(packBackground);
    this.addMouseMotionListener(packBackground);
    getContentPane().add(packBackground);
    this.setUndecorated(true);
  }

  private void initComponents() {
    Font minecraft = getMinecraftFont(12);
    name = new LiteTextBox(this, "Username...");
    name.setBounds(602, 414, 140, 24);
    name.setFont(minecraft);
    name.addKeyListener(this);
    pass = new LitePasswordBox(this, "Password...");
    pass.setBounds(602, 443, 140, 24);
    pass.setFont(minecraft);
    pass.addKeyListener(this);
    remember = new JCheckBox("Remember");
    remember.setBounds(755, 443, 110, 24);
    remember.setFont(minecraft);
    remember.setOpaque(false);
    remember.setBorderPainted(false);
    remember.setContentAreaFilled(false);
    remember.setBorder(null);
    remember.setForeground(Color.WHITE);
    remember.addKeyListener(this);
    login = new LiteButton("Login");
    login.setBounds(755, 414, 92, 24);
    login.setFont(minecraft);
    login.setActionCommand(LOGIN_ACTION);
    login.addActionListener(this);
    login.addKeyListener(this);
    JLabel logo = new JLabel();
    logo.setBounds(FRAME_WIDTH / 2 - 200, 15, 400, 109);
    setIcon(logo, "techniclauncher.png", logo.getWidth(), logo.getHeight());
    JLabel selectorBackground = new JLabel();
    selectorBackground.setBounds(0, FRAME_HEIGHT / 2 - 84, FRAME_WIDTH, 168);
    setIcon(selectorBackground, "selectorBackground.png", selectorBackground.getWidth(), selectorBackground.getHeight());
    ImageButton switchLeft = new ImageButton(getIcon("selectLeft.png", 22, 168), getIcon("selectLeftInverted.png", 22, 168));
    switchLeft.setBounds(0, FRAME_HEIGHT / 2 - 84, 22, 168);
    switchLeft.setActionCommand(PACKLEFT_ACTION);
    switchLeft.addActionListener(this);
    ImageButton switchRight = new ImageButton(getIcon("selectRight.png", 22, 168), getIcon("selectRightInverted.png", 22, 168));
    switchRight.setBounds(FRAME_WIDTH - 22, FRAME_HEIGHT / 2 - 84, 22, 168);
    switchRight.setActionCommand(PACKRIGHT_ACTION);
    switchRight.addActionListener(this);
    TransparentJLabel loginStrip = new TransparentJLabel();
    loginStrip.setBounds(0, FRAME_HEIGHT - 107 - 55, FRAME_WIDTH, 107);
    loginStrip.setTransparency(0.95F);
    loginStrip.setHoverTransparency(0.95F);
    setIcon(loginStrip, "loginstrip.png", loginStrip.getWidth(), loginStrip.getHeight());
    progressBar = new LiteProgressBar();
    progressBar.setBounds(8, 130, 395, 23);
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
    forums.setBounds(FRAME_WIDTH - 190, 20, 170, 95);
    setIcon(forums, "forums.png", forums.getWidth(), forums.getHeight());
    JButton donate = new ImageHyperlinkButton("http://www.technicpack.net/donate/");
    donate.setToolTipText("Donate to the modders");
    donate.setBounds(forums.getX() - 180, forums.getY(), 170, 95);
    setIcon(donate, "donate.png", forums.getWidth(), forums.getHeight());
    HyperlinkJLabel issues = new HyperlinkJLabel("Issues", "http://forums.technicpack.net/forums/bug-reports.81/");
    issues.setFont(largerMinecraft);
    issues.setBounds(733, 35, 85, 20);
    issues.setForeground(Color.WHITE);
    issues.setOpaque(false);
    issues.setTransparency(0.70F);
    issues.setHoverTransparency(1F);
    ImageButton options = new ImageButton(getIcon("gear.png", 28, 28), getIcon("gearInverted.png", 28, 28));
    options.setRolloverIcon(getIcon("gearInverted.png", 28, 28));
    options.setBounds(FRAME_WIDTH - 34 * 2, 6, 28, 28);
    options.setActionCommand(OPTIONS_ACTION);
    options.addActionListener(this);
    options.addKeyListener(this);
    ImageButton packOptionsBtn = new ImageButton(getIcon("gear.png", 28, 28), getIcon("gearInverted.png", 28, 28));
    packOptionsBtn.setRolloverIcon(getIcon("gearInverted.png", 28, 28));
    packOptionsBtn.setBounds(FRAME_WIDTH / 2 + 58, 175, 28, 28);
    packOptionsBtn.setActionCommand(PACKOPTIONS_ACTION);
    packOptionsBtn.addActionListener(this);
    ImageButton exit = new ImageButton(getIcon("quit.png", 28, 28), getIcon("quit.png", 28, 28));
    exit.setRolloverIcon(getIcon("quitHover.png", 28, 28));
    exit.setBounds(FRAME_WIDTH - 34, 6, 28, 28);
    exit.setActionCommand(EXIT_ACTION);
    exit.addActionListener(this);
    JButton steam = new ImageHyperlinkButton("http://steamcommunity.com/groups/technic-pack");
    steam.setToolTipText("Game with us on Steam");
    steam.setBounds(6, 6, 28, 28);
    setIcon(steam, "steam.png", 28);
    JButton twitter = new ImageHyperlinkButton("https://twitter.com/TechnicPack");
    twitter.setToolTipText("Follow us on Twitter");
    twitter.setBounds(6 + 34 * 3, 6, 28, 28);
    setIcon(twitter, "twitter.png", 28);
    JButton facebook = new ImageHyperlinkButton("https://www.facebook.com/TechnicPack");
    facebook.setToolTipText("Like us on Facebook");
    facebook.setBounds(6 + 34 * 2, 6, 28, 28);
    setIcon(facebook, "facebook.png", 28);
    JButton youtube = new ImageHyperlinkButton("http://www.youtube.com/user/kakermix");
    youtube.setToolTipText("Subscribe to our videos");
    youtube.setBounds(6 + 34, 6, 28, 28);
    setIcon(youtube, "youtube.png", 28);
    Container contentPane = getContentPane();
    contentPane.setLayout(null);
    packSelector = new ModpackSelector(this);
    packSelector.setBounds(0, (FRAME_HEIGHT / 2) - 85, FRAME_WIDTH, 170);
    java.util.List<String> savedUsers = getSavedUsernames();
    int users = Math.min(5, this.getSavedUsernames().size());
    for (int i = 0; i < users; i++) {
      String accountName = savedUsers.get(i);
      String userName = this.getUsername(accountName);
      DynamicButton userButton = new DynamicButton(this, getImage(userName), 10, accountName, userName);
      userButton.setFont(minecraft.deriveFont(14F));
      userButton.setBounds(FRAME_WIDTH - ((i + 1) * 75), FRAME_HEIGHT - 60, 50, 50);
      contentPane.add(userButton);
      userButton.setActionCommand(IMAGE_LOGIN_ACTION);
      userButton.addActionListener(this);
      setIcon(userButton.getRemoveIcon(), "remove.png", 16);
      userButton.getRemoveIcon().addActionListener(this);
      userButton.getRemoveIcon().setActionCommand(REMOVE_USER);
      removeButtons.put(userButton.getRemoveIcon(), userButton);
    }
    contentPane.add(switchLeft);
    contentPane.add(switchRight);
    contentPane.add(packOptionsBtn);
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
    contentPane.add(logo);
    contentPane.add(loginStrip);
    contentPane.add(options);
    contentPane.add(exit);
    contentPane.add(progressBar);
    setFocusTraversalPolicy(new LoginFocusTraversalPolicy());
  }

  public ModpackSelector getModpackSelector() {
    return packSelector;
  }

  public BackgroundImage getBackgroundImage() {
    return packBackground;
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

  private void setIcon(JButton label, String iconName, int w, int h) {
    try {
      label.setIcon(new ImageIcon(ImageUtils.scaleImage(ImageIO.read(ResourceUtils.getResourceAsStream("/org/spoutcraft/launcher/resources/" + iconName)), w, h)));
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

  private BufferedImage getImage(String user) {
    try {
      URLConnection conn = (new URL("https://minotar.net/helm/" + user + "/100")).openConnection();
      InputStream stream = conn.getInputStream();
      BufferedImage image = ImageIO.read(stream);
      if (image != null) {
        return image;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      return ImageIO.read(getResourceAsStream("/org/spoutcraft/launcher/resources/face.png"));
    } catch (IOException e1) {
      throw new RuntimeException("Error reading backup image", e1);
    }
  }

  @Override public void actionPerformed(ActionEvent e) {
    if (e.getSource() instanceof JComponent) {
      action(e.getActionCommand(), (JComponent) e.getSource());
    }
  }

  private void action(String action, JComponent c) {
    if (action.equals(OPTIONS_ACTION)) {
      if (optionsMenu == null || !optionsMenu.isVisible()) {
        optionsMenu = new OptionsMenu();
        optionsMenu.setModal(true);
        optionsMenu.setVisible(true);
      }
    } else {
      if (action.equals(PACKOPTIONS_ACTION)) {
        if (packOptions == null || !packOptions.isVisible()) {
          packOptions = new ModpackOptions(getModpackSelector().getSelectedPack());
          packOptions.setModal(true);
          packOptions.setVisible(true);
        }
      } else {
        if (action.equals(EXIT_ACTION)) {
          System.exit(0);
        } else {
          if (action.equals(PACKLEFT_ACTION)) {
            getModpackSelector().selectPreviousPack();
            updateFrameTitle();
          } else {
            if (action.equals(PACKRIGHT_ACTION)) {
              getModpackSelector().selectNextPack();
              updateFrameTitle();
              String modpack = getModpackSelector().getSelectedPack().getName();
              String build;
              try {
                build = TechnicRestAPI.getRecommendedBuild(modpack);
                Launcher.getGameUpdater().onModpackBuildChange(TechnicRestAPI.getModpack(getModpackSelector().getSelectedPack(), build));
              } catch (RestfulAPIException e) {
                e.printStackTrace();
              }
            } else {
              if (action.equals(LOGIN_ACTION)) {
                String modpack = getModpackSelector().getSelectedPack().getName();
                String build;
                try {
                  build = TechnicRestAPI.getRecommendedBuild(modpack);
                  Launcher.getGameUpdater().onModpackBuildChange(TechnicRestAPI.getModpack(getModpackSelector().getSelectedPack(), build));
                } catch (RestfulAPIException e) {
                  e.printStackTrace();
                }
                String pass = new String(this.pass.getPassword());
                if (getSelectedUser().length() > 0 && pass.length() > 0) {
                  this.doLogin(getSelectedUser(), pass);
                  if (remember.isSelected()) {
                    saveUsername(getSelectedUser(), pass);
                  }
                }
              } else {
                if (action.equals(IMAGE_LOGIN_ACTION)) {
                  DynamicButton userButton = (DynamicButton) c;
                  this.name.setText(userButton.getAccount());
                  this.pass.setText(this.getSavedPassword(userButton.getAccount()));
                  this.remember.setSelected(true);
                  action(LOGIN_ACTION, userButton);
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

  public void updateFrameTitle() {
    this.setTitle("Technic Launcher: " + packSelector.getSelectedPack().getDisplayName());
  }

  public void updateBackground() {
    getBackgroundImage().setIcon(new ImageIcon(newBackgroundImage(packSelector.getSelectedPack())));
  }

  public Image newBackgroundImage(ModpackInfo modpack) {
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
        action(PACKLEFT_ACTION, null);
      } else {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
          action(PACKRIGHT_ACTION, null);
        }
      }
    }
  }

  @Override public void keyReleased(KeyEvent e) {
  }
}