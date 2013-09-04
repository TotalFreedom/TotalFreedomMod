package me.StevenLawson.TotalFreedomMod.Commands;

import java.lang.reflect.Field;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "For developers only - debug things via reflection.", usage = "/<command>")
public class Command_debug extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 3)
        {
            return false;
        }

        try
        {
            String className = args[0];
            String fieldName = args[1];
            String newValue = StringUtils.join(ArrayUtils.subarray(args, 2, args.length), " ");

            if (className.equalsIgnoreCase("_"))
            {
                className = "me.StevenLawson.TotalFreedomMod.TotalFreedomMod";
            }

            setStaticValue(className, fieldName, newValue);

            sender.sendMessage("Debug: OK");
        }
        catch (Exception ex)
        {
            sender.sendMessage(ex.getMessage());
        }

        return true;
    }

    public static void setStaticValue(final String className, final String fieldName, final String newValueString) throws Exception
    {
        Class<?> forName = Class.forName(className);
        if (forName != null)
        {
            final Field field = forName.getDeclaredField(fieldName);
            if (field != null)
            {
                Object newValue;

                Class<?> type = field.getType();
                if (type.isPrimitive())
                {
                    if (type.getName().equals("int"))
                    {
                        newValue = Integer.parseInt(newValueString);
                    }
                    else if (type.getName().equals("double"))
                    {
                        newValue = Double.parseDouble(newValueString);
                    }
                    else if (type.getName().equals("boolean"))
                    {
                        newValue = Boolean.parseBoolean(newValueString);
                    }
                    else
                    {
                        throw new Exception("Unknown primitive field type.");
                    }
                }
                else
                {
                    if (type.isAssignableFrom(Integer.class))
                    {
                        newValue = new Integer(newValueString);
                    }
                    else if (type.isAssignableFrom(Double.class))
                    {
                        newValue = new Double(newValueString);
                    }
                    else if (type.isAssignableFrom(Boolean.class))
                    {
                        newValue = Boolean.valueOf(newValueString);
                    }
                    else if (type.isAssignableFrom(String.class))
                    {
                        newValue = newValueString;
                    }
                    else
                    {
                        throw new Exception("Unknown complex field type.");
                    }
                }

                field.setAccessible(true);

                final Object oldValue = field.get(Class.forName(className));
                if (oldValue != null)
                {
                    field.set(oldValue, newValue);
                }

                field.setAccessible(false);
            }
        }
    }
}
