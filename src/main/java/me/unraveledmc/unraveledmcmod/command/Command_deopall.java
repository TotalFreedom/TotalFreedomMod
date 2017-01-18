package me.unraveledmc.unraveledmcmod.command;

import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH, blockHostConsole = true)
@CommandParameters(description = "Deop everyone on the server.", usage = "/<command>")
public class Command_deopall extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        FUtil.adminAction(sender.getName(), "De-opping all players on the server", true);

        for (Player player : server.getOnlinePlayers())
        {
            player.setOp(false);
            plugin.da.setAdminDeopped(player.getName(), true);
            player.sendMessage(FreedomCommand.YOU_ARE_NOT_OP);
        }

        return true;
    }
}
