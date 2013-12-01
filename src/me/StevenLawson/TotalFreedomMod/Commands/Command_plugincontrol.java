package me.StevenLawson.TotalFreedomMod.Commands;

import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.BOTH)
@CommandParameters(description = "Enable / disable plugins.", usage = "/<command> < <enable | disable> <pluginname> | list >", aliases = "plc")
public class Command_plugincontrol extends TFM_Command
{
    private enum CommandMode
    {
        ENABLE, DISABLE, LIST, RELOAD
    }

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        CommandMode commandMode = null;

        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("list"))
            {
                commandMode = CommandMode.LIST;
            }
            else if (args[0].equalsIgnoreCase("reload"))
            {
                commandMode = CommandMode.RELOAD;
            }
        }
        else if (args.length >= 2)
        {
            if (args[0].equalsIgnoreCase("enable"))
            {
                commandMode = CommandMode.ENABLE;
            }
            else if (args[0].equalsIgnoreCase("disable"))
            {
                commandMode = CommandMode.DISABLE;
            }
        }

        if (commandMode == null)
        {
            return false;
        }

        PluginManager pluginManager = plugin.getServer().getPluginManager();

        if (commandMode == CommandMode.LIST)
        {
            playerMsg("Plugins: " + StringUtils.join(pluginManager.getPlugins(), ", "));
        }
        else if (commandMode == CommandMode.RELOAD)
        {
            playerMsg("Disabling all plugins.");
            for (Plugin targetPlugin : pluginManager.getPlugins())
            {
                if (!targetPlugin.getName().toLowerCase().startsWith("totalfreedommod"))
                {
                    pluginManager.disablePlugin(targetPlugin);
                }
            }

            playerMsg("Enabling all plugins.");
            for (Plugin targetPlugin : pluginManager.getPlugins())
            {
                if (!targetPlugin.getName().toLowerCase().startsWith("totalfreedommod"))
                {
                    pluginManager.enablePlugin(targetPlugin);
                }
            }
        }
        else
        {
            final String searchPluginName = args[1].toLowerCase().trim();

            Plugin targetPlugin = null;

            for (Plugin serverPlugin : pluginManager.getPlugins())
            {
                if (searchPluginName.equalsIgnoreCase(serverPlugin.getName().toLowerCase().trim()))
                {
                    targetPlugin = serverPlugin;
                    break;
                }
            }

            if (targetPlugin == null)
            {
                playerMsg("Plugin \"" + searchPluginName + "\" is not installed.");
                return true;
            }
            else
            {
                if (commandMode == CommandMode.ENABLE)
                {
                    pluginManager.enablePlugin(targetPlugin);
                    if (targetPlugin.isEnabled())
                    {
                        playerMsg("Plugin \"" + targetPlugin.getName() + "\" enabled.");
                    }
                    else
                    {
                        playerMsg("Error enabling plugin \"" + targetPlugin.getName() + "\".");
                    }
                }
                else
                {
                    pluginManager.disablePlugin(targetPlugin);
                    if (!targetPlugin.isEnabled())
                    {
                        playerMsg("Plugin \"" + targetPlugin.getName() + "\" disabled.");
                    }
                    else
                    {
                        playerMsg("Error disabling plugin \"" + targetPlugin.getName() + "\".");
                    }
                }
            }
        }

        return true;
    }
}
