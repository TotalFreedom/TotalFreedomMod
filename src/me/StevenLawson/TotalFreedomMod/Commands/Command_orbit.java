package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "POW!!! Right in the kisser! One of these days Alice, straight to the Moon!",
        usage = "/<command> <target> [<<power> | stop>]")
public class Command_orbit extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        Player player = getPlayer(args[0]);

        if (player == null)
        {
            playerMsg(TFM_Command.PLAYER_NOT_FOUND, ChatColor.RED);
            return true;
        }

        TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);

        double strength = 10.0;

        if (args.length >= 2)
        {
            if (args[1].equals("stop"))
            {
                playerMsg("Stopped orbiting " + player.getName());
                playerdata.stopOrbiting();
                return true;
            }

            try
            {
                strength = Math.max(1.0, Math.min(150.0, Double.parseDouble(args[1])));
            }
            catch (NumberFormatException ex)
            {
                playerMsg(ex.getMessage(), ChatColor.RED);
                return true;
            }
        }

        player.setGameMode(GameMode.SURVIVAL);
        playerdata.startOrbiting(strength);

        player.setVelocity(new Vector(0, strength, 0));
        TFM_Util.adminAction(sender.getName(), "Orbiting " + player.getName(), false);

        return true;
    }
}
