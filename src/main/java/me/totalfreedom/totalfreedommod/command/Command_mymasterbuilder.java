package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.masterbuilder.MasterBuilder;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.util.Ips;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Manage my Master Builder entry", usage = "/<command> [-o <masterbuilder>] <clearips | clearip <ip> | genbackupcodes>", aliases = "mymb")
public class Command_mymasterbuilder extends FreedomCommand
{

    @Override
    protected boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        Player init = null;
        MasterBuilder target = plugin.mbl.getMasterBuilder(playerSender);
        Player targetPlayer = playerSender;

        // -o switch
        if (args[0].equals("-o"))
        {
            if (!FUtil.canManageMasterBuilders(playerSender.getName()))
            {
                return noPerms();
            }
            init = playerSender;
            targetPlayer = getPlayer(args[1]);
            if (targetPlayer == null)
            {
                msg(FreedomCommand.PLAYER_NOT_FOUND);
                return true;
            }

            target = plugin.mbl.getMasterBuilder(playerSender);
            if (target == null)
            {
                msg("That player is not a Master Builder", ChatColor.RED);
                return true;
            }

            // Shift 2
            args = Arrays.copyOfRange(args, 2, args.length);
            if (args.length < 1)
            {
                return false;
            }
        }

        final String targetIp = Ips.getIp(targetPlayer);

        switch (args[0])
        {
            case "clearips":
            {
                if (args.length != 1)
                {
                    return false; // Double check: the player might mean "clearip"
                }

                if (init == null)
                {
                    FUtil.adminAction(sender.getName(), "Clearing my IPs", false);
                }
                else
                {
                    FUtil.adminAction(sender.getName(), "Clearing " + target.getName() + "' IPs", true);
                }

                int counter = target.getIps().size() - 1;
                target.clearIPs();
                target.addIp(targetIp);

                plugin.mbl.save();
                plugin.mbl.updateTables();

                msg(counter + " IPs removed.");
                msg(targetPlayer, target.getIps().get(0) + " is now your only IP address");
                return true;
            }

            case "clearip":
            {
                if (args.length != 2)
                {
                    return false; // Double check: the player might mean "clearips"
                }

                if (!target.getIps().contains(args[1]))
                {
                    if (init == null)
                    {
                        msg("That IP is not registered to you.");
                    }
                    else
                    {
                        msg("That IP does not belong to that player.");
                    }
                    return true;
                }

                if (targetIp.equals(args[1]))
                {
                    if (init == null)
                    {
                        msg("You cannot remove your current IP.");
                    }
                    else
                    {
                        msg("You cannot remove that Master Builders's current IP.");
                    }
                    return true;
                }

                FUtil.adminAction(sender.getName(), "Removing an IP" + (init == null ? "" : " from " + targetPlayer.getName() + "'s IPs"), true);

                target.removeIp(args[1]);
                plugin.mbl.save();
                plugin.mbl.updateTables();

                msg("Removed IP " + args[1]);
                msg("Current IPs: " + StringUtils.join(target.getIps(), ", "));
                return true;
            }

            case "genbackupcodes":
                if (!plugin.dc.enabled)
                {
                    msg("The Discord verification system is currently disabled.", ChatColor.RED);
                    return true;
                }
                else if (target.getDiscordID() == null || target.getDiscordID().isEmpty())
                {
                    msg("Discord verification is not enabled for you.", ChatColor.RED);
                    return true;
                }

                msg("Generating backup codes...", ChatColor.GREEN);

                boolean generated = plugin.dc.sendBackupCodes(target);

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
            {
                return false;
            }
        }
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {

        if (!plugin.mbl.isMasterBuilder(playerSender) && !FUtil.canManageMasterBuilders(playerSender.getName()))
        {
            return Collections.emptyList();
        }

        List<String> singleArguments = Arrays.asList("clearips");
        List<String> doubleArguments = Arrays.asList("clearip", "genbackupcodes");
        if (args.length == 1)
        {
            List<String> options = new ArrayList<>();
            options.add("-o");
            options.addAll(singleArguments);
            options.addAll(doubleArguments);
            return options;
        }
        else if (args.length == 2)
        {
            if (args[0].equals("-o"))
            {
                return FUtil.getPlayerList();
            }
            else
            {
                if (doubleArguments.contains(args[0]))
                {
                    if (args[0].equals("clearip"))
                    {
                        if (args[0].equals("clearip"))
                        {
                            List<String> ips = plugin.mbl.getMasterBuilder(sender).getIps();
                            ips.remove(Ips.getIp(playerSender));
                            return ips;
                        }
                    }
                }
            }
        }
        else if (args.length == 3)
        {
            if (args[0].equals("-o"))
            {
                List<String> options = new ArrayList<>();
                options.addAll(singleArguments);
                options.addAll(doubleArguments);
                return options;
            }
        }
        else if (args.length == 4)
        {
            if (args[0].equals("-o") && args[2].equals("clearip"))
            {
                MasterBuilder masterBuilder = plugin.mbl.getEntryByName(args[1]);
                if (masterBuilder != null)
                {
                    return masterBuilder.getIps();
                }
            }
        }
        return FUtil.getPlayerList();
    }
}
