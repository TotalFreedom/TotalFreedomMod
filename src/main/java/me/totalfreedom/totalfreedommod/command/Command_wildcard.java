package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Run any command on all users, username placeholder = ?.", usage = "/<command> [fluff] ? [fluff] ?")
public class Command_wildcard extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            return false;
        }

        if (args[0].equals("wildcard"))
        {
            msg("What the hell are you trying to do, you stupid idiot...", ChatColor.RED);
            return true;
        }
        if (args[0].equals("gtfo"))
        {
            msg("Nice try", ChatColor.RED);
            return true;
        }
        if (args[0].equals("doom"))
        {
            msg("Look, we all hate people, but this is not the way to deal with it, doom is evil enough!", ChatColor.RED);
            return true;
        }
        if (args[0].equals("saconfig"))
        {
            msg("WOA, WTF are you trying to do???", ChatColor.RED);
            return true;
        }
        if (args[0].equals("stop"))
        {
            msg("No, hell no, that is rouge activity right there, this has been logged!", ChatColor.RED);
            if (!senderIsConsole)
            {
                Admin admin = plugin.al.getAdmin(playerSender);
                admin.setActive(false);
                plugin.al.save();
                plugin.al.updateTables();
                playerSender.setOp(false);
                FPlayer playerData = plugin.pl.getPlayer(playerSender);
                Location targetPos = playerSender.getLocation().clone().add(0, 1, 0);
                playerData.getCageData().cage(targetPos, Material.GLASS, Material.AIR);
                playerSender.setGameMode(GameMode.SURVIVAL);
                playerSender.closeInventory();
            }
            FUtil.adminAction(sender.getName(), "Has just attempted to execute the command /wildcard stop", true);
            FUtil.bcastMsg("This is rouge activity, " + (senderIsConsole ? sender.getName() + " is console! Please standby and alert an executive admin or owner!" : playerSender.getName() + " has been removed from the admin list, deopped, and caged!"), ChatColor.RED);
            return true;
        }

        String baseCommand = StringUtils.join(args, " ");

        if (plugin.cb.isCommandBlocked(baseCommand, sender))
        {
            // CommandBlocker handles messages and broadcasts
            return true;
        }

        for (Player player : server.getOnlinePlayers())
        {
            String out_command = baseCommand.replaceAll("\\x3f", player.getName());
            msg("Running Command: " + out_command);
            server.dispatchCommand(sender, out_command);
        }

        return true;
    }
}
