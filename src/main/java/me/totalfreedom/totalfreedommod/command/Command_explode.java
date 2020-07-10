package me.totalfreedom.totalfreedommod.command;

import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Surprise someone.", usage = "/<command> <player>")
public class Command_explode extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (args.length == 0)
        {
            return false;
        }

        final Player player = getPlayer(args[0]);

        if (player == null)
        {
            msg(FreedomCommand.PLAYER_NOT_FOUND);
            return true;
        }

        player.setFlying(false);
        player.setVelocity(player.getVelocity().clone().add(new Vector(0, 50, 0)));
        for (int i = 1; i <= 3; i++)
        {
            FUtil.createExplosionOnDelay(player.getLocation(), 2L, i * 10);
        }
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (int i = 0; i < 4; i++)
                {
                    player.getWorld().strikeLightning(player.getLocation());
                    player.getWorld().createExplosion(player.getLocation(), 4L);
                }
                player.setHealth(0.0);
                msg("Exploded " + player.getName());
            }
        }.runTaskLater(plugin, 40);

        return true;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1 && plugin.al.isAdmin(sender))
        {
            return FUtil.getPlayerList();
        }
        return Collections.emptyList();
    }
}