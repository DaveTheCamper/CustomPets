package me.davethecamper.custompets.entities.custom;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;

import me.davethecamper.custompets.Main;
import me.davethecamper.custompets.PetsManager;
import me.davethecamper.custompets.Utils;
import me.davethecamper.custompets.entities.custom.bone.Bone;
import me.davethecamper.custompets.entities.custom.bone.BoneConfiguration;
import me.davethecamper.custompets.objects.PlayerProfile;
import me.davethecamper.itemfilter.ItemFilterAPI;
import net.minecraft.server.v1_16_R3.EntityCreature;
import net.minecraft.server.v1_16_R3.EntityInsentient;
import net.minecraft.server.v1_16_R3.EntityTameableAnimal;
import net.minecraft.server.v1_16_R3.GenericAttributes;
import net.minecraft.server.v1_16_R3.PathfinderGoal;
import net.minecraft.server.v1_16_R3.PathfinderGoalFloat;
import net.minecraft.server.v1_16_R3.PathfinderGoalFollowOwner;
import net.minecraft.server.v1_16_R3.PathfinderGoalMoveTowardsTarget;
import net.minecraft.server.v1_16_R3.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_16_R3.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_16_R3.PathfinderGoalSelector;

public class SummonedPet {
	
	public SummonedPet(Wolf w, String name, double speed) {
		this.volante = w;
		this.speed = speed;
		this.name = name;
		this.spawned = System.currentTimeMillis();
		
		changeWolfMind(w);
	}
	
	private String name;
	
	private double speed;
	
	private long tempo_indo;
	private long spawned;

	private Wolf volante;
	
	private Item objetive;
	
	private HashMap<ArmorStand, ArmorStandConfiguration> membros = new HashMap<>();
	private HashMap<String, ArmorStand> membros_by_name = new HashMap<>();
	
	private HashMap<ArmorStandConfiguration, Bone> bones = new HashMap<>();
	
	private Set<Bone> bones_as_list = new HashSet<>();
	
	private Location older;

	
	
	
	public void setOlderLocation(Location older) {this.older = older;}
	
	
	public void addMembro(ArmorStand ars, ArmorStandConfiguration asc) {
		membros.put(ars, asc);
		membros_by_name.put(asc.getMembroName(), ars);
	}
	
	public void setBones(Set<BoneConfiguration> bones) {
		for (BoneConfiguration bc : bones) {
			Bone b = new Bone(this, bc);
			for (ArmorStandConfiguration arc : bc.getSubBones()) {
				this.bones.put(arc, b);
			}
			this.bones.put(bc.getMainBone(), b);
			bones_as_list.add(b);
		}
	} 
	
	
	public String getName() {return this.name;}
	
	public ArmorStand getMembro(String membro) {return membros_by_name.get(membro);}
	
	public Wolf getVolante() {return this.volante;}
	
	public Set<ArmorStand> getMembros() {return new HashSet<>(membros.keySet());}
	
	public ArmorStandConfiguration getConfiguration(ArmorStand ars) {return membros.get(ars);}
	
	public Location getLocation() {return this.volante.getLocation();}
	
	public Location getOlderLocation() {return older != null ? this.older : getLocation();}
	
	public double getSpeed() {return this.speed;}
	
	public Bone getBone(ArmorStandConfiguration asc) {return this.bones.get(asc);}
	
	public Set<Bone> getRegisteredBones() {return this.bones_as_list;}
	
	public Player getOwner() {return (Player) getVolante().getOwner();}
	

	
	public boolean haveOwner() {return getVolante().getOwner() != null && getVolante().getOwner() instanceof Player;}
	
	public boolean isMoving() {return this.getLocation().getX() != this.getOlderLocation().getX() || this.getLocation().getZ() != this.getOlderLocation().getZ();}
	
	public boolean isMovingAnyPart() {
		Location l = this.getLocation().subtract(this.getOlderLocation());
		return l.getYaw() > 0 || l.getPitch() > 0 || l.getX() > 0 || l.getY() > 0 || l.getZ() > 0;
	}
	
	public boolean isBone(ArmorStandConfiguration asc) {return this.bones.containsKey(asc);}
	
	public void update() {
		for (ArmorStand ars : this.getMembros()) {
			this.getConfiguration(ars).update(this, ars);
		}
	}
	
	public void respawn(Location l) {
		Player p = this.getOwner();
		PetsManager.removeSummonedPet(p);
	}
	
	public void remove() {
		PetsManager.removeSummonedPet(this);
		volante.remove();
		
		for (ArmorStand ars : new ArrayList<>(getMembros())) {
			ars.remove();
		}
	}
	
	
	private boolean respawn = false;
	
