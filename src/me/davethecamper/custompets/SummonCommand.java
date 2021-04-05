package me.davethecamper.custompets;

import me.davethecamper.custompets.eggs.Ovo;
import me.davethecamper.custompets.eggs.OvosManager;
import me.davethecamper.custompets.entities.custom.CustomPetDefault;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SummonCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
        	if (args.length > 0) {
        		switch (args[0]) {
        			case "custom_teste":
    	    			if (sender.hasPermission("pets.admin")) {
    	    				customTest(sender, args);
    	    			}
        				break;
        				
        			case "convert":
    	    			if (sender.hasPermission("pets.admin")) {
	        				Utils.adaptConfiguration();
	        				sender.sendMessage("§aAdaptado");
    	    			}
        				break;

        			case "unlock":
    	    			if (sender.hasPermission("pets.admin")) {
    	    				for (Ovo o : OvosManager.getOvosRegistered()) {
    	    					for (String s : o.getPets()) {
    	    						Main.getPlayerProfile(((Player) sender).getUniqueId()).addPet(s);
    	    					}
    	    				}
    	    			}
    	    			break;
        				
        			case "edit":
    	    			if (sender.hasPermission("pets.admin")) {
    	    				editCommand(sender, args);
    	    			}
        				break;
        				
        			default:
                		defaultCommand(sender);
                		break;
        		}
        	} else {
        		defaultCommand(sender);
        	}
            
        }

		if (args.length > 0) {
    		switch (args[0]) {
	    		case "reload":
	    			if (sender.hasPermission("pets.admin")) {
	    				PetsManager.reloadCommand();
	    				sender.sendMessage("§aRecarregado");
	    			}
					break;
					
	    		case "add_ovo":
	    			if (sender.hasPermission("pets.admin")) {
		    			Player p = Bukkit.getPlayer(args[1]);
		    			int quantia = args.length >= 4 ? Integer.parseInt(args[3]) : 1;
		    			for (int i = 0; i < quantia; i++) {
			    			Main.getPlayerProfile(p.getUniqueId()).addOvo(args[2]);
		    			}
		    			sender.sendMessage("§aAdicionado ovo " + args[2] + " ao player " + args[1]);
	    			}
	    			break;
    		}
			
		}

        return true;
    }
    
    private void customTest(CommandSender sender, String[] args) {
    	Player p = (Player) sender;
    	CustomPetDefault cpd = PetsManager.getCustomPet(args[1]);
    	if (cpd != null) {
    		cpd.summon(p);
    	} else {
    		p.sendMessage("§cMob inválido, siga a lista");
    		p.sendMessage("§7" + PetsManager.getRegisteredMobs());
    	}
    }
    
    private void editCommand(CommandSender sender, String[] args) {
    	Player p = (Player) sender;
    	String pet = args[1];
    	
    	p.openInventory(EditorManager.getEditorInventory(pet, p));
    }
    
    private void defaultCommand(CommandSender sender) {
    	Player p = (Player) sender;
    	p.openInventory(PetsManager.getMainInventory());
    }
}
