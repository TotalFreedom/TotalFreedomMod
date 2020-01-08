package me.totalfreedom.totalfreedommod.shop;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.base.ConfigLoadable;
import net.pravian.aero.base.ConfigSavable;
import net.pravian.aero.base.Validatable;
import org.apache.commons.lang3.Validate;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ShopData implements ConfigLoadable, ConfigSavable, Validatable
{

    @Getter
    @Setter
    private String username;
    private final List<String> ips = Lists.newArrayList();
    @Getter
    @Setter
    private int coins;
    private List<String> items = Lists.newArrayList();

    public ShopData(Player player)
    {
        this(player.getName());
    }

    public ShopData(String username)
    {
        this.username = username;
    }

    @Override
    public void loadFrom(ConfigurationSection cs)
    {
        this.username = cs.getString("username", username);
        this.ips.clear();
        this.ips.addAll(cs.getStringList("ips"));
        this.coins = cs.getInt("coins", coins);
        this.items.addAll(cs.getStringList("items"));
    }

    @Override
    public void saveTo(ConfigurationSection cs)
    {
        Validate.isTrue(isValid(), "Could not save shop entry: " + username + ". Entry not valid!");
        cs.set("username", username);
        cs.set("ips", ips);
        cs.set("coins", coins);
        cs.set("items", items);
    }

    public List<String> getIps()
    {
        return Collections.unmodifiableList(ips);
    }

    // IP utils
    public boolean addIp(String ip)
    {
        return ips.contains(ip) ? false : ips.add(ip);
    }

    public boolean removeIp(String ip)
    {
        return ips.remove(ip);
    }

    public List<String> getItems()
    {
        return Collections.unmodifiableList(items);
    }

    public String giveItem(ShopItem item)
    {
        String signature = String.valueOf(item.ordinal());
        for (int i = 0; i < 7; i++)
        {
            signature += FUtil.getRandomCharacter();
        }
        items.add(signature);
        return signature;
    }

    public void giveRawItem(String signature)
    {
        items.add(signature);
    }

    public boolean hasItem(ShopItem item)
    {
        for (String i : items)
        {
            int id = Integer.valueOf(i.substring(0, 1));
            if (item.ordinal() == id)
            {
                return true;
            }
        }
        return false;
    }

    public ItemStack getItem(ShopItem item)
    {
        String signature = "";
        for (String i : items)
        {
            int id = Integer.valueOf(i.substring(0, 1));
            if (item.ordinal() == id)
            {
                signature = i;
            }
        }
        ItemStack stack = new ItemStack(item.getMaterial(), 1);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(item.getColoredName());
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.DARK_GRAY + signature);
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public boolean validate(ItemStack stack, String nameSegment)
    {
        if (!stack.hasItemMeta())
        {
            return false;
        }

        if (!stack.getItemMeta().hasDisplayName())
        {
            return false;
        }

        if (!stack.getItemMeta().getDisplayName().contains(nameSegment))
        {
            return false;
        }

        if (!stack.getItemMeta().hasLore())
        {
            return false;
        }

        boolean loreValid = false;

        for (String i : items)
        {
            if (stack.getItemMeta().getLore().contains(ChatColor.DARK_GRAY + i))
            {
                loreValid = true;
            }
        }

        if (!loreValid)
        {
            return false;
        }

        return true;
    }

    public boolean validate(ItemStack stack, ShopItem item)
    {
        if (!stack.hasItemMeta())
        {
            return false;
        }

        if (!stack.getItemMeta().hasDisplayName())
        {
            return false;
        }

        if (!stack.getItemMeta().getDisplayName().contains(item.getName()))
        {
            return false;
        }

        if (!stack.getItemMeta().hasLore())
        {
            return false;
        }

        boolean loreValid = false;

        for (String i : items)
        {
            if (stack.getItemMeta().getLore().contains(ChatColor.DARK_GRAY + i))
            {
                loreValid = true;
            }
        }

        if (!loreValid)
        {
            return false;
        }

        return true;
    }

    @Override
    public boolean isValid()
    {
        return username != null
                && !ips.isEmpty();
    }
}