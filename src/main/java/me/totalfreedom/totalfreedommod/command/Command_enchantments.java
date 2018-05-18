package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.enchantments.Enchantment;

@CommandPermissions(level = Rank.OP, source = SourceType.BOTH)
@CommandParameters(description = "Lists all possible enchantments.", usage = "/<command>")
public class Command_enchantments extends FreedomCommand
{

    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        String list = "All possible enchantments: ";

        for (Enchantment enchantment : Enchantment.values())
        {
            list += enchantment.getName() + ", ";
        }

        // Remove extra comma at the end of the list
        list = list.substring(0, list.length() - 2);

        msg(list);
        return true;
    }
}
