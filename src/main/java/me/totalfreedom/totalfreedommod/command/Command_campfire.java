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

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "You know the words.", usage = "/<command>")
public class Command_campfire extends FreedomCommand
{

    public static final String CAMPFIRE_LYRICS = "Let's gather round the campfire, and sing our campfire song....";

    @Override
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole)
    {
        final StringBuilder output = new StringBuilder();

        for (final String word : CAMPFIRE_LYRICS.split(" "))
        {
            output.append(FUtil.randomChatColor()).append(word).append(" ");
        }

        final ItemStack heldItem = new ItemStack(Material.CAMPFIRE);
        final ItemMeta heldItemMeta = heldItem.getItemMeta();
        heldItemMeta.setDisplayName(ChatColor.DARK_RED + "The " + ChatColor.DARK_RED + "Campfire");
        heldItem.setItemMeta(heldItemMeta);

        for (final Player player : this.server.getOnlinePlayers())
        {
            final int firstEmpty = player.getInventory().firstEmpty();
            if (firstEmpty >= 0)
            {
                player.getInventory().setItem(firstEmpty, heldItem);
            }
        }

        FUtil.bcastMsg(output.toString());
        return true;
    }
}
