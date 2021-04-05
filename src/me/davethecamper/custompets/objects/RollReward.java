package me.davethecamper.custompets.objects;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import me.davethecamper.custompets.Main;
import me.davethecamper.custompets.PetsManager;
import me.davethecamper.custompets.Utils;


public class RollReward {

	private static Set<Player> players_rolando = new HashSet<>();

	public static boolean isRolando(Player p) {return players_rolando.contains(p);}
	

	public static RecompensaChave preRoll(Player p, Caixa c, boolean recompensa, int id) {
		Inventory inv = Bukkit.createInventory(null, 27, "§5Roleta de pets");
		ItemStack preto = Utils.criarItemMenus(Material.BLACK_STAINED_GLASS_PANE, "§r");
		for (int i = 0; i < inv.getSize(); i++) { 
			inv.setItem(i, preto);
		}
		
		inv.setItem(22, Utils.criarItemMenus(Material.BARRIER, "§cPular"));
		
		if (p != null) {
			p.openInventory(inv);
		}
		
		return RollReward.rollInventory(p, inv, c, recompensa, "§5Roleta", id);
	}
	
	public static void preRoll(Player p, Caixa c, boolean recompensa, int id, RecompensaChave rc) {
		Inventory inv = Bukkit.createInventory(null, 27, "§5Roleta de pets");
		ItemStack preto = Utils.criarItemMenus(Material.BLACK_STAINED_GLASS_PANE, "§r");
		for (int i = 0; i < inv.getSize(); i++) { 
			inv.setItem(i, preto);
		}
		
		inv.setItem(22, Utils.criarItemMenus(Material.BARRIER, "§cPular"));
		
		if (p != null) {
			p.openInventory(inv);
		}
		
		RollReward.rollInventory(p, inv, c, rc, false, "§5Roleta de pets");
	}
	

	public static RecompensaChave rollInventory(Player p, Inventory inv, Caixa c, String title) {
		return rollInventory(p, inv, c, c.gerarRecompensa(), true, title, -1);
	}
	
	public static RecompensaChave rollInventory(Player p, Inventory inv, Caixa c, boolean recompensa, String title, int id) {
		return rollInventory(p, inv, c, c.gerarRecompensa(), recompensa, title, id);
	}
	
	public static RecompensaChave rollInventory(Player p, Inventory inv, Caixa c, final RecompensaChave rc, boolean recompensa, String title) {
		return rollInventory(p, inv, c, rc, recompensa, title, -1);
	}
	
	public static RecompensaChave rollInventory(Player p, Inventory inv, Caixa c, final RecompensaChave rc, String title) {
		return rollInventory(p, inv, c, rc, true, title, -1);
	}
	
	public static RecompensaChave rollInventory(Player p, Inventory inv, Caixa c, RecompensaChave rc, boolean recompensa, String title, int backup) {
		players_rolando.add(p);
		new BukkitRunnable() {
			ItemStack preto = Utils.criarItemMenus(Material.BLACK_STAINED_GLASS_PANE, "§r");
			int count = 0;
			int slot = 0;
            public void run() {
            	if (p != null && p.isValid()) {
                	if (!p.getOpenInventory().getTitle().equals(title)) {
                		if (recompensa) darRecompensa(p, rc);
                		players_rolando.remove(p);
                		
                		switch (backup) {
	                		case 1:
	                			p.openInventory(PetsManager.getMainInventory());
	                			break;
                		}
                		
                		this.cancel();
                		return;
                	}
                	
                	count++;
                	
            		if (count < 15*3) {
	            		ItemStack bt = count == ((15*3)-5) ? rc.getIcone() : c.gerarRecompensa().getIcone();
	            		
	            		for (int i = 8; i > 0; i--) {
	            			inv.setItem((9+i), inv.getItem((8+i)));
	            		}
	            		
	            		inv.setItem(9, bt);
	            		
	            		p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.4f);
	            		
            		} else if (count < (15*3) + 4) {
                		inv.setItem(12-slot, preto);
                		inv.setItem(14+slot, preto);
            			
            			slot++;
                	} else if (count < ((15*3) + 10)) {
                		if (count == ((15*3) + 9)) {
                			if (recompensa) p.playSound(p.getLocation(), Sound.ENTITY_WITCH_CELEBRATE, 3.0f, .3f);
                			players_rolando.remove(p);
                		}
                	} else {
                		if (recompensa) darRecompensa(p, rc);
                		switch (backup) {
	                		case 1:
	                			p.openInventory(PetsManager.getMainInventory());
	                			break;
                			default:
                        		p.closeInventory();
                				break;
                		}
                		this.cancel();
                	}
            	} else {
            		this.cancel();
            	}
            }
		}.runTaskTimer(Main.main, 0, 2);
		
		return rc;
	}
	
	public static void darRecompensa(Player p, RecompensaChave rc) {
		if (rc.getItemRewards() != null) {
			if (p.getInventory().firstEmpty() != -1) {
				p.getInventory().addItem(rc.getItemRewards().clone());
			} else {
				p.getWorld().dropItem(p.getLocation(), rc.getItemRewards().clone());
			}
		}
		
		if (rc.getComandosReward().size() > 0) {
			for (String s : rc.getComandosReward()) {
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("@player", p.getName()));
			}
		}
	}
}
