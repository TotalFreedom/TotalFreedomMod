package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Util;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Shortcut to enable/disable DisguiseCraft.", usage = "/<command>")
public class Command_dtoggle extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        Plugin disguiseCraft = server.getPluginManager().getPlugin("DisguiseCraft");
        if (disguiseCraft != null)
        {
            PluginManager pluginManager = plugin.getServer().getPluginManager();

            boolean enabled = disguiseCraft.isEnabled();
            if (enabled)
            {
                pluginManager.disablePlugin(disguiseCraft);
            }
            else
            {
                pluginManager.enablePlugin(disguiseCraft);
            }

            TFM_Util.adminAction(sender.getName(), (!enabled ? "Enabled" : "Disabled") + " DisguiseCraft.", true);
        }
        else
        {
            sender.sendMessage("DisguiseCraft is not installed on this server.");
        }

        return true;
    }
}
