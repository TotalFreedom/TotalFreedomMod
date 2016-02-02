package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.BOTH)
@CommandParameters(description = "Senior command - Send a chat message with chat formatting.", usage = "/<command> <message...>")
public class Command_csay extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsPlayer)
    {
        if (args.length > 0)
        {
            TFM_Util.bcastMsg(String.format("§7[Senior Broadcast]§f<§c%s§f> %s", sender.getName(), StringUtils.join(args, " ")));
        }
        return true;
    }
}
