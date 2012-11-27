package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.SUPER, source = SOURCE_TYPE_ALLOWED.BOTH, ignore_permissions = false)
public class Command_stfu extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            TFM_Util.playerMsg(sender, "Muted players:");
            TFM_UserInfo info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = TFM_UserInfo.getPlayerData(mp);
                if (info.isMuted())
                {
                    TFM_Util.playerMsg(sender, "- " + mp.getName());
                    count++;
                }
            }
            if (count == 0)
            {
                TFM_Util.playerMsg(sender, "- none");
            }
        }
        else if (args[0].equalsIgnoreCase("purge"))
        {
            TFM_Util.adminAction(sender.getName(), "Unmuting all players.", true);
            TFM_UserInfo info;
            int count = 0;
            for (Player mp : server.getOnlinePlayers())
            {
                info = TFM_UserInfo.getPlayerData(mp);
                if (info.isMuted())
                {
                    info.setMuted(false);
                    count++;
                }
            }
            TFM_Util.playerMsg(sender, "Unmuted " + count + " players.");
        }
        else if (args[0].equalsIgnoreCase("all"))
        {
            TFM_Util.adminAction(sender.getName(), "Muting all non-Superadmins", true);

            TFM_UserInfo playerdata;
            int counter = 0;
            for (Player p : server.getOnlinePlayers())
            {
                if (!TFM_SuperadminList.isUserSuperadmin(p))
                {
                    playerdata = TFM_UserInfo.getPlayerData(p);
                    playerdata.setMuted(true);
                    counter++;
                }
            }

            TFM_Util.playerMsg(sender, "Muted " + counter + " players.");
        }
        else
        {
            Player p;
            try
            {
                p = getPlayer(args[0]);
            }
            catch (CantFindPlayerException ex)
            {
                sender.sendMessage(ex.getMessage());
                return true;
            }

            TFM_UserInfo playerdata = TFM_UserInfo.getPlayerData(p);
            if (playerdata.isMuted())
            {
                TFM_Util.adminAction(sender.getName(), "Unmuting " + p.getName(), true);
                playerdata.setMuted(false);
                TFM_Util.playerMsg(sender, "Unmuted " + p.getName());
            }
            else
            {
                if (!TFM_SuperadminList.isUserSuperadmin(p))
                {
                    TFM_Util.adminAction(sender.getName(), "Muting " + p.getName(), true);
                    playerdata.setMuted(true);
                    TFM_Util.playerMsg(sender, "Muted " + p.getName());
                }
                else
                {
                    TFM_Util.playerMsg(sender, p.getName() + " is a superadmin, and can't be muted.");
                }
            }
        }

        return true;
    }
}
