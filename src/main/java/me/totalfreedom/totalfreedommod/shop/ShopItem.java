package me.totalfreedom.totalfreedommod.shop;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum ShopItem
{
    GRAPPLING_HOOK("Grappling Hook", Material.FISHING_ROD, 100, ChatColor.GREEN),
    THOR_STAR("Thor's Star", Material.NETHER_STAR, 10000, ChatColor.LIGHT_PURPLE);

    @Getter
    private final String name;
    @Getter
    private final Material material;
    @Getter
    private final int cost;
    @Getter
    private final ChatColor color;

    ShopItem(String name, Material material, int cost, ChatColor color)
    {
        this.name = name;
        this.material = material;
        this.cost = cost;
        this.color = color;
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