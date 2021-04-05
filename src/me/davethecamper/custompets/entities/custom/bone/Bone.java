package me.davethecamper.custompets.entities.custom.bone;

import me.davethecamper.custompets.entities.custom.SummonedPet;

public class Bone {
	
	public Bone(SummonedPet pet, BoneConfiguration config) {
		this.pet = pet;
		this.config = config;
		this.movimento_atual = config.getMovimento();
		this.tick = 0;
	}
	
	private SummonedPet pet;
	
	private BoneConfiguration config;
	
	private int tick;

	private BoneMoviment movimento_atual;
	
	

	public SummonedPet getPet() {return pet;}

	public BoneConfiguration getConfig() {return config;}

	public int getTick() {return tick;}

	public BoneMoviment getMovimentoAtual() {return movimento_atual;}
	


	public void increaseTick() {
		
		switch (this.movimento_atual) {
			case FORWARD:
				this.tick += pet.isMoving() ? 1 : (this.tick > 0 ? -1 : 1);
				break;
			case BACKWARD:
				this.tick -= pet.isMoving() ? 1 : (this.tick < 0 ? -1 : 1);
				break;
		}
	}
	
	public void invertDirection() {
		switch (this.movimento_atual) {
			case FORWARD:
				this.movimento_atual = BoneMoviment.BACKWARD;
				break;
			case BACKWARD:
				this.movimento_atual = BoneMoviment.FORWARD;
				break;
		}
		
	}
	
	public void setTick(int tick) {this.tick = tick;}

	public void setMovimentoAtual(BoneMoviment movimento_atual) {this.movimento_atual = movimento_atual;}

}
