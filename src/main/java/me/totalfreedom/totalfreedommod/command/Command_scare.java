package me.totalfreedom.totalfreedommod.command;

import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Sends a guardian particle effect with an enderman scream to the specified player.", usage = "/<command> <player>")
public class Command_scare extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!FUtil.isPaper())
        {
            msg("This command won't work without Paper!", ChatColor.RED);
            return true;
        }

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
        player.sendMessage(ChatColor.RED + "ZING");

        player.spawnParticle(Particle.MOB_APPEARANCE, player.getLocation(), 4);
        for (int i = 0; i < 10; ++i)
        {
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
