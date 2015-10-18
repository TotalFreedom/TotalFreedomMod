package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminMessenger;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Message online admins", usage = "/<command> [off] <message>")
public class Command_adminmsg extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        TFM_AdminMessenger.adminMessengerMsg(sender, StringUtils.join(args, " "), senderIsConsole);
        final TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(sender_p);
        playerdata.setAdminHelpNeeded(true);

        if (args.length == 2)
        {
            if (args[0].equalsIgnoreCase("off"))
            {
                playerdata.setAdminHelpNeeded(false);
            }
        }
        return true;
    }
}
