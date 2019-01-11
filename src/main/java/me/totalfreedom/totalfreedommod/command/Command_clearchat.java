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
@CommandParameters(description = "Clear chat", usage = "/<command> [optout]", aliases = "cc")
public class Command_clearchat extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (plugin.al.isAdmin(playerSender))
        {
            server.getOnlinePlayers().stream().filter(player -> !plugin.al.isAdmin(player) || !plugin.pv.getVerificationPlayer(player).isClearChatOptOut() || !plugin.mbl.getMasterBuilder(player).isClearChatOptOut()).forEach(player -> IntStream.range(0, 100).mapToObj(i -> "").forEach(player::sendMessage));
            FUtil.adminAction(sender.getName(), "Cleared chat", true);
            return true;
        }
        if (args.length != 1)
        {
            return false;
        }
        if (plugin.mbl.isMasterBuilder(playerSender))
        {
            MasterBuilder mb = plugin.mbl.getMasterBuilder(playerSender);
            mb.setClearChatOptOut(!mb.isClearChatOptOut());
            msg(mb.isClearChatOptOut() ? "Opted-out" : "Opted-in" + " to clear chat.");
            plugin.mbl.save();
            plugin.mbl.updateTables();
            return true;
        }
        if (plugin.pv.getVerificationPlayer(playerSender).getEnabled())
        {
            VPlayer vp = plugin.pv.getVerificationPlayer(playerSender);
            vp.setClearChatOptOut(!vp.isClearChatOptOut());
            msg(vp.isClearChatOptOut() ? "Opted-out" : "Opted-in" + " to clear chat.");
            plugin.pv.saveVerificationData(vp);
            return true;
        }
        msg("Only Master Builders, and players with verification enabled can opt out of clear chat.", ChatColor.RED);
        return true;
    }
}