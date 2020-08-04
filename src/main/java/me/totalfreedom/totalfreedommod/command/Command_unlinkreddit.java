package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Unlink your reddit account", usage = "/<command>")
public class Command_unlinkreddit extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!plugin.rd.enabled)
        {
            msg("The reddit system is currently disabled.", ChatColor.RED);
            return true;
        }

        PlayerData data = getData(playerSender);

        if (data.getRedditUsername() == null)
        {
            msg("You don't have a reddit account linked.", ChatColor.RED);
            return true;
        }

        plugin.rd.removeFlair(data.getRedditUsername());

        data.setRedditUsername(null);
        plugin.pl.save(data);

        msg("Successfully unlinked your reddit account. If you had a flair, it was removed.", ChatColor.GREEN);

        return true;
    }
}
