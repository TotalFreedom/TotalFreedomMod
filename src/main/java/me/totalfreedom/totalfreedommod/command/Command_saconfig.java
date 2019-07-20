package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.masterbuilder.MasterBuilder;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.util.Ips;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Manage admins.", usage = "/<command> <list | clean | reload | | setrank <username> <rank> | <add | remove | info> <username>>")
public class Command_saconfig extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        switch (args[0])
        {
            case "list":
            {
                msg("Admins: " + StringUtils.join(plugin.al.getAdminNames(), ", "), ChatColor.GOLD);
                return true;
            }

            case "clean":
            {
                checkConsole();
                checkRank(Rank.TELNET_ADMIN);

                FUtil.adminAction(sender.getName(), "Cleaning admin list", true);
                plugin.al.deactivateOldEntries(true);
                msg("Admins: " + StringUtils.join(plugin.al.getAdminNames(), ", "), ChatColor.GOLD);

                return true;
            }

            case "reload":
            {
                checkRank(Rank.SUPER_ADMIN);

                FUtil.adminAction(sender.getName(), "Reloading the admin list", true);
                plugin.al.load();
                msg("Admin list reloaded!");
                return true;
            }

            case "setrank":
            {
                checkConsole();
                checkNotHostConsole();
                checkRank(Rank.SENIOR_CONSOLE);

                if (args.length < 3)
                {
                    return false;
                }

                Rank rank = Rank.findRank(args[2]);
                if (rank == null)
                {
                    msg("Unknown rank: " + rank);
                    return true;
                }

                if (rank.isConsole())
                {
                    msg("You cannot set players to a console rank");
                    return true;
                }

                if (!rank.isAtLeast(Rank.SUPER_ADMIN))
                {
                    msg("Rank must be Super Admin or higher.", ChatColor.RED);
                    return true;
                }

                Admin admin = plugin.al.getEntryByName(args[1]);
                if (admin == null)
                {
                    msg("Unknown admin: " + args[1]);
                    return true;
                }

                FUtil.adminAction(sender.getName(), "Setting " + admin.getName() + "'s rank to " + rank.getName(), true);

                admin.setRank(rank);
                plugin.al.save();

                Player player = getPlayer(admin.getName());
                if (player != null)
                {
                    plugin.rm.updateDisplay(player);
                }

                if (plugin.dc.enabled && ConfigEntry.DISCORD_ROLE_SYNC.getBoolean())
                {
                    plugin.dc.syncRoles(admin);
                }

                msg("Set " + admin.getName() + "'s rank to " + rank.getName());
                return true;
            }

            case "info":
            {
                if (args.length < 2)
                {
                    return false;
                }

                checkRank(Rank.SUPER_ADMIN);

                Admin admin = plugin.al.getEntryByName(args[1]);

                if (admin == null)
                {
                    final Player player = getPlayer(args[1]);
                    if (player != null)
                    {
                        admin = plugin.al.getAdmin(player);
                    }
                }

                if (admin == null)
                {
                    msg("Admin not found: " + args[1]);
                }
                else
                {
                    msg(admin.toString());
                }

                return true;
            }

            case "add":
            {
                if (args.length < 2)
                {
                    return false;
                }

                checkConsole();
                checkRank(Rank.TELNET_ADMIN);

                // Player already an admin?
                final Player player = getPlayer(args[1]);

                if (player == null)
                {
                    msg(FreedomCommand.PLAYER_NOT_FOUND);
                    return true;
                }

                if (player != null && plugin.al.isAdmin(player))
                {
                    msg("That player is already admin.");
                    return true;
                }

                // Find the old admin entry
                String name = player != null ? player.getName() : args[1];
                Admin admin = null;
                for (Admin loopAdmin : plugin.al.getAllAdmins().values())
                {
                    if (loopAdmin.getName().equalsIgnoreCase(name))
                    {
                        admin = loopAdmin;
                        break;
                    }
                }

                if (plugin.pv.isPlayerImpostor(player))
                {
                    msg("This player was labeled as a Player impostor and is not an admin, therefore they cannot be added to the admin list.", ChatColor.RED);
                    return true;
                }

                if (admin == null) // New admin
                {
                    if (plugin.mbl.isMasterBuilderImpostor(player))
                    {
                        msg("This player was labeled as a Master Builder impostor and is not an admin, therefore they cannot be added to the admin list.", ChatColor.RED);
                        return true;
                    }
                    if (player == null)
                    {
                        msg(FreedomCommand.PLAYER_NOT_FOUND);
                        return true;
                    }

                    FUtil.adminAction(sender.getName(), "Adding " + player.getName() + " to the admin list", true);
                    plugin.al.addAdmin(new Admin(player));
                    if (player != null)
                    {
                        plugin.rm.updateDisplay(player);
                    }

                    // Attempt to find discord account
                    if (plugin.mbl.isMasterBuilder(player))
                    {
                        MasterBuilder masterBuilder = plugin.mbl.getMasterBuilder(player);
                        admin.setDiscordID(plugin.mbl.getMasterBuilder(player).getDiscordID());
                    }
                    else if (plugin.pv.getVerificationPlayer(player.getName()) != null)
                    {
                        VPlayer vPlayer = plugin.pv.getVerificationPlayer(player.getName());
                        if (vPlayer.getDiscordId() != null)
                        {
                            admin.setDiscordID(vPlayer.getDiscordId());
                        }
                    }
                }
                else // Existing admin
                {
                    FUtil.adminAction(sender.getName(), "Re-adding " + admin.getName() + " to the admin list", true);

                    if (player != null)
                    {
                        admin.setName(player.getName());
                        admin.addIp(Ips.getIp(player));
                    }

                    // Handle master builders
                    if (!plugin.mbl.isMasterBuilder(player))
                    {
                        MasterBuilder masterBuilder = null;
                        for (MasterBuilder loopMasterBuilder : plugin.mbl.getAllMasterBuilders().values())
                        {
                            if (loopMasterBuilder.getName().equalsIgnoreCase(name))
                            {
                                masterBuilder = loopMasterBuilder;
                                break;
                            }
                        }

                        if (masterBuilder != null)
                        {
                            if (player != null)
                            {
                                masterBuilder.setName(player.getName());
                                masterBuilder.addIp(Ips.getIp(player));
                            }

                            masterBuilder.setLastLogin(new Date());

                            plugin.mbl.save();
                            plugin.mbl.updateTables();
                        }
                    }

                    admin.setActive(true);
                    admin.setLastLogin(new Date());

                    // Attempt to find discord account
                    if (plugin.mbl.isMasterBuilder(player))
                    {
                        MasterBuilder masterBuilder = plugin.mbl.getMasterBuilder(player);
                        admin.setDiscordID(plugin.mbl.getMasterBuilder(player).getDiscordID());
                    }
                    else if (plugin.pv.getVerificationPlayer(admin.getName()) != null)
                    {
                        VPlayer vPlayer = plugin.pv.getVerificationPlayer(admin.getName());
                        if (vPlayer.getDiscordId() != null)
                        {
                            admin.setDiscordID(vPlayer.getDiscordId());
                        }
                    }

                    plugin.al.save();
                    plugin.al.updateTables();
                    if (player != null)
                    {
                        plugin.rm.updateDisplay(player);
                    }

                    if (plugin.dc.enabled && ConfigEntry.DISCORD_ROLE_SYNC.getBoolean())
                    {
                        plugin.dc.syncRoles(admin);
                    }
                }

                if (player != null)
                {
                    final FPlayer fPlayer = plugin.pl.getPlayer(player);
                    if (fPlayer.getFreezeData().isFrozen())
                    {
                        fPlayer.getFreezeData().setFrozen(false);
                        msg(player.getPlayer(), "You have been unfrozen.");
                    }

                    if (!player.isOp())
                    {
                        player.setOp(true);
                        player.sendMessage(YOU_ARE_OP);
                    }
                    plugin.pv.removeEntry(player.getName()); // admins can't have player verification entries
                }
                return true;
            }

            case "remove":
            {
                if (args.length < 2)
                {
                    return false;
                }

                checkConsole();
                checkRank(Rank.TELNET_ADMIN);

                Player player = getPlayer(args[1]);
                Admin admin = player != null ? plugin.al.getAdmin(player) : plugin.al.getEntryByName(args[1]);

                if (admin == null)
                {
                    msg("Admin not found: " + args[1]);
                    return true;
                }

                FUtil.adminAction(sender.getName(), "Removing " + admin.getName() + " from the admin list", true);
                admin.setActive(false);
                plugin.al.save();
                plugin.al.updateTables();
                if (player != null)
                {
                    plugin.rm.updateDisplay(player);
                }

                if (plugin.dc.enabled && ConfigEntry.DISCORD_ROLE_SYNC.getBoolean())
                {
                    plugin.dc.syncRoles(admin);
                }

                return true;
            }

            default:
            {
                return false;
            }
        }
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (sender instanceof Player)
        {
            if (args.length == 1)
            {
                List<String> arguments = new ArrayList<>();
                arguments.add("list");
                if (plugin.al.isAdmin(sender))
                {
                    arguments.add("info");
                }
                return arguments;
            }
            else if (args.length == 2 && args[0].equals("info") && plugin.al.isAdmin(sender))
            {
                return plugin.al.getActiveAdminNames();
            }
            return Collections.emptyList();
        }
        else
        {
            if (args.length == 1)
            {
                return Arrays.asList("add", "remove", "clean", "reload", "setrank", "info", "list");
            }
            else if (args.length == 2)
            {
                if (args[0].equals("add") || args[0].equals("remove") || args[0].equals("setrank") || args[0].equals("info"))
                {
                    return FUtil.getPlayerList();
                }
            }
            else if (args.length == 3 && args[0].equals("setrank"))
            {
                return Arrays.asList("super_admin", "telnet_admin", "senior_admin");
            }
        }

        return Collections.emptyList();
    }
}
