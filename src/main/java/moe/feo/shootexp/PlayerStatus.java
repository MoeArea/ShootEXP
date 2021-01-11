package moe.feo.shootexp;

import moe.feo.shootexp.config.Config;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * 这个类表示一个玩家的状态
 */
public class PlayerStatus {

	private int timesOfShoot = 0;// 发射经验次数
	private int stock = Config.MAX_STOCK.getInt();
	private BukkitTask restoreShootTask = null;// 恢复发射次数任务
	private BukkitTask restoreStockTask = null;// 恢复经验存量任务

	/**
	 * 获取一个新的恢复发射经验次数的Runnable
	 * @return 一个新的BukkitRunnable
	 */
	private BukkitRunnable getRestoreShootRunnable() {
		return new BukkitRunnable() {
			public void run() {
				// 只要发射经验次数小于0就不会恢复，否则恢复一次并判断是否恢复满
				if (timesOfShoot <= 0 || restoreShoot()) {
					restoreShootTask.cancel();//如果恢复满则退出定时器
					restoreShootTask = null;
				}
			}
		};
	}

	/**
	 * 获取一个新的恢复经验存量的Runnable
	 * @return 一个新的BukkitRunnable
	 */
	private BukkitRunnable getRestoreStockRunnable() {
		return new BukkitRunnable() {
			public void run() {
				// 只要存量大于设定值就不会恢复，否则恢复一次并判断是否恢复满
				if (stock >= Config.MAX_STOCK.getInt() || restoreStock()) {
					restoreStockTask.cancel();//如果恢复满则退出定时器
					restoreStockTask = null;
				}
			}
		};
	}

	/**
	 * 设置射出次数
	 * @param times
	 * 射出次数
	 */
	public void setTimesOfShoot(int times) {
		this.timesOfShoot = times;
	}

	/**
	 * 获取射出次数
	 * @return 射出次数
	 */
	public int getTimesOfShoot() {
		return this.timesOfShoot;
	}

	/**
	 * 设置经验存量
	 * @param stock
	 * 经验存量
	 */
	public void setStock(int stock) {
		this.stock = stock;
	}

	/**
	 * 获取经验存量
	 * @return 经验存量
	 */
	public int getStock() {
		return this.stock;
	}

	/**
	 * 获取下次成功施法所需的攻击次数
	 * @return 所需的蹲起次数
	 */
	public int getRequiredAttackTimes() {
		Expression e = new ExpressionBuilder(Config.REQUIRED_ATTACK_TIMES.getString())
				.variables("SHOOT", "STOCK", "MAXSTOCK")
				.build()
				.setVariable("SHOOT", timesOfShoot)
				.setVariable("STOCK", stock)
				.setVariable("MAXSTOCK", Config.MAX_STOCK.getInt());
		double result = e.evaluate();
		return (int) result;
	}

	/**
	 * 获取下次施法成功时射出的经验量
	 * @return 射出的经验量
	 */
	public int getShootAmount() {
		Expression e = new ExpressionBuilder(Config.SHOOT_AMOUNT.getString())
				.variables("SHOOT", "STOCK", "MAXSTOCK")
				.build()
				.setVariable("SHOOT", timesOfShoot)
				.setVariable("STOCK", stock)
				.setVariable("MAXSTOCK", Config.MAX_STOCK.getInt());
		double result = e.evaluate();
		return (int) result;
	}

	/**
	 * 射一次，注意这不是真的射了，而是让玩家在数据上射了一次
	 * @return 射出的经验量
	 */
	public int ejaculation() {
		int amount = 0;
		if (stock > 0) {// 经验存量大于0，开始计算射出量
			amount = getShootAmount();
		}
		stock = stock - amount;
		timesOfShoot++;
		if (restoreShootTask == null) {// 如果没有恢复发射次数任务正在进行
			int period = Config.RESTORE_SHOOT_PERIOD.getInt();
			restoreShootTask = getRestoreShootRunnable().runTaskTimer(ShootEXP.getPlugin(ShootEXP.class), period, period);
		}
		if (restoreStockTask == null) {
			int period = Config.RESTORE_STOCK_PERIOD.getInt();
			restoreStockTask = getRestoreStockRunnable().runTaskTimer(ShootEXP.getPlugin(ShootEXP.class), period, period);
		}
		return amount;
	}

	/**
	 * 恢复一次射出次数
	 * @return 是否恢复满
	 */
	public boolean restoreShoot() {
		timesOfShoot = timesOfShoot - Config.RESTORE_SHOOT_AMOUNT.getInt();
		// 检查属性是否合法
		if (timesOfShoot < 0) {
			timesOfShoot = 0;
		}
		return timesOfShoot == 0;
	}

	/**
	 * 恢复一次指定次数的射出次数，允许已射出次数为负数
	 * @param times
	 * 恢复的射出次数
	 * @return 是否恢复满
	 */
	public boolean restoreShoot(int times) {
		timesOfShoot = timesOfShoot - times;
		return timesOfShoot <= 0;
	}

	/**
	 * 将射出次数清零
	 */
	public void restoreShootFull() {
		if (timesOfShoot > 0) {
			timesOfShoot = 0;
		}
	}

	/**
	 * 恢复一次经验存量
	 * @return 是否恢复满
	 */
	public boolean restoreStock() {
		stock = stock + Config.RESTORE_STOCK_AMOUNT.getInt();
		// 检查属性是否合法
		int max = Config.MAX_STOCK.getInt();
		if (stock > max) {
			stock = max;
		}
		return stock == max;
	}

	/**
	 * 恢复一次指定数量的经验存量，并允许数量超过最大值
	 * @param amount
	 * 恢复的经验存量
	 * @return 是否恢复满
	 */
	public boolean restoreStock(int amount) {
		stock = stock + amount;
		return stock >= Config.MAX_STOCK.getInt();
	}

	/**
	 * 恢复满玩家经验存量
	 */
	public void restoreStockFull() {
		if (stock < Config.MAX_STOCK.getInt()) {
			stock = Config.MAX_STOCK.getInt();
		}
	}
}
