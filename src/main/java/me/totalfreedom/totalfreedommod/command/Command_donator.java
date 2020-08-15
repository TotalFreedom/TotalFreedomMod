package me.totalfreedom.totalfreedommod.command;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.ADMIN, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Adds or removes donators", usage = "/<command> <mode> <name> <ip> <package> [forum_user]")
public class Command_donator extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!FUtil.isFromHostConsole(sender.getName()) && !ConfigEntry.SERVER_OWNERS.getStringList().contains(sender.getName()))
        {
            return noPerms();
        }

        Boolean mode = args[0].equals("add");
        String name = args[1];
        String ip = args[2];
        String pkg = args[3];
        String forum_id = null;

        if (args.length > 4)
        {
            forum_id = args[4];
        }

        PlayerData data = plugin.pl.getData(name);

        if (data == null)
        {
            data = plugin.pl.getDataByIp(ip);
        }

        if (data != null)
        {
            data.setDonator(mode);
            plugin.pl.save(data);
        }

        if (mode)
        {
            FUtil.bcastMsg(ChatColor.AQUA + name + ChatColor.GREEN + " has donated to the server!");
        }
        Player player = getPlayer(name);

        if (player != null)
        {
            plugin.rm.updateDisplay(player);
        }

        if (forum_id != null && !forum_id.equals("0"))
        {
            String baseurl = ConfigEntry.DONATION_PROBOARDS_URL.getString();
            String group_id = ConfigEntry.DONATION_GROUP_ID.getString();
            String session_id = ConfigEntry.DONATION_SESSION_ID.getString();
            String csrf_token = ConfigEntry.DONATION_CSRF_TOKEN.getString();
            if (baseurl == null || group_id == null || session_id == null || csrf_token == null)
            {
                return true;
            }
            String url = baseurl + "/user/group_members/" + (mode ? "adding" : "remove");
            List<String> headers = Arrays.asList("Cookie:session_id=" + session_id, "X-Requested-With:XMLHttpRequest");
            String payload = "group_id=" + group_id + "&user_ids[]=" + forum_id + "&csrf_token=" + csrf_token;

            try
            {
                FUtil.sendRequest(url, "POST", headers, payload);
            }
            catch (IOException e)
            {
                FLog.severe(e.getMessage());
                e.printStackTrace();
            }
        }

        return true;
    }
}