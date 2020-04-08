package me.totalfreedom.totalfreedommod.shop;

import lombok.Getter;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum ShopItem
{
    GRAPPLING_HOOK("Grappling Hook", Material.FISHING_ROD, 10, ConfigEntry.SHOP_PRICES_GRAPPLING_HOOK, ChatColor.GREEN, "grapplingHook"),
    LIGHTNING_ROD("Lightning Rod", Material.BLAZE_ROD, 12, ConfigEntry.SHOP_PRICES_LIGHTNING_ROD, ChatColor.LIGHT_PURPLE, "lightningRod"),
    FIRE_BALL("Fire Ball", Material.FIRE_CHARGE, 14, ConfigEntry.SHOP_PRICES_FIRE_BALL, ChatColor.RED, "fireBall"),
    RIDEABLE_PEARL("Rideable Ender Pearl", Material.ENDER_PEARL, 16, ConfigEntry.SHOP_PRICES_RIDEABLE_PEARL, ChatColor.DARK_PURPLE, "rideablePearl");

    /*
        Shop GUI Layout:

        Dimensions: 9x4 = 36
        Key: g = Grappling Hook, l = Lightning Rod, f = Fire Ball, r = Rideable Ender Pearl, $ = Coins}

        ---------
        -g-l-f-r-
        ---------
        --------$
    */

    @Getter
    private final String name;
    @Getter
    private final Material icon;
    @Getter
    private final int slot;
    private final ConfigEntry cost;
    @Getter
    private final ChatColor color;
    @Getter
    private final String dataName;

    ShopItem(String name, Material icon, int slot, ConfigEntry cost, ChatColor color, String dataName)
    {
        this.name = name;
        this.icon = icon;
        this.slot = slot;
        this.cost = cost;
        this.color = color;
        this.dataName = dataName;
    }

    public String getColoredName()
    {
        return color + name;
    }

    public int getCost()
    {
        return cost.getInteger();
    }

    public static ShopItem findItem(String string)
    {
        try
        {
            return ShopItem.valueOf(string.toUpperCase());
        }
        catch (Exception ignored)
        {
        }

        return null;
    }
}