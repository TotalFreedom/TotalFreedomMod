package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
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
                TFM_ConfigEntry.EXPLOSIVE_RADIUS.setDouble(Math.max(1.0, Math.min(30.0, Double.parseDouble(args[1]))));
            }
            catch (NumberFormatException nfex)
            {
                TFM_Util.playerMsg(sender, nfex.getMessage());
                return true;
            }
        }

        if (args[0].equalsIgnoreCase("on"))
        {
            TFM_ConfigEntry.ALLOW_EXPLOSIONS.setBoolean(true);
            playerMsg("Explosives are now enabled, radius set to " + TFM_ConfigEntry.EXPLOSIVE_RADIUS.getDouble() + " blocks.");
        }
        else
        {
            TFM_ConfigEntry.ALLOW_EXPLOSIONS.setBoolean(false);
            playerMsg("Explosives are now disabled.");
        }

        return true;
    }
}
