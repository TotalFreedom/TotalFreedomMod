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
        msg("The Discord API system is currently undergoing maintenance...", ChatColor.RED);
        msg("You can still use discord to chat in the server and vise versa.", ChatColor.RED);
        return true;
    }
}
