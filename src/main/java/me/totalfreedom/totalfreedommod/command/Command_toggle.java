package me.totalfreedom.totalfreedommod.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.GameRuleHandler;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
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
            msg("- unsafeenchs");
            msg("- bells");
            msg("- armorstands");
            msg("- structureblocks");
            msg("- jigsaws");
            msg("- grindstones");
            msg("- jukeboxes");
            msg("- spawners");
            msg("- 4chan");
            msg("- beehives");
            msg("- respawnanchors");
            msg("- autotp");
            msg("- autoclear");
            msg("- minecarts");
            msg("- landmines");
            msg("- mp44");
            msg("- tossmob");
            return false;
        }

        switch (args[0].toLowerCase())
        {
            case "waterplace":
            {
                toggle("Water placement is", ConfigEntry.ALLOW_WATER_PLACE);
                break;
            }

            case "frostwalk":
            {
                toggle("Frost walker enchantment is", ConfigEntry.ALLOW_FROSTWALKER);
                break;
            }

            case "fireplace":
            {
                toggle("Fire placement is", ConfigEntry.ALLOW_FIRE_PLACE);
                break;
            }

            case "lavaplace":
            {
                toggle("Lava placement is", ConfigEntry.ALLOW_LAVA_PLACE);
                break;
            }

            case "fluidspread":
            {
                toggle("Fluid spread is", ConfigEntry.ALLOW_FLUID_SPREAD);
                break;
            }

            case "lavadmg":
            {
                toggle("Lava damage is", ConfigEntry.ALLOW_LAVA_DAMAGE);
                break;
            }

            case "firespread":
            {
                toggle("Fire spread is", ConfigEntry.ALLOW_FIRE_SPREAD);
                plugin.gr.setGameRule(GameRuleHandler.GameRule.DO_FIRE_TICK, ConfigEntry.ALLOW_FIRE_SPREAD.getBoolean());
                break;
            }

            case "prelog":
            {
                toggle("Command prelogging is", ConfigEntry.ENABLE_PREPROCESS_LOG);
                break;
            }

            case "lockdown":
            {
                boolean active = !plugin.lp.isLockdownEnabled();
                plugin.lp.setLockdownEnabled(active);
                FUtil.staffAction(sender.getName(), (active ? "A" : "De-a") + "ctivating server lockdown", true);
                break;
            }

            case "petprotect":
            {
                toggle("Tamed pet protection is", ConfigEntry.ENABLE_PET_PROTECT);
                break;
            }

            case "entitywipe":
            {
                toggle("Automatic entity wiping is", ConfigEntry.AUTO_ENTITY_WIPE);
                break;
            }

            case "firework":
            {
                toggle("Firework explosion is", ConfigEntry.ALLOW_FIREWORK_EXPLOSION);
                break;
            }

            case "nonuke":
            {
                if (args.length >= 2)
                {
                    try
                    {
                        ConfigEntry.NUKE_MONITOR_RANGE.setDouble(Math.max(1.0, Math.min(500.0, Double.parseDouble(args[1]))));
                    }
                    catch (NumberFormatException ex)
                    {
                        msg("The input provided is not a valid integer.");
                        return true;
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
                        msg("The input provided is not a valid integer.");
                        return true;
                    }
                }

                toggle("Nuke monitor is", ConfigEntry.NUKE_MONITOR_ENABLED);

                if (ConfigEntry.NUKE_MONITOR_ENABLED.getBoolean())
                {
                    msg("Anti-freecam range is set to " + ConfigEntry.NUKE_MONITOR_RANGE.getDouble() + " blocks.");
                    msg("Block throttle rate is set to " + ConfigEntry.NUKE_MONITOR_COUNT_BREAK.getInteger() + " blocks destroyed per 5 seconds.");
                }
                break;
            }

            case "explosives":
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
                break;
            }

            case "unsafeenchs":
            {
                toggle("Unsafe enchantments are", ConfigEntry.ALLOW_UNSAFE_ENCHANTMENTS);
                break;
            }

            case "bells":
            {
                toggle("The ringing of bells is", ConfigEntry.ALLOW_BELLS);
                break;
            }

            case "armorstands":
            {
                toggle("The placement of armor stands is", ConfigEntry.ALLOW_ARMOR_STANDS);
                break;
            }

            case "structureblocks":
            {
                toggle("Structure blocks are", ConfigEntry.ALLOW_STRUCTURE_BLOCKS);
                break;
            }

            case "jigsaws":
            {
                toggle("Jigsaws are", ConfigEntry.ALLOW_JIGSAWS);
                break;
            }

            case "grindstones":
            {
                toggle("Grindstones are", ConfigEntry.ALLOW_GRINDSTONES);
                break;
            }

            case "jukeboxes":
            {
                toggle("Jukeboxes are", ConfigEntry.ALLOW_JUKEBOXES);
                break;
            }

            case "spawners":
            {
                toggle("Spawners are", ConfigEntry.ALLOW_SPAWNERS);
                break;
            }

            case "4chan":
            {
                toggle("4chan mode is", ConfigEntry.FOURCHAN_ENABLED);
                break;
            }

            case "beehives":
            {
                toggle("Beehives are", ConfigEntry.ALLOW_BEEHIVES);
                break;
            }

            case "respawnanchors":
            {
                toggle("Respawn anchors are", ConfigEntry.ALLOW_RESPAWN_ANCHORS);
                break;
            }

            case "autotp":
            {
                toggle("Teleportation on join is", ConfigEntry.AUTO_TP);
                break;
            }

            case "autoclear":
            {
                toggle("Clearing inventories on join is", ConfigEntry.AUTO_CLEAR);
                break;
            }

            case "minecarts":
            {
                toggle("Minecarts are", ConfigEntry.ALLOW_MINECARTS);
                break;
            }

            case "landmines":
            {
                toggle("Landmines are", ConfigEntry.LANDMINES_ENABLED);
                break;
            }

            case "mp44":
            {
                toggle("MP44 is", ConfigEntry.MP44_ENABLED);
                break;
            }

            case "tossmob":
            {
                toggle("Tossmob is", ConfigEntry.TOSSMOB_ENABLED);
                break;
            }
        }
        return true;
    }

    private void toggle(final String name, final ConfigEntry entry)
    {
        msg(name + " now " + (entry.setBoolean(!entry.getBoolean()) ? "enabled." : "disabled."));
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
            return Arrays.asList(
                    "waterplace", "fireplace", "lavaplace", "fluidspread", "lavadmg", "firespread", "frostwalk",
                    "firework", "prelog", "lockdown", "petprotect", "entitywipe", "nonuke", "explosives", "unsafeenchs",
                    "bells", "armorstands", "structureblocks", "jigsaws", "grindstones", "jukeboxes", "spawners", "4chan", "beehives",
                    "respawnanchors", "autotp", "autoclear", "minecarts", "mp44", "landmines", "tossmob");
        }
        return Collections.emptyList();
    }
}