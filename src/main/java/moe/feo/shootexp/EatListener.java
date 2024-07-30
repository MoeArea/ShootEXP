package moe.feo.shootexp;

import com.dre.brewery.api.BreweryApi;
import moe.feo.shootexp.config.Config;
import moe.feo.shootexp.config.Language;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import static org.bukkit.Bukkit.getServer;

/**
 * 监听玩家右键
 */
public class EatListener implements Listener {

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		Action action = e.getAction();
		ItemStack item = e.getItem();
		if (item == null) {
			return;
		}
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			if (!EXP.isEXPItem(item)) {
				return;
			}
			if (((getServer().getPluginManager().getPlugin("Brewery") != null && getServer().getPluginManager().getPlugin("Brewery").isEnabled())
					|| (getServer().getPluginManager().getPlugin("BreweryX") != null && getServer().getPluginManager().getPlugin("BreweryX").isEnabled()))
					&& action == Action.RIGHT_CLICK_BLOCK) {
				System.out.println("Material: " + e.getClickedBlock().getType().name());
				if (BreweryApi.getBarrel(e.getClickedBlock()) != null || BreweryApi.getCauldron(e.getClickedBlock()) != null) {
					return;
				} else if (e.getClickedBlock().getType() == Material.getMaterial("WATER_CAULDRON")) {
					int x = e.getClickedBlock().getLocation().getBlockX();
					int y = e.getClickedBlock().getLocation().getBlockY();
					int z = e.getClickedBlock().getLocation().getBlockZ();
					World world = e.getClickedBlock().getLocation().getWorld();
					Location down = new Location(world, x, y-1, z);
					if (down.getBlock().getType() == Material.FIRE) {
						return;
					}
				}
			}
			EXP exp = new EXP(item);
			switch (Config.EXP_TYPE.getString()){
				case "SKILLAPI":
					com.sucy.skill.SkillAPI.getPlayerData(player)
							.giveExp(exp.getAmount(), com.sucy.skill.api.enums.ExpSource.SPECIAL);
					break;
				case "MMOCORE":
					net.Indyuce.mmocore.api.player.PlayerData.get(player)
							.giveExperience(exp.getAmount(), net.Indyuce.mmocore.api.experience.EXPSource.OTHER);
					break;
				default:
					player.giveExp(exp.getAmount());
			}
			player.getWorld().playSound(player.getLocation(), Config.SOUND_EAT.getString(), SoundCategory.PLAYERS, 1, 1);
			String msg = Language.MESSAGES_EAT.getString().replace("%PLAYER%", player.getName()).replace("%OWNER%", exp.getOwner())
					.replace("%RECIPIENT%", exp.getRecipient()).replace("%AMOUNT%", String.valueOf(exp.getAmount()));
			if (Config.PRIVATE_MESSAGE.getBoolean()) {
				player.sendMessage(msg);
				Player owner = Bukkit.getPlayer(exp.getOwner());
				Player recipient = Bukkit.getPlayer(exp.getRecipient());
				if (owner != null && owner.isOnline()) {
					owner.sendMessage(msg);
				}
				if (recipient != null && recipient.isOnline()) {
					recipient.sendMessage(msg);
				}
			} else {
				getServer().broadcastMessage(msg);
			}
			item.setAmount(0);
			e.setCancelled(true);
		}
	}
}
