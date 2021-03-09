package danieldev.main;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import danieldev.comandos.ComandosQuests;
import danieldev.comandos.QuestsVerificar;
import danieldev.eventos.CanceliClicksNoInventario;
import danieldev.eventos.JogadorEntraNoJogo;
import danieldev.eventos.JogadorInterageNpc;
import danieldev.eventos.MatarMobs;
import danieldev.eventos.QuebrarBlocos;
import danieldev.utils.Utils;
import danieldev.utils.FilesConfig;
import danieldev.utils.GMHook;
import net.milkbowl.vault.economy.Economy;

public class Quests extends JavaPlugin {

	public static Quests main;
	public static Economy econ = null;
	public static HashMap<String, String> Ranks;
	public static HashMap<ItemStack, String> RanksItens;
	public static HashMap<String, List<String>> questsMatar;
	public static HashMap<String, List<ItemStack>> books;
	public static HashMap<Integer, List<ItemStack>> booksId;
	public static HashMap<ItemStack, List<String>> itensQuests;
	public static HashMap<ItemStack, HashMap<String, String>> tipoQuests;
	public static HashMap<ItemStack, Double> recompensas;
	public static HashMap<String, List<String>> questsEntrega;
	public static HashMap<String, List<String>> questsQuebrar;
	public static HashMap<Integer, String> npcId;
	public static ItemStack painel;

