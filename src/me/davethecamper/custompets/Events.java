package me.davethecamper.custompets;


import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import me.davethecamper.custompets.eggs.Ovo;
import me.davethecamper.custompets.eggs.OvoRaridade;
import me.davethecamper.custompets.eggs.OvosManager;
import me.davethecamper.custompets.entities.custom.CustomPetDefault;
import me.davethecamper.custompets.objects.Fuel;
import me.davethecamper.custompets.objects.Incubadora;
import me.davethecamper.custompets.objects.PlayerProfile;
import me.davethecamper.custompets.objects.RecompensaChave;
import me.davethecamper.custompets.objects.RollReward;
import me.davethecamper.cash.CashManager;

public class Events implements Listener {

    @EventHandler
    public void onPlayerDisconnected(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Main.removePlayerProfile(player.getUniqueId());
    }
    
    @EventHandler
    public void onTargetEvent(EntityTargetEvent e) {
    	if (PetsManager.isPet(e.getEntity()) || PetsManager.isPet(e.getTarget())) {
    		e.setCancelled(true);
    	}
    }
    
    
    public static void setAllowSpawn() {can_spawn = true;}
    private static boolean can_spawn = false;
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawn(CreatureSpawnEvent e) {
    	if (can_spawn) {
    		can_spawn = false;
    		e.setCancelled(false);
    	}
    }
    
