package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.Bridge.TFM_EssentialsBridge;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Essentials Interface Command - Nyanify your nickname.", usage = "/<command> <<nick> | off>")
public class Command_nicknyan extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if ("off".equals(args[0]))
        {
            TFM_EssentialsBridge.setNickname(sender.getName(), null);
            playerMsg("Nickname cleared.");
            return true;
        }

        final String nickPlain = ChatColor.stripColor(TFM_Util.colorize(args[0].trim()));

        if (!nickPlain.matches("^[a-zA-Z_0-9\u00a7]+$"))
        {
            playerMsg("That nickname contains invalid characters.");
            return true;
        }
        else if (nickPlain.length() < 4 || nickPlain.length() > 30)
        {
            playerMsg("Your nickname must be between 4 and 30 characters long.");
            return true;
        }

        for (Player player : Bukkit.getOnlinePlayers())
        {
            if (player == sender_p)
            {
                continue;
            }
            if (player.getName().equalsIgnoreCase(nickPlain) || ChatColor.stripColor(player.getDisplayName()).trim().equalsIgnoreCase(nickPlain))
            {
                playerMsg("That nickname is already in use.");
                return true;
            }
        }

        final StringBuilder newNick = new StringBuilder();

        final char[] chars = nickPlain.toCharArray();
        for (char c : chars)
        {
            newNick.append(TFM_Util.randomChatColor()).append(c);
        }

        newNick.append(ChatColor.WHITE);

        TFM_EssentialsBridge.setNickname(sender.getName(), newNick.toString());

        playerMsg("Your nickname is now: " + newNick.toString());

        return true;
    }
}
