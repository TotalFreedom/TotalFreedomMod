package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Essentials Interface Command - Remove illegal chatcodes from nicknames of one or all players on server.", usage = "/<command> [player]", aliases = "nc")
public class Command_nickclean extends FreedomCommand
{
    private Map<String, Color> colorCodes = new HashMap<String, Color>()
    {{
        put("&0", Color.BLACK);
        put("&1", Color.BLUE);
        put("&2", Color.GREEN);
        put("&3", Color.TEAL);
        put("&4", Color.MAROON);
        put("&5", Color.FUCHSIA);
        put("&6", Color.OLIVE);
        put("&7", Color.SILVER);
        put("&8", Color.GRAY);
        put("&9", Color.NAVY);
        put("&a", Color.LIME);
        put("&b", Color.AQUA);
        put("&c", Color.RED);
        put("&d", Color.PURPLE);
        put("&e", Color.YELLOW);
        put("&f", Color.WHITE);
    }};

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 1)
        {
            Player player = getPlayer(args[0]);

            if (player == null)
            {
                msg(PLAYER_NOT_FOUND);
                return true;
            }

            FUtil.staffAction(sender.getName(), "Cleaning " + player.getName() + "'s nickname", false);
            cleanNickname(player);
            return true;
        }

        FUtil.staffAction(sender.getName(), "Cleaning all nicknames", false);
        for (final Player player : server.getOnlinePlayers())
        {
            cleanNickname(player);
        }
        return true;
    }

    public void cleanNickname(Player player)
    {
        final String playerName = player.getName();
        final String nickName = plugin.esb.getNickname(playerName);
        StringBuilder newNick = new StringBuilder();
        boolean nickChanged = false;

        if(nickName.contains("§x"))
        {
            // Detects colors that are similar to blocked codes.
            spliterator:
            for (String split : nickName.split("§x"))
            {
                List<Color> colors = new ArrayList<>();
                String hexColorSub = null;
                if (split.length() >= 12 && split.contains("§"))
                {
                    hexColorSub = split.substring(0, 12);
                    split = String.valueOf(split.charAt(12));
                    String hexColorString = "#" + hexColorSub.replaceAll("§", "");
                    java.awt.Color hexColor = java.awt.Color.decode(hexColorString);

                    // Get a range of nearby colors that are alike to the color blocked.
                    Color colorFirst;
                    Color colorSecond;

                    colorFirst = Color.fromRGB(Math.min(hexColor.getRed() + 20, 255), Math.min(hexColor.getGreen() + 20, 255), Math.min(hexColor.getBlue() + 20, 255));
                    colorSecond = Color.fromRGB(Math.max(hexColor.getRed() - 20, 0), Math.max(hexColor.getGreen() - 20, 0), Math.max(hexColor.getBlue() - 20, 0));
                    colors.addAll(FUtil.createColorGradient(colorFirst, colorSecond, 40));

                    colorFirst = Color.fromRGB(Math.min(hexColor.getRed() + 20, 255), Math.min(hexColor.getGreen(), 255), Math.min(hexColor.getBlue(), 255));
                    colorSecond = Color.fromRGB(Math.max(hexColor.getRed() - 20, 0), Math.max(hexColor.getGreen(), 0), Math.max(hexColor.getBlue(), 0));
                    colors.addAll(FUtil.createColorGradient(colorFirst, colorSecond, 40));

                    colorFirst = Color.fromRGB(Math.min(hexColor.getRed(), 255), Math.min(hexColor.getGreen() + 20, 255), Math.min(hexColor.getBlue(), 255));
                    colorSecond = Color.fromRGB(Math.max(hexColor.getRed(), 0), Math.max(hexColor.getGreen() - 20, 0), Math.max(hexColor.getBlue(), 0));
                    colors.addAll(FUtil.createColorGradient(colorFirst, colorSecond, 40));

                    colorFirst = Color.fromRGB(Math.min(hexColor.getRed(), 255), Math.min(hexColor.getGreen(), 255), Math.min(hexColor.getBlue() + 20, 255));
                    colorSecond = Color.fromRGB(Math.max(hexColor.getRed(), 0), Math.max(hexColor.getGreen(), 0), Math.max(hexColor.getBlue() - 20, 0));
                    colors.addAll(FUtil.createColorGradient(colorFirst, colorSecond, 40));

                    for (String colorCode : ConfigEntry.BLOCKED_CHATCODES.getString().split(","))
                    {
                        // Makes sure that there's hex colors in the split.
                        for (Color color : colors)
                        {
                            if (colorCodes.get(colorCode) != null && FUtil.colorClose(color, colorCodes.get(colorCode), 40))
                            {
                                nickChanged = true;
                                newNick.append(split);
                                continue spliterator;
                            }
                        }

                    }
                    newNick.append("§x").append(hexColorSub).append(split);
                }
            }
        }
        else
        {
            // Falls back on old code if hex isn't used.
            final Pattern REGEX = Pattern.compile(FUtil.colorize(ChatColor.COLOR_CHAR + "[" + StringUtils.join(ConfigEntry.BLOCKED_CHATCODES.getString().split(","), "") + "]"), Pattern.CASE_INSENSITIVE);
            if (!nickName.isEmpty() && !nickName.equalsIgnoreCase(playerName))
            {
                final Matcher matcher = REGEX.matcher(nickName);
                if (matcher.find())
                {
                    nickChanged = true;
                    newNick.append(matcher.replaceAll(""));
                }
            }
        }

        if(nickChanged)
        {
            msg(ChatColor.RESET + playerName + ": \"" + nickName + ChatColor.RESET + "\" -> \"" + newNick.toString() + ChatColor.RESET + "\".");
        }
        plugin.esb.setNickname(playerName, newNick.toString());
    }
}