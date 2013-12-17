package me.StevenLawson.TotalFreedomMod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.StevenLawson.TotalFreedomMod.Commands.TFM_CommandLoader;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TFM_CommandBlocker
{
    private Map<String, CommandBlockerEntry> blockedCommands = new HashMap<String, CommandBlockerEntry>();

    private TFM_CommandBlocker()
    {
        parseBlockingRules();
    }

    public final void parseBlockingRules()
    {
        blockedCommands.clear();

        CommandMap commandMap = TFM_CommandLoader.getInstance().getCommandMap();
        if (commandMap == null)
        {
            TFM_Log.severe("Error loading commandMap.");
            return;
        }

        List<String> _blockedCommands = (List<String>) TFM_ConfigEntry.BLOCKED_COMMANDS.getList();
        for (String rawEntry : _blockedCommands)
        {
            String[] parts = rawEntry.split(":");
            if (parts.length < 3 || parts.length > 4)
            {
                continue;
            }

            CommandBlockerRank rank = CommandBlockerRank.fromToken(parts[0]);
            if (rank == null)
            {
                continue;
            }

            CommandBlockerAction action = CommandBlockerAction.fromToken(parts[1]);
            if (action == null)
            {
                continue;
            }

            String command = parts[2];
            if (command == null || command.isEmpty())
            {
                continue;
            }
            Matcher matcher = Pattern.compile("^/?(\\S+)").matcher(command);
            if (matcher.find())
            {
                command = matcher.group(1);
                if (command == null)
                {
                    continue;
                }
                else
                {
                    command = command.toLowerCase().trim();
                }
            }
            else
            {
                continue;
            }

            String message = null;
            if (parts.length == 4)
            {
                message = parts[3];
            }

            CommandBlockerEntry blockedCommandEntry = new CommandBlockerEntry(rank, action, command, message);

            Command bukkitCommand = commandMap.getCommand(command);
            if (bukkitCommand == null)
            {
                //TFM_Log.info("Blocking unknown command: " + blockedCommandEntry.getCommand());
                blockedCommands.put(blockedCommandEntry.getCommand(), blockedCommandEntry);
            }
            else
            {
                blockedCommandEntry.setCommand(bukkitCommand.getName().toLowerCase());

                //TFM_Log.info("Blocking command: " + blockedCommandEntry.getCommand());
                blockedCommands.put(blockedCommandEntry.getCommand(), blockedCommandEntry);

                for (String alias : bukkitCommand.getAliases())
                {
                    //TFM_Log.info("Blocking alias: " + alias.toLowerCase() + " of " + blockedCommandEntry.getCommand());
                    blockedCommands.put(alias.toLowerCase(), blockedCommandEntry);
                }
            }
        }
    }

    public boolean isCommandBlocked(String command, CommandSender sender)
    {
        return isCommandBlocked(command, sender, true);
    }

    public boolean isCommandBlocked(String command, CommandSender sender, boolean doAction)
    {
        if (command == null || command.isEmpty())
        {
            return false;
        }

        Matcher matcher = Pattern.compile("^/?(\\S+)").matcher(command);
        if (matcher.find())
        {
            command = matcher.group(1);
            if (command == null)
            {
                return false;
            }
            else
            {
                command = command.toLowerCase().trim();
            }
        }
        else
        {
            return false;
        }

        final CommandBlockerEntry blockedCommandEntry = blockedCommands.get(command);

        if (blockedCommandEntry != null)
        {
            if (!blockedCommandEntry.getRank().hasPermission(sender))
            {
                if (doAction)
                {
                    blockedCommandEntry.doActions(sender);
                }

                return true;
            }
        }

        return false;
    }

    private static enum CommandBlockerRank
    {
        ANYONE("a", 0),
        OP("o", 1),
        SUPER("s", 2),
        TELNET("t", 3),
        SENIOR("c", 4),
        NOBODY("n", 5);
        private final String token;
        private final int level;

        private CommandBlockerRank(String token, int level)
        {
            this.token = token;
            this.level = level;
        }

        public String getToken()
        {
            return this.token;
        }

        public boolean hasPermission(CommandSender sender)
        {
            return getSenderRank(sender).level >= this.level;
        }

        public static CommandBlockerRank getSenderRank(CommandSender sender)
        {
            if (!TFM_SuperadminList.isUserSuperadmin(sender))
            {
                if (sender.isOp())
                {
                    return OP;
                }

                return ANYONE;
            }
            else
            {
                if (TFM_SuperadminList.isSeniorAdmin(sender))
                {
                    return SENIOR;
                }

                if (!(sender instanceof Player))
                {
                    return TELNET;
                }

                return SUPER;
            }
        }

        public static CommandBlockerRank fromToken(String token)
        {
            for (CommandBlockerRank rank : CommandBlockerRank.values())
            {
                if (rank.getToken().equalsIgnoreCase(token))
                {
                    return rank;
                }
            }
            return ANYONE;
        }
    }

    private enum CommandBlockerAction
    {
        BLOCK("b"),
        BLOCK_AND_EJECT("a"),
        BLOCK_UNKNOWN("u");
        private final String token;

        private CommandBlockerAction(String token)
        {
            this.token = token;
        }

        public String getToken()
        {
            return this.token;
        }

        public static CommandBlockerAction fromToken(String token)
        {
            for (CommandBlockerAction action : CommandBlockerAction.values())
            {
                if (action.getToken().equalsIgnoreCase(token))
                {
                    return action;
                }
            }
            return null;
        }
    }

    private static class CommandBlockerEntry
    {
        private final CommandBlockerRank rank;
        private final CommandBlockerAction action;
        private String command;
        private final String message;

        public CommandBlockerEntry(CommandBlockerRank rank, CommandBlockerAction action, String command, String message)
        {
            this.rank = rank;
            this.action = action;
            this.command = command;
            this.message = message;
        }

        public CommandBlockerAction getAction()
        {
            return this.action;
        }

        public String getCommand()
        {
            return this.command;
        }

        public String getMessage()
        {
            return this.message;
        }

        public CommandBlockerRank getRank()
        {
            return this.rank;
        }

        public void setCommand(String command)
        {
            this.command = command;
        }

        private void doActions(CommandSender sender)
        {
            if (this.action == CommandBlockerAction.BLOCK_AND_EJECT && sender instanceof Player)
            {
                TFM_Util.autoEject((Player) sender, "You used a prohibited command: " + this.command);
                TFM_Util.bcastMsg(sender.getName() + " was automatically kicked for using harmful commands.", ChatColor.RED);
            }
            else
            {
                String response;

                if (this.action == CommandBlockerAction.BLOCK_UNKNOWN)
                {
                    response = "Unknown command. Type \"help\" for help.";
                }
                else if (this.message == null || "_".equals(this.message))
                {
                    response = ChatColor.GRAY + "That command is blocked.";
                }
                else
                {
                    response = ChatColor.GRAY + TFM_Util.colorize(this.message);
                }

                sender.sendMessage(response);
            }
        }
    }

    public static TFM_CommandBlocker getInstance()
    {
        return TFM_CommandBlockerHolder.INSTANCE;
    }

    private static class TFM_CommandBlockerHolder
    {
        private static final TFM_CommandBlocker INSTANCE = new TFM_CommandBlocker();
    }
}
