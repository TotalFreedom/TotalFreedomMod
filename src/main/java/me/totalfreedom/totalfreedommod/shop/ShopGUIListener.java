package me.totalfreedom.totalfreedommod.shop;

import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ShopGUIListener extends FreedomService
{
    public ShopGUIListener(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event)
    {
        if (!(event.getWhoClicked() instanceof Player))
        {
            return;
        }
        Inventory i = event.getInventory();
        if (!i.getTitle().equals(plugin.sh.GUIName) && !i.getTitle().equals(ChatColor.AQUA + "Login messages"))
        {
            return;
        }
        event.setCancelled(true);
        Player p = (Player) event.getWhoClicked();
        ShopData sd = plugin.sh.getData(p);
        ItemStack is = event.getCurrentItem();
        String prefix = plugin.sh.getShopPrefix();
        if (i.getTitle().equals(plugin.sh.GUIName))
        {
            int coins = sd.getCoins();
            int coloredChatPrice = ConfigEntry.SHOP_COLORED_CHAT_PRICE.getInteger();
            int customLoginMessagePrice = ConfigEntry.SHOP_LOGIN_MESSAGE_PRICE.getInteger();
            if (is.getType().equals(Material.BOOK_AND_QUILL) && !sd.isColoredchat() && canOfford(coloredChatPrice, coins))
            {
                sd.setCoins(coins - coloredChatPrice);
                sd.setColoredchat(true);
                plugin.sh.save(sd);
                p.sendMessage(prefix + ChatColor.GREEN + "You have successfully bought colored chat!");
                event.setCancelled(true);
                p.closeInventory();
            }
            else if (is.getType().equals(Material.NAME_TAG) && !sd.isCustomLoginMessage() && canOfford(customLoginMessagePrice, coins))
            {
                sd.setCoins(coins - customLoginMessagePrice);
                sd.setCustomLoginMessage(true);
                plugin.sh.save(sd);
                p.sendMessage(prefix + ChatColor.GREEN + "You have successfully bought custom login messages! Use /loginmessage to set one!");
                event.setCancelled(true);
                p.closeInventory();
            }
        }
        else if (i.getTitle().equals(ChatColor.AQUA + "Login messages"))
        {
            if (is.getType().equals(Material.BARRIER))
            {
                sd.setLoginMessage("none");
                plugin.sh.save(sd);
                p.closeInventory();
                p.sendMessage(ChatColor.GREEN + "Successfully removed login message!");
            }
            else if (is.getItemMeta().getDisplayName().equals(ChatColor.RED + "Anime fan"))
            {
                sd.setLoginMessage("&ban &canime fan");
                plugin.sh.save(sd);
                p.closeInventory();
                p.sendMessage(ChatColor.GREEN + "Your login message is now " + createLoginMessage(p, sd.getLoginMessage()));
            }
            else if (is.getItemMeta().getDisplayName().equals(ChatColor.GREEN + "Some damn bot"))
            {
                sd.setLoginMessage("&aprobably some damn bot");
                plugin.sh.save(sd);
                p.closeInventory();
                p.sendMessage(ChatColor.GREEN + "Your login message is now " + createLoginMessage(p, sd.getLoginMessage()));
            }
            else if (is.getItemMeta().getDisplayName().equals(ChatColor.GOLD + "UnraveledMC Addict"))
            {
                sd.setLoginMessage("an &6UnraveledMC Addict");
                plugin.sh.save(sd);
                p.closeInventory();
                p.sendMessage(ChatColor.GREEN + "Your login message is now " + createLoginMessage(p, sd.getLoginMessage()));
            }
            else if (is.getItemMeta().getDisplayName().equals(ChatColor.LIGHT_PURPLE + "RWBY fan"))
            {
                sd.setLoginMessage("a &4R&fW&8B&6Y &dfan");
                plugin.sh.save(sd);
                p.closeInventory();
                p.sendMessage(ChatColor.GREEN + "Your login message is now " + createLoginMessage(p, sd.getLoginMessage()));
            }
        }
    }
    
    public String createLoginMessage(Player player, String msg)
    {
        Rank r = plugin.rm.getRank(player);
        String loginMessage = ChatColor.AQUA + player.getName() + " is " + r.getDeterminer() + " "
                + r.getItalicColoredName() + ChatColor.AQUA + " and " + FUtil.colorize(msg);
        return loginMessage;
    }
    
    public boolean canOfford(int p, int c)
    {
        if (c >= p)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
