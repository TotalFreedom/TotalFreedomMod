package me.totalfreedom.totalfreedommod.command;

import java.util.stream.IntStream;
import me.totalfreedom.totalfreedommod.masterbuilder.MasterBuilder;
import me.totalfreedom.totalfreedommod.playerverification.VPlayer;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Clear chat", usage = "/<command>", aliases = "cc")
public class Command_clearchat extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        if (plugin.al.isAdmin(playerSender))
        {
            for (Player player : server.getOnlinePlayers())
            {
                boolean optedOut = false;

                if (plugin.al.isAdmin(player))
                {
                    optedOut = true;
                }
                else if (plugin.mbl.isMasterBuilder(player) && plugin.mbl.getMasterBuilder(player).isClearChatOptOut())
                {
                    optedOut = true;
                }
                else if (plugin.pv.getVerificationPlayer(player).getEnabled() && plugin.pv.getVerificationPlayer(player).isClearChatOptOut())
                {
                    optedOut = true;
                }

                if (!optedOut)
                {
                    IntStream.range(0, 100).mapToObj(i -> "").forEach(player::sendMessage);
                }
            }
            FUtil.adminAction(sender.getName(), "Cleared chat", true);
        }
        else if (plugin.mbl.isMasterBuilder(playerSender))
        {
            MasterBuilder mb = plugin.mbl.getMasterBuilder(playerSender);
            mb.setClearChatOptOut(!mb.isClearChatOptOut());
            msg((mb.isClearChatOptOut() ? "Opted-out of" : "Opted-in to") + " clear chat.");
            plugin.mbl.save();
            plugin.mbl.updateTables();
        }
        else if (plugin.pv.getVerificationPlayer(playerSender).getEnabled())
        {
            VPlayer vp = plugin.pv.getVerificationPlayer(playerSender);
            vp.setClearChatOptOut(!vp.isClearChatOptOut());
            msg((vp.isClearChatOptOut() ? "Opted-out of" : "Opted-in to") + " clear chat.");
            plugin.pv.saveVerificationData(vp);
            return true;
        }
        else
        {
            msg("Only Master Builders, admins, and players with verification enabled can opt-out of clear chat.", ChatColor.RED);
        }

        return true;
    }
}