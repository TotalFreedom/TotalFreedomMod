package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.fun.Lightning;
import me.unraveledmc.unraveledmcmod.config.ConfigEntry;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Toggle lightning power", usage = "/<command> [amount]")
public class Command_lightning extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!ConfigEntry.LIGHTNING_SWING_ENABLED.getBoolean())
        {
            msg("Lightning swing is currently disabled", ChatColor.RED);
            return true;
        }
        if (args.length > 0)
        {
            try
            {
                Lightning.amount = Math.max(1, Math.min(30, Integer.parseInt(args[0])));
                msg("Set lightning bolt count to " + Lightning.amount);
                return true;
            }
            catch (NumberFormatException ex)
            {
                msg("Invalid number: " + args[0], ChatColor.RED);
                return true;
            }
        }
        else
        {
            if (!Lightning.lpl.contains(playerSender))
            {
                Lightning.lpl.add(playerSender);
                msg("You can now strike lightning by swinging your arm!", ChatColor.GREEN);
                return true;
            }
            else
            {
                Lightning.lpl.remove(playerSender);
                msg("You can no longer strike lightning with your arm!", ChatColor.RED);
                return true;
            }
        }
    }
}
