package me.totalfreedom.totalfreedommod.command;

import java.util.Arrays;
import java.util.List;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Allows you to set your own prefix.", usage = "/<command> [-s[ave]] <set <tag..> | list | gradient <hex> <hex> <tag..> | off | clear <player> | clearall>")
public class Command_tag extends FreedomCommand
{

    public static final List<String> FORBIDDEN_WORDS = Arrays.asList(
            "admin", "owner", "moderator", "developer", "console", "dev", "staff", "mod", "sra", "tca", "sta", "sa");

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        boolean save = false;

        if (args.length < 1)
        {
            return false;
        }

        if (args[0].equals("-s") || args[0].equals("-save"))
        {
            save = true;
            args = ArrayUtils.remove(args, 0);
        }

        if (args.length == 1)
        {

            if ("list".equalsIgnoreCase(args[0]))
            {
                msg("Tags for all online players:");

                for (final Player player : server.getOnlinePlayers())
                {
                    if (plugin.sl.isVanished(player.getName()) && !plugin.sl.isStaff(sender))
                    {
                        continue;
                    }
                    final FPlayer playerdata = plugin.pl.getPlayer(player);
                    if (playerdata.getTag() != null)
                    {
                        msg(player.getName() + ": " + playerdata.getTag());
                    }
                }

                return true;
            }
            else if ("clearall".equalsIgnoreCase(args[0]))
            {
                if (!plugin.sl.isStaff(sender))
                {
                    noPerms();
                    return true;
                }

                FUtil.staffAction(sender.getName(), "Removing all tags", false);

                int count = 0;
                for (final Player player : server.getOnlinePlayers())
                {
                    final FPlayer playerdata = plugin.pl.getPlayer(player);
                    if (playerdata.getTag() != null)
                    {
                        count++;
                        playerdata.setTag(null);
                    }
                }

                msg(count + " tag(s) removed.");

                return true;
            }
            else if ("off".equalsIgnoreCase(args[0]))
            {
                if (senderIsConsole)
                {
                    msg("\"/tag off\" can't be used from the console. Use \"/tag clear <player>\" or \"/tag clearall\" instead.");
                }
                else
                {
                    plugin.pl.getPlayer(playerSender).setTag(null);
                    if (save)
                    {
                        save(playerSender, null);
                    }
                    msg("Your tag has been removed." + (save ? " (Saved)" : ""));
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
                if (!plugin.sl.isStaff(sender))
                {
                    noPerms();
                    return true;
                }

                final Player player = getPlayer(args[1]);

                if (player == null)
                {
                    msg(FreedomCommand.PLAYER_NOT_FOUND);
                    return true;
                }

                plugin.pl.getPlayer(player).setTag(null);
                if (save)
                {
                    save(player, null);
                }
                msg("Removed " + player.getName() + "'s tag." + (save ? " (Saved)" : ""));

                return true;
            }
            else if ("set".equalsIgnoreCase(args[0]))
            {
                if (senderIsConsole)
                {
                    msg("\"/tag set\" can't be used from the console.");
                    return true;
                }

                final String inputTag = StringUtils.join(args, " ", 1, args.length);
                final String strippedTag = StringUtils.replaceEachRepeatedly(StringUtils.strip(inputTag),
                        new String[]
                                {
                                        "" + ChatColor.COLOR_CHAR, "&k"
                                },
                        new String[]
                                {
                                        "", ""
                                });
                final String outputTag = FUtil.colorize(strippedTag);

                int tagLimit = (plugin.sl.isStaff(sender) ? 30 : 20);

                final String rawTag = ChatColor.stripColor(outputTag).toLowerCase();

                if (rawTag.length() > tagLimit)
                {
                    msg("That tag is too long (Max is " + tagLimit + " characters).");
                    return true;
                }

                if (!plugin.sl.isStaff(sender))
                {
                    for (String word : FORBIDDEN_WORDS)
                    {
                        if (rawTag.contains(word))
                        {
                            msg("That tag contains a forbidden word.");
                            return true;
                        }
                    }
                }

                plugin.pl.getPlayer(playerSender).setTag(outputTag);
                if (save)
                {
                    save(playerSender, strippedTag);
                }
                msg("Tag set to '" + outputTag + ChatColor.GRAY + "'." + (save ? " (Saved)" : ""));

                return true;
            }
            else if (args[0].equalsIgnoreCase("gradient"))
            {
                if (senderIsConsole)
                {
                    msg("\"/tag gradient\" can't be used from the console.");
                    return true;
                }

                String from = "", to = "";
                java.awt.Color awt1, awt2;
                try
                {
                    if (args[1].equalsIgnoreCase("random") ||
                            args[1].equalsIgnoreCase("r"))
                    {
                        awt1 = FUtil.getRandomAWTColor();
                        from = " (From: " + FUtil.getHexStringOfAWTColor(awt1) + ")";
                    }
                    else
                        awt1 = java.awt.Color.decode(args[1]);
                    if (args[2].equalsIgnoreCase("random") ||
                            args[2].equalsIgnoreCase("r"))
                    {
                        awt2 = FUtil.getRandomAWTColor();
                        to = " (To: " + FUtil.getHexStringOfAWTColor(awt2) + ")";
                    }
                    else
                        awt2 = java.awt.Color.decode(args[2]);
                }
                catch (NumberFormatException ex)
                {
                    msg("Invalid hex values.");
                    return true;
                }
                Color c1 = FUtil.fromAWT(awt1);
                Color c2 = FUtil.fromAWT(awt2);
                String tag = StringUtils.join(args, " ", 3, args.length);
                List<Color> gradient = FUtil.createColorGradient(c1, c2, tag.length());
                String[] splitTag = tag.split("");
                for (int i = 0; i < splitTag.length; i++)
                {
                    splitTag[i] = net.md_5.bungee.api.ChatColor.of(FUtil.toAWT(gradient.get(i))) + splitTag[i];
                }
                tag = StringUtils.join(splitTag, "");
                final String outputTag = FUtil.colorize(tag);

                int tagLimit = (plugin.sl.isStaff(sender) ? 30 : 20);

                final String rawTag = ChatColor.stripColor(outputTag).toLowerCase();

                if (rawTag.length() > tagLimit)
                {
                    msg("That tag is too long (Max is " + tagLimit + " characters).");
                    return true;
                }

                if (!plugin.sl.isStaff(sender))
                {
                    for (String word : FORBIDDEN_WORDS)
                    {
                        if (rawTag.contains(word))
                        {
                            msg("That tag contains a forbidden word.");
                            return true;
                        }
                    }
                }

                plugin.pl.getPlayer(playerSender).setTag(outputTag);
                if (save)
                {
                    save(playerSender, tag);
                }
                msg("Tag set to '" + outputTag + ChatColor.GRAY + "'." + (save ? " (Saved)" : "") + from + to);

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

    public void save(Player player, String tag)
    {
        PlayerData playerData = plugin.pl.getData(player);
        playerData.setTag(tag);
        plugin.pl.save(playerData);
    }
}