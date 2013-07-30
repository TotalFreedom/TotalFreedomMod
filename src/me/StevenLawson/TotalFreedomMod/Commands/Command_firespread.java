package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_GameRuleHandler;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Enable/disable fire spread.", usage = "/<command> <on | off>")
public class Command_firespread extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        playerMsg("Fire spread is now " + ((TotalFreedomMod.allowFireSpread = !args[0].equalsIgnoreCase("off")) ? "enabled" : "disabled") + ".");

        TFM_GameRuleHandler.setGameRule(TFM_GameRuleHandler.TFM_GameRule.DO_FIRE_TICK, TotalFreedomMod.allowFireSpread);

        return true;
    }
}
