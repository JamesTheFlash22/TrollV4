package de.presti.trollv4.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import de.presti.trollv4.cmd.Haupt;
import de.presti.trollv4.config.Config;
import de.presti.trollv4.config.Items;
import de.presti.trollv4.config.Language;
import de.presti.trollv4.listener.Event;
import de.presti.trollv4.listener.GuiListener;
import de.presti.trollv4.listener.iListener;
import de.presti.trollv4.logging.Logger;
import de.presti.trollv4.utils.ArrayUtils;
import de.presti.trollv4.utils.Metrics;
import de.presti.trollv4.utils.PluginUtil;
import de.presti.trollv4.utils.UpdateChecker;
import de.presti.trollv4.utils.control.Controls;

/*
*	Urheberrechtshinweis														*
*																				*
*	Copyright § Baris Arslan 2018											    *
*	Erstellt: 11.05.2019 / 18:17:20												    *
*																				*
*	Alle Inhalte dieses Quelltextes sind urheberrechtlich gesch§tzt.			*
*	Das Urheberrecht liegt, soweit nicht ausdr§cklich anders gekennzeichnet,	*
*	bei Baris Arslan. Alle Rechte vorbehalten.								    *
*																				*
*	Jede Art der Vervielf§ltigung, Verbreitung, Vermietung, Verleihung,			*
*	§ffentlichen Zug§nglichmachung oder anderer Nutzung							*
*	bedarf der ausdr§cklichen, schriftlichen Zustimmung von Baris Arslan	    *
*/
public class Main extends JavaPlugin {
	public static Main instance;
	public static Logger logger = new Logger();
	public UpdateChecker update;
	public static Controls control;
	public static String version;

	public void onEnable() {
		
		instance = this;
		version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
		ArrayUtils.armor = new HashMap<String, ItemStack[]>();
		ArrayUtils.inventory = new HashMap<String, ItemStack[]>();
		ArrayUtils.cd = new ArrayList<String>();

		boolean need = ((Bukkit.getPluginManager().getPlugin("ProtocolLib") == null)
				|| (Bukkit.getPluginManager().getPlugin("NoteBlockAPI") == null)
				|| (Bukkit.getPluginManager().getPlugin("LibsDisguises") == null)
				|| (!new File("plugins/TrollV4/rick.nbs").exists()));
		downloadAll();

		if (Bukkit.getPluginManager().getPlugin("LibsDisguises") != null) {
			new Controls();
		} else {

			logger.info("---------->");
			logger.info(" ");
			logger.info("Pls restart the Server");
			logger.info("Because of the Libs Plugin");
			logger.info(" ");
		}

		if (need)
			logger.info("---------->");

		new Language();
		new Items();
		new Config().init();
		new Config().init2();
		new Config().init3();

		try {
			Metrics metrics = new Metrics(this, 4690);
			metrics.addCustomChart(new Metrics.SimplePie("used_language", () -> Config.cfg.getString("Language")));
		} catch (Exception e) {
			logger.error("Error Main Metrics Custom Chart: " + e.getMessage());
		}

		Language.clearAll();
		Items.clearAll();
		new Language();
		new Items();
		updateConfig();

		if (Config.getconfig().getBoolean("AutoUpdate")) {
			update = new UpdateChecker(this);
			update.checkForUpdate();
		}

		Startup();
	}

	public void onDisable() {
		Language.clearAll();
		Items.clearAll();
	}

	public static void reloadConfigurations() {
		Language.clearAll();
		Items.clearAll();
		new Language();
		new Items();
		new Config().init();
		new Config().init2();
		new Config().init3();
		Language.clearAll();
		Items.clearAll();
		new Language();
		new Items();
	}

	public static void CMD() {
		instance.getCommand("troll").setExecutor(new Haupt());
	}

	public void downloadAll() {
		boolean need = ((Bukkit.getPluginManager().getPlugin("ProtocolLib") == null)
				|| (Bukkit.getPluginManager().getPlugin("NoteBlockAPI") == null)
				|| (Bukkit.getPluginManager().getPlugin("LibsDisguises") == null)
				|| (!new File("plugins/TrollV4/rick.nbs").exists()));

		if (need)
			logger.info("---------->");

		if (!new File("plugins/TrollV4/rick.nbs").exists()) {
			logger.info("Downloading Rick.nbs!");
			download("https://trollv4.000webhostapp.com/download/uni/rick.nbs", "plugins/TrollV4/rick.nbs");
		}

		if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
			logger.info("Downloading ProtocolLib!");
			download("https://trollv4.000webhostapp.com/download/uni/ProtocolLib.jar", "plugins/ProtocolLib.jar");
		}

		if (Bukkit.getPluginManager().getPlugin("NoteBlockAPI") == null) {
			logger.info("Downloading NoteBlockAPI!");
			download("https://trollv4.000webhostapp.com/download/uni/NoteBlockAPI.jar", "plugins/NoteBlockAPI.jar");
		}

		if (Bukkit.getPluginManager().getPlugin("LibsDisguises") == null) {
			logger.info("Downloading LibsDisguises!");
			if (version.toLowerCase().startsWith("v1_8")) {
				download("https://trollv4.000webhostapp.com/download/1-8/LibsDisguises.jar",
						"plugins/LibsDisguises.jar");
			} else {
				download("https://trollv4.000webhostapp.com/download/1-12-x/LibsDisguises.jar",
						"plugins/LibsDisguises.jar");
			}
		}

