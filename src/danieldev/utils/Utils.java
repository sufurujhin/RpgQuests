package danieldev.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;

import danieldev.main.Quests;
import net.milkbowl.vault.economy.Economy;

public class Utils extends FilesConfig {
	public static Inventory addQuests(Player p) {

		Inventory inv = Quests.invetarioQuest();

		return inv;
	}

	public static List<String> getLores(List<String> lores, String mobs) {
		List<String> lores2 = new ArrayList<>();
		for (String s : lores) {
			lores2.add((s.replaceAll("&", "§").replaceAll("<mobs>", "" + mobs)));
		}

		return lores2;
	}

	public static Inventory getConfimation(ItemStack item) {
		FileConfiguration config = Quests.main.getConfig();
		String pergunta = config.getString("InvetarioDePergunta.pergunta").replaceAll("&", "§");
		Inventory fakeInventory = Bukkit.getServer().createInventory(null, config.getInt("InvetarioDePergunta.tamanho"), pergunta);

		fakeInventory.setItem(3, getSim());

		fakeInventory.setItem(5, getNao());
		if (item != null) {
			fakeInventory.setItem(0, item);
		}

		return fakeInventory;
	}

	public static List<String> getLores(List<String> lores) {
		if (lores != null) {
			List<String> lores2 = new ArrayList<>();
			for (String s : lores) {
				lores2.add(s.replaceAll("&", "§"));
			}
			return lores2;
		}
		return null;
	}

	public static ItemStack getSim() {

		FileConfiguration config = Quests.main.getConfig();
		String idYes = config.getString("InvetarioDePergunta.sim.id").toUpperCase();
		String sim = config.getString("InvetarioDePergunta.sim.display").replaceAll("&", "§");
		List<String> lores = new ArrayList<>();
		lores = config.getStringList("InvetarioDePergunta.sim.lores");
		return getItemCraft(idYes, sim, lores);

	}

	public static Inventory openInventoryQuest(Player p) {

		Inventory inv = Quests.invetarioQuest();

		for (String rank : Quests.Ranks.keySet()) {

			String permissao = Quests.Ranks.get(rank);

			/*
			 * if(FilesConfig.getPlayersConfig().getString("Players." +
			 * (p.getName().toUpperCase()) + ".permissao")==null){
			 * getPlayersConfig().set("Players." + (p.getName().toUpperCase()) +
			 * ".permissao", permissao); savePlayersConfig();
			 * reloadPlayersConfig(); }
			 */

			if (GMHook.hasPermission(p, permissao) && !getPlayersConfig().getStringList("Players." + (p.getName().toUpperCase()) + ".permissoes").contains(permissao) && getPlayersConfig().getString("Players." + (p.getName().toUpperCase()) + ".permissao").equalsIgnoreCase(permissao)) {

				int nivel = FilesConfig.getPlayersConfig().getInt("Players." + (p.getName().toUpperCase()) + ".nivelQuest");
				List<ItemStack> listQuest = new ArrayList<>();
				if (getStatusConluida(p)) {
					for (String item : FilesConfig.getPlayersConfig().getConfigurationSection("Players." + (p.getName().toUpperCase()) + ".listaConcluida").getKeys(false)) {
						ItemStack item2 = FilesConfig.getPlayersConfig().getItemStack("Players." + (p.getName().toUpperCase()) + ".listaConcluida." + item + ".quest");
						listQuest.add(item2);
					}

				}

				if (getStatusProgresso(p)) {
					for (String item : FilesConfig.getPlayersConfig().getConfigurationSection("Players." + (p.getName().toUpperCase()) + ".listaProgresso").getKeys(false)) {

						ItemStack item2 = FilesConfig.getPlayersConfig().getItemStack("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + item + ".quest");

						if (!listQuest.contains(item2)) {
							listQuest.add(item2);
						}
					}
				}

				if (Quests.booksId.containsKey(nivel)) {
					for (ItemStack it : Quests.booksId.get(nivel)) {
						// if(!p.hasPermission(Quests.RanksItens.get(it))){
						// continue;
						// }
						if (!listQuest.contains(it) && Quests.RanksItens.get(it).equals(permissao)) {
							if (!getPlayersConfig().getString("Players." + (p.getName().toUpperCase()) + ".permissao").equalsIgnoreCase(permissao)) {
								getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".permissao", permissao);
							}

							inv.addItem(it);

						}

					}

				}
				break;
			}
		}

		return inv;
	}

