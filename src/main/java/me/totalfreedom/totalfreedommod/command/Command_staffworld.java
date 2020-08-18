package me.totalfreedom.totalfreedommod.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.world.WorldTime;
import me.totalfreedom.totalfreedommod.world.WorldWeather;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Allows for staff to configure time, and weather of the StaffWorld, and allows for staff and ops to go to the StaffWorld.",
        usage = "/<command> [time <morning | noon | evening | night> | weather <off | rain | storm>]",
        aliases = "sw")
public class Command_staffworld extends FreedomCommand
{

    private enum CommandMode
    {
        TELEPORT, TIME, WEATHER
    }

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        CommandMode commandMode = null;

        if (args.length == 0)
        {
            commandMode = CommandMode.TELEPORT;
        }
        else if (args.length >= 2)
        {
            if ("time".equalsIgnoreCase(args[0]))
            {
                commandMode = CommandMode.TIME;
            }
            else if ("weather".equalsIgnoreCase(args[0]))
            {
                commandMode = CommandMode.WEATHER;
            }
        }

        if (commandMode == null)
        {
            return false;
        }

        try
        {
            switch (commandMode)
            {
                case TELEPORT:
                {
                    if (!(sender instanceof Player) || playerSender == null)
                    {
                        return false;
                    }

                    World staffWorld = null;
                    try
                    {
                        staffWorld = plugin.wm.staffworld.getWorld();
                    }
                    catch (Exception ex)
                    {
                    }

                    if (staffWorld == null || playerSender.getWorld() == staffWorld)
                    {
                        msg("Going to the main world.");
                        playerSender.teleport(server.getWorlds().get(0).getSpawnLocation());
                    }
                    else
                    {
                        msg("Going to the StaffWorld.");
                        plugin.wm.staffworld.sendToWorld(playerSender);
                    }
                    break;
                }
                case TIME:
                {
                    assertCommandPerms(sender, playerSender);

                    if (args.length == 2)
                    {
                        WorldTime timeOfDay = WorldTime.getByAlias(args[1]);
                        if (timeOfDay != null)
                        {
                            plugin.wm.staffworld.setTimeOfDay(timeOfDay);
                            msg("StaffWorld time set to: " + timeOfDay.name());
                        }
                        else
                        {
                            msg("Invalid time of day. Can be: sunrise, noon, sunset, midnight");
                        }
                    }
                    else
                    {
                        return false;
                    }

                    break;
                }
                case WEATHER:
                {
                    assertCommandPerms(sender, playerSender);

                    if (args.length == 2)
                    {
                        WorldWeather weatherMode = WorldWeather.getByAlias(args[1]);
                        if (weatherMode != null)
                        {
                            plugin.wm.staffworld.setWeatherMode(weatherMode);
                            msg("StaffWorld weather set to: " + weatherMode.name());
                        }
                        else
                        {
                            msg("Invalid weather mode. Can be: off, rain, storm");
                        }
                    }
                    else
                    {
                        return false;
                    }

                    break;
                }
                default:
                {
                    return false;
                }
            }
        }
        catch (PermissionDeniedException ex)
        {
            if (ex.getMessage().isEmpty())
            {
                return noPerms();
            }
            sender.sendMessage(ex.getMessage());
            return true;
        }

        return true;
    }

    // TODO: Redo this properly
    private void assertCommandPerms(CommandSender sender, Player playerSender) throws PermissionDeniedException
    {
        if (!(sender instanceof Player) || playerSender == null || !isStaff(sender))
        {
            throw new PermissionDeniedException();
        }
    }

    private class PermissionDeniedException extends Exception
    {

        private static final long serialVersionUID = 1L;

        private PermissionDeniedException()
        {
            super("");
        }

        private PermissionDeniedException(String string)
        {
            super(string);
        }
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (!plugin.sl.isStaff(sender))
        {
            return Collections.emptyList();
        }
        if (args.length == 1)
        {
            return Arrays.asList("time", "weather");
        }
        else if (args.length == 2)
        {
            if (args[0].equals("time"))
            {
                return Arrays.asList("morning", "noon", "evening", "night");
            }
            else if (args[0].equals("weather"))
            {
                return Arrays.asList("off", "rain", "storm");
            }
        }
        return Collections.emptyList();
    }
}