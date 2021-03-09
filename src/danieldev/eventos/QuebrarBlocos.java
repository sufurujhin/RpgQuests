package danieldev.eventos;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import danieldev.main.Quests;
import danieldev.utils.Utils;
import danieldev.utils.Verificar;
import danieldev.utils.FilesConfig;

public class QuebrarBlocos implements Listener, Verificar {

	@EventHandler
	public void quebra(BlockBreakEvent evento) {

		if (evento instanceof Player) {
			return;
		}
		if (!Utils.getStatusProgresso(evento.getPlayer())) {
			return;
		}
		Player p = evento.getPlayer();

		if (Quests.player.size() == 0) {

			verificar(p, evento.getBlock().getType());
			return;
		} else {
			verificar(p, evento.getBlock().getType());
			return;
		}

	}

	@Override
	public void check(Player p, Material type) {

		if (FilesConfig.getQuebrar().getConfigurationSection("JogadoresQuebra").contains((p.getName().toUpperCase()))) {

			if (FilesConfig.getQuebrar().getConfigurationSection("JogadoresQuebra." + (p.getName().toUpperCase())).contains((type.name().toUpperCase()))) {

				String[] path = (FilesConfig.getQuebrar().getString("JogadoresQuebra." + (p.getName().toUpperCase()) + "." + (type.name().toUpperCase()) + ".path")).split("=");

				if (path.length != 5) {
					return;
				}

				if (!FilesConfig.getPlayersConfig().getBoolean("Players." + path[0] + "." + path[1] + "." + path[2] + "." + path[3] + "." + path[4] + ".status")) {

					int qtd = FilesConfig.getQuebrar().getInt("JogadoresQuebra." + (p.getName().toUpperCase()) + "." + type.name() + ".qtd");
					int progress = FilesConfig.getQuebrar().getInt("JogadoresQuebra." + (p.getName().toUpperCase()) + "." + type.name() + ".progresso");
					progress = (progress + Quests.player.get(p.getName() + type.name()));
					if (progress < qtd) {

						if (Quests.player.size() == 0) {
							send(p, qtd, progress, type, 1);

						} else {

							if (!Quests.player.containsKey(p.getName() + type.name())) {
								send(p, qtd, progress, type, 1);
								return;
							} else {

								FilesConfig.getPlayersConfig().set("Players." + path[0] + "." + path[1] + "." + path[2] + "." + path[3] + "." + path[4] + ".progresso", progress);
								FilesConfig.savePlayersConfig();
								FilesConfig.getQuebrar().set("JogadoresQuebra." + (p.getName().toUpperCase()) + "." + type.name() + ".progresso", progress);
								FilesConfig.saveQuebrar();

								send(p, qtd, progress, type, 1);
								return;
							}
						}
					} else {

						Utils.quebrarSet(p, qtd, progress, type, path);
						FilesConfig.getPlayersConfig().set("Players." + path[0] + "." + path[1] + "." + path[2] + "." + path[3] + "." + path[4] + ".progresso", progress);
						FilesConfig.savePlayersConfig();

						FilesConfig.getQuebrar().set("JogadoresQuebra." + (p.getName().toUpperCase()) + "." + (type.name().toUpperCase()) + ".path", "completado");
						FilesConfig.getQuebrar().set("JogadoresQuebra." + (p.getName().toUpperCase()) + "." + type.name() + ".progresso", progress);
						FilesConfig.saveQuebrar();
						send(p, qtd, qtd, type, 60);
						return;
					}
				}

			}
		}
	}

	@Override
	public void verificar(Player p, Material type) {

		if (FilesConfig.getQuebrar() == null) {
			FilesConfig.reloadQuebrar();
		}

		if (Quests.player != null) {

			if (Quests.player.containsKey(p.getName() + type.name())) {

				Quests.player.put((p.getName() + type.name()), (Quests.player.get(p.getName() + type.name()) + 1));

				int qtd = FilesConfig.getQuebrar().getInt("JogadoresQuebra." + (p.getName().toUpperCase()) + "." + type.name() + ".qtd");
				int progresso = FilesConfig.getQuebrar().getInt("JogadoresQuebra." + (p.getName().toUpperCase()) + "." + type.name() + ".progresso");

				if ((progresso + Quests.player.get(p.getName() + type.name())) >= qtd) {
					check(p, type);
					return;
				} else if (Quests.main.getConfig().getInt("ConfigQuebrarBlocos.msgDelay") <= Quests.player.get(p.getName() + type.name())) {

					check(p, type);
					return;
				}

				return;
			} else {
				Quests.player.put((p.getName() + type.name()), 1);

				check(p, type);
				return;
			}
		} else {

			Quests.player.put((p.getName() + type.name()), 1);
			check(p, type);
			return;
		}
	}

	public void send(Player p, int qtd, int progress, Material type, int cont) {

		String nome = FilesConfig.getQuebrar().getString("JogadoresQuebra." + (p.getName().toUpperCase()) + "." + type.name() + ".nome");
		if (progress == 0) {
			progress = 1;
		}
		Utils.sendMsg(p, nome, "" + progress + "/" + qtd, Quests.main.getConfig().getString("Mensagens.QuebrarBlocos"));
		if (cont == 60) {
			Quests.player.remove((p.getName() + type.name()));
		} else {
			if (progress >= qtd) {
				Quests.player.put((p.getName() + type.name()), 0);
			} else {
				Quests.player.put((p.getName() + type.name()), cont);
			}

		}

		return;
	}

}
