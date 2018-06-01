package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import net.pravian.aero.util.Ips;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Manage your verification", usage = "/<command> <enable | disable | clearips | status> <discord | forum>", aliases = "playerverification,pv")
public class Command_playerverify extends FreedomCommand
{
    @Override
    protected boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("clearips"))
            {
                VPlayer data = plugin.pv.getVerificationPlayer(playerSender);
                int cleared = 0;
                for (String ip : data.getIps())
                {
                    if (!ip.equals(Ips.getIp(playerSender)))
                    {
                        data.removeIp(ip);
                        cleared++;
                    }
                }

                msg("Cleared all IP's except your current IP \"" + Ips.getIp(playerSender) + "\"");
                msg("Cleared " + cleared + " IP's.");
                plugin.pv.saveVerificationData(data);
                return true;
            }
        }

        if (args.length < 2)
        {
            return false;
        }

        if (plugin.al.isAdmin(sender))
        {
            msg("This command is only for OP's.", ChatColor.RED);
            return true;
        }
        switch (args[0].toLowerCase())
        {
            case "enable":
                switch (args[1].toLowerCase())
                {
                    case "discord":
                        if (!plugin.dc.enabled)
                        {
                            msg("The discord verification system is currently disabled.", ChatColor.RED);
                            return true;
                        }
                        VPlayer data = plugin.pv.getVerificationPlayer(playerSender);
                        if (data.getDiscordEnabled())
                        {
                            msg("Discord verification is already enabled for you.", ChatColor.RED);
                            return true;
                        }
                        data.setDiscordEnabled(true);
                        plugin.pv.saveVerificationData(data);
                        msg("Enabled discord verification. Please type /linkdiscord to link a discord account.", ChatColor.GREEN);
                        return true;
                    case "forum":
                        msg("TODO. This will be enabled in a later update. Please use discord verification instead.");
                        return true;
                    default:
                        return false;
                }
            case "disable":
                switch (args[1].toLowerCase())
                {
                    case "discord":
                        VPlayer data = plugin.pv.getVerificationPlayer(playerSender);
                        if (!data.getDiscordEnabled())
                        {
                            msg("Discord verification is already disabled for you.", ChatColor.RED);
                            return true;
                        }
                        data.setDiscordEnabled(false);
                        plugin.pv.saveVerificationData(data);
                        msg("Disabled discord verification.", ChatColor.GREEN);
                        return true;
                    case "forum":
                        msg("TODO. Forum verification will be enabled in a later update.");
                        return true;
                    default:
                        return false;
                }
            case "status":
                switch (args[1].toLowerCase())
                {
                    case "discord":
                        VPlayer data = plugin.pv.getVerificationPlayer(playerSender);
                        boolean enabled = data.getDiscordEnabled();
                        boolean specified = data.getDiscordId() != null;
                        msg(ChatColor.GRAY + "Discord Verification Enabled: " + (enabled ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
                        msg(ChatColor.GRAY + "Discord ID: " + (specified ? ChatColor.GREEN + data.getDiscordId() : ChatColor.RED + "not set"));
                        return true;
                    case "forum":
                        msg("TODO. Forum verification will be enabled in a later update.");
                        return true;
                    default:
                        return false;
                }
            default:
                return false;
        }
    }
}
