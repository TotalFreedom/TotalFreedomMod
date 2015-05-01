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
@CommandParameters(description = "Shows (optionally smites) invisisible players", usage = "/<command> (smite)")
public class Command_invis extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        boolean smite = false;
        if (args.length >= 1)
        {
            if (args[0].equalsIgnoreCase("smite"))
            {
                TFM_Util.adminAction(sender.getName(), "Smiting all invisible players", true);
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
                if (smite && !TFM_AdminList.isSuperAdmin(player))
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
