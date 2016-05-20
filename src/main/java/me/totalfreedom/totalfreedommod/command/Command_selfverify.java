package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SENIOR_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Enable or disable self verification", usage = "/<command> [on | off]")
public class Command_selfverify extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 1)
        {
            if (args[0].equals("on"))
            {
                ConfigEntry.VERIFY_ENABLED.setBoolean(true);
                FUtil.adminAction(sender.getName(), "Enabling the self verification system", true);
                return true;
            }
            else if (args[0].equals("off"))
            {
                ConfigEntry.VERIFY_ENABLED.setBoolean(false);
                FUtil.adminAction(sender.getName(), "Disabling the self verification system", true);
                return true;
            }
        }
        return false;
    }
}
