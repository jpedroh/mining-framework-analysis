package com.mojang.minecraft;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import com.mojang.minecraft.render.TextureManager;
import com.mojang.util.LogUtil;

public final class GameSettings {
	public static String StatusString = "";
	public static String PercentString = "";
	public static boolean CanReplaceSlot = true;
	public static List<String> typinglog = new ArrayList<String>();
	private static final String[] viewDistanceOptions = new String[]{
	        "FAR", "NORMAL", "SHORT", "TINY"
	};
	public boolean music = true;
	public boolean sound = true;
	public boolean invertMouse = false;
	public boolean canServerChangeTextures = true;
	public boolean showDebug = false;
	public boolean viewBobbing = true;
	public boolean limitFramerate = true;
        public boolean TPDisabled = false;
<<<<<<< /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/left.java
	public KeyBinding forwardKey = new KeyBinding("Forward", 17);
||||||| /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/base.java
	public KeyBinding forwardKey = new KeyBinding("Forward", 17);
=======
	public KeyBinding forwardKey = new KeyBinding("Forward", Keyboard.KEY_W);
>>>>>>> /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/right.java
<<<<<<< /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/left.java
	public KeyBinding leftKey = new KeyBinding("Left", 30);
||||||| /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/base.java
	public KeyBinding leftKey = new KeyBinding("Left", 30);
=======
	public KeyBinding leftKey = new KeyBinding("Left", Keyboard.KEY_A);
>>>>>>> /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/right.java
<<<<<<< /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/left.java
	public KeyBinding backKey = new KeyBinding("Back", 31);
||||||| /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/base.java
	public KeyBinding backKey = new KeyBinding("Back", 31);
=======
	public KeyBinding backKey = new KeyBinding("Back", Keyboard.KEY_S);
>>>>>>> /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/right.java
<<<<<<< /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/left.java
	public KeyBinding rightKey = new KeyBinding("Right", 32);
||||||| /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/base.java
	public KeyBinding rightKey = new KeyBinding("Right", 32);
=======
	public KeyBinding rightKey = new KeyBinding("Right", Keyboard.KEY_D);
>>>>>>> /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/right.java
<<<<<<< /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/left.java
	public KeyBinding jumpKey = new KeyBinding("Jump", 57);
||||||| /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/base.java
	public KeyBinding jumpKey = new KeyBinding("Jump", 57);
=======
	public KeyBinding jumpKey = new KeyBinding("Jump", Keyboard.KEY_SPACE);
>>>>>>> /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/right.java
<<<<<<< /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/left.java
	public KeyBinding inventoryKey = new KeyBinding("Block List", 48);
||||||| /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/base.java
	public KeyBinding inventoryKey = new KeyBinding("Block List", 48);
=======
	public KeyBinding inventoryKey = new KeyBinding("Block List", Keyboard.KEY_B);
>>>>>>> /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/right.java
<<<<<<< /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/left.java
	public KeyBinding chatKey = new KeyBinding("Chat", 20);
||||||| /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/base.java
	public KeyBinding chatKey = new KeyBinding("Chat", 20);
=======
	public KeyBinding chatKey = new KeyBinding("Chat", Keyboard.KEY_T);
>>>>>>> /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/right.java
<<<<<<< /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/left.java
	public KeyBinding toggleFogKey = new KeyBinding("Toggle fog", 33);
||||||| /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/base.java
	public KeyBinding toggleFogKey = new KeyBinding("Toggle fog", 33);
=======
	public KeyBinding toggleFogKey = new KeyBinding("Toggle fog", Keyboard.KEY_F);
>>>>>>> /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/right.java
<<<<<<< /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/left.java
	public KeyBinding saveLocationKey = new KeyBinding("Save location", 28);
||||||| /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/base.java
	public KeyBinding saveLocationKey = new KeyBinding("Save location", 28);
=======
	public KeyBinding saveLocationKey = new KeyBinding("Save location", Keyboard.KEY_RETURN);
>>>>>>> /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/right.java
<<<<<<< /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/left.java
	public KeyBinding loadLocationKey = new KeyBinding("Load location", 19);
||||||| /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/base.java
	public KeyBinding loadLocationKey = new KeyBinding("Load location", 19);
=======
	public KeyBinding loadLocationKey = new KeyBinding("Load location", Keyboard.KEY_R);
>>>>>>> /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/right.java
<<<<<<< /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/left.java
	public KeyBinding runKey = new KeyBinding("Run", 42);
||||||| /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/base.java
	public KeyBinding runKey = new KeyBinding("Run", 42);
=======
	public KeyBinding runKey = new KeyBinding("Run", Keyboard.KEY_LSHIFT);
>>>>>>> /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/right.java
        public KeyBinding toggleTPKey = new KeyBinding("Toggle TP", 46);
	public KeyBinding[] bindings;
	public transient Minecraft minecraft;
<<<<<<< /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/left.java
	private File settingsFile;
||||||| /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/base.java
	private File settingsFile;
=======
	private final File settingsFile;
>>>>>>> /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/right.java
	public int settingCount;
	public boolean CanSpeed = true;
	public int HackType = 0;
	public int ShowNames = 0;
	public String lastUsedTexturePack;
	public boolean HacksEnabled = true;
	public int smoothing = 0;
	public static String[] smoothingOptions = new String[]{"OFF", "Automatic", "Universal"};
	public KeyBinding flyKey = new KeyBinding("Fly", Keyboard.KEY_Z);
	public KeyBinding flyUp = new KeyBinding("Fly Up", Keyboard.KEY_Q);
	public KeyBinding flyDown = new KeyBinding("Fly Down", Keyboard.KEY_E);
	public GameSettings(Minecraft minecraft, File minecraftFolder) {
	    bindings = new KeyBinding[]{
	            forwardKey, leftKey, backKey, rightKey, jumpKey, inventoryKey,
	            chatKey, toggleFogKey, saveLocationKey, loadLocationKey};
	    bindingsmore = new KeyBinding[]{runKey, flyKey, flyUp, flyDown, noClip};

	    this.minecraft = minecraft;

	    settingsFile = new File(minecraftFolder, "options.txt");
	    load();
	}
	public String getBinding(int key) {
	    return bindings[key].name + ": " + Keyboard.getKeyName(bindings[key].key);
	}
	public String getSetting(int id) {
		return id == 0 ? "Music: " + (music ? "ON" : "OFF") : id == 1 ? "Sound: "
				+ (sound ? "ON" : "OFF") : id == 2 ? "Invert mouse: "
				+ (invertMouse ? "ON" : "OFF") : id == 3 ? "Show Debug: "
				+ (showDebug ? "ON" : "OFF") : id == 4 ? "Render distance: "
				+ renderDistances[viewDistance] : id == 5 ? "View bobbing: "
				+ (viewBobbing ? "ON" : "OFF") : id == 6 ? "3d anaglyph: "
				+ (anaglyph ? "ON" : "OFF") : id == 7 ? "Limit framerate: "
				+ (limitFramerate ? "ON" : "OFF") : id == 8 ? "Smoothing: "
				+ smoothingOptions[smoothing] : id == 9 ? "Anisotropic: "
				+ anisotropicOptions[anisotropic] : id == 10 ? "Allow server textures: "
				+ (canServerChangeTextures ? "Yes" : "No") : id == 11 ? "SpeedHack Type: "
				+ (HackType == 0 ? "Normal" : "Adv") : id == 12 ? "Font Scale: "
				+ new DecimalFormat("#.#").format(scale) : id == 13 ? "Enable Hacks: "
				+ (HacksEnabled ? "Yes" : "No") : id == 14 ? "Show Names: "
				+ (ShowNames == 0 ? "Hover" : "Always") : "";
	}
<<<<<<< /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/left.java
	private void load() {
		try {
			if (settingsFile.exists()) {
				FileReader fileReader = new FileReader(settingsFile);
				BufferedReader reader = new BufferedReader(fileReader);

				String line = null;

				while ((line = reader.readLine()) != null) {
					String[] setting = line.split(":");

					if (setting[0].equals("music")) {
						music = setting[1].equals("true");
					}

					if (setting[0].equals("sound")) {
						sound = setting[1].equals("true");
					}

					if (setting[0].equals("invertYMouse")) {
						invertMouse = setting[1].equals("true");
					}

					if (setting[0].equals("showDebug")) {
						showDebug = setting[1].equals("true");
					}

					if (setting[0].equals("viewDistance")) {
						viewDistance = Integer.parseInt(setting[1]);
					}

					if (setting[0].equals("bobView")) {
						viewBobbing = setting[1].equals("true");
					}

					if (setting[0].equals("anaglyph3d")) {
						anaglyph = setting[1].equals("true");
					}

					if (setting[0].equals("limitFramerate")) {
						limitFramerate = setting[1].equals("true");
						Display.setVSyncEnabled(limitFramerate);
					}

					if (setting[0].equals("smoothing")) {
						smoothing = Integer.parseInt(setting[1]);
					}

					if (setting[0].equals("anisotropic")) {
						anisotropic = Integer.parseInt(setting[1]);
					}
					if (setting[0].equals("canServerChangeTextures")) {
						canServerChangeTextures = setting[1].equals("true");
					}
					if (setting[0].equals("HackType")) {
						HackType = Integer.parseInt(setting[1]);
					}
					if (setting[0].equals("Scale")) {
						scale = Float.parseFloat(setting[1]);
					}
					if (setting[0].equals("HacksEnabled")) {
						HacksEnabled = setting[1].equals("true");
					}
					if (setting[0].equals("ShowNames")) {
						ShowNames = Integer.parseInt(setting[1]);
					}
					if (setting[0].equals("texturepack")) {
						lastUsedTexturePack = setting[1];
					}

					for (int index = 0; index < bindings.length; index++) {
						if (setting[0].equals("key_" + bindings[index].name)) {
							bindings[index].key = Integer.parseInt(setting[1]);
						}
					}
				}

				reader.close();
			}
		} catch (Exception e) {
			System.out.println("Failed to load options");

			e.printStackTrace();
		}
	}
||||||| /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/base.java
	private void load() {
		try {
			if (settingsFile.exists()) {
				FileReader fileReader = new FileReader(settingsFile);
				BufferedReader reader = new BufferedReader(fileReader);

				String line = null;

				while ((line = reader.readLine()) != null) {
					String[] setting = line.split(":");

					if (setting[0].equals("music")) {
						music = setting[1].equals("true");
					}

					if (setting[0].equals("sound")) {
						sound = setting[1].equals("true");
					}

					if (setting[0].equals("invertYMouse")) {
						invertMouse = setting[1].equals("true");
					}

					if (setting[0].equals("showDebug")) {
						showDebug = setting[1].equals("true");
					}

					if (setting[0].equals("viewDistance")) {
						viewDistance = Integer.parseInt(setting[1]);
					}

					if (setting[0].equals("bobView")) {
						viewBobbing = setting[1].equals("true");
					}

					if (setting[0].equals("anaglyph3d")) {
						anaglyph = setting[1].equals("true");
					}

					if (setting[0].equals("limitFramerate")) {
						limitFramerate = setting[1].equals("true");
						Display.setVSyncEnabled(limitFramerate);
					}

					if (setting[0].equals("smoothing")) {
						smoothing = Integer.parseInt(setting[1]);
					}

					if (setting[0].equals("anisotropic")) {
						anisotropic = Integer.parseInt(setting[1]);
					}
					if (setting[0].equals("canServerChangeTextures")) {
						canServerChangeTextures = setting[1].equals("true");
					}
					if (setting[0].equals("HackType")) {
						HackType = Integer.parseInt(setting[1]);
					}
					if (setting[0].equals("Scale")) {
						scale = Float.parseFloat(setting[1]);
					}
					if (setting[0].equals("HacksEnabled")) {
						HacksEnabled = setting[1].equals("true");
					}
					if (setting[0].equals("ShowNames")) {
						ShowNames = Integer.parseInt(setting[1]);
					}
					if (setting[0].equals("texturepack")) {
						lastUsedTexturePack = setting[1];
					}

					for (int index = 0; index < bindings.length; index++) {
						if (setting[0].equals("key_" + bindings[index].name)) {
							bindings[index].key = Integer.parseInt(setting[1]);
						}
					}
				}

				reader.close();
			}
		} catch (Exception e) {
			System.out.println("Failed to load options");

			e.printStackTrace();
		}
	}
=======
	private void load() {
        try {
            if (settingsFile.exists()) {
                try (FileReader fileReader = new FileReader(settingsFile);
                     BufferedReader reader = new BufferedReader(fileReader)) {
                    // Read the raw settings keys/values
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] setting = line.split(":");
                        String key = setting[0].toLowerCase();
                        String value = setting[1];
                        try {
                            parseOneSetting(key, value);
                        } catch (Exception ex) {
                            String errorMsg = String.format("Error parsing a setting: %s=%s", key, value);
                            LogUtil.logWarning(errorMsg, ex);
                        }
                    }
                }
            } else {
                LogUtil.logWarning("Options file not found at " + settingsFile + ", using defaults.");
            }
        } catch (Exception ex) {
            LogUtil.logError("Failed to load options from " + settingsFile, ex);
        }
    }
