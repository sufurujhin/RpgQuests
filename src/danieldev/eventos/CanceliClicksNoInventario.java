package danieldev.eventos;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import danieldev.main.Quests;
import danieldev.utils.Utils;
import danieldev.utils.FilesConfig;

public class CanceliClicksNoInventario implements Listener {

	@EventHandler
	public void clicks(PlayerDropItemEvent e) {

		Player p = e.getPlayer();

		if (p.getOpenInventory().getTopInventory().getTitle().equals(Quests.invetarioQuest().getTitle())) {
			Inventory inv = Utils.addQuests(p);

			p.openInventory(inv);
			if (inv.contains(e.getItemDrop().getItemStack())) {
				e.getItemDrop().getItemStack().setAmount(0);
			}
			e.setCancelled(true);
			return;
		}
		if (p.getOpenInventory().getTopInventory().getTitle().equals(Utils.getConfimation(null).getTitle())) {

			e.getItemDrop().getItemStack().setAmount(0);
			e.setCancelled(true);
			p.closeInventory();
			return;
		}
	}

	@EventHandler
	public void InteractBlock(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if (event.getClickedInventory() != null) {
			if (p.getOpenInventory().getTitle().equals(Quests.invetarioQuest().getTitle())) {

				if ((event.isLeftClick() | event.isRightClick()) & event.getInventory().getTitle().equals(Quests.invetarioQuest().getTitle())) {
					event.setCancelled(true);
					if (event.getCurrentItem() != null) {

						if (Quests.itensQuests.containsKey(event.getCurrentItem())) {
							p.openInventory(Utils.getConfimation(event.getCurrentItem()));
						}

					}

				}
				event.setCancelled(true);
				return;
			}

			if (p.getOpenInventory().getTitle().equals(Utils.getConfimation(null).getTitle())) {
				if (event.getCurrentItem().equals(Utils.getSim()) && event.getSlot() == 3) {

					event.setCancelled(true);

					List<String> itens = Quests.itensQuests.get(event.getClickedInventory().getItem(0));
					String string = getString(event.getClickedInventory().getItem(0).getItemMeta().getDisplayName(), null);
					String[] path2 = string.split(" ");
					String string2 = "";
					for (int i = 0; i < path2.length; i++) {
						string2 += path2[i];
					}

					FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + string2 + ".quest", event.getClickedInventory().getItem(0));
					FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + string2 + ".status", false);

					FilesConfig.savePlayersConfig();
					p.closeInventory();
					p.sendMessage(Quests.main.getConfig().getString("Mensagens.AceitarAMissao").replaceAll("&", "§").replaceAll("<quest>", event.getClickedInventory().getItem(0).getItemMeta().getDisplayName()));

					for (String s : itens) {
						String[] path = s.split(":");
						String id = path[1].toUpperCase();
						int qtd = Integer.parseInt(path[2]);
						if (Quests.tipoQuests.get(event.getClickedInventory().getItem(0)).get(path[1]).equalsIgnoreCase("QuebrarBlocos")) {
							if(FilesConfig.getQuebrar() ==null){
								FilesConfig.reloadQuebrar();
							}
							
							FilesConfig.getQuebrar().set("JogadoresQuebra." + (p.getName().toUpperCase()) + "." + id + ".nome",  path[3]);
							FilesConfig.getQuebrar().set("JogadoresQuebra." + (p.getName().toUpperCase()) + "." + id + ".progresso", 0);
							FilesConfig.getQuebrar().set("JogadoresQuebra." + (p.getName().toUpperCase()) + "." + id + ".qtd", qtd);
							FilesConfig.getQuebrar().set("JogadoresQuebra." + (p.getName().toUpperCase()) + "." + id + ".path", (p.getName().toUpperCase()) + "=listaProgresso=" + string2 + "=itens=" + path[1]);
							FilesConfig.saveQuebrar();
							
						}

						FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + string2 + ".itens." + path[1] + ".tipo", Quests.tipoQuests.get(event.getClickedInventory().getItem(0)).get(path[1]));
						FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + string2 + ".itens." + path[1] + ".status", false);
						FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + string2 + ".itens." + path[1] + ".qtd", qtd);
						FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + string2 + ".itens." + path[1] + ".nome", path[3]);
						FilesConfig.getPlayersConfig().set("Players." + (p.getName().toUpperCase()) + ".listaProgresso." + string2 + ".itens." + path[1] + ".progresso", 0);

						FilesConfig.savePlayersConfig();
					}
					p.openInventory(Utils.openInventoryQuest(p));
					return;
				}
				if (event.getCurrentItem().equals(Utils.getNao()) && event.getSlot() == 5) {
					p.sendMessage(Quests.main.getConfig().getString("Mensagens.RecusouAMissao").replaceAll("&", "§").replaceAll("<quest>", event.getClickedInventory().getItem(0).getItemMeta().getDisplayName()));
					event.setCancelled(true);
					p.closeInventory();
					return;
				}
				event.setCancelled(true);
			}
		}

	}

	public static String getString(String string, String space) {
		if (space == "" || space == null) {
			return string.trim().replaceAll("§1", "").replaceAll("§2", "").replaceAll("§3", "").replaceAll("§4", "").replaceAll("§5", "").replaceAll("§6", "").replaceAll("§7", "").replaceAll("§8", "").replaceAll("§9", "").replaceAll("§a", "").replaceAll("§b", "").replaceAll("§c", "").replaceAll("§d", "").replaceAll("§e", "").replaceAll("§f", "").replaceAll("§l", "");

		} else {
			return string.replaceAll("\\s+", " ").replaceAll("§1", "").replaceAll("§2", "").replaceAll("§3", "").replaceAll("§4", "").replaceAll("§5", "").replaceAll("§6", "").replaceAll("§7", "").replaceAll("§8", "").replaceAll("§9", "").replaceAll("§a", "").replaceAll("§b", "").replaceAll("§c", "").replaceAll("§d", "").replaceAll("§e", "").replaceAll("§f", "").replaceAll("§l", "");

		}
	}
}
