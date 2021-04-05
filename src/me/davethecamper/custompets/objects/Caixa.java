package me.davethecamper.custompets.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.bukkit.inventory.ItemStack;

public class Caixa {
	
	private String titulo;
	private ItemStack chave;
	private String tipo_da_caixa;
	private ArrayList<RecompensaChave> recompensas;
	
	private double max_range = 0;
	

	public ItemStack getChave() {return this.chave;}
	
	public String getTitle() {return this.titulo;}
	
	public String getTipoDaCaixa() {return this.tipo_da_caixa;}
	
	public ArrayList<RecompensaChave> getRecompensas() {return this.recompensas;}
	
	public double getMaxRange() {
		if (this.max_range == 0) {
			this.max_range = 0;
			for (RecompensaChave rc : recompensas) {
				this.max_range += rc.getChance();
			}
		}
		return this.max_range;
	}
	

	
	public void setTitle(String var) {this.titulo = var;}
	
	public void setChave(ItemStack var) {this.chave = var;}
	
	public void setTipoCaixa(String var) {this.tipo_da_caixa = var;}
	
	public void setRecompensas(ArrayList<RecompensaChave> var) {this.recompensas = var;}
	
	public void setMaxRange(double var) {this.max_range = var;}
	
	public void shuffle() {
		Collections.shuffle(this.recompensas);
	}
	
	public RecompensaChave gerarRecompensa() {
		shuffle();
		
		double chance_atual = randomSomeDouble(getMaxRange());
		double numeroatual = 0.0;

		for (RecompensaChave recompensa : this.getRecompensas()) {
			double chance = recompensa.getChance();
			numeroatual = numeroatual + chance;
			if (chance_atual <= numeroatual || chance_atual > getMaxRange()) {
				return recompensa;
			}
		}
		
		return null;
	}
	
	public static double randomSomeDouble(double d) {
		Random r = new Random();
		double floor = Math.floor(d);
		int a = floor > 0 ? r.nextInt((int) floor) : 0;
		double b = (d-floor > 0 ? r.nextDouble() % (d-floor) : 0) + (floor > 0 ? r.nextDouble() : 0);
		
		return a+b;
	}

}

