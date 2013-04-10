package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Enable/disable explosives and set effect radius.", usage = "/<command> <on | off> [radius]")
public class Command_explosives extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        if (args.length == 2)
        {
            try
            {
                TotalFreedomMod.explosiveRadius = Math.max(1.0, Math.min(30.0, Double.parseDouble(args[1])));
            }
            catch (NumberFormatException nfex)
            {
                TFM_Util.playerMsg(sender, nfex.getMessage());
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("on"))
        {
            TotalFreedomMod.allowExplosions = true;
            playerMsg("Explosives are now enabled, radius set to " + TotalFreedomMod.explosiveRadius + " blocks.");
        }
        else
        {
            TotalFreedomMod.allowExplosions = false;
            playerMsg("Explosives are now disabled.");
        }

        return true;
    }
}
