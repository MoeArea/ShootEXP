package moe.feo.shootexp.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public enum Language {

	ITEM_NAME("item.name"), ITEM_LORE("item.lore"), MESSAGES_SHOOT("messages.shoot"),
	MESSAGES_SHOOT_NO_EXP("messages.shoot-no-exp"), MESSAGES_EAT("messages.eat"),
	COMMAND_INVALID("command.invalid"), COMMAND_NO_PERMISSION("command.no-permission"),
	COMMAND_PLAYER_ONLY("player-only"), COMMAND_RELOADED("command.reloaded"),
	COMMAND_HELP_TITLE("command.help.title"), COMMAND_HELP_HELP("command.help.help"),
	COMMAND_HELP_ITEM("command.help.item"), COMMAND_HELP_RELOAD("command.help.reload");

	private static FileConfiguration config;
	private final String path;

	Language(String path) {
		this.path = path;
	}

	public static void load() {
		String lang = Config.LANG.getString();
		String fileName = "lang/" + lang + ".yml";
		config = ConfigUtil.load(fileName);
	}

	public static void saveDefault() {
		ConfigUtil.saveDefault("lang/zh_CN.yml");
	}

	public String getString() {
		String string = config.getString(path);
		if (string != null){
			string = ChatColor.translateAlternateColorCodes('&', string);
		}
		return string;
	}

	public List<String> getStringList() {
		List<String> list = new ArrayList<>();
		List<String> source = config.getStringList(path);
		for (String string : source) {
			list.add(ChatColor.translateAlternateColorCodes('&', string));
		}
		return list;
	}
}