>>>>>>> /usr/src/app/output/andrewphorn/classicube-client/268948793e799134b9a023699c3f118f381d8447/src/main/java/com/mojang/minecraft/GameSettings.java/right.java
	public void save() {
	    try {
	        try (FileWriter fileWriter = new FileWriter(settingsFile);
	             PrintWriter writer = new PrintWriter(fileWriter)) {
	            writer.println("music:" + music);
	            writer.println("sound:" + sound);
	            writer.println("invertYMouse:" + invertMouse);
	            writer.println("showDebug:" + showDebug);
	            writer.println("viewDistance:" + viewDistance);
	            writer.println("bobView:" + viewBobbing);
	            writer.println("limitFramerate:" + limitFramerate);
	            writer.println("smoothing:" + smoothing);
	            writer.println("anisotropic:" + anisotropy);
	            writer.println("canServerChangeTextures:" + canServerChangeTextures);
	            writer.println("HackType:" + HackType);
	            writer.println("Scale:" + scale);
	            writer.println("HacksEnabled:" + HacksEnabled);
	            writer.println("ShowNames:" + ShowNames);
	            writer.println("texturepack:" + lastUsedTexturePack);
	            for (KeyBinding binding : bindings) {
	                writer.println("key_" + binding.name + ":" + binding.key);
	            }
	        }
	    } catch (Exception ex) {
	        LogUtil.logError("Failed to save options.", ex);
	    }
	}
	public void setBinding(int key, int keyID) {
	    bindings[key].key = keyID;
	    save();
	}
	public void toggleSetting(int setting, int fogValue) {
		if (setting == 0) {
			music = !music;
		}

		if (setting == 1) {
			sound = !sound;
		}

		if (setting == 2) {
			invertMouse = !invertMouse;
		}

		if (setting == 3) {
			showDebug = !showDebug;
		}

		if (setting == 4) {
			viewDistance = viewDistance + fogValue & 3;
		}

		if (setting == 5) {
			viewBobbing = !viewBobbing;
		}

		if (setting == 6) {
			anaglyph = !anaglyph;

			TextureManager textureManager = minecraft.textureManager;
			Iterator<?> iterator = minecraft.textureManager.textureImages.keySet().iterator();

			int i;
			BufferedImage image;

			while (iterator.hasNext()) {
				i = (Integer) iterator.next();
				image = textureManager.textureImages.get(Integer.valueOf(i));

				textureManager.load(image, i);
			}

			iterator = textureManager.textures.keySet().iterator();

			while (iterator.hasNext()) {
				String s = (String) iterator.next();

				try {
					if (s.startsWith("##")) {
						image = TextureManager.load1(ImageIO.read(TextureManager.class
								.getResourceAsStream(s.substring(2))));
					} else {
						image = ImageIO.read(TextureManager.class.getResourceAsStream(s));
					}

					i = textureManager.textures.get(s);

					textureManager.load(image, i);
				} catch (IOException var6) {
					var6.printStackTrace();
				}
			}
		}

		if (setting == 7) {
			limitFramerate = !limitFramerate;
			if (Display.isCreated()) {
				Display.setVSyncEnabled(limitFramerate);
			}
		}

		if (setting == 8) {
			if (smoothing == smoothingOptions.length - 1) {
				smoothing = 0;
			} else {
				smoothing++;
			}

			minecraft.textureManager.textures.clear();

			// minecraft.levelRenderer.refresh();
		}

		if (setting == 9) {
			if (anisotropic == anisotropicOptions.length - 1) {
				anisotropic = 0;
			} else {
				anisotropic++;
			}

			minecraft.textureManager.textures.clear();

			// minecraft.levelRenderer.refresh();
		}
		if (setting == 10) {
			canServerChangeTextures = !canServerChangeTextures;
		}
		if (setting == 11) {
			if (HackType == 1) {
				HackType = 0;
			} else {
				HackType++;
			}
		}
		if (setting == 12) {
			scale += 0.1;
			if (scale > 1.2f) {
				scale = 0.6f;
			}
		}
		if (setting == 13) {
			HacksEnabled = !HacksEnabled;
		}
		if (setting == 14) {
			if (ShowNames == 0) {
				ShowNames = 1;
			} else {
				ShowNames = 0;
			}
		}

		save();
	}
    // ==== CONSTANTS =============================================================================
    public static String[] showNamesOptions = new String[]{
            "Hover", "Hover (No Scaling)", "Always", "Always (No Scaling)"
    };
    // showNames values
    public static final int SHOWNAMES_HOVER = 0,
            SHOWNAMES_HOVER_UNSCALED = 1,
            SHOWNAMES_ALWAYS = 2,
            SHOWNAMES_ALWAYS_UNSCALED = 3;
    // thirdPersonMode values
    public static final int FIRST_PERSON = 0,
            THIRD_PERSON_BACK = 1,
            THIRD_PERSON_FRONT = 2;
    // hackType values
    public static final int HACKTYPE_NORMAL = 0,
            HACKTYPE_ADVANCED = 1;
    // valid range of values for viewDistance
    public static final int VIEWDISTANCE_MIN = 0,
            VIEWDISTANCE_MAX = viewDistanceOptions.length - 1;
    // smoothing values
    public static final int SMOOTHING_OFF = 0,
            SMOOTHING_AUTO = 1,
            SMOOTHING_UNIVERSAL = 2;
    public static final float SCALE_MIN = 0.6f,
            SCALE_MAX = 1.2f;
    // min valid value for anisotropy. Max is set by TextureManager.
    public static final int ANISOTROPY_OFF = 0;
    // TODO Below two never used
    public static List<String> typingLog = new ArrayList<>();
    public static int typingLogPos = 0;
    public boolean showClouds = true;
    public byte thirdPersonMode = 0;
