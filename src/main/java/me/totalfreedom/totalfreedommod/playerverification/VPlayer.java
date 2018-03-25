package me.totalfreedom.totalfreedommod.playerverification;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import net.pravian.aero.base.ConfigLoadable;
import net.pravian.aero.base.ConfigSavable;
import net.pravian.aero.base.Validatable;
import net.pravian.aero.util.Ips;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

public class VPlayer implements ConfigLoadable, ConfigSavable, Validatable
{

    private final List<String> ips = Lists.newArrayList();
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String forumUsername = null;
    @Getter
    @Setter
    private String discordID = null;
    @Getter
    @Setter
    private Boolean discordVerificationEnabled = false;
    @Getter
    @Setter
    private Boolean forumVerificationEnabled = false;


    public VPlayer(String username)
    {
        this.name = username;
    }

    public void loadFrom(Player player)
    {
        name = player.getName();
        ips.clear();
        ips.add(Ips.getIp(player));
    }

    @Override
    public void loadFrom(ConfigurationSection cs)
    {
        name = cs.getString("username", null);
        ips.clear();
        ips.addAll(cs.getStringList("ips"));
        forumUsername = cs.getString("forum_username", null);
        discordID = cs.getString("discord_id", null);
        discordVerificationEnabled = cs.getBoolean("discord_verification_enabled", false);
        forumVerificationEnabled = cs.getBoolean("forum_verification_enabled", false);
    }

    @Override
    public void saveTo(ConfigurationSection cs)
    {
        Validate.isTrue(isValid(), "Could not save player veirfication entry: " + name + ". Entry not valid!");
        cs.set("username", name);
        cs.set("forum_username", forumUsername);
        cs.set("discord_id", discordID);
        cs.set("ips", Lists.newArrayList(ips));
        cs.set("discord_verification_enabled", discordVerificationEnabled);
        cs.set("forum_verification_enabled", forumVerificationEnabled);
    }

    // Util IP methods
    public void addIp(String ip)
    {
        if (!ips.contains(ip))
        {
            ips.add(ip);
        }
    }

    public void addIps(List<String> ips)
    {
        for (String ip : ips)
        {
            addIp(ip);
        }
    }


    public void removeIp(String ip)
    {
        if (ips.contains(ip))
        {
            ips.remove(ip);
        }
    }

    public List<String> getIPs()
    {
        return ips;
    }

    public void clearIPs()
    {
        ips.clear();
    }

    public Boolean isDiscordVerificationEnabled()
    {
        return discordVerificationEnabled;
    }

    public Boolean isForumVerificationEnabled()
    {
        return forumVerificationEnabled;
    }

    public void setDiscordVerificationEnabled(boolean enabled)
    {
        this.discordVerificationEnabled = enabled;
    }

    public void setForumVerificationEnabled(boolean enabled)
    {
        this.forumVerificationEnabled = enabled;
    }

    public String getDiscordID()
    {
        return discordID;
    }

    public void setDiscordID(String discordID)
    {
        this.discordID = discordID;
    }

    public String getForumUsername()
    {
        return forumUsername;
    }

    public void setForumUsername(String forumUsername)
    {
        this.forumUsername = forumUsername;
    }


    @Override
    public boolean isValid()
    {
        return name != null
                && !ips.isEmpty();
    }
}