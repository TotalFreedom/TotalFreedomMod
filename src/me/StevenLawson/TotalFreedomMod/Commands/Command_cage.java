package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Place a cage around someone.", usage = "/<command> <purge | off | <partialname> [outermaterial] [innermaterial]>")
public class Command_cage extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        if ("off".equals(args[0]) && sender instanceof Player)
        {
            TFM_Util.adminAction(sender.getName(), "Uncaging " + sender.getName(), true);
            TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(sender_p);

            playerdata.setCaged(false);
            playerdata.regenerateHistory();
            playerdata.clearHistory();

            return true;
        }
        else if ("purge".equals(args[0]))
        {
            TFM_Util.adminAction(sender.getName(), "Uncaging all players", true);

            for (Player player : server.getOnlinePlayers())
            {
                TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
                playerdata.setCaged(false);
                playerdata.regenerateHistory();
                playerdata.clearHistory();
            }

            return true;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            sender.sendMessage(TFM_Command.PLAYER_NOT_FOUND);
            return true;
        }

        TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);

        Material outerMaterial = Material.GLASS;
        Material innerMaterial = Material.AIR;

        if (args.length >= 2)
        {
            if ("off".equals(args[1]))
            {
                TFM_Util.adminAction(sender.getName(), "Uncaging " + player.getName(), true);

                playerdata.setCaged(false);
                playerdata.regenerateHistory();
                playerdata.clearHistory();

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
        playerdata.setCaged(true, targetPos, outerMaterial, innerMaterial);
        playerdata.regenerateHistory();
        playerdata.clearHistory();
        TFM_Util.buildHistory(targetPos, 2, playerdata);
        TFM_Util.generateHollowCube(targetPos, 2, outerMaterial);
        TFM_Util.generateCube(targetPos, 1, innerMaterial);

        player.setGameMode(GameMode.SURVIVAL);

        if (outerMaterial != Material.SKULL)
        {
            TFM_Util.adminAction(sender.getName(), "Caging " + player.getName(), true);
        }
        else
        {
            TFM_Util.adminAction(sender.getName(), "Caging " + player.getName() + " in PURE_DARTH", true);
        }

        return true;
    }
}
