package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.HashMap;
import java.util.Map;
import me.StevenLawson.TotalFreedomMod.TFM_UserInfo;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Command_tossmob extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole || sender.isOp())
        {
            if (senderIsConsole)
            {
                sender.sendMessage(TotalFreedomMod.NOT_FROM_CONSOLE);
                return true;
            }

            TFM_UserInfo playerData = TFM_UserInfo.getPlayerData(sender_p, plugin);

            CreatureType creature = CreatureType.PIG;
            if (args.length >= 1)
            {
                if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("end"))
                {
                    playerData.disableMobThrower();
                    sender.sendMessage(ChatColor.GREEN + "MobThrower is disabled.");
                    return true;
                }

                Map<String, CreatureType> mobtypes = new HashMap<String, CreatureType>();
                mobtypes.put("chicken", CreatureType.CHICKEN);
                mobtypes.put("cow", CreatureType.COW);
                mobtypes.put("creeper", CreatureType.CREEPER);
                mobtypes.put("pig", CreatureType.PIG);
                mobtypes.put("sheep", CreatureType.SHEEP);
                mobtypes.put("skeleton", CreatureType.SKELETON);
                mobtypes.put("spider", CreatureType.SPIDER);
                mobtypes.put("zombie", CreatureType.ZOMBIE);
                mobtypes.put("wolf", CreatureType.WOLF);

                CreatureType creature_query = mobtypes.get(args[0].toLowerCase().trim());
                if (creature_query != null)
                {
                    creature = creature_query;
                }
                else
                {
                    sender.sendMessage(args[0] + " is not a supported mob type. Using a pig instead.");
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
            sender.sendMessage(ChatColor.GREEN + "MobThrower is enabled. Creature: " + creature + " - Speed: " + speed + ".");
            sender.sendMessage(ChatColor.GREEN + "Left click while holding a stick to throw mobs!");
            sender.sendMessage(ChatColor.GREEN + "Type '/tossmob off' to disable.  -By Madgeek1450");
            
            sender_p.setItemInHand(new ItemStack(Material.STICK, 1));
        }
        else
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
        }

        return true;
    }
}
