package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.StringUtils;
+import java.util.Arrays;
+import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Gives you a tag with random colors", usage = "/<command> <tag>", aliases = "tn")
public class Command_tagnyan extends TFM_Command
{
    public static final List<String> FORBIDDEN_WORDS = Arrays.asList(new String[]
    {
        "admin", "owner", "moderator", "developer", "console, super admin, superadmin, sa, sta, sra"
    });
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        final StringBuilder tag = new StringBuilder();

        for (char c : ChatColor.stripColor(TFM_Util.colorize(StringUtils.join(args, " "))).toCharArray())
        {
            tag.append(TFM_Util.randomChatColor()).append(c);
        }

        final TFM_PlayerData data = TFM_PlayerData.getPlayerData(sender_p);
        data.setTag(tag.toString());
        
        if (!TFM_AdminList.isSuperAdmin(sender))
        {
            for (String word : FORBIDDEN_WORDS)
            {
                if (args[0].contains(word))
                {
                    playerMsg("That tag contains a forbidden word.");
                    return true;
                }
            }
        }

        playerMsg("Set tag to " + tag);

        return true;
    }
}
