package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Manage your verification", usage = "/<command> <enable | disable | clearips | clearip <ip> | status | genbackupcodes>", aliases = "playerverify,pv")
public class Command_playerverification extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        PlayerData target = plugin.pl.getData(playerSender);

        List<String> ips = new ArrayList<>();
        ips.addAll(target.getIps());

        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("clearips"))
            {
                int cleared = 0;
                for (String ip : ips)
                {
                    if (!ip.equals(FUtil.getIp(playerSender)))
                    {
                        target.removeIp(ip);
                        cleared++;
                    }
                }

                msg("Cleared all IP's except your current IP \"" + FUtil.getIp(playerSender) + "\"");
                msg("Cleared " + cleared + " IP's.");
                plugin.pl.save(target);
                plugin.pl.syncIps(target);
                return true;
            }
            else if (args[0].equalsIgnoreCase("clearip"))
            {
                if (args.length < 2)
                {
                    return false;
                }
                target.removeIp(args[1]);
                msg("Removed" + args[1] + " from your list of IPs");
                plugin.pl.save(target);
                plugin.pl.syncIps(target);
                return true;
            }
        }

        if (args.length < 1)
        {
            return false;
        }

        PlayerData data = plugin.pl.getData(playerSender);

        switch (args[0].toLowerCase())
        {
            case "enable":
                if (!plugin.dc.enabled)
                {
                    msg("The Discord verification system is currently disabled.", ChatColor.RED);
                    return true;
                }
                else if (data.hasVerification())
                {
                    msg("Discord verification is already enabled for you.", ChatColor.RED);
                    return true;
                }
                else if (data.getDiscordID() == null)
                {
                    msg("Please link a discord account with /linkdiscord.", ChatColor.RED);
                    return true;
                }
                data.setVerification(true);
                plugin.pl.save(data);
                msg("Re-enabled Discord verification.", ChatColor.GREEN);
                return true;

            case "disable":
                if (!data.hasVerification())
                {
                    msg("Discord verification is already disabled for you.", ChatColor.RED);
                    return true;
                }
                data.setVerification(false);
                plugin.pl.save(data);
                msg("Disabled Discord verification.", ChatColor.GREEN);
                return true;

            case "status":
                boolean enabled = target.hasVerification();
                boolean specified = target.getDiscordID() != null;
                msg(ChatColor.GRAY + "Discord Verification Enabled: " + (enabled ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));
                msg(ChatColor.GRAY + "Discord ID: " + (specified ? ChatColor.GREEN + target.getDiscordID() : ChatColor.RED + "not set"));
                msg(ChatColor.GRAY + "Backup Codes: " + data.getBackupCodes().size() + "/" + "10");
                return true;

            case "genbackupcodes":
                if (!plugin.dc.enabled)
                {
                    msg("The Discord verification system is currently disabled.", ChatColor.RED);
                    return true;
                }
                else if (!data.hasVerification())
                {
                    msg("Discord verification is not enabled for you.", ChatColor.RED);
                    return true;
                }

                boolean generated = plugin.dc.sendBackupCodes(data);

                if (generated)
                {
                    msg("Your backup codes have been sent to your discord account. They can be re-generated at anytime.", ChatColor.GREEN);
                }
                else
                {
                    msg("Failed to generate backup codes, please contact a developer (preferably Seth)", ChatColor.RED);
                }
                return true;

            default:
                return false;
        }
    }
    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1)
        {
            return Arrays.asList("enable", "disable", "status", "clearips", "genbackupcodes");
        }

        return Collections.emptyList();
    }
}
