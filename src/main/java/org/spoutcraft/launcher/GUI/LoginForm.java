package org.spoutcraft.launcher.GUI;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.spoutcraft.launcher.Exceptions.BadLoginException;
import org.spoutcraft.launcher.Exceptions.MCNetworkException;
import org.spoutcraft.launcher.Exceptions.OutdatedMCLauncherException;
import org.spoutcraft.launcher.GameUpdater;
import org.spoutcraft.launcher.MinecraftUtils;
import org.spoutcraft.launcher.PlatformUtils;


public class LoginForm extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -192904429165686059L;

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginForm frame = new LoginForm();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

			private Cipher getCipher(int mode, String password) throws Exception {
			  Random random = new Random(43287234L);
			  byte[] salt = new byte[8];
			  random.nextBytes(salt);
			  PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);

			  SecretKey pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(password.toCharArray()));
			  Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
			  cipher.init(mode, pbeKey, pbeParamSpec);
			  return cipher;
			}

	/**
	 * Create the frame.
	 */
	JEditorPane jedHTML = new JEditorPane();

	@SuppressWarnings("rawtypes")
	private Stack urlStack = new Stack();

	private JPasswordField txtPassword;

	private JComboBox cmbUsername = new JComboBox();

	private JButton btnLogin = new JButton("Login");

	private JCheckBox cbRemember = new JCheckBox("Remember");

	private JButton btnOptions = new JButton("Options");

	public LoginForm() {
		btnLogin.setBounds(761, 390, 86, 23);
		btnLogin.addActionListener(this);
		btnOptions.addActionListener(this);
		setIconImage(Toolkit.getDefaultToolkit().getImage(LoginForm.class.getResource("/org/spoutcraft/launcher/favicon.png")));
		setResizable(false);
		try {
			jedHTML = new JEditorPane("http://updates.getspout.org/");
			jedHTML.setEditable(false);
		} catch (IOException e1) {
			jedHTML = new JEditorPane();
			jedHTML.setEditable(false);
			jedHTML.setText("Could not connect to the Spoutcraft Updates Page. Don't worry about it though :P");
		}
		jedHTML.setBounds(0, 0, 855, 381);
		jedHTML.setEditable(false);
		jedHTML.setForeground(new Color(255, 255, 255));
		jedHTML.addHyperlinkListener(new HyperlinkListener() {
			@SuppressWarnings("unchecked")
			public void hyperlinkUpdate(HyperlinkEvent event) {
				if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					try {
						urlStack.push(event.getURL().toString());
						jedHTML.setPage(event.getURL());
					} catch (IOException e) {
						jedHTML.setText("Error: " + e);
					}
				}
			}
		});
		setTitle("Spoutcraft Launcher");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 861, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		JLabel lblLogo = new JLabel("");
		lblLogo.setBounds(8, 375, 294, 99);
		lblLogo.setIcon(new ImageIcon(LoginForm.class.getResource("/org/spoutcraft/launcher/spoutcraft.png")));
		jedHTML.setBackground(Color.DARK_GRAY);
		JLabel lblMinecraftUsername = new JLabel("Minecraft Username: ");
		lblMinecraftUsername.setHorizontalAlignment(SwingConstants.RIGHT);
		lblMinecraftUsername.setBounds(472, 394, 150, 14);
		JLabel lblPassword = new JLabel("Password: ");
		lblPassword.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPassword.setBounds(522, 419, 100, 20);
		txtPassword = new JPasswordField();
		txtPassword.setBounds(633, 419, 119, 20);
		readUsedUsernames();
		JLabel lblNewLabel = new HyperlinkJLabel("<html><u>Need a minecraft account?</u></html>", "http://www.minecraft.net/register.jsp");
		lblNewLabel.setBounds(757, 447, 86, 14);
		lblNewLabel.setText("<html><u>Need an account?</u></html>");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 11));
		lblNewLabel.setForeground(new Color(0, 0, 255));
		cmbUsername.setBounds(632, 391, 119, 20);
		cmbUsername.setEditable(true);
		contentPane.setLayout(null);
		cbRemember.setBounds(633, 443, 93, 23);
		contentPane.add(cbRemember);
		contentPane.add(lblLogo);
		contentPane.add(lblPassword);
		contentPane.add(lblMinecraftUsername);
		contentPane.add(txtPassword);
		contentPane.add(cmbUsername);
		contentPane.add(btnLogin);
		btnOptions.setBounds(761, 418, 86, 23);
		contentPane.add(btnOptions);
		contentPane.add(lblNewLabel);
		contentPane.add(jedHTML);
	}

	ArrayList<String> usernames = new ArrayList<String>();

	private void readUsedUsernames() {
		File recentsU = new File(PlatformUtils.getWorkingDirectory(), "recentUsernames");
		if (!recentsU.exists()) {
			return;
		}
		try {
			FileInputStream fstream = new FileInputStream(recentsU);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			while ((strLine = br.readLine()) != null) {
				this.cmbUsername.addItem(strLine);
				usernames.add(strLine);
			} 
			in.close();
		} catch (java.lang.Exception e) {
		}
		try {
			File lastLogin = new File(PlatformUtils.getWorkingDirectory(), "lastlogin");
			Cipher cipher = getCipher(2, "passwordfile");
			DataInputStream dis;
			if (cipher != null) {
				dis = new DataInputStream(new CipherInputStream(new FileInputStream(lastLogin), cipher));
			} else {
				dis = new DataInputStream(new FileInputStream(lastLogin));
			}
			String username = dis.readUTF();
			this.cmbUsername.addItem(username);
			usernames.add(username);
			this.txtPassword.setText(dis.readUTF());
			this.cbRemember.setSelected(this.txtPassword.getPassword().length > 0);
			dis.close();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	private void writeUsernameList(String user) {
		File recentsU = new File(PlatformUtils.getWorkingDirectory(), "recentUsernames");
		try {
			FileWriter fstream = new FileWriter(recentsU, true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(user);
			out.write("\n");
			out.close();
		} catch (java.lang.Exception e) {
		}
		try {
			File lastLogin = new File(PlatformUtils.getWorkingDirectory(), "lastlogin");
			Cipher cipher = getCipher(1, "passwordfile");
			DataOutputStream dos;
			if (cipher != null) {
				dos = new DataOutputStream(new CipherOutputStream(new FileOutputStream(lastLogin, true), cipher));
			} else {
				dos = new DataOutputStream(new FileOutputStream(lastLogin, true));
			}
			dos.writeUTF(user);
			dos.writeUTF(this.cbRemember.isSelected() ? new String(this.txtPassword.getPassword()) : "");
			dos.close();
		} catch (java.lang.Exception e) {
			e.printStackTrace();
		}
	}

	public void actionPerformed(ActionEvent evt) {
		String btnID = evt.getActionCommand();
		if (btnID.equals("Login")) {
			try {
				String[] values = MinecraftUtils.doLogin(this.cmbUsername.getSelectedItem().toString(), new String(this.txtPassword.getPassword()));
				if (!usernames.contains(this.cmbUsername.getSelectedItem().toString())) {
					this.writeUsernameList(this.cmbUsername.getSelectedItem().toString());
				}
				if (cbRemember.isSelected()) {
				}
				GameUpdater gu = new GameUpdater(values[2].trim(), values[1].trim(), values[0].trim());
				gu.updateMC();
				gu.updateSpout(false);
				LauncherFrame launcher = new LauncherFrame();
				launcher.runGame(values[2].trim(), values[3].trim(), values[1].trim(), new String(this.txtPassword.getPassword()));
				this.setVisible(false);
			} catch (BadLoginException e) {
				JOptionPane.showMessageDialog(this, "Incorrect username/password combination");
			} catch (MCNetworkException e) {
				JOptionPane.showMessageDialog(this, "Cannot connect to minecraft.net");
			} catch (OutdatedMCLauncherException e) {
				JOptionPane.showMessageDialog(this, "The unthinkable has happened, alert alta189@getsport.org!!!!");
			} catch (UnsupportedEncodingException e) {
				JOptionPane.showMessageDialog(this, "Incorrect username/password combination");
				e.printStackTrace();
			} catch (java.lang.Exception e) {
				e.printStackTrace();
			}
		} else if (btnID.equals("Options")) {
			OptionDialog options = new OptionDialog();
			options.setVisible(true);
		}
	}
}