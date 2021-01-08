package moe.feo.shootexp;

import moe.feo.shootexp.config.Config;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.List;

public class AttackListener implements Listener {

	@EventHandler
	public void onShift(PlayerToggleSneakEvent e) {
		if (e.isSneaking()) {// 按下了shift
			Player attacker = e.getPlayer();
			List<Player> playerList = e.getPlayer().getWorld().getPlayers();
			//e.getPlayer().getWorld().getNearbyEntities();
			Player partner = null;
			double partnerDistance = Config.ATTACK_DISTANCE.getDouble();// 同伴最大距离
			for (Player p : playerList) {
				if (p.equals(attacker)) {// 要排除自己
					continue;
				}
				double distance = e.getPlayer().getLocation().distance(p.getLocation());
				if (distance <= 2) {
					if (partner == null || distance < partnerDistance) {// 如果这个玩家比之前的玩家更靠近主角
						partner = p;
						partnerDistance = distance;
					}
				}
			}
			if (partner == null) {// 该玩家没有干任何人
				return;
			}
			if (!Couple.hasCouple(attacker.getUniqueId())) {// 没有该玩家数据，说明该玩家之前没有在干活
				Couple couple = new Couple(attacker, partner);
				Couple.addCouple(attacker.getUniqueId(), couple);// 添加到正在干活的玩家名单
			}
			Couple couple = Couple.getCouple(attacker.getUniqueId());
			couple.setDefender(partner);// 更新施法对象
			couple.attack();// 攻击一次
			attacker.getWorld().playSound(attacker.getLocation(), Config.SOUND_ATTACK.getString(), SoundCategory.PLAYERS, 1, 1);
		}
	}
}
