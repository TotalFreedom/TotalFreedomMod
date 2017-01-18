package me.unraveledmc.unraveledmcmod.command;

import java.util.List;
import java.util.ArrayList;
import me.unraveledmc.unraveledmcmod.rank.Rank;
import me.unraveledmc.unraveledmcmod.shop.ShopData;
import me.unraveledmc.unraveledmcmod.config.ConfigEntry;
import me.unraveledmc.unraveledmcmod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

@CommandPermissions(level = Rank.OP, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Open the GUI to set your login message", usage = "/<command>", aliases = "lm")
public class Command_loginmessage extends FreedomCommand
{
    @Override
    public boolean run(CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (!ConfigEntry.SHOP_ENABLED.getBoolean())
        {
            msg("The shop is currently disabled and this command is a shop item", ChatColor.RED);
            return true;
        }
        ShopData sd = plugin.sh.getData(playerSender);
        if (!sd.isCustomLoginMessage())
        {
            msg(plugin.sh.getShopPrefix() + ChatColor.RED + "You have not purchased " + ChatColor.BLUE + "Custom Login Messages" + ChatColor.RED + " yet!");
            return true;
        }
        Inventory i = server.createInventory(null, 9, ChatColor.AQUA + "Login Messages");
        ItemStack removeLoginMessage = new ItemStack(Material.BARRIER);
        ItemMeta meta = removeLoginMessage.getItemMeta();
        meta.setDisplayName(ChatColor.RED + "Remove current login message");
        removeLoginMessage.setItemMeta(meta);
        i.setItem(0, removeLoginMessage);
        ItemStack animeFan = newLoginMessage(new ItemStack(Material.NAME_TAG), "Anime Fan", ChatColor.RED, "an &cAnime Fan", playerSender);
        i.setItem(1, animeFan);
        ItemStack someDamnBot = newLoginMessage(new ItemStack(Material.NAME_TAG), "Some damn bot", ChatColor.GREEN, "&aprobably some damn bot", playerSender);
        i.setItem(2, someDamnBot);
        ItemStack UnraveledMCAddict = newLoginMessage(new ItemStack(Material.NAME_TAG), "UnraveledMC Addict", ChatColor.GOLD, "an &7&lUnraveled&8&lMC &e&lAddict", playerSender);
        i.setItem(3, UnraveledMCAddict);
        ItemStack RWBYFan = newLoginMessage(new ItemStack(Material.NAME_TAG), "RWBY Fan", ChatColor.LIGHT_PURPLE, "a &4R&fW&8B&6Y &dFan", playerSender);
        i.setItem(4, RWBYFan);
        playerSender.openInventory(i);
        return true;
    }

    public ItemStack newLoginMessage(ItemStack is, String name, ChatColor color, String message, Player player)
    {
        ItemMeta m = is.getItemMeta();
        m.setDisplayName(color + name);
        List<String> l = new ArrayList();
        l.add(ChatColor.AQUA + player.getName() + " is " + plugin.rm.getDisplay(player).getDeterminer() + " " + plugin.rm.getDisplay(player).getItalicColoredName());
        l.add(ChatColor.AQUA + "and " + FUtil.colorize(message));
        m.setLore(l);
        is.setItemMeta(m);
        return is;
    }
    
    public ItemStack newLoginMessage(Material mat, String name, ChatColor color, String message, Player player)
    {
        return newLoginMessage(new ItemStack(mat), name, color, message, player);
    }
}
