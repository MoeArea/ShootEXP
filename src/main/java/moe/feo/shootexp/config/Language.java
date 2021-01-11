package moe.feo.shootexp.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public enum Language {

	ITEM_NAME("item.name"), ITEM_LORE("item.lore"), MESSAGES_SHOOT("messages.shoot"),
	MESSAGES_SHOOT_NO_EXP("messages.shoot-no-exp"), MESSAGES_EAT("messages.eat"),
	COMMAND_INVALID("command.invalid"), COMMAND_NO_PERMISSION("command.no-permission"),
	COMMAND_PLAYER_ONLY("player-only"), COMMAND_STATUS("command.status"),
	COMMAND_RESTORE("command.restore"), COMMAND_SET("command.set"),
	COMMAND_RELOADED("command.reloaded"), COMMAND_HELP_TITLE("command.help.title"),
	COMMAND_HELP_HELP("command.help.help"), COMMAND_HELP_STATUS("command.help.status"),
	COMMAND_HELP_ITEM("command.help.item"), COMMAND_HELP_RESTORE("command.help.restore"),
	COMMAND_HELP_SET("command.help.set"), COMMAND_HELP_RELOAD("command.help.reload");

	private static FileConfiguration config;
	private final String path;

	/**
	 * @param path
	 * 此项语言的路径
	 */
	Language(String path) {
		this.path = path;
	}

	/**
	 * 加载语言文件
	 */
	public static void load() {
		String lang = Config.LANG.getString();
		String fileName = "lang/" + lang + ".yml";
		config = ConfigUtil.load(fileName);
	}

	/**
	 * 保存默认的语言文件
	 */
	public static void saveDefault() {
		ConfigUtil.saveDefault("lang/zh_CN.yml");
	}

	/**
	 * 获取字符串，并转换样式代码
	 * @return 最终的字符串
	 */
	public String getString() {
		String string = config.getString(path);
		if (string != null){
			string = ChatColor.translateAlternateColorCodes('&', string);
		}
		return string;
	}

	/**
	 * 获取字符串list，并转换每个元素的样式代码
	 * @return 最终的字符串list
	 */
	public List<String> getStringList() {
		List<String> list = new ArrayList<>();
		List<String> source = config.getStringList(path);
		for (String string : source) {
			list.add(ChatColor.translateAlternateColorCodes('&', string));
		}
		return list;
	}
}
