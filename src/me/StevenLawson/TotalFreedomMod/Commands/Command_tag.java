package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Arrays;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Sets yourself a prefix", usage = "/<command> <set <tag..> | off | clear <player> | clearall>")
public class Command_tag extends TFM_Command
{
    public static final List<String> FORBIDDEN_WORDS = Arrays.asList(new String[]
    {
        "admin", "owner", "moderator", "developer", "&k"
    });

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        if (args.length == 1)
        {
            if ("clearall".equals(args[0]))
            {
                if (!TFM_AdminList.isSuperAdmin(sender))
                {
                    playerMsg(TotalFreedomMod.MSG_NO_PERMS);
                    return true;
                }

                TFM_Util.adminAction(sender.getName(), "Removing all tags", false);
                int count = 0;
                for (Player player : server.getOnlinePlayers())
                {
                    TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);

                    if (playerdata.getTag() != null)
                    {
                        count++;
                        TFM_PlayerData.getPlayerData(player).setTag(null);
                    }
                }

                playerMsg(count + " tag(s) removed.");
                return true;
            }

            if (senderIsConsole)
            {
                playerMsg("Only in-game players can set tags. Use \"/tag clearall\" to reset all tags.");
                return true;
            }

            if ("off".equals(args[0]))
            {
                TFM_PlayerData.getPlayerData(sender_p).setTag(null);
                playerMsg("Your tag has been removed.");
                return true;
            }

            if (ChatColor.stripColor(TFM_Util.colorize(args[0])).length() > 20)
            {
                playerMsg("That tag is too long [Max = 20 characters, not including color codes].");
                return true;
            }

            return false;
        }

        if (args.length == 2)
        {
            if ("clear".equals(args[0]))
            {
                if (!TFM_AdminList.isSuperAdmin(sender))
                {
                    playerMsg(TotalFreedomMod.MSG_NO_PERMS);
                    return true;
                }

                final Player player = getPlayer(args[1]);

                if (player == null)
                {
                    playerMsg(TotalFreedomMod.PLAYER_NOT_FOUND);
                    return true;
                }

                TFM_PlayerData.getPlayerData(player).setTag(null);
                playerMsg("Removed " + player.getName() + "'s tag.");
                return true;
            }
        }

        if ("set".equals(args[0]))
        {
            final String tag = StringUtils.join(args, " ", 1, args.length);

            if (!TFM_AdminList.isSuperAdmin(sender))
            {
                for (String word : FORBIDDEN_WORDS)
                {
                    if (!tag.toLowerCase().contains(word))
                    {
                        continue;
                    }

                    if (word.contains(String.valueOf(ChatColor.COLOR_CHAR)))
                    {
                        playerMsg("That tag contains a forbidden color-code.");
                    }
                    else
                    {
                        playerMsg("That tag contains a forbidden word.");
                    }
                    return true;
                }

            }

            TFM_PlayerData.getPlayerData(sender_p).setTag(TFM_Util.colorize(tag));
            playerMsg("Tag set to " + TFM_Util.colorize(tag));

            return true;
        }

        return false;
    }
}
