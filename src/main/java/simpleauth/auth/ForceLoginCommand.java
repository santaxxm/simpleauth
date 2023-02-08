package simpleauth.auth;

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
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 1) {
            player.sendMessage("Usage: /forcelogin <nickname>");
            return true;
        }
        String name = args[0];
        plugin.getLoggedInPlayers().put(name, true);
        player.sendMessage("Login forced for player " + name + "!");
        return true;
    }
}