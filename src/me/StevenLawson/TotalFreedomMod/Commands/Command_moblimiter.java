package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import me.StevenLawson.TotalFreedomMod.TFM_GameRuleHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Control mob rezzing parameters.", usage = "/<command> <on|off|setmax <count>|dragon|giant|ghast|slime>")
public class Command_moblimiter extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("on"))
        {
            TFM_ConfigEntry.MOB_LIMITER_ENABLED.setBoolean(true);
        }
        else if (args[0].equalsIgnoreCase("off"))
        {
            TFM_ConfigEntry.MOB_LIMITER_ENABLED.setBoolean(false);
        }
        else if (args[0].equalsIgnoreCase("dragon"))
        {
            TFM_ConfigEntry.MOB_LIMITER_DISABLE_DRAGON.setBoolean(!TFM_ConfigEntry.MOB_LIMITER_DISABLE_DRAGON.getBoolean());
        }
        else if (args[0].equalsIgnoreCase("giant"))
        {
            TFM_ConfigEntry.MOB_LIMITER_DISABLE_GIANT.setBoolean(!TFM_ConfigEntry.MOB_LIMITER_DISABLE_GIANT.getBoolean());
        }
        else if (args[0].equalsIgnoreCase("slime"))
        {
            TFM_ConfigEntry.MOB_LIMITER_DISABLE_SLIME.setBoolean(!TFM_ConfigEntry.MOB_LIMITER_DISABLE_SLIME.getBoolean());
        }
        else if (args[0].equalsIgnoreCase("ghast"))
        {
            TFM_ConfigEntry.MOB_LIMITER_DISABLE_GHAST.setBoolean(!TFM_ConfigEntry.MOB_LIMITER_DISABLE_GHAST.getBoolean());
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
                    TFM_ConfigEntry.MOB_LIMITER_MAX.setInteger(Math.max(0, Math.min(2000, Integer.parseInt(args[1]))));
                }
                catch (NumberFormatException nfex)
                {
                }
            }
        }

        if (TFM_ConfigEntry.MOB_LIMITER_ENABLED.getBoolean())
        {
            sender.sendMessage("Moblimiter enabled. Maximum mobcount set to: " + TFM_ConfigEntry.MOB_LIMITER_MAX.getInteger() + ".");

            playerMsg("Dragon: " + (TFM_ConfigEntry.MOB_LIMITER_DISABLE_DRAGON.getBoolean() ? "disabled" : "enabled") + ".");
            playerMsg("Giant: " + (TFM_ConfigEntry.MOB_LIMITER_DISABLE_GIANT.getBoolean() ? "disabled" : "enabled") + ".");
            playerMsg("Slime: " + (TFM_ConfigEntry.MOB_LIMITER_DISABLE_SLIME.getBoolean() ? "disabled" : "enabled") + ".");
            playerMsg("Ghast: " + (TFM_ConfigEntry.MOB_LIMITER_DISABLE_GHAST.getBoolean() ? "disabled" : "enabled") + ".");
        }
        else
        {
            playerMsg("Moblimiter is disabled. No mob restrictions are in effect.");
        }

        TFM_GameRuleHandler.setGameRule(TFM_GameRuleHandler.TFM_GameRule.DO_MOB_SPAWNING, !TFM_ConfigEntry.MOB_LIMITER_ENABLED.getBoolean());

        return true;
    }
}
