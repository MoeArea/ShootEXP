package moe.feo.shootexp;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

/**
 * 工具类
 */
public class Util {

	/**
	 * 将占位符替换为可翻译字符串，模仿原版聊天的颜色与格式的运作方法
	 * @param msg
	 * 原始消息
	 * @param placeholder
	 * 占位符
	 * @param path
	 * 可翻译字符串路径
	 * @return 最终的文字组件
	 */
	public static TextComponent translateEntityComponent(String msg, String placeholder, String path) {
		String[] normalMessages = msg.split(placeholder);
		TextComponent component = new TextComponent();
		// 上一个组件的颜色与格式
		char color = 0;
		List<Character> formats = new ArrayList<>();
		// 每一句的后面插入一个可翻译的被施法者组件
		for (int i = 0; i < normalMessages.length; i++) {
			String normalMessage = normalMessages[i];
			TextComponent normalMessageComponent = new TextComponent(normalMessage);
			// 用上一个组件为这个普通消息组件设置格式
			component.addExtra(formatBaseComponent(normalMessageComponent, color, formats));
			// 如果已经遍历到最后一句，就不用再往后加组件了
			if (i == normalMessages.length - 1) {
				// 除非这条消息以占位符结尾...
				if (!msg.endsWith(placeholder)) {
					break;
				}
			}
			// 处理颜色字符
			char[] chars = normalMessage.toCharArray();
			boolean isColor = false;
			for (char code : chars) {
				if (isColor) {
					// 如果是颜色字符
					if ("0123456789AaBbCcDdEeFfXx".contains(String.valueOf(code))) {
						color = code;
						// 原版的颜色字符会清掉后面字符的格式
						formats.clear();
					} else if ("KkLlMmNnOoRr".contains(String.valueOf(code))) {
						formats.add(code);
					}
					isColor = false;
				}
				if (code == '§') {
					isColor = true;
				}
			}
			// 要替换上去的被施法者组件
			TranslatableComponent translatableMessageComponent = new TranslatableComponent(path);
			// 用之前的普通消息组件为这个可翻译字符串组件设置格式
			formatBaseComponent(translatableMessageComponent, color, formats);
			component.addExtra(translatableMessageComponent);
		}
		return component;
	}

	/**
	 * 用给定的颜色和格式格式化组件
	 * @param component
	 * 需要被格式化的组件
	 * @param color
	 * 颜色代码
	 * @param formats
	 * 格式代码list
	 * @return 格式化后的组件
	 */
	// 格式化组件
	public static BaseComponent formatBaseComponent(BaseComponent component, char color, List<Character> formats) {
		// 处理颜色字符
		if (color != 0) {
			component.setColor(ChatColor.getByChar(color));
		}
		for (Character character : formats) {
			if (character.equals('K') || character.equals('k')) {
				component.setObfuscated(true);
			}
			if (character.equals('L') || character.equals('l')) {
				component.setBold(true);
			}
			if (character.equals('M') || character.equals('m')) {
				component.setStrikethrough(true);
			}
			if (character.equals('N') || character.equals('n')) {
				component.setUnderlined(true);
			}
			if (character.equals('O') || character.equals('o')) {
				component.setItalic(true);
			}
			if (character.equals('R') || character.equals('r')) {
				component.setColor(ChatColor.WHITE);
				component.setObfuscated(false);
				component.setBold(false);
				component.setStrikethrough(false);
				component.setUnderlined(false);
				component.setItalic(false);
			}
		}
		return component;
	}

	/**
	 * 获取给定范围内属于给定类型的最近的实体
	 * @param self
	 * 需要排除的自己
	 * @param range
	 * 范围
	 * @param includes
	 * 实体的类型列表
	 * @return 最近的实体
	 */
	public static Entity getNearestEntity(Entity self, double range, List<String> includes) {
		World world = self.getWorld();
		Location location = self.getLocation();
		Collection<Entity> entityList = world.getNearbyEntities(location, range, range, range);
		Entity partner = null;
		List<Class> classes = new ArrayList<>();
		// 实体类型
		for (String include : includes) {
			try {
				Class clazz = Class.forName("org.bukkit.entity." + include);
				classes.add(clazz);
			} catch (ClassNotFoundException e) {
				Bukkit.getLogger().log(Level.SEVERE, "Illegal Entity type.", e);
			}
		}
		// 便利实体列表
		double partnerDistance = range;// 同伴最大距离
		for (Entity entity : entityList) {
			if (entity.equals(self)) {
				continue;
			}
			// 判断是否为配置文件中定义的实体类型
			boolean isDefinition = false;
			for (Class clazz : classes) {
				// 如果partner是这种类型
				if (clazz.isInstance(entity)) {
					isDefinition = true;
					break;
				}
			}
			if (!isDefinition) {
				continue;
			}
			double cacheDistance = self.getLocation().distance(entity.getLocation());
			if (partner == null || cacheDistance < partnerDistance) {
				partner = entity;
				partnerDistance = cacheDistance;
			}
		}
		return partner;
	}

}
