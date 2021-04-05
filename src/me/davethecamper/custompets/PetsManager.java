package me.davethecamper.custompets;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import me.davethecamper.custompets.eggs.OvoRaridade;
import me.davethecamper.custompets.eggs.OvosManager;
import me.davethecamper.custompets.entities.custom.CustomPetDefault;
import me.davethecamper.custompets.entities.custom.SummonedPet;
import me.davethecamper.custompets.entities.custom.bone.Bone;
import me.davethecamper.custompets.objects.Incubadora;
import me.davethecamper.custompets.objects.PlayerProfile;

public class PetsManager {

	public final static String TITLE_MAIN = "§2Pets";
	public final static String TITLE_PETS = "§2Pets adquiridos";
	
	private static HashMap<String, CustomPetDefault> custom_pets = new HashMap<>();
	
	public static HashMap<String, Set<SummonedPet>> summoned_by_type = new HashMap<>();
	public static HashMap<Player, SummonedPet> summoned_by_owner = new HashMap<>();
	
	public static Set<SummonedPet> summoned_pets = new HashSet<>();
	
	private static Set<LivingEntity> pets_entities = new HashSet<>();
	
	public static CustomPetDefault getCustomPet(String s) {return custom_pets.get(s);}
	
	public static PlayerProfile getPlayerProfile(Player p) {return Main.getPlayerProfile(p.getUniqueId());}
	
	public static PlayerProfile getPlayerProfile(UUID uuid) {return Main.getPlayerProfile(uuid);}
	
	
	
	public static String getRegisteredMobs() {
		String msg = "";
		for (String s : custom_pets.keySet()) {
			msg += s + ", ";
		}
		msg.substring(0, msg.length()-2);
		return msg;
	}
	
	
	public static Set<SummonedPet> getSummonedPetsByType(String type) {return summoned_by_type.get(type) != null ? summoned_by_type.get(type) : new HashSet<>();}
	
	public static SummonedPet getSummonedPetByOwner(Player p) {return summoned_by_owner.get(p);}
	
	
	public static boolean isPet(Entity e) {return pets_entities.contains(e);}

	public static void addSummonedPet(SummonedPet smp) {
		summoned_pets.add(smp);
		pets_entities.add(smp.getVolante());
		summoned_by_owner.put(smp.getOwner(), smp);
		
		if (summoned_by_type.get(smp.getName()) == null) {
			summoned_by_type.put(smp.getName(), new HashSet<>(Arrays.asList(smp)));
		} else {
			summoned_by_type.get(smp.getName()).add(smp);
		}
	}
	
	public static void removeSummonedPet(SummonedPet smp) {
		summoned_pets.remove(smp);
		summoned_by_type.get(smp.getName()).remove(smp);
	}

	public static void removeSummonedPet(Player p) {
		SummonedPet smp = getSummonedPetByOwner(p);
		
		if (smp != null) smp.remove();
		
		if (!Main.getPlayerProfile(p.getUniqueId()).getActivePet().equals("null")) {
			if (Utils.isLoaded(p.getLocation())) {
				getCustomPet(Main.getPlayerProfile(p.getUniqueId()).getActivePet()).summon(p);
			} else {
				new BukkitRunnable(){public void run() {
		            	if (p.isValid()) {
			            	removeSummonedPet(p);
		            	}
		        }}.runTaskLater(Main.main, 5);
			}
		}
	}
	
	public static void reloadCommand() {
		custom_pets.clear();
		init();
	}
	
	
	
	public static void init() {
		File f = new File(Main.main.getDataFolder().getAbsolutePath() + "/custom/");
		
		if (f.exists()) {
			File files[] = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				custom_pets.put(files[i].getName().replaceAll(".yml", ""), new CustomPetDefault(files[i]));
			}
		} else {
			f.mkdirs();
			Main.main.saveResource("custom" + File.separator + "Gorila.yml", true);
			
			init();
			return;
		}
		
