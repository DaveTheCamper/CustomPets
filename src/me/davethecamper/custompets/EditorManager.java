package me.davethecamper.custompets;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import me.davethecamper.custompets.entities.custom.ArmorStandConfiguration;
import me.davethecamper.custompets.entities.custom.CustomPetDefault;
import me.davethecamper.custompets.entities.custom.SummonedPet;
import me.davethecamper.custompets.objects.Coords;

public class EditorManager implements Listener {
	
	public final static String TITLE_DEFAULT = "§bEditar pets";
	public final static String TITLE_EDIT = "§bEditar pet-membro";

	private static Set<UUID> editing_hand = new HashSet<>();
	private static HashMap<UUID, String> edit_via_chat = new HashMap<>();
	private static HashMap<UUID, String> editing = new HashMap<>();
	private static HashMap<UUID, Double> editing_value = new HashMap<>();
	private static HashMap<UUID, ArmorStandConfiguration> editing_parte = new HashMap<>();
	
	private static HashMap<UUID, Long> cooldown = new HashMap<>();
	
	
	
	public static void removeEditing(Player p) {
		saveChanges(editing.get(p.getUniqueId()));
		
		editing.remove(p.getUniqueId());
		editing_parte.remove(p.getUniqueId());
		editing_value.remove(p.getUniqueId());
		
		PetsManager.getSummonedPetByOwner(p).remove();
	}
	
	public static boolean isEditingHand(Player p) {return editing_hand.contains(p.getUniqueId());}
	
	public static boolean isEditing(Player p) {return editing.containsKey(p.getUniqueId());}
	
	public static String getParteEditing(Player p) {return editing.get(p.getUniqueId());}
	
	
	public static void init() {
		runnable();
		Bukkit.getServer().getPluginManager().registerEvents(new EditorManager(), Main.main);
	}
	
	
	
	public static Inventory getEditorInventory(String pet, Player p) {
		Inventory inv = Bukkit.createInventory(null, 54, TITLE_DEFAULT);
		
		
		CustomPetDefault cpd = PetsManager.getCustomPet(pet);
		
		int slot = 0;
		for (ArmorStandConfiguration arc : cpd.getMembros()) {
			ItemStack item = arc.getHandItem() != null ? arc.getHandItem().clone(): arc.getMainItem().clone();
			
			ItemMeta im = item.getItemMeta();
			im.setDisplayName(arc.getMembroName());
			Utils.addPersistentData(im, "membro", PersistentDataType.STRING, arc.getMembroName());
			item.setItemMeta(im);
			
			inv.setItem(slot, item);
			slot++;
		}
		
		inv.setItem(slot, Utils.criarItemMenus(Material.TORCH, "§aAdicionar novo membro"));
		inv.setItem(53, Utils.criarItemMenus(Material.BARRIER, "§cFim de edição"));
		
		if (editing.get(p.getUniqueId()) == null) {
			editing.put(p.getUniqueId(), pet);
			editing_value.put(p.getUniqueId(), 0.05);
			cpd.summon(p, false);
		}
		
		return inv;
	}
	
	public static Inventory getEditorMembroInventory(Player p, String membro) {
		CustomPetDefault cpd = PetsManager.getCustomPet(editing.get(p.getUniqueId()));
		ArmorStandConfiguration asc = cpd.getMembro(membro);
		Inventory inv = Bukkit.createInventory(null, 36, TITLE_EDIT);
		editing_parte.put(p.getUniqueId(), asc);
		editing_hand.remove(p.getUniqueId());
		
		
		inv.setItem(10, asc.getMainItem() != null ? asc.getMainItem() : Utils.criarItemMenus(Material.BARRIER, "§cSem cabeça"));
		
		inv.setItem(12, asc.getHandItem() != null ? asc.getHandItem() : Utils.criarItemMenus(Material.BARRIER, "§cSem item na mão"));
		
		inv.setItem(5, Utils.criarItemMenus(Material.ARMOR_STAND, asc.isSmall() ? "§aPequeno" : "§2Grande", "§7§oClick para alterar"));
		
		inv.setItem(23, Utils.criarItemMenus(Material.ARMOR_STAND, asc.isVisible() ? "§aVisivel" : "§2Invisivel", "§7§oClick para alterar"));

		inv.setItem(16, Utils.criarItemMenus(Material.WOODEN_AXE, "§aEditar coordenadas"));
		inv.setItem(17, Utils.criarItemMenus(Material.IRON_AXE, "§aEditar coordenadas mão"));
		
		
		inv.setItem(inv.getSize()-1, Utils.criarItemMenus(Material.BARRIER, "§cFim de edição"));
		return inv;
	}
	
