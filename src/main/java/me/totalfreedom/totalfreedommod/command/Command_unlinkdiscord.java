package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Unlink your Discord account from your Minecraft account", usage = "/<command> [player]")
public class Command_unlinkdiscord extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!plugin.dc.enabled)
        {
            msg("The Discord verification system is currently disabled.", ChatColor.RED);
            return true;
        }

        if (args.length != 0 && plugin.sl.isStaff(playerSender))
        {
            PlayerData playerData = plugin.pl.getData(args[0]);
            if (playerData == null)
            {
                msg(PLAYER_NOT_FOUND);
                return true;
            }

            playerData.setDiscordID(null);
            msg("Unlinked " + args[0] + "'s discord account.", ChatColor.GREEN);
            return true;
        }

        PlayerData data = plugin.pl.getData(playerSender);
        if (data.getDiscordID() == null)
        {
            msg("Your Minecraft account is not linked to a Discord account.", ChatColor.RED);
            return true;
        }
        data.setDiscordID(null);
        data.setVerification(false);
        plugin.pl.save(data);
        msg("Your Minecraft account has been successfully unlinked from the Discord account.", ChatColor.GREEN);
        return true;
    }
}
