package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Push people away from you.", usage = "/<command> [radius] [strength]")
public class Command_expel extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
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

        List<String> pushedPlayers = new ArrayList<>();

        final Vector senderPos = playerSender.getLocation().toVector();
        final List<Player> players = playerSender.getWorld().getPlayers();
        for (final Player player : players)
        {
            if (player.equals(playerSender))
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
                FUtil.setFlying(player, false);
                player.setVelocity(targetPosVec.subtract(senderPos).normalize().multiply(strength));
                pushedPlayers.add(player.getName());
            }
        }

        if (pushedPlayers.isEmpty())
        {
            msg("No players pushed.");
        }
        else
        {
            msg("Pushed " + pushedPlayers.size() + " players: " + StringUtils.join(pushedPlayers, ", "));
        }

        return true;
    }
}
