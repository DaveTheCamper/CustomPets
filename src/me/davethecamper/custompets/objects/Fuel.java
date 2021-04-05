package me.davethecamper.custompets.objects;

import org.bukkit.Material;

public enum Fuel {
	
	LAVA_BUCKET(20000),
	COAL_BLOCK(16000),
	DRIED_KELP_BLOCK(4000),
	BLAZE_ROD(2400),
	COAL(1600),
	CHARCOAL(1600),
	NULL(0);
	
	
	
    private int burntime;
	
	private Fuel(int burntime) {
        this.burntime = burntime;
    }
	
	public int getBurnTime() {
        return burntime;
    }
	
	public static Fuel valueOfMaterial(Material m) {
		switch (m) {
			case LAVA_BUCKET:
				return Fuel.LAVA_BUCKET;
			case COAL_BLOCK:
				return Fuel.COAL_BLOCK;
			case DRIED_KELP_BLOCK:
				return Fuel.DRIED_KELP_BLOCK;
			case BLAZE_ROD:
				return Fuel.BLAZE_ROD;
			case COAL:
				return Fuel.COAL;
			case CHARCOAL:
				return Fuel.CHARCOAL;
			default:
				return Fuel.NULL;
		}
	}

}
