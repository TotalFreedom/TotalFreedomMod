package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_Admin;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_TwitterHandler;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Manage superadmins.",
        usage = "/<command> <list | clean | clearme [ip] | <add | delete | info> <username>>")
public class Command_saconfig extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0 || args.length > 2)
        {
            return false;
        }

        if (args.length == 1)
        {
            if (args[0].equals("list"))
            {
                playerMsg("Superadmins: " + StringUtils.join(TFM_AdminList.getSuperNames(), ", "), ChatColor.GOLD);
                return true;
            }

            if (args[0].equals("clean"))
            {
                if (!TFM_AdminList.isSeniorAdmin(sender, true))
                {
                    playerMsg(TFM_Command.MSG_NO_PERMS);
                    return true;
                }

                TFM_Util.adminAction(sender.getName(), "Cleaning superadmin list", true);
                TFM_AdminList.cleanSuperadminList(true);
                playerMsg("Superadmins: " + StringUtils.join(TFM_AdminList.getSuperNames(), ", "), ChatColor.YELLOW);
                return true;
            }
        }

        // All commands below are superadmin+ commands.
        if (!TFM_AdminList.isSuperAdmin(sender))
        {
            playerMsg(TFM_Command.MSG_NO_PERMS);
            return true;
        }

        if (args[0].equals("clearme"))
        {
            if (senderIsConsole)
            {
                playerMsg(TFM_Command.NOT_FROM_CONSOLE);
                return true;
            }

            final TFM_Admin admin = TFM_AdminList.getEntry(sender_p);

            final String ip = TFM_Util.getIp(sender_p);

            if (args.length == 1)
            {
                TFM_Util.adminAction(sender.getName(), "Cleaning my supered IPs", true);

                int counter = 0;
                for (int i = 0; i < admin.getIps().size(); i++)
                {
                    if (admin.getIps().get(i).equals(ip))
                    {
                        continue;
                    }

                    admin.removeIp(admin.getIps().get(i));
                    counter++;
                }

                TFM_AdminList.saveAll();

                playerMsg(counter + " IPs removed.");
                playerMsg(admin.getIps().get(0) + " is now your only IP address");
                return true;
            }

            // args.length == 2
            if (!admin.getIps().contains(args[1]))
            {
                playerMsg("That IP is not registered to you.");
                return true;
            }

            if (ip.equals(args[1]))
            {
                playerMsg("You cannot remove your current IP.");
                return true;
            }

            TFM_Util.adminAction(sender.getName(), "Removing a supered IP", true);

            admin.removeIp(args[1]);
            TFM_AdminList.saveAll();

            playerMsg("Removed IP " + args[1]);
            playerMsg("Current IPs: " + StringUtils.join(admin.getIps(), ", "));
            return true;
        }

        if (args[0].equals("info"))
        {

            TFM_Admin superadmin = TFM_AdminList.getEntry(args[1].toLowerCase());

            if (superadmin == null)
            {

                final Player player = getPlayer(args[1]);

                if (player != null)
                {
                    superadmin = TFM_AdminList.getEntry(player.getName().toLowerCase());
                }
            }

            if (superadmin == null)
            {
                playerMsg("Superadmin not found: " + args[1]);
                return true;
            }

            playerMsg(superadmin.toString());
            return true;
        }

        if (!senderIsConsole)
        {
            playerMsg("This command may only be used from the console.");
            return true;
        }

        if (args[0].equals("add"))
        {
            OfflinePlayer player = getPlayer(args[1]);

            if (player == null)
            {
                final TFM_Admin superadmin = TFM_AdminList.getEntry(args[1]);

                if (superadmin == null)
                {
                    playerMsg(TFM_Command.PLAYER_NOT_FOUND);
                    return true;
                }

                player = me.StevenLawson.TotalFreedomMod.TFM_DepreciationAggregator.getOfflinePlayer(server, superadmin.getLastLoginName());
            }

            TFM_Util.adminAction(sender.getName(), "Adding " + player.getName() + " to the superadmin list", true);
            TFM_AdminList.addSuperadmin(player);

            if (player.isOnline())
            {
                final TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData((Player) player);

                if (playerdata.isFrozen())
                {
                    playerdata.setFrozen(false);
                    playerMsg((Player) player, "You have been unfrozen.");
                }
            }

            return true;
        }

        if ("delete".equals(args[0]))
        {
            if (!TFM_AdminList.isSeniorAdmin(sender))
            {
                playerMsg(TFM_Command.MSG_NO_PERMS);
                return true;
            }

            String targetName = args[1];

            final Player player = getPlayer(targetName);

            if (player != null)
            {
                targetName = player.getName();
            }

            if (!TFM_AdminList.getLowerSuperNames().contains(targetName.toLowerCase()))
            {
                playerMsg("Superadmin not found: " + targetName);
                return true;
            }

            TFM_Util.adminAction(sender.getName(), "Removing " + targetName + " from the superadmin list", true);
            TFM_AdminList.removeSuperadmin(me.StevenLawson.TotalFreedomMod.TFM_DepreciationAggregator.getOfflinePlayer(server, targetName));

            // Twitterbot
            if (TFM_ConfigEntry.TWITTERBOT_ENABLED.getBoolean())
            {
                TFM_TwitterHandler.delTwitterVerbose(targetName, sender);
            }
            return true;
        }
        return false;

    }
}
