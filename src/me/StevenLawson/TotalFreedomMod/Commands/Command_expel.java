package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Push people away from you.", usage = "/<command> [radius] [strength]")
public class Command_expel extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        double radius = 20.0;
        double strength = 5.0;

        if (args.length >= 1)
        {
            try
            {
                radius = Math.max(1.0, Math.min(100.0, Double.parseDouble(args[0])));
            }
            catch (NumberFormatException ex)
            {
            }
        }

        if (args.length >= 2)
        {
            try
            {
                strength = Math.max(0.0, Math.min(50.0, Double.parseDouble(args[1])));
            }
            catch (NumberFormatException ex)
            {
            }
        }

        List<String> pushedPlayers = new ArrayList<String>();

        final Vector senderPos = sender_p.getLocation().toVector();
        final List<Player> players = sender_p.getWorld().getPlayers();
        for (final Player player : players)
        {
            if (player.equals(sender_p))
            {
                continue;
            }

            final Location targetPos = player.getLocation();
            final Vector targetPosVec = targetPos.toVector();

            boolean inRange = false;
            try
            {
                inRange = targetPosVec.distanceSquared(senderPos) < (radius * radius);
            }
            catch (IllegalArgumentException ex)
            {
            }

            if (inRange)
            {
                player.getWorld().createExplosion(targetPos, 0.0f, false);
                TFM_Util.setFlying(player, false);
                player.setVelocity(targetPosVec.subtract(senderPos).normalize().multiply(strength));
                pushedPlayers.add(player.getName());
            }
        }

        if (pushedPlayers.isEmpty())
        {
            playerMsg("No players pushed.");
        }
        else
        {
            playerMsg("Pushed " + pushedPlayers.size() + " players: " + StringUtils.join(pushedPlayers, ", "));
        }

        return true;
    }
}
