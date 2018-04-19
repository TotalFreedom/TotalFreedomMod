package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Toggle old admin formatting", usage = "/<command>", aliases = "oam")
public class Command_oldadminmode extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {

        Admin admin = plugin.al.getAdmin(playerSender);
        admin.setOldAdminMode(!admin.getOldAdminMode());
        plugin.al.save();
        plugin.al.updateTables();
        msg("Old admin mode has been " + (admin.getOldAdminMode() ? "enabled." : "disabled."));

        return true;
    }
}
