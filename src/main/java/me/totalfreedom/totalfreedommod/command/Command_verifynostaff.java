package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.MOD, source = SourceType.BOTH)
@CommandParameters(description = "Verify a staff member without giving them staff permissions.", usage = "/<command> <player>", aliases = "vns")
public class Command_verifynostaff extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        Player player = getPlayer(args[0]);

        if (player == null)
        {
            msg(PLAYER_NOT_FOUND);
            return true;
        }

        if (plugin.sl.isStaffImpostor(player))
        {
            if (!plugin.sl.verifiedNoStaff.contains(player.getName()))
            {
                plugin.sl.verifiedNoStaff.add(player.getName());
            }
            String ip = FUtil.getIp(player);
            if (!plugin.sl.verifiedNoStaffIps.containsKey(player.getName()))
            {
                List<String> ips = new ArrayList<>();
                ips.add(ip);
                plugin.sl.verifiedNoStaffIps.put(player.getName(), ips);
            }
            else
            {
                List<String> ips = plugin.sl.verifiedNoStaffIps.get(player.getName());
                if (!ips.contains(ip))
                {
                    ips.add(ip);
                    plugin.sl.verifiedNoStaff.remove(player.getName());
                    plugin.sl.verifiedNoStaffIps.put(player.getName(), ips);

                }
            }
            plugin.rm.updateDisplay(player);
            FUtil.staffAction(sender.getName(), "Verified " + player.getName() + ", without staff permissions.", true);
            player.setOp(true);
            player.sendMessage(YOU_ARE_OP);
            final FPlayer fPlayer = plugin.pl.getPlayer(player);
            if (fPlayer.getFreezeData().isFrozen())
            {
                fPlayer.getFreezeData().setFrozen(false);
                player.sendMessage(ChatColor.GRAY + "You have been unfrozen.");
            }
            msg("Verified " + player.getName() + " but didn't give them staff permissions", ChatColor.GREEN);
        }
        else
        {
            msg(player.getName() + " is not a staff imposter.", ChatColor.RED);
        }

        return true;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1)
        {
            List<String> adminImposters = new ArrayList<>();
            for (Player player : server.getOnlinePlayers())
            {
                if (plugin.sl.isStaffImpostor(player))
                {
                    adminImposters.add(player.getName());
                }
            }
            return adminImposters;
        }

        return Collections.emptyList();
    }
}