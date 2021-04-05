package me.davethecamper.custompets;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import me.davethecamper.custompets.eggs.OvosManager;
import me.davethecamper.custompets.objects.EntityHider;
import me.davethecamper.custompets.objects.PlayerProfile;

public class Main extends JavaPlugin {
	
    public static Main main;
    
    FileConfiguration config = getConfig();
    
    static HashMap<UUID, PlayerProfile> playerProfiles = new HashMap<>();
    
    static EntityHider entityHider;
    
    
    
    public static PlayerProfile getPlayerProfile(UUID uuid) {
    	if (playerProfiles.get(uuid) == null) {
            playerProfiles.put(uuid, new PlayerProfile(uuid));
    	}
    	return playerProfiles.get(uuid);
    }

    public static void setPlayerProfile(UUID uuid, PlayerProfile profile) {playerProfiles.put(uuid, profile);}

    public static void removePlayerProfile(UUID uuid) {
    	if (playerProfiles.get(uuid) != null) {
    		playerProfiles.get(uuid).save();
        	playerProfiles.remove(uuid);
    	}
    }
    
    
    
    
    @Override
    public void onEnable() {
        main = this;
        entityHider = new EntityHider(this, EntityHider.Policy.BLACKLIST);

        config.options().copyDefaults(true);
        saveConfig();

        this.getCommand("pof_summon").setExecutor(new SummonCommand());

        getServer().getPluginManager().registerEvents(new Events(), this);
        

        PetsManager.init();
        EditorManager.init();
        OvosManager.init();

        Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[PetsOnFire] Plugin is Enabled!");
    }

    @Override
    public void onDisable() {
    	for (UUID uuid : playerProfiles.keySet()) {
    		removePlayerProfile(uuid);
    	}
    	
    	PetsManager.disable();
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[PetsOnFire] Plugin is Disabled!");
    }
}
