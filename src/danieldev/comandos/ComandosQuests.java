package danieldev.comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import danieldev.main.Quests;
import danieldev.utils.Utils;
import danieldev.utils.FilesConfig;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;

public class ComandosQuests extends Utils implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		// TODO Auto-generated method stub

		if (sender instanceof Player) {
			Player p = (Player) sender;

			if (p.hasPermission("quest.config")) {
				// exemplo do comando
				// /qnpc add item NomeParaOItem
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("add")) {
						if (p.getInventory().getItemInMainHand() == null) {
							p.sendMessage("§4[§5QUESTS§4] Precisa segurar o item na mao principal");
							return false;
						}

						FilesConfig.getKeysConfig().set("Keys." + args[1], p.getInventory().getItemInMainHand());
						FilesConfig.saveKeysConfig();
						p.sendMessage("§4[§5QUESTS§4] Item §4[§5" + args[1] + "§4] Salvo na config Keys.yml");
						return true;
					}
					// qnpc give Basica
					if (args[0].equalsIgnoreCase("give")) {
						if (getKeysConfig().getConfigurationSection("Keys") !=null) {
							if(getKeysConfig().getConfigurationSection("Keys").contains(args[1])){
								p.getInventory().addItem(getKeysConfig().getItemStack("Keys."+args[1]));
								p.sendMessage("§4[§5QUESTS§4] Ganhou um ["+args[1]+"]");
							}else{ 
								p.sendMessage("§4[§5QUESTS§4] O item §4[§5" + args[1] + "§4] não encontrado na config Keys.yml");
							}
							return false;
						}else{ 
							p.sendMessage("§4[§5QUESTS§4] Config Keys.yml fazia");
						}

						FilesConfig.getKeysConfig().set("Keys." + args[1], p.getInventory().getItemInMainHand());
						FilesConfig.saveKeysConfig();
						
						return true;
					}
					return false;
				}
				if (args.length == 0) {
						p.sendMessage("§l§4[§5QUESTS§4]----------------# Quests #------------------");
						p.sendMessage("§l§4[§5QUESTS§4] /qnpc create [NomeRank] &5NomeNpc- criar npc de quest !");
						p.sendMessage("§l§4[§5QUESTS§4] /qnpc add item NomeParaOItem = Adicionar um item qualquer na config keys.yml");
						p.sendMessage("§l§4[§5QUESTS§4] /quest = ver progresso de quests");
						p.sendMessage("§l§4[§5QUESTS§4]-------------------------------------------");
					return false;
				}
				if (args.length == 1) {
					p.sendMessage("§l§4[§5QUESTS§4]----------------# Quests #------------------");
					p.sendMessage("§l§4[§5QUESTS§4] /qnpc create [NomeRank] &5NomeNpc- criar npc de quest !");
					p.sendMessage("§l§4[§5QUESTS§4] /qnpc add item NomeParaOItem = Adicionar um item qualquer na config keys.yml");
					p.sendMessage("§l§4[§5QUESTS§4] /quest = ver progresso de quests");
					p.sendMessage("§l§4[§5QUESTS§4]-------------------------------------------");
				return false;
			}
				// /qnpc create [NomeRank] &5NomeNpc
				if (args.length > 2) {

					if (args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("criar")) {
						int x = 1;
						String rank = args[1];
						String NomeNpc = "";

						for (int i = 0; i < args.length; i++) {
							if (i > x) {

								if (NomeNpc == "") {
									NomeNpc += args[i];
								} else {
									if (args[i].length() == (i + 1)) {
										NomeNpc += args[i];
									} else {
										NomeNpc += " " + args[i];
									}
								}
							}
						}

						/*
						 * Location loc = p.getLocation(); double xLoc =
						 * loc.getX(), yLoc= loc.getY(), zLoc= loc.getZ();
						 * 
						 * String world = loc.getWorld().getName(); String
						 * comando = "npc create "+NomeNpc
						 * +" --at "+xLoc+":"+yLoc+":"+zLoc+":"+world; Server s
						 * = Quests.main.getServer();
						 * s.dispatchCommand(s.getConsoleSender(), comando);
						 */

						NPCRegistry registry = CitizensAPI.getNPCRegistry();

						NPC npc = registry.createNPC(EntityType.PLAYER, NomeNpc);

						if (npc != null) {
							npc.spawn(p.getLocation());
							FilesConfig.getQuestsConfig().set(rank + ".IdNpc", npc.getId());
							FilesConfig.getQuestsConfig().set(rank + ".Quests.Permissao", "");
							FilesConfig.getQuestsConfig().set(rank + ".Quests.MatarMob", "");
							FilesConfig.getQuestsConfig().set(rank + ".Quests.EntregarItens", "");
							FilesConfig.saveQuestsConfig();
							
							return true;
						} else {
							p.sendMessage("§4[§5QUESTS§4]Erro ao criar o npc!");
							return false;
						}

					}

				} else {
					p.sendMessage("§4[§5QUESTS§4]Exemplo de comando para criar um npc! /qnpc create [NomeRank] [&5NomeNpc]");
					return false;
				}

			} else {
				p.sendMessage("§4[§5QUESTS§4]Você não tem permissão para usar este comando");
				return false;
			}
		} else {
			sender.sendMessage("§4[§5QUESTS§4]Este comando pode ser usado apenas em jogo!");
			return false;
		}
		return false;
	}
}
