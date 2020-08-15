package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.shop.ShopItem;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.ADMIN, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Manage the shop", usage = "/<command> <coins: <add | set | remove> <amount> <player | all> | items: <give | take> <item> <player>", aliases = "ms")
public class Command_manageshop extends FreedomCommand
{
    @Override
    public boolean run(final CommandSender sender, final Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (!ConfigEntry.SERVER_OWNERS.getStringList().contains(sender.getName()))
        {
            return noPerms();
        }

        if (args.length < 2)
        {
            return false;
        }
        if (args[0].equals("coins"))
        {
            if (args.length < 4)
            {
                return false;
            }
            switch (args[1])
            {

                case "add":
                    try
                    {
                        int amount = Math.max(0, Math.min(1000000, Integer.parseInt(args[2])));
                        if (!args[3].equals("all"))
                        {
                            PlayerData playerData = plugin.pl.getData(args[3]);
                            if (playerData == null)
                            {
                                msg(PLAYER_NOT_FOUND);
                                return true;
                            }
                            playerData.setCoins(playerData.getCoins() + amount);
                            plugin.pl.save(playerData);
                            msg("Successfully added " + amount + " coins to " + args[3] + ". Their new balance is " + playerData.getCoins(), ChatColor.GREEN);
                            Player player = getPlayer(args[3]);
                            if (player != null)
                            {
                                player.sendMessage(ChatColor.GREEN + sender.getName() + " gave you " + amount + " coins. Your new balance is " + playerData.getCoins());
                            }
                            return true;
                        }
                        else
                        {
                            for (Player player : server.getOnlinePlayers())
                            {
                                PlayerData playerData = plugin.pl.getData(player);
                                playerData.setCoins(playerData.getCoins() + amount);
                                plugin.pl.save(playerData);
                                player.sendMessage(ChatColor.GREEN + sender.getName() + " gave you " + amount + " coins. Your new balance is " + playerData.getCoins());
                            }
                            msg("Successfully added " + amount + " coins to all online players.", ChatColor.GREEN);
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
                        int amount = Math.max(0, Math.min(1000000, Integer.parseInt(args[2])));
                        if (!args[3].equals("all"))
                        {
                            PlayerData playerData = plugin.pl.getData(args[3]);
                            if (playerData == null)
                            {
                                msg(PLAYER_NOT_FOUND);
                                return true;
                            }
                            playerData.setCoins(playerData.getCoins() + amount);
                            if (playerData.getCoins() < 0)
                            {
                                playerData.setCoins(0);
                            }
                            plugin.pl.save(playerData);
                            msg("Successfully removed " + amount + " coins from " + args[3] + ". Their new balance is " + playerData.getCoins(), ChatColor.GREEN);
                            Player player = getPlayer(args[3]);
                            if (player != null)
                            {
                                player.sendMessage(ChatColor.RED + sender.getName() + " took " + amount + " coins from you. Your new balance is " + playerData.getCoins());
                            }
                            return true;
                        }
                        else
                        {
                            for (Player player : server.getOnlinePlayers())
                            {
                                PlayerData playerData = plugin.pl.getData(player);
                                playerData.setCoins(playerData.getCoins() - amount);
                                if (playerData.getCoins() < 0)
                                {
                                    playerData.setCoins(0);
                                }
                                plugin.pl.save(playerData);
                                player.sendMessage(ChatColor.RED + sender.getName() + " took " + amount + " coins from you. Your new balance is " + playerData.getCoins());
                            }
                            msg("Successfully took " + amount + " coins from all online players.", ChatColor.GREEN);
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
                        int amount = Math.max(0, Math.min(1000000, Integer.parseInt(args[2])));
                        PlayerData playerData = plugin.pl.getData(args[3]);
                        if (playerData == null)
                        {
                            msg(PLAYER_NOT_FOUND);
                            return true;
                        }
                        playerData.setCoins(amount);
                        plugin.pl.save(playerData);
                        msg("Successfully set " + args[3] + "'s coins to " + amount, ChatColor.GREEN);
                        Player player = getPlayer(args[3]);
                        if (player != null)
                        {
                            player.sendMessage(ChatColor.GREEN + sender.getName() + " set your coin balance to " + amount);
                        }
                        return true;
                    }
                    catch (NumberFormatException ex)
                    {
                        msg("Invalid number: " + args[2], ChatColor.RED);
                        return true;
                    }
            }
        }
        else if (args[0].equals("items"))
        {
            if (args[1].equals("list"))
            {
                msg("List of all shop items: " + StringUtils.join(ShopItem.values(), ", "));
                return true;
            }

            if (args.length < 4)
            {
                return false;
            }

            if (args[1].equals("give"))
            {
                ShopItem item = ShopItem.findItem(args[2].toUpperCase());
                if (item == null)
                {
                    msg(args[2] + " is not a valid item.", ChatColor.RED);
                    return true;
                }

                PlayerData playerData = plugin.pl.getData(args[3]);
                if (playerData == null)
                {
                    msg(PLAYER_NOT_FOUND);
                    return true;
                }
                playerData.giveItem(item);
                plugin.pl.save(playerData);
                msg("Successfully gave the " + item.getName() + " to " + args[3], ChatColor.GREEN);
                Player player = getPlayer(args[3]);
                if (player != null)
                {
                    player.sendMessage(ChatColor.GREEN + sender.getName() + " gave the " + item.getName() + " to you");
                }
                return true;
            }
            else if (args[1].equals("take"))
            {
                ShopItem item = ShopItem.findItem(args[2].toUpperCase());
                if (item == null)
                {
                    msg(args[2] + " is not a valid item.", ChatColor.RED);
                    return true;
                }

                PlayerData playerData = plugin.pl.getData(args[3]);
                if (playerData == null)
                {
                    msg(PLAYER_NOT_FOUND);
                    return true;
                }
                playerData.removeItem(item);
                plugin.pl.save(playerData);
                msg("Successfully took the " + item.getName() + " from " + args[3], ChatColor.GREEN);
                Player player = getPlayer(args[3]);
                if (player != null)
                {
                    player.sendMessage(ChatColor.RED + sender.getName() + " took the " + item.getName() + " from you");
                }
                return true;
            }

        }
        return false;
    }

}
