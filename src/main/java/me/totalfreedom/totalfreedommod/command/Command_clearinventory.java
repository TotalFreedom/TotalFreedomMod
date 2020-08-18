package me.totalfreedom.totalfreedommod.command;

import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Clear your inventory.", usage = "/<command> [player]", aliases = "ci,clear")
public class Command_clearinventory extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (args.length < 1)
        {
            if (senderIsConsole)
            {
                return false;
            }

            playerSender.getInventory().clear();
            msg("Your inventory has been cleared.");
        }
        else
        {
            if (plugin.sl.isStaff(sender))
            {
                if (args[0].equals("-a"))
                {
                    FUtil.staffAction(sender.getName(), "Clearing everyone's inventory", true);
                    for (Player player : server.getOnlinePlayers())
                    {
                        player.getInventory().clear();
                    }
                    msg("Sucessfully cleared everyone's inventory.");
                }
                else
                {
                    Player player = getPlayer(args[0]);

                    if (player == null)
                    {
                        msg(PLAYER_NOT_FOUND);
                        return true;
                    }

                    player.getInventory().clear();
                    msg("Cleared " + player.getName() + "'s inventory.");
                    player.sendMessage(sender.getName() + " has cleared your inventory.");
                }
            }
            else
            {
                return noPerms();
            }
        }

        return true;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1 && plugin.sl.isStaff(sender))
        {
            List<String> players = FUtil.getPlayerList();
            players.add("-a");
            return players;
        }

        return Collections.emptyList();
    }
}
