package me.davethecamper.custompets.objects;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RecompensaChave {
	
	public RecompensaChave() {}
	
	public RecompensaChave(double chance, ArrayList<String> comandos, ItemStack icone) {
		this.chance = chance;
		this.comandos_reward = comandos;
		
		this.name = icone.getItemMeta().getDisplayName();
		this.lore = (ArrayList<String>) icone.getItemMeta().getLore();
		this.tipo = icone.getType();
		this.quantia = icone.getAmount();
		this.glowing = icone.getItemMeta().hasEnchants() || (icone.getEnchantments() != null && icone.getEnchantments().size() > 0);
	}
	
	public RecompensaChave(double chance, ArrayList<String> comandos) {
		this.chance = chance;
		this.comandos_reward = comandos;
	}

	public RecompensaChave(double chance, ItemStack item, ItemStack icone) {
		this.chance = chance;
		this.item_reward = item;
		
		this.name = icone.getItemMeta().getDisplayName();
		this.lore = (ArrayList<String>) icone.getItemMeta().getLore();
		this.tipo = icone.getType();
		this.quantia = icone.getAmount();
		this.glowing = icone.getItemMeta().hasEnchants() || (icone.getEnchantments() != null && icone.getEnchantments().size() > 0);
	}
	
	public RecompensaChave(double chance, ItemStack item) {
		this.chance = chance;
		this.item_reward = item;
	}
	
	
	
	private String name;
	private Material tipo;
	private int quantia;
	private double chance;
	private boolean firework;
	private boolean glowing;
	private ArrayList<String> lore = new ArrayList<>();

	private ArrayList<String> mensagem = new ArrayList<>();
	private ArrayList<String> comandos_reward = new ArrayList<>();
	private ItemStack item_reward = null;
	
	public void setName(String var) {this.name = var;}
	
	public void setTipo(Material mat) {this.tipo = mat;}
	
	public void setQuantia(int var) {this.quantia = var;}
	
	public void setChance(double var) {this.chance = var;}
	
	public void setFirework(boolean var) {this.firework = var;}
	
	public void setGlowing(boolean var) {this.glowing = var;}

	public void setMensagem(ArrayList<String> var) {this.mensagem = var;}
	
	public void setLore(ArrayList<String> var) {this.lore = var;}
	
	public void setComandosReward(ArrayList<String> var) {this.comandos_reward = var;}
	
	public void setItemRewards(ItemStack var) {this.item_reward = var;}
	
	
	
	public String getName() {return this.name.replaceAll("&", "§");}
	
	public Material getTipo() {return this.tipo;}
	
	public ArrayList<String> getMensagem() {return this.mensagem;}
	
	public int getQuantia() {return this.quantia;}
	
	public double getChance() {return this.chance;}
	
	public boolean getFirework() {return this.firework;}
	
	public boolean getGlowing() {return this.glowing;}
	
	public ArrayList<String> getLore() {return this.lore;}
	
	public ArrayList<String> getComandosReward() {return this.comandos_reward;}
	
	public ItemStack getItemRewards() {return this.item_reward;}
	
	
	public ItemStack getIcone() {
		if (item_reward != null) return item_reward;
		
		ItemStack item = new ItemStack(this.tipo, this.quantia);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(this.name);
		im.setLore(lore);
		
		if (glowing) {
			im.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
			im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		
		item.setItemMeta(im);
		
		return item;
	}
}
