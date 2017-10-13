

package me.totalfreedom.totalfreedommod.command;

import java.util.Iterator;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.Achievement;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.command.CommandSender;
import java.util.Random;
import me.totalfreedom.totalfreedommod.rank.Rank;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
@CommandParameters(description = "For the people that are still alive.", usage = "/<command>")
public class Command_cake extends FreedomCommand
{
    public static final String CAKE_LYRICS = "But there's no sense crying over every mistake. You just keep on trying till you run out of cake.";
    private final Random random;
    
    public Command_cake() {
        this.random = new Random();
    }
    
    public boolean run(final CommandSender sender, final Player playerSender, final Command cmd, final String commandLabel, final String[] args, final boolean senderIsConsole) {
        final StringBuilder output = new StringBuilder();
        final String[] split;
        final String[] words = split = "But there's no sense crying over every mistake. You just keep on trying till you run out of cake.".split(" ");
        for (final String word : split) {
            output.append(FUtil.rainbowChatColor()).append(word).append(" ");
        }
        final ItemStack heldItem = new ItemStack(Material.CAKE);
        final ItemMeta heldItemMeta = heldItem.getItemMeta();
        heldItemMeta.setDisplayName(ChatColor.WHITE + "The " + ChatColor.DARK_GRAY + "Lie");
        heldItem.setItemMeta(heldItemMeta);
        for (final Player player : this.server.getOnlinePlayers()) {
            final int firstEmpty = player.getInventory().firstEmpty();
            if (firstEmpty >= 0) {
                player.getInventory().setItem(firstEmpty, heldItem);
            }
        }
        FUtil.bcastMsg(output.toString());
        return true;
    }
}
