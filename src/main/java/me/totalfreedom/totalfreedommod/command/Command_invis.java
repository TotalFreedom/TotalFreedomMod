package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Shows (optionally clears) invisisible players", usage = "/<command> (clear)")
public class Command_invis extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        boolean clear = false;

        if (args.length >= 1)
        {
            if (args[0].equalsIgnoreCase("clear"))
            {
                FUtil.adminAction(sender.getName(), "Clearing all invis potion effect from all players", true);
                clear = true;
            }
            else
            {
                return false;
            }
        }

        List<String> players = new ArrayList<String>();
        int clears = 0;

        for (Player player : server.getOnlinePlayers())
        {
            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
            {
                players.add(player.getName());
                if (clear && !plugin.al.isAdmin(player))
                {
                    player.removePotionEffect((PotionEffectType.INVISIBILITY));
                    clears++;
                }
            }
        }

        if (players.isEmpty())
        {
            sender.sendMessage("There are no invisible players");
            return true;
        }
        if (clear)
        {
            sender.sendMessage("Cleared " + clears + " players");
        }
        else
        {
            sender.sendMessage("Invisible players (" + players.size() + "): " + StringUtils.join(players, ", "));
        }

        return true;
    }
}
