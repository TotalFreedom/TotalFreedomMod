package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Remove forbiiden colors, things from Tags.", usage = "/<command>", aliases = "tc")
public class Command_tagclean extends TFM_Command
{
    private static final ChatColor[] BLOCKED = new ChatColor[]
    {
        ChatColor.MAGIC,
        ChatColor.STRIKETHROUGH,
        ChatColor.ITALIC,
        ChatColor.UNDERLINE,
        ChatColor.BLACK
    };
    private static final Pattern REGEX = Pattern.compile("\\u00A7[" + StringUtils.join(BLOCKED, "") + "]");

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        TFM_Util.adminAction(sender.getName(), "Cleaning all tags.", false);

        for (final Player player : server.getOnlinePlayers())
        {
            final String playerName = player.getName();
            final TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
            final String Tag = playerdata.getTag();
            if (Tag != null && !Tag.isEmpty() && !Tag.equalsIgnoreCase(playerName))
            {
                final Matcher matcher = REGEX.matcher(Tag);
                if (matcher.find())
                {
                    final String newTag = matcher.replaceAll("");
                    playerMsg(ChatColor.RESET + playerName + ": \"" + Tag + ChatColor.RESET + "\" -> \"" + newTag + ChatColor.RESET + "\".");
        
                    TFM_PlayerData.getPlayerData(player).setTag(newTag);
                }
            }
        }

        return true;
    }
}
