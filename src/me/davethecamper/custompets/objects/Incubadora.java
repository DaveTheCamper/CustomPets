package me.davethecamper.custompets.objects;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.davethecamper.custompets.Utils;
import me.davethecamper.custompets.eggs.Ovo;
import me.davethecamper.custompets.eggs.OvoRaridade;
import me.davethecamper.custompets.eggs.OvosManager;

public class Incubadora {
	

	public Incubadora(IncubadoraType type, int max) {
		this.type = type;
		this.identificador = new Random().nextLong();
		this.max_usos = max;
	}
	
	public Incubadora(IncubadoraType type) {
		this.type = type;
		this.identificador = new Random().nextLong();
	}
	
	public Incubadora(FileConfiguration fc, String identificador) {
		this.type = IncubadoraType.valueOf(fc.getString("incubadoras." + identificador + ".type"));
		
		this.identificador = Long.parseLong(identificador);
		this.progresso = fc.getLong("incubadoras." + identificador + ".progresso");
		this.combustivel = fc.getLong("incubadoras." + identificador + ".combustivel");
		this.tempo_colocado_carvao = fc.getLong("incubadoras." + identificador + ".tempo_colocado_carvao");
		
		this.horas = fc.getInt("incubadoras." + identificador + ".horas");
		this.max_usos = fc.getInt("incubadoras." + identificador + ".max_usos");
		this.usos = fc.getInt("incubadoras." + identificador + ".usos");
		
		this.em_uso = fc.getBoolean("incubadoras." + identificador + ".em_uso");
		this.pet = fc.getString("incubadoras." + identificador + ".pet");
		this.ovo = fc.getString("incubadoras." + identificador + ".ovo");
	}
	
	private static final DecimalFormat f = new DecimalFormat("#,###");
	
	private final long MAX_COMBUSTIVEL = 576000;
	
	private IncubadoraType type = IncubadoraType.DEFAULT;
	
	private long identificador;
	private long progresso = 0;
	private long combustivel = 0;
	private long tempo_colocado_carvao = 0;
	
	private int horas = 0;
	private int max_usos = 0;
	private int usos = 0;
	
	private boolean em_uso = false;
	
	private String pet = "null";
	private String ovo = "null";
	
	
	
	
	
	public long getMaxCombustivelColocavel() {
		this.update();
		
		long max = MAX_COMBUSTIVEL;
		
		max -= this.combustivel;
		
		if (max + progresso > this.horas * 72000) {
			max = ((this.horas * 72000) - progresso) - this.combustivel;
		}
		
		return max;
	}
	
	public long getIdentificador() {return this.identificador;}
	

	private long getCombustivelMax() {
		this.update();
		
		long max = MAX_COMBUSTIVEL;
		
		if (max > this.horas * 72000) {
			max = (this.horas * 72000) - progresso;
		}
		
		return max;
	}
	
	private double getPercentageCombustivel() {
		return (this.combustivel/(double) getCombustivelMax())*100;
	}
	
	public String getPet() {return this.pet;}
	
	public Ovo getOvo() {return OvosManager.getOvo(this.ovo);}
	
	
	public boolean isEmUso() {return this.em_uso;}
	
	public boolean isReady() {return this.horas * 72000 <= this.progresso;}
	
	public boolean isLastUse() {
		switch (this.type) {
			case COMPRADA:
				if (this.max_usos > this.usos) break;
			case EXPIRE_TIER0:
				return true;
			default:
				break;
		}
		return false;
	}
	
	public void setCompleted() {
		this.progresso = this.horas*72000;
	}
	
	
	
	public void update() {
		if (this.tempo_colocado_carvao == 0) this.tempo_colocado_carvao = System.currentTimeMillis();
		
		int combustivel_usado = (int) (tempo_colocado_carvao + ((combustivel/20)*1000) > System.currentTimeMillis() ? 
				this.combustivel - (((tempo_colocado_carvao + ((combustivel/20)*1000)) - System.currentTimeMillis())/1000*20) : 
				this.combustivel);
		
		this.progresso += combustivel_usado;
		this.combustivel -= combustivel_usado;
		
		
		this.tempo_colocado_carvao = this.combustivel <= 0 ? 0 : System.currentTimeMillis();
	}
	
	public void addCombustivel(long add) {
		this.combustivel += add;
		this.update();
	}
	
	public void incubarOvo(Ovo ovo) {
		this.pet = ovo.gerarPet();
		this.ovo = ovo.getIdentificador();
		
		OvoRaridade or = OvosManager.getPetRaridade(this.pet);
		this.horas = or.getHoras();
		this.em_uso = true;
	}
	
	
	public ItemStack getDisplayItem() {
		this.update();
		
		Material m = Material.GLASS;
		String prefix = "§f";
		switch (this.type) {
			case TIER0:
				prefix = "§8";
				m = Material.BLACK_STAINED_GLASS;
				break;
				
			case COMPRADA:
				prefix = "§a";
				m = Material.GREEN_STAINED_GLASS;
				break;
				
			default:
				break;
		}
		
		ItemStack item = new ItemStack(m);
		ItemMeta im = item.getItemMeta();
		
		im.setDisplayName(prefix + "Incubadora");
		ArrayList<String> lore = new ArrayList<>();
		
		if (!this.isEmUso()) {
			lore.add("§aDisponivel para uso");
		} else {
			lore.add(!this.isReady() ? "§eEm progresso..." : "§2Pronto!");
			lore.add("");
			lore.add("§aProgresso: §7" + f.format(this.progresso) + "§f/§7" + f.format(this.horas*72000));
			lore.add("§aCombustivel: §7" + f.format(getPercentageCombustivel()) + "%");
		}
		
		if (this.type.equals(IncubadoraType.COMPRADA)) {
			lore.add("");
			lore.add("§7Usos restantes: " + (this.max_usos-this.usos));
		}
		
		im.setLore(lore);
		Utils.addPersistentData(im, "ovo_incubando", PersistentDataType.STRING, this.ovo);
		Utils.addPersistentData(im, "id", PersistentDataType.LONG, this.identificador);
		
		item.setItemMeta(im);
		
		return item;
	}
	
