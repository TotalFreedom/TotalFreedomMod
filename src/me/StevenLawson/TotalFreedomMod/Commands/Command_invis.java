package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Shows (optionally clears) invisisible players", usage = "/<command> [clear]")
public class Command_invis extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        boolean clear = false;
        if (args.length >= 1)
        {
            if (args[0].equalsIgnoreCase("clear"))
            {
                TFM_Util.adminAction(sender.getName(), "Clearing all invisible players", true);
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
                if (clear && !TFM_AdminList.isSuperAdmin(player))
                {
                    player.removePotionEffect(PotionEffectType.INVISIBILITY);
                    clears++;
                }
            }
        }

        if (players.isEmpty())
        {
            playerMsg("There are no invisible players");
            return true;
        }

        if (clear)
        {
            playerMsg("Cleared invisibility effect from " + clears + " players"");
        }
        else
        {
            playerMsg("Invisible players (" + players.size() + "): " + StringUtils.join(players, ", "));
        }

        return true;
    }
}
