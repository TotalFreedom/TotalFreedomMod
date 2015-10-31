package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.StevenLawson.TotalFreedomMod.Bridge.TFM_EssentialsBridge;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "TFM Interface Command - Remove distracting things from tags of all players on server.", usage = "/<command>", aliases = "tc")
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
    public static final String[] BLOCKED_WORDS = new String[]
    {
        "super admin",
        "telnet admin",
        "owner",
        "developer",
        "senior admin",
        "mod"
    };
    private static final Pattern REGEX = Pattern.compile("\\u00A7[" + StringUtils.join(BLOCKED, "") + "]");

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        TFM_Util.adminAction(sender.getName(), "Cleaning all tags.", false);

        for (final Player player : Bukkit.getOnlinePlayers())
        {
            final String playerName = player.getName();
            final TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
            if (playerdata.getTag() != null && !playerdata.getTag().isEmpty() && !TFM_AdminList.isSuperAdmin(player))
            {
                final Matcher matcher = REGEX.matcher(playerdata.getTag());

                if (matcher.find())
                {
                    final String newTag = matcher.replaceAll("");
                    playerMsg(ChatColor.RESET + playerName + ": \"" + playerdata.getTag() + ChatColor.RESET + "\" -> \"" + newTag + ChatColor.RESET + "\".");
                    TFM_PlayerData.getPlayerData(sender_p).setTag(newTag);
                }
                
                if (playerdata.getTag().contains(Arrays.toString(BLOCKED_WORDS)))
                {
                    TFM_PlayerData.getPlayerData(sender_p).setTag("");
                    player.sendMessage("Your tag has been removed due to illegal words/characters");
                }

            }

        }
        return true;
    }
}
