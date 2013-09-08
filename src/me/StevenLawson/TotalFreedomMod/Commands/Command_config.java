package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_ConfigEntry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Temporarily change config parameters.", usage = "/<command> <entry> <value>")
public class Command_config extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 2)
        {
            return false;
        }

        TFM_ConfigEntry entry = TFM_ConfigEntry.findConfigEntry(args[0]);

        if (entry == null)
        {
            sender.sendMessage("Can't find configuration option: " + args[0]);
            return true;
        }

        Object newValue = null;
        final String newValueString = args[1].trim();
        final Class<?> type = entry.getType();
        try
        {
            if (type.isAssignableFrom(Integer.class))
            {
                newValue = new Integer(newValueString);
                entry.setInteger((Integer) newValue);
            }
            else if (type.isAssignableFrom(Double.class))
            {
                newValue = new Double(newValueString);
                entry.setDouble((Double) newValue);
            }
            else if (type.isAssignableFrom(Boolean.class))
            {
                newValue = Boolean.valueOf(newValueString);
                entry.setBoolean((Boolean) newValue);
            }
            else if (type.isAssignableFrom(String.class))
            {
                newValue = newValueString;
                entry.setString((String) newValue);
            }
        }
        catch (Exception ex)
        {
        }

        if (newValue != null)
        {
            sender.sendMessage(String.format("Set configuration entry \"%s\" to \"%s\" value \"%s\".",
                    entry.toString(), type.getName(), newValue.toString()));
        }
        else
        {
            sender.sendMessage("Could not parse value \"" + newValueString + "\" as type \"" + type.getName() + "\".");
        }

        return true;
    }
}
