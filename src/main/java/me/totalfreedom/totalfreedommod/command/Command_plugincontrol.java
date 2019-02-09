package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

@CommandPermissions(level = Rank.TELNET_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Manage plugins", usage = "/<command> <<enable | disable | reload> <pluginname>> | list>", aliases = "plc")
public class Command_plugincontrol extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0 || args.length > 2)
        {
            return false;
        }

        final PluginManager pm = server.getPluginManager();

        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("list"))
            {
                for (Plugin serverPlugin : pm.getPlugins())
                {
                    final String version = serverPlugin.getDescription().getVersion();
                    msg(ChatColor.GRAY + "- " + (serverPlugin.isEnabled() ? ChatColor.GREEN : ChatColor.RED) + serverPlugin.getName()
                            + ChatColor.GOLD + (version != null && !version.isEmpty() ? " v" + version : "") + " by "
                            + StringUtils.join(serverPlugin.getDescription().getAuthors(), ", "));
                }

                return true;
            }

            return false;
        }

        if ("enable".equals(args[0]))
        {
            final Plugin target = getPlugin(args[1]);
            if (target == null)
            {
                msg("Plugin not found!");
                return true;
            }

            if (target.isEnabled())
            {
                msg("Plugin is already enabled.");
                return true;
            }

            pm.enablePlugin(target);

            if (!pm.isPluginEnabled(target))
            {
                msg("Error enabling plugin " + target.getName());
                return true;
            }

            msg(target.getName() + " is now enabled.");
            return true;
        }

        if ("disable".equals(args[0]))
        {
            final Plugin target = getPlugin(args[1]);
            if (target == null)
            {
                msg("Plugin not found!");
                return true;
            }

            if (!target.isEnabled())
            {
                msg("Plugin is already disabled.");
                return true;
            }

            if (target.getName().equals(plugin.getName()))
            {
                msg("You cannot disable " + plugin.getName());
                return true;
            }

            pm.disablePlugin(target);

            if (pm.isPluginEnabled(target))
            {
                msg("Error disabling plugin " + target.getName());
                return true;
            }

            msg(target.getName() + " is now disabled.");
            return true;
        }

        if ("reload".equals(args[0]))
        {
            final Plugin target = getPlugin(args[1]);
            if (target == null)
            {
                msg("Plugin not found!");
                return true;
            }

            if (target.getName().equals(plugin.getName()))
            {
                msg("Use /tfm reload to reload instead.");
                return true;
            }

            pm.disablePlugin(target);
            pm.enablePlugin(target);
            msg(target.getName() + " reloaded.");
            return true;
        }

        return false;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (!plugin.al.isAdmin(sender))
        {
            return Collections.emptyList();
        }
        if (args.length == 1)
        {
            return Arrays.asList("enable", "disable", "reload", "list");
        }
        else if (args.length == 2)
        {
            if (!args[0].equals("list"))
            {
                return getAllPluginNames();
            }
        }

        return Collections.emptyList();
    }

    public List<String> getAllPluginNames()
    {
        List<String> names = new ArrayList<>();
        for (Plugin plugin : server.getPluginManager().getPlugins())
        {
            names.add(plugin.getName());
        }
        names.remove(plugin.getName());
        return names;
    }

    public Plugin getPlugin(String name)
    {
        for (Plugin serverPlugin : server.getPluginManager().getPlugins())
        {
            if (serverPlugin.getName().equalsIgnoreCase(name))
            {
                return serverPlugin;
            }
        }

        if (name.length() >= 3)
        {
            for (Plugin serverPlugin : server.getPluginManager().getPlugins())
            {
                if (serverPlugin.getName().toLowerCase().contains(name.toLowerCase()))
                {
                    return serverPlugin;
                }
            }
        }
        return null;
    }
}
