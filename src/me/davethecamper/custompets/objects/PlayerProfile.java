package me.davethecamper.custompets.objects;

import me.davethecamper.custompets.Main;
import me.davethecamper.custompets.PetsManager;
import me.davethecamper.custompets.Utils;
import me.davethecamper.custompets.eggs.OvoRaridade;
import me.davethecamper.custompets.eggs.OvosManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

public class PlayerProfile {
	
	public PlayerProfile(UUID uuid) {
		this.uuid = uuid;
		
		load();
	}
	

	private int fragmentos = 0;
	
	private UUID uuid;
	
	private String actived_pet = "null";
	
	private Set<String> pets = new HashSet<>();
	private Set<Incubadora> incubadoras = new HashSet<>();
	
	private HashMap<String, HashMap<ItemStack, Integer>> inventarios = new HashMap<>();
	
	private HashMap<String, Integer> total_size = new HashMap<>();
	private HashMap<String, Integer> ovos = new HashMap<>();
	

    
    public UUID getUniqueId() {return this.uuid;}
    
    public HashMap<String, Integer> getOvos() {return this.ovos;}
    
    public Incubadora getAvaibleIncubadora() {
    	for (Incubadora i : incubadoras) {
    		if (!i.isEmUso()) {
    			return i;
    		}
    	}
    	return null;
    }
    
    public Set<Incubadora> getAllIncubadoras() {return this.incubadoras;}
    
    public Set<String> getPets() {return this.pets;}
    
    public String getActivePet() {return this.actived_pet;}
    
    public Incubadora getIncubadoraById(long id) {
    	for (Incubadora i : incubadoras) {
    		if (i.getIdentificador() == id) {
    			return i;
    		}
    	}
    	return null;
    }
    
    public int getFragmentos() {return this.fragmentos;}
    
    public int getItensSizePet(String pet) {
    	return total_size.get(pet) != null ? total_size.get(pet) : 0;
    }
    
    
    
    public void removeFragmentos(int i) {this.fragmentos -= i;}
    
    public void summonPet(String pet) {
		OvoRaridade or = OvosManager.getPetRaridade(pet);
		if (getPets().contains(pet)) {
			if (Utils.isTier(Bukkit.getPlayer(uuid), or.getTier())) {
				if (this.actived_pet.equals(pet)) {
					this.actived_pet = "null";
				} else {
					this.actived_pet = pet;
				}//Aviso do pet
				PetsManager.removeSummonedPet(Bukkit.getPlayer(uuid));
			} else {
				Bukkit.getPlayer(this.uuid).sendMessage("§cSem tier suficiente");
			}
		}
    }
    
    
    public boolean canPickUp(String pet, Item item) {
    	//if (this.pets.contains(pet)) {
        	int tam_max = PetsManager.getCustomPet(pet).getSlots()*64;
    		if (total_size.get(pet) == null || total_size.get(pet) < tam_max) {
    			return item.getTicksLived() > item.getPickupDelay();
    		}
    	//}
    	return false;
    }
    
    public void addPet(String pet) {
    	if (this.pets.contains(pet)) {
    		OvoRaridade or = OvosManager.getPetRaridade(pet);
    		this.fragmentos += or.getFragmentos();
    	} else {
    		this.pets.add(pet);
    	}
    }
    
    public void addItem(String pet, Item item) {
    	int sobra = addItem(pet, item.getItemStack());
    	
    	if (sobra > 0) {
    		item.getItemStack().setAmount(sobra);
    	} else {
    		item.remove();
    	}
    }
    
    public int addItem(String pet, ItemStack item) {
    	ItemStack clone = item.clone();
    	clone.setAmount(1);
    	
    	HashMap<ItemStack, Integer> map = new HashMap<>();
    	
    	int tam_max = PetsManager.getCustomPet(pet).getSlots()*64;
    	int tam_atual = total_size.get(pet) != null ? total_size.get(pet) : 0;
    	int stack = item.getAmount();
    	int stack_atual = 0;
    	
    	if (inventarios.get(pet) != null) {
    		map = inventarios.get(pet);
    		stack_atual = inventarios.get(pet).get(clone) != null ? inventarios.get(pet).get(clone) : 0;
    	}
    	
    	if (tam_max < tam_atual + stack) {
    		stack = tam_max - tam_atual;
    	}
    	
    	map.put(clone, stack_atual + stack);
    	
    	inventarios.put(pet, map);
    	total_size.put(pet, tam_atual + stack);
    	
    	return item.getAmount() - stack;
    }
    
