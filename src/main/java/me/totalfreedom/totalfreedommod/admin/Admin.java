package me.totalfreedom.totalfreedommod.admin;

import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import me.totalfreedom.totalfreedommod.rank.Rank;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.base.ConfigLoadable;
import net.pravian.aero.base.ConfigSavable;
import net.pravian.aero.base.Validatable;
import net.pravian.aero.util.Ips;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class Admin implements ConfigLoadable, ConfigSavable, Validatable
{

    @Getter
    private String configKey;
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private boolean active = true;
    @Getter
    @Setter
    private Rank rank = Rank.SUPER_ADMIN;
    @Getter
    private final List<String> ips = Lists.newArrayList();
    @Getter
    @Setter
    private Date lastLogin = new Date();
    @Getter
    @Setter
    private String loginMessage = null;

    public Admin(Player player)
    {
        this.configKey = player.getName().toLowerCase();
        this.name = player.getName();
        this.ips.add(Ips.getIp(player));
    }

    public Admin(String configKey)
    {
        this.configKey = configKey;
    }

    @Override
    public String toString()
    {
        final StringBuilder output = new StringBuilder();

        output.append("Admin: ").append(name).append("\n")
                .append("- IPs: ").append(StringUtils.join(ips, ", ")).append("\n")
                .append("- Last Login: ").append(FUtil.dateToString(lastLogin)).append("\n")
                .append("- Custom Login Message: ").append(loginMessage).append("\n")
                .append("- Rank: ").append(rank.getName()).append("\n")
                .append("- Is Active: ").append(active);

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
        active = cs.getBoolean("active", true);
        rank = Rank.findRank(cs.getString("rank"));
        ips.clear();
        ips.addAll(cs.getStringList("ips"));
        lastLogin = FUtil.stringToDate(cs.getString("last_login"));
        loginMessage = cs.getString("login_message", null);
    }

    @Override
    public void saveTo(ConfigurationSection cs)
    {
        Validate.isTrue(isValid(), "Could not save admin entry: " + name + ". Entry not valid!");
        cs.set("username", name);
        cs.set("active", active);
        cs.set("rank", rank.toString());
        cs.set("ips", Lists.newArrayList(ips));
        cs.set("last_login", FUtil.dateToString(lastLogin));
        cs.set("login_message", loginMessage);
    }

    public boolean isAtLeast(Rank pRank)
    {
        return rank.isAtLeast(pRank);
    }

    public boolean hasLoginMessage()
    {
        return loginMessage != null && !loginMessage.isEmpty();
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

    public void clearIPs()
    {
        ips.clear();
    }

    @Override
    public boolean isValid()
    {
        return configKey != null
                && name != null
                && rank != null
                && !ips.isEmpty()
                && lastLogin != null;
    }
}
