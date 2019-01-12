package me.totalfreedom.totalfreedommod.playerverification;

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

public class VPlayer implements ConfigLoadable, ConfigSavable, Validatable
{

    private final List<String> ips = Lists.newArrayList();
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String discordId = null;
    @Getter
    @Setter
    private String forumUsername = null;
    @Getter
    @Setter
    private Boolean enabled = false;
    @Getter
    @Setter
    private String tag = null;
    @Getter
    @Setter
    private boolean clearChatOptOut = false;

    public VPlayer(String name)
    {
        this.name = name;
    }

    public VPlayer(Player player)
    {
        this(player.getName());
    }

    @Override
    public void loadFrom(ConfigurationSection cs)
    {
        name = cs.getString("username", name);
        ips.clear();
        ips.addAll(cs.getStringList("ips"));
        discordId = cs.getString("discordId", null);
        enabled = cs.getBoolean("enabled", false);
        tag = cs.getString("tag", null);
        clearChatOptOut = cs.getBoolean("clearChatOptOut", false);
    }

    @Override
    public void saveTo(ConfigurationSection cs)
    {
        Validate.isTrue(isValid(), "Could not save player verification entry: " + name + ". Entry not valid!");
        cs.set("name", name);
        cs.set("discordId", discordId);
        cs.set("enabled", enabled);
        cs.set("tag", tag);
        cs.set("ips", Lists.newArrayList(ips));
        cs.set("clearChatOptOut", clearChatOptOut);
    }

    public List<String> getIps()
    {
        return Collections.unmodifiableList(ips);
    }

    public boolean addIp(String ip)
    {
        return !ips.contains(ip) && ips.add(ip);
    }

    public void removeIp(String ip)
    {
        ips.remove(ip);
    }

    @Override
    public boolean isValid()
    {
        return name != null && !ips.isEmpty();
    }
}
