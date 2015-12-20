package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Cleaning Particles", usage = "/<command>")
public class Command_purgeparticles extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        TFM_Util.adminAction(sender.getName(), "Purging all playerparticle data", true);
        {
        for (Player player : server.getOnlinePlayers())
        {
        server.dispatchCommand(player, "pp clear");

          }
        }

        return true;
    }
}


