package org.spoutcraft.launcher;

import java.util.Map;
import org.bukkit.util.config.Configuration;
import org.spoutcraft.launcher.async.DownloadListener;

public class SpoutcraftBuild {
	private String minecraftVersion;
	private String latestVersion;
	private int build;
	Map<String, Object> libraries;
	private DownloadListener listener = null;

	private SpoutcraftBuild(String minecraft, String latest, int build, Map<String, Object> libraries) {
		this.minecraftVersion = minecraft;
		this.latestVersion = latest;
		this.build = build;
		this.libraries = libraries;
	}

	public int getBuild() {
		return build;
	}

	public String getMinecraftVersion() {
		return minecraftVersion;
	}

	public String getLatestMinecraftVersion() {
		return latestVersion;
	}

	public String getMinecraftURL(String user) {
		return "http://s3.amazonaws.com/MinecraftDownload/minecraft.jar?user=" + user + "&ticket=1";
	}

	public String getSpoutcraftURL() {
		return MirrorUtils.getMirrorUrl("Spoutcraft/" + build + "/spoutcraft-dev-SNAPSHOT.jar", null, listener);
	}

	public void setDownloadListener(DownloadListener listener) {
		this.listener = listener;
	}

	public void install() {
		Configuration config = SpoutcraftYML.getSpoutcraftYML();
		config.setProperty("current", getBuild());
		config.save();
	}

	public int getInstalledBuild() {
		Configuration config = SpoutcraftYML.getSpoutcraftYML();
		return config.getInt("current", -1);
	}

	public String getPatchURL() {
		String mirrorURL = "/Patches/Minecraft/minecraft_";
		mirrorURL += getLatestMinecraftVersion();
		mirrorURL += "-" + getMinecraftVersion() + ".patch";
		String fallbackURL = "http://mirror3.getspout.org/Patches/Minecraft/minecraft_";
		fallbackURL += getLatestMinecraftVersion();
		fallbackURL += "-" + getMinecraftVersion() + ".patch";
		return MirrorUtils.getMirrorUrl(mirrorURL, fallbackURL, listener);
	}
	
	public Map<String, Object> getLibraries() {
		return libraries;
	}

	@SuppressWarnings("unchecked")
	public static SpoutcraftBuild getSpoutcraftBuild() {
		Configuration config = SpoutcraftYML.getSpoutcraftYML();
		Map<Integer, Object> builds = (Map<Integer, Object>) config.getProperty("builds");
		int latest = config.getInt("latest", -1);
		int recommended = config.getInt("recommended", -1);
		int selected = SettingsUtil.getSelectedBuild();
		if (SettingsUtil.isRecommendedBuild()) {
			Map<String, Object> build = (Map<String, Object>) builds.get(recommended);
			Map<String, Object> libs = (Map<String, Object>) build.get("libraries");
			return new SpoutcraftBuild((String)build.get("minecraft"), MinecraftYML.getLatestMinecraftVersion(), recommended, libs);
		} else if (SettingsUtil.isDevelopmentBuild()) {
<<<<<<< /usr/src/app/output/spoutcraft/legacylauncher/66b1a81e0b1ce7a64454b616e2731b7c27f38ecb/src/main/java/org/spoutcraft/launcher/SpoutcraftBuild.java/left.java
			Map<String, Object> build = (Map<String, Object>) builds.get(latest);
			Map<String, Object> libs = (Map<String, Object>) build.get("libraries");
			return new SpoutcraftBuild((String)build.get("minecraft"), MinecraftYML.getLatestMinecraftVersion(), latest, libs);
||||||| /usr/src/app/output/spoutcraft/legacylauncher/66b1a81e0b1ce7a64454b616e2731b7c27f38ecb/src/main/java/org/spoutcraft/launcher/SpoutcraftBuild.java/base.java
			Map<String, Object> build = (Map<String, Object>) builds.get(recommended);
			return new SpoutcraftBuild((String)build.get("minecraft"), MinecraftYML.getLatestMinecraftVersion(), latest);
=======
			Map<String, Object> build = (Map<String, Object>) builds.get(latest);
			return new SpoutcraftBuild((String)build.get("minecraft"), MinecraftYML.getLatestMinecraftVersion(), latest);
>>>>>>> /usr/src/app/output/spoutcraft/legacylauncher/66b1a81e0b1ce7a64454b616e2731b7c27f38ecb/src/main/java/org/spoutcraft/launcher/SpoutcraftBuild.java/right.java
		}
<<<<<<< /usr/src/app/output/spoutcraft/legacylauncher/66b1a81e0b1ce7a64454b616e2731b7c27f38ecb/src/main/java/org/spoutcraft/launcher/SpoutcraftBuild.java/left.java
		Map<String, Object> build = (Map<String, Object>) builds.get(selected);
		Map<String, Object> libs = (Map<String, Object>) build.get("libraries");
		return new SpoutcraftBuild((String)build.get("minecraft"), MinecraftYML.getLatestMinecraftVersion(), selected, libs);
||||||| /usr/src/app/output/spoutcraft/legacylauncher/66b1a81e0b1ce7a64454b616e2731b7c27f38ecb/src/main/java/org/spoutcraft/launcher/SpoutcraftBuild.java/base.java
		Map<String, Object> build = (Map<String, Object>) builds.get(recommended);
		return new SpoutcraftBuild((String)build.get("minecraft"), MinecraftYML.getLatestMinecraftVersion(), selected);
=======
		Map<String, Object> build = (Map<String, Object>) builds.get(selected);
		return new SpoutcraftBuild((String)build.get("minecraft"), MinecraftYML.getLatestMinecraftVersion(), selected);
>>>>>>> /usr/src/app/output/spoutcraft/legacylauncher/66b1a81e0b1ce7a64454b616e2731b7c27f38ecb/src/main/java/org/spoutcraft/launcher/SpoutcraftBuild.java/right.java
	}
}