	public Inventory getInventory() {
		Inventory inv = Bukkit.createInventory(null, 45, "§2Incubadora " + this.identificador);
		
		ItemStack black = Utils.criarItemMenus(Material.BLACK_STAINED_GLASS_PANE, "§r");
		for (int i = 0; i < 27; i++) {
			inv.setItem(i, black);
		}
		
		inv.setItem(0, Utils.criarItemMenus(Material.BOOK, "§6O que é isso?", Utils.formatText("Aqui é onde nós vamos manter o ovo quente para que possa ser chocado, adicione os mesmos combustiveis de uma fornalha comum para manter a incubadora aquecida", "§a")));
		
		ItemStack white = Utils.criarItemMenus(Material.GLASS_PANE, "§r");
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				inv.setItem(3 + i + (9*j), white);
			}
		}
		
		inv.setItem(13, OvosManager.getOvo(this.ovo).getItem());
		
		if (!this.isReady()) inv.setItem(36, Utils.criarItemMenus(Material.GOLD_INGOT, "§aAcelerar processo", "§6Custo: §e" + getCustoAcelerar() + " cash's,,§7§oAperte 9 para comprar"));
		
		updateCombustivel(inv);
		
		inv.setItem(inv.getSize()-1, Utils.criarItemMenus(Material.PAPER, "§aVoltar para incubadoras"));
		
		return inv;
	}
	
	public int getCustoAcelerar() {
		if (this.isReady()) return 0;
		
		long total = (this.horas*72000) - this.progresso;
		long cash = total/72000 + (total % 72000 > 0 ? 1 : 0);
		
		return (int)cash;
	}
	

	public void updateCombustivel(InventoryView inv) {
		updateCombustivel(inv.getTopInventory());
	}
	
	public void updateCombustivel(Inventory inv) {
		if (!this.isReady()) {
			double percent = getPercentageCombustivel();
			int vidros_verdes = (int) (percent/20);
			vidros_verdes = vidros_verdes > 5 ? 5 : vidros_verdes;
			double sobra = percent % 20;
			
			ItemStack green = Utils.criarItemMenus(Material.GREEN_STAINED_GLASS_PANE, "§aCombustivel", "§eCombustivel atual: " + f.format(this.combustivel));
			int slot = 29;
			for (int i = 0; i < vidros_verdes; i++) {
				inv.setItem(slot, green);
				slot++;
			}
			
			if (sobra > 0 && vidros_verdes < 5) {
				inv.setItem(slot, Utils.criarItemMenus(Material.YELLOW_STAINED_GLASS_PANE, "§aCombustivel", "§eCombustivel atual: " + f.format(this.combustivel)));
				slot++;
			}
	
			ItemStack red = Utils.criarItemMenus(Material.RED_STAINED_GLASS_PANE, "§aCombustivel", "§eCombustivel atual: " + f.format(this.combustivel));
			for (int i = vidros_verdes + (sobra > 0 ? 1 : 0); i < 5; i++) {
				inv.setItem(slot, red);
				slot++;
			}
			
			inv.setItem(28, Utils.criarItemMenus(Material.FURNACE, "§eProgresso", 
					"§7Combustivel: §a" + f.format(this.combustivel) + "§f/§a" + f.format(this.getCombustivelMax()) + ",,"
					+ "§7Progresso: §a" + f.format(this.progresso) + "§f/§a" + f.format(this.horas*72000) + " §7§o(" + f.format(((double)this.progresso/(double)(this.horas*72000))*100) + "%)"));
		} else {
			inv.setItem(31, Utils.criarItemMenus(Material.LIME_WOOL, "§2Abrir ovo"));
		}
	} 
	
	public void reset(Player p) {
		this.tempo_colocado_carvao = 0;
		this.progresso = 0;
		this.combustivel = 0;
		
		this.em_uso = false;
		this.usos++;
		
		this.ovo = "null";
		this.pet = "null";
		
		if (!Utils.isTier0(p)) {
			switch (this.type) {
				case TIER0:
					this.type = IncubadoraType.EXPIRE_TIER0;
					break;
				default:
					break;
			}
		}
	}
	
	public void save(FileConfiguration fc) {
		fc.set("incubadoras." + this.identificador + ".type", this.type.toString());
		
		fc.set("incubadoras." + this.identificador + ".progresso", this.progresso);
		fc.set("incubadoras." + this.identificador + ".combustivel", this.combustivel);
		fc.set("incubadoras." + this.identificador + ".tempo_colocado_carvao", this.tempo_colocado_carvao);
		
		fc.set("incubadoras." + this.identificador + ".horas", this.horas);
		fc.set("incubadoras." + this.identificador + ".max_usos", this.max_usos);
		fc.set("incubadoras." + this.identificador + ".usos", this.usos);
		
		fc.set("incubadoras." + this.identificador + ".em_uso", this.em_uso);
		fc.set("incubadoras." + this.identificador + ".pet", this.pet);
		fc.set("incubadoras." + this.identificador + ".ovo", this.ovo);
	}

}
