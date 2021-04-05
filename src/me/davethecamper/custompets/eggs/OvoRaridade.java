package me.davethecamper.custompets.eggs;

import org.bukkit.configuration.file.FileConfiguration;

public class OvoRaridade {
	
	public OvoRaridade(FileConfiguration fc, String nome) {
		this.ordem = fc.getInt("raridades." + nome + ".ordem");
		this.fragmentos = fc.getInt("raridades." + nome + ".fragmentos");
		this.horas = fc.getInt("raridades." + nome + ".horas");
		this.tier = fc.getInt("raridades." + nome + ".tier_necessario");

		this.nome = fc.getString("raridades." + nome + ".nome");
		this.prefixo = fc.getString("raridades." + nome + ".prefixo");
		
		this.chance = fc.getDouble("raridades." + nome + ".chance");
	}
	
	private int ordem;
	private int fragmentos;
	private int horas;
	private int tier;
	
	private String nome;
	private String prefixo;
	
	private double chance;
	

	public int getOrdem() {return ordem;}

	public int getFragmentos() {return fragmentos;}

	public int getHoras() {return horas;}

	public int getTier() {return tier;}
	

	public String getNome() {return nome;}

	public String getPrefixo() {return prefixo;}
	

	public double getChance() {return chance;}

}
