package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import net.pravian.aero.util.Ips;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Manage your verification", usage = "/<command> <<enable | disable | clearips | status>", aliases = "playerverification,pv")
public class Command_playerverify extends FreedomCommand
{
    @Override
    protected boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        VPlayer target = plugin.pv.getVerificationPlayer(playerSender);

        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("clearips"))
            {
                int cleared = 0;
                for (String ip : target.getIps())
                {
                    if (!ip.equals(Ips.getIp(playerSender)))
                    {
                        target.removeIp(ip);
                        cleared++;
                    }
                }

                msg("Cleared all IP's except your current IP \"" + Ips.getIp(playerSender) + "\"");
                msg("Cleared " + cleared + " IP's.");
                plugin.pv.saveVerificationData(target);
                return true;
            }
        }

        if (args.length < 1)
        {
            return false;
        }

        if (plugin.al.isAdmin(sender))
        {
            msg("This command is only for OP's.", ChatColor.RED);
            return true;
        }

        VPlayer data = plugin.pv.getVerificationPlayer(playerSender);

        switch (args[0].toLowerCase())
        {
            case "enable":
                if (!plugin.dc.enabled)
                {
                    msg("The Discord verification system is currently disabled.", ChatColor.RED);
                    return true;
                }
                if (data.getEnabled())
                {
                    msg("Discord verification is already enabled for you.", ChatColor.RED);
                    return true;
                }
                data.setEnabled(true);
                plugin.pv.saveVerificationData(data);
                if (data.getDiscordId() != null)
                {
                    msg("Re-enabled Discord verification.", ChatColor.GREEN);
                }
                else
                {
                    msg("Enabled Discord verification. Please type /linkdiscord to link a Discord account.", ChatColor.GREEN);
                }
                return true;

            case "disable":
                if (!data.getEnabled())
                {
                    msg("Discord verification is already disabled for you.", ChatColor.RED);
                    return true;
                }
                data.setEnabled(false);
                plugin.pv.saveVerificationData(data);
                msg("Disabled Discord verification.", ChatColor.GREEN);
                return true;

            case "status":
                boolean enabled = target.getEnabled();
                boolean specified = target.getDiscordId() != null;
                msg(ChatColor.GRAY + "Discord Verification Enabled: " + (enabled ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
                msg(ChatColor.GRAY + "Discord ID: " + (specified ? ChatColor.GREEN + target.getDiscordId() : ChatColor.RED + "not set"));
                return true;
            default:
                return false;
        }
    }
}
