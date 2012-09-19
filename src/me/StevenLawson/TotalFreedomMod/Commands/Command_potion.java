package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionType;

public class Command_potion extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (senderIsConsole)
        {
            sender.sendMessage(TotalFreedomMod.NOT_FROM_CONSOLE);
            return true;
        }

        if (!sender.isOp())
        {
            sender.sendMessage(TotalFreedomMod.MSG_NO_PERMS);
            return true;
        }

        if (args.length < 1)
        {
            return false;
        }

        if (args[0].equalsIgnoreCase("list"))
        {
            List potionTypeNames = new ArrayList<String>();
            for (PotionType potion_type : PotionType.values())
            {
                potionTypeNames.add(potion_type.name());
            }
            sender.sendMessage(ChatColor.AQUA + "Potion types: " + StringUtils.join(potionTypeNames, ", "));
        }

        return true;
    }
}
