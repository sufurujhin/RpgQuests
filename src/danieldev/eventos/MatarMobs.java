package danieldev.eventos;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.mysql.fabric.xmlrpc.base.Array;

import danieldev.main.Quests;
import danieldev.utils.Utils;
import danieldev.utils.FilesConfig;

public class MatarMobs implements Listener {

	@EventHandler
	public void mata(EntityDeathEvent evento) {

		if (!(evento instanceof Player)) {

			if (evento == null) {
				
				return;
			}
			if (evento.getEntity().getKiller() == null) {
				
				return;
			}
			if (evento.getEntity().getKiller().getType() != EntityType.PLAYER) {
				
				return;
			}
			if (evento.getEntity() == null) {
				
				return;
			}
			Player p = evento.getEntity().getKiller();

			EntityType entity = evento.getEntity().getType();
			
			
			if (Utils.getStatusProgresso(p)) {
								
					HashMap<String[], EntityType> lista = new HashMap<>();
					lista = Utils.getMobsProgresso(p);
					
					if (lista != null) {
					if (lista.values().contains(entity)) {
						
						
						for (String[] path : lista.keySet()) {

							
							
							if (lista.get(path) == entity) {
								int progresso = FilesConfig.getPlayersConfig().getInt(path[4] + ".progresso") + 1;
								int qtd = FilesConfig.getPlayersConfig().getInt(path[4] + ".qtd");
								FileConfiguration config = Quests.main.getConfig();

								if (progresso >= qtd) {
									FilesConfig.getPlayersConfig().set(path[4] + ".status", true);
									FilesConfig.getPlayersConfig().set(path[4] + ".progresso", progresso);
									FilesConfig.savePlayersConfig();

									Utils.sendMsg(p, path[2], "" + progresso + "/" + qtd, config.getString("Mensagens.MatouMobs"));
									Utils.calcular(p, path);
									return;
								}
								if (progresso < qtd) {
									
									FilesConfig.getPlayersConfig().set(path[4] + ".progresso", progresso);
									FilesConfig.savePlayersConfig();
									if (Quests.player == null) {
										Utils.sendMsg(p, path[2], "" + progresso + "/" + qtd, config.getString("Mensagens.MatouMobs"));
										Quests.player.put(p.getName()+entity.name(), 1);
									} else {

										if (!Quests.player.containsKey(p.getName()+entity.name())) {

											Utils.sendMsg(p, path[2], "" + progresso + "/" + qtd, config.getString("Mensagens.MatouMobs"));
											Quests.player.put(p.getName()+entity.name(), 0);
											Quests.playerIds.put(p, entity.name());
										} else {
											
											if(Quests.playerIds.containsValue(entity.name())){
												Quests.player.put(p.getName()+entity.name(), Quests.player.get(p.getName()+entity.name()) + 1);
											}else{
												Quests.player.put(p.getName()+entity.name(), 0);
												Quests.playerIds.put(p, entity.name());
												Utils.sendMsg(p, path[2], "" + progresso + "/" + qtd, config.getString("Mensagens.MatouMobs"));
											
											}
											
											if (Quests.main.getConfig().getInt("ConfigMatarMobs.msgDelay") == Quests.player.get(p.getName()+entity.name())) {

												Utils.sendMsg(p, path[2], "" + progresso + "/" + qtd, config.getString("Mensagens.MatouMobs"));
												Quests.player.put(p.getName()+entity.name(), 0);
												Quests.playerIds.put(p, entity.name());
											}
										}
									}

									
									
									
									Utils.calcular(p, path);
									return;
								}
								break;
							}
							
						}
					}
					
					return;
				}
				return;
			}

			return;
		}
	}
}
