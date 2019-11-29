package me.totalfreedom.totalfreedommod.command;

import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Surprise someone.", usage = "/<command> <player>")
public class Command_scare extends FreedomCommand   
{
    /* This command will not work on Paper because there was a patch to remove it. This will work on Spigot and Bukkit. */
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

        msg("Scared " + player.getName());

        player.spawnParticle(Particle.MOB_APPEARANCE, player.getLocation(), 4);
        for (int i = 0; i < 10; ++i) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_SCREAM, 1, 0);
        }


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