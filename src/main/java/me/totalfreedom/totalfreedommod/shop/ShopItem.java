package me.totalfreedom.totalfreedommod.shop;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum ShopItem
{
    GRAPPLING_HOOK("Grappling Hook", Material.FISHING_ROD, 100, ChatColor.GREEN, true),
    THOR_STAR("Thor's Star", Material.NETHER_STAR, 10000, ChatColor.LIGHT_PURPLE, true),
    ELECTRICAL_DIAMOND_SWORD("Electrical Diamond Sword", Material.DIAMOND_SWORD, 0, ChatColor.YELLOW, false),
    SUPERIOR_SWORD("Superior Sword", Material.GOLDEN_SWORD, 0, ChatColor.GOLD, false);

    @Getter
    private final String name;
    @Getter
    private final Material material;
    @Getter
    private final int cost;
    @Getter
    private final ChatColor color;
    @Getter
    private final boolean purchaseable;

    ShopItem(String name, Material material, int cost, ChatColor color, boolean purchaseable)
    {
        this.name = name;
        this.material = material;
        this.cost = cost;
        this.color = color;
        this.purchaseable = purchaseable;
    }

    public String getColoredName()
    {
        return color + name;
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