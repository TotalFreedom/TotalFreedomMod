package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;


@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Shows (optionally smites) invisisible players", usage = "/<command> (smite)")
public class Command_invis extends TFM_Command {
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        boolean smite = false;
        if (args.length >= 1)
        {
            if (args[0].equalsIgnoreCase("smite"))
            {
                smite = true;
            }
            else
            {
                return false;
            }
        }

        List<String> players = new ArrayList<String>();
        int smites = 0;

        for (Player p : server.getOnlinePlayers())
        {
            if (p.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                players.add(p.getName());
                if (smite && !TFM_SuperadminList.isUserSuperadmin(p))
                {
                    server.dispatchCommand(sender, "smite " + p.getName());
                    smites++;
                }
            }
        }

        if (players.isEmpty()) {
            TFM_Util.playerMsg(sender, "There are no invisible players");
            return true;
        }

        if (smite)
        {
            TFM_Util.playerMsg(sender, "Smitten " + smites + " players");
        }
        else
        {
            TFM_Util.playerMsg(sender, "Invisble players (" + players.size() + "): " + StringUtils.join(players, ", "));
        }


        return true;
    }
}
