package net.custommoney.cmd;

import net.custommoney.CustomMoney;
import net.custommoney.process.Eeconomy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Money implements CommandExecutor {
    private final CustomMoney plugin;
    private final Eeconomy economy;
    public Money(CustomMoney plugin) {
        this.plugin = plugin;
        this.economy = new Eeconomy(plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("custommoney.commands")) {
            sender.sendMessage(ChatColor.DARK_RED + "You have no permissions!");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage("Use: /money <give/set/take> <sum> <nick>");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid amount!");
            return true;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[2]);
        if (target == null || !target.hasPlayedBefore()) {
            sender.sendMessage(ChatColor.RED + "Player " + args[2] + " not found!");
            return true;
        }
        String action = args[0].toLowerCase();
        EconomyResponse response;

        switch (action) {
            case "give":
                response = economy.depositPlayer(target, amount);
                if (response.transactionSuccess()) {
                    sender.sendMessage("You give " + ChatColor.YELLOW + amount + ChatColor.RESET + " coins for player " + ChatColor.GREEN + target.getName());
                } else {sender.sendMessage(ChatColor.RED + response.errorMessage);}
                break;

            case "set":
                double currentBalance = economy.getBalance(target);
                if (currentBalance > amount) {response = economy.withdrawPlayer(target, currentBalance - amount);} else {response = economy.depositPlayer(target, amount - currentBalance);}

                if (response.transactionSuccess()) {
                    sender.sendMessage("You set " + ChatColor.YELLOW + amount + ChatColor.RESET + " coins for player " + ChatColor.GREEN + target.getName());
                } else {sender.sendMessage(ChatColor.RED + response.errorMessage);}
                break;

            case "take":
                response = economy.withdrawPlayer(target, amount);
                if (response.transactionSuccess()) {
                    sender.sendMessage("You take " + ChatColor.YELLOW + amount + ChatColor.RESET + " coins for player " + ChatColor.GREEN + target.getName());
                } else {sender.sendMessage(ChatColor.RED + response.errorMessage);}
                break;

            default:
                sender.sendMessage(ChatColor.DARK_RED + "Unknown sub-command!");
                return true;
        }
        plugin.saveData();
        return true;
    }
}