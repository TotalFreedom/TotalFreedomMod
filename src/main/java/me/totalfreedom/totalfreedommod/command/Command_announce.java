package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Allows Super Administrators to announce messages.", usage = "/<command> <message...>", aliases = "ae")
public class Command_announce extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsPlayer)
    {
        if (args.length == 0)
        {
            return false;
        }
        String message = StringUtils.join(args, " ");
        
        FUtil.bcastMsg(String.format("[§5[§eTotalFreedom§5] §b:%s] %s", sender.getName(), message));
        
        
        return true;
    }
}

