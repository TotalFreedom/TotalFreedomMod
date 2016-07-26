package me.unraveledmc.unraveledmcmod.shop;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.pravian.aero.base.ConfigLoadable;
import net.pravian.aero.base.ConfigSavable;
import net.pravian.aero.base.Validatable;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ShopData implements ConfigLoadable, ConfigSavable, Validatable
{

    @Getter
    @Setter
    private String username;
    private final List<String> ips = Lists.newArrayList();
    @Getter
    @Setter
    private int coins;
    @Getter
    @Setter
    private boolean coloredchat;
    @Getter
    @Setter
    private boolean customLoginMessage = false;
    @Getter
    @Setter
    private String loginMessage = "none";

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
        this.coloredchat = cs.getBoolean("coloredchat", coloredchat);
        this.customLoginMessage = cs.getBoolean("customLoginMessage", customLoginMessage);
        this.loginMessage = cs.getString("loginMessage", loginMessage);
    }

    @Override
    public void saveTo(ConfigurationSection cs)
    {
        Validate.isTrue(isValid(), "Could not save shop entry: " + username + ". Entry not valid!");
        cs.set("username", username);
        cs.set("ips", ips);
        cs.set("coins", coins);
        cs.set("coloredchat", coloredchat);
        cs.set("customLoginMessage", customLoginMessage);
        cs.set("loginMessage", loginMessage);
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

    @Override
    public boolean isValid()
    {
        return username != null
                && !ips.isEmpty()
                && !loginMessage.isEmpty();
    }
}
