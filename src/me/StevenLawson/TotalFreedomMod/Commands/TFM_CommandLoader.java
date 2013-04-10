package me.StevenLawson.TotalFreedomMod.Commands;

import java.io.IOException;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

public class TFM_CommandLoader
{
    public static Pattern COMMAND_CLASS_PATTERN = Pattern.compile(TotalFreedomMod.COMMAND_PATH.replace('.', '/') + "/(" + TotalFreedomMod.COMMAND_PREFIX + "[^\\$]+)\\.class");
    private List<TFM_CommandInfo> commandList = null;

    private TFM_CommandLoader()
    {
    }

    public void scan()
    {
        CommandMap commandMap = TFM_Util.getField(Bukkit.getServer().getPluginManager(), "commandMap");
        if (commandMap == null)
        {
            TFM_Log.severe("Error loading command map.");
            return;
        }

        if (commandList == null)
        {
            commandList = getCommands();
        }

        for (TFM_CommandInfo commandInfo : commandList)
        {
            String description = commandInfo.getDescription();
            switch (commandInfo.getLevel())
            {
                case SENIOR:
                    description = "Senior " + (commandInfo.getSource() == SourceType.ONLY_CONSOLE ? "Console" : "") + " Command - " + description;
                    break;
                case SUPER:
                    description = "Superadmin Command - " + description;
                    break;
                case OP:
                    description = "OP Command - " + description;
                    break;
            }
            TFM_DynamicCommand dynamicCommand = new TFM_DynamicCommand(commandInfo.getCommandName(), description, commandInfo.getUsage(), commandInfo.getAliases());
            commandMap.register(TotalFreedomMod.plugin.getDescription().getName(), dynamicCommand);
        }
    }

    private static List<TFM_CommandInfo> getCommands()
    {
        List<TFM_CommandInfo> commandList = new ArrayList<TFM_CommandInfo>();

        try
        {
            CodeSource codeSource = TotalFreedomMod.class.getProtectionDomain().getCodeSource();
            if (codeSource != null)
            {
                ZipInputStream zip = new ZipInputStream(codeSource.getLocation().openStream());
                ZipEntry zipEntry;
                while ((zipEntry = zip.getNextEntry()) != null)
                {
                    String entryName = zipEntry.getName();
                    Matcher matcher = COMMAND_CLASS_PATTERN.matcher(entryName);
                    if (matcher.find())
                    {
                        try
                        {
                            Class<?> commandClass = Class.forName(TotalFreedomMod.COMMAND_PATH + "." + matcher.group(1));

                            CommandPermissions commandPermissions = (CommandPermissions) commandClass.getAnnotation(CommandPermissions.class);
                            CommandParameters commandParameters = (CommandParameters) commandClass.getAnnotation(CommandParameters.class);

                            if (commandPermissions != null && commandParameters != null)
                            {
                                TFM_CommandInfo commandInfo = new TFM_CommandInfo(
                                        commandClass,
                                        matcher.group(1).split("_")[1],
                                        commandPermissions.level(),
                                        commandPermissions.source(),
                                        commandPermissions.block_host_console(),
                                        commandParameters.description(),
                                        commandParameters.usage(),
                                        commandParameters.aliases());

                                commandList.add(commandInfo);
                            }
                        }
                        catch (ClassNotFoundException ex)
                        {
                            TFM_Log.severe(ex);
                        }
                    }
                }
            }
        }
        catch (IOException ex)
        {
            TFM_Log.severe(ex);
        }

        return commandList;
    }

    private static class TFM_CommandInfo
    {
        private final String commandName;
        private final Class<?> commandClass;
        private final AdminLevel level;
        private final SourceType source;
        private final boolean blockHostConsole;
        private final String description;
        private final String usage;
        private final List<String> aliases;

        public TFM_CommandInfo(Class<?> commandClass, String commandName, AdminLevel level, SourceType source, boolean blockHostConsole, String description, String usage, String aliases)
        {
            this.commandName = commandName;
            this.commandClass = commandClass;
            this.level = level;
            this.source = source;
            this.blockHostConsole = blockHostConsole;
            this.description = description;
            this.usage = usage;
            this.aliases = Arrays.asList(aliases.split(","));
        }

        public List<String> getAliases()
        {
            return aliases;
        }

        public Class<?> getCommandClass()
        {
            return commandClass;
        }

        public String getCommandName()
        {
            return commandName;
        }

        public String getDescription()
        {
            return description;
        }

        public AdminLevel getLevel()
        {
            return level;
        }

        public SourceType getSource()
        {
            return source;
        }

        public String getUsage()
        {
            return usage;
        }

        public boolean getBlockHostConsole()
        {
            return blockHostConsole;
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("commandName: ").append(commandName);
            sb.append("\ncommandClass: ").append(commandClass.getName());
            sb.append("\nlevel: ").append(level);
            sb.append("\nsource: ").append(source);
            sb.append("\nblock_host_console: ").append(blockHostConsole);
            sb.append("\ndescription: ").append(description);
            sb.append("\nusage: ").append(usage);
            sb.append("\naliases: ").append(aliases);
            return sb.toString();
        }
    }

    private class TFM_DynamicCommand extends Command implements PluginIdentifiableCommand
    {
        public TFM_DynamicCommand(String commandName, String description, String usage, List<String> aliases)
        {
            super(commandName, description, usage, aliases);
        }

        @Override
        public boolean execute(CommandSender sender, String commandLabel, String[] args)
        {
            boolean success = false;

            if (!getPlugin().isEnabled())
            {
                return false;
            }

            try
            {
                success = getPlugin().onCommand(sender, this, commandLabel, args);
            }
            catch (Throwable ex)
            {
                throw new CommandException("Unhandled exception executing command '" + commandLabel + "' in plugin " + getPlugin().getDescription().getFullName(), ex);
            }

            if (!success && getUsage().length() > 0)
            {
                for (String line : getUsage().replace("<command>", commandLabel).split("\n"))
                {
                    sender.sendMessage(line);
                }
            }

            return success;
        }

        @Override
        public Plugin getPlugin()
        {
            return TotalFreedomMod.plugin;
        }
    }

    public static TFM_CommandLoader getInstance()
    {
        return TFM_CommandScannerHolder.INSTANCE;
    }

    private static class TFM_CommandScannerHolder
    {
        private static final TFM_CommandLoader INSTANCE = new TFM_CommandLoader();
    }
}
