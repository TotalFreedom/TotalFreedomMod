package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.util.Ips;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TELNET_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Warns a player.", usage = "/<command> <player>", aliases = "vna")
public class Command_verifynoadmin extends FreedomCommand
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

        if (plugin.al.isAdminImpostor(player))
        {
            if (!plugin.al.verifiedNoAdmins.contains(player.getName()))
            {
                plugin.al.verifiedNoAdmins.add(player.getName());
            }
            String ip = Ips.getIp(player);
            if (!plugin.al.verifiedNoAdminIps.containsKey(player.getName()))
            {
                List<String> ips = new ArrayList<>();
                ips.add(ip);
                plugin.al.verifiedNoAdminIps.put(player.getName(), ips);
            }
            else
            {
                List<String> ips = plugin.al.verifiedNoAdminIps.get(player.getName());
                if (!ips.contains(ip))
                {
                    ips.add(ip);
                    plugin.al.verifiedNoAdmins.remove(player.getName());
                    plugin.al.verifiedNoAdminIps.put(player.getName(), ips);

                }
            }
            plugin.rm.updateDisplay(player);
            FUtil.adminAction(sender.getName(), "Verified " + player.getName() + ", but didn't give them admin permissions", true);
            player.setOp(true);
            player.sendMessage(YOU_ARE_OP);
            final FPlayer fPlayer = plugin.pl.getPlayer(player);
            if (fPlayer.getFreezeData().isFrozen())
            {
                fPlayer.getFreezeData().setFrozen(false);
                player.sendMessage(ChatColor.GRAY + "You have been unfrozen.");
            }
            msg("Verified " + player.getName() + " but didn't give them admin permissions", ChatColor.GREEN);
        }
        else
        {
            msg(player.getName() + " is not an admin imposter.", ChatColor.RED);
        }

        return true;
    }
}