	@Override
	public void onEnable() {
		main = this;
		if (!new File(getDataFolder(), "config.yml").exists()) {
			saveDefaultConfig();
		}
		FilesConfig.reloadKeysConfig();
		FilesConfig.reloadQuestsConfig();
		FilesConfig.reloadPlayersConfig();
		Bukkit.getPluginManager().registerEvents(new CanceliClicksNoInventario(), this);
		Bukkit.getPluginManager().registerEvents(new QuebrarBlocos(), this);
		Bukkit.getPluginManager().registerEvents(new MatarMobs(), this);
		Bukkit.getPluginManager().registerEvents(new GMHook(this), this);
		
		Bukkit.getPluginManager().registerEvents(new JogadorInterageNpc(), this);
		Bukkit.getPluginManager().registerEvents(new JogadorEntraNoJogo(), this);
		Bukkit.getPluginCommand("qnpc").setExecutor(new ComandosQuests());
		Bukkit.getPluginCommand("quest").setExecutor(new QuestsVerificar());

		invetarioQuest();
		setQuests();
		craftQuest();

	}

	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		super.onDisable();
	}

	public void craftQuest() {
		books = new HashMap<>();
		tipoQuests = new HashMap<>();
		booksId = new HashMap<>();
		itensQuests = new HashMap<>();
		recompensas = new HashMap<>();
		RanksItens = new HashMap<>();
		String cor = Quests.main.getConfig().getString("Quests.CorNomeItensMobs");
		for (String rank : FilesConfig.getQuestsConfig().getConfigurationSection("").getKeys(false)) {
			String permissao = (FilesConfig.getQuestsConfig().getString(rank + ".Permissao").replaceAll("\\s+", ""));
			List<ItemStack> itens = new ArrayList<>();

			// Entregar itens
			if (Quests.questsEntrega.size() > 0) {
				if (Quests.questsEntrega.containsKey(permissao)) {
					for (String quest : Quests.questsEntrega.get(permissao)) {
						ItemStack book = new ItemStack(Material.BOOK);

						ItemMeta im = book.getItemMeta();
						im.setDisplayName("Quest " + rank + " " + FilesConfig.getQuestsConfig().getString(rank + ".Quests.Entrega." + quest + ".subrank"));

						double recompensa = FilesConfig.getQuestsConfig().getDouble(rank + ".Quests.Entrega." + quest + ".recompensa");
						String nome = "";
						List<String> lores = new ArrayList<>();
						lores.add("&f------------------------");
						String display = "&l" + Quests.main.getConfig().getString("Quests.NomeItemQuest");
						
						im.setDisplayName(display.replaceAll("&", "§").replaceAll("<rank>", rank).replaceAll("<subrank>", FilesConfig.getQuestsConfig().getString(rank + ".Quests.Entrega." + quest + ".subrank")));

						lores = Quests.main.getConfig().getStringList("Quests.LoresQuestEntregar.descricao");
						String mobs = "";
						int x = 0;
						String missao = Quests.main.getConfig().getString("Quests.LoresQuestEntregar.missao");
						List<String> missoes = new ArrayList<>();
						missoes.add(missao);
						List<String> questListItens = new ArrayList<>();

						HashMap<String, String> tipo = new HashMap<>();

						for (String bloco : FilesConfig.getQuestsConfig().getConfigurationSection(rank + ".Quests.Entrega." + quest + ".items").getKeys(false)) {
							nome = FilesConfig.getQuestsConfig().getString(rank + ".Quests.Entrega." + quest + ".items." + bloco + ".nome");
							int qtd = FilesConfig.getQuestsConfig().getInt(rank + ".Quests.Entrega." + quest + ".items." + bloco + ".qtd");

							questListItens.add("entregar" + ":" + bloco + ":" + qtd + ":" + nome);
							tipo.put(bloco, "Entrega");
							if (x == 0) {
								missoes.add(cor + ("x<qtd>" + " " + "<item>" + "'s").replaceAll("<item>", nome).replaceAll("<qtd>", "" + qtd));
							} else {
								if (x == FilesConfig.getQuestsConfig().getConfigurationSection(rank + ".Quests.Entrega." + quest + ".items").getKeys(false).size()) {
									missoes.add(cor + ("x<qtd>" + " " + "<item>" + "'s").replaceAll("<item>", nome).replaceAll("<qtd>", "" + qtd));
								} else {
									missoes.add(cor + ("x<qtd>" + " " + "<item>" + "'s").replaceAll("<item>", nome).replaceAll("<qtd>", "" + qtd));
								}
							}

							if (mobs == "") {
								mobs = nome;
							} else {
								if (x == FilesConfig.getQuestsConfig().getConfigurationSection(rank + ".Quests.Entrega." + quest + ".items").getKeys(false).size()) {
									mobs += nome;
								} else {
									mobs += ", " + nome;
								}
							}
							x++;
						}
						lores.add("&f------------------------");
						for (String ss : missoes) {
							lores.add(ss.replaceAll("&", "§"));
						}
						lores.add("&f------------------------");
						String money = Quests.main.getConfig().getString("recompensa").replaceAll("<money>", "" + recompensa).replaceAll("&", "§");
						lores.add(money);
						lores = Utils.getLores(lores, mobs);

						im.setLore(lores);
						im.addEnchant(Enchantment.ARROW_INFINITE, 10, true);
						im.addItemFlags(ItemFlag.HIDE_ENCHANTS);

						book.setItemMeta(im);
						itens.add(book);
						itensQuests.put(book, questListItens);
						recompensas.put(book, FilesConfig.getQuestsConfig().getDouble(rank + ".Quests.Entrega." + quest + ".recompensa"));

						tipoQuests.put(book, tipo);
						RanksItens.put(book, permissao);
						if (booksId.containsKey(FilesConfig.getQuestsConfig().getInt(rank + ".Quests.Entrega." + quest + ".nivel"))) {
							booksId.get(FilesConfig.getQuestsConfig().getInt(rank + ".Quests.Entrega." + quest + ".nivel")).add(book);
						} else {
							List<ItemStack> list = new ArrayList<>();
							list.add(book);
							booksId.put(FilesConfig.getQuestsConfig().getInt(rank + ".Quests.Entrega." + quest + ".nivel"), list);
						}

					}
				}
			}
			// Matar Mobs
			if (Quests.questsMatar.size() > 0) {

				if (Quests.questsMatar.containsKey(permissao)) {
					for (String quest : Quests.questsMatar.get(permissao)) {
						ItemStack book = new ItemStack(Material.BOOK);

						ItemMeta im = book.getItemMeta();
						im.setDisplayName("Quest " + rank + " " + FilesConfig.getQuestsConfig().getString(rank + ".Quests.MatarMob." + quest + ".subrank"));

						double recompensa = FilesConfig.getQuestsConfig().getDouble(rank + ".Quests.MatarMob." + quest + ".recompensa");
						String nome = "";
						List<String> lores = new ArrayList<>();
						lores.add("&f------------------------");
						String display = "&l" + Quests.main.getConfig().getString("Quests.NomeItemQuest");
						im.setDisplayName(display.replaceAll("&", "§").replaceAll("<rank>", rank).replaceAll("<subrank>", FilesConfig.getQuestsConfig().getString(rank + ".Quests.MatarMob." + quest + ".subrank")));

						lores = Quests.main.getConfig().getStringList("Quests.LoresQuestMatar.descricao");
						String mobs = "";
						int x = 0;
						String missao = Quests.main.getConfig().getString("Quests.LoresQuestMatar.missao");
						List<String> missoes = new ArrayList<>();
						missoes.add(missao);
						List<String> questListItens = new ArrayList<>();
						HashMap<String, String> tipo = new HashMap<>();
						for (String mob : FilesConfig.getQuestsConfig().getConfigurationSection(rank + ".Quests.MatarMob." + quest + ".items").getKeys(false)) {
							nome = FilesConfig.getQuestsConfig().getString(rank + ".Quests.MatarMob." + quest + ".items." + mob + ".nome");
							int qtd = FilesConfig.getQuestsConfig().getInt(rank + ".Quests.MatarMob." + quest + ".items." + mob + ".qtd");
							tipo.put(mob, "MatarMob");
							questListItens.add("matar" + ":" + mob + ":" + qtd + ":" + nome);

							if (x == 0) {
								missoes.add(cor + ("x<qtd>" + " " + "<item>" + "'s").replaceAll("<item>", nome).replaceAll("<qtd>", "" + qtd));
							} else {
								if (x == FilesConfig.getQuestsConfig().getConfigurationSection(rank + ".Quests.MatarMob." + quest + ".items").getKeys(false).size()) {
									missoes.add(cor + ("x<qtd>" + " " + "<item>" + "'s").replaceAll("<item>", nome).replaceAll("<qtd>", "" + qtd));
								} else {
									missoes.add(cor + ("x<qtd>" + " " + "<item>" + "'s").replaceAll("<item>", nome).replaceAll("<qtd>", "" + qtd));
								}
							}

							if (mobs == "") {
								mobs = nome;
							} else {
								if (x == FilesConfig.getQuestsConfig().getConfigurationSection(rank + ".Quests.MatarMob." + quest + ".items").getKeys(false).size()) {
									mobs += nome;
								} else {
									mobs += ", " + nome;
								}
							}
							x++;
						}
						lores.add("&f------------------------");
						for (String ss : missoes) {
							lores.add(ss.replaceAll("&", "§"));
						}
						lores.add("&f------------------------");
						String money = Quests.main.getConfig().getString("recompensa").replaceAll("<money>", "" + recompensa).replaceAll("&", "§");
						lores.add(money);
						lores = Utils.getLores(lores, mobs);
						im.setLore(lores);
						im.addEnchant(Enchantment.ARROW_INFINITE, 10, true);
						im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						book.setItemMeta(im);
						itens.add(book);
						itensQuests.put(book, questListItens);
						recompensas.put(book, FilesConfig.getQuestsConfig().getDouble(rank + ".Quests.MatarMob." + quest + ".recompensa"));
						tipoQuests.put(book, tipo);
						RanksItens.put(book, permissao);
						if (booksId.containsKey(FilesConfig.getQuestsConfig().getInt(rank + ".Quests.MatarMob." + quest + ".nivel"))) {
							booksId.get(FilesConfig.getQuestsConfig().getInt(rank + ".Quests.MatarMob." + quest + ".nivel")).add(book);
						} else {
							List<ItemStack> list = new ArrayList<>();
							list.add(book);
							booksId.put(FilesConfig.getQuestsConfig().getInt(rank + ".Quests.MatarMob." + quest + ".nivel"), list);
						}
					}
				}
			}

			// QUebrar blocos
			if (Quests.questsQuebrar.size() > 0) {
				if (Quests.questsQuebrar.containsKey(permissao)) {
					for (String quest : Quests.questsQuebrar.get(permissao)) {

						ItemStack book = new ItemStack(Material.BOOK);

						ItemMeta im = book.getItemMeta();
						im.setDisplayName("Quest " + rank + " " + FilesConfig.getQuestsConfig().getString(rank + ".Quests.QuebrarBlocos." + quest + ".subrank"));

						double recompensa = FilesConfig.getQuestsConfig().getDouble(rank + ".Quests.QuebrarBlocos." + quest + ".recompensa");

						List<String> lores = new ArrayList<>();
						lores.add("&f------------------------");
						String display = "&l" + Quests.main.getConfig().getString("Quests.NomeItemQuest");
						im.setDisplayName(display.replaceAll("&", "§").replaceAll("<rank>", rank).replaceAll("<subrank>", FilesConfig.getQuestsConfig().getString(rank + ".Quests.QuebrarBlocos." + quest + ".subrank")));

						lores = Quests.main.getConfig().getStringList("Quests.LoresQuestQuebrar.descricao");
						String mobs = "";
						int x = 0;
						String missao = Quests.main.getConfig().getString("Quests.LoresQuestQuebrar.missao");
						List<String> missoes = new ArrayList<>();
						missoes.add(missao);
						String nome = "";
						List<String> questListItens = new ArrayList<>();
						HashMap<String, String> tipo = new HashMap<>();
						for (String bloco : FilesConfig.getQuestsConfig().getConfigurationSection(rank + ".Quests.QuebrarBlocos." + quest + ".items").getKeys(false)) {
							nome = FilesConfig.getQuestsConfig().getString(rank + ".Quests.QuebrarBlocos." + quest + ".items." + bloco + ".nome");
							int qtd = FilesConfig.getQuestsConfig().getInt(rank + ".Quests.QuebrarBlocos." + quest + ".items." + bloco + ".qtd");
							tipo.put(bloco, "QuebrarBlocos");
							questListItens.add("quebrar" + ":" + bloco + ":" + qtd + ":" + nome);
							if (x == 0) {
								missoes.add(cor + ("x<qtd>" + " " + "<item>" + "'s").replaceAll("<item>", nome).replaceAll("<qtd>", "" + qtd));
							} else {
								if (x == FilesConfig.getQuestsConfig().getConfigurationSection(rank + ".Quests.QuebrarBlocos." + quest + ".items").getKeys(false).size()) {
									missoes.add(cor + ("x<qtd>" + " " + "<item>" + "'s").replaceAll("<item>", nome).replaceAll("<qtd>", "" + qtd));
								} else {
									missoes.add(cor + (("x<qtd>" + " " + "<item>" + "'s").replaceAll("<item>", nome).replaceAll("<qtd>", "" + qtd)));
								}
							}

							if (mobs == "") {
								mobs = nome;
							} else {
								if (x == FilesConfig.getQuestsConfig().getConfigurationSection(rank + ".Quests.QuebrarBlocos." + quest + ".items").getKeys(false).size()) {
									mobs += nome;
								} else {
									mobs += ", " + nome;
								}
							}
							x++;
						}
						lores.add("&f------------------------");
						for (String ss : missoes) {
							lores.add(ss.replaceAll("&", "§"));
						}
						lores.add("&f------------------------");
						String money = Quests.main.getConfig().getString("recompensa").replaceAll("<money>", "" + recompensa).replaceAll("&", "§");
						lores.add(money);
						lores = Utils.getLores(lores, mobs);
						im.setLore(lores);
						im.addEnchant(Enchantment.ARROW_INFINITE, 10, true);
						im.addItemFlags(ItemFlag.HIDE_ENCHANTS);

						book.setItemMeta(im);
						itens.add(book);
						itensQuests.put(book, questListItens);
						recompensas.put(book, FilesConfig.getQuestsConfig().getDouble(rank + ".Quests.QuebrarBlocos." + quest + ".recompensa"));
						tipoQuests.put(book, tipo);
						RanksItens.put(book, permissao);
						if (booksId.containsKey(FilesConfig.getQuestsConfig().getInt(rank + ".Quests.QuebrarBlocos." + quest + ".nivel"))) {
							booksId.get(FilesConfig.getQuestsConfig().getInt(rank + ".Quests.QuebrarBlocos." + quest + ".nivel")).add(book);
						} else {
							List<ItemStack> list = new ArrayList<>();
							list.add(book);
							
							booksId.put(FilesConfig.getQuestsConfig().getInt(rank + ".Quests.QuebrarBlocos." + quest + ".nivel"), list);
						}
					}

					books.put(permissao, itens);
				}
			}
		}
	}

	public static HashMap<String, Integer> player;
	public static HashMap<Player, String> playerIds;
	public void setQuests() {
		Ranks = new HashMap<>();
		
		questsMatar = new HashMap<>();
		questsEntrega = new HashMap<>();
		questsQuebrar = new HashMap<>();
		npcId = new HashMap<>();
		player = new HashMap<>();
		playerIds = new HashMap<>();
		if (FilesConfig.getQuestsConfig() != null) {

			Bukkit.getConsoleSender().sendMessage("§2[§6QUESTS§2]§6 Carregando quests...");
			for (String rank : FilesConfig.getQuestsConfig().getConfigurationSection("").getKeys(false)) {
				
				if (FilesConfig.getQuestsConfig().getConfigurationSection(rank).contains("Permissao")) {
					String permissao = (FilesConfig.getQuestsConfig().getString(rank + ".Permissao").replaceAll("\\s+", ""));
					Ranks.put(rank, permissao);
					npcId.put(FilesConfig.getQuestsConfig().getInt(rank + ".IdNpc"), rank);
					
					if (FilesConfig.getQuestsConfig().getConfigurationSection(rank + ".Quests.MatarMob") != null) {
						for (String kill : FilesConfig.getQuestsConfig().getConfigurationSection(rank + ".Quests.MatarMob").getKeys(false)) {
							if (questsMatar.containsKey(permissao)) {
								questsMatar.get(permissao).add(kill);
							} else {
								List<String> list = new ArrayList<>();
								list.add(kill);
								questsMatar.put(permissao, list);
							}
						}

					}
					
					if (FilesConfig.getQuestsConfig().getConfigurationSection(rank + ".Quests.Entrega") != null) {

						for (String entrega : FilesConfig.getQuestsConfig().getConfigurationSection(rank + ".Quests.Entrega").getKeys(false)) {

							if (questsEntrega.containsKey(permissao)) {
								questsEntrega.get(permissao).add(entrega);
							} else {
								List<String> list = new ArrayList<>();
								list.add(entrega);
								questsEntrega.put(permissao, list);
							}
						}
					}
					if (FilesConfig.getQuestsConfig().getConfigurationSection(rank + ".Quests.QuebrarBlocos") != null) {

						for (String entrega : FilesConfig.getQuestsConfig().getConfigurationSection(rank + ".Quests.QuebrarBlocos").getKeys(false)) {

							if (questsQuebrar.containsKey(permissao)) {
								questsQuebrar.get(permissao).add(entrega);
							} else {
								List<String> list = new ArrayList<>();
								list.add(entrega);
								questsQuebrar.put(permissao, list);
							}

						}
					}

					Bukkit.getConsoleSender().sendMessage("§2[§6QUESTS§2]§6 As Quests do Rank§2[§6 " + rank + " §2]§6foram carregadas!");
				} else {
					Bukkit.getConsoleSender().sendMessage("§2[§6QUESTS§2]§6 O rank §2[§6 " + rank + " §2]§6 esta sem permissao, favor adicionar e deh /rl no server");
				}
			}
			Bukkit.getConsoleSender().sendMessage("§2[§6QUESTS§2]§6 Todas as Quests foram caregadas!");
		} else {
			Bukkit.getConsoleSender().sendMessage("§2[§6QUESTS§2]§6 Não hã nenhuma quest");
		}

	}

	public static Inventory invetarioQuest() {
		String nome = Quests.main.getConfig().getString("Quests.NomeInventario").replaceAll("&", "§");
		Inventory inv = Bukkit.getServer().createInventory(null, Quests.main.getConfig().getInt("Quests.TamanhoInventarioQuest"), nome);

		for (String s : Quests.main.getConfig().getStringList("Quests.Paineis")) {
			String[] path = s.split(":");

			ItemStack item = new ItemStack(Material.getMaterial((path[1].toUpperCase())));
			ItemMeta im = item.getItemMeta();

			im.setDisplayName(" ");
			item.setItemMeta(im);
			if (Integer.parseInt(path[0]) < 54) {
				inv.setItem(Integer.parseInt(path[0]), item);
			}
		}

		return inv;
	}

}
