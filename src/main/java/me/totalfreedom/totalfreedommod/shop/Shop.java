package me.totalfreedom.totalfreedommod.shop;

import com.google.common.collect.Maps;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import me.rayzr522.jsonmessage.JSONMessage;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.config.YamlConfig;
import net.pravian.aero.util.Ips;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Shop extends FreedomService
{
    @Getter
    public final Map<UUID, ShopData> dataMap = Maps.newHashMap();
    @Getter
    private final File configFolder;
    private BukkitTask reactions;
    public String reactionString = "";
    public Date reactionStartTime;
    public final int coinsPerReactionWin = ConfigEntry.SHOP_REACTIONS_COINS_PER_WIN.getInteger();

    public Shop(TotalFreedomMod plugin)
    {
        super(plugin);

        this.configFolder = new File(plugin.getDataFolder(), "shopdata");
    }

    @Override
    protected void onStart()
    {
        dataMap.clear();
        if (ConfigEntry.SHOP_REACTIONS_ENABLED.getBoolean())
        {
            long interval = ConfigEntry.SHOP_REACTIONS_INTERVAL.getInteger() * 20L;

            reactions = new BukkitRunnable()
            {

                @Override
                public void run()
                {
                    reactionString = FUtil.randomString(ConfigEntry.SHOP_REACTIONS_STRING_LENGTH.getInteger());
                    for (Player player : server.getOnlinePlayers())
                    {
                        String reactionMessage = ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Reaction" + ChatColor.DARK_GRAY + "] "
                                + ChatColor.AQUA + "Hover over this message or click on it and type the "
                                + ChatColor.AQUA + "string to win " + ChatColor.GOLD + plugin.sh.coinsPerReactionWin + ChatColor.AQUA + " coins!";
                        JSONMessage.create(reactionMessage)
                                .tooltip(ChatColor.DARK_AQUA + reactionString)
                                .runCommand("/reactionbar")
                                .send(player);
                        reactionStartTime = new Date();
                    }
                }
            }.runTaskTimer(plugin, interval, interval);
        }
    }

    @Override
    protected void onStop()
    {
        for (ShopData sd : dataMap.values())
        {
            save(sd);
        }

        if (ConfigEntry.SHOP_REACTIONS_ENABLED.getBoolean())
        {
            reactions.cancel();
        }
    }
    
    public void save(ShopData data)
    {
        YamlConfig config = getConfig(data);
        data.saveTo(config);
        config.save();
        dataMap.remove(data.getUUID());
        dataMap.put(data.getUUID(), data);
    }
    
    public String getShopPrefix()
    {
        return FUtil.colorize(ConfigEntry.SHOP_PREFIX.getString());
    }

    public String getShopTitle()
    {
        return FUtil.colorize(ConfigEntry.SHOP_TITLE.getString());
    }

    // May not return null
    public ShopData getData(Player player)
    {
        // Check already loaded
        ShopData data = dataMap.get(player.getUniqueId());
        if (data != null)
        {
            return data;
        }

        // Load data
        data = getData(player.getUniqueId());

        // Create data if nonexistent
        if (data == null)
        {
            FLog.info("Creating new shop data entry for " + player.getName());

            // Create new player
            data = new ShopData(player);
            data.setUsername(player.getName());
            
            // Set defaults
            data.setCoins(0);

            // Store player
            dataMap.put(player.getUniqueId(), data);

            // Save player
            YamlConfig config = getConfig(data);
            data.saveTo(config);
            config.save();
        }

        dataMap.put(player.getUniqueId(), data);

        return data;
    }

    public ShopData getData(String username)
    {
        UUID uuid = FUtil.nameToUUID(username);
        if (uuid != null)
        {
            return getData(uuid);
        }
        return null;
    }

    public ShopData getData(UUID uuid)
    {
        // Check if the player is a known player
        final File configFile = getConfigFile(uuid);
        if (!configFile.exists())
        {
            return null;
        }

        // Load entry
        final ShopData data = new ShopData(uuid);
        data.loadFrom(getConfig(data));
        return data;
    }

    public Inventory generateShopGUI(ShopData shopData)
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
            ItemStack item = shopGUIItem(shopItem, shopData);
            gui.setItem(shopItem.getSlot(), item);
        }
        // Coins
        ItemStack coins = new ItemStack(Material.GOLD_NUGGET);
        ItemMeta meta = coins.getItemMeta();
        meta.setDisplayName(FUtil.colorize("&c&lYou have &e&l" + shopData.getCoins() + "&c&l coins"));
        coins.setItemMeta(meta);
        gui.setItem(35, coins);
        return gui;
    }

    public boolean isRealItem(ShopData data, ShopItem shopItem, ItemStack givenItem, ItemStack realItem)
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

    public ItemStack shopGUIItem(ShopItem item, ShopData data)
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
        if (inventory.getSize() != 36 || !event.getView().getTitle().equals(plugin.sh.getShopTitle()))
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
        ShopData shopData = plugin.sh.getData(player);
        int price = shopItem.getCost();
        int coins = shopData.getCoins();

        if (shopData.hasItem(shopItem) || !plugin.sh.canAfford(price, coins))
        {
            return;
        }

        shopData.giveItem(shopItem);
        shopData.setCoins(coins - price);
        save(shopData);

        player.closeInventory();

        player.sendMessage(plugin.sh.getShopPrefix() + " " + ChatColor.GREEN + "Successfully purchased the \"" + shopItem.getColoredName() + ChatColor.GREEN + "\" for " + ChatColor.GOLD + price + ChatColor.GREEN + "!");

        if (shopItem.equals(ShopItem.GRAPPLING_HOOK))
        {
            player.sendMessage(ChatColor.GREEN + "Run /grapplinghook to get one!");
        }
        else if (shopItem.equals(ShopItem.LIGHTNING_ROD))
        {
            player.sendMessage(ChatColor.GREEN + "Run /lightningrod to get one!");
        }
        else if (shopItem.equals(ShopItem.FIRE_BALL))
        {
            player.sendMessage(ChatColor.GREEN + "Run /fireball to get one!");
        }
        else if (shopItem.equals(ShopItem.RIDEABLE_PEARL))
        {
            player.sendMessage(ChatColor.GREEN + "Run /rideablepearl to get one!");
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        final String ip = Ips.getIp(event.getPlayer());
        dataMap.remove(ip);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerVote(VotifierEvent event)
    {
        Vote vote = event.getVote();
        String name = vote.getUsername();
        int coinsPerVote =  ConfigEntry.SHOP_COINS_PER_VOTE.getInteger();
        Player player = server.getPlayer(name);
        ShopData data = null;
        if (player != null)
        {
            data = plugin.sh.getData(player);
        }
        else
        {
            data = plugin.sh.getData(name);
        }

        if (data != null)
        {
            data.setCoins(data.getCoins() + coinsPerVote);
            data.setTotalVotes(data.getTotalVotes() + 1);
            save(data);
            FUtil.bcastMsg(ChatColor.GREEN + name + ChatColor.AQUA + " has voted for us on " + ChatColor.GREEN + vote.getServiceName() + ChatColor.AQUA + "!");
        }

        if (player != null)
        {
            player.sendMessage(ChatColor.GREEN + "Thank you for voting for us! Here are " + coinsPerVote + " coins!");
        }
    }

    public Collection<ShopData> getLoadedData()
    {
        return dataMap.values();
    }

    protected File getConfigFile(UUID uuid)
    {
        return new File(getConfigFolder(), uuid + ".yml");
    }

    protected YamlConfig getConfig(ShopData data)
    {
        final YamlConfig config = new YamlConfig(plugin, getConfigFile(data.getUUID()), false);
        config.load();
        return config;
    }
}