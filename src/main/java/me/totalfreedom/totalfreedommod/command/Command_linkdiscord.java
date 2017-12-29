package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.admin.Admin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import java.util.Random;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Link your discord account to your minecraft account", usage = "/<command>")
public class Command_linkdiscord extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!plugin.dc.enabled)
        {
            msg("The discord verification system is currently disabled.", ChatColor.RED);
            return true;
        }
        
        Admin admin = plugin.al.getAdmin(playerSender);
        if (admin.getDiscordID() != null)
        {
            msg("Your minecraft account is already linked to a discord account.", ChatColor.RED);
            return true;
        }
        
        if (plugin.dc.LINK_CODES.containsValue(admin))
        {
            msg("Your linking code is " + ChatColor.GREEN + plugin.dc.getCodeForAdmin(admin), ChatColor.AQUA);
        }
        else
        {
            String code = "";
            Random random = new Random();
            for (int i = 0; i < 5; i++)   
            {
                code += random.nextInt(10);
            }
            plugin.dc.LINK_CODES.put(code, admin);
            msg("Your linking code is " + ChatColor.GREEN + code, ChatColor.AQUA);
        }
        return true;
    }
}
