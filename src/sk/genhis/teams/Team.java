package sk.genhis.teams;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import sk.genhis.teams.scheduler.ReloadNicks;

public final class Team {
	private final String name;
	private final Player owner;
	private final List<Player> players = new ArrayList<Player>();
	private final List<Player> invitedPlayers = new ArrayList<Player>();
	
	public Team(Player owner, String name) {
		this.owner = owner;
		this.players.add(owner);
		this.name = name;
	}
	
	public void sendMessage(String message) {
		Teams.getLog().log("[" + this.name + "]" + message);
		for(Player p : this.players)
			p.sendMessage(message);
		for(Player p : Teams.getChatspyPlayers())
			p.sendMessage(ChatColor.DARK_GRAY + "[" + this.name + "]" + message);
	}
	
	public boolean invitePlayer(Player p) {
		if(this.invitedPlayers.contains(p) || this.players.contains(p)) {
			this.owner.sendMessage(ChatColor.RED + Teams.getMessages().getString("player.team.invite_player_error"));
			return false;
		}
		this.invitedPlayers.add(p);
		p.sendMessage(ChatColor.AQUA + Teams.getMessages().getString("player.team.invite_player").replace("%owner", this.owner.getName()).
				replace("%team", this.name));
		this.owner.sendMessage(ChatColor.GREEN + Teams.getMessages().getString("player.team.invite_player_sender").replace("%player", p.getName()));
		return true;
	}
	
	public boolean uninvitePlayer(Player p) {
		if(!this.invitedPlayers.contains(p)) {
			this.owner.sendMessage(ChatColor.RED + Teams.getMessages().getString("player.team.uninvite_player_error"));
			return false;
		}
		this.invitedPlayers.remove(p);
		p.sendMessage(ChatColor.AQUA + Teams.getMessages().getString("player.team.uninvite_player").replace("%owner", this.owner.getName()).
				replace("%team", this.name));
		this.owner.sendMessage(ChatColor.GREEN + Teams.getMessages().getString("player.team.uninvite_player_sender").replace("%player", p.getName()));
		return true;
	}
	
	public boolean kickPlayer(Player p, boolean left) {
		if(p == this.owner)
			return Teams.disbandTeam(p, this.name);
		if(!this.players.contains(p)) {
			this.owner.sendMessage(ChatColor.RED + Teams.getMessages().getString("player.team.kick_player_error"));
			return false;
		}
		if(left)
			this.sendMessage(ChatColor.BLUE + Teams.getMessages().getString("player.team.player_left").replace("%player", p.getName()));
		else
			this.sendMessage(ChatColor.BLUE + Teams.getMessages().getString("player.team.kick_player_team").replace("%player", p.getName()));
		this.reloadNicks();
		p.setMetadata("team", new FixedMetadataValue(Teams.getPlugin(), "n"));
		p.setMetadata("teamchat", new FixedMetadataValue(Teams.getPlugin(), false));
		this.players.remove(p);
		return true;
	}
	
	public boolean joinPlayer(Player p) {
		if(!this.invitedPlayers.contains(p)) {
			p.sendMessage(ChatColor.RED + Teams.getMessages().getString("player.team.join_no_invitation"));
			return false;
		}
		if(p.getMetadata("team").get(0).asString() != "n") {
			p.sendMessage(ChatColor.RED + Teams.getMessages().getString("player.team.already_in_team"));
			return false;
		}
		this.invitedPlayers.remove(p);
		this.players.add(p);
		p.setMetadata("team", new FixedMetadataValue(Teams.getPlugin(), this.name));
		this.sendMessage(ChatColor.BLUE + Teams.getMessages().getString("player.team.join").replace("%player", p.getName()));
		this.reloadNicks();
		return true;
	}
	
	public void reloadNicks() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Teams.getPlugin(), new ReloadNicks(this.players), 30L);
	}
	
	public String getName() {
		return this.name;
	}
	
	public Player getOwner() {
		return this.owner;
	}
	
	public List<Player> getPlayers() {
		return this.players;
	}
	
	public List<Player> getInvitedPlayers() {
		return this.invitedPlayers;
	}
}
