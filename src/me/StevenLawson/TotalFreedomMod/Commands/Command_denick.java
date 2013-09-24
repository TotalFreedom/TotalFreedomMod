package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_EssentialsBridge;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Essentials Interface Command - Remove the nickname of all players on the server.", usage = "/<command>")
public class Command_denick extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        TFM_Util.adminAction(sender.getName(), "Removing all nicknames.", false);

        Player[] onlinePlayers = server.getOnlinePlayers();
        for (Player player : onlinePlayers)
        {
            TFM_EssentialsBridge.getInstance().setNickname(player.getName(), null);
        }

        return true;
    }
}
