package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.NON_OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Goto the nether.", usage = "/<command>")
public class Command_nether extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        FUtil.gotoWorld(playerSender, server.getWorlds().get(0).getName() + "_nether");
        return true;
    }
}
