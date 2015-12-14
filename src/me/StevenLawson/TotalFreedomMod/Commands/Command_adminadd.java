package me.StevenLawson.TotalFreedomMod.Commands;

import static me.StevenLawson.TotalFreedomMod.Commands.Command_smite.smite;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Ban;
import me.StevenLawson.TotalFreedomMod.TFM_BanManager;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "A command for only Developers to add SA to SuperAdmin", usage = "/<command> [add/saadd <player> | del <player> | suspend <player> | teston | testoff]")
public class Command_adminadd extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!TFM_Util.IFDEV.contains(sender.getName()))
        {
            sender.sendMessage(TFM_Command.MSG_NO_PERMS);
            Bukkit.broadcastMessage(ChatColor.RED + "WARNING: " + sender.getName() + " has attempted to use a Developer command. IF-DEV team has been alerted.");
            smite(sender_p);
            //lol smites them if they cant do /adminadd i'm really evil :)
            return true;
        }
        if (args.length == 0)
        {
            return false;
        }

        String mode = args[0].toLowerCase();

        if (mode.equals("add"))
        {
            Player player = getPlayer(args[1]);
            if (player == null)
            {
                sender.sendMessage(TFM_Command.PLAYER_NOT_FOUND);
            }
            TFM_Util.adminAction(sender.getName(), "Adding " + args[1] + " to the superadmin list", true);
            TFM_AdminList.addSuperadmin(player);
            if (player.isOnline()) // Remove them from being frozen
            {
                final TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player.getPlayer());

                if (playerdata.isFrozen())
                {
                    playerdata.setFrozen(false);
                    playerMsg(player.getPlayer(), "You have been unfrozen.");
                }
            }
        }

        if (mode.equals("saadd"))
        {
            Player player = getPlayer(args[1]);
            if (player == null)
            {
                sender.sendMessage(TFM_Command.PLAYER_NOT_FOUND);
            }
            TFM_Util.adminAction(sender.getName(), "Adding " + args[1] + " to the superadmin list", true);
            TFM_AdminList.addSuperadmin(player);
            if (player.isOnline())
            {
                final TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player.getPlayer());

                if (playerdata.isFrozen()) // Remove them from being frozen
                {
                    playerdata.setFrozen(false);
                    playerMsg(player.getPlayer(), "You have been unfrozen.");
                }
            }
        }

        if (mode.equals("del"))
        {
            Player player = getPlayer(args[1]);
            if (player == null)
            {
                sender.sendMessage(TFM_Command.PLAYER_NOT_FOUND);
            }
            TFM_Util.adminAction(sender.getName(), "Removing " + args[1] + " from the superadmin list", true);
            TFM_AdminList.removeSuperadmin(player);
            if (TFM_Util.RF_DEVELOPERS.contains(sender.getName()) && TFM_ConfigEntry.SERVER_OWNERS.getList().contains(sender.getName()))
            {
                sender.sendMessage(ChatColor.RED + "You can suspend the player instead by doing /sys suspend <player>");
            }
        }

        if (mode.equals("suspend"))
        {
            if (!TFM_Util.IFDEV.contains(sender.getName()))
            {
                sender.sendMessage(TFM_Command.MSG_NO_PERMS);
                return true;
            }
            Player player = getPlayer(args[1]);
            if (player == null)
            {
                sender.sendMessage(TFM_Command.PLAYER_NOT_FOUND);
            }
            TFM_Util.adminAction(sender.getName(), "Suspending " + args[1], true);
            TFM_AdminList.removeSuperadmin(player);
            for (String playerIp : TFM_PlayerList.getEntry(player).getIps())
            {
                TFM_BanManager.addIpBan(new TFM_Ban(playerIp, player.getName()));
            }
            TFM_BanManager.addUuidBan(player);
            player.closeInventory();
            player.getInventory().clear();
            player.kickPlayer("You have been suspended. Check the forums for more information.");
        }

        if (mode.equals("teston"))
        {
            Bukkit.broadcastMessage(ChatColor.RED + "WARNING: " + sender.getName() + " has started testing on this server.");
        }

        if (mode.equals("testoff"))
        {
            Bukkit.broadcastMessage(ChatColor.RED + sender.getName() + " has successfully tested on this server.");
        }

        return true;
    }
}
