package moe.feo.shootexp;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 这个类维护了一个Couple的map
 */
public class CoupleManager {

	private static final Map<UUID, Couple> activeCoupleMap = new HashMap<>();// 正在干活的玩家名单

	/**
	 * 增加一对情侣
	 * @param uuid
	 * 进攻者的UUID
	 * @param couple
	 * 情侣
	 */
	public static void addCouple(UUID uuid, Couple couple) {
		activeCoupleMap.put(uuid, couple);
	}

	/**通过UUID判断玩家作为攻击者是否有情侣
	 * @param uuid
	 * 进攻者的UUID
	 * @return 玩家作为攻击者是否有情侣
	 */
	public static boolean hasCouple(UUID uuid) {
		return activeCoupleMap.containsKey(uuid);
	}

	/**
	 * 通过UUID获取情侣
	 * @param uuid
	 * 攻击者的UUID
	 * @return 情侣
	 */
	public static Couple getCouple(UUID uuid) {
		return activeCoupleMap.get(uuid);
	}

	/**
	 * 移除一对情侣
	 * @param uuid
	 * 攻击者的UUID
	 */
	public static void removeCouple(UUID uuid) {
		activeCoupleMap.remove(uuid);
	}
}
