package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Automatically ops the sender.", usage = "/<command>")
public class Command_opme extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
         if (!TFM_AdminList.isAdminImpostor(sender_p))
        TFM_Util.adminAction(sender.getName(), "Opping " + sender.getName(), false);
        sender.setOp(true);
        sender.sendMessage(TFM_Command.YOU_ARE_OP);
         if (TFM_AdminList.isAdminImpostor(sender_p))
        sender.sendMessage(ChatColor.BLUE + "You need to verify before you get op!");
        sender.setOp(false);

        return true;
    }
}
