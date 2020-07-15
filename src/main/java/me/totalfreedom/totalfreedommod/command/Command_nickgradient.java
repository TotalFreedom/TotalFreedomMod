package me.totalfreedom.totalfreedommod.command;

import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Essentials Interface Command - Rainbowify your nickname.", usage = "/<command> <hex> <hex> <nick>", aliases = "nickgr")
public class Command_nickgradient extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 3)
        {
            return false;
        }

        String nick = args[2].trim();

        if (nick.length() < 3 || nick.length() > 30)
        {
            msg("Your nickname must be between 3 and 30 characters long.");
            return true;
        }

        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (player == playerSender)
            {
                continue;
            }
            if (player.getName().equalsIgnoreCase(nick) || ChatColor.stripColor(player.getDisplayName()).trim().equalsIgnoreCase(nick))
            {
                msg("That nickname is already in use.");
                return true;
            }
        }

        String from = "", to = "";
        java.awt.Color awt1, awt2;
        try
        {
            if (args[0].equalsIgnoreCase("random") ||
                    args[0].equalsIgnoreCase("r"))
            {
                awt1 = FUtil.getRandomAWTColor();
                from = " (From: " + FUtil.getHexStringOfAWTColor(awt1) + ")";
            }
            else
                awt1 = java.awt.Color.decode(args[0]);
            if (args[1].equalsIgnoreCase("random") ||
                    args[1].equalsIgnoreCase("r"))
            {
                awt2 = FUtil.getRandomAWTColor();
                to = " (To: " + FUtil.getHexStringOfAWTColor(awt2) + ")";
            }
            else
                awt2 = java.awt.Color.decode(args[1]);
        }
        catch (NumberFormatException ex)
        {
            msg("Invalid hex values.");
            return true;
        }
        Color c1 = FUtil.fromAWT(awt1);
        Color c2 = FUtil.fromAWT(awt2);
        List<Color> gradient = FUtil.createColorGradient(c1, c2, nick.length());
        String[] splitNick = nick.split("");
        for (int i = 0; i < splitNick.length; i++)
        {
            splitNick[i] = net.md_5.bungee.api.ChatColor.of(FUtil.toAWT(gradient.get(i))) + splitNick[i];
        }
        nick = StringUtils.join(splitNick, "");
        final String outputNick = FUtil.colorize(nick);

        plugin.esb.setNickname(sender.getName(), outputNick);

        msg("Your nickname is now: '" + outputNick + ChatColor.GRAY + "'" + from + to);

        return true;
    }
}