    @EventHandler
    public void onClick(InventoryClickEvent e) {
    	if (e.getView().getTitle() != null) {
    		switch (e.getView().getTitle()) {
	    		case PetsManager.TITLE_MAIN:
	    			e.setCancelled(true);
	    			
	    			if (e.getClickedInventory() != null && e.getClickedInventory().equals(e.getView().getTopInventory())) {
	    				switch (e.getSlot()) {
		    				case 11:
		    					e.getWhoClicked().openInventory(PetsManager.getPetsInventory((Player) e.getWhoClicked()));
		    					break;
		    				case 13:
		    					e.getWhoClicked().openInventory(OvosManager.getOvosInventory((Player) e.getWhoClicked()));
		    					break;
		    				case 15:
		    					e.getWhoClicked().openInventory(OvosManager.getIncubadorasInventory((Player) e.getWhoClicked()));
		    					break;
		    			}
	    			}
	    			break;
	    			
	    		case "§5Roleta de pets":
	    			e.setCancelled(true);
	    			break;
	    			
	    		case OvosManager.TITLE_OVOS:
	    			e.setCancelled(true);
	    			
	    			if (e.getClickedInventory() != null && e.getClickedInventory().equals(e.getView().getTopInventory()) && e.getCurrentItem() != null) {
	    				switch (e.getSlot()) {
		    				case 18:
		    					e.getWhoClicked().openInventory(OvosManager.getBuyEggsInventory(Main.getPlayerProfile(e.getWhoClicked().getUniqueId())));
		    					break;
		    				case 26:
		    					e.getWhoClicked().openInventory(PetsManager.getMainInventory());
		    					break;
	    					default:
	    						PlayerProfile pp = Main.getPlayerProfile(e.getWhoClicked().getUniqueId());
	    	    				Incubadora i = pp.getAvaibleIncubadora();
	    	    				if (Utils.hasPersistentValue(e.getCurrentItem().getItemMeta(), "ovo", PersistentDataType.STRING)) {
	    	    					Ovo o = OvosManager.getOvo(Utils.getPersistentValue(e.getCurrentItem().getItemMeta(), "ovo", PersistentDataType.STRING));
		    	    				if (i != null && pp.haveOvo(o.getIdentificador())) {
			    	    				i.incubarOvo(o);
			    	    				pp.removeOvo(o.getIdentificador());
			    	    				e.getWhoClicked().openInventory(i.getInventory());
		    	    				} else {
		    	    					e.getWhoClicked().sendMessage("§cSem incubadoras disponiveis");
		    	    				}
	    	    				}
	    	    				
	    						break;
	    				}
	    			}
	    			break;
	    			
	    		case OvosManager.TITLE_INCUBADORAS:
	    			e.setCancelled(true);
	    			if (e.getClickedInventory() != null && e.getClickedInventory().equals(e.getView().getTopInventory()) && e.getCurrentItem() != null) {
	    				switch (e.getSlot()) {
		    				case 26:
		    					e.getWhoClicked().openInventory(PetsManager.getMainInventory());
		    					break;
	    					default:
	    						PlayerProfile pp = Main.getPlayerProfile(e.getWhoClicked().getUniqueId());
	    	    				Incubadora i = pp.getIncubadoraById(Utils.getPersistentValue(e.getCurrentItem().getItemMeta(), "id", PersistentDataType.LONG));
	    	    				if (i.isEmUso()) {
	    	    					e.getWhoClicked().openInventory(i.getInventory());
	    	    				}
	    						break;
	    				}
	    			}
	    			
	    			break;
	    			
	    		case PetsManager.TITLE_PETS:
	    			e.setCancelled(true);
	    			if (e.getClickedInventory() != null && e.getClickedInventory().equals(e.getView().getTopInventory()) && e.getCurrentItem() != null) {
	    				switch (e.getSlot()) {
		    				case 26:
		    					e.getWhoClicked().openInventory(PetsManager.getMainInventory());
		    					break;
	    					default:
	    						PlayerProfile pp = Main.getPlayerProfile(e.getWhoClicked().getUniqueId());
	    						if (e.getCurrentItem() != null && Utils.hasPersistentValue(e.getCurrentItem().getItemMeta(), "pet", PersistentDataType.STRING)) {
									String pet = Utils.getPersistentValue(e.getCurrentItem().getItemMeta(), "pet", PersistentDataType.STRING);
	    							switch (e.getHotbarButton()) {
	    								case 8:
	    									pp.colectInventory(pet);
	    									e.getWhoClicked().openInventory(PetsManager.getPetsInventory((Player) e.getWhoClicked()));
	    									break;
	    									
	    								default:
	    									pp.summonPet(pet);
	    									break;
	    							}
	    						}
	    						break;
	    				}
	    			}
	    			break;
	    			
	    		case OvosManager.TITLE_OVOS_COMPRAR:
	    			e.setCancelled(true);
	    			if (e.getClickedInventory() != null && e.getClickedInventory().equals(e.getView().getTopInventory()) && e.getCurrentItem() != null) {
	    				Player p = (Player) e.getWhoClicked();
	    				PlayerProfile pp = Main.getPlayerProfile(p.getUniqueId());
	    				int custo = 0;
	    				int quantia = 0;
		    			switch (e.getSlot()) {
			    			case 15:
			    				custo = 6;
			    				quantia += 10;
			    			case 13:
			    				custo += 3;
			    				quantia += 4;
			    			case 11:
			    				custo += 1;
			    				quantia += 1;
			    				if (e.getHotbarButton() == 8) {
				    				if (CashManager.haveCash(p.getUniqueId(), custo)) {
				    					CashManager.removeCash(p.getUniqueId(), custo);
				    					for (int i = 0; i < quantia; i++) {
					    					pp.addOvo("OvoDaSelva");
				    					}
				    					p.sendMessage("§aOvos adicionados com sucesso a sua conta!");
				    				} else {
				    					p.sendMessage("§cCash insuficiente, consiga-o comprando na nossa loja virtual /site");
				    				}
				    				e.getWhoClicked().openInventory(OvosManager.getOvosInventory(p));
			    				}
			    				break;
			    				
			    			case 24:
			    				custo = 30;
			    				quantia += 10;
			    			case 22:
			    				custo += 12;
			    				quantia += 4;
			    			case 20:
			    				custo += 3;
			    				quantia += 1;
			    				if (e.getHotbarButton() == 8) {
				    				if (pp.getFragmentos() >= custo) {
				    					pp.removeFragmentos(custo);
				    					for (int i = 0; i < quantia; i++) {
					    					pp.addOvo("OvoDaSelva");
				    					}
				    					p.sendMessage("§aOvos adicionados com sucesso a sua conta!");
				    				} else {
				    					p.sendMessage("§cFragmentos insuficientes, consiga-os abrindo ovos e ganhando pets repetidos");
				    				}
				    				e.getWhoClicked().openInventory(OvosManager.getOvosInventory(p));
			    				}
			    				break;
			    				
			    				
			    			case 35:
			    				e.getWhoClicked().openInventory(OvosManager.getOvosInventory(p));
			    				break;
		    			}
	    			}
	    			break;
	    			
    			default:
    				if (e.getView().getTitle().startsWith("§2Incubadora")) {
    					e.setCancelled(true);
    	    			if (e.getClickedInventory() != null && e.getCurrentItem() != null) {
    	    				if (e.getClickedInventory().equals(e.getView().getBottomInventory())) {
	    						PlayerProfile pp = Main.getPlayerProfile(e.getWhoClicked().getUniqueId());
	    	    				Incubadora i = pp.getIncubadoraById(Long.parseLong(e.getView().getTitle().replaceAll("§2Incubadora ", "")));
    	    					
	    	    				if (!i.isReady()) {
	    	    					ItemStack item = e.getCurrentItem();
    	    						long burn_time = Fuel.valueOfMaterial(item.getType()).getBurnTime();
	    	    					if (item != null && burn_time > 0) {
	    	    						long stack = item.getAmount();
	    	    						long max_burn = i.getMaxCombustivelColocavel();
	    	    						
	    	    						if (burn_time*stack > max_burn) {
	    	    							stack = max_burn/burn_time + (max_burn%burn_time > 0 ? 1 : 0);
	    	    						}
	    	    						
	    	    						if (burn_time*stack > 0) {
    	    								i.addCombustivel(stack * burn_time);
    	    								item.setAmount((int) (item.getAmount() - stack));
    	    							}
	    	    					}
	    	    				}
     	    				} else {
	    						PlayerProfile pp = Main.getPlayerProfile(e.getWhoClicked().getUniqueId());
	    	    				Incubadora i = pp.getIncubadoraById(Long.parseLong(e.getView().getTitle().replaceAll("§2Incubadora ", "")));
    	    					Player p = (Player) e.getWhoClicked();
     	    					switch (e.getSlot()) {
	     	    					case 31:
	    	    	    				if (i.isReady()) {
	    	    	    					String pet = i.getPet();
	    	    	    					OvoRaridade or = OvosManager.getPetRaridade(pet);
	    	    	    					CustomPetDefault cpd = PetsManager.getCustomPet(pet);
	    	    	    					ItemStack item = Utils.changeItemName(cpd.getMembro("head").getMainItem().clone(), or.getPrefixo() + cpd.getName());
	    	    	    					RollReward.preRoll(p, i.getOvo().getCaixa(), false, 1, new RecompensaChave(or.getChance(), item, item));
	    	    	    					
	    	    	    					boolean ja_tem = pp.getPets().contains(pet);
	    	    	    					pp.addPet(pet);
	    	    	    					
	    	    	    					avisarOqGanhou(p, "§aVocê desbloqueou o pet " + or.getPrefixo() + pet + "!!!", true, or.getFragmentos() == 100);
	    	    	    					if (ja_tem) avisarOqGanhou(p, "§aComo você já possui este pet, você recebeu §b" + or.getFragmentos() + " §afragmentos", false, false);
	    	    	    					
	    	    	    					i.reset((Player)e.getWhoClicked());
	    	    	    				}
	     	    						break;
	     	    						
	     	    					case 36:
	     	    						int quantia = i.getCustoAcelerar();
	     	    						if (e.getHotbarButton() == 8 && CashManager.haveCash(p.getUniqueId(), quantia)) {
	     	    							CashManager.removeCash(p.getUniqueId(), quantia);
	     	    							i.setCompleted();
	     	    							p.sendMessage("§aProgresso acelerado com sucesso!");
	     	    							p.openInventory(i.getInventory());
	     	    						}
	     	    						break;
	     	    						
	     	    					case 44:
	     	    						e.getWhoClicked().openInventory(OvosManager.getIncubadorasInventory((Player) e.getWhoClicked()));
	     	    						break;
     	    					}
     	    				}
    	    			}
    				}
    				break;
    		}
    	}
    }
    
    private static void avisarOqGanhou(Player p, String message, boolean sound, boolean rarissimo) {
    	new BukkitRunnable() {
            public void run() {
            	if (p.isValid()) {
            		if (sound) p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_CELEBRATE, 3f, 1f);
            		p.sendMessage(message);
            		if (rarissimo) {
            			Bukkit.broadcastMessage("");
            			Bukkit.broadcastMessage("§eO jogador §6" + p.getName() + " §econsegui um pet §6lendário!!!!");
            			Bukkit.broadcastMessage("");
            			
            			for (Player other : Bukkit.getOnlinePlayers()) {
            				other.playSound(other.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 3.0f, 1.2f);
            			}
            		}
            	}
            }
    	}.runTaskLater(Main.main, 55*2);
    }

}
