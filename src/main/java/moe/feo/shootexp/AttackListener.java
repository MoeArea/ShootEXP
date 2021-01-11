package moe.feo.shootexp;

import moe.feo.shootexp.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.logging.Level;

public class AttackListener implements Listener {

	@EventHandler
	public void onShift(PlayerToggleSneakEvent e) {
		if (e.isSneaking()) {// 按下了shift
			Player attacker = e.getPlayer();
			Entity partner = Util.getNearestEntity(attacker, Config.ATTACK_DISTANCE.getDouble(), Config.ENTITY_TYPE.getStringList());
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
