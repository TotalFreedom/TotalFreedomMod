package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Manually verify someone", usage = "/<command> <playername>", aliases="mv")
public class Command_manuallyverify extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (!plugin.dc.enabled)
        {
            msg("The Discord verification system is currently disabled", ChatColor.RED);
            return true;
        }

        if (args.length == 0)
        {
            return false;
        }

        final Player player = getPlayer(args[0]);
        if (player == null)
        {
            msg(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        if (!plugin.pl.isImposter(player))
        {
            msg("That player is not an impostor.");
            return true;
        }

        FUtil.staffAction(sender.getName(), "Manually verifying player " + player.getName(), false);
        player.setOp(true);
        player.sendMessage(YOU_ARE_OP);

        if (plugin.pl.getPlayer(player).getFreezeData().isFrozen())
        {
            plugin.pl.getPlayer(player).getFreezeData().setFrozen(false);
            player.sendMessage(ChatColor.GRAY + "You have been unfrozen.");
        }

        plugin.pl.verify(player, null);
        plugin.rm.updateDisplay(player);

        return true;
    }
}