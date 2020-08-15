package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.discord.Discord;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Link your Discord account to your Minecraft account", usage = "/<command> [<name> <id>]")
public class Command_linkdiscord extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!plugin.dc.enabled)
        {
            msg("The Discord verification system is currently disabled.", ChatColor.RED);
            return true;
        }

        if (args.length > 1 && plugin.sl.isStaff(playerSender))
        {
            PlayerData playerData = plugin.pl.getData(args[0]);
            if (playerData == null)
            {
                msg(PLAYER_NOT_FOUND);
                return true;
            }

            playerData.setDiscordID(args[1]);
            msg("Linked " + args[0] + "'s discord account.", ChatColor.GREEN);
            return true;
        }

        String code;

        PlayerData data = plugin.pl.getData(playerSender);
        if (data.getDiscordID() != null)
        {
            msg("Your Minecraft account is already linked to a Discord account.", ChatColor.RED);
            return true;
        }

        if (Discord.LINK_CODES.containsValue(data))
        {
            code = plugin.dc.getCode(data);
        }
        else
        {
            code = plugin.dc.generateCode(5);
            Discord.LINK_CODES.put(code, data);
        }
        msg("Your linking code is " + ChatColor.AQUA + code, ChatColor.GREEN);
        msg("Take this code and DM the server bot (" + plugin.dc.formatBotTag() + ") the code (do not put anything else in the message, only the code)");
        return true;
    }
}
