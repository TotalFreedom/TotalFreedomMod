package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_AdminWorld;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Go to the AdminWorld.", usage = "/<command> [guest < list | add <player> | remove <player> > | time <worldtime>]")
public class Command_adminworld extends TFM_Command
{
    private enum CommandMode
    {
        TELEPORT, GUEST, TIME, WEATHER
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

        switch (commandMode)
        {
            case TELEPORT:
            {
//                /adminworld
                if (sender_p.getWorld() == TFM_AdminWorld.getInstance().getAdminWorld())
                {
                    playerMsg("Going to the main world.");
                    sender_p.teleport(server.getWorlds().get(0).getSpawnLocation());
                }
                else
                {
                    playerMsg("Going to the AdminWorld.");
                    TFM_AdminWorld.getInstance().sendToAdminWorld(sender_p);
                }

                break;
            }
            case GUEST:
            {
//                /adminworld guest list
//                /adminworld guest add <player>
//                /adminworld guest remove <player>
                if (args.length == 2)
                {
                    if ("list".equalsIgnoreCase(args[1]))
                    {
                        //list
                    }
                }
                else if (args.length == 3)
                {
                    if ("add".equalsIgnoreCase(args[1]))
                    {
                        //add args[2]
                    }
                    else if (TFM_Util.isRemoveCommand(args[1]))
                    {
                        //remove args[2]
                    }
                }

                break;
            }
            case TIME:
            {
//                /adminworld time <morning|noon|evening|night>
                if (args.length == 2)
                {
                    //set time = args[1]
                }

                break;
            }
            case WEATHER:
            {
//                /adminworld weather <off|on|storm>
                if (args.length == 2)
                {
                    //set weather = args[1]
                }

                break;
            }
            default:
            {
                return false;
            }
        }

        return true;
    }
}
