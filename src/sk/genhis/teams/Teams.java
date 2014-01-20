package sk.genhis.teams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import sk.genhis.glib.LicenceChecker;
import sk.genhis.glib.Logger;
import sk.genhis.glib.configuration.Config;
import sk.genhis.glib.plugin.GPlugin;
import sk.genhis.teams.command.*;
import sk.genhis.teams.listener.*;

public final class Teams extends GPlugin {
	private static JavaPlugin plugin;
	private static Logger logger;
	private static FileConfiguration messages;
	
	private static final Map<String, Team> teams = new HashMap<String, Team>();
	private static final List<Player> chatspyPlayers = new ArrayList<Player>();
	
	protected void enable() {
		Teams.plugin = this;
		Teams.logger = new Logger("[" + this.getDescription().getName() + "]");
		Teams.logger.log("Setting main variables");
		Teams.messages = new Config(this, "lang").getConfig();
		
		LicenceChecker c = new LicenceChecker(this);
		if(!c.checkLicence()) {
			c.unlicenced();
			return;
		}
		
		for(Player p : Bukkit.getOnlinePlayers()) {
			p.setMetadata("team", new FixedMetadataValue(this, "n"));
			p.setMetadata("teamchat", new FixedMetadataValue(this, false));
		}
		
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
		
		this.getCommand("team").setExecutor(new TeamCMD());
	}
	
	protected void disable() {
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		String msg = ChatColor.RED + "Internal Server Error: Command " + ChatColor.YELLOW + cmd.getName();
		Teams.logger.log(ChatColor.RESET + msg, Level.WARNING);
		sender.sendMessage(msg);
		return true;
	}
	
	public static boolean createTeam(Player owner, String team) {
		if(Teams.teams.containsKey(team)) {
			owner.sendMessage(ChatColor.RED + Teams.messages.getString("player.team.team_already_exists"));
			return false;
		}
		if(team.length() <= 3) {
			owner.sendMessage(ChatColor.RED + Teams.messages.getString("player.team.too_short_name"));
			return false;
		}
		if(owner.getMetadata("team").get(0).asString() != "n") {
			owner.sendMessage(ChatColor.RED + Teams.messages.getString("player.team.already_in_team"));
			return false;
		}
		Teams.teams.put(team, new Team(owner, team));
		owner.setMetadata("team", new FixedMetadataValue(Teams.plugin, team));
		owner.sendMessage(ChatColor.GREEN + Teams.messages.getString("player.team.create_success"));
		return true;
	}
	
	public static boolean disbandTeam(CommandSender sender, String team) {
		if(!Teams.teams.containsKey(team)) {
			sender.sendMessage(ChatColor.RED + Teams.messages.getString("player.team.no_team"));
			return false;
		}
		Team t = Teams.teams.get(team);
		if(!sender.hasPermission("teams.team.disband.other") && ((Player)sender) != t.getOwner()) {
			sender.sendMessage(ChatColor.RED + Teams.messages.getString("player.team.no_owner"));
			return false;
		}
		t.reloadNicks();
		t.sendMessage(ChatColor.BLUE + Teams.messages.getString("player.team.disband"));
		for(Player p : t.getPlayers()) {
			p.setMetadata("team", new FixedMetadataValue(Teams.plugin, "n"));
			p.setMetadata("teamchat", new FixedMetadataValue(Teams.plugin, false));
		}
		Teams.teams.remove(team);
		return true;
	}
	
	public static boolean infoTeam(CommandSender sender, String team) {
		if(!Teams.teams.containsKey(team)) {
			sender.sendMessage(ChatColor.RED + Teams.messages.getString("player.team.no_team"));
			return false;
		}
		final Team t = Teams.teams.get(team);
		sender.sendMessage(ChatColor.GOLD + "TeamInfo: Team " + ChatColor.DARK_RED + t.getName());
		String output = "";
		int i = 0;
		for(Player p : t.getPlayers()) {
			if(i != 0)
				output += ", ";
			output += p.getName();
			i++;
		}
		sender.sendMessage(ChatColor.GOLD + "Owner: " + ChatColor.GREEN + t.getOwner().getName());
		sender.sendMessage(ChatColor.GOLD + "Players: " + ChatColor.GREEN + output);
		return true;
	}
	
	public static boolean addChatspyPlayer(Player p) {
		if(Teams.chatspyPlayers.contains(p)) {
			p.sendMessage(ChatColor.RED + Teams.messages.getString("player.team.player_already_chatspy"));
			return false;
		}
		p.sendMessage(ChatColor.GREEN + Teams.messages.getString("player.team.player_success_chatspy"));
		Teams.chatspyPlayers.add(p);
		return true;
	}
	
	public static boolean removeChatspyPlayer(Player p) {
		if(!Teams.chatspyPlayers.contains(p)) {
			p.sendMessage(ChatColor.RED + Teams.messages.getString("player.team.player_no_chatspy"));
			return false;
		}
		p.sendMessage(ChatColor.GREEN + Teams.messages.getString("player.team.player_disable_chatspy"));
		Teams.chatspyPlayers.remove(p);
		return true;
	}
	
	public static JavaPlugin getPlugin() {
		return Teams.plugin;
	}
	
	public static Logger getLog() {
		return Teams.logger;
	}
	
	public static FileConfiguration getMessages() {
		return Teams.messages;
	}
	
	public static Map<String, Team> getTeams() {
		return Teams.teams;
	}
	
	public static List<Player> getChatspyPlayers() {
		return Teams.chatspyPlayers;
	}
}
