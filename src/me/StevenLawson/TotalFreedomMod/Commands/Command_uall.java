package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Undisguises all players", usage = "/<command>")
public class Command_uall extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        TFM_Util.adminAction(sender.getName(), "Undisguising all players", true);

        if (senderIsConsole)
        {
            for (Player p : Bukkit.getOnlinePlayers())
            {
                server.dispatchCommand(p, "u");
            }
        }
        else
        {
            server.dispatchCommand(sender, "u *");
        }

        return true;
    }
}
