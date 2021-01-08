package moe.feo.shootexp.config;

import moe.feo.shootexp.ShootEXP;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class ConfigUtil {

	public static FileConfiguration load(String filename) {
		JavaPlugin plugin = ShootEXP.getPlugin(ShootEXP.class);
		File file = new File(plugin.getDataFolder(), filename);
		FileConfiguration config = YamlConfiguration.loadConfiguration(file);// 用这个方法加载配置可以解决编码问题
		InputStream input = plugin.getResource(filename);
		if (input != null) {
			try (Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {// 读取默认配置
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
				config.setDefaults(defConfig);// 设置默认
			} catch (IOException ioe) {
				plugin.getLogger().log(Level.SEVERE, "Error when reading default config!", ioe);
			}
		}
		return config;
	}

	public static void saveDefault(String filename) {
		JavaPlugin plugin = ShootEXP.getPlugin(ShootEXP.class);
		File dir = new File(plugin.getDataFolder(), filename).getParentFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}
		plugin.saveResource(filename, false);
	}
}
