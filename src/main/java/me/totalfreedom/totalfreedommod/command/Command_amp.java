package me.totalfreedom.totalfreedommod.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.staff.StaffMember;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Manage your AMP account", usage = "/<command> <create | resetpassword>")
public class Command_amp extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (!plugin.amp.isEnabled())
        {
            msg("AMP integration is currently disabled.", ChatColor.RED);
            return true;
        }

        PlayerData playerData = getData(playerSender);

        if (playerData.getDiscordID() == null)
        {
            msg("You must have a linked discord account.", ChatColor.RED);
            return true;
        }

        if (args.length == 0)
        {
            return false;
        }

        if (args[0].equals("create"))
        {
            msg("Creating your AMP account...", ChatColor.GREEN);
            StaffMember staffMember = getAdmin(playerSender);

            if (staffMember.getAmpUsername() != null)
            {
                msg("You already have an AMP account.", ChatColor.RED);
                return true;
            }

            String username = sender.getName();
            String password = FUtil.randomString(30);

            staffMember.setAmpUsername(username);
            plugin.sl.save(staffMember);
            plugin.sl.updateTables();

            plugin.amp.createAccount(username, password);
            plugin.dc.sendAMPInfo(playerData, username, password);
            msg("Successfully created your AMP account. Check your DMs from " + plugin.dc.formatBotTag() + " on discord to get your credentials.", ChatColor.GREEN);
            return true;
        }
        else if (args[0].equals("resetpassword"))
        {
            StaffMember staffMember = getAdmin(playerSender);

            if (staffMember.getAmpUsername() == null)
            {
                msg("You do not have an AMP account.", ChatColor.RED);
                return true;
            }

            msg("Resetting your password...", ChatColor.GREEN);

            String username = staffMember.getAmpUsername();
            String password = FUtil.randomString(30);
            plugin.amp.setPassword(username,password);
            plugin.dc.sendAMPInfo(playerData, username, password);

            msg("Successfully reset your AMP account password. Check your DMs from " + plugin.dc.formatBotTag() + " on discord to get your credentials.", ChatColor.GREEN);
            return true;
        }

        return false;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1 && plugin.sl.isAdmin(sender))
        {
            return Arrays.asList("create", "resetpassword");
        }

        return Collections.emptyList();
    }

}