	public static void setJogador(Player p, int nivel, boolean reset, String permiss) {

		// reloadPlayersConfig();
		if (FilesConfig.getPlayersConfig().getConfigurationSection("Players") != null) {

			if (!FilesConfig.getPlayersConfig().getConfigurationSection("Players").getKeys(false).contains((p.getName().toUpperCase()))) {
				List<String> permissoes = new ArrayList<>();
				FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".nivelQuest", nivel);
				FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".status", false);
				FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".listaConcluida", " ");
				FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".permissao", "");
				FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".permissoes", permissoes);
				FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".listaProgresso", " ");
				FilesConfig.savePlayersConfig();
				Utils.setPermissao(p);
				return;
			}
		}
		if (FilesConfig.getPlayersConfig().getConfigurationSection("Players") == null) {

			List<String> permissoes = new ArrayList<>();
			FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".nivelQuest", nivel);
			FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".status", false);
			FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".listaConcluida", " ");
			FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".permissao", "");
			FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".permissoes", permissoes);
			FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".listaProgresso", " ");
			FilesConfig.savePlayersConfig();
			Utils.setPermissao(p);
			return;
		}

		if (reset) {
			FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".nivelQuest", nivel);
			FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".status", false);
			FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".listaConcluida", " ");
			List<String> permissoes = new ArrayList<>();
			permissoes = FilesConfig.getPlayersConfig().getStringList("Players." + (p.getName().toUpperCase()) + ".permissoes");
			if (!permissoes.contains(permiss)) {
				permissoes.add(permiss);
			}
			FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".permissao", "");
			FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".permissoes", permissoes);
			FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".listaProgresso", " ");
			FilesConfig.savePlayersConfig();
			return;
		}
	}

	public static ItemStack getNao() {
		String idNo, nao;
		FileConfiguration config = Quests.main.getConfig();
		nao = config.getString("InvetarioDePergunta.nao.display").replaceAll("&", "§");

		idNo = config.getString("InvetarioDePergunta.nao.id").toUpperCase();
		List<String> lores = new ArrayList<>();
		lores = config.getStringList("InvetarioDePergunta.nao.lores");
		return getItemCraft(idNo, nao, lores);
	}

	public static boolean getStatusProgresso(Player p) {

		if (FilesConfig.getPlayersConfig().getConfigurationSection("Players") == null) {
			setJogador(p, 1, false, "");
			setPermissao(p);
			return false;
		}
		if (!FilesConfig.getPlayersConfig().getConfigurationSection("Players").contains((p.getName().toUpperCase()))) {
			setJogador(p, 1, false, "");
			setPermissao(p);
			if (FilesConfig.getPlayersConfig().getString("Players." + (p.getName().toUpperCase()) + ".permissao").length() < 1) {
				setPermissao(p);
			}

			return FilesConfig.getPlayersConfig().getConfigurationSection("Players." + (p.getName().toUpperCase()) + ".listaProgresso") != null;

		} else {
			if (FilesConfig.getPlayersConfig().getString("Players." + (p.getName().toUpperCase()) + ".permissao").length() < 1) {
				setPermissao(p);
			}
			return FilesConfig.getPlayersConfig().getConfigurationSection("Players." + (p.getName().toUpperCase()) + ".listaProgresso") != null;
		}

	}

	public static void quebrarSet(Player p, int qtd, int progresso, Material type, String[] path){
		
			FilesConfig.getPlayersConfig().set("Players." +path[0]+"."+path[1]+"."+path[2]+"."+path[3]+"."+path[4]+ ".status", true);
			FilesConfig.getPlayersConfig().set("Players." +path[0]+"."+path[1]+"."+path[2]+"."+path[3]+"."+path[4] + ".progresso", progresso);
			FilesConfig.savePlayersConfig();
			
			String[] s = { "Players." + path[0], path[2], path[4], "" + true, path[0]+"."+path[1]+"."+path[2]+"."+path[3]+"."+path[4], "" };
			Utils.calcular(p, s);
			
			return;

	}
	
	
	public static HashMap<String[], ItemStack> getItensProgresso(Player p) {
		HashMap<String[], ItemStack> lista = new HashMap<>();

		if (FilesConfig.getPlayersConfig().getConfigurationSection("Players") == null) {
			setJogador(p, 1, false, "");
		} else if (!FilesConfig.getPlayersConfig().getConfigurationSection("Players").contains((p.getName().toUpperCase()))) {
			setJogador(p, 1, false, "");
		}

		for (String nomeQuest : FilesConfig.getPlayersConfig().getConfigurationSection("Players." + (p.getName().toUpperCase()) + ".listaProgresso").getKeys(false)) {
			if (FilesConfig.getPlayersConfig().getConfigurationSection("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + nomeQuest) != null) {
				String nameQuest = "Players." + (p.getName().toUpperCase()) + ".listaProgresso." + nomeQuest;
				String permiss = "Players." + (p.getName().toUpperCase()) + ".permissao";
				for (String idItem : FilesConfig.getPlayersConfig().getConfigurationSection(nameQuest + ".itens").getKeys(false)) {

					String id = nameQuest + ".itens." + idItem;

					boolean status = !FilesConfig.getPlayersConfig().getBoolean(nameQuest + ".itens." + idItem + ".status");

					if (status) {
						ItemStack item = null;
						if (FilesConfig.getKeysConfig().getConfigurationSection("Keys").contains(idItem)) {
							item = FilesConfig.getKeysConfig().getItemStack("Keys." + idItem);
							// 0=Players.player - 1= nomeQuest - 2= idItem - 3=
							// StatusItemEntregue - 4=
							// Players.player.listaProgresso.nomeQuest.itens.idItem
							String[] s = { "Players." + (p.getName().toUpperCase()), nomeQuest, idItem, "" + status, id, permiss };
							lista.put(s, item);
							continue;
						}
						String name = idItem.toUpperCase();
						try {
							item = new ItemStack(Material.getMaterial(name));
						} catch (Exception e) {
							// TODO: handle exception
						}

						String[] s = { "Players." + (p.getName().toUpperCase()), nomeQuest, idItem, "" + status, id, permiss };
						lista.put(s, item);
					}

				}
			}

		}
		return lista;
	}

	public static HashMap<String[], EntityType> getMobsProgresso(Player p) {
		HashMap<String[], EntityType> lista = new HashMap<>();

		for (String nomeQuest : FilesConfig.getPlayersConfig().getConfigurationSection("Players." + (p.getName().toUpperCase()) + ".listaProgresso").getKeys(false)) {
			if (FilesConfig.getPlayersConfig().getConfigurationSection("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + nomeQuest) != null) {
				String nameQuest = "Players." + (p.getName().toUpperCase()) + ".listaProgresso." + nomeQuest;
				String permiss = "Players." + (p.getName().toUpperCase()) + ".permissao";
				for (String mobItem : FilesConfig.getPlayersConfig().getConfigurationSection(nameQuest + ".itens").getKeys(false)) {

					String id = nameQuest + ".itens." + mobItem;
					boolean status = !FilesConfig.getPlayersConfig().getBoolean(nameQuest + ".itens." + mobItem + ".status");

					if (status) {
						EntityType et = null;

						String name = mobItem.toUpperCase();
						try {
							et = EntityType.fromName(name);
						} catch (Exception e) {
							// TODO: handle exception
						}

						String[] s = { "Players." + (p.getName().toUpperCase()), nomeQuest, mobItem, "" + status, id, permiss };

						lista.put(s, et);
					}

				}
			}

		}
		return lista;
	}

	public static boolean getStatusConluida(Player p) {
		return FilesConfig.getPlayersConfig().getConfigurationSection("Players." + (p.getName().toUpperCase()) + ".listaConcluida") != null;
	}

	public static void setMoney(Player p, ItemStack item) {

		RegisteredServiceProvider<Economy> economyProvider = Quests.main.getServer().getServicesManager().getRegistration(Economy.class);

		Economy economy = economyProvider.getProvider();

		economy.depositPlayer(p.getName(), Quests.recompensas.get(item));
		p.sendMessage(Quests.main.getConfig().getString("Mensagens.RecompensaMoney").replaceAll("<money>", Quests.recompensas.get(item) + "").replaceAll("<quest>", item.getItemMeta().getDisplayName()).replaceAll("&", "§"));
	}

	public static ItemStack getItemCraft(String id, String display, List<String> lores) {

		ItemStack item = new ItemStack(Material.getMaterial(id));

		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName(display.replaceAll("&", "§"));
		itemMeta.setLore(getLores(lores));
		item.setItemMeta(itemMeta);
		return item;
	}

	public static void sendMsg(Player p, String item, String qtd, String msg) {
		p.sendMessage(msg.replaceAll("&", "§").replaceAll("<item>", item).replaceAll("<qtd>", "" + qtd));
	}

	public static void calcular(Player p, String[] path) {
		// 0=Players.player
		// - 1=
		// nomeQuest - 2=
		// idItem
		// - 3=
		// StatusItemEntregue
		// -
		// 4=
		// Players.player.listaProgresso.nomeQuest.itens.idItem
		boolean verificarQuest = false;
		boolean verificarItem = false;

		for (String s : FilesConfig.getPlayersConfig().getConfigurationSection(path[0] + ".listaProgresso." + path[1] + ".itens").getKeys(false)) {

			if (!FilesConfig.getPlayersConfig().getBoolean(path[0] + ".listaProgresso." + path[1] + ".itens." + s + ".status")) {
				verificarItem = true;
			}
		}

		if (!verificarItem) {
			if (Quests.player.containsKey(p)) {
				Quests.player.remove(p);
			}
			FilesConfig.getPlayersConfig().set(path[0] + ".listaProgresso." + path[1] + ".status", true);
			Utils.setMoney(p, FilesConfig.getPlayersConfig().getItemStack(path[0] + ".listaProgresso." + path[1] + ".quest"));
			FilesConfig.savePlayersConfig();
		}

		for (String quest : FilesConfig.getPlayersConfig().getConfigurationSection(path[0] + ".listaProgresso").getKeys(false)) {
			if (!FilesConfig.getPlayersConfig().getBoolean(path[0] + ".listaProgresso." + quest + ".status")) {
				verificarQuest = true;
			}
		}
		if (!verificarQuest) {

			if (Utils.openInventoryQuest(p).getItem(9) == null) {

				String permiss = FilesConfig.getPlayersConfig().getString(path[0] + ".permissao");
				int nivel = getPlayersConfig().getInt("Players." + (p.getName().toUpperCase()) + ".nivelQuest") + 1;
				getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".nivelQuest", nivel);
				getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".permissao", permiss);
				savePlayersConfig();

				if (Utils.openInventoryQuest(p).getItem(9) == null) {

					Server s = Quests.main.getServer();

					for (String rank : FilesConfig.getQuestsConfig().getConfigurationSection("").getKeys(false)) {
						String per = getQuestsConfig().getString(rank + ".Permissao");
						if (permiss.equalsIgnoreCase(per)) {
							if (!getPlayersConfig().getString("Players." + (p.getName().toUpperCase()) + ".permissoes").contains(per)) {
								for (String string : FilesConfig.getQuestsConfig().getStringList(rank + ".recompensaRank.comandos")) {
									String comando = string.replaceAll("@player", p.getName());
									s.dispatchCommand(s.getConsoleSender(), comando);
								}
								Utils.setJogador(p, 1, true, permiss);

							}
							break;
						}
					}

				}
			}
		}
	}

	public static void setPermissao(Player p) {

		List<String> list = new ArrayList<>();
		list = getPlayersConfig().getStringList("Players." + (p.getName().toUpperCase()) + ".permissoes");
		String aux = "";

		for (String per : GMHook.getGroups(p)) {

			if ((per.equalsIgnoreCase("PrataEliteMestre") || per.equalsIgnoreCase("PrataElite") || per.equalsIgnoreCase("Prata4") || per.equalsIgnoreCase("Prata3") || per.equalsIgnoreCase("Prata2") || per.equalsIgnoreCase("Prata") || (per.equalsIgnoreCase("Prata1"))) && !p.isOp()) {
				if (!list.contains("ezranks.rank.Prata1")) {
					list.add("ezranks.rank.Prata1");
				}
				break;
			}
			if ((per.equalsIgnoreCase("Ouro4") || per.equalsIgnoreCase("Ouro3") || per.equalsIgnoreCase("Ouro2") || per.equalsIgnoreCase("Ouro1") || (per.equalsIgnoreCase("Ouro"))) && !p.isOp()) {

				if (!list.contains("ezranks.rank.Prata1")) {
					list.add("ezranks.rank.Prata1");
				}
				if (!list.contains("deluxetags.tag.Ouro1")) {
					list.add("deluxetags.tag.Ouro1");
				}
				break;
			}
			if ((per.equalsIgnoreCase("AkCruzada") || per.equalsIgnoreCase("Ak2") || per.equalsIgnoreCase("Ak1") || per.equalsIgnoreCase("Ak")) && !p.isOp()) {
				if (!list.contains("ezranks.rank.Prata1")) {
					list.add("ezranks.rank.Prata1");
				}
				if (!list.contains("deluxetags.tag.Ouro1")) {
					list.add("deluxetags.tag.Ouro1");
				}
				if (!list.contains("deluxetags.tag.Ak1")) {
					list.add("deluxetags.tag.Ak1");
				}
				break;
			}
			if ((per.equalsIgnoreCase("Xerife1") || per.equalsIgnoreCase("Xerife")) && !p.isOp()) {
				if (!list.contains("ezranks.rank.Prata1")) {
					list.add("ezranks.rank.Prata1");
				}
				if (!list.contains("deluxetags.tag.Ouro1")) {
					list.add("deluxetags.tag.Ouro1");
				}
				if (!list.contains("deluxetags.tag.Ak1")) {
					list.add("deluxetags.tag.Ak1");
				}
				if (!list.contains("deluxetags.tag.Xerife")) {
					list.add("deluxetags.tag.Xerife");
				}
				break;
			}
			if ((per.equalsIgnoreCase("Aguia3") || per.equalsIgnoreCase("Aguia4") || per.equalsIgnoreCase("Aguia2") || per.equalsIgnoreCase("Aguia1") || per.equalsIgnoreCase("Aguia")) && !p.isOp()) {
				if (!list.contains("ezranks.rank.Prata1")) {
					list.add("ezranks.rank.Prata1");
				}
				if (!list.contains("deluxetags.tag.Ouro1")) {
					list.add("deluxetags.tag.Ouro1");
				}
				if (!list.contains("deluxetags.tag.Ak1")) {
					list.add("deluxetags.tag.Ak1");
				}
				if (!list.contains("deluxetags.tag.Xerife")) {
					list.add("deluxetags.tag.Xerife");
				}
				if (!list.contains("deluxetags.tag.Aguia1")) {
					list.add("deluxetags.tag.Aguia1");
				}
				break;
			}
			if ((per.equalsIgnoreCase("Supremo1") || per.equalsIgnoreCase("Supremo") || per.equalsIgnoreCase("Supremo")) && !p.isOp()) {
				if (!list.contains("ezranks.rank.Prata1")) {
					list.add("ezranks.rank.Prata1");
				}
				if (!list.contains("deluxetags.tag.Ouro1")) {
					list.add("deluxetags.tag.Ouro1");
				}
				if (!list.contains("deluxetags.tag.Ak1")) {
					list.add("deluxetags.tag.Ak1");
				}
				if (!list.contains("deluxetags.tag.Xerife")) {
					list.add("deluxetags.tag.Xerife");
				}
				if (!list.contains("deluxetags.tag.Aguia1")) {
					list.add("deluxetags.tag.Aguia1");
				}
				if (!list.contains("deluxetags.tag.Supremo")) {
					list.add("deluxetags.tag.Supremo");
				}
				break;
			}
			if ((per.equalsIgnoreCase("Global1") || per.equalsIgnoreCase("Global")) && !p.isOp()) {
				if (!list.contains("ezranks.rank.Prata1")) {
					list.add("ezranks.rank.Prata1");
				}
				if (!list.contains("deluxetags.tag.Ouro1")) {
					list.add("deluxetags.tag.Ouro1");
				}
				if (!list.contains("deluxetags.tag.Ak1")) {
					list.add("deluxetags.tag.Ak1");
				}
				if (!list.contains("deluxetags.tag.Xerife")) {
					list.add("deluxetags.tag.Xerife");
				}
				if (!list.contains("deluxetags.tag.Aguia1")) {
					list.add("deluxetags.tag.Aguia1");
				}
				if (!list.contains("deluxetags.tag.Supremo")) {
					list.add("deluxetags.tag.Supremo");
				}
				if (!list.contains("deluxetags.tag.Global")) {
					list.add("deluxetags.tag.Global");
				}
				break;
			}
			if ((per.equalsIgnoreCase("Lv20DaGc")) && !p.isOp()) {

				if (!list.contains("ezranks.rank.Prata1")) {
					list.add("ezranks.rank.Prata1");
				}
				if (!list.contains("deluxetags.tag.Ouro1")) {
					list.add("deluxetags.tag.Ouro1");
				}
				if (!list.contains("deluxetags.tag.Ak1")) {
					list.add("deluxetags.tag.Ak1");
				}
				if (!list.contains("deluxetags.tag.Xerife")) {
					list.add("deluxetags.tag.Xerife");
				}
				if (!list.contains("deluxetags.tag.Aguia1")) {
					list.add("deluxetags.tag.Aguia1");
				}
				if (!list.contains("deluxetags.tag.Supremo")) {
					list.add("deluxetags.tag.Supremo");
				}
				if (!list.contains("deluxetags.tag.Global")) {
					list.add("deluxetags.tag.Global");
				}
				if (!list.contains("deluxetags.tag.Lv20DaGc")) {
					list.add("deluxetags.tag.Lv20DaGc");
				}
				break;
			}
			if (per.equalsIgnoreCase("Lv20DaGc") && p.isOp()) {
				if (!list.contains("ezranks.rank.Prata1")) {
					list.add("ezranks.rank.Prata1");
				}
				if (!list.contains("deluxetags.tag.Ouro1")) {
					list.add("deluxetags.tag.Ouro1");
				}
				if (!list.contains("deluxetags.tag.Ak1")) {
					list.add("deluxetags.tag.Ak1");
				}
				if (!list.contains("deluxetags.tag.Xerife")) {
					list.add("deluxetags.tag.Xerife");
				}
				if (!list.contains("deluxetags.tag.Aguia1")) {
					list.add("deluxetags.tag.Aguia1");
				}
				if (!list.contains("deluxetags.tag.Supremo")) {
					list.add("deluxetags.tag.Supremo");
				}
				if (!list.contains("deluxetags.tag.Global")) {
					list.add("deluxetags.tag.Global");
				}
				if (!list.contains("deluxetags.tag.Lv20DaGc")) {
					list.add("deluxetags.tag.Lv20DaGc");
				}
				break;
			}
		}

		/*
		 * for (String per :
		 * Quests.main.getConfig().getStringList("Quests.TodasPermissoes")) { if
		 * (!GMHook.hasPermission(p, per.trim())) {
		 * 
		 * if (per.equalsIgnoreCase("")) {
		 * 
		 * }
		 * 
		 * if (i > 0) { aux = list.get((i - 1)); list.remove((i - 1)); break; }
		 * if (i == 0) { aux = per; break; }
		 * 
		 * }
		 * 
		 * p.sendMessage("Grupos: " + );
		 * 
		 * i++; }
		 */
		if (list.size() > 1) {
			aux = list.get((list.size() - 1));
			list.remove(aux);
		}else if (list.size() == 1) {
			aux = list.get((list.size() - 1));
			list.remove(aux);
		}else if (list.size() == 0) {
			aux = "ezranks.rank.Prata1";
		}
		if (!getPlayersConfig().getStringList("Players." + (p.getName().toUpperCase()) + ".permissoes").contains(aux)) {
			getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".permissao", aux);
			getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".permissoes", list);
			savePlayersConfig();

		}

	}
}
