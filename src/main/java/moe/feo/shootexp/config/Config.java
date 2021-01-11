package moe.feo.shootexp.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public enum Config {

	LANG("lang"), MAX_STOCK("max-stock"), REQUIRED_ATTACK_TIMES("required-attack-times"),
	SHOOT_AMOUNT("shoot-amount"), ENTITY_TYPE("entity-type"), EXP_TYPE("exp-type"),
	ATTACK_DISTANCE("attack.distance"), ATTACK_TIMEOUT("attack.timeout"),
	RESTORE_SHOOT_PERIOD("restore.shoot.period"), RESTORE_SHOOT_AMOUNT("restore.shoot.amount"),
	RESTORE_STOCK_PERIOD("restore.stock.period"), RESTORE_STOCK_AMOUNT("restore.stock.amount"),
	CUSTOM_MODEL_DATA_ENABLE("custom-model-data.enable"), CUSTOM_MODEL_DATA_VALUE("custom-model-data.value"),
	SOUND_ATTACK("sound.attack"), SOUND_SHOOT("sound.shoot"), SOUND_SHOOT_NO_EXP("sound.shoot-no-exp"),
	SOUND_EAT("sound.eat");

	private static FileConfiguration config;
	private final String path;

	Config(String path) {
		this.path = path;
	}

	public static void load() {
		config = ConfigUtil.load("config.yml");
	}

	public int getInt() {
		return config.getInt(path);
	}

	public double getDouble() {
		return config.getDouble(path);
	}

	public boolean getBoolean() {
		return config.getBoolean(path);
	}

	public String getString() {
		return config.getString(path);
	}

	public List<String> getStringList() {
		return config.getStringList(path);
	}
}
