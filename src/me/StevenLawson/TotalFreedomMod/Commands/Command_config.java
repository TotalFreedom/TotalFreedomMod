package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_Config;
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

        boolean valueSet = false;

        final String newValueString = args[1].trim();
        final Class<?> type = entry.getType();
        try
        {
            if (type.isAssignableFrom(Integer.class))
            {
                entry.setInteger(new Integer(newValueString));
                valueSet = true;
            }
            else if (type.isAssignableFrom(Double.class))
            {
                entry.setDouble(new Double(newValueString));
                valueSet = true;
            }
            else if (type.isAssignableFrom(Boolean.class))
            {
                entry.setBoolean(Boolean.valueOf(newValueString));
                valueSet = true;
            }
            else if (type.isAssignableFrom(String.class))
            {
                TFM_Config.getInstance().set(entry, newValueString, String.class);
                valueSet = true;
            }
        }
        catch (Exception ex)
        {
        }

        if (!valueSet)
        {
            sender.sendMessage(String.format("Set configuration entry \"%s\" to \"%s\" value \"%s\".",
                    entry.toString(), type.getName(), newValueString));
        }
        else
        {
            sender.sendMessage("Could not parse value \"" + newValueString + "\" as type \"" + type.getName() + "\".");
        }

        return true;
    }
}
