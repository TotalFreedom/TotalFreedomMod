package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Place a cage around someone.", usage = "/<command> <purge | off | <partialname> [custom | block] [Block name | Player name(for skull)]")
public class Command_cage extends FreedomCommand
{

    public static String playerSkullName;

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
            if (null != args[1])
            {
                switch (args[1])
                {
                    case "off":
                        FUtil.adminAction(sender.getName(), "Uncaging " + player.getName(), true);
                        playerdata.getCageData().setCaged(false);

                        return true;
                    case "custom":
                        outerMaterial = Material.SKULL;
                        playerSkullName = args[2];
                        break;
                    case "block":
                        if (Material.matchMaterial(args[2]) != null)
                        {
                            outerMaterial = Material.matchMaterial(args[2]);
                        }
                        else
                        {
                            sender.sendMessage(ChatColor.RED + "Invalid block!");
                        }
                        break;
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
            FUtil.adminAction(sender.getName(), "Caging " + player.getName() + " in " + playerSkullName, true);
        }

        return true;
    }
}
