package me.totalfreedom.totalfreedommod.command;

import me.totalfreedom.totalfreedommod.util.FUtil;
import me.totalfreedom.totalfreedommod.rank.Rank;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.Arrays;

@CommandPermissions(level = Rank.SUPER_ADMIN, source = SourceType.BOTH)
public class Command_cookie extends FreedomCommand
{
    public static final String COOKIE_LYRICS = "Imagine that you have zero cookies and you divide them evenly among zero friends. How many cookies does each person get? See, it doesn't " +
            "seem to make sense, and Cookie Monster is sad there are no cookies, and you are sad you have no friends.";
    public static final String LORE = "But, you can have a cookie anyways,\nsince you are sad you are have no friends.";

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        final StringBuilder output = new StringBuilder();

        for (final String word : COOKIE_LYRICS.split(" "))
        {
            output.append(FUtil.randomChatColor()).append(word).append(" ");
        }

        final StringBuilder name = new StringBuilder();

        name.append(ChatColor.DARK_RED).append("C")
                .append(ChatColor.GOLD).append("o")
                .append(ChatColor.YELLOW).append("o")
                .append(ChatColor.DARK_GREEN).append("k")
                .append(ChatColor.DARK_BLUE).append("i")
                .append(ChatColor.DARK_PURPLE).append("e");

        final StringBuilder lore = new StringBuilder();

        for (final String word : LORE.split(" "))
        {
            lore.append(FUtil.randomChatColor()).append(word).append(" ");
        }

        final ItemStack heldItem = new ItemStack(Material.COOKIE);
        final ItemMeta heldItemMeta = heldItem.getItemMeta();
        heldItemMeta.setDisplayName(name.toString());
        heldItemMeta.setLore(Arrays.asList(lore.toString().split("\n")));
        heldItem.setItemMeta(heldItemMeta);

        for (final Player player : server.getOnlinePlayers())
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
