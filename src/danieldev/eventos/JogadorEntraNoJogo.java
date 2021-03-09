package danieldev.eventos;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import danieldev.main.Quests;
import danieldev.utils.FilesConfig;
import danieldev.utils.Utils;

public class JogadorEntraNoJogo implements Listener {

	@EventHandler
	public void join(PlayerJoinEvent evento) {

		Utils.setJogador(evento.getPlayer(), 1, false, "");

	}
}
