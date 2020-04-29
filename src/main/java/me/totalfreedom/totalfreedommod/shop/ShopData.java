package me.totalfreedom.totalfreedommod.shop;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import net.pravian.aero.base.ConfigLoadable;
import net.pravian.aero.base.ConfigSavable;
import net.pravian.aero.base.Validatable;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ShopData implements ConfigLoadable, ConfigSavable, Validatable
{

    private final List<String> ips = Lists.newArrayList();
    @Getter
    @Setter
    private String username;
    private String uuid;
    @Getter
    @Setter
    private int coins;
    private List<String> items = Lists.newArrayList();
    @Getter
    @Setter
    private int totalVotes;

    public ShopData(Player player)
    {
        this(player.getName());
    }

    public ShopData(String name)
    {
        this.username = name;
    }

    @Override
    public void loadFrom(ConfigurationSection cs)
    {
        this.username = cs.getString("username", username);
        this.ips.addAll(cs.getStringList("ips"));
        this.coins = cs.getInt("coins", coins);
        this.items.addAll(cs.getStringList("items"));
        this.totalVotes = cs.getInt("totalVotes");
    }

    @Override
    public void saveTo(ConfigurationSection cs)
    {
        Validate.isTrue(isValid(), "Could not save shop entry: " + username + ". Entry not valid!");
        cs.set("username", username);
        cs.set("ips", ips);
        cs.set("coins", coins);
        cs.set("items", items);
        cs.set("totalVotes", totalVotes);
    }

    public List<String> getItems()
    {
        return Collections.unmodifiableList(items);
    }

    public void giveItem(ShopItem item)
    {
        items.add(item.getDataName());
    }

    public boolean hasItem(ShopItem item)
    {
        if (items.contains(item.getDataName()))
        {
            return true;
        }
        return false;
    }

    public boolean addIp(String ip)
    {
        return !ips.contains(ip) && ips.add(ip);
    }

    public void removeIp(String ip)
    {
        ips.remove(ip);
    }

    public List<String> getsIps()
    {
        return Collections.unmodifiableList(ips);
    }

    public void removeItem(ShopItem item)
    {
        items.remove(item.getDataName());
    }

    @Override
    public boolean isValid()
    {
        return username != null;
    }
}