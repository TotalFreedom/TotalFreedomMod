package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.ADMIN, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Set a player's total votes", usage = "/<command> <player> <votes>")
public class Command_settotalvotes extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (!ConfigEntry.SERVER_OWNERS.getStringList().contains(sender.getName()))
        {
            return noPerms();
        }

        if (args.length < 2)
        {
            return false;
        }

        int votes = 0;
        try
        {
            votes = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e)
        {
            msg("Invalid number: " +  args[0]);
            return true;
        }

        PlayerData playerData = plugin.pl.getData(args[1]);

        if (playerData == null)
        {
            msg(PLAYER_NOT_FOUND);
            return true;
        }

        msg("Set " + args[1] + "'s votes to " + args[0]);

        playerData.setTotalVotes(votes);
        plugin.pl.save(playerData);

        Player player = getPlayer(args[1]);

        if (player != null)
        {
            player.sendMessage(ChatColor.GREEN + sender.getName() + " has set your total votes to " + votes);
        }

        return true;
    }
}
