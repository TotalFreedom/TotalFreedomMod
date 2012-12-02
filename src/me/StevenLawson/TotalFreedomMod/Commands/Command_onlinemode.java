package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PropertyManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = ADMIN_LEVEL.SENIOR, source = SOURCE_TYPE_ALLOWED.ONLY_CONSOLE, block_host_console = true, ignore_permissions = false)
public class Command_onlinemode extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            sender.sendMessage("Server is currently running with 'online-mode=" + (server.getOnlineMode() ? "true" : "false") + "'.");
            sender.sendMessage("Use \"/onlinemode on\" and \"/onlinemode off\" to change online mode.");
        }
        else
        {
            boolean online_mode;

            if (args[0].equalsIgnoreCase("on"))
            {
                online_mode = true;
            }
            else if (args[0].equalsIgnoreCase("off"))
            {
                online_mode = false;
            }
            else
            {
                return false;
            }

            try
            {
                PropertyManager prop_man = MinecraftServer.getServer().getPropertyManager();
                prop_man.a("online-mode", online_mode);
                prop_man.savePropertiesFile();

                if (online_mode)
                {
                    for (Player p : server.getOnlinePlayers())
                    {
                        p.kickPlayer("Server is activating \"online-mode=true\". Please reconnect.");
                    }
                }

                TFM_Util.adminAction(sender.getName(), "Turning player validation " + (online_mode ? "on" : "off") + ".", true);

                server.reload();
            }
            catch (Exception ex)
            {
                TFM_Log.severe(ex);
            }
        }

        return true;
    }
}
