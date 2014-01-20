package sk.genhis.teams.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import sk.genhis.teams.Teams;

public final class TeamCMD implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0)
			return false;
		if(args[0].equalsIgnoreCase("help") && sender.hasPermission("teams.team.help")) {
			if(args.length > 1)
				return this.help(sender, Integer.parseInt(args[1]));
			else
				return this.help(sender, 1);
		}
		if(args[0].equalsIgnoreCase("info") && sender.hasPermission("teams.team.info.others") && args.length > 1)
			return this.info(sender, args[1]);
		if(args[0].equalsIgnoreCase("disband") && sender.hasPermission("teams.team.disband.others") && args.length > 1)
			return this.disband(sender, args[1]);
		if(!(sender instanceof Player)) {
			sender.sendMessage("This command is only for players!");
			return true;
		}
		final Player p = (Player)sender;
		if(args[0].equalsIgnoreCase("create") && p.hasPermission("teams.team.create") && args.length > 1)
			return this.create(p, args[1]);
		if(args[0].equalsIgnoreCase("info") && sender.hasPermission("teams.team.info"))
			return this.info(sender, p.getMetadata("team").get(0).asString());
		if(args[0].equalsIgnoreCase("disband") && p.hasPermission("teams.team.disband"))
			return this.disband(p, p.getMetadata("team").get(0).asString());
		if(args[0].equalsIgnoreCase("invite") && p.hasPermission("teams.team.invite") && args.length > 1)
			return this.invite(p, Bukkit.getPlayer(args[1]));
		if(args[0].equalsIgnoreCase("uninvite") && p.hasPermission("teams.team.uninvite") && args.length > 1)
			return this.uninvite(p, Bukkit.getPlayer(args[1]));
		if(args[0].equalsIgnoreCase("kick") && p.hasPermission("teams.team.kick") && args.length > 1)
			return this.kick(p, Bukkit.getPlayer(args[1]), false);
		if(args[0].equalsIgnoreCase("join") && p.hasPermission("teams.team.join") && args.length > 1)
			return this.join(p, args[1]);
		if(args[0].equalsIgnoreCase("leave") && p.hasPermission("teams.team.leave"))
			return this.kick(p, p, true);
		if(args[0].equalsIgnoreCase("chat") && p.hasPermission("teams.team.chat"))
			return this.chat(p);
		if(args[0].equalsIgnoreCase("chatspy") && p.hasPermission("teams.team.chatspy"))
			return this.chatspy(p);
		return false;
	}
	
	private boolean help(CommandSender sender, int page) {
		final int maxPage = 1;
		final int maxPageAdmin = 2;
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6Teams: Help for command &4/team: &7(page " + page + ")"));
		if(page == 1) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6/team create &4<name> &7- ") + Teams.getMessages().getString("player.team.help.create"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6/team disband &7- ") + Teams.getMessages().getString("player.team.help.disband"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6/team invite &4<player-name> &7- ") + Teams.getMessages().getString("player.team.help.invite"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6/team uninvite &4<player-name> &7- ") + Teams.getMessages().getString("player.team.help.uninvite"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6/team kick &4<player-name> &7- ") + Teams.getMessages().getString("player.team.help.kick"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6/team join &4<team-name> &7- ") + Teams.getMessages().getString("player.team.help.join"));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6/team chat &7- ") + Teams.getMessages().getString("player.team.help.chat"));
		}
		if(page == 2 && sender.hasPermission("teams.team.help.admin")) {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6/team disband &e[name] &7- ") + Teams.getMessages().getString("player.team.help.admin.disband"));
		}
		if(page < maxPage)
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6/team help &4" + (page + 1) + " &7-") + Teams.getMessages().getString("player.team.help.next_page"));
		else if(page < maxPageAdmin && sender.hasPermission("teams.team.help.admin"))
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6/team help &4" + (page + 1) + " &7-") + Teams.getMessages().getString("player.team.help.next_page"));
		return true;
	}
	
	private boolean disband(CommandSender sender, String name) {
		Teams.disbandTeam(sender, name);
		return true;
	}
	
	private boolean create(Player p, String name) {
		Teams.createTeam(p, name);
		return true;
	}
	
	private boolean info(CommandSender sender, String team) {
		Teams.infoTeam(sender, team);
		return true;
	}
	
	private boolean invite(Player p, Player ip) {
		final String name = p.getMetadata("team").get(0).asString();
		if(ip == null)
			p.sendMessage(ChatColor.RED + Teams.getMessages().getString("player.team.player_not_online"));
		else if(!Teams.getTeams().containsKey(name))
			p.sendMessage(ChatColor.RED + Teams.getMessages().getString("player.team.no_team"));
		else
			Teams.getTeams().get(name).invitePlayer(ip);
		return true;
	}
	
	private boolean uninvite(Player p, Player ip) {
		final String name = p.getMetadata("team").get(0).asString();
		if(ip == null)
			p.sendMessage(ChatColor.RED + Teams.getMessages().getString("player.team.player_not_online"));
		else if(!Teams.getTeams().containsKey(name))
			p.sendMessage(ChatColor.RED + Teams.getMessages().getString("player.team.no_team"));
		else
			Teams.getTeams().get(name).uninvitePlayer(ip);
		return true;
	}
	
	private boolean kick(Player p, Player ip, boolean leave) {
		final String name = p.getMetadata("team").get(0).asString();
		if(ip == null)
			p.sendMessage(ChatColor.RED + Teams.getMessages().getString("player.team.player_not_online"));
		else if(!Teams.getTeams().containsKey(name))
			p.sendMessage(ChatColor.RED + Teams.getMessages().getString("player.team.no_team"));
		else
			Teams.getTeams().get(name).kickPlayer(ip, leave);
		return true;
	}
	
	private boolean join(Player p, String team) {
		if(!Teams.getTeams().containsKey(team))
			p.sendMessage(ChatColor.RED + Teams.getMessages().getString("player.team.no_team"));
		else
			Teams.getTeams().get(team).joinPlayer(p);
		return true;
	}
	
	private boolean chat(Player p) {
		final String name = p.getMetadata("team").get(0).asString();
		final boolean status = p.getMetadata("teamchat").get(0).asBoolean();
		if(!Teams.getTeams().containsKey(name))
			p.sendMessage(ChatColor.RED + Teams.getMessages().getString("player.team.no_team"));
		else {
			p.setMetadata("teamchat", new FixedMetadataValue(Teams.getPlugin(), !status));
			p.sendMessage(ChatColor.GREEN + Teams.getMessages().getString("player.team.chat." + !status));
		}
		return true;
	}
	
	private boolean chatspy(Player p) {
		final boolean status = Teams.getChatspyPlayers().contains(p);
		if(status)
			Teams.removeChatspyPlayer(p);
		else
			Teams.addChatspyPlayer(p);
		return true;
	}
}
