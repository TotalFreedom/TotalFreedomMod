package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.admin.Admin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Unlink your Discord account to your Minecraft account", usage = "/<command>")
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

        if (plugin.al.isAdmin(playerSender))
        {
            Admin admin = plugin.al.getAdmin(playerSender);
            if (admin.getDiscordID() == null)
            {
                msg("Your Minecraft account is not linked to a Discord account.", ChatColor.RED);
                return true;
            }
            admin.setDiscordID(null);
            msg("Your Minecraft account has been successfully unlinked from the Discord account.", ChatColor.GREEN);
            return true;
        }
        else
        {
            VPlayer data = plugin.pv.getVerificationPlayer(playerSender);
            if (data.getDiscordId() == null)
            {
                msg("Your Minecraft account is not linked to a Discord account.", ChatColor.RED);
                return true;
            }
            data.setDiscordId(null);
            data.setDiscordEnabled(false);
            msg("Your Minecraft account has been successfully unlinked from the Discord account.", ChatColor.GREEN);
            return true;
        }
    }
}
