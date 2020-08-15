package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.MOD, source = SourceType.BOTH)
@CommandParameters(description = "Toggle online players' ability to chat.", usage = "/<command>", aliases = "tc")
public class Command_togglechat extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        ConfigEntry.TOGGLE_CHAT.setBoolean(!ConfigEntry.TOGGLE_CHAT.getBoolean());
        FUtil.staffAction(sender.getName(), "Chat " + (ConfigEntry.TOGGLE_CHAT.getBoolean() ? "enabled" : "disabled") + ".", true);
        return true;
    }
}
