package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Toggle item pickup.", usage = "/<command>")
public class Command_togglepickup extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        boolean enabled = !playerSender.getCanPickupItems();
        playerSender.setCanPickupItems(enabled);
        msg((enabled ? "En" : "Dis") + "abled item pickup.", (enabled ? ChatColor.GREEN : ChatColor.RED));
        return true;
    }
}
