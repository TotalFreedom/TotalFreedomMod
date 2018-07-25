package me.totalfreedom.totalfreedommod.playerverification;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.pravian.aero.base.ConfigLoadable;
import net.pravian.aero.base.ConfigSavable;
import net.pravian.aero.base.Validatable;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

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
    private Boolean discordEnabled = false;
    @Getter
    @Setter
    private Boolean forumEnabled = false;

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
        forumUsername = cs.getString("forumUsername", null);
        discordEnabled = cs.getBoolean("discordEnabled", false);
        forumEnabled = cs.getBoolean("forumEnabled", false);
    }

    @Override
    public void saveTo(ConfigurationSection cs)
    {
        Validate.isTrue(isValid(), "Could not save player verification entry: " + name + ". Entry not valid!");
        cs.set("name", name);
        cs.set("discordId", discordId);
        cs.set("forumUsername", forumUsername);
        cs.set("discordEnabled", discordEnabled);
        cs.set("forumEnabled", forumEnabled);
        cs.set("ips", Lists.newArrayList(ips));
    }

    public List<String> getIps()
    {
        return Collections.unmodifiableList(ips);
    }

    public boolean addIp(String ip)
    {
        return ips.contains(ip) ? false : ips.add(ip);
    }

    public boolean removeIp(String ip)
    {
        return ips.remove(ip);
    }

    public void setDiscordId(final String discordId)
    {
        this.discordId = discordId;
    }

    @Override
    public boolean isValid()
    {
        return name != null
                && !ips.isEmpty();
    }
}
