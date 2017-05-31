package me.totalfreedom.totalfreedommod.blocking.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.command.CommandReflection;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CommandBlocker extends FreedomService
{

    private final Pattern flagPattern = Pattern.compile("(:([0-9]){5,})");
    //
    private final Map<String, CommandBlockerEntry> entryList = Maps.newHashMap();
    private final List<String> unknownCommands = Lists.newArrayList();

    public CommandBlocker(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
        load();
    }

    @Override
    protected void onStop()
    {
        entryList.clear();
    }

    public void load()
    {
        entryList.clear();
        unknownCommands.clear();

        final CommandMap commandMap = CommandReflection.getCommandMap();
        if (commandMap == null)
        {
            FLog.severe("Error loading commandMap.");
            return;
        }

        @SuppressWarnings("unchecked")
        List<String> blockedCommands = (List<String>) ConfigEntry.BLOCKED_COMMANDS.getList();
        for (String rawEntry : blockedCommands)
        {
            final String[] parts = rawEntry.split(":");
            if (parts.length < 3 || parts.length > 4)
            {
                FLog.warning("Invalid command blocker entry: " + rawEntry);
                continue;
            }

            final CommandBlockerRank rank = CommandBlockerRank.fromToken(parts[0]);
            final CommandBlockerAction action = CommandBlockerAction.fromToken(parts[1]);
            String commandName = parts[2].toLowerCase().substring(1);
            final String message = (parts.length > 3 ? parts[3] : null);

            if (rank == null || action == null || commandName == null || commandName.isEmpty())
            {
                FLog.warning("Invalid command blocker entry: " + rawEntry);
                continue;
            }

            final String[] commandParts = commandName.split(" ");
            String subCommand = null;
            if (commandParts.length > 1)
            {
                commandName = commandParts[0];
                subCommand = StringUtils.join(commandParts, " ", 1, commandParts.length).trim().toLowerCase();
            }

            final Command command = commandMap.getCommand(commandName);

            // Obtain command from alias
            if (command == null)
            {
                unknownCommands.add(commandName);
            }
            else
            {
                commandName = command.getName().toLowerCase();
            }

            if (entryList.containsKey(commandName))
            {
                FLog.warning("Not blocking: /" + commandName + " - Duplicate entry exists!");
                continue;
            }

            final CommandBlockerEntry blockedCommandEntry = new CommandBlockerEntry(rank, action, commandName, subCommand, message);
            entryList.put(blockedCommandEntry.getCommand(), blockedCommandEntry);

            if (command != null)
            {
                for (String alias : command.getAliases())
                {
                    entryList.put(alias.toLowerCase(), blockedCommandEntry);
                }
            }
        }

        FLog.info("Loaded " + blockedCommands.size() + " blocked commands (" + (blockedCommands.size() - unknownCommands.size()) + " known).");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        // Blocked commands
        if (isCommandBlocked(event.getMessage(), event.getPlayer(), true))
        {
            // CommandBlocker handles messages and broadcasts
            event.setCancelled(true);
        }
    }

    public boolean isCommandBlocked(String command, CommandSender sender)
    {
        return isCommandBlocked(command, sender, false);
    }

    public boolean isCommandBlocked(String command, CommandSender sender, boolean doAction)
    {
        if (command == null || command.isEmpty())
        {
            return false;
        }

        // Format
        command = command.toLowerCase().trim();
        command = command.startsWith("/") ? command.substring(1) : command;

        // Check for plugin specific commands
        final String[] commandParts = command.split(" ");
        if (commandParts[0].contains(":"))
        {
            if (doAction)
            {
                FUtil.playerMsg(sender, "Plugin specific commands are disabled.");
            }
            return true;
        }

        for (String part : commandParts)
        {
            Matcher matcher = flagPattern.matcher(part);
            if (!matcher.matches())
            {
                continue;
            }
            if (doAction)
            {
                FUtil.playerMsg(sender, "That command contains an illegal number: " + matcher.group(1));
            }
            return true;
        }

        // Obtain sub command, if it exists
        String subCommand = null;
        if (commandParts.length > 1)
        {
            subCommand = StringUtils.join(commandParts, " ", 1, commandParts.length).toLowerCase();
        }

        // Obtain entry
        final CommandBlockerEntry entry = entryList.get(commandParts[0]);
        if (entry == null)
        {
            return false;
        }

        // Validate sub command
        if (entry.getSubCommand() != null)
        {
            if (subCommand == null || !subCommand.startsWith(entry.getSubCommand()))
            {
                return false;
            }
        }

        if (entry.getRank().hasPermission(sender))
        {
            return false;
        }

        if (doAction)
        {
            entry.doActions(sender);
        }

        return true;
    }
}
