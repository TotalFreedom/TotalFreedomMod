package me.totalfreedom.totalfreedommod.command;

import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.staff.StaffMember;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.spigotmc.SpigotConfig;

public abstract class FreedomCommand implements CommandExecutor, TabCompleter
{
    public static final String COMMAND_PREFIX = "Command_";

    @Getter
    private static CommandMap commandMap;
    @Getter
    private final String name;
    private final String description;
    private final String usage;
    @Getter
    private final String aliases;
    private final Rank level;
    private final SourceType source;
    private final boolean blockHostConsole;
    private final int cooldown;
    private final CommandParameters params;
    @Getter
    private final CommandPermissions perms;

    protected CommandSender sender;

    protected final TotalFreedomMod plugin = TotalFreedomMod.getPlugin();
    protected final Server server = plugin.getServer();

    public static final String UNKNOWN_COMMAND = ChatColor.WHITE + SpigotConfig.unknownCommandMessage;
    public static final String YOU_ARE_OP = ChatColor.YELLOW + "You are now op!";
    public static final String YOU_ARE_NOT_OP = ChatColor.YELLOW + "You are no longer op!";
    public static final String PLAYER_NOT_FOUND = ChatColor.GRAY + "Player not found!";
    public static final String ONLY_CONSOLE = ChatColor.RED + "Only console senders may execute this command!";
    public static final String ONLY_IN_GAME = ChatColor.RED + "Only in-game players may execute this command!";
    public static final String NO_PERMISSION = ChatColor.RED + "You do not have permission to execute this command.";

    public static final Timer timer = new Timer();
    public static final Map<CommandSender, FreedomCommand> COOLDOWN_TIMERS = new HashMap<>();

    FreedomCommand()
    {
        params = getClass().getAnnotation(CommandParameters.class);
        perms = getClass().getAnnotation(CommandPermissions.class);
        this.name = getClass().getSimpleName().replace(COMMAND_PREFIX, "").toLowerCase();
        this.description = params.description();
        this.usage = params.usage();
        this.aliases = params.aliases();
        this.level = perms.level();
        this.source = perms.source();
        this.blockHostConsole = perms.blockHostConsole();
        this.cooldown = perms.cooldown();
    }

    public void register()
    {
        FCommand cmd = new FCommand(this.name);
        if (this.aliases != null)
        {
            cmd.setAliases(Arrays.asList(StringUtils.split(this.aliases, ",")));
        }
        if (this.description != null)
        {
            cmd.setDescription(this.description);
        }
        if (this.usage != null)
        {
            cmd.setUsage(this.usage);
        }
        getCommandMap().register("totalfreedommod", cmd);
        cmd.setExecutor(this);
    }

