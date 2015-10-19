package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.TwitterHandler;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import net.pravian.aero.util.Ips;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Manage admins.", usage = "/<command> <list | clean | clearme [ip] | <add | delete | info> <username>>")
public class Command_saconfig extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        final SAConfigMode mode;
        try
        {
            mode = SAConfigMode.findMode(args, sender, senderIsConsole);
        }
        catch (final PermissionsException ex)
        {
            playerMsg(ex.getMessage());
            return true;
        }
        catch (final FormatException ex)
        {
            playerMsg(ex.getMessage());
            return false;
        }

        switch (mode)
        {
            case LIST:
            {
                playerMsg("Superadmins: " + StringUtils.join(plugin.al.getAdminNames(), ", "), ChatColor.GOLD);

                break;
            }
            case CLEAN:
            {
                FUtil.adminAction(sender.getName(), "Cleaning admin list", true);
                plugin.al.deactivateOldEntries(true);
                playerMsg("Superadmins: " + StringUtils.join(plugin.al.getAdminNames(), ", "), ChatColor.YELLOW);

                break;
            }
            case CLEARME:
            {
                final Admin admin = plugin.al.getAdmin(sender_p);

                if (admin == null)
                {
                    playerMsg("Could not find your admin entry! Please notify a developer.", ChatColor.RED);
                    return true;
                }

                final String ip = Ips.getIp(sender_p);

                if (args.length == 1)
                {
                    FUtil.adminAction(sender.getName(), "Cleaning my supered IPs", true);

                    int counter = admin.getIps().size() - 1;
                    admin.clearIPs();
                    admin.addIp(ip);

                    plugin.al.save(admin);

                    playerMsg(counter + " IPs removed.");
                    playerMsg(admin.getIps().get(0) + " is now your only IP address");
                }
                else
                {
                    if (!admin.getIps().contains(args[1]))
                    {
                        playerMsg("That IP is not registered to you.");
                    }
                    else if (ip.equals(args[1]))
                    {
                        playerMsg("You cannot remove your current IP.");
                    }
                    else
                    {
                        FUtil.adminAction(sender.getName(), "Removing a supered IP", true);

                        admin.removeIp(args[1]);

                        plugin.al.save(admin);

                        playerMsg("Removed IP " + args[1]);
                        playerMsg("Current IPs: " + StringUtils.join(admin.getIps(), ", "));
                    }
                }

                break;
            }
            case INFO:
            {
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
                    playerMsg("Superadmin not found: " + args[1]);
                }
                else
                {
                    playerMsg(admin.toString());
                }

                break;
            }
            case ADD:
            {
                Player player = getPlayer(args[1]);
                Admin admin = player == null ? plugin.al.getAdmin(player) : plugin.al.getEntryByName(args[1]);

                // Existing admin
                if (admin != null)
                {
                    if (admin.isActivated())
                    {
                        playerMsg("That player is already admin.");
                    }

                    FUtil.adminAction(sender.getName(), "Readding " + admin.getName() + " to the admin list", true);

                    if (player != null)
                    { // Reset IP, username
                        admin.loadFrom(player);
                    }

                    admin.setActivated(true);
                    plugin.al.save(admin);
                    plugin.al.updateTables();
                    return true;
                }

                // New admin
                if (player == null)
                {
                    playerMsg(FreedomCommand.PLAYER_NOT_FOUND);
                    return true;
                }

                FUtil.adminAction(sender.getName(), "Adding " + player.getName() + " to the admin list", true);
                plugin.al.addAdmin(new Admin(player));

                final FPlayer playerdata = plugin.pl.getPlayer(player.getPlayer());
                if (playerdata.isFrozen())
                {
                    playerdata.setFrozen(false);
                    playerMsg(player.getPlayer(), "You have been unfrozen.");
                }

                break;
            }
            case DELETE:
            {
                Player player = getPlayer(args[1]);
                Admin admin = player == null ? plugin.al.getAdmin(player) : plugin.al.getEntryByName(args[1]);

                if (admin == null)
                {
                    playerMsg("Superadmin not found: " + args[1]);
                    return true;
                }

                FUtil.adminAction(sender.getName(), "Removing " + admin.getName() + " from the admin list", true);
                plugin.al.removeAdmin(admin);

                // Twitterbot
                if (ConfigEntry.TWITTERBOT_ENABLED.getBoolean())
                {
                    TwitterHandler.delTwitterVerbose(admin.getName(), sender);
                }

                break;
            }
        }

        return true;
    }

    private static enum SAConfigMode
    {
        LIST("list", PlayerRank.OP, SourceType.BOTH, 1, 1),
        CLEAN("clean", PlayerRank.SENIOR_ADMIN, SourceType.BOTH, 1, 1),
        CLEARME("clearme", PlayerRank.SUPER_ADMIN, SourceType.ONLY_IN_GAME, 1, 2),
        INFO("info", PlayerRank.SUPER_ADMIN, SourceType.BOTH, 2, 2),
        ADD("add", PlayerRank.SUPER_ADMIN, SourceType.ONLY_CONSOLE, 2, 2),
        DELETE("delete", PlayerRank.SENIOR_ADMIN, SourceType.ONLY_CONSOLE, 2, 2);
        private final String modeName;
        private final PlayerRank rank;
        private final SourceType sourceType;
        private final int minArgs;
        private final int maxArgs;

        private SAConfigMode(String modeName, PlayerRank adminLevel, SourceType sourceType, int minArgs, int maxArgs)
        {
            this.modeName = modeName;
            this.rank = adminLevel;
            this.sourceType = sourceType;
            this.minArgs = minArgs;
            this.maxArgs = maxArgs;
        }

        private static SAConfigMode findMode(final String[] args, final CommandSender sender, final boolean senderIsConsole) throws PermissionsException, FormatException
        {
            if (args.length == 0)
            {
                throw new FormatException("Invalid number of arguments.");
            }

            Admin admin = TotalFreedomMod.plugin.al.getAdmin(sender);

            boolean isSeniorAdmin = admin != null ? admin.isMinimum(PlayerRank.SENIOR_ADMIN) : false;

            for (final SAConfigMode mode : values())
            {
                if (mode.modeName.equalsIgnoreCase(args[0]))
                {
                    if (mode.rank == PlayerRank.SUPER_ADMIN)
                    {
                        if (admin == null)
                        {
                            throw new PermissionsException(FreedomCommand.MSG_NO_PERMS);
                        }
                    }
                    else if (mode.rank == PlayerRank.SENIOR_ADMIN)
                    {
                        if (!isSeniorAdmin)
                        {
                            throw new PermissionsException(FreedomCommand.MSG_NO_PERMS);
                        }
                    }

                    if (mode.sourceType == SourceType.ONLY_IN_GAME)
                    {
                        if (senderIsConsole)
                        {
                            throw new PermissionsException("This command may only be used in-game.");
                        }
                    }
                    else if (mode.sourceType == SourceType.ONLY_CONSOLE)
                    {
                        if (!senderIsConsole)
                        {
                            throw new PermissionsException("This command may only be used from the console.");
                        }
                    }

                    if (args.length >= mode.minArgs && args.length <= mode.maxArgs)
                    {
                        return mode;
                    }
                    else
                    {
                        throw new FormatException("Invalid number of arguments for mode: " + mode.modeName);
                    }
                }
            }

            throw new FormatException("Invalid mode.");
        }
    }

    private static class PermissionsException extends Exception
    {
        private static final long serialVersionUID = 234235261231L;

        private PermissionsException(final String message)
        {
            super(message);
        }
    }

    private static class FormatException extends Exception
    {
        private static final long serialVersionUID = 33331341256779901L;

        private FormatException(final String message)
        {
            super(message);
        }
    }
}
