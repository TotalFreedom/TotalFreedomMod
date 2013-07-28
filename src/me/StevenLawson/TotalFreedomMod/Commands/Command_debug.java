package me.StevenLawson.TotalFreedomMod.Commands;

import java.lang.reflect.Field;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
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
        setStaticValue("me.StevenLawson.TotalFreedomMod.TotalFreedomMod", args[0], null);
        return true;
    }

    public static void setStaticValue(final String className, final String fieldName, final Object newValue)
    {
        try
        {
            Class<?> forName = Class.forName(className);
            if (forName != null)
            {
                final Field field = forName.getDeclaredField(fieldName);
                if (field != null)
                {
                    Class<?> type = field.getType();

                    TFM_Log.info("type.toString() = " + type.toString() + ", type.isPrimitive() = " + type.isPrimitive());

//                    TFM_Log.info(type.toString());
//
//                    if (Boolean.class.isAssignableFrom(type))
//                    {
//                        TFM_Log.info("boolean");
//                    }
//                    else if (Integer.class.isAssignableFrom(type))
//                    {
//                        TFM_Log.info("integer");
//                    }
//                    else if (Double.class.isAssignableFrom(type))
//                    {
//                        TFM_Log.info("double");
//                    }
//                    else if (String.class.isAssignableFrom(type))
//                    {
//                        TFM_Log.info("string");
//                    }

//                    field.setAccessible(true);
//
//                    final Object oldValue = field.get(Class.forName(className));
//                    if (oldValue != null)
//                    {
//                        field.set(oldValue, newValue);
//                    }
//
//                    field.setAccessible(false);
                }
            }
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }
    }
}
