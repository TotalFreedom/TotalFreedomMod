package me.totalfreedom.totalfreedommod.masterbuilder;

import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.base.ConfigLoadable;
import net.pravian.aero.base.ConfigSavable;
import net.pravian.aero.base.Validatable;
import net.pravian.aero.util.Ips;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class MasterBuilder implements ConfigLoadable, ConfigSavable, Validatable
{

    @Getter
    private String configKey;
    @Getter
    @Setter
    private String name;
    @Getter
    private final List<String> ips = Lists.newArrayList();
    @Getter
    @Setter
    private Date lastLogin = new Date();
    @Getter
    @Setter
    private String discordID = null;
    @Getter
    @Setter
    private String tag = null;

    public static final String CONFIG_FILENAME = "masterbuilders.yml";

    public MasterBuilder(Player player)
    {
        this.configKey = player.getName().toLowerCase();
        this.name = player.getName();
        this.ips.add(Ips.getIp(player));
    }

    public MasterBuilder(String configKey)
    {
        this.configKey = configKey;
    }

    @Override
    public String toString()
    {
        final StringBuilder output = new StringBuilder();

        output.append("MasterBuilder: ").append(name).append("\n")
                .append("- IPs: ").append(StringUtils.join(ips, ", ")).append("\n")
                .append("- Last Login: ").append(FUtil.dateToString(lastLogin)).append("\n")
                .append("- Discord ID: ").append(discordID).append("\n")
                .append("- Tag: ").append(tag).append("\n");

        return output.toString();
    }

    public void loadFrom(Player player)
    {
        configKey = player.getName().toLowerCase();
        name = player.getName();
        ips.clear();
        ips.add(Ips.getIp(player));
    }

    @Override
    public void loadFrom(ConfigurationSection cs)
    {
        name = cs.getString("username", configKey);
        ips.clear();
        ips.addAll(cs.getStringList("ips"));
        lastLogin = FUtil.stringToDate(cs.getString("last_login"));
        discordID = cs.getString("discord_id", null);
        tag = cs.getString("tag", null);
    }

    @Override
    public void saveTo(ConfigurationSection cs)
    {
        Validate.isTrue(isValid(), "Could not save master builder entry: " + name + ". Entry not valid!");
        cs.set("username", name);
        cs.set("ips", Lists.newArrayList(ips));
        cs.set("last_login", FUtil.dateToString(lastLogin));
        cs.set("discord_id", discordID);
        cs.set("tag", tag);
    }

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

    public void clearIPs()
    {
        ips.clear();
    }

    @Override
    public boolean isValid()
    {
        return configKey != null
                && name != null
                && !ips.isEmpty()
                && lastLogin != null;
    }

    public String getConfigKey()
    {
        return this.configKey;
    }

    public String getName()
    {
        return this.name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public List<String> getIps()
    {
        return this.ips;
    }

    public Date getLastLogin()
    {
        return this.lastLogin;
    }

    public void setLastLogin(final Date lastLogin)
    {
        this.lastLogin = lastLogin;
    }
}
