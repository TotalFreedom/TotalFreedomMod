package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_GameRuleHandler;
import me.StevenLawson.TotalFreedomMod.TFM_GameRuleHandler.TFM_GameRule;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Toggles TotalFreedomMod settings", usage = "/<command> [option] [value] [value]")
public class Command_toggle extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            playerMsg("Available toggles: ");
            playerMsg("- waterplace");
            playerMsg("- fireplace");
            playerMsg("- lavaplace");
            playerMsg("- fluidspread");
            playerMsg("- lavadmg");
            playerMsg("- firespread");
            playerMsg("- prelog");
            playerMsg("- lockdown");
            playerMsg("- petprotect");
            playerMsg("- droptoggle");
            playerMsg("- nonuke");
            playerMsg("- explosives");
            return false;
        }

        if (args[0].equals("waterplace"))
        {
            toggle("Water placement is", TFM_ConfigEntry.ALLOW_WATER_PLACE);
            return true;
        }

        if (args[0].equals("fireplace"))
        {
            toggle("Fire placement is", TFM_ConfigEntry.ALLOW_FIRE_PLACE);
            return true;
        }

        if (args[0].equals("lavaplace"))
        {
            toggle("Lava placement is", TFM_ConfigEntry.ALLOW_LAVA_PLACE);
            return true;
        }

        if (args[0].equals("fluidspread"))
        {
            toggle("Fluid spread is", TFM_ConfigEntry.ALLOW_FLUID_SPREAD);
            return true;
        }

        if (args[0].equals("lavadmg"))
        {
            toggle("Lava damage is", TFM_ConfigEntry.ALLOW_LAVA_DAMAGE);
            return true;
        }

        if (args[0].equals("firespread"))
        {
            toggle("Fire spread is", TFM_ConfigEntry.ALLOW_FIRE_SPREAD);
            TFM_GameRuleHandler.setGameRule(TFM_GameRule.DO_FIRE_TICK, TFM_ConfigEntry.ALLOW_FIRE_SPREAD.getBoolean());
            return true;
        }

        if (args[0].equals("prelog"))
        {
            toggle("Command prelogging is", TFM_ConfigEntry.ENABLE_PREPROCESS_LOG);
            return true;
        }

        if (args[0].equals("lockdown"))
        {
            TFM_Util.adminAction(sender.getName(), (TotalFreedomMod.lockdownEnabled ? "De-a" : "A") + "ctivating server lockdown", true);
            TotalFreedomMod.lockdownEnabled = !TotalFreedomMod.lockdownEnabled;
            return true;
        }

        if (args[0].equals("petprotect"))
        {
            toggle("Tamed pet protection is", TFM_ConfigEntry.ENABLE_PET_PROTECT);
            return true;
        }

        if (args[0].equals("droptoggle"))
        {
            toggle("Automatic entity wiping is", TFM_ConfigEntry.AUTO_ENTITY_WIPE);
            return true;
        }

        if (args[0].equals("nonuke"))
        {
            if (args.length >= 2)
            {
                try
                {
                    TFM_ConfigEntry.NUKE_MONITOR_RANGE.setDouble(Math.max(1.0, Math.min(500.0, Double.parseDouble(args[1]))));
                }
                catch (NumberFormatException nfex)
                {
                }
            }

            if (args.length >= 3)
            {
                try
                {
                    TFM_ConfigEntry.NUKE_MONITOR_COUNT_BREAK.setInteger(Math.max(1, Math.min(500, Integer.parseInt(args[2]))));
                }
                catch (NumberFormatException nfex)
                {
                }
            }

            toggle("Nuke monitor is", TFM_ConfigEntry.NUKE_MONITOR_ENABLED);

            if (TFM_ConfigEntry.NUKE_MONITOR_ENABLED.getBoolean())
            {
                playerMsg("Anti-freecam range is set to " + TFM_ConfigEntry.NUKE_MONITOR_RANGE.getDouble() + " blocks.");
                playerMsg("Block throttle rate is set to " + TFM_ConfigEntry.NUKE_MONITOR_COUNT_BREAK.getInteger() + " blocks destroyed per 5 seconds.");
            }

            return true;
        }
        if (args[0].equals("explosives"))
        {
            if (args.length == 2)
            {
                try
                {
                    TFM_ConfigEntry.EXPLOSIVE_RADIUS.setDouble(Math.max(1.0, Math.min(30.0, Double.parseDouble(args[1]))));
                }
                catch (NumberFormatException ex)
                {
                    playerMsg(ex.getMessage());
                    return true;
                }
            }

            toggle("Explosions are", TFM_ConfigEntry.ALLOW_EXPLOSIONS);

            if (TFM_ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
            {
                playerMsg("Radius set to " + TFM_ConfigEntry.EXPLOSIVE_RADIUS.getDouble());
            }
            return true;
        }

        return false;
    }

    private void toggle(String name, TFM_ConfigEntry entry)
    {
        playerMsg(name + " now " + (entry.setBoolean(!entry.getBoolean()) ? "enabled." : "disabled."));
    }
}
