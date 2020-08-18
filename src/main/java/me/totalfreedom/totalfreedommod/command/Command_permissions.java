package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

@CommandPermissions(level = Rank.NON_OP, source = SourceType.BOTH)
@CommandParameters(description = "Check your permissions", usage = "/<command> [prefix | reload]")
public class Command_permissions extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length > 0 && args[0].equals("reload") && plugin.sl.isStaff(sender))
        {
            plugin.permissions.load();
            plugin.pem.loadPermissionNodes();
            plugin.pem.updatePlayers();
            msg("Reloaded permissions");
        }
        else
        {
            String prefix = "";
            if (args.length > 0)
            {
                prefix = args[0];
            }
            checkPlayer();
            List<String> permissions = new ArrayList<>();
            for (PermissionAttachmentInfo attachmentInfo : playerSender.getEffectivePermissions())
            {
                if (attachmentInfo.getValue())
                {
                    String permission = attachmentInfo.getPermission();
                    if (!prefix.isEmpty() && !permission.startsWith(prefix))
                    {
                        continue;
                    }
                    permissions.add(permission);
                }
            }
            msg(String.join(", ", permissions));
        }

        return true;
    }
}