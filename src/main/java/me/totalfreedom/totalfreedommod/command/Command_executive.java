package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.pravian.aero.util.Ips;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
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
@CommandParameters(description = "Executive things...", usage = "/<command> [<salist:[list | clean | reload | setrank <username> <rank> | add <username> | remove <username> | info <username> | <adminmode:[on | off]> | <hell: <username>>]", aliases = "exec")
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
                final ItemStack cakeItem = new ItemStack(Material.CAKE);
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
                case "salist":
                {
                    if (args.length > 1)
                    {
                        switch (args[1])
                        {
                            case "list":
                            {
                                msg("Superadmins: " + StringUtils.join(plugin.al.getAdminNames(), ", "), ChatColor.GOLD);
                                return true;
                            }
                            case "clean":
                            {
                                FUtil.adminAction(sender.getName(), "Cleaning admin list", true);
                                plugin.al.deactivateOldEntries(true);
                                msg("Superadmins: " + StringUtils.join(plugin.al.getAdminNames(), ", "), ChatColor.GOLD);
                                return true;
                            }
                            case "reload":
                            {
                                FUtil.adminAction(sender.getName(), "Reloading the admin list", true);
                                plugin.al.load();
                                msg("Admin list reloaded!");
                                return true;
                            }
                            case "setrank":
                            {
                                if (args.length < 4)
                                {
                                    return false;
                                }
                                if (getPlayer(args[2]) == null)
                                {
                                    sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                                    return true;
                                }
                                Rank rank = Rank.findRank(args[3]);
                                if (!rank.isAtLeast(Rank.SUPER_ADMIN))
                                {
                                    msg("Rank must be superadmin or higher.", ChatColor.RED);
                                    return true;
                                }
                                if (getPlayer(args[2]).equals(playerSender))
                                {
                                    msg("You can not change your own rank!", ChatColor.RED);
                                    return true;
                                }
                                Admin admin = plugin.al.getEntryByName(args[2]);
                                if (admin == null)
                                {
                                    msg("Unknown admin: " + args[2]);
                                    return true;
                                }
                                admin.setRank(rank);
                                plugin.al.save();
                                FUtil.adminAction(sender.getName(), "Set " + admin.getName() + "'s rank to " + rank.getName(), true);
                                msg("Set " + admin.getName() + "'s rank to " + rank.getName());
                                return true;
                            }
                            case "info":
                            {
                                if (args.length < 3)
                                {
                                    return false;
                                }
                                Admin admin = plugin.al.getEntryByName(args[2]);
                                if (admin == null)
                                {
                                    final Player player = getPlayer(args[2]);
                                    if (player != null)
                                    {
                                        admin = plugin.al.getAdmin(player);
                                    }
                                }
                                if (admin == null)
                                {
                                    msg("Admin not found: " + args[2]);
                                }
                                else
                                {
                                    msg(admin.toString());
                                }
                                return true;
                            }
                            case "add":
                            {
                                if (args.length < 3)
                                {
                                    return false;
                                }
                                if (getPlayer(args[2]) == null)
                                {
                                    sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                                    return true;
                                }
                                final Player player = getPlayer(args[2]);
                                if (plugin.al.isAdmin(player))
                                {
                                    msg("That player is already admin.");
                                    return true;
                                }
                                String name = player != null ? player.getName() : args[2];
                                Admin admin = null;
                                for (Admin loopAdmin : plugin.al.getAllAdmins().values())
                                {
                                    if (loopAdmin.getName().equalsIgnoreCase(name))
                                    {
                                        admin = loopAdmin;
                                        break;
                                    }
                                }
                                if (admin != null) // Existing admin
                                {
                                    FUtil.adminAction(sender.getName(), "Readding " + admin.getName() + " to the admin list", true);
                                    if (player != null)
                                    {
                                        admin.loadFrom(player); // Reset IP, username
                                    }
                                    admin.setActive(true);
                                    admin.setLastLogin(new Date());
                                    if (player != null)
                                    {
                                        admin.addIp(Ips.getIp(player));
                                    }
                                    plugin.al.save();
                                    plugin.al.updateTables();
                                }
                                else // New admin
                                {
                                    if (player == null)
                                    {
                                        msg(FreedomCommand.PLAYER_NOT_FOUND);
                                        return true;
                                    }
                                    FUtil.adminAction(sender.getName(), "Adding " + player.getName() + " to the admin list", true);
                                    plugin.al.addAdmin(new Admin(player));
                                }
                                if (player != null)
                                {
                                    final FPlayer fPlayer = plugin.pl.getPlayer(player);
                                    if (fPlayer.getFreezeData().isFrozen())
                                    {
                                        fPlayer.getFreezeData().setFrozen(false);
                                        msg(player.getPlayer(), "You have been unfrozen.");
                                    }
                                }
                                return true;
                            }
                            case "remove":
                            {
                                if (args.length < 3)
                                {
                                    return false;
                                }
                                if (getPlayer(args[2]) == null)
                                {
                                    sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
                                    return true;
                                }
                                Player player = getPlayer(args[1]);
                                Admin admin = player == null ? plugin.al.getAdmin(player) : plugin.al.getEntryByName(args[1]);
                                if (admin == null)
                                {
                                    msg("Admin not found: " + args[2]);
                                    return true;
                                }
                                FUtil.adminAction(sender.getName(), "Removing " + admin.getName() + " from the admin list", true);
                                admin.setActive(false);
                                plugin.al.save();
                                plugin.al.updateTables();
                                return true;
                            }
                        }
                    }
                }
                case "adminmode":
                {
                    if (args.length == 2)
                    {
                        if (args[1].equalsIgnoreCase("off"))
                        {
                            ConfigEntry.ADMIN_ONLY_MODE.setBoolean(false);
                            FUtil.adminAction(sender.getName(), "Opening the server to all players.", true);
                            return true;
                        }
                        else if (args[1].equalsIgnoreCase("on"))
                        {
                            ConfigEntry.ADMIN_ONLY_MODE.setBoolean(true);
                            FUtil.adminAction(sender.getName(), "Closing the server to non-admins.", true);
                            for (Player player : server.getOnlinePlayers())
                            {
                                if (!isAdmin(player))
                                {
                                    player.kickPlayer("Server is now closed to non-superadmins.");
                                }
                            }
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }
                    else
                    {
                        return false;
                    }
                }
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
                        String reason = null;

                        FUtil.adminAction(sender.getName(), "Calling Saten to open the gates of hell for " + player.getName(), true);
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
                        player.getWorld().createExplosion(player.getLocation(), 4F);
                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                player.getWorld().strikeLightning(player.getLocation());
                            }
                        }.runTaskLater(plugin, 20L * 2L);
                        player.getWorld().createExplosion(player.getLocation(), 4F);
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
                                banPlayer(player.getName(), "You are in hell, so fuck off!", true, false);
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
