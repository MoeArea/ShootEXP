package moe.feo.shootexp;

import moe.feo.shootexp.config.Config;
import moe.feo.shootexp.config.Language;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个类表示一滩粘稠的经验
 */
public class EXP {

	private static final NamespacedKey ownerKey = new NamespacedKey(ShootEXP.getPlugin(ShootEXP.class), "owner");
	private static final NamespacedKey recipientKey = new NamespacedKey(ShootEXP.getPlugin(ShootEXP.class), "recipient");
	private static final NamespacedKey amountKey = new NamespacedKey(ShootEXP.getPlugin(ShootEXP.class), "amount");

	private final String owner;
	private final String recipient;
	private final int amount;
	private final ItemStack item;

	/**
	 * 构造一滩粘稠的经验
	 * @param owner
	 * 经验所有者
	 * @param recipient
	 * 经验赠予者
	 * @param amount
	 * 经验数量
	 */
	public EXP(String owner, String recipient, int amount) {
		this.owner = owner;
		this.recipient = recipient;
		this.amount = amount;
		this.item = genEXPItem();
	}

	/**
	 * 构造一滩粘稠的经验
	 * @param item
	 * 经验物品
	 */
	public EXP(ItemStack item) {
		if (item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			assert meta != null;
			PersistentDataContainer container = meta.getPersistentDataContainer();
			this.owner = container.get(ownerKey, PersistentDataType.STRING);
			this.recipient = container.get(recipientKey, PersistentDataType.STRING);
			Integer amount = container.get(amountKey, PersistentDataType.INTEGER);
			if (amount != null) {
				this.amount = amount;
			} else {
				this.amount = 0;
			}
		} else {
			this.owner = null;
			this.recipient = null;
			this.amount = 0;
		}
		this.item = item;
	}

	/**
	 * 判断物品是否为粘稠的经验
	 * @param item
	 * 物品
	 * @return 物品是否为粘稠的经验
	 */
	public static boolean isEXPItem(ItemStack item) {
		if (item.getType() == Material.BONE_MEAL || item.hasItemMeta()) {
			ItemMeta meta = item.getItemMeta();
			assert meta != null;
			PersistentDataContainer container = meta.getPersistentDataContainer();
			return container.has(ownerKey, PersistentDataType.STRING) || container.has(recipientKey, PersistentDataType.STRING) ||
					container.has(amountKey, PersistentDataType.STRING);
		}
		return false;
	}

	/**
	 * 生成一个经验物品
	 * @return 生成的经验物品
	 */
	private ItemStack genEXPItem() {
		ItemStack item = new ItemStack(Material.BONE_MEAL);
		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(Material.BONE_MEAL);
		String itemName = Language.ITEM_NAME.getString().replace("%OWNER%", owner)
				.replace("%RECIPIENT%", recipient).replace("%AMOUNT%", Integer.toString(amount));
		List<String > loreList = new ArrayList<>();
		for (String lore : Language.ITEM_LORE.getStringList()) {
			loreList.add(lore.replace("%OWNER%", owner).replace("%RECIPIENT%", recipient).
					replace("%AMOUNT%", Integer.toString(amount)));
		}
		assert meta != null;
		meta.setDisplayName(itemName);
		meta.setLore(loreList);
		if (Config.CUSTOM_MODEL_DATA_ENABLE.getBoolean()) {
			meta.setCustomModelData(Config.CUSTOM_MODEL_DATA_VALUE.getInt());
		}
		PersistentDataContainer container = meta.getPersistentDataContainer();
		container.set(ownerKey, PersistentDataType.STRING, owner);
		container.set(recipientKey, PersistentDataType.STRING, recipient);
		container.set(amountKey, PersistentDataType.INTEGER, amount);
		item.setItemMeta(meta);
		return item;
	}

	/**
	 * 获取经验物品
	 * @return 经验物品
	 */
	public ItemStack getEXPItem() {
		return item;
	}

	/**
	 * 获取经验所有者的名称
	 * @return 经验所有者的名称
	 */
	public String getOwner() {
		if (owner == null) {
			return "UNKNOWN";
		}
		return owner;
	}

	/**
	 * 获取经验的赠予者
	 * @return 经验的赠予者
	 */
	public String getRecipient() {
		if (recipient == null) {
			return "UNKNOWN";
		}
		return recipient;
	}

	/**
	 * 获取这滩经验所蕴含的经验数量
	 * @return 经验的数量
	 */
	public int getAmount() {
		return amount;
	}
}
