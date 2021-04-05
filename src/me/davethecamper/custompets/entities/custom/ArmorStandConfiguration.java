package me.davethecamper.custompets.entities.custom;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import me.davethecamper.custompets.PetsManager;
import me.davethecamper.custompets.Utils;
import me.davethecamper.custompets.objects.Coords;

public class ArmorStandConfiguration {
	
	public ArmorStandConfiguration(String name, String owner) {
		double x = 0;
		double y = 0;
		double z = 0;
		this.coords_main = new Coords(x, y, z);
		
		double x_headp = 0;
		double y_headp = 0;
		double z_headp = 0;
		this.coords_head = new Coords(x_headp, y_headp, z_headp);
		
		double x_handp = 0;
		double y_handp = 0;
		double z_handp = 0;
		this.coords_hand = new Coords(x_handp, y_handp, z_handp);
		
		this.visible = false;
		this.small = false;
		
		String item = "STONE";
		this.item_main = new ItemStack(Material.valueOf(item.toUpperCase()));
		this.item_main_string = item;
	
		this.membro_name = name;
		this.corpo_name = owner;
	}
	
	
	public ArmorStandConfiguration(FileConfiguration fc, String name, String owner) {
		double x = fc.get("membros." + name + ".coordenadas.x") != null ? fc.getDouble("membros." + name + ".coordenadas.x") : 0;
		double y = fc.get("membros." + name + ".coordenadas.y") != null ? fc.getDouble("membros." + name + ".coordenadas.y") : 0;
		double z = fc.get("membros." + name + ".coordenadas.z") != null ? fc.getDouble("membros." + name + ".coordenadas.z") : 0;
		this.coords_main = new Coords(x, y, z);
		
		double x_headp = fc.get("membros." + name + ".coordenadas.headpose.x") != null ? fc.getDouble("membros." + name + ".coordenadas.headpose.x") : 0;
		double y_headp = fc.get("membros." + name + ".coordenadas.headpose.y") != null ? fc.getDouble("membros." + name + ".coordenadas.headpose.y") : 0;
		double z_headp = fc.get("membros." + name + ".coordenadas.headpose.z") != null ? fc.getDouble("membros." + name + ".coordenadas.headpose.z") : 0;
		this.coords_head = new Coords(x_headp, y_headp, z_headp);
		
		double x_handp = fc.get("membros." + name + ".coordenadas.handpose.x") != null ? fc.getDouble("membros." + name + ".coordenadas.handpose.x") : 0;
		double y_handp = fc.get("membros." + name + ".coordenadas.handpose.y") != null ? fc.getDouble("membros." + name + ".coordenadas.handpose.y") : 0;
		double z_handp = fc.get("membros." + name + ".coordenadas.handpose.z") != null ? fc.getDouble("membros." + name + ".coordenadas.handpose.z") : 0;
		this.coords_hand = new Coords(x_handp, y_handp, z_handp);
		
		this.visible = fc.get("membros." + name + ".visible") != null ? fc.getBoolean("membros." + name + ".visible") : false;
		this.small = fc.get("membros." + name + ".small") != null ? fc.getBoolean("membros." + name + ".small") : false;
		
		if (fc.get("membros." + name + ".hand_item.default") != null) {
			String item = fc.getString("membros." + name + ".hand_item.default");
			setHandItem(item);
		}
		
		if (fc.get("membros." + name + ".head_item.default") != null) {
			String item = fc.getString("membros." + name + ".head_item.default");
			setHeadItem(item);
		}
		
		this.membro_name = name;
		this.corpo_name = owner;
	}
	

	private String corpo_name;
	private String membro_name;
	private String item_main_string;
	private String item_hand_string;
	
	private Coords coords_main;
	private Coords coords_head;
	private Coords coords_hand;
	
	private boolean visible = false;
	private boolean small = false;
	
	
	private ItemStack item_main;
	private ItemStack item_hand;
	

	public Coords getCoordsMain() {return coords_main;}

	public Coords getCoordsHead() {return coords_head;}

	public Coords getCoordsHand() {return coords_hand;}

	public boolean isVisible() {return visible;}

	public boolean isSmall() {return small;}

	public ItemStack getMainItem() {return item_main;}
	
	public ItemStack getHandItem() {return item_hand;}
	
	public String getMembroName() {return membro_name;}
	
	public String getCorpoName() {return corpo_name;}

	public String getItemHandAsString() {return item_hand_string;}
	
	public String getItemMainAsString() {return item_main_string;}
	


	public void setVisible(boolean b) {visible = b;}

