package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "List, add, remove, or set the rank of admins, clean or reload the admin list, or view the info of admins.", usage = "/<command> <list | clean | reload | | setrank <username> <rank> | <add | remove | info> <username>>")
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
                checkRank(Rank.SENIOR_ADMIN);

                FUtil.adminAction(sender.getName(), "Cleaning admin list", true);
                plugin.al.deactivateOldEntries(true);
                msg("Admins: " + StringUtils.join(plugin.al.getAdminNames(), ", "), ChatColor.GOLD);

                return true;
            }

            case "reload":
            {
                checkRank(Rank.SENIOR_ADMIN);

                FUtil.adminAction(sender.getName(), "Reloading the admin list", true);
                plugin.al.load();
                msg("Admin list reloaded!");
                return true;
            }

            case "setrank":
            {
                checkConsole();
                checkRank(Rank.SENIOR_ADMIN);

                if (args.length < 3)
                {
                    return false;
                }

                Rank rank = Rank.findRank(args[2]);
                if (rank == null)
                {
                    msg("Unknown rank: " + args[2]);
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
                plugin.al.save(admin);

                Player player = getPlayer(admin.getName());
                if (player != null)
                {
                    plugin.rm.updateDisplay(player);
                }

                if (plugin.dc.enabled && ConfigEntry.DISCORD_ROLE_SYNC.getBoolean())
                {
                    plugin.dc.syncRoles(admin, plugin.pl.getData(admin.getName()).getDiscordID());
                }

                plugin.amp.updateAccountStatus(admin);

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

                if (plugin.al.isAdmin(player))
                {
                    msg("That player is already admin.");
                    return true;
                }

                // Find the old admin entry
                String name = player != null ? player.getName() : args[1];
                Admin admin = null;
                for (Admin loopAdmin : plugin.al.getAllAdmins())
                {
                    if (loopAdmin.getName().equalsIgnoreCase(name) || loopAdmin.getIps().contains(FUtil.getIp(player)))
                    {
                        admin = loopAdmin;
                        break;
                    }
                }

                if (plugin.pl.isPlayerImpostor(player))
                {
                    msg("This player was labeled as a Player impostor and is not an admin, therefore they cannot be added to the admin list.", ChatColor.RED);
                    return true;
                }

                if (admin == null) // New admin
                {
                    if (player == null)
                    {
                        msg(FreedomCommand.PLAYER_NOT_FOUND);
                        return true;
                    }

                    FUtil.adminAction(sender.getName(), "Adding " + player.getName() + " to the admin list", true);
                    admin = new Admin(player);

                    plugin.al.addAdmin(admin);
                    plugin.rm.updateDisplay(player);
                    plugin.amp.updateAccountStatus(admin);
                }
                else // Existing admin
                {
                    FUtil.adminAction(sender.getName(), "Re-adding " + player.getName() + " to the admin list", true);

                    if (player != null)
                    {
                        String oldName = admin.getName();
                        if (oldName != player.getName())
                        {
                            admin.setName(player.getName());
                            plugin.sql.updateAdminName(oldName, admin.getName());
                        }
                        admin.addIp(FUtil.getIp(player));
                    }

                    admin.setActive(true);
                    admin.setLastLogin(new Date());

                    if (plugin.al.isVerifiedAdmin(player))
                    {
                        plugin.al.verifiedNoAdmins.remove(player.getName());
                        plugin.al.verifiedNoAdminIps.remove(player.getName());
                    }

                    plugin.al.save(admin);
                    plugin.al.updateTables();
                    if (player != null)
                    {
                        plugin.rm.updateDisplay(player);
                    }

                    if (plugin.dc.enabled && ConfigEntry.DISCORD_ROLE_SYNC.getBoolean())
                    {
                        plugin.dc.syncRoles(admin, plugin.pl.getData(player).getDiscordID());
                    }
                    plugin.amp.updateAccountStatus(admin);
                }

                if (player != null)
                {
                    final FPlayer fPlayer = plugin.pl.getPlayer(player);
                    if (fPlayer.getFreezeData().isFrozen())
                    {
                        fPlayer.getFreezeData().setFrozen(false);
                        msg(player, "You have been unfrozen.");
                    }

                    if (!player.isOp())
                    {
                        player.setOp(true);
                        player.sendMessage(YOU_ARE_OP);
                    }
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

                if (plugin.pl.getPlayer(player).inAdminChat())
                {
                    plugin.pl.getPlayer(player).setAdminChat(false);
                }

                plugin.al.save(admin);
                plugin.al.updateTables();
                if (player != null)
                {
                    plugin.rm.updateDisplay(player);
                }

                if (plugin.dc.enabled && ConfigEntry.DISCORD_ROLE_SYNC.getBoolean())
                {
                    plugin.dc.syncRoles(admin, plugin.pl.getData(admin.getName()).getDiscordID());
                }

                plugin.amp.updateAccountStatus(admin);

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
        if (args.length == 1)
        {
            List<String> arguments = new ArrayList<>();
            arguments.add("list");
            if (plugin.al.isAdmin(sender))
            {
                arguments.add("info");
            }
            if (plugin.al.isTelnetAdmin(sender))
            {
                arguments.add("add");
                arguments.add("remove");
            }
            if (plugin.al.isSeniorAdmin(sender))
            {
                arguments.add("reload");
                arguments.add("clean");
                arguments.add("setrank");
            }
            return arguments;
        }
        if (args.length == 2 && (args[0].equals("add") || args[0].equals("remove") || args[0].equals("setrank") || args[0].equals("info")))
        {
            return FUtil.getPlayerList();
        }
        if (args.length == 3 && args[0].equals("setrank"))
        {
            return Arrays.asList("SUPER_ADMIN", "TELNET_ADMIN", "SENIOR_ADMIN");
        }

        return Collections.emptyList();
    }
}