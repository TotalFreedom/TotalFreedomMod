package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.GameRuleHandler;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "Toggles TotalFreedomMod settings", usage = "/<command> [option] [value] [value]")
public class Command_toggle extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            msg("Available toggles: ");
            msg("- waterplace");
            msg("- fireplace");
            msg("- lavaplace");
            msg("- fluidspread");
            msg("- lavadmg");
            msg("- firespread");
            msg("- frostwalk");
            msg("- firework");
            msg("- prelog");
            msg("- lockdown");
            msg("- petprotect");
            msg("- entitywipe");
            msg("- nonuke [range] [count]");
            msg("- explosives [radius]");
            return false;
        }

        if (args[0].equalsIgnoreCase("waterplace"))
        {
            toggle("Water placement is", ConfigEntry.ALLOW_WATER_PLACE);
            return true;
        }
        else if (args[0].equalsIgnoreCase("frostwalk"))
        {
            toggle("Frost walker enchantment is ", ConfigEntry.ALLOW_FROSTWALKER);
            return true;
        }
        else if (args[0].equalsIgnoreCase("fireplace"))
        {
            toggle("Fire placement is", ConfigEntry.ALLOW_FIRE_PLACE);
            return true;
        }
        else if (args[0].equalsIgnoreCase("lavaplace"))
        {
            toggle("Lava placement is", ConfigEntry.ALLOW_LAVA_PLACE);
            return true;
        }
        else if (args[0].equalsIgnoreCase("fluidspread"))
        {
            toggle("Fluid spread is", ConfigEntry.ALLOW_FLUID_SPREAD);
            return true;
        }
        else if (args[0].equalsIgnoreCase("lavadmg"))
        {
            toggle("Lava damage is", ConfigEntry.ALLOW_LAVA_DAMAGE);
            return true;
        }
        else if (args[0].equalsIgnoreCase("firespread"))
        {
            toggle("Fire spread is", ConfigEntry.ALLOW_FIRE_SPREAD);
            plugin.gr.setGameRule(GameRuleHandler.GameRule.DO_FIRE_TICK, ConfigEntry.ALLOW_FIRE_SPREAD.getBoolean());
            return true;
        }
        else if (args[0].equalsIgnoreCase("prelog"))
        {
            toggle("Command prelogging is", ConfigEntry.ENABLE_PREPROCESS_LOG);
            return true;
        }
        else if (args[0].equalsIgnoreCase("lockdown"))
        {
            boolean active = !plugin.lp.isLockdownEnabled();
            plugin.lp.setLockdownEnabled(active);
            FUtil.adminAction(sender.getName(), (active ? "A" : "De-a") + "ctivating server lockdown", true);
            return true;
        }
        else if (args[0].equalsIgnoreCase("petprotect"))
        {
            toggle("Tamed pet protection is", ConfigEntry.ENABLE_PET_PROTECT);
            return true;
        }
        else if (args[0].equalsIgnoreCase("entitywipe"))
        {
            toggle("Automatic entity wiping is", ConfigEntry.AUTO_ENTITY_WIPE);
            return true;
        }
        else if (args[0].equalsIgnoreCase("firework"))
        {
            toggle("Firework explosion is", ConfigEntry.ALLOW_FIREWORK_EXPLOSION);
            return true;
        }
        else if (args[0].equalsIgnoreCase("nonuke"))
        {
            if (args.length >= 2)
            {
                try
                {
                    ConfigEntry.NUKE_MONITOR_RANGE.setDouble(Math.max(1.0, Math.min(500.0, Double.parseDouble(args[1]))));
                }
                catch (NumberFormatException ex)
                {
                }
            }
            if (args.length >= 3)
            {
                try
                {
                    ConfigEntry.NUKE_MONITOR_COUNT_BREAK.setInteger(Math.max(1, Math.min(500, Integer.parseInt(args[2]))));
                }
                catch (NumberFormatException ex)
                {
                }
            }
            toggle("Nuke monitor is", ConfigEntry.NUKE_MONITOR_ENABLED);
            if (ConfigEntry.NUKE_MONITOR_ENABLED.getBoolean())
            {
                msg("Anti-freecam range is set to " + ConfigEntry.NUKE_MONITOR_RANGE.getDouble() + " blocks.");
                msg("Block throttle rate is set to " + ConfigEntry.NUKE_MONITOR_COUNT_BREAK.getInteger() + " blocks destroyed per 5 seconds.");
            }
            return true;
        }
        else if (args[0].equalsIgnoreCase("explosives"))
        {
            if (args.length == 2)
            {
                try
                {
                    ConfigEntry.EXPLOSIVE_RADIUS.setDouble(Math.max(1.0, Math.min(30.0, Double.parseDouble(args[1]))));
                }
                catch (NumberFormatException ex)
                {
                    msg("The input provided is not a valid integer.");
                    return true;
                }
            }
            toggle("Explosions are", ConfigEntry.ALLOW_EXPLOSIONS);
            if (ConfigEntry.ALLOW_EXPLOSIONS.getBoolean())
            {
                msg("Radius set to " + ConfigEntry.EXPLOSIVE_RADIUS.getDouble());
            }
            return true;
        }
        else
        {
            return false;
        }
    }

    private void toggle(final String name, final ConfigEntry entry)
    {
        msg(name + " now " + (entry.setBoolean(!entry.getBoolean()) ? "enabled." : "disabled."));
    }
}

