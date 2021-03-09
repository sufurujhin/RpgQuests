package danieldev.eventos;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import danieldev.main.Quests;
import danieldev.utils.Utils;
import danieldev.utils.FilesConfig;
import danieldev.utils.GMHook;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPCRegistry;

public class JogadorInterageNpc implements Listener {
	ArrayList<String> playerCooldown = new ArrayList<>();

	@EventHandler
	public void interagir(PlayerInteractEntityEvent evento) {

		if (evento != null) {
			if (evento.getRightClicked() != null) {
				if (evento.getRightClicked().getName() != null) {

					Iterable<NPCRegistry> registry = CitizensAPI.getNPCRegistries();
					if (registry.iterator().next().isNPC(evento.getRightClicked())) {
						int i = registry.iterator().next().getNPC(evento.getRightClicked()).getId();

						if (Quests.npcId.containsKey(i)) {
							Player p = evento.getPlayer();

							if (!playerCooldown.contains(p.getName())) {

								playerCooldown.add(p.getName());

								evento.setCancelled(true);
								
								if (p.getInventory().getItemInMainHand() != null) {
									HashMap<String[], ItemStack> listaItensQuest = new HashMap<>();

									if (Utils.getStatusProgresso(p)) {
										// Pega a lista de itens que são
										// necessarios para quest.
										
										listaItensQuest = Utils.getItensProgresso(p);
										// captura o iten na mao do jogador.
										ItemStack itemNaMao = new ItemStack(p.getInventory().getItemInMainHand());

										boolean verificar = false;
										if (itemNaMao != null) {

											itemNaMao.setAmount(1);
											for (String[] path : listaItensQuest.keySet()) {

												ItemStack item = listaItensQuest.get(path);

												if (item == null) {
													continue;

												}

												if (item.equals(itemNaMao) && FilesConfig.getPlayersConfig().getString(path[4] + ".tipo").equalsIgnoreCase("entrega")) {
													// 0=Players.player - 1=
													// nomeQuest - 2= idItem -
													// 3=
													// StatusItemEntregue - 4=
													// Players.player.listaProgresso.nomeQuest.itens.idItem

													if (!FilesConfig.getPlayersConfig().getBoolean(path[0] + ".status")) {

														if (!FilesConfig.getPlayersConfig().getBoolean(path[4] + ".status")) {
															// 0=Players.player
															// - 1=
															// nomeQuest - 2=
															// idItem
															// - 3=
															// StatusItemEntregue
															// -
															// 4=
															// Players.player.listaProgresso.nomeQuest.itens.idItem

															int maoQtd = p.getInventory().getItemInMainHand().getAmount();
															int qtdNecessaria = FilesConfig.getPlayersConfig().getInt(path[4] + ".qtd");
															int qtdprogresso = FilesConfig.getPlayersConfig().getInt(path[4] + ".progresso");
															FileConfiguration config = Quests.main.getConfig();

															if ((maoQtd + qtdprogresso) < qtdNecessaria) {
																qtdprogresso += maoQtd;

																p.getInventory().getItemInMainHand().setAmount(0);
																FilesConfig.getPlayersConfig().set(path[4] + ".progresso", qtdprogresso);
																FilesConfig.savePlayersConfig();
																Utils.sendMsg(p, path[2], "x" + maoQtd, config.getString("Mensagens.MaterialEntregues"));
																Utils.calcular(p, path);
																verificar = true;

																break;
															} else {

																int resto = (maoQtd + qtdprogresso) - qtdNecessaria;
																int qtd = qtdNecessaria - qtdprogresso;

																p.getInventory().getItemInMainHand().setAmount(resto);
																FilesConfig.getPlayersConfig().set(path[4] + ".status", true);
																FilesConfig.getPlayersConfig().set(path[4] + ".progresso", qtdNecessaria);
																FilesConfig.savePlayersConfig();

																Utils.sendMsg(p, path[2], "x" + qtd, config.getString("Mensagens.MaterialEntregues"));
																Utils.calcular(p, path);
																verificar = true;
																break;
															}

														}

													}

												}

											}
											if (!verificar) {
												Inventory inv = Utils.openInventoryQuest(p);
												if(FilesConfig.getPlayersConfig().getString("Players."+(p.getName().toUpperCase())+".permissao").length() < 1){
													Utils.setPermissao(p);
												}
												if (inv.getItem(9) == null) {
													p.sendMessage(Quests.main.getConfig().getString("Mensagens.NaoHaQuestsDisponivel").replaceAll("&", "§"));
												}
												p.openInventory(inv);
												return;
											}
										}
										if (!verificar) {
											Inventory inv = Utils.openInventoryQuest(p);
											if(FilesConfig.getPlayersConfig().getString("Players."+(p.getName().toUpperCase())+".permissao").length() < 1){
												Utils.setPermissao(p);
											}
											if (inv.getItem(9) == null) {

												p.sendMessage(Quests.main.getConfig().getString("Mensagens.NaoHaQuestsDisponivel").replaceAll("&", "§"));
											}
											p.openInventory(inv);
											return;
										}
									} else {
										Inventory inv = Utils.openInventoryQuest(p);
										if(FilesConfig.getPlayersConfig().getString("Players."+(p.getName().toUpperCase())+".permissao").length() < 1){
											Utils.setPermissao(p);
										}
										if (inv.getItem(9) == null) {
										
											p.sendMessage(Quests.main.getConfig().getString("Mensagens.NaoHaQuestsDisponivel").replaceAll("&", "§"));
										}
										p.openInventory(inv);
										return;
									}
								} else {
									Inventory inv = Utils.openInventoryQuest(p);
									if(FilesConfig.getPlayersConfig().getString("Players."+(p.getName().toUpperCase())+".permissao").length() < 1){
										Utils.setPermissao(p);
									}
									if (inv.getItem(9) == null) {
										p.sendMessage(Quests.main.getConfig().getString("Mensagens.NaoHaQuestsDisponivel").replaceAll("&", "§"));
									}
									p.openInventory(inv);
									return;
								}
							}
							new BukkitRunnable() {

								@Override
								public void run() {

									playerCooldown.remove(p.getName());
								}

							}.runTaskLater(Quests.main, 2 * 20);

						}
					}

				}
			}
		}
	}

}
