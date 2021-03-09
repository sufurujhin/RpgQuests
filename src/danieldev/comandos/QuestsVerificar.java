package danieldev.comandos;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import danieldev.main.Quests;
import danieldev.utils.Utils;
import danieldev.utils.FilesConfig;

public class QuestsVerificar extends Utils implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

			
		
		
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (FilesConfig.getPlayersConfig().getConfigurationSection("Players") == null) {
				setJogador(p, 1, false, "");
				return true;
			}

			if (!FilesConfig.getPlayersConfig().getConfigurationSection("Players").contains((p.getName().toUpperCase()))) {
				setJogador(p, 1, false, "");
				return true;
			}
			
			if (Utils.getStatusProgresso(p)) {
				if (FilesConfig.getPlayersConfig().getConfigurationSection("Players." + (p.getName().toUpperCase()) + ".listaProgresso") == null) {
					p.openInventory(Quests.invetarioQuest());
					return false;
				}

				Inventory inv = Quests.invetarioQuest();
				for (String s : FilesConfig.getPlayersConfig().getConfigurationSection("Players." + (p.getName().toUpperCase()) + ".listaProgresso").getKeys(false)) {
					if (FilesConfig.getPlayersConfig().getItemStack("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".quest") == null) {
						continue;
					}
					ItemStack item = new ItemStack(FilesConfig.getPlayersConfig().getItemStack("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".quest"));

					List<String> lores = new ArrayList<>();

					lores.add("§f------------------------");

					for (String id : FilesConfig.getPlayersConfig().getConfigurationSection("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".itens").getKeys(false)) {

						if (!FilesConfig.getPlayersConfig().getBoolean("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".itens." + id + ".status") && FilesConfig.getPlayersConfig().getString("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".itens." + id + ".tipo").equalsIgnoreCase("MatarMob")) {
							int qtd = FilesConfig.getPlayersConfig().getInt("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".itens." + id + ".qtd");
							int progresso = FilesConfig.getPlayersConfig().getInt("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".itens." + id + ".progresso");
							String nome = FilesConfig.getPlayersConfig().getString("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".itens." + id + ".nome");
							lores.add(Quests.main.getConfig().getString("Quests.LoresQuestMatar.missao").replaceAll("&", "§") + "§6" + progresso + "§9/§6" + qtd + " " + nome);
							continue;
						}
						if (!FilesConfig.getPlayersConfig().getBoolean("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".itens." + id + ".status") && FilesConfig.getPlayersConfig().getString("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".itens." + id + ".tipo").equalsIgnoreCase("Entrega")) {
							int qtd = FilesConfig.getPlayersConfig().getInt("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".itens." + id + ".qtd");
							int progresso = FilesConfig.getPlayersConfig().getInt("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".itens." + id + ".progresso");
							String nome = FilesConfig.getPlayersConfig().getString("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".itens." + id + ".nome");
							lores.add(Quests.main.getConfig().getString("Quests.LoresQuestEntregar.missao").replaceAll("&", "§") + "§6" + progresso + "§9/§6" + qtd + " " + nome);
							continue;
						}
						if (!FilesConfig.getPlayersConfig().getBoolean("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".itens." + id + ".status") && FilesConfig.getPlayersConfig().getString("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".itens." + id + ".tipo").equalsIgnoreCase("QuebrarBlocos")) {
							int qtd = FilesConfig.getPlayersConfig().getInt("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".itens." + id + ".qtd");
							int progresso = FilesConfig.getPlayersConfig().getInt("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".itens." + id + ".progresso");
							String nome = FilesConfig.getPlayersConfig().getString("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + s + ".itens." + id + ".nome");
							lores.add(Quests.main.getConfig().getString("Quests.LoresQuestQuebrar.missao").replaceAll("&", "§") + "§6" + progresso + "§9/§6" + qtd + " " + nome);
							continue;
						}

					}
					
					lores.add("§f------------------------");
					if(lores.size() < 3){
						continue;
					}
					lores.add(Quests.main.getConfig().getString("recompensa").replaceAll("&", "§").replaceAll("<money>", Quests.recompensas.get(item) + ""));
					ItemMeta im = item.getItemMeta();
					im.setLore(lores);
					item.setItemMeta(im);
					inv.addItem(item);

				}
				p.openInventory(inv);
			} else {
				p.openInventory(Quests.invetarioQuest());
			}

		} else {
			
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
					reloadKeysConfig();
					reloadQuebrar();
					reloadPlayersConfig();
					reloadQuestsConfig();
					Quests.main.reloadConfig();
					Quests.invetarioQuest();
					Quests.main.setQuests();
					Quests.main.craftQuest();
					Bukkit.getConsoleSender().sendMessage("§l§4[§5QUESTS§4]Arquivos Carregados com sucesso!");
					return true;
				}
				return false;
			}else{
				sender.sendMessage("§4[§5QUESTS§4]Este comando pode ser usado apenas em jogo!");
			}
			
			
		}
		return false;
	}

}
