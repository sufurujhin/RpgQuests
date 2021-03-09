package danieldev.utils;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import danieldev.main.Quests;


public class FilesConfig {
	private static File quests;
	private static FileConfiguration questsFc;
	public static FilesConfig load;
	
	private static File keys;
	private static FileConfiguration keysFc;
	
	private static File logPlayers;
	private static FileConfiguration logPlayersFc;
	private static File quebrar;
	private static FileConfiguration quebrarFc;
	
	
	public static void reloadQuebrar() {
		quebrar = new File(Quests.main.getDataFolder(), "quebrar.yml");
		if (!quebrar.exists()) {
			Quests.main.saveResource("quebrar.yml", false);
		}
		quebrarFc = YamlConfiguration.loadConfiguration(quebrar);
	}

	public static FileConfiguration getQuebrar() {
		if (quebrarFc == null) {
			reloadPlayersConfig();
		}
		return quebrarFc;
	}

	public static void saveQuebrar() {
		
		
		try {
			quebrar = new File(Quests.main.getDataFolder(), "quebrar.yml");
			if (!quebrar.exists()) {
				reloadPlayersConfig();
			}
			quebrarFc.save(quebrar);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(load().getQuebrar().getString("quebrar.yml" + " Não esta na pasta"));
		}
	}
	
	
	
	public static void reloadPlayersConfig() {
		logPlayers = new File(Quests.main.getDataFolder(), "logPlayers.yml");
		if (!logPlayers.exists()) {
			Quests.main.saveResource("logPlayers.yml", false);
		}
		logPlayersFc = YamlConfiguration.loadConfiguration(logPlayers);
	}

	public static FileConfiguration getPlayersConfig() {
		if (logPlayersFc == null) {
			reloadPlayersConfig();
		}
		return logPlayersFc;
	}

	public static void savePlayersConfig() {
		
		
		try {
			logPlayers = new File(Quests.main.getDataFolder(), "logPlayers.yml");
			if (!logPlayers.exists()) {
				reloadPlayersConfig();
			}
			logPlayersFc.save(logPlayers);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(load().getPlayersConfig().getString("logPlayers.yml" + " Não esta na pasta"));
		}
	}
	
	
	public static void reloadQuestsConfig() {
		quests = new File(Quests.main.getDataFolder(), "quests.yml");
		if (!quests.exists()) {
			Quests.main.saveResource("quests.yml", false);
		}
		questsFc = YamlConfiguration.loadConfiguration(quests);
	}

	public static FileConfiguration getQuestsConfig() {
		if (questsFc == null) {
			reloadQuestsConfig();
		}
		return questsFc;
	}

	public static void saveQuestsConfig() {
		
		try {
			quests = new File(Quests.main.getDataFolder(), "quests.yml");
			if (!quests.exists()) {
				reloadQuestsConfig();
			}
			questsFc.save(quests);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(load().getQuestsConfig().getString("quests.yml" + " Não esta na pasta"));
		}
	}

	
	public static void reloadKeysConfig() {
		keys = new File(Quests.main.getDataFolder(), "keys.yml");
		if (!keys.exists()) {
			Quests.main.saveResource("keys.yml", false);
		}
		keysFc = YamlConfiguration.loadConfiguration(keys);
	}

	public static FileConfiguration getKeysConfig() {
		if (keysFc == null) {
			reloadKeysConfig();
		}
		return keysFc;
	}

	public static void saveKeysConfig() {
		try {
			keys = new File(Quests.main.getDataFolder(), "keys.yml");
			if (!keys.exists()) {
				reloadKeysConfig();
			}
			keysFc.save(keys);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().severe(load().getKeysConfig().getString("keys.yml" + " Não esta na pasta"));
		}
	}
	
	public static FilesConfig load(){
		return load;
	}
}
