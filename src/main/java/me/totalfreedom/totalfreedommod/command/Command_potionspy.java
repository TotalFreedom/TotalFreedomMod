package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Toggles potion spy.", usage = "/<command>", aliases = "potspy")
public class Command_potionspy extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        FPlayer playerdata = plugin.pl.getPlayer(playerSender);
        playerdata.setPotionMonitorEnabled(!playerdata.isPotionMonitorEnabled());
        msg("PotionSpy is now " + (playerdata.isPotionMonitorEnabled() ? "enabled." : "disabled."));
        return true;
    }
}
