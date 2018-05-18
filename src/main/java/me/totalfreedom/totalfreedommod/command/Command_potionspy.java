package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Toggles potion spy.", usage = "/<command>", aliases = "potspy")
public class Command_potionspy extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Admin admin = plugin.al.getAdmin(playerSender);
        admin.setPotionSpy(!admin.getPotionSpy());
        plugin.al.save();
        plugin.al.updateTables();
        msg("PotionSpy is now " + (admin.getPotionSpy() ? "enabled." : "disabled."));
        return true;
    }
}
