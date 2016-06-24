package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Place a cage around someone.", usage = "/<command> <purge | off | <partialname> [outermaterial] [innermaterial]>")
public class Command_cage extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        if ("off".equals(args[0]) && sender instanceof Player)
        {
            FUtil.adminAction(sender.getName(), "Uncaging " + sender.getName(), true);
            FPlayer playerdata = plugin.pl.getPlayer(playerSender);

            playerdata.getCageData().setCaged(false);
            return true;
        }
        else if ("purge".equals(args[0]))
        {
            FUtil.adminAction(sender.getName(), "Uncaging all players", true);

            for (Player player : server.getOnlinePlayers())
            {
                FPlayer playerdata = plugin.pl.getPlayer(player);
                playerdata.getCageData().setCaged(false);
            }

            return true;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        FPlayer playerdata = plugin.pl.getPlayer(player);

        Material outerMaterial = Material.GLASS;
        Material innerMaterial = Material.AIR;

        if (args.length >= 2)
        {
            if ("off".equals(args[1]))
            {
                FUtil.adminAction(sender.getName(), "Uncaging " + player.getName(), true);
                playerdata.getCageData().setCaged(false);

                return true;
            }
            else
            {
                if ("darth".equalsIgnoreCase(args[1]))
                {
                    outerMaterial = Material.SKULL;
                }
                else if (Material.matchMaterial(args[1]) != null)
                {
                    outerMaterial = Material.matchMaterial(args[1]);
                }
            }
        }

        if (args.length >= 3)
        {
            if (args[2].equalsIgnoreCase("water"))
            {
                innerMaterial = Material.STATIONARY_WATER;
            }
            else if (args[2].equalsIgnoreCase("lava"))
            {
                innerMaterial = Material.STATIONARY_LAVA;
            }
        }

        Location targetPos = player.getLocation().clone().add(0, 1, 0);
        playerdata.getCageData().cage(targetPos, outerMaterial, innerMaterial);

        player.setGameMode(GameMode.SURVIVAL);

        if (outerMaterial != Material.SKULL)
        {
            FUtil.adminAction(sender.getName(), "Caging " + player.getName(), true);
        }
        else
        {
            FUtil.adminAction(sender.getName(), "Caging " + player.getName() + " in PURE_DARTH", true);
        }

        return true;
    }
}
