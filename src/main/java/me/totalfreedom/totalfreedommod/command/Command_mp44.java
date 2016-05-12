package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Modern weaponry, FTW. Use 'draw' to start firing, 'sling' to stop firing.", usage = "/<command> <draw | sling>")
public class Command_mp44 extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!ConfigEntry.MP44_ENABLED.getBoolean())
        {
            msg("The mp44 is currently disabled.", ChatColor.GREEN);
            return true;
        }

        if (args.length == 0)
        {
            return false;
        }

        FPlayer playerdata = plugin.pl.getPlayer(playerSender);

        if (args[0].equalsIgnoreCase("draw"))
        {
            playerdata.armMP44();

            msg("mp44 is ARMED! Left click with gunpowder to start firing, left click again to quit.", ChatColor.GREEN);
            msg("Type /mp44 sling to disable.  -by Madgeek1450", ChatColor.GREEN);

            playerSender.getEquipment().setItemInMainHand(new ItemStack(Material.SULPHUR, 1));
        }
        else
        {
            playerdata.disarmMP44();

            sender.sendMessage(ChatColor.GREEN + "mp44 Disarmed.");
        }

        return true;
    }
}