	public void setSmall(boolean b) {small = b;}
	
	
	public void setHeadItem(String item) {
		if (item != null && item.length() > 1) {
			if (Utils.isMaterial(item)) {
				this.item_main = new ItemStack(Material.valueOf(item.toUpperCase()));
			} else {
				this.item_main = Utils.getSkullSkin(item);
			}
			this.item_main_string = item;
		} else {
			this.item_main_string = null;
			this.item_main = null;
		}
	}
	
	public void setHandItem(String item) {
		if (item != null && item.length() > 1) {
			if (Utils.isMaterial(item)) {
				this.item_hand = new ItemStack(Material.valueOf(item.toUpperCase()));
			} else {
				this.item_hand = Utils.getSkullSkin(item);
			}
			this.item_hand_string = item;
		} else {
			this.item_hand_string = null;
			this.item_hand = null;
		}
	}
	
	
	public ArmorStand summon(Location l, String type) {
		ArmorStand ars = l.getWorld().spawn(l.clone().add(this.getCoordsMain().getAsVector()), ArmorStand.class);
		
		if (this.getCoordsHead() != null) ars.setHeadPose(this.getCoordsHead().getAsEulerAngle());
		
		if (this.getCoordsHand() != null) ars.setRightArmPose(this.getCoordsHand().getAsEulerAngle());
		
		if (this.getMainItem() != null) ars.getEquipment().setHelmet(this.getMainItem());
		
		if (this.getHandItem() != null) ars.getEquipment().setItemInMainHand(this.getHandItem());
		
		ars.setMarker(true);
		ars.setVisible(this.isVisible());
		ars.setSmall(this.isSmall());
		ars.setGravity(false);
		
		setNoClipArmorStand(ars);
		
		Utils.addPersistentData(ars, "type", PersistentDataType.STRING, type);
		
		return ars;
	}
	
	
	public Location update(SummonedPet smp, ArmorStand ars) {
		Location l = smp.getLocation();
		Location temp = l.clone();
		
		float yaw = l.getYaw();
		
		double init_x = coords_main.getX();
		double init_z = coords_main.getZ();
		double x = 0, z = 0, y = coords_main.getY();
		
		double raio = Math.sqrt(Math.pow(init_x, 2) + Math.pow(init_z, 2));
		
		float angulo_relativo = init_x != 0 || init_z != 0 ? (float) getAnguloRelativo(init_x, raio, init_z, 0) : 0;
		float angulo = (float) ((yaw+angulo_relativo)/360 * (Math.PI*2));
		
		x = (double) (raio * Math.cos(angulo));
		z = (double) (raio * Math.sin(angulo));
		
		
		// Head ter difirença no angulo yaw, pois tem rotação com a cabeça do cachorro enquanto o resto do corpo não
		
		temp.setY(temp.getY() + y);
		temp.setX(temp.getX() + x);
		temp.setZ(temp.getZ() + z);
		
		ars.teleport(temp);
		return temp;
	}
	
	private double getAnguloRelativo(double x1, double x2, double y1, double y2) {
		double norma1 = Math.sqrt(Math.pow(x1, 2) + Math.pow(y1, 2));
		double norma2 = Math.sqrt(Math.pow(x2, 2) + Math.pow(y2, 2));
		
		double produto = x1*x2 + y1*y2;
		
		return y1 < 0 ? 360 - Math.acos(produto / (norma1*norma2))/(Math.PI/180) : Math.acos(produto / (norma1*norma2))/(Math.PI/180);
	}
	
	private void setNoClipArmorStand(ArmorStand ams) {
		((CraftEntity)ams).getHandle().noclip = true;
	}
	
	public void updateAllPetsByEditor() {
		for (SummonedPet smp : PetsManager.getSummonedPetsByType(this.getCorpoName())) {
			ArmorStand ars = smp.getMembro(this.getMembroName());
			
			if (this.getCoordsHead() != null) ars.setHeadPose(this.getCoordsHead().getAsEulerAngle());
			
			if (this.getCoordsHand() != null) ars.setRightArmPose(this.getCoordsHand().getAsEulerAngle());
			
			if (this.getMainItem() != null) ars.getEquipment().setHelmet(this.getMainItem());
			
			if (this.getHandItem() != null) ars.getEquipment().setItemInMainHand(this.getHandItem());
			
			ars.setMarker(true);
			ars.setVisible(this.isVisible());
			ars.setSmall(this.isSmall());
			ars.setGravity(false);
			
			this.update(smp, ars);
		}
	}

}
