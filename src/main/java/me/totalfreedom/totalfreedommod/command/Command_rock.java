package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@CommandPermissions(level = Rank.MOD, source = SourceType.BOTH)
@CommandParameters(description = "You have thrown a rock, but you have also summoned a meteor!", usage = "/<command>")
public class Command_rock extends FreedomCommand
{

    public static final String ROCK_LYRICS = ChatColor.BLUE + "You have thrown a rock, but you have also summoned a meteor!";

    @Override
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole)
    {
        final ItemStack heldItem = new ItemStack(Material.STONE);
        final ItemMeta heldItemMeta = heldItem.getItemMeta();
        heldItemMeta.setDisplayName(ChatColor.BLUE + "Rock");
        heldItem.setItemMeta(heldItemMeta);

        for (final Player player : this.server.getOnlinePlayers())
        {
            final int firstEmpty = player.getInventory().firstEmpty();
            if (firstEmpty >= 0)
            {
                player.getInventory().setItem(firstEmpty, heldItem);
            }
        }

        FUtil.bcastMsg(ROCK_LYRICS);
        return true;
    }
}
