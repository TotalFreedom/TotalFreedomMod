package me.totalfreedom.totalfreedommod.commands;

import me.totalfreedom.totalfreedommod.permission.PlayerRank;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandPermissions(level = PlayerRank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Throw a mob in the direction you are facing when you left click with a stick.",
        usage = "/<command> <mobtype [speed] | off | list>")
public class Command_tossmob extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!ConfigEntry.TOSSMOB_ENABLED.getBoolean())
        {
            playerMsg("Tossmob is currently disabled.");
            return true;
        }

        FPlayer playerData = plugin.pl.getPlayer(sender_p);

        EntityType creature = EntityType.PIG;
        if (args.length >= 1)
        {
            if ("off".equals(args[0]))
            {
                playerData.disableMobThrower();
                playerMsg("MobThrower is disabled.", ChatColor.GREEN);
                return true;
            }

            if (args[0].equalsIgnoreCase("list"))
            {
                playerMsg("Supported mobs: " + StringUtils.join(FUtil.mobtypes.keySet(), ", "), ChatColor.GREEN);
                return true;
            }

            try
            {
                creature = FUtil.getEntityType(args[0]);
            }
            catch (Exception ex)
            {
                playerMsg(args[0] + " is not a supported mob type. Using a pig instead.", ChatColor.RED);
                playerMsg("By the way, you can type /tossmob list to see all possible mobs.", ChatColor.RED);
                creature = EntityType.PIG;
            }
        }

        double speed = 1.0;
        if (args.length >= 2)
        {
            try
            {
                speed = Double.parseDouble(args[1]);
            }
            catch (NumberFormatException nfex)
            {
            }
        }

        if (speed < 1.0)
        {
            speed = 1.0;
        }
        else if (speed > 5.0)
        {
            speed = 5.0;
        }

        playerData.enableMobThrower(creature, speed);
        playerMsg("MobThrower is enabled. Creature: " + creature + " - Speed: " + speed + ".", ChatColor.GREEN);
        playerMsg("Left click while holding a " + Material.BONE.toString() + " to throw mobs!", ChatColor.GREEN);
        playerMsg("Type '/tossmob off' to disable.  -By Madgeek1450", ChatColor.GREEN);

        sender_p.setItemInHand(new ItemStack(Material.BONE, 1));

        return true;
    }
}
