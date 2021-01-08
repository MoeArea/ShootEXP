package moe.feo.shootexp;

import moe.feo.shootexp.config.Config;
import moe.feo.shootexp.config.Language;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Commands implements TabExecutor {

	private static final Commands executor = new Commands();

	private Commands() {
	}

	public static Commands getInstance() {
		return executor;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
		if (args.length == 1) {
			List<String> list = new ArrayList<>();
			String arg = args[0].toLowerCase();
			if ("help".startsWith(arg)) list.add("help");
			if ("item".startsWith(arg) && sender.hasPermission("shootexp.item")) {
				list.add("item");
			}
			if ("reload".startsWith(arg) && sender.hasPermission("shootexp.reload")) {
				list.add("reload");
			}
			return list;
		}
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0) {// 没有带参数
			String[] helpArgs = { "help" };
			onCommand(sender, cmd, label, helpArgs);
		}
		switch (args[0].toLowerCase()) {
			case "help": {
				sender.sendMessage(Language.COMMAND_HELP_TITLE.getString());
				sender.sendMessage(Language.COMMAND_HELP_HELP.getString());
				if (sender.hasPermission("shootexp.reload")) {
					sender.sendMessage(Language.COMMAND_HELP_RELOAD.getString());
				}
				if (sender.hasPermission("shootexp.item")) {
					sender.sendMessage(Language.COMMAND_HELP_ITEM.getString());
				}
				break;
			}
			case "item": {
				if (!(sender instanceof Player)) {
					sender.sendMessage(Language.COMMAND_PLAYER_ONLY.getString());
					break;
				}
				if (!sender.hasPermission("shootexp.item")) {
					sender.sendMessage(Language.COMMAND_NO_PERMISSION.getString());
					break;
				}
				if (args.length != 4) {
					sender.sendMessage(Language.COMMAND_INVALID.getString());
					sender.sendMessage(Language.COMMAND_HELP_ITEM.getString());
					break;
				}
				String attacker = args[1];
				String defender = args[2];
				int amount;
				try {
					amount = Integer.parseInt(args[3]);
				} catch (NumberFormatException e) {
					sender.sendMessage(Language.COMMAND_INVALID.getString());
					sender.sendMessage(Language.COMMAND_HELP_ITEM.getString());
					break;
				}
				EXP exp = new EXP(attacker, defender, amount);
				Player player = (Player) sender;
				player.getInventory().addItem(exp.getEXPItem());
				break;
			}
			case "reload": {
				if (!sender.hasPermission("shootexp.reload")) {
					sender.sendMessage(Language.COMMAND_NO_PERMISSION.getString());
					break;
				}
				ShootEXP.getPlugin(ShootEXP.class).saveDefaultConfig();
				Config.load();
				Language.saveDefault();
				Language.load();
				sender.sendMessage(Language.COMMAND_RELOADED.getString());
				break;
			}
			default: {
				sender.sendMessage(Language.COMMAND_INVALID.getString());
				sender.sendMessage(Language.COMMAND_HELP_HELP.getString());
			}
		}
		return true;
	}
}
