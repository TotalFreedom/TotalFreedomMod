package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.config.ConfigEntry;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.shop.ShopData;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Manage the shop", usage = "/<command> <coins: <add | set | remove> <amount> <player | all>>", aliases = "ms")
public class Command_manageshop extends FreedomCommand
{
    @Override
    public boolean run(final CommandSender sender, final Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!ConfigEntry.SHOP_ENABLED.getBoolean())
        {
            msg("The shop is currently disabled!", ChatColor.RED);
            return true;
        }
        if (!FUtil.isExecutive(sender.getName()) && !sender.getName().equals("CONSOLE"))
        {
            msg("Only executives can use this command!", ChatColor.RED);
            return true;
        }
        final String prefix = FUtil.colorize(ConfigEntry.SHOP_PREFIX.getString() + " ");
        if (args.length > 3)
        {
            if (args[0].equalsIgnoreCase("coins"))
            {
                if (getPlayer(args[3]) != null || args[3].equals("all"))
                {   
                    Player p = null;
                    ShopData sd = null;
                    if (!args[3].equals("all"))
                    {
                        p = getPlayer(args[3]);
                        sd = plugin.sh.getData(p);
                    }
                    int newAmount;
                    int num;
                    switch (args[1])
                    {
                        case "add":
                            try
                            {
                                num = Math.max(0, Math.min(1000000, Integer.parseInt(args[2])));
                                if (!args[3].equals("all"))
                                {
                                    newAmount = sd.getCoins() + num;
                                    sd.setCoins(newAmount);
                                    plugin.sh.save(sd);
                                    msg(prefix + ChatColor.GREEN + "Gave " + ChatColor.RED + args[2] + ChatColor.GREEN + " coins to " + p.getName() + ", " + p.getName() + " now has " + ChatColor.RED + sd.getCoins() + ChatColor.GREEN + " coins.");
                                    p.sendMessage(prefix + ChatColor.GREEN + sender.getName() + " gave you " + ChatColor.RED + args[2] + ChatColor.GREEN + " coins, you now have " + ChatColor.RED + sd.getCoins() + ChatColor.GREEN + " coins.");
                                    return true;
                                }
                                else
                                {
                                    for (Player player : server.getOnlinePlayers())
                                    {
                                        sd = plugin.sh.getData(player);
                                        newAmount = sd.getCoins() + num;
                                        sd.setCoins(newAmount);
                                        plugin.sh.save(sd);
                                        player.sendMessage(prefix + ChatColor.GREEN + sender.getName() + " gave you " + ChatColor.RED + args[2] + ChatColor.GREEN + " coins, you now have " + ChatColor.RED + sd.getCoins() + ChatColor.GREEN + " coins.");
                                    }
                                    msg(prefix + ChatColor.GREEN + "Gave " + ChatColor.RED + args[2] + ChatColor.GREEN + " coins to everyone.");
                                    return true;
                                }
                            }
                            catch (NumberFormatException ex)
                            {
                                msg("Invalid number: " + args[2], ChatColor.RED);
                                return true;
                            }
                        case "set":
                            try
                            {
                                newAmount = Math.max(0, Math.min(1000000, Integer.parseInt(args[2])));
                                if (!args[3].equals("all"))
                                {
                                    sd.setCoins(newAmount);
                                    plugin.sh.save(sd);
                                    msg(prefix + ChatColor.GREEN + "Set " + p.getName() + "'s coin amount to " + ChatColor.RED + newAmount + ChatColor.GREEN + ".");
                                    p.sendMessage(prefix + ChatColor.GREEN + sender.getName() + " set your coin amount to " + args[2] + ChatColor.GREEN + ".");
                                    return true;
                                }
                                else
                                {
                                    newAmount = Math.max(0, Math.min(1000000, Integer.parseInt(args[2])));
                                    for (Player player : server.getOnlinePlayers())
                                    {
                                        sd = plugin.sh.getData(player);
                                        sd.setCoins(newAmount);
                                        plugin.sh.save(sd);
                                        player.sendMessage(prefix + ChatColor.GREEN + sender.getName() + " set your coin amount to " + args[2] + ChatColor.GREEN + ".");
                                    }
                                    msg(prefix + ChatColor.GREEN + "Set everyones's coin amount to " + ChatColor.RED + newAmount + ChatColor.GREEN + ".");
                                    return true;
                                }
                            }
                            catch (NumberFormatException ex)
                            {
                                msg("Invalid number: " + args[2], ChatColor.RED);
                                return true;
                            }
                        case "remove":
                            try
                            {
                                num = Math.max(0, Math.min(1000000, Integer.parseInt(args[2])));
                                if (!args[3].equals("all"))
                                {
                                    if (num > sd.getCoins())
                                    {
                                        msg(prefix + "You can't give a player a negative amount of coins, I'm sorry, you can't put anyone in debt.", ChatColor.RED);
                                        return true;
                                    }
                                    newAmount = sd.getCoins() - num;
                                    sd.setCoins(newAmount);
                                    plugin.sh.save(sd);
                                    msg(prefix + ChatColor.GREEN + "Took " + ChatColor.RED + args[2] + ChatColor.GREEN + " coins from " + p.getName() + ", " + p.getName() + " now has " + ChatColor.RED + sd.getCoins() + ChatColor.GREEN + " coins.");
                                    p.sendMessage(prefix + ChatColor.GREEN + sender.getName() + " took " + ChatColor.RED + args[2] + ChatColor.GREEN + " coins from you, you now have " + ChatColor.RED + sd.getCoins() + ChatColor.GREEN + " coins.");
                                    return true;
                                }
                                else
                                {
                                    for (Player player : server.getOnlinePlayers())
                                    {
                                        sd = plugin.sh.getData(player);
                                        if (num > sd.getCoins())
                                        {
                                            sd.setCoins(0);
                                        }
                                        newAmount = sd.getCoins() - num;
                                        sd.setCoins(newAmount);
                                        plugin.sh.save(sd);
                                        player.sendMessage(prefix + ChatColor.GREEN + sender.getName() + " took " + ChatColor.RED + args[2] + ChatColor.GREEN + " coins from you, you now have " + ChatColor.RED + sd.getCoins() + ChatColor.GREEN + " coins.");
                                    }
                                    msg(prefix + ChatColor.GREEN + "Took " + ChatColor.RED + args[2] + ChatColor.GREEN + " coins from everyone.");
                                    return true;
                                }
                            }
                            catch (NumberFormatException ex)
                            {
                                msg("Invalid number: " + args[2], ChatColor.RED);
                                return true;
                            }
                        default:
                            break;
                    }
                }
                else
                {
                    msg(FreedomCommand.PLAYER_NOT_FOUND);
                    return true;
                }
            }
        }
        return false;
    }

}
