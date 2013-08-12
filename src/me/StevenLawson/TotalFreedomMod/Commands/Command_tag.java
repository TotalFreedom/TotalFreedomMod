package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Arrays;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Sets yourself a prefix", usage = "/<command> [<prefix> | off]")
public class Command_tag extends TFM_Command
{
    
    public static final List<String> FORBIDDEN_WORDS = Arrays.asList(new String[]{"admin", "owner", "moderator", "developer"});
    
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if (TFM_Util.isStopCommand(args[0]))
        {
            TFM_PlayerData.getPlayerData(sender_p).setTag(null);
            playerMsg("Removed your tag.");
            return true;
        }

        if (args[0].length() > 15)
        {
            playerMsg("That tag is too long.");
            return true;
        }

        if (!TFM_SuperadminList.isUserSuperadmin(sender))
        {
            for (String word : FORBIDDEN_WORDS)
            {
                if (args[0].toLowerCase().contains(word))
                {
                    playerMsg("That tag contains a forbidden word.");
                    return true;
                }
            }
        }

        TFM_PlayerData.getPlayerData(sender_p).setTag(args[0]);
        playerMsg("Tag set.");

        return true;
    }

}
