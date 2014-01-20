package sk.genhis.teams.scheduler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

public final class ReloadNicks implements Runnable {
	private List<Player> players;
	
	public ReloadNicks(List<Player> players) {
		this.players = players;
	}
	
	public void run() {
		Set<Player> set = new HashSet<Player>(this.players);
		for(Player p : this.players)
			TagAPI.refreshPlayer(p, set);
	}
}
