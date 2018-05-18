package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Op everyone on the server, optionally change everyone's gamemode at the same time.", usage = "/<command> [-c | -s | -a]")
public class Command_opall extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        FUtil.adminAction(sender.getName(), "Opping all players on the server", false);

        boolean doSetGamemode = false;
        GameMode targetGamemode = GameMode.CREATIVE;
        if (args.length != 0)
        {
            if (args[0].equals("-c"))
            {
                doSetGamemode = true;
                targetGamemode = GameMode.CREATIVE;
            }
            else if (args[0].equals("-s"))
            {
                doSetGamemode = true;
                targetGamemode = GameMode.SURVIVAL;
            }
            else if (args[0].equals("-a"))
            {
                doSetGamemode = true;
                targetGamemode = GameMode.ADVENTURE;
            }
        }

        for (Player player : server.getOnlinePlayers())
        {
            player.setOp(true);
            player.sendMessage(FreedomCommand.YOU_ARE_OP);

            if (doSetGamemode && !player.getGameMode().equals(GameMode.SPECTATOR))
            {
                player.setGameMode(targetGamemode);
            }
        }

        return true;
    }
}
