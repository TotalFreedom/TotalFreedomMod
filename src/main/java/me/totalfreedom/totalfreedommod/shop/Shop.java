package me.totalfreedom.totalfreedommod.shop;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.player.PlayerData;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Shop extends FreedomService
{
    private BukkitTask reactions;
    public String reactionString = "";
    public Date reactionStartTime;
    public final int coinsPerReactionWin = ConfigEntry.SHOP_REACTIONS_COINS_PER_WIN.getInteger();
    public final String prefix = ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Reaction" + ChatColor.DARK_GRAY + "] ";
    public BukkitTask countdownTask;
    private BossBar countdownBar = null;

    @Override
    public void onStart()
    {
        if (ConfigEntry.SHOP_REACTIONS_ENABLED.getBoolean())
        {
            startReactionTimer();
        }
    }

    public void startReactionTimer()
    {

        long interval = ConfigEntry.SHOP_REACTIONS_INTERVAL.getInteger() * 20L;

        reactions = new BukkitRunnable()
        {

            @Override
            public void run()
            {
                startReaction();
            }
        }.runTaskLater(plugin, interval);
    }

    public void forceStartReaction()
    {
        reactions.cancel();
        startReaction();
    }

    public void startReaction()
    {
        reactionString = FUtil.randomString(ConfigEntry.SHOP_REACTIONS_STRING_LENGTH.getInteger());

        FUtil.bcastMsg(prefix + ChatColor.AQUA + "Enter the code above to win " + ChatColor.GOLD + coinsPerReactionWin + ChatColor.AQUA + " coins!", false);

        reactionStartTime = new Date();

        countdownBar = server.createBossBar(reactionString, BarColor.GREEN, BarStyle.SOLID);
        for (Player player : server.getOnlinePlayers())
        {
            countdownBar.addPlayer(player);
        }
        countdownBar.setVisible(true);
        countdownTask = new BukkitRunnable()
        {
            double seconds = 30;
            double max = seconds;
            @Override
            public void run()
            {
                if ((seconds -= 1) == 0)
                {
                    endReaction(null);
                }
                else
                {
                    countdownBar.setProgress(seconds / max);
                    if (!countdownBar.getColor().equals(BarColor.YELLOW) && seconds / max <= 0.25)
                    {
                        countdownBar.setColor(BarColor.YELLOW);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    public void endReaction(String winner)
    {
        countdownTask.cancel();
        countdownBar.removeAll();
        countdownBar = null;
        reactionString = "";

        if (winner != null)
        {
            Date currentTime = new Date();
            long seconds = (currentTime.getTime() - reactionStartTime.getTime()) / 1000;
            FUtil.bcastMsg(prefix + ChatColor.GREEN + winner + ChatColor.AQUA + " won in " + seconds + " seconds!", false);
            return;
        }

        FUtil.bcastMsg(prefix + ChatColor.RED + "No one reacted fast enough", false);
        startReactionTimer();
    }

    @Override
    public void onStop()
    {
        if (ConfigEntry.SHOP_REACTIONS_ENABLED.getBoolean())
        {
            reactions.cancel();
        }
    }
    
    public String getShopPrefix()
    {
        return FUtil.colorize(ConfigEntry.SHOP_PREFIX.getString());
    }

    public String getShopTitle()
    {
        return FUtil.colorize(ConfigEntry.SHOP_TITLE.getString());
    }

    public Inventory generateShopGUI(PlayerData playerData)
    {
        Inventory gui = server.createInventory(null, 36, getShopTitle());
        for (int slot = 0; slot < 36; slot++)
        {
            ItemStack blank = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
            ItemMeta meta = blank.getItemMeta();
            meta.setDisplayName(" ");
            blank.setItemMeta(meta);
            gui.setItem(slot, blank);
        }
        for (ShopItem shopItem : ShopItem.values())
        {
            ItemStack item = shopGUIItem(shopItem, playerData);
            gui.setItem(shopItem.getSlot(), item);
        }
        // Coins
        ItemStack coins = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta meta = coins.getItemMeta();
        meta.setDisplayName(FUtil.colorize("&c&lYou have &e&l" + playerData.getCoins() + "&c&l coins"));
        coins.setItemMeta(meta);
        gui.setItem(35, coins);
        return gui;
    }

    public boolean isRealItem(PlayerData data, ShopItem shopItem, ItemStack givenItem, ItemStack realItem)
    {
        if (!data.hasItem(shopItem) || !givenItem.getType().equals(realItem.getType()))
        {
            return false;
        }

        ItemMeta givenMeta = givenItem.getItemMeta();
        ItemMeta realMeta = realItem.getItemMeta();

        if (givenMeta.getDisplayName().equals(realMeta.getDisplayName()) && givenMeta.getLore().equals(realMeta.getLore()))
        {
            return true;
        }

        return false;
    }

    public ItemStack getLightningRod()
    {
        ItemStack itemStack = new ItemStack(Material.BLAZE_ROD);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(FUtil.colorize("&bL&3i&bg&3h&bt&3i&bn&3g &b&bR&3o&bd"));
        itemMeta.setLore(Arrays.asList(ChatColor.AQUA + "Strike others down with the power of lightning.", ChatColor.RED + ChatColor.ITALIC.toString() + "The classic way to exterminate annoyances."));
        itemMeta.addEnchant(Enchantment.CHANNELING, 1, false);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack getGrapplingHook()
    {
        ItemStack itemStack = new ItemStack(Material.FISHING_ROD);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "Grappling Hook");
        itemMeta.setLore(Arrays.asList(ChatColor.GREEN + "be spider-man but ghetto"));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack getFireBall()
    {
        ItemStack itemStack = new ItemStack(Material.FIRE_CHARGE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.RED + "Fire Ball");
        itemMeta.setLore(Arrays.asList(ChatColor.GOLD+ "Yeet this at people"));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack getRideablePearl()
    {
        ItemStack itemStack = new ItemStack(Material.ENDER_PEARL);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.DARK_PURPLE + "Rideable Ender Pearl");
        itemMeta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "What the title says.", "", ChatColor.WHITE + ChatColor.ITALIC.toString() + "TotalFreedom is not responsible for any injuries", ChatColor.WHITE + ChatColor.ITALIC.toString() + "sustained while using this item."));
        itemMeta.addEnchant(Enchantment.BINDING_CURSE, 1, false);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public ItemStack getStackingPotato()
    {
        ItemStack itemStack = new ItemStack(Material.POTATO);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "Stacking Potato");
        itemMeta.setLore(Arrays.asList(ChatColor.GREEN + "Left click to ride a mob, right click to put a mob on your head."));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }


    public boolean canAfford(int price, int coins)
    {
        if (coins >= price)
        {
            return true;
        }
        return false;
    }

    public int amountNeeded(int price, int coins)
    {
        return price - coins;
    }

    public ItemStack shopGUIItem(ShopItem item, PlayerData data)
    {
        ItemStack itemStack = new ItemStack(item.getIcon());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(item.getColoredName());
        int price = item.getCost();
        int coins = data.getCoins();
        Boolean canAfford = canAfford(price, coins);
        List<String> lore = new ArrayList();
        if (!data.hasItem(item))
        {
            lore.add(ChatColor.GOLD + "Price: " + (canAfford ? ChatColor.DARK_GREEN : ChatColor.RED) + price);
            if (!canAfford)
            {
                lore.add(ChatColor.RED + "You can not afford this item!");
                lore.add(ChatColor.RED + "You need " + amountNeeded(price, coins) + " more coins to buy this item.");
            }
        }
        else
        {
            lore.add(ChatColor.RED + "You already purchased this item.");
        }
        itemMeta.setLore(lore);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event)
    {
        if (!(event.getWhoClicked() instanceof Player))
        {
            return;
        }

        Inventory inventory = event.getInventory();
        if (inventory.getSize() != 36 || !event.getView().getTitle().equals(getShopTitle()))
        {
            return;
        }
        event.setCancelled(true);

        ShopItem shopItem = getShopItem(event.getSlot());
        if (shopItem == null)
        {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        PlayerData playerData = plugin.pl.getData(player);
        int price = shopItem.getCost();
        int coins = playerData.getCoins();

        if (playerData.hasItem(shopItem) || !canAfford(price, coins))
        {
            return;
        }

        playerData.giveItem(shopItem);
        playerData.setCoins(coins - price);
        plugin.pl.save(playerData);

        player.closeInventory();

        player.sendMessage(getShopPrefix() + " " + ChatColor.GREEN + "Successfully purchased the \"" + shopItem.getColoredName() + ChatColor.GREEN + "\" for " + ChatColor.GOLD + price + ChatColor.GREEN + "!");

        if (shopItem.getCommand() != null)
        {
            player.sendMessage(ChatColor.GREEN + "Run " + shopItem.getCommand() + " to get one!");
        }

    }

    public ShopItem getShopItem(int slot)
    {
        for (ShopItem shopItem : ShopItem.values())
        {
            if (shopItem.getSlot() == slot)
            {
                return shopItem;
            }
        }
        return null;
    }
}