// TODO Never used
    // ==== BINDINGS ==============================================================================
    public KeyBinding noClip = new KeyBinding("NoClip", Keyboard.KEY_X);
    public KeyBinding[] bindingsmore;
    // ==== SETTINGS ==============================================================================
    public int viewDistance;
    // 0 = off, higher values mean nth-powers-of-2 (e.g. 1 => 2x, 2 => 4x, 3 => 8x, 4 => 16x)
    public int anisotropy;
    // Interface font scale, as a ratio of default font (1.0 => 100%)
    public float scale = 1;
    private static String toOnOff(boolean value) {
        return (value ? "ON" : "OFF");
    }
    public String getBindingMore(int key) {
        return bindingsmore[key].name + ": " + Keyboard.getKeyName(bindingsmore[key].key);
    }
    public String getSetting(Setting id) {
        switch (id) {
            case MUSIC:
                return "Music: " + toOnOff(music);
            case SOUND:
                return "Sound: " + toOnOff(sound);
            case INVERT_MOUSE:
                return "Invert mouse: " + toOnOff(invertMouse);
            case SHOW_DEBUG:
                return "Show Debug: " + toOnOff(showDebug);
            case RENDER_DISTANCE:
                return "Render distance: " + viewDistanceOptions[viewDistance];
            case VIEW_BOBBING:
                return "View bobbing: " + toOnOff(viewBobbing);
            case LIMIT_FRAMERATE:
                return "Limit framerate: " + toOnOff(limitFramerate);
            case SMOOTHING:
                return "Smoothing: " + smoothingOptions[smoothing];
            case ANISOTROPIC:
                return "Anisotropic: " + (anisotropy == 0 ? "OFF" : (1 << anisotropy) + "x");
            case ALLOW_SERVER_TEXTURES:
                return "Allow server textures: " + (canServerChangeTextures ? "Yes" : "No");
            case SPEEDHACK_TYPE:
                return "SpeedHack type: " + (HackType == 0 ? "Normal" : "Adv");
            case FONT_SCALE:
                return "Font Scale: " + Math.round(scale * 100) + "%";
            case ENABLE_HACKS:
                return "Enable Hacks: " + (HacksEnabled ? "Yes" : "No");
            case SHOW_NAMES:
                return "Show Names: " + showNamesOptions[ShowNames];
            default:
                throw new IllegalArgumentException();
        }
    }
    private void parseOneSetting(String key, String value) {
        boolean isTrue = "true".equalsIgnoreCase(value) || "1".equals(value);
        switch (key) {
            case "music":
                music = isTrue;
                break;
            case "sound":
                sound = isTrue;
                break;
            case "invertymouse":
                invertMouse = isTrue;
                break;
            case "showdebug":
                showDebug = isTrue;
                break;
            case "viewdistance":
                viewDistance = Math.min(Math.max(Byte.parseByte(value),
                        VIEWDISTANCE_MIN), VIEWDISTANCE_MAX);
                break;
            case "bobview":
                viewBobbing = isTrue;
                break;
            case "limitframerate":
                limitFramerate = isTrue;
                Display.setVSyncEnabled(limitFramerate);
                break;
            case "smoothing":
                smoothing = Math.min(Math.max(Byte.parseByte(value),
                        SMOOTHING_OFF), SMOOTHING_UNIVERSAL);
                break;
            case "anisotropic":
                anisotropy = Byte.parseByte(value);
                break;
            case "canserverchangetextures":
                canServerChangeTextures = isTrue;
                break;
            case "hacktype":
                HackType = Math.min(Math.max(Byte.parseByte(value),
                        HACKTYPE_NORMAL), HACKTYPE_ADVANCED);
                break;
            case "scale":
                // Round scale to nearest 10% step (0.1)
                float roundedVal = Math.round(Float.parseFloat(value) * 10) / 10f;
                scale = Math.min(Math.max(roundedVal, SCALE_MIN), SCALE_MAX);
                break;
            case "hacksenabled":
                HacksEnabled = isTrue;
                break;
            case "shownames":
                ShowNames = Math.min(Math.max(Byte.parseByte(value),
                        SHOWNAMES_HOVER), SHOWNAMES_ALWAYS_UNSCALED);
                break;
            case "texturepack":
                lastUsedTexturePack = value;
                break;
            default:
                for (KeyBinding binding : bindings) {
                    if (("key_" + binding.name.toLowerCase()).equals(value)) {
                        binding.key = Integer.parseInt(value);
                        break;
                    }
                }
                break;
        }
    }
    public void setBindingMore(int key, int keyID) {
        bindingsmore[key].key = keyID;
        save();
    }
    public void toggleSetting(Setting setting, int fogValue) {
        switch (setting) {
            case MUSIC:
                music = !music;
                break;
            case SOUND:
                sound = !sound;
                break;
            case INVERT_MOUSE:
                invertMouse = !invertMouse;
                break;
            case SHOW_DEBUG:
                showDebug = !showDebug;
                break;
            case RENDER_DISTANCE:
                int newViewDist = viewDistance + fogValue;
                if (newViewDist < VIEWDISTANCE_MIN) {
                    newViewDist = VIEWDISTANCE_MAX;
                } else if (newViewDist > VIEWDISTANCE_MAX) {
                    newViewDist = VIEWDISTANCE_MIN;
                }
                viewDistance = newViewDist;
                break;
            case VIEW_BOBBING:
                viewBobbing = !viewBobbing;
                break;
            case LIMIT_FRAMERATE:
                limitFramerate = !limitFramerate;
                if (Display.isCreated()) {
                    Display.setVSyncEnabled(limitFramerate);
                }
                break;
            case SMOOTHING:
                smoothing++;
                if (smoothing > SMOOTHING_UNIVERSAL) {
                    smoothing = SMOOTHING_OFF;
                }
                minecraft.textureManager.textures.clear();
                break;
            case ANISOTROPIC:
                anisotropy++;
                if (anisotropy > TextureManager.getMaxAnisotropySetting()) {
                    anisotropy = ANISOTROPY_OFF;
                }
                minecraft.textureManager.textures.clear();
                break;
            case ALLOW_SERVER_TEXTURES:
                canServerChangeTextures = !canServerChangeTextures;
                break;
            case SPEEDHACK_TYPE:
                HackType++;
                if (HackType > HACKTYPE_ADVANCED) {
                    HackType = HACKTYPE_NORMAL;
                }
                break;
            case FONT_SCALE:
                scale += 0.1;
                if (scale > SCALE_MAX) {
                    scale = SCALE_MIN;
                }
                break;
            case ENABLE_HACKS:
                HacksEnabled = !HacksEnabled;
                break;
            case SHOW_NAMES:
                ShowNames++;
                if (ShowNames > SHOWNAMES_ALWAYS_UNSCALED) {
                    ShowNames = SHOWNAMES_HOVER;
                }
                break;
        }
        save();
    }
}