		runnable();
	}
	
	public static Inventory getMainInventory() {
		Inventory inv = Bukkit.createInventory(null, 27, TITLE_MAIN);

		inv.setItem(11, Utils.criarItemMenus(Material.CREEPER_HEAD, "§bPets", Utils.formatText("Veja e ative os pets que você tem", "§7")));
		inv.setItem(13, Utils.criarItemMenus(Material.SLIME_SPAWN_EGG, "§2Ovos", Utils.formatText("Confira os ovos que você tem para coloca-los na incubadora", "§7")));
		inv.setItem(15, Utils.criarItemMenus(Material.GLASS, "§fIncubadoras", Utils.formatText("Aqui ficam as incubadoras onde os ovos precisam ficar para serem chocados", "§7")));
		
		return inv;
	}
	
	public static Inventory getPetsInventory(Player p) {
		Inventory inv = Bukkit.createInventory(null, 27, TITLE_PETS);
		PlayerProfile pp = Main.getPlayerProfile(p.getUniqueId());
		DecimalFormat f = new DecimalFormat("###");
		int slot = 0;
		ArrayList<String> lista = new ArrayList<>(pp.getPets());
		Collections.sort(lista);
		
		for (String pet : lista) {
			CustomPetDefault cpd = getCustomPet(pet);
			ItemStack item = cpd.getMembro("head").getMainItem().clone();
			ItemMeta im = item.getItemMeta();
			ArrayList<String> lore = new ArrayList<>();
			OvoRaridade or = OvosManager.getPetRaridade(pet);
			
			lore.add("§9Tier necessario: §b" + or.getTier());
			lore.add("");
			lore.add("§2Velocidade: §a" + f.format((cpd.getSpeed()/0.2)*100));
			lore.add("§2Inventário: §f" + pp.getItensSizePet(pet) + "§7/§f" + (cpd.getSlots()*64) + " §7§o(" + f.format(((((double)pp.getItensSizePet(pet)/(double)(cpd.getSlots()*64d)))*100d)) + "%)");
			if (cpd.getExtra() != null) {
				lore.add("");
				lore.add("§5Extra:");
				lore.addAll(Arrays.asList(Utils.formatText(cpd.getExtra(), "§d").split(",,")));
				lore.add("");
			}
			
			im.setDisplayName(or.getPrefixo() + cpd.getName() + (pp.getActivePet().equals(pet) ? " §7(Ativo)" : ""));
			im.setLore(lore);
			
			Utils.addPersistentData(im, "tier", PersistentDataType.INTEGER, or.getTier());
			Utils.addPersistentData(im, "pet", PersistentDataType.STRING, pet);
			
			item.setItemMeta(im);
			
			inv.setItem(slot, item);
			slot++;
		}
		
		if (slot == 0) {
			inv.setItem(0, Utils.criarItemMenus(Material.BARRIER, "§cSem pets!", Utils.formatText("Você não tem pets! Consiga-os abrindo seus ovos nas incubadoras", "§7")));
		}
		
		inv.setItem(inv.getSize()-9, Utils.criarItemMenus(Material.BOOK, "§6Informações uteis", Utils.formatText("Os pets usam o mesmo filtro do /filtro, eles pegam itens para você. Para pegar os itens de volta basta pressionar o botão 9 neste inventário com o mouse em cima do pet.", "§a")));
		
		inv.setItem(inv.getSize()-1, Utils.criarItemMenus(Material.PAPER, "§aVoltar"));
		
		return inv;
	}
	
	public static void disable() {
		for (SummonedPet smp : summoned_pets) {
			smp.remove();
		}
	}
	
	public static void runnable() {
		new BukkitRunnable(){
			int count = 0;
			int verify = 0;
			long delay = 0;
            public void run() {
            	count++;
            	verify++;
            	long inicial = System.currentTimeMillis();
            	for (SummonedPet smp : new HashSet<>(summoned_pets)) {
            		if (Utils.isLoaded(smp.getLocation()) && smp.getVolante().isValid() && smp.haveOwner()) {
            			if (smp.isMovingAnyPart()) {
            				smp.update();
            			}
            			
            			
            			for (Bone b : smp.getRegisteredBones()) {
            				if (smp.isMoving() || b.getTick() != 0) {
	            				b.increaseTick();
	            				if (Math.abs(b.getTick()) >= 20) {
	            					b.invertDirection();
	            				}
            				}
            			}
            			
            			smp.setOlderLocation(smp.getLocation());
            			smp.checkObjetive();
            			
                    	if (verify % 50 == 0) {
                    		verifyEfeitos(smp);
                    	}
            		} else {
            			if (smp.haveOwner() && smp.getOwner().isOnline()) {
            				removeSummonedPet(smp.getOwner());
            			} else {
                			smp.remove();
            			}
            		}
            	}
            	
            	if (count % 10 == 0) {
            		for (Player p : Bukkit.getOnlinePlayers()) {
            			if (p.getOpenInventory().getTitle().startsWith("§2Incubadora")) {
    						PlayerProfile pp = Main.getPlayerProfile(p.getUniqueId());
    	    				Incubadora i = pp.getIncubadoraById(Long.parseLong(p.getOpenInventory().getTitle().replaceAll("§2Incubadora ", "")));
    	    				i.updateCombustivel(p.getOpenInventory());
            			}
            		}
            	}
            	
            	delay += System.currentTimeMillis() - inicial;
            	

            	if (count % (2*10*30) == 0) {
            		Bukkit.getConsoleSender().sendMessage("§CTotal de delay nos pets: " + delay + "ms em 30 segundos");
            		delay = 0;
            	}
            }
		}.runTaskTimer(Main.main, 0, 2);
	}
	
	private static void verifyEfeitos(SummonedPet smp) {
		if (smp.haveOwner()) {
			switch (smp.getName()) {
				case "Tigre":
					smp.getOwner().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 7*20, 2));
					break;
			}
		}
	}

}
