package me.totalfreedom.totalfreedommod.command;

import java.util.Date;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.player.FPlayer;
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
                msg("Superadmins: " + StringUtils.join(plugin.al.getAdminNames(), ", "), ChatColor.GOLD);

                return true;
            }

            case "clean":
            {
                checkConsole();
                checkRank(Rank.TELNET_ADMIN);

                FUtil.adminAction(sender.getName(), "Cleaning admin list", true);
                plugin.al.deactivateOldEntries(true);
                msg("Superadmins: " + StringUtils.join(plugin.al.getAdminNames(), ", "), ChatColor.GOLD);

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

                if (!rank.isAtLeast(Rank.SUPER_ADMIN))
                {
                    msg("Rank must be superadmin or higher.", ChatColor.RED);
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
                    msg("Superadmin not found: " + args[1]);
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

                if (admin == null) // New admin
                {
                    if (player == null)
                    {
                        msg(FreedomCommand.PLAYER_NOT_FOUND);
                        return true;
                    }

                    FUtil.adminAction(sender.getName(), "Adding " + player.getName() + " to the admin list", true);
                    plugin.al.addAdmin(new Admin(player));
                }
                else // Existing admin
                {
                    FUtil.adminAction(sender.getName(), "Readding " + admin.getName() + " to the admin list", true);

                    if (player != null)
                    {
                        admin.setName(player.getName());
                        admin.addIp(Ips.getIp(player));
                    }

                    admin.setActive(true);
                    admin.setLastLogin(new Date());

                    plugin.al.save();
                    plugin.al.updateTables();
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
                    msg("Superadmin not found: " + args[1]);
                    return true;
                }

                FUtil.adminAction(sender.getName(), "Removing " + admin.getName() + " from the admin list", true);
                admin.setActive(false);
                plugin.al.save();
                plugin.al.updateTables();
                return true;
            }

            default:
            {
                return false;
            }
        }
    }

}
