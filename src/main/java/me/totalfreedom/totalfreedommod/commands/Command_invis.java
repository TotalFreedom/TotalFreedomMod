package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Shows (optionally smites) invisisible players", usage = "/<command> (smite)")
public class Command_invis extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        boolean smite = false;
        if (args.length >= 1)
        {
            if (args[0].equalsIgnoreCase("smite"))
            {
                FUtil.adminAction(sender.getName(), "Smiting all invisible players", true);
                smite = true;
            }
            else
            {
                return false;
            }
        }

        List<String> players = new ArrayList<String>();
        int smites = 0;

        for (Player player : server.getOnlinePlayers())
        {
            if (player.hasPotionEffect(PotionEffectType.INVISIBILITY))
            {
                players.add(player.getName());
                if (smite && !plugin.al.isAdmin(player))
                {
                    player.setHealth(0.0);
                    smites++;
                }
            }
        }

        if (players.isEmpty())
        {
            playerMsg("There are no invisible players");
            return true;
        }

        if (smite)
        {
            playerMsg("Smitten " + smites + " players");
        }
        else
        {
            playerMsg("Invisible players (" + players.size() + "): " + StringUtils.join(players, ", "));
        }

        return true;
    }
}
