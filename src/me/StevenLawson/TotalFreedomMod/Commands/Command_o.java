package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.SUPER, source = SOURCE_TYPE_ALLOWED.BOTH, ignore_permissions = false)
public class Command_o extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if(senderIsConsole && args.length == 0)
        {
            TFM_Util.playerMsg(sender, "Only in-game players can toggle AdminChat.");
            return true;
        }

        if(args.length == 0)
        {
            TFM_UserInfo userinfo = TFM_UserInfo.getPlayerData(sender_p);
            userinfo.setAdminChat(!userinfo.inAdminChat());
            TFM_Util.playerMsg(sender, "Toggled Admin Chat " + (userinfo.inAdminChat() ? "on" : "off")+".");
        }
        else
        {

            // very complicated magic stuff
            TFM_Util.AdminChatMessage(sender, StringUtils.join(args, " "), senderIsConsole);
        }
        return true;
    }
}