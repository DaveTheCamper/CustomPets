package me.davethecamper.custompets.entities.custom;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import me.davethecamper.custompets.Events;
import me.davethecamper.custompets.Main;
import me.davethecamper.custompets.PetsManager;
import me.davethecamper.custompets.entities.custom.bone.BoneConfiguration;
import me.davethecamper.custompets.entities.custom.bone.BoneMoviment;

public class CustomPetDefault {
	
	public CustomPetDefault(File f) {
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		this.name = f.getName().replaceAll(".yml", "");
		
		if (fc.get("membros") != null) {
			int partes = fc.getConfigurationSection("membros").getKeys(false).size();
			
			for (String s : fc.getConfigurationSection("membros").getKeys(false)) {
				ArmorStandConfiguration ars = new ArmorStandConfiguration(fc, s, this.name);
				this.addMembro(ars);
			}
			
			Bukkit.getConsoleSender().sendMessage("§a[V] Carregado " + f.getName().replaceAll(".yml", "") + " em " + partes + " partes");
		}
		
		if (fc.get("extra") != null) this.extra = fc.getString("extra");
		
		if (fc.get("speed") != null) this.speed = fc.getDouble("speed");
		
		if (fc.get("slots") != null) this.slots = fc.getInt("slots");
		
		if (fc.get("bones") != null) {
			for (String bone : fc.getConfigurationSection("bones").getKeys(false)) {
				BoneMoviment bm = BoneMoviment.valueOf(fc.getString("bones." + bone + ".moviment"));
				ArmorStandConfiguration main = all.get(bone);
				Set<ArmorStandConfiguration> set = new HashSet<>();
				
				for (String sub_bone : fc.getStringList("bones." + bone + ".sub_bones")) {
					set.add(all.get(sub_bone));
				}
				bones.add(new BoneConfiguration(bm, main, set));
			}
		}
		
	}
	
	
	
	private String extra = null;
	private String name;
	
	private double speed = 0.2f;
	
	private int slots = 0;
	
	private Set<BoneConfiguration> bones = new HashSet<>();
	
	private ArmorStandConfiguration head;
	
	private HashMap<String, ArmorStandConfiguration> all = new HashMap<>();
	
	private HashMap<String, ArmorStandConfiguration> body = new HashMap<>();
	
	private HashMap<String, ArmorStandConfiguration> legs = new HashMap<>();
	
	private HashMap<String, ArmorStandConfiguration> tail = new HashMap<>();
	
	private HashMap<String, ArmorStandConfiguration> other = new HashMap<>();
	
	
	
	public String getName() {return this.name;}
	
	public String getExtra() {return this.extra;}
	
	public double getSpeed() {return this.speed;}
	
	public int getSlots() {return this.slots;}

	private ArmorStandConfiguration getHead() {return head;}

	private HashMap<String, ArmorStandConfiguration> getBody() {return body;}

	private HashMap<String, ArmorStandConfiguration> getLegs() {return legs;}

	private HashMap<String, ArmorStandConfiguration> getTail() {return tail;}

	private HashMap<String, ArmorStandConfiguration> getOther() {return other;}
	
	public ArmorStandConfiguration getMembro(String s) {return all.get(s);}
	
	public Set<ArmorStandConfiguration> getMembros() {
		Set<ArmorStandConfiguration> set = new HashSet<>();
		
		for (String s : all.keySet()) {set.add(all.get(s));}
		
		return set;
	}
	
	
	public void addMembro(ArmorStandConfiguration ars) {
		String s = ars.getMembroName();
		if (s.contains("head")) {
			this.head = ars;
		} else if (s.contains("body")) {
			body.put(s, ars);
		} else if (s.contains("legs") || s.contains("leg")) {
			legs.put(s, ars);
		} else if (s.contains("tail")) {
			tail.put(s, ars);
		} else {
			other.put(s, ars);
		}
		all.put(s, ars);
	}
	
	
	public LivingEntity summon(Player p) {
		return summon(p, true);
	}
	
	public LivingEntity summon(Player p, boolean ai) {
		return summon(p, p.getLocation(), ai);
	}
	
	public LivingEntity summon(Player p, Location l) {
		return summon(p, l, true);
	}
	
	public LivingEntity summon(Player p, Location l, boolean ai) {
		
		if (!ai) {
			l.setYaw(0);
		}
		
		Events.setAllowSpawn();
		Wolf w = l.getWorld().spawn(l, Wolf.class);

		w.setTamed(true);
		w.setInvisible(true);
		w.setInvulnerable(true);
		w.setSilent(true);
		w.setOwner(p);
		w.setAI(ai);
		
		SummonedPet smp = new SummonedPet(w, this.getName(), this.speed);
		
		
		
		smp.addMembro(getHead().summon(l, "head"), getHead());
		
		for (String s : getBody().keySet()) {
			ArmorStandConfiguration asc = getBody().get(s);
			smp.addMembro(asc.summon(l, "body"), asc);
		}
		
		for (String s : getLegs().keySet()) {
			ArmorStandConfiguration asc = getLegs().get(s);
			Location temp = l.clone();
			temp.setYaw(0f);
			smp.addMembro(asc.summon(temp, "legs"), asc);
		}
		
		for (String s : getTail().keySet()) {
			ArmorStandConfiguration asc = getTail().get(s);
			smp.addMembro(asc.summon(l, "tail"), asc);
		}

		for (String s : getOther().keySet()) {
			ArmorStandConfiguration asc = getOther().get(s);
			smp.addMembro(asc.summon(l, "other"), asc);
		}
		
		smp.setBones(new HashSet<>(bones));
		
		
		PetsManager.addSummonedPet(smp);
		
		return w;
	}
	
	public void saveAll() {
		File f = new File(Main.main.getDataFolder().getAbsolutePath() + "/custom/" + this.getName() + ".yml");
		FileConfiguration fc = YamlConfiguration.loadConfiguration(f);
		
		fc.set("speed", this.speed);
		
		for (String membro : all.keySet()) {
			ArmorStandConfiguration ars = all.get(membro);
			
			fc.set("membros." + membro + ".coordenadas.x", ars.getCoordsMain().getX());
			fc.set("membros." + membro + ".coordenadas.y", ars.getCoordsMain().getY());
			fc.set("membros." + membro + ".coordenadas.z", ars.getCoordsMain().getZ());
			
			fc.set("membros." + membro + ".coordenadas.headpose.x", ars.getCoordsHead().getX());
			fc.set("membros." + membro + ".coordenadas.headpose.y", ars.getCoordsHead().getY());
			fc.set("membros." + membro + ".coordenadas.headpose.z", ars.getCoordsHead().getZ());
			
			fc.set("membros." + membro + ".coordenadas.handpose.x", ars.getCoordsHand().getX());
			fc.set("membros." + membro + ".coordenadas.handpose.y", ars.getCoordsHand().getY());
			fc.set("membros." + membro + ".coordenadas.handpose.z", ars.getCoordsHand().getZ());

			fc.set("membros." + membro + ".visible", ars.isVisible());
			fc.set("membros." + membro + ".small", ars.isSmall());
			
			if (ars.getMainItem() != null) {
				fc.set("membros." + membro + ".head_item.default", ars.getItemMainAsString());
			}

			if (ars.getHandItem() != null) {
				fc.set("membros." + membro + ".hand_item.default", ars.getItemHandAsString());
			}
		}
		
		
		try {
			fc.save(f);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
