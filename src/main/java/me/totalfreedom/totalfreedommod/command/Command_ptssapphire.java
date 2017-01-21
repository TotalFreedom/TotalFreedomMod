package me.totalfreedom.totalfreedommod.command;
 
import java.util.Random;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
 
@CommandPermissions(level = Rank.CO_FOUNDER, source = SourceType.BOTH)
@CommandParameters(description = "For the sapphire ones.", usage = "/<command>")
public class Command_ptssapphire extends FreedomCommand
{
 
    public static final String MUTTON_LYRICS = "Have a BLUE day and take the SapphireCadbury!";
    private final Random random = new Random();
 
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        final StringBuilder output = new StringBuilder();
 
        final String[] words = MUTTON_LYRICS.split(" ");
        for (final String word : words)
        {
            output.append(ChatColor.COLOR_CHAR).append(Integer.toHexString(1 + random.nextInt(14))).append(word).append(" ");
        }
 
        final ItemStack heldItem = new ItemStack(Material.COOKED_MUTTON);
        final ItemMeta heldItemMeta = heldItem.getItemMeta();
        heldItemMeta.setDisplayName((new StringBuilder()).append(ChatColor.WHITE).append("Take a Cadbury bar!").append(ChatColor.DARK_GRAY).append("A SAPPHIRE ONE DOOD!!!!!!!!").toString());
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