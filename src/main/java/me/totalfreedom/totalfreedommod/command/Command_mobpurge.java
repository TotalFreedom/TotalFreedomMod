package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.util.Groups;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

@CommandPermissions(level = Rank.TRIAL_MOD, source = SourceType.BOTH)
@CommandParameters(description = "Purge all mobs in all worlds.", usage = "/<command> [name]", aliases = "mp")
public class Command_mobpurge extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        EntityType type = null;
        String mobName = null;
        if (args.length > 0)
        {
            try
            {
                type = EntityType.valueOf(args[0].toUpperCase());
            }
            catch (Exception e)
            {
                msg(args[0] + " is not a valid mob type.", ChatColor.RED);
                return true;
            }

            if (!Groups.MOB_TYPES.contains(type))
            {
                msg(FUtil.formatName(type.name()) + " is an entity, however it is not a mob.", ChatColor.RED);
                return true;
            }
        }

        if (type != null)
        {
            mobName = FUtil.formatName(type.name());
        }

        FUtil.staffAction(sender.getName(), "Purging all " + (type != null ? mobName + "s" : "mobs"), true);
        int count = plugin.ew.purgeMobs(type);
        msg(count + " " + (type != null ? mobName : "mob") + FUtil.showS(count) + " removed.");
        return true;
    }

    public static List<String> getAllMobNames()
    {
        List<String> names = new ArrayList<>();
        for (EntityType entityType : Groups.MOB_TYPES)
        {
            names.add(entityType.name());
        }
        return names;
    }

    @Override
    public List<String> getTabCompleteOptions(CommandSender sender, Command command, String alias, String[] args)
    {
        if (args.length == 1)
        {
            return getAllMobNames();
        }

        return Collections.emptyList();
    }
}
