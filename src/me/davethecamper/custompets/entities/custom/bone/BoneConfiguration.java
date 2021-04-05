package me.davethecamper.custompets.entities.custom.bone;

import java.util.HashMap;
import java.util.Set;

import me.davethecamper.custompets.entities.custom.ArmorStandConfiguration;

public class BoneConfiguration {
	
	public BoneConfiguration(BoneMoviment movimento, ArmorStandConfiguration main_bone, Set<ArmorStandConfiguration> sub_bones) {
		this.movimento = movimento;
		this.main_bone = main_bone;
		this.sub_bones = sub_bones;
	}
	
	private BoneMoviment movimento;
	
	private ArmorStandConfiguration main_bone;
	
	private Set<ArmorStandConfiguration> sub_bones;
	
	private HashMap<ArmorStandConfiguration, Double> distances = new HashMap<>();

	
	public BoneMoviment getMovimento() {return movimento;}

	public ArmorStandConfiguration getMainBone() {return main_bone;}

	public Set<ArmorStandConfiguration> getSubBones() {return sub_bones;}
	
	public double getDistance(ArmorStandConfiguration ars) {
		if (distances.get(ars) == null) {
			distances.put(ars, ars.getCoordsMain().distance(getMainBone().getCoordsMain()));
		}
		return distances.get(ars);
	}

}