	public static void runnable() {
		new BukkitRunnable(){
            public void run() {
            	for (UUID uuid : editing_parte.keySet()) {
            		
            		if (!Bukkit.getOfflinePlayer(uuid).isOnline()) continue;
            		
            		Player p = Bukkit.getPlayer(uuid);
            		SummonedPet smp = PetsManager.getSummonedPetByOwner(p);
            		ArmorStandConfiguration asc = editing_parte.get(uuid);
            		Location l = asc.update(smp, smp.getMembro(asc.getMembroName())).clone();
            		
            		l = l.add(0, asc.isSmall() ? 1 : 1.75, 0);
            		
            		efeitosQuadrado(Particle.FLAME, p, l, 0.5);
            	}
            }
		}.runTaskTimer(Main.main, 0, 5);
	}
	
	public static void efeitosQuadrado(Particle p, Player player, Location l, double size) {
		double add = size;
		
		for (double x = l.getX() - add*3; x < l.getX() + add*3; x+=add) {
			player.spawnParticle(p, new Location(l.getWorld(), x, l.getY(), l.getZ()), 1, 0, 0, 0, 0);
		}

		for (double y = l.getY() - add*3; y < l.getY() + add*3; y+=add) {
			player.spawnParticle(p, new Location(l.getWorld(), l.getX(), y, l.getZ()), 1, 0, 0, 0, 0);
		}

		for (double z = l.getZ() - add*3; z < l.getZ() + add*3; z+=add) {
			player.spawnParticle(p, new Location(l.getWorld(), l.getX(), l.getY(), z), 1, 0, 0, 0, 0);
		}
		
		/*for (double x = l.getX() - add; x <= l.getX() + add; x+=add) {
			for (double z = l.getZ() - add; z <= l.getZ() + add; z+=add) {
				for (double y = (l.getY()+1) - add; y <= (l.getY()+1)  + add; y+=add) {
					boolean x_b = (x == l.getX() - add || x == l.getX() + add);
					boolean y_b = (y == (l.getY()+1) - add || y == (l.getY()+1) + add);
					boolean z_b = (z == l.getZ() - add || z == l.getZ() + add);
					if (((x_b || y_b) && z_b) || ((z_b || y_b) && x_b) || ((x_b || z_b) && y_b)) {
						player.spawnParticle(p, new Location(l.getWorld(), x, y, z), 1, 0, 0, 0, 0);
					}
				}
			}
		}*/
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getWhoClicked() instanceof Player) {
			Player p = (Player) e.getWhoClicked();
			
			if (!isEditing(p)) return;
			
			switch (e.getView().getTitle()) {
				case TITLE_DEFAULT:
					e.setCancelled(true);
					if (e.getCurrentItem() == null || !e.getCurrentItem().hasItemMeta() || e.getClickedInventory() == null || e.getClickedInventory().equals(e.getView().getBottomInventory())) return;
					
					if (Utils.hasPersistentValue(e.getCurrentItem().getItemMeta(), "membro", PersistentDataType.STRING)) {
						String data = Utils.getPersistentValue(e.getCurrentItem().getItemMeta(), "membro", PersistentDataType.STRING);
						
						p.openInventory(getEditorMembroInventory(p, data));
					} else {
						switch (e.getCurrentItem().getType()) {
							case BARRIER:
								p.closeInventory();
								removeEditing(p);
								p.getInventory().clear();
								break;
							case TORCH:
								edit_via_chat.put(p.getUniqueId(), "add_membro");
								p.closeInventory();
								p.sendMessage("§aEscreva o nome do membro no chat");
								break;
								
								default:break;
						}
					}
					break;
					
				case TITLE_EDIT:
					e.setCancelled(true);
					if (e.getCurrentItem() == null || e.getClickedInventory() == null || e.getClickedInventory().equals(e.getView().getBottomInventory())) return;
					
					ArmorStandConfiguration asc = editing_parte.get(p.getUniqueId());
					
					switch (e.getSlot()) {
						case 10:
							edit_via_chat.put(p.getUniqueId(), "set_skull");
							p.closeInventory();
							p.sendMessage("§aEscreva o link no chat");
							break;
							
						case 12:
							edit_via_chat.put(p.getUniqueId(), "set_hand");
							p.closeInventory();
							p.sendMessage("§aEscreva o link no chat");
							break;
							
						case 16:
							setEditingMembro(p, false);
							break;
							
						case 17:
							setEditingMembro(p, true);
							break;
							
							
						case 5:
							asc.setSmall(!asc.isSmall());
							respawnPet(p);
							p.openInventory(getEditorMembroInventory(p, asc.getMembroName()));
							break;
							
						case 23:
							asc.setVisible(!asc.isVisible());
							respawnPet(p);
							p.openInventory(getEditorMembroInventory(p, asc.getMembroName()));
							break;
							
						case 35:
							p.openInventory(getEditorInventory(editing.get(p.getUniqueId()), p));
							break;
					}
					
					break;
			}
		}
	}
	
	private static void setEditingMembro(Player p, boolean b) {
		ArmorStandConfiguration asc = editing_parte.get(p.getUniqueId());
		
		if (b) {
			editing_hand.add(p.getUniqueId());
		}
		
		adjustItens(p, asc);
		
		p.closeInventory();
	}
	
	private static void adjustItens(Player p, ArmorStandConfiguration asc) {
		for (int i = 3; i < 8; i++) {p.getInventory().setItem(i, null);}
		
		DecimalFormat f = new DecimalFormat("0.00");
		Coords c = isEditingHand(p) ? asc.getCoordsHand() : asc.getCoordsMain();
		
		p.getInventory().setItem(0, Utils.criarItemMenus(Material.DIAMOND_SHOVEL, "§cx_main: " + f.format(c.getX())));
		p.getInventory().setItem(1, Utils.criarItemMenus(Material.IRON_SHOVEL, "§ay_main: " + f.format(c.getY())));
		p.getInventory().setItem(2, Utils.criarItemMenus(Material.GOLDEN_SHOVEL, "§9z_main: " + f.format(c.getZ())));
		
		p.getInventory().setItem(3, Utils.criarItemMenus(Material.DIAMOND_AXE, "§cx_head: " + f.format(asc.getCoordsHead().getX())));
		p.getInventory().setItem(4, Utils.criarItemMenus(Material.IRON_AXE, "§ay_head: " + f.format(asc.getCoordsHead().getY())));
		p.getInventory().setItem(5, Utils.criarItemMenus(Material.GOLDEN_AXE, "§9z_head: " + f.format(asc.getCoordsHead().getZ())));
		
		p.getInventory().setItem(8, Utils.criarItemMenus(Material.BARRIER, "§cFim"));
		
		updateMob(asc);
	}
	
	private static void updateMob(ArmorStandConfiguration asc) {
		asc.updateAllPetsByEditor();
	}
	
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		switch (e.getAction()) {
			case RIGHT_CLICK_AIR:
			case LEFT_CLICK_AIR:
				Player p = e.getPlayer();
				
				if (!isEditing(p) || edit_via_chat.containsKey(p.getUniqueId())) return;
				e.setCancelled(true);
				
				if (e.getItem() == null || (p.getInventory().getItemInOffHand() != null && e.getItem().equals(p.getInventory().getItemInOffHand()))) return;
				
				boolean sub = e.getAction().equals(Action.RIGHT_CLICK_AIR);
				double value = editing_value.get(p.getUniqueId());
				
				if (cooldown.get(p.getUniqueId()) == null || cooldown.get(p.getUniqueId()) < System.currentTimeMillis()) {
					ArmorStandConfiguration asc = editing_parte.get(p.getUniqueId());
					Coords c = isEditingHand(p) ? asc.getCoordsHand() : asc.getCoordsMain();
					switch (p.getInventory().getHeldItemSlot()) {
						case 0:
							c.addX(sub ? -value : value);
							adjustItens(p, asc);
							break;
						case 1:
							c.addY(sub ? -value : value);
							adjustItens(p, asc);
							break;
						case 2:
							c.addZ(sub ? -value : value);
							adjustItens(p, asc);
							break;
							
						case 3:
							asc.getCoordsHead().addX(sub ? -value : value);
							adjustItens(p, asc);
							break;
						case 4:
							asc.getCoordsHead().addY(sub ? -value : value);
							adjustItens(p, asc);
							break;
						case 5:
							asc.getCoordsHead().addZ(sub ? -value : value);
							adjustItens(p, asc);
							break;
							
						case 8:
							p.openInventory(getEditorMembroInventory(p, editing_parte.get(p.getUniqueId()).getMembroName()));
							break;
					}
					
					
					
					cooldown.put(p.getUniqueId(), System.currentTimeMillis() + 200);
				}
				
				break;
			default:
				break;
		}
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		
		
		
		if (!isEditing(p)) return;
		
		e.setCancelled(true);
		
		
		if (edit_via_chat.containsKey(p.getUniqueId())) {
			String message = e.getMessage().toLowerCase();
			String edit = edit_via_chat.get(p.getUniqueId());
			
			switch (edit) {
				case "add_membro": 
					{
						CustomPetDefault cpd = PetsManager.getCustomPet(editing.get(p.getUniqueId()));
						
						ArmorStandConfiguration asc = new ArmorStandConfiguration(message, editing.get(p.getUniqueId()));
						cpd.addMembro(asc);
						edit_via_chat.remove(p.getUniqueId());
						
						Bukkit.getScheduler().runTask(Main.main, () -> {
							respawnPet(p);
							p.openInventory(getEditorMembroInventory(p, asc.getMembroName()));
						});
					}
					break;
					
				case "set_skull":
					{
						String head = "";
						
						try {head = Utils.getSkinOnline(e.getMessage());} catch (Exception e1) {}
						
						ArmorStandConfiguration asc = editing_parte.get(p.getUniqueId());
						
						asc.setHeadItem(head);
						edit_via_chat.remove(p.getUniqueId());
						
						Bukkit.getScheduler().runTask(Main.main, () -> {
							respawnPet(p);
							p.openInventory(getEditorMembroInventory(p, asc.getMembroName()));
						});
					}
					break;
					
				case "set_hand":
					{
						String head = "";
						try {head = Utils.getSkinOnline(e.getMessage());} catch (Exception e1) {}
						
						ArmorStandConfiguration asc = editing_parte.get(p.getUniqueId());
						
						asc.setHandItem(head);
						edit_via_chat.remove(p.getUniqueId());
						
						Bukkit.getScheduler().runTask(Main.main, () -> {
							respawnPet(p);
							p.openInventory(getEditorMembroInventory(p, asc.getMembroName()));
						});
					}
					break;
			}
			
			
			
		} else {
			if (Utils.isDouble(e.getMessage())) {
				double d = Double.parseDouble(e.getMessage());
				editing_value.put(p.getUniqueId(), d);
				p.sendMessage("§aValor ajustado " + e.getMessage());
			} else {
				p.sendMessage("§cValor inválido " + e.getMessage());
			}
		}
		
	}
	
	private static void respawnPet(Player p) {
		CustomPetDefault cpd = PetsManager.getCustomPet(editing.get(p.getUniqueId()));
		Location l = PetsManager.getSummonedPetByOwner(p).getLocation();
		PetsManager.getSummonedPetByOwner(p).remove();
		cpd.summon(p, l, false);
	}
	
	
	private static void saveChanges(String pet) {
		CustomPetDefault cpd = PetsManager.getCustomPet(pet);
		
		cpd.saveAll();
	}
	
}
