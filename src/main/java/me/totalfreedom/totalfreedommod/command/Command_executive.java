package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.ChatManager;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.banning.Ban;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.GameMode;
import org.bukkit.scheduler.BukkitRunnable;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Executive things...", usage = "/<command> [hell: <username> | accolor: <colorcode | random | nyan>]", aliases = "exec")
public class Command_executive extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!ConfigEntry.SERVER_EXECS.getStringList().contains(sender.getName()) && !ConfigEntry.SERVER_OWNERS.getStringList().contains(sender.getName()) && !FUtil.UMCDEVS.contains(sender.getName()))
        {
            msg("You aren't an executive admin, have a cookie instead!");
            if (!senderIsConsole)
            {
                final int firstEmpty = playerSender.getInventory().firstEmpty();
                final ItemStack cakeItem = new ItemStack(Material.COOKIE);
                playerSender.getInventory().setItem(firstEmpty, cakeItem);
            }
            else
            {
                msg("Sorry, you're not an in-game player, so it's impossible to give you a cookie :(");
            }
            return true;
        }
	if (args.length > 0)
        {
            switch (args[0])
            {
                case "hell":
                {
                    if (args.length == 2)
                    {
                        if (getPlayer(args[1]) == null)
                        {
                            msg(FreedomCommand.PLAYER_NOT_FOUND);
                            return true;
                        }
                        final Player player = getPlayer(args[1]);

                        FUtil.adminAction(sender.getName(), "Calling Satan to open the gates of hell for " + player.getName(), true);
                        FUtil.bcastMsg(player.getName() + " is going to have a bad time!", ChatColor.RED);
                        final String IP = player.getAddress().getAddress().getHostAddress().trim();
                        if (plugin.al.isAdmin(player))
                        {
                            Admin admin = plugin.al.getAdmin(player);
                            admin.setActive(false);
                            plugin.al.save();
                            plugin.al.updateTables();
                        }
                        player.setWhitelisted(false);
                        player.setOp(false);
                        player.setGameMode(GameMode.SURVIVAL);
                        player.closeInventory();
                        player.getInventory().clear();
                        player.setFireTicks(10000);
                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                player.getWorld().strikeLightning(player.getLocation());
                            }
                        }.runTaskLater(plugin, 20L * 2L);
                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                player.getWorld().strikeLightning(player.getLocation());
                            }
                        }.runTaskLater(plugin, 20L * 2L);
                        FUtil.bcastMsg("The gates to hell have opened, let the wrath of " + sender.getName() + " condem " + player.getName() + "!", ChatColor.RED);
                        player.setFireTicks(10000);
                        final CommandSender cSender = sender;
                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                player.getWorld().createExplosion(player.getLocation(), 4F);
                                banPlayer(player.getName(), "You need to get your act together, come back when you can handle yourself!", true, false);
                                FUtil.adminAction(cSender.getName(), "Has sent " + player.getName() + " to hell, IP: " + IP, true);
                                player.kickPlayer(ChatColor.RED + "Welcome to hell you fucking ignorant cunt!");
                            }
                        }.runTaskLater(plugin, 40L * 4L);
                        return true;
                    }
                    else
                    {
                        return false;
                    }    
                }
                case "accolor":
                {
                    if (args.length == 2)
                    {
                        if (args[1].equals("random"))
                        {
                            ChatManager.acr = true;
                            msg(ChatColor.GREEN + "Adminchat color is now " + FUtil.randomChatColor() + "random" + ChatColor.GREEN + "!");
                            return true;
                        }
                        else if (ChatManager.acr == true)
                        {
                            ChatManager.acr = false;
                        }
                        if (args[1].equals("nyan"))
                        {
                            ChatManager.acn = true;
                            String rb = "nyan";
                            String r = "";
                            for (char c : rb.toCharArray())
                            {
                                r = r + FUtil.randomChatColor() + c;
                            }
                            msg(ChatColor.GREEN + "Adminchat color is now " + r + ChatColor.GREEN + "!");
                            return true;
                        }
                        else if (ChatManager.acn == true)
                        {
                            ChatManager.acn = false;
                        }
                        if (args[1].length() != 2)
                        {
                            msg("Invalid color code!", ChatColor.RED);
                            return true;
                        }
                        if (!args[1].startsWith("&"))
                        {
                            msg("Invalid color code!", ChatColor.RED);
                            return true;
                        }
                        String colorChar;
                        ChatColor newColor;
                        if (ChatColor.getByChar(args[1].replace("&", "")) != null)
                        {
                            colorChar = args[1].replace("&", "");
                        }
                        else
                        {
                            msg("Invalid color code!", ChatColor.RED);
                            return true;
                        }
                        if (colorChar.equals("k") || colorChar.equals("l") || colorChar.equals("m") || colorChar.equals("n") || colorChar.equals("o") || colorChar.equals("r"))
                        {
                            msg("That is not a color!", ChatColor.RED);
                            return true;
                        }
                        else
                        {
                            newColor = ChatColor.getByChar(colorChar);
                            ChatManager.acc = newColor;
                            msg(ChatColor.GREEN + "Adminchat color is now " + newColor + newColor.name().toLowerCase().replace("_", " ") + ChatColor.GREEN + "!");
                           return true;
                        }
                    }
                }
            }
            return false;
        }
        return false;
    }
    public void banPlayer (String playerName, String reason, Boolean silent, Boolean kick)
    {
        PlayerData playerData = plugin.pl.getData(playerName);
        final List<String> ips = new ArrayList<>();
        ips.addAll(playerData.getIps());
        String username;
        final Player player = getPlayer(args[1]);
        username = playerName;
        if (!silent)
        {
            FUtil.adminAction(sender.getName(), "Banning " + username + " and IPs: " + StringUtils.join(ips, ", "), true);
        }
        Ban ban = Ban.forPlayerName(username, sender, null, reason);
        for (String ip : ips)
        {
            ban.addIp(ip);
            ban.addIp(FUtil.getFuzzyIp(ip));
        }
        plugin.bm.addBan(ban);

        if (player != null && kick)
        {
            player.kickPlayer(ban.bakeKickMessage());
        }
    }
}
