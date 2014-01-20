package sk.genhis.teams.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

import sk.genhis.teams.Team;
import sk.genhis.teams.Teams;

public final class PlayerListener implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().setMetadata("team", new FixedMetadataValue(Teams.getPlugin(), "n"));
		e.getPlayer().setMetadata("teamchat", new FixedMetadataValue(Teams.getPlugin(), false));
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		final Player p = e.getPlayer();
		final String team = p.getMetadata("team").get(0).asString();
		if(Teams.getTeams().containsKey(team))
			Teams.getTeams().get(team).kickPlayer(p, true);
	}
	
	@EventHandler
	public void onPlayerReceiveNameTag(PlayerReceiveNameTagEvent e) {
		final Player p = e.getPlayer();
		final Team t = Teams.getTeams().get(p.getMetadata("team").get(0).asString());
		if(t != null) {
			if(t.getPlayers().contains(e.getNamedPlayer()) && p.hasPermission("teams.team.colorednames"))
				e.setTag(ChatColor.GREEN + e.getTag());
		}
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		final Player p = e.getPlayer();
		if(p.getMetadata("teamchat").get(0).asBoolean()) {
			Teams.getTeams().get(p.getMetadata("team").get(0).asString()).sendMessage(ChatColor.AQUA + "(TeamChat) <" +
					p.getDisplayName() + ChatColor.AQUA + "> " + e.getMessage());
			e.setCancelled(true);
		}
	}
}
