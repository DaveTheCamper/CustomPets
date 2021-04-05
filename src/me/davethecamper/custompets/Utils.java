package me.davethecamper.custompets;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class Utils {
	
	public static boolean isMaterial(String s) {
		try {
			Material.valueOf(s.toUpperCase());
			return true;
		} catch (Exception e) {}
		return false;
	}
    
    public static boolean isDouble(String s) {
    	try {
    		Double.parseDouble(s.toUpperCase());
    		return true;
    	} catch (Exception e) {
    		return false;
    	}
    }
	
	public static ItemStack getSkullSkin(String value) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		String signature = "eWzMvykVci274As2htIEDijhoLOMR/yKrFT9+S+asGENt2KhIfR84fD1HPclWMYvSixlJPEsMnBUVichnQQnp8zSuBNlYD/Md/NbhsTkngmvRXfvplKokqmmVdmpY3y4OSZ3c1T3CxUtXSHnqhV250xlbL0j8hkaVk9+HDZUXuiXbTy+EB+LOtYpFwpoq28ARefAQ9OBD4n7iAETIZLwgK7hB4khIKHaJyY1DPo8H70zGI9DA9qiQC9We7RZuoRWmkoy/Ra8G4ugE5Wz0fWg4c0ASkWN8DWETrSROUYYicJXCfRLPvjfuXoMK7qDCcnSfxNVGVoT1OM6FjnQP9/H2H94962tYFM0ruHrhWaycTtVb33B/QtCyhGxnBu41cnab9166olVMSpU7yxdRK24L61XCUWVt6TfGCl+M9j3jgwfEoECEYFxQ26ErqdkGJU0t8S9LJJ+bBThBg6ILmlf9oel8iC7iz8CltWvw7io01BYIQ5XxPALfKfng6TZjRM1Fg/WZPjUutTic87a2RZb4ALTI32kIlCVMtGmCXn5QMve+RAtauTK3TcV68r3OXK4ryUwkfTUOCGQdTHpR5ygH6X1TEueDQAd48Swb4EGcT0J2PgbhvSv0teZYW3eGx86R9rcgvAYWnLJIIfIYIZYUHWqehSaPg+AERhH41lBOeI=";
		
		GameProfile gameProfile = new GameProfile(UUID.randomUUID(), null);
	    gameProfile.getProperties().put("textures", new Property("textures", value, signature));
		
	    SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
	    Field profileField = null;
	    try {
	        profileField = skullMeta.getClass().getDeclaredField("profile");
	        profileField.setAccessible(true);
	        profileField.set(skullMeta, gameProfile);
	    } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException exception) {
	        exception.printStackTrace();
	    }
	    
	    item.setItemMeta(skullMeta);
		
		return item;
	}
	
	public static <Z> void addPersistentData(PersistentDataHolder holder, String tag, PersistentDataType<?, Z> pdt, Z valor) {
    	holder.getPersistentDataContainer().set(new NamespacedKey(Main.main, tag), pdt, valor);
    }
    
    public static <Z> Z getPersistentValue(PersistentDataHolder holder, String tag, PersistentDataType<?, Z> pdt) {
    	return holder.getPersistentDataContainer().get(new NamespacedKey(Main.main, tag), pdt);
    }
    
    public static <Z> boolean hasPersistentValue(PersistentDataHolder holder, String tag, PersistentDataType<?, Z> pdt) {
    	return holder.getPersistentDataContainer().has(new NamespacedKey(Main.main, tag), pdt);
    }
    
    public static void removePersistentData(PersistentDataHolder holder, String tag) {
    	holder.getPersistentDataContainer().remove(new NamespacedKey(Main.main, tag));
    }
    
    public static boolean isLoaded(Location l) {
		return l.getWorld().isChunkLoaded(l.getBlockX()/16, l.getBlockZ()/16);	
	}
    
    public static ItemStack changeItemName(ItemStack item, String nome) {
    	ItemMeta im = item.getItemMeta();
    	im.setDisplayName(nome);
    	item.setItemMeta(im);
    	return item;
    }
    
    public static ItemStack criarItemMenus(Material mat, String nome, String linhas) {
    	return criarItemMenus(mat, 1, nome, linhas, false);
	}

    public static ItemStack criarItemMenus(Material mat, int i, String nome, String linhas) {
    	return criarItemMenus(mat, i, nome, linhas, false);
    }
    
    public static ItemStack criarItemMenus(Material mat, String nome, String linhas, boolean bool) {
    	return criarItemMenus(mat, 1, nome, linhas, bool);
    }
    
    public static ItemStack criarItemMenus(Material mat, String nome) {
		return criarItemMenus(mat, 1, nome, "", false);
	}
    
    public static ItemStack criarItemMenus(Material mat, int i, String nome) {
		return criarItemMenus(mat, i, nome, "", false);
	}

    public static ItemStack criarItemMenus(Material mat, int qtd, String nome, String linhas, Boolean glow) {
		ArrayList<String> lore = new ArrayList<>();
		ItemStack item = new ItemStack(mat, qtd);
		ItemMeta im = item.getItemMeta();
		im.setDisplayName(nome);
		im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		im.addItemFlags(ItemFlag.HIDE_DESTROYS);
		im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
		if (linhas.length() > 1) {
			String[] linha = linhas.split(",,");
			for (String l : linha) {
				lore.add(l);
			}
			im.setLore(lore);
		}
        if (glow) {
            im.addEnchant(Enchantment.DURABILITY, 1, false);
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
		item.setItemMeta(im);
		return item;
	}
    
    public static void giveItem(Player p, ItemStack item) {
        if (Utils.espacoInv((Inventory)p.getInventory(), item) >= item.getAmount()) {
            p.getInventory().addItem(new ItemStack[]{item});
        } else {
            p.getWorld().dropItem(p.getLocation(), item);
        }
    }

    public static int espacoInv(Inventory inv, ItemStack item) {
        int quantia = 0;
        int slots = 0;
        slots = inv.getType().equals(InventoryType.PLAYER) ? 35 : inv.getSize() - 1;
        for (int i = 0; i <= slots; ++i) {
            int stack;
            int max;
            if (inv.getItem(i) == null) {
                quantia += item.getMaxStackSize();
                continue;
            }
            if (item == null || inv.getItem(i).getType() != item.getType() || (max = item.getMaxStackSize()) == (stack = inv.getItem(i).getAmount())) continue;
            int adicionar = max - stack;
            quantia += adicionar;
        }
        return quantia;
    }
    
    public static void adaptConfiguration() {
		File f = new File(Main.main.getDataFolder().getAbsolutePath() + "/custom/");
    	File files[] = f.listFiles();
		for (int i = 0; i < files.length; i++) {
			FileConfiguration fc = YamlConfiguration.loadConfiguration(files[i]);
			
			if (fc.get("config") != null) {
				for (String parte : fc.getConfigurationSection("config").getKeys(false)) {
					String partes[] = fc.getString("config." + parte).split(" ");
					
					
					for (int ii = 0; ii < partes.length; ii++) {
						String verify[] = partes[ii].split(":");
						switch (verify[0]) {
							case "x":
								fc.set("membros." + parte + ".coordenadas." + verify[0], Double.parseDouble(verify[1])); 
								break;
								
							case "y":
								fc.set("membros." + parte + ".coordenadas." + verify[0], Double.parseDouble(verify[1])); 
								break;
								
							case "z":
								fc.set("membros." + parte + ".coordenadas." + verify[0], Double.parseDouble(verify[1])); 
								break;
								
							case "headpose":
								{
									String split[] = verify[1].split(";");
									fc.set("membros." + parte + ".coordenadas.headpose.x", Double.parseDouble(split[0]));
									fc.set("membros." + parte + ".coordenadas.headpose.y", Double.parseDouble(split[1]));
									fc.set("membros." + parte + ".coordenadas.headpose.z", Double.parseDouble(split[2]));
								}
								break;
								
							case "handpose":
								{
									String split[] = verify[1].split(";");
									fc.set("membros." + parte + ".coordenadas.handpose.x", Double.parseDouble(split[0]));
									fc.set("membros." + parte + ".coordenadas.handpose.y", Double.parseDouble(split[1]));
									fc.set("membros." + parte + ".coordenadas.handpose.z", Double.parseDouble(split[2]));
								}
								break;
								
							case "visible":
								fc.set("membros." + parte + ".visible", Boolean.valueOf(verify[1]));
								break;
								
							case "mini":
								fc.set("membros." + parte + ".small", Boolean.valueOf(verify[1]));
								break;
								
							case "handmaterial":
							case "handskin":
								fc.set("membros." + parte + ".hand_item.default", verify[1]);
								break;
								
							case "skin":
							case "material":
								fc.set("membros." + parte + ".head_item.default", verify[1]);
								break;
						}
					}
				}
				fc.set("config", null);
				
				try {
					fc.save(files[i]);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    }
    
    public static int getBurnTime(Material m) {
    	net.minecraft.server.v1_16_R3.ItemStack nms = CraftItemStack.asNMSCopy(new ItemStack(m));
    	Bukkit.broadcastMessage(nms.getTag() + "");
        return nms.getTag().getShort("BurnTime");
    }
    
    public static String getSkinOnline(String url_reader) throws Exception {
        URL url = new URL(url_reader);
        BufferedReader stream = new BufferedReader(new InputStreamReader(
                url.openStream()));
        StringBuilder entirePage = new StringBuilder();
        String inputLine;
        while ((inputLine = stream.readLine()) != null)
            entirePage.append(inputLine);
        stream.close();
        if(!(entirePage.toString().contains("\"material\":\"")))
            return null;
        return entirePage.toString().split("\"material\":\"")[1].split("\"}")[0];
    }
    
    public static boolean isTier0(Player p) {
    	return isTier(p, 0);
    }
    
    public static boolean isTier(Player p, int tier) {
    	for (int i = 0; i <= tier; i++) {
        	if (p.hasPermission("sou.tier" + i)) {
        		return true;
        	}
    	}
    	return false;
    }
    
    public static String formatText(String msg, String cor, int per_linha) {
        String[] partes = msg.split(" ");
        String nova_string = "";
        String temp_string = "";
        for (String str : partes) {
            if (temp_string.length() > per_linha) {
                nova_string = String.valueOf(cor) + nova_string + temp_string + ",," + cor;
                temp_string = str;
                continue;
            }
            temp_string = temp_string.length() > 0 ? String.valueOf(temp_string) + " " + str : str;
        }
        if (temp_string.length() > 0) {
            nova_string = String.valueOf(nova_string) + cor + temp_string;
        }
        return nova_string;
    }

    public static String formatText(String msg, String cor) {return formatText(msg, cor, 25);}
    
    public static double randomSomeDouble(double d) {
		Random r = new Random();
		double floor = Math.floor(d);
		int a = floor > 0 ? r.nextInt((int) floor) : 0;
		double b = (d-floor > 0 ? r.nextDouble() % (d-floor) : 0) + (floor > 0 ? r.nextDouble() : 0);
		
		return a+b;
	}
    
    public static <Z> HashMap<Z, Integer> sortMenorMaior(HashMap<Z, Integer> hm)  { 
        List<Map.Entry<Z, Integer> > list = new LinkedList<Map.Entry<Z, Integer>>(hm.entrySet()); 
  
        Collections.sort(list, new Comparator<Map.Entry<Z, Integer>>() { 
            public int compare(Map.Entry<Z, Integer> o1,  
                               Map.Entry<Z, Integer> o2) { 
                return (o1.getValue()).compareTo(o2.getValue()); 
            } 
        }); 
          
        HashMap<Z, Integer> temp = new LinkedHashMap<Z, Integer>(); 
        for (Map.Entry<Z, Integer> aa : list) { 
            temp.put(aa.getKey(), aa.getValue()); 
        } 
        return temp; 
    } 
    
    public static <Z> HashMap<Z, Double> sortMenorMaiorDouble(HashMap<Z, Double> hm)  { 
        List<Map.Entry<Z, Double> > list = new LinkedList<Map.Entry<Z, Double>>(hm.entrySet()); 
  
        Collections.sort(list, new Comparator<Map.Entry<Z, Double>>() { 
            public int compare(Map.Entry<Z, Double> o1,  
                               Map.Entry<Z, Double> o2) { 
                return (o1.getValue()).compareTo(o2.getValue()); 
            } 
        }); 
          
        HashMap<Z, Double> temp = new LinkedHashMap<Z, Double>(); 
        for (Map.Entry<Z, Double> aa : list) { 
            temp.put(aa.getKey(), aa.getValue()); 
        } 
        return temp; 
    } 

}
