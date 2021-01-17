package moe.feo.shootexp;

import moe.feo.shootexp.config.Config;
import moe.feo.shootexp.config.Language;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Couple表示一对情侣
 * 包括一个进攻方和防守方
 */
public class Couple {

	private final Player attacker;// 进攻方
	private Entity defender;// 防守方
	private int numOfAttack;// 攻击次数
	private final int period = 20;// 循环检查的间隔

	/**
	 * 初始化一对情侣
	 * @param attacker
	 * 进攻方
	 * @param defender
	 * 防守方
	 */
	public Couple(Player attacker, Entity defender) {
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

		/**
		 * 检查玩家是否超时
		 */
		public void checkTimes() {
			if (numOfAttack > cacheNumOfAttack) {// 攻击次数增加
				attackTimeoutCount = 0;// 超时计数置零
			} else {
				attackTimeoutCount++;// 超时计数增加
			}
			int timeoutCount = Config.ATTACK_TIMEOUT.getInt() / period;
			if (attackTimeoutCount > timeoutCount) {// 超时
				timer.cancel();
				CoupleManager.removeCouple(attacker.getUniqueId());// 从干活玩家名单中删除
			}
			cacheNumOfAttack = numOfAttack;
		}

		/**
		 * 检查攻击次数
		 */
		public void checkNum() {
			if (!PlayerStatusManager.hasStatus(attacker.getUniqueId())) {// 如果不存在攻击者的状态数据
				PlayerStatusManager.addStatus(attacker.getUniqueId(), new PlayerStatus());// 放一个数据进去
			}
			if (numOfAttack >= PlayerStatusManager.getStatus(attacker.getUniqueId()).getRequiredAttackTimes()) {// 当攻击次数大于所需次数
				int EXPAmount = PlayerStatusManager.getStatus(attacker.getUniqueId()).ejaculation();// 射一次
				boolean isTranslate = false;
				String msg;
				String sound;
				if (EXPAmount != 0) {
					ItemStack EXPItem = new EXP(attacker.getName(), defender.getName(), EXPAmount).getEXPItem();
					attacker.getWorld().dropItem(attacker.getLocation(), EXPItem);
					msg = Language.MESSAGES_SHOOT.getString().replace("%ATTACKER%", attacker.getName())
							.replace("%TIMES%", String.valueOf(numOfAttack)).replace("%AMOUNT%", String.valueOf(EXPAmount));
					sound = Config.SOUND_SHOOT.getString();
				} else {
					msg = Language.MESSAGES_SHOOT_NO_EXP.getString().replace("%ATTACKER%", attacker.getName())
							.replace("%TIMES%", String.valueOf(numOfAttack));
					sound = Config.SOUND_SHOOT_NO_EXP.getString();
				}
				if (defender instanceof Player) {
					msg = msg.replace("%DEFENDER%", defender.getName());
				} else {
					String defenderName = defender.getCustomName();
					// 没有自定义名称，显示可翻译字符串名称
					if (defenderName == null) {
						isTranslate = true;
					} else {// 有自定义名称，显示自定义名称
						msg = msg.replace("%DEFENDER%", defender.getCustomName());
					}
				}
				if (isTranslate) {
					String path = "entity.minecraft." + defender.getType().toString().toLowerCase();
					TextComponent component = Util.translateEntityComponent(msg, "%DEFENDER%", path);
					if (Config.PRIVATE_MESSAGE.getBoolean()) {
						attacker.spigot().sendMessage(component);
						if (defender instanceof Player) {
							((Player) defender).spigot().sendMessage(component);
						}
					} else {
						Bukkit.spigot().broadcast(component);
					}
				} else {
					if (Config.PRIVATE_MESSAGE.getBoolean()) {
						attacker.sendMessage(msg);
						if (defender instanceof Player) {
							defender.sendMessage(msg);
						}
					} else {
						Bukkit.getServer().broadcastMessage(msg);
					}
				}
				attacker.getWorld().playSound(attacker.getLocation(), sound, SoundCategory.PLAYERS, 1, 1);
				timer.cancel();// 将定时器移除
				CoupleManager.removeCouple(attacker.getUniqueId());// 把这个对象从正在干活的列表中移除
			}
		}
	};

	/**
	 * 设置防守方
	 * @param defender
	 * 防守方
	 */
	public void setDefender(Entity defender) {
		this.defender = defender;
	}

	/**
	 * 攻击一次
	 */
	public void attack() {
		numOfAttack++;// 攻击次数增加
	}
}
