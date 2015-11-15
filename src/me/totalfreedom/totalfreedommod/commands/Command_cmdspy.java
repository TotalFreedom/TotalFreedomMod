package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Spy on commands", usage = "/<command>", aliases = "commandspy")
public class Command_cmdspy extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        FPlayer playerdata = plugin.pl.getPlayer(sender_p);
        playerdata.setCommandSpy(!playerdata.cmdspyEnabled());
        playerMsg("CommandSpy " + (playerdata.cmdspyEnabled() ? "enabled." : "disabled."));

        return true;
    }
}
