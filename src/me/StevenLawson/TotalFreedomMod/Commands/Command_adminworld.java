package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.World.TFM_AdminWorld;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.BOTH)
@CommandParameters(description = "Go to the AdminWorld.", usage = "/<command> [guest < list | purge | add <player> | remove <player> > | time <morning | noon | evening | night> | weather <off | on | storm>]")
public class Command_adminworld extends TFM_Command
{
    private enum CommandMode
    {
        TELEPORT, GUEST, TIME, WEATHER;
    }

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        CommandMode commandMode = null;

        if (args.length == 0)
        {
            commandMode = CommandMode.TELEPORT;
        }
        else if (args.length >= 2)
        {
            if ("guest".equalsIgnoreCase(args[0]))
            {
                commandMode = CommandMode.GUEST;
            }
            else if ("time".equalsIgnoreCase(args[0]))
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
                    if (!(sender instanceof Player) || sender_p == null)
                    {
                        return true;
                    }

                    World adminWorld = null;
                    try
                    {
                        adminWorld = TFM_AdminWorld.getInstance().getWorld();
                    }
                    catch (Exception ex)
                    {
                    }

                    if (adminWorld == null || sender_p.getWorld() == adminWorld)
                    {
                        playerMsg("Going to the main world.");
                        sender_p.teleport(server.getWorlds().get(0).getSpawnLocation());
                    }
                    else
                    {
                        if (TFM_AdminWorld.getInstance().canAccessWorld(sender_p))
                        {
                            playerMsg("Going to the AdminWorld.");
                            TFM_AdminWorld.getInstance().sendToWorld(sender_p);
                        }
                        else
                        {
                            playerMsg("You don't have permission to access the AdminWorld.");
                        }
                    }

                    break;
                }
                case GUEST:
                {
                    if (args.length == 2)
                    {
                        if ("list".equalsIgnoreCase(args[1]))
                        {
                            playerMsg("AdminWorld guest list: " + TFM_AdminWorld.getInstance().guestListToString());
                        }
                        else if ("purge".equalsIgnoreCase(args[1]))
                        {
                            assertCommandPerms(sender, sender_p);
                            TFM_AdminWorld.getInstance().purgeGuestList();
                            TFM_Util.adminAction(sender.getName(), "AdminWorld guest list purged.", false);
                        }
                        else
                        {
                            return false;
                        }
                    }
                    else if (args.length == 3)
                    {
                        assertCommandPerms(sender, sender_p);

                        if ("add".equalsIgnoreCase(args[1]))
                        {
                            final Player player = getPlayer(args[2]);

                            if (player == null)
                            {
                                sender.sendMessage(TFM_Command.PLAYER_NOT_FOUND);
                                return true;
                            }

                            if (TFM_AdminWorld.getInstance().addGuest(player, sender_p))
                            {
                                TFM_Util.adminAction(sender.getName(), "AdminWorld guest added: " + player.getName(), false);
                            }
                            else
                            {
                                playerMsg("Could not add player to guest list.");
                            }
                        }
                        else if ("remove".equals(args[1]))
                        {
                            final Player player = TFM_AdminWorld.getInstance().removeGuest(args[2]);
                            if (player != null)
                            {
                                TFM_Util.adminAction(sender.getName(), "AdminWorld guest removed: " + player.getName(), false);
                            }
                            else
                            {
                                playerMsg("Can't find guest entry for: " + args[2]);
                            }
                        }
                        else
                        {
                            return false;
                        }
                    }

                    break;
                }
                case TIME:
                {
                    assertCommandPerms(sender, sender_p);

                    if (args.length == 2)
                    {
                        TFM_AdminWorld.TimeOfDay timeOfDay = TFM_AdminWorld.TimeOfDay.getByAlias(args[1]);
                        if (timeOfDay != null)
                        {
                            TFM_AdminWorld.getInstance().setTimeOfDay(timeOfDay);
                            playerMsg("AdminWorld time set to: " + timeOfDay.name());
                        }
                        else
                        {
                            playerMsg("Invalid time of day. Can be: sunrise, noon, sunset, midnight");
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
                    assertCommandPerms(sender, sender_p);

                    if (args.length == 2)
                    {
                        TFM_AdminWorld.WeatherMode weatherMode = TFM_AdminWorld.WeatherMode.getByAlias(args[1]);
                        if (weatherMode != null)
                        {
                            TFM_AdminWorld.getInstance().setWeatherMode(weatherMode);
                            playerMsg("AdminWorld weather set to: " + weatherMode.name());
                        }
                        else
                        {
                            playerMsg("Invalid weather mode. Can be: off, rain, storm");
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
            sender.sendMessage(ex.getMessage());
        }

        return true;
    }

    private void assertCommandPerms(CommandSender sender, Player sender_p) throws PermissionDeniedException
    {
        if (!(sender instanceof Player) || sender_p == null || !TFM_AdminList.isSuperAdmin(sender))
        {
            throw new PermissionDeniedException(TFM_Command.MSG_NO_PERMS);
        }
    }

    private class PermissionDeniedException extends Exception
    {
        private static final long serialVersionUID = 1L;

        private PermissionDeniedException(String string)
        {
            super(string);
        }
    }
}