    public static CommandMap getCommandMap()
    {
        if (commandMap == null)
        {
            try
            {
                final Field f = Bukkit.getServer().getPluginManager().getClass().getDeclaredField("commandMap");
                f.setAccessible(true);
                commandMap = (CommandMap)f.get(Bukkit.getServer().getPluginManager());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return commandMap;
    }

    private final class FCommand extends Command
    {
        private FreedomCommand cmd = null;

        private FCommand(String command)
        {
            super(command);
        }

        public void setExecutor(FreedomCommand cmd)
        {
            this.cmd = cmd;
        }

        public boolean execute(CommandSender sender, String commandLabel, String[] args)
        {
            if (cmd != null)
            {
                cmd.sender = sender;

                if (COOLDOWN_TIMERS.containsKey(sender) && COOLDOWN_TIMERS.containsValue(cmd))
                {
                    msg(ChatColor.RED + "You are on cooldown for this command.");
                    return true;
                }

                if (perms.blockHostConsole() && FUtil.isFromHostConsole(sender.getName()) && !FUtil.inDeveloperMode())
                {
                    msg(ChatColor.RED + "Host console is not allowed to use this command!");
                    return true;
                }

                if (!plugin.rm.getRank(sender).isAtLeast(perms.level()))
                {
                    msg(NO_PERMISSION);
                    return true;
                }

                if (perms.source() == SourceType.ONLY_CONSOLE && sender instanceof Player)
                {
                    msg(ONLY_CONSOLE);
                    return true;
                }

                if (perms.source() == SourceType.ONLY_IN_GAME && sender instanceof ConsoleCommandSender)
                {
                    msg(ONLY_IN_GAME);
                    return true;
                }

                if (perms.cooldown() != 0 && !isStaff(sender))
                {
                    COOLDOWN_TIMERS.put(sender, cmd);
                    timer.schedule(new TimerTask()
                    {
                        @Override
                        public void run()
                        {
                            COOLDOWN_TIMERS.remove(sender);
                        }
                    }, perms.cooldown() * 1000);
                }
                return cmd.onCommand(sender, this, commandLabel, args);
            }
            return false;
        }

        @Override
        public List<String> tabComplete(CommandSender sender, String alias, String[] args)
        {
            if (cmd != null)
            {
                return cmd.onTabComplete(sender, this, alias, args);
            }
            return null;
        }
    }

    protected void msg(CommandSender sender, String message)
    {
        sender.sendMessage(ChatColor.GRAY + message);
    }

    protected void msg(Player player, String message)
    {
        player.sendMessage(ChatColor.GRAY + message);
    }

    protected void msg(Player player, String message, ChatColor color)
    {
        player.sendMessage(color + message);
    }

    protected void msg(String message)
    {
        msg(sender, message);
    }

    protected void msg(String message, ChatColor color)
    {
        msg(color + message);
    }

    protected void msg(String message, net.md_5.bungee.api.ChatColor color)
    {
        msg(color + message);
    }

    protected boolean isStaff(Player player)
    {
        return plugin.sl.isStaff(player);
    }

    protected boolean isStaff(CommandSender sender)
    {
        return plugin.sl.isStaff(sender);
    }

    protected void checkConsole()
    {
        if (!isConsole())
        {
            throw new CommandFailException(ONLY_CONSOLE);
        }
    }

    protected void checkPlayer()
    {
        if (isConsole())
        {
            throw new CommandFailException(ONLY_IN_GAME);
        }
    }

    protected void checkRank(Rank rank)
    {
        if (!plugin.rm.getRank(sender).isAtLeast(rank))
        {
            noPerms();
        }
    }

    protected void checkNotHostConsole()
    {
        if (isConsole() && FUtil.isFromHostConsole(sender.getName()))
        {
            throw new CommandFailException("This command can not be used from the host console.");
        }
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        try
        {
            boolean run = run(sender, sender instanceof ConsoleCommandSender ? null : (Player) sender, cmd, commandLabel, args, sender instanceof ConsoleCommandSender);
            if (!run)
            {
                msg(ChatColor.WHITE + cmd.getUsage().replace("<command>", cmd.getLabel()));
                return true;
            }
        }
        catch (CommandFailException ex)
        {
            msg(ChatColor.RED + ex.getMessage());
        }
        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args)
    {
        List<String> options = getTabCompleteOptions(sender, command, alias, args);
        if (options == null)
        {
            return null;
        }
        return StringUtil.copyPartialMatches(args[args.length - 1], options, Lists.newArrayList());
    }

    public abstract boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole);

    protected List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        return FUtil.getPlayerList();
    }

    protected boolean isConsole()
    {
        return sender instanceof ConsoleCommandSender;
    }

    protected Player getPlayer(String name)
    {
        return Bukkit.getPlayer(name);
    }

    protected Player getPlayer(String name, Boolean nullVanished)
    {
        Player player = Bukkit.getPlayer(name);
        if (player != null)
        {
            if (nullVanished && plugin.sl.isVanished(player.getName()) && !plugin.sl.isStaff(sender))
            {
                return null;
            }
        }
        return player;
    }

    protected StaffMember getAdmin(CommandSender sender)
    {
        return plugin.sl.getAdmin(sender);
    }

    protected StaffMember getAdmin(Player player)
    {
        return plugin.sl.getAdmin(player);
    }

    protected PlayerData getData(Player player)
    {
        return plugin.pl.getData(player);
    }

    protected boolean noPerms()
    {
        throw new CommandFailException(NO_PERMISSION);
    }

    public static FreedomCommand getFrom(Command command)
    {
        try
        {
            return (FreedomCommand) (((PluginCommand) command).getExecutor());
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}