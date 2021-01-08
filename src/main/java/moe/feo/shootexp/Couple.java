package moe.feo.shootexp;

import moe.feo.shootexp.config.Config;
import moe.feo.shootexp.config.Language;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Couple {

	private final Player attacker;// 进攻方
	private Player defender;// 防守方
	private int numOfAttack;// 攻击次数
	private final int period = 20;// 循环间隔

	private static final Map<UUID, Couple> activeCoupleMap = new HashMap<>();// 正在干活的玩家名单

	public Couple(Player attacker, Player defender) {
		this.attacker = attacker;
		this.defender = defender;
		timer.runTaskTimer(ShootEXP.getPlugin(ShootEXP.class), 0, period);// 循环检查玩家状态
	}

	BukkitRunnable timer = new BukkitRunnable() {
		private int cacheNumOfAttack = 0;// 缓存的攻击次数
		private int attackTimeoutCount = 0;// 攻击超时计数

		public void run() {
			checkNum();
			checkTimes();
		}

		public void checkTimes() {// 检查超时
			if (numOfAttack > cacheNumOfAttack) {// 攻击次数增加
				attackTimeoutCount = 0;// 超时计数置零
			} else {
				attackTimeoutCount++;// 超时计数增加
			}
			if (attackTimeoutCount > getCountWhenTimeOut()) {// 超时
				timer.cancel();
				activeCoupleMap.remove(attacker.getUniqueId());// 从干活玩家名单中删除
			}
			cacheNumOfAttack = numOfAttack;
		}

		public void checkNum() {// 检查攻击次数
			if (!PlayerStatus.hasStatus(attacker.getUniqueId())) {// 如果不存在攻击者的状态数据
				PlayerStatus.addStatus(attacker.getUniqueId(), new PlayerStatus());// 放一个数据进去
			}
			if (numOfAttack >= PlayerStatus.getStatus(attacker.getUniqueId()).getRequiredAttackTimes()) {// 当攻击次数大于所需次数
				int EXPAmount = PlayerStatus.getStatus(attacker.getUniqueId()).ejaculation();// 射一次
				String msg;
				String sound;
				if (EXPAmount != 0) {
					ItemStack EXPItem = new EXP(attacker.getName(), defender.getName(), EXPAmount).getEXPItem();
					attacker.getWorld().dropItem(attacker.getLocation(), EXPItem);
					msg = Language.MESSAGES_SHOOT.getString().replace("%ATTACKER%", attacker.getName()).replace("%DEFENDER%", defender.getName())
							.replace("%TIMES%", String.valueOf(numOfAttack)).replace("%AMOUNT%", String.valueOf(EXPAmount));
					sound = Config.SOUND_SHOOT.getString();
				} else {
					msg = Language.MESSAGES_SHOOT_NO_EXP.getString().replace("%ATTACKER%", attacker.getName()).replace("%DEFENDER%", defender.getName())
							.replace("%TIMES%", String.valueOf(numOfAttack));
					sound = Config.SOUND_SHOOT_NO_EXP.getString();
				}
				attacker.getWorld().playSound(attacker.getLocation(), sound, SoundCategory.PLAYERS, 1, 1);
				Bukkit.getServer().broadcastMessage(msg);
				timer.cancel();// 将定时器移除
				activeCoupleMap.remove(attacker.getUniqueId());// 把这个对象从正在干活的列表中移除
			}
		}
	};

	public void setDefender(Player player) {
		this.defender = player;
	}

	public int getCountWhenTimeOut() {// 获取达到超时条件的count数值, i为超时秒数
		return Config.ATTACK_TIMEOUT.getInt() / period;
	}

	public void attack() {
		numOfAttack++;// 攻击次数增加
	}

	public static void addCouple(UUID uuid, Couple couple) {
		activeCoupleMap.put(uuid, couple);
	}

	public static boolean hasCouple(UUID uuid) {
		return activeCoupleMap.containsKey(uuid);
	}

	public static Couple getCouple(UUID uuid) {
		return activeCoupleMap.get(uuid);
	}
}
