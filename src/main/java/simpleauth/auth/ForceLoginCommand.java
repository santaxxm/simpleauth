package simpleauth.auth;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ForceLoginCommand implements CommandExecutor {
    private final Auth plugin;

    public ForceLoginCommand(Auth plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!sender.hasPermission("simpleauth.forcelogin")) {
            sender.sendMessage("You don't have permission to use this command.");
            return true;
        }
        if (args.length < 1) {
            sender.sendMessage("Usage: /forcelogin <player>");
            return true;
        }
        String playerName = args[0];
        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null) {
            sender.sendMessage("Player not found.");
            return true;
        }
        plugin.getLoggedInPlayers().put(targetPlayer.getName(), true);
        targetPlayer.sendMessage("You have been forced to log in.");
        sender.sendMessage("Player " + playerName + " has been forced to log in.");
        return true;
    }
}