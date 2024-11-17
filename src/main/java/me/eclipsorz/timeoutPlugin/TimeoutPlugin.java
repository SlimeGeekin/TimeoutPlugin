package me.eclipsorz.timeoutPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TimeoutPlugin extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        this.getCommand("timeout").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if the sender has permission
        if (!sender.hasPermission("timeoutplugin.use")) {
            sender.sendMessage("You do not have permission to use this command.");
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /timeout <player> <time in minutes>");
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage("Player not found!");
            return true;
        }

        try {
            int timeoutMinutes = Integer.parseInt(args[1]);
            UUID playerUUID = player.getUniqueId();
            player.kickPlayer("You have been timed out for " + timeoutMinutes + " minutes.");
            Bukkit.getBanList(org.bukkit.BanList.Type.NAME).addBan(player.getName(), "Temporary ban", null, sender.getName());

            // Schedule unban
            Bukkit.getScheduler().runTaskLater(this, () -> {
                Bukkit.getBanList(org.bukkit.BanList.Type.NAME).pardon(player.getName());
                sender.sendMessage("Player " + player.getName() + " has been unbanned.");
            }, timeoutMinutes * 60L * 20L);  // Convert minutes to ticks (1 sec = 20 ticks)

            sender.sendMessage("Player " + player.getName() + " has been banned for " + timeoutMinutes + " minutes.");

        } catch (NumberFormatException e) {
            sender.sendMessage("Invalid time format. Please provide the time in minutes.");
        }

        return true;
    }
}