package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "NickFilter: Prefix any command with this command to replace nicknames in that command with real names. Nicknames should be prefixed with a !.", usage = "/<command> <other_command> !<playernick>")
public class Command_nf extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        boolean nickMatched = false;

        final List<String> outputCommand = new ArrayList<String>();

        if (args.length >= 1)
        {
            final List<String> argsList = Arrays.asList(args);
            for (String arg : argsList)
            {
                Player player = null;

                Matcher matcher = Pattern.compile("^!(.+)$").matcher(arg);
                if (matcher.find())
                {
                    String displayName = matcher.group(1);
                    try
                    {
                        player = getPlayerByDisplayName(displayName);
                    }
                    catch (PlayerNotFoundException ex)
                    {
                    }
                    if (player == null)
                    {
                        try
                        {
                            player = getPlayerByDisplayNameAlt(displayName);
                        }
                        catch (PlayerNotFoundException ex)
                        {
                            sender.sendMessage(ChatColor.GRAY + "Can't find player by nickname: " + displayName);
                            return true;
                        }
                    }
                }

                if (player == null)
                {
                    outputCommand.add(arg);
                }
                else
                {
                    nickMatched = true;
                    outputCommand.add(player.getName());
                }
            }
        }

        if (!nickMatched)
        {
            sender.sendMessage("No nicknames replaced in command.");
            return true;
        }

        String newCommand = StringUtils.join(outputCommand, " ");
        sender.sendMessage("Sending command: \"" + newCommand + "\".");
        server.dispatchCommand(sender, newCommand);

        return true;
    }

    private static Player getPlayerByDisplayName(String needle) throws PlayerNotFoundException
    {
        needle = needle.toLowerCase().trim();

        Player[] onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers)
        {
            if (player.getDisplayName().toLowerCase().trim().contains(needle))
            {
                return player;
            }
        }

        throw new PlayerNotFoundException();
    }

    private static Player getPlayerByDisplayNameAlt(String needle) throws PlayerNotFoundException
    {
        needle = needle.toLowerCase().trim();

        Integer minEditDistance = null;
        Player minEditMatch = null;

        Player[] onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers)
        {
            String haystack = player.getDisplayName().toLowerCase().trim();
            int editDistance = StringUtils.getLevenshteinDistance(needle, haystack.toLowerCase());
            if (minEditDistance == null || minEditDistance.intValue() > editDistance)
            {
                minEditDistance = editDistance;
                minEditMatch = player;
            }
        }

        if (minEditMatch == null)
        {
            throw new PlayerNotFoundException();
        }

        return minEditMatch;
    }
}
