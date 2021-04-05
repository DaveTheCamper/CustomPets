package me.davethecamper.custompets.eggs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.davethecamper.custompets.PetsManager;
import me.davethecamper.custompets.Utils;
import me.davethecamper.custompets.entities.custom.CustomPetDefault;
import me.davethecamper.custompets.objects.Caixa;
import me.davethecamper.custompets.objects.RecompensaChave;

public class Ovo {
	
	public Ovo(File f) {
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		
		for (String raridade : fc.getConfigurationSection("pets").getKeys(false)) {
			Set<String> set = new HashSet<>(fc.getStringList("pets." + raridade));
			OvoRaridade or = OvosManager.getRaridade(raridade);
			
			pets.put(or, set);
			OvosManager.setPetRaridade(set, or);
		}
		
		
		this.identificador = f.getName().replaceAll(".yml", "");
		gerarItem(Material.valueOf(fc.getString("material")), fc.getString("nome"));
		
		this.c = gerarCaixa();
	}
	
	
	private HashMap<OvoRaridade, Set<String>> pets = new HashMap<>();
	
	private ItemStack item;
	
	private String identificador;
	
	private Caixa c;
	
	
	public Set<String> getPets() {
		Set<String> set = new HashSet<>();
		for (OvoRaridade or : pets.keySet()) {
			set.addAll(pets.get(or));
		}
		return set;
	}
	
	public ItemStack getItem() {return this.item;}
	
	public Caixa getCaixa() {return c;}
	
	public String getIdentificador() {return this.identificador;}
	
	public String gerarPet() {
		double max_range = 0;
		ArrayList<OvoRaridade> raridades = new ArrayList<>(pets.keySet());
		
		Collections.shuffle(raridades);
		
		for (OvoRaridade or : raridades) {
			max_range += or.getChance();
		}
		
		Random r = new Random();
		double random = Utils.randomSomeDouble(max_range);
		double range = 0;
		
		for (OvoRaridade or : raridades) {
			if (or.getChance() + range >= random) {
				ArrayList<String> list = new ArrayList<>(pets.get(or));
				return list.get(r.nextInt(list.size()));
			}
			range += or.getChance();
		}
		
		return "null";
	}
	

	
	private void gerarItem(Material m, String name) {
		this.item = new ItemStack(m);
		ItemMeta im = this.item.getItemMeta();
		ArrayList<String> lore = new ArrayList<>();
		
		HashMap<OvoRaridade, Integer> map = new HashMap<>();
		
		for (OvoRaridade or : pets.keySet()) {
			map.put(or, or.getOrdem());
		}
		
		map = Utils.sortMenorMaior(map);
		
		lore.add("§7Pets possiveis:");
		
		for (OvoRaridade or : map.keySet()) {
			for (String pet : pets.get(or)) {
				lore.add(or.getPrefixo() + "➡ " + pet);
			}
		}
		
		im.setDisplayName(name);
		im.setLore(lore);
		
		Utils.addPersistentData(im, "ovo", PersistentDataType.STRING, this.identificador);
		
		this.item.setItemMeta(im);
	}
	
	private Caixa gerarCaixa() {
		Caixa c = new Caixa();
		
		ArrayList<RecompensaChave> recompensas = new ArrayList<>();
		
		for (OvoRaridade or : pets.keySet()) {
			for (String pet : pets.get(or)) {
				CustomPetDefault cpd = PetsManager.getCustomPet(pet);
				ItemStack item = Utils.changeItemName(cpd.getMembro("head").getMainItem(), or.getPrefixo() + cpd.getName());
				recompensas.add(new RecompensaChave(or.getChance(), item, item));
			}
		}
		
		c.setRecompensas(recompensas);
		c.setTitle(this.identificador);
		
		return c;
	}
}
