package simpleauth.auth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RegisterCommand implements CommandExecutor {
    private final Auth plugin;

    public RegisterCommand(Auth plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("auth.register")) {
            player.sendMessage("You do not have permission to use this command.");
            return true;
        }
        if (args.length < 2) {
            player.sendMessage("Usage: /register <nickname> <password>");
            return true;
        }
        String name = args[0];
        String password = args[1];
        plugin.getPlayerDataMap().put(name, new Auth.PlayerData(name, password));
        plugin.savePlayerData();
        player.sendMessage("Registration successful!");
        return true;
    }
}