		if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
			if (new File("plugins/ProtocolLib.jar").exists()) {
				logger.info("Trying to load ProtocolLib!");
				try {
					PluginUtil.loadPlugin("ProtocolLib");
					logger.info("Loaded ProtocolLib!");
				} catch (Exception e) {
					logger.error("Coudlnt load ProtocolLib!");
				}
			}
		}

		if (Bukkit.getPluginManager().getPlugin("LibsDisguises") == null) {
			if (new File("plugins/LibsDisguises.jar").exists()) {
				logger.info("Trying to load LibsDisguises!");
				try {
					PluginUtil.loadPlugin("LibsDisguises");
					logger.info("Loaded LibsDisguises!");
				} catch (Exception e) {
					logger.error("Coudlnt load LibsDisguises!");
				}
			}
		}

		if (Bukkit.getPluginManager().getPlugin("NoteBlockAPI") == null) {
			if (new File("plugins/NoteBlockAPI.jar").exists()) {
				logger.info("Trying to load NoteBlockAPI!");
				try {
					PluginUtil.loadPlugin("NoteBlockAPI");
					logger.info("Loaded NoteBlockAPI!");
				} catch (Exception e) {
					logger.error("Coudlnt load NoteBlockAPI!");
				}
			}
		}
	}

	public void updateConfig() {
		if (Config.cfg.getString("Plugin-Version") == null) {
			Config.getFile().delete();
			new Config().init();

			logger.info("Config broken recreating!");

		} else {
			if (!Config.cfg.getString("Plugin-Version").equalsIgnoreCase(Data.version)) {

				System.out.print("Updating Config!");

				String l = (Language.getLanguage() != null ? Language.getLanguage() : "US");
				boolean cin = (Config.getconfig().get("Custom-Item-Name") != null
						? Config.getconfig().getBoolean("Custom-Item-Name")
						: false);
				boolean autoup = (Config.getconfig().get("AutoUpdate") != null
						? Config.getconfig().getBoolean("AutoUpdate")
						: true);
				boolean anim = (Config.getconfig().get("Animations") != null
						? Config.getconfig().getBoolean("Animations")
						: false);
				boolean cs = (Config.getconfig().get("Community-surprise") != null
						? Config.getconfig().getBoolean("Community-surprise")
						: false);
				int hack = (Config.getconfig().get("trolls.hack.time") != null
						? Config.getconfig().getInt("trolls.hack.time")
						: 15);
				int fakeinv = (Config.getconfig().get("trolls.fakeinv.time") != null
						? Config.getconfig().getInt("trolls.fakeinv.time")
						: 5);
				int hands = (Config.getconfig().get("trolls.slipperyhands.time") != null
						? Config.getconfig().getInt("trolls.slipperyhands.time")
						: 1);

				Config.getFile().delete();

				Config.createFirstConfigWithValue((l.toUpperCase()), cin, autoup, anim, cs, hack, fakeinv, hands);
				System.out.print("Config updated!");
			}

		}
	}

	public static void Listener() {
		Bukkit.getPluginManager().registerEvents(new iListener(instance), instance);
		Bukkit.getPluginManager().registerEvents(new Event(), instance);
		Bukkit.getPluginManager().registerEvents(new GuiListener(), instance);
	}

	public static void Message() {
		logger.info("-----------------------------------");
		logger.info("TrollV" + Data.version + " by Presti");
		logger.info("In case of errors please report");
		logger.info("Skype: DxsSucuk@hotmail.com");
		logger.info("YouTube: Not Memerinoto");
		logger.info("Instagram: Memerinoto");
		logger.info("Otherwise have fun");
		logger.info("------------------------------------");
		logger.info("Online Changelog: " + instance.getDescription().getWebsite());
		logger.info("Plugin Version: " + Data.version);
		logger.info("Server Version: " + version + " - " + getMcVersion());
	}

	public static String getMcVersion() {
		return Bukkit.getVersion().split("MC:")[1].replaceAll(" ", "").replaceAll("\\)", "");
	}

	public static void Startup() {
		Message();
		Listener();
		CMD();
		/*
		 * if (Config.getconfig().getBoolean("Community-surprise")) { Community.host =
		 * "servertrollv4.dev-presti.tk"; Community.port = 187; try { Community.run(); }
		 * catch (IOException e) { logger.info("Error at connecting to the Cloud!"); } }
		 */
	}

	public int getRandom(int lower, int upper) {
		Random r = new Random();
		int toreturn = r.nextInt(upper - lower + 1) + lower;

		return toreturn;
	}

	public static String getRandomID() {
		String str = "";
		int lastrandom = 0;
		for (int i = 0; i < 9; i++) {
			Random r = new Random();
			int rand = r.nextInt(9);
			while (rand == lastrandom) {
				rand = r.nextInt(9);
			}
			lastrandom = rand;
			str = str + rand;
		}
		return str;
	}

	public static Main getPlugin() {
		return instance;
	}

	public static void startControlling(Player v, Player c) {
		control.startControlling(v, c);
	}

	public static void stopControlling(Player v, Player c) {
		control.stopControlling(v, c);
	}

	public static boolean download(String url, String FileName) {
		try {
			URL link = new URL(url);
			URLConnection con = link.openConnection();
			con.addRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
			InputStream in = con.getInputStream();
			ReadableByteChannel readableByteChannel = Channels.newChannel(in);
			FileOutputStream fileOutputStream = new FileOutputStream(FileName);
			FileChannel fileChannel = fileOutputStream.getChannel();
			fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
			fileChannel.close();
			fileOutputStream.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
