package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.GameRuleHandler;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Control mob rezzing parameters.", usage = "/<command> <on|off|setmax <count>|dragon|giant|ghast|slime>")
public class Command_moblimiter extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("on"))
        {
            ConfigEntry.MOB_LIMITER_ENABLED.setBoolean(true);
        }
        else if (args[0].equalsIgnoreCase("off"))
        {
            ConfigEntry.MOB_LIMITER_ENABLED.setBoolean(false);
        }
        else if (args[0].equalsIgnoreCase("dragon"))
        {
            ConfigEntry.MOB_LIMITER_DISABLE_DRAGON.setBoolean(!ConfigEntry.MOB_LIMITER_DISABLE_DRAGON.getBoolean());
        }
        else if (args[0].equalsIgnoreCase("giant"))
        {
            ConfigEntry.MOB_LIMITER_DISABLE_GIANT.setBoolean(!ConfigEntry.MOB_LIMITER_DISABLE_GIANT.getBoolean());
        }
        else if (args[0].equalsIgnoreCase("slime"))
        {
            ConfigEntry.MOB_LIMITER_DISABLE_SLIME.setBoolean(!ConfigEntry.MOB_LIMITER_DISABLE_SLIME.getBoolean());
        }
        else if (args[0].equalsIgnoreCase("ghast"))
        {
            ConfigEntry.MOB_LIMITER_DISABLE_GHAST.setBoolean(!ConfigEntry.MOB_LIMITER_DISABLE_GHAST.getBoolean());
        }
        else
        {
            if (args.length < 2)
            {
                return false;
            }

            if (args[0].equalsIgnoreCase("setmax"))
            {
                try
                {
                    ConfigEntry.MOB_LIMITER_MAX.setInteger(Math.max(0, Math.min(2000, Integer.parseInt(args[1]))));
                }
                catch (NumberFormatException nfex)
                {
                }
            }
        }

        if (ConfigEntry.MOB_LIMITER_ENABLED.getBoolean())
        {
            sender.sendMessage("Moblimiter enabled. Maximum mobcount set to: " + ConfigEntry.MOB_LIMITER_MAX.getInteger() + ".");

            msg("Dragon: " + (ConfigEntry.MOB_LIMITER_DISABLE_DRAGON.getBoolean() ? "disabled" : "enabled") + ".");
            msg("Giant: " + (ConfigEntry.MOB_LIMITER_DISABLE_GIANT.getBoolean() ? "disabled" : "enabled") + ".");
            msg("Slime: " + (ConfigEntry.MOB_LIMITER_DISABLE_SLIME.getBoolean() ? "disabled" : "enabled") + ".");
            msg("Ghast: " + (ConfigEntry.MOB_LIMITER_DISABLE_GHAST.getBoolean() ? "disabled" : "enabled") + ".");
        }
        else
        {
            msg("Moblimiter is disabled. No mob restrictions are in effect.");
        }

        plugin.gr.setGameRule(GameRuleHandler.GameRule.DO_MOB_SPAWNING, !ConfigEntry.MOB_LIMITER_ENABLED.getBoolean());

        return true;
    }
}
