package moe.feo.shootexp;

import moe.feo.shootexp.config.Config;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStatus {

	private static Map<UUID, PlayerStatus> statusMap = new HashMap<>();
	private int timesOfShoot = 0;// 发射经验次数
	private int stock = Config.MAX_STOCK.getInt();
	private BukkitTask restoreShootTask = null;// 恢复发射次数任务
	private BukkitTask restoreStockTask = null;// 恢复经验存量任务

	private BukkitRunnable getRestoreShootRunnable() {// 获取一个新的runnable
		return new BukkitRunnable() {
			public void run() {
				// 只要发射经验次数小于0就不会恢复，否则恢复一次并判断是否恢复满
				if (timesOfShoot <= 0 || restoreShoot()) {// 恢复一次
					restoreShootTask.cancel();//如果恢复满则退出定时器
					restoreShootTask = null;
				}
			}
		};
	}

	private BukkitRunnable getRestoreStockRunnable() {// 获取一个新的runnable
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

	public int getRequiredAttackTimes() {// 所需蹲起次数
		Expression e = new ExpressionBuilder(Config.REQUIRED_ATTACK_TIMES.getString())
				.variables("SHOOT", "STOCK", "MAXSTOCK")
				.build()
				.setVariable("SHOOT", timesOfShoot)
				.setVariable("STOCK", stock)
				.setVariable("MAXSTOCK", Config.MAX_STOCK.getInt());
		double result = e.evaluate();
		return (int) result;
	}

	public int getShootAmount() {// 射出经验量
		Expression e = new ExpressionBuilder(Config.SHOOT_AMOUNT.getString())
				.variables("SHOOT", "STOCK", "MAXSTOCK")
				.build()
				.setVariable("SHOOT", timesOfShoot)
				.setVariable("STOCK", stock)
				.setVariable("MAXSTOCK", Config.MAX_STOCK.getInt());
		double result = e.evaluate();
		return (int) result;
	}

	public int ejaculation() {// 射一次，返回射出经验量
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

	public boolean restoreShoot() {// 恢复一次，返回是否恢复满
		timesOfShoot = timesOfShoot - Config.RESTORE_SHOOT_AMOUNT.getInt();
		// 检查属性是否合法
		if (timesOfShoot < 0) {
			timesOfShoot = 0;
		}
		return timesOfShoot == 0;
	}

	public boolean restoreShoot(int times) {// 恢复一次指定次数，并允许已射出次数为负数，返回是否恢复满
		timesOfShoot = timesOfShoot - times;
		return timesOfShoot <= 0;
	}

	public boolean restoreStock() {// 恢复一次，返回是否恢复满
		stock = stock + Config.RESTORE_STOCK_AMOUNT.getInt();
		// 检查属性是否合法
		int max = Config.MAX_STOCK.getInt();
		if (stock > max) {
			stock = max;
		}
		return stock == max;
	}

	public boolean restoreStock(int amount) {// 恢复一次指定数量，并允许数量超过最大值，返回是否恢复满
		stock = stock + amount;
		return stock >= Config.MAX_STOCK.getInt();
	}

	public static void addStatus(UUID uuid, PlayerStatus status) {
		statusMap.put(uuid, status);
	}

	public static boolean hasStatus(UUID uuid) {
		return statusMap.containsKey(uuid);
	}

	public static PlayerStatus getStatus(UUID uuid) {
		return statusMap.get(uuid);
	}
}
