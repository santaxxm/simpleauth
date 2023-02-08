package simpleauth.auth;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class LoginCommand implements CommandExecutor {
    private final Auth plugin;

    public LoginCommand(Auth plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }
        Player player = (Player) sender;
        if (args.length < 2) {
            player.sendMessage("Usage: /login <nickname> <password>");
            return true;
        }
        String name = args[0];
        String password = args[1];
        Auth.PlayerData playerData = plugin.getPlayerDataMap().get(name);
        if (playerData == null || !password.equals(playerData.getPassword())) {
            player.sendMessage("Login failed. Incorrect nickname or password.");
            return true;
        }
        plugin.getLoggedInPlayers().put(player.getName(), true);
        player.sendMessage("Login successful!");
        return true;
    }
}
