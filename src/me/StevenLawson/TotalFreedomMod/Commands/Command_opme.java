package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Automatically ops user.", usage = "/<command>")
public class Command_opme extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        TFM_Util.adminAction(sender.getName(), "Opping " + sender.getName(), false);
        sender.setOp(true);
            sender.sendMessage(TotalFreedomMod.YOU_ARE_OP);
               if (!TFM_AdminList.isAdminImpostor(sender_p))
               {
               sender.sendMessage("You are a Imposter, You need to verify first");
                sender.setOp(false);
               }
        return true;
    }
}
