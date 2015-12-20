package me.StevenLawson.TotalFreedomMod.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Clearing all particle effect from all players on the server", usage = "/<command> <partialname>")
public class Command_purgeparticles extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)

        {           
            {
                for (Player player : server.getOnlinePlayers())
                {
                server.dispatchCommand(player, "pp clear");
                }
            }
        return true;
    }
}
