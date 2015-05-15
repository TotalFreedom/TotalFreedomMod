package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.Arrays;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.StringUtils;
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
        "admin", "owner", "moderator", "developer", "console"
    });

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 1)
        {
            if ("list".equalsIgnoreCase(args[0]))
            {
                playerMsg("Tags for all online players:");

                for (final Player player : server.getOnlinePlayers())
                {
                    final TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
                    if (playerdata.getTag() != null)
                    {
                        playerMsg(player.getName() + ": " + playerdata.getTag());
                    }
                }

                return true;
            }
            else if ("clearall".equalsIgnoreCase(args[0]))
            {
                if (!TFM_AdminList.isSuperAdmin(sender))
                {
                    playerMsg(TFM_Command.MSG_NO_PERMS);
                    return true;
                }

                TFM_Util.adminAction(sender.getName(), "Removing all tags", false);

                int count = 0;
                for (final Player player : server.getOnlinePlayers())
                {
                    final TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
                    if (playerdata.getTag() != null)
                    {
                        count++;
                        playerdata.setTag(null);
                    }
                }

                playerMsg(count + " tag(s) removed.");

                return true;
            }
            else if ("off".equalsIgnoreCase(args[0]))
            {
                if (senderIsConsole)
                {
                    playerMsg("\"/tag off\" can't be used from the console. Use \"/tag clear <player>\" or \"/tag clearall\" instead.");
                }
                else
                {
                    TFM_PlayerData.getPlayerData(sender_p).setTag(null);
                    playerMsg("Your tag has been removed.");
                }

                return true;
            }
            else
            {
                return false;
            }
        }
        else if (args.length >= 2)
        {
            if ("clear".equalsIgnoreCase(args[0]))
            {
                if (!TFM_AdminList.isSuperAdmin(sender))
                {
                    playerMsg(TFM_Command.MSG_NO_PERMS);
                    return true;
                }

                final Player player = getPlayer(args[1]);

                if (player == null)
                {
                    playerMsg(TFM_Command.PLAYER_NOT_FOUND);
                    return true;
                }

                TFM_PlayerData.getPlayerData(player).setTag(null);
                playerMsg("Removed " + player.getName() + "'s tag.");

                return true;
            }
            else if ("set".equalsIgnoreCase(args[0]))
            {
                final String inputTag = StringUtils.join(args, " ", 1, args.length);
                final String outputTag = TFM_Util.colorize(StringUtils.replaceEachRepeatedly(StringUtils.strip(inputTag),
                        new String[]
                        {
                            "" + ChatColor.COLOR_CHAR, "&k"
                        },
                        new String[]
                        {
                            "", ""
                        })) + ChatColor.RESET;

                if (!TFM_AdminList.isSuperAdmin(sender))
                {
                    final String rawTag = ChatColor.stripColor(outputTag).toLowerCase();

                    if (rawTag.length() > 20)
                    {
                        playerMsg("That tag is too long (Max is 20 characters).");
                        return true;
                    }

                    for (String word : FORBIDDEN_WORDS)
                    {
                        if (rawTag.contains(word))
                        {
                            playerMsg("That tag contains a forbidden word.");
                            return true;
                        }
                    }
                }

                TFM_PlayerData.getPlayerData(sender_p).setTag(outputTag);
                playerMsg("Tag set to '" + outputTag + "'.");

                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}
