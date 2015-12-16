package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Remove various server entities that may cause lag, such as dropped items, minecarts, and boats.", usage = "/<command> <carts>")
public class Command_rd extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        FUtil.adminAction(sender.getName(), "Removing all server entities.", true);
        playerMsg((FUtil.TFM_EntityWiper.wipeEntities(true, true)) + " entities removed.");

        return true;
    }
}