	public void checkObjetive() {
		if (!this.haveOwner()) return;
		
		if (!this.getOwner().getLocation().getWorld().equals(getVolante().getWorld()) || this.getOwner().getLocation().distance(this.getVolante().getLocation()) >= 25) {
			respawn(this.getOwner().getLocation());
			return;
		}
		
		if (this.spawned+(1000*5) > System.currentTimeMillis()) return;
		
		if (objetive == null) {
			Set<Item> set = getNearbyItens();
			if (set.size() > 0) {
				unregisterGoals(getVolante());
				respawn = true;
				for (Item i : getNearbyItens()) {
					if (canRearchObjetive(getVolante(), i.getLocation())) {
						setObjetive(getVolante(), i.getLocation());
						objetive = i;
						tempo_indo = System.currentTimeMillis();
						break;
					}
				}
			}
		}
		
		if (objetive != null) {
			if (objetive.getLocation().getWorld().equals(getVolante().getWorld()) && objetive.isValid()) {
				
				double distance = objetive.getLocation().distance(getVolante().getLocation());
				if (distance <= 2) {
					pickUp(objetive);
					objetive = null;
					checkObjetive();
				} else if (tempo_indo + (long) ((1000d*5d)/((double)this.getSpeed()/0.2d)) < System.currentTimeMillis()) {
					objetive = null;
					checkObjetive();
				}
			} else {
				objetive = null;
			}
		} else if (respawn == true) {
			respawn(this.getLocation());
		}
	}
	
	private void pickUp(Item item) {
		PlayerProfile pp = Main.getPlayerProfile(this.getOwner().getUniqueId());
		pp.addItem(this.getName(), item);
	}
	
	private Set<Item> getNearbyItens() {
		PlayerProfile pp = Main.getPlayerProfile(this.getOwner().getUniqueId());
		HashMap<Item, Double> itens = new HashMap<>();
		
		for (Entity e : getVolante().getNearbyEntities(20, 20, 20)) {
			if (e instanceof Item) {
				Item i = (Item) e;
				
				if (i.isValid() && pp.canPickUp(this.getName(), i) && ItemFilterAPI.canPickup(i.getItemStack().getType(), getOwner())) {
					itens.put((Item) e, getVolante().getLocation().distance(e.getLocation()));
				}
			}
		}
		
		return Utils.sortMenorMaiorDouble(itens).keySet();
	}
	
	
	private boolean canRearchObjetive(LivingEntity e, Location l) {
		EntityCreature c = (EntityCreature) ((EntityInsentient) ((CraftEntity)e).getHandle());
	    
		return c.getNavigation().a(l.getX(), l.getY(), l.getZ(), e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).getBaseValue());
	}
	
	private void setObjetive(LivingEntity e, Location l) {

		Bukkit.getScheduler().runTask(Main.main, () -> {
			EntityCreature c = (EntityCreature) ((EntityInsentient) ((CraftEntity)e).getHandle());
		    
		    try {
		    	unregisterGoals(c);

				c.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(1000d);

				c.getNavigation().a(l.getX(), l.getY(), l.getZ(), 1.2);
				
	            c.goalSelector.a(0, new PathfinderGoalFloat(c));
	            c.goalSelector.a(5, new PathfinderGoalMoveTowardsTarget(c, 1D, 0.3f));
		    } catch (Exception exc) {exc.printStackTrace();}
		});
	}
	
	private void changeWolfMind(LivingEntity e) {

		Bukkit.getScheduler().runTask(Main.main, () -> {
		    EntityCreature c = (EntityCreature) ((EntityInsentient)((CraftEntity)e).getHandle());
		   
		    try {
		    	
		    	unregisterGoals(c);

	            c.goalSelector.a(0, new PathfinderGoalFloat(c));
	            c.goalSelector.a(1, new PathfinderGoalFollowOwner((EntityTameableAnimal) c, 1.0D, 10.0F, 2.0F, true));
	            c.goalSelector.a(5, new PathfinderGoalMoveTowardsTarget(c, 1D, 0.3f));
	            c.goalSelector.a(7, new PathfinderGoalRandomStroll(c, 1.0D));
	            c.goalSelector.a(8, new PathfinderGoalRandomLookaround(c));
	            
		    	e.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(speed);
				
		    } catch (Exception exc) {exc.printStackTrace();}
		});
	}
	
	private void unregisterGoals(LivingEntity e) {
		try {
			unregisterGoals((EntityCreature) ((EntityInsentient)((CraftEntity)e).getHandle()));
		} catch (Exception exp) {}
	}
	
	private void unregisterGoals(EntityCreature c) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        PathfinderGoalSelector goalSelector = c.goalSelector;
        PathfinderGoalSelector targetSelector = c.targetSelector;
        
		Field dField;
        dField = PathfinderGoalSelector.class.getDeclaredField("d");
        dField.setAccessible(true);
        dField.set(goalSelector, new LinkedHashSet<>());
        dField.set(targetSelector, new LinkedHashSet<>());
        
        Field cField;
        cField = PathfinderGoalSelector.class.getDeclaredField("c");
        cField.setAccessible(true);
        dField.set(goalSelector, new LinkedHashSet<>());
        cField.set(targetSelector, new EnumMap<>(PathfinderGoal.Type.class));
       
        Field fField;
        fField = PathfinderGoalSelector.class.getDeclaredField("f");
        fField.setAccessible(true);
        dField.set(goalSelector, new LinkedHashSet<>());
        fField.set(targetSelector, EnumSet.noneOf(PathfinderGoal.Type.class));
	}
	
}
