package me.davethecamper.custompets.eggs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import me.davethecamper.custompets.Main;
import me.davethecamper.custompets.Utils;
import me.davethecamper.custompets.objects.Incubadora;
import me.davethecamper.custompets.objects.PlayerProfile;
import me.davethecamper.cash.CashManager;

public class OvosManager {
	
	public final static String TITLE_OVOS = "§fOvos";
	public final static String TITLE_OVOS_COMPRAR = "§9Comprar ovos";
	public final static String TITLE_INCUBADORAS = "§9Incubadoras";
	
	
	private static HashMap<String, OvoRaridade> raridades = new HashMap<>();
	
	private static HashMap<String, OvoRaridade> pets_raridades = new HashMap<>();
	
	private static HashMap<String, Ovo> ovos = new HashMap<>();
	

	
	public static void setPetRaridade(Set<String> set, OvoRaridade or) {
		for (String s : set) {
			setPetRaridade(s, or);
		}
	}
	
	public static void setPetRaridade(String s, OvoRaridade or) {pets_raridades.put(s, or);}
	
	public static OvoRaridade getPetRaridade(String r) {return pets_raridades.get(r);}
	
	public static OvoRaridade getRaridade(String r) {return raridades.get(r);}
	
	public static Ovo getOvo(String r) {return ovos.get(r);}
	
	public static Set<Ovo> getOvosRegistered() {
		Set<Ovo> set = new HashSet<>();
		for (String s : ovos.keySet()) {
			set.add(ovos.get(s));
		}
		return set;
	}
	
	
	public static void init() {
		File f = new File(Main.main.getDataFolder().getAbsolutePath() + "/config.yml");
		if (!f.exists()) Main.main.saveResource("config.yml", true);
		
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		if (fc.get("raridades") == null) {
			Main.main.saveResource("config.yml", true);
			fc = YamlConfiguration.loadConfiguration(f);
		}
		
		for (String raridade : fc.getConfigurationSection("raridades").getKeys(false)) {
			raridades.put(raridade, new OvoRaridade(fc, raridade));
		}
		
		
		
		
		f = new File(Main.main.getDataFolder().getAbsolutePath() + "/ovos/");
		if (!f.exists()) Main.main.saveResource("ovos/OvoDaSelva.yml", true);
		
		File files[] = f.listFiles();
		
		for (int i = 0; i < files.length; i++) {
			f = files[i];
			ovos.put(f.getName().replaceAll(".yml", ""), new Ovo(f));
		}
	}
	
	public static Inventory getOvosInventory(Player p) {
		Inventory inv = Bukkit.createInventory(null, 27, TITLE_OVOS);
		int slot = 0;
		
		for (String s : Main.getPlayerProfile(p.getUniqueId()).getOvos().keySet()) {
			Ovo o = getOvo(s);
			ItemStack item = o.getItem().clone();
			ItemMeta im = item.getItemMeta();
			ArrayList<String> lore = new ArrayList<>(im.getLore());
			
			lore.add("");
			lore.add("§a§oClick para incuba-lo");
			
			im.setLore(lore);
			im.setDisplayName(im.getDisplayName() + " §7[§f" + Main.getPlayerProfile(p.getUniqueId()).getOvos().get(s) + "§7]");
			
			item.setItemMeta(im);
			
			inv.setItem(slot, item);
			slot++;
		}
		
		if (slot == 0) {
			inv.setItem(0, Utils.criarItemMenus(Material.BARRIER, "§cSem ovos!", Utils.formatText("Você não tem ovos! Consiga-os no /passe ou matando bosses nas dungeons", "§7")));
		}
		
		inv.setItem(inv.getSize()-9, Utils.criarItemMenus(Material.SKELETON_SPAWN_EGG, "§aComprar ovos"));
		
		inv.setItem(inv.getSize()-1, Utils.criarItemMenus(Material.PAPER, "§aVoltar"));
		
		return inv;
	}
	
	public static Inventory getIncubadorasInventory(Player p) {
		Inventory inv = Bukkit.createInventory(null, 27, TITLE_INCUBADORAS);
		PlayerProfile pp = Main.getPlayerProfile(p.getUniqueId());
		int slot = 0;
		
		for (Incubadora i : pp.getAllIncubadoras()) {
			inv.setItem(slot, i.getDisplayItem());
			slot++;
		}
		
		
		inv.setItem(inv.getSize()-1, Utils.criarItemMenus(Material.PAPER, "§aVoltar"));
		
		return inv;
	}
	
	public static Inventory getBuyEggsInventory(PlayerProfile pp) {
		Inventory inv = Bukkit.createInventory(null, 36, TITLE_OVOS_COMPRAR);
		
		ItemStack black = Utils.criarItemMenus(Material.BLACK_STAINED_GLASS_PANE, "§r");
		for (int i = 0; i < inv.getSize(); i++) {
			inv.setItem(i, black);
		}
		
		inv.setItem(2, Utils.criarItemMenus(Material.SKELETON_SPAWN_EGG, "§bComprar 1 ovo"));
		inv.setItem(4, Utils.criarItemMenus(Material.SKELETON_SPAWN_EGG, "§bComprar 5 ovos"));
		inv.setItem(6, Utils.criarItemMenus(Material.SKELETON_SPAWN_EGG, "§bComprar 15 ovos"));
		
		inv.setItem(11, Utils.criarItemMenus(Material.GOLD_INGOT, "§61 cash", "§5Você possui: §d" + CashManager.getCash(pp.getUniqueId()) + " cash's" + ",,,,§7§oAperte 9 para comprar"));
		inv.setItem(13, Utils.criarItemMenus(Material.GOLD_INGOT, "§64 cash", "§5Você possui: §d" + CashManager.getCash(pp.getUniqueId()) + " cash's" + ",,,,§7§oAperte 9 para comprar"));
		inv.setItem(15, Utils.criarItemMenus(Material.GOLD_INGOT, "§610 cash", "§5Você possui: §d" + CashManager.getCash(pp.getUniqueId()) + " cash's" + ",,,,§7§oAperte 9 para comprar"));

		inv.setItem(20, Utils.criarItemMenus(Material.IRON_INGOT, "§73 fragmentos", "§5Você possui: §d" + pp.getFragmentos() + " fragmentos" + ",,,,§7§oAperte 9 para comprar"));
		inv.setItem(22, Utils.criarItemMenus(Material.IRON_INGOT, "§715 fragmentos", "§5Você possui: §d" + pp.getFragmentos() + " fragmentos" + ",,,,§7§oAperte 9 para comprar"));
		inv.setItem(24, Utils.criarItemMenus(Material.IRON_INGOT, "§745 fragmentos", "§5Você possui: §d" + pp.getFragmentos() + " fragmentos" + ",,,,§7§oAperte 9 para comprar"));

		inv.setItem(inv.getSize()-1, Utils.criarItemMenus(Material.PAPER, "§aVoltar"));
		
		return inv;
	}
	

}