    public void colectInventory(String pet) {
    	if (inventarios.get(pet) != null) {
    		for (ItemStack item : new ArrayList<>(inventarios.get(pet).keySet())) {
    			item.setAmount(inventarios.get(pet).get(item));
    			Utils.giveItem(Bukkit.getPlayer(this.uuid), item);
    		}
    	}
    	total_size.remove(pet);
    	inventarios.remove(pet);
    }
    
    public void addOvo(String ovo) {
    	int quantia = 1;
    	
    	if (ovos.get(ovo) != null) {
    		quantia += ovos.get(ovo);
    	}
    	
    	ovos.put(ovo, quantia);
    }
    
    public boolean haveOvo(String ovo) {
    	return ovos.get(ovo) != null && ovos.get(ovo) > 0;
    }
    
    public void removeOvo(String ovo) {
    	int quantia = ovos.get(ovo);
    	if (quantia-1 <= 0) {
    		ovos.remove(ovo);
    	} else {
    		ovos.put(ovo, quantia-1);
    	}
    }
    
    
    
    public void load() {
    	File f = new File(Main.main.getDataFolder().getAbsolutePath() + "/players/" + this.uuid + ".yml");
    	if (f.exists()) {
    		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
    		
    		if (fc.get("pets") != null) {
	    		for (String pet : fc.getConfigurationSection("pets").getKeys(false)) {
	    			this.pets.add(pet);
	    			if (fc.get("pets." + pet + ".inventario") != null) {
		    			for (String id : fc.getConfigurationSection("pets." + pet + ".inventario").getKeys(false)) {
		    				ItemStack item = fc.getItemStack("pets." + pet + ".inventario." + id + ".item");
		    				int quantia = fc.getInt("pets." + pet + ".inventario." + id + ".quantia");
		    				item.setAmount(quantia);
		    				addItem(pet, item);
		    			}
	    			}
	    		}
    		}
    		
    		this.fragmentos = fc.getInt("fragmentos");
    		
    		for (String identificador : fc.getConfigurationSection("incubadoras").getKeys(false)) {
    			incubadoras.add(new Incubadora(fc, identificador));
    		}
    		
    		if (fc.get("ovos") != null) {
	    		for (String ovo : fc.getConfigurationSection("ovos").getKeys(false)) {
	        		ovos.put(ovo, fc.getInt("ovos." + ovo));
	        	}
    		}
    	} else {
    		createNew();
    	}
    }
    
    private void createNew() {
    	incubadoras.add(new Incubadora(IncubadoraType.DEFAULT));
    }
    
    
    public void save() {
    	File f = new File(Main.main.getDataFolder().getAbsolutePath() + "/players/" + this.uuid + ".yml");
    	if (!f.getParentFile().exists()) f.getParentFile().mkdirs();
    	
    	FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
    	
    	fc.set("pets", null);
    	for (String pet : pets) {
    		fc.set("pets." + pet + ".unlocked", true);
    		if (inventarios.get(pet) != null) {
    			int id = 0;
        		for (ItemStack item : inventarios.get(pet).keySet()) {
            		fc.set("pets." + pet + ".inventario." + id + ".item", item);
            		fc.set("pets." + pet + ".inventario." + id + ".quantia", inventarios.get(pet).get(item));
            		id++;
        		}
    		}
    	}
    	
    	fc.set("activated", actived_pet);
    	fc.set("fragmentos", fragmentos);
    	
    	fc.set("ovos", null);
    	for (String ovo : ovos.keySet()) {
    		fc.set("ovos." + ovo, ovos.get(ovo));
    	}
    	
    	fc.set("incubadoras", null);
    	for (Incubadora i : incubadoras) {
    		i.save(fc);
    	}
    	
    	
    	try {
			fc.save(f);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    }

}
