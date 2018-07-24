package me.totalfreedom.totalfreedommod.admin;

import com.google.common.collect.Lists;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import me.totalfreedom.totalfreedommod.LogViewer.LogsRegistrationMode;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
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
    @Getter
    @Setter
    private String discordID = null;
    @Getter
    @Setter
    private String tag = null;
    @Getter
    @Setter
    private Boolean commandSpy = false;
    @Getter
    @Setter
    private Boolean potionSpy = false;
    @Getter
    @Setter
    private String acFormat = null;
    @Getter
    @Setter
    private Boolean oldTags = false;
    @Getter
    @Setter
    private Boolean logStick = false;

    public static final String CONFIG_FILENAME = "admins.yml";

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
                .append("- Is Active: ").append(active).append("\n")
                .append("- Discord ID: ").append(discordID).append("\n")
                .append("- Tag: ").append(tag).append("\n")
                .append("- Potion Spy: ").append(potionSpy).append("\n")
                .append("- Admin Chat Format: ").append(acFormat).append("\n")
                .append("- Old Tags: ").append(oldTags).append("\n")
                .append("- Log Stick: ").append(logStick);

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
        discordID = cs.getString("discord_id", null);
        tag = cs.getString("tag", null);
        commandSpy = cs.getBoolean("command_spy", false);
        potionSpy = cs.getBoolean("potion_spy", false);
        acFormat = cs.getString("acformat", null);
        oldTags = cs.getBoolean("oldtags", false);
        logStick = cs.getBoolean("logstick", false);

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
        cs.set("discord_id", discordID);
        cs.set("tag", tag);
        cs.set("command_spy", commandSpy);
        cs.set("potion_spy", potionSpy);
        cs.set("acformat", acFormat);
        cs.set("oldtags", oldTags);
        cs.set("logstick", logStick);
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

    public String getLoginMessage()
    {
        return this.loginMessage;
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

    public void setActive(boolean active)
    {
        this.active = active;

        final TotalFreedomMod plugin = TotalFreedomMod.plugin();

        if (!active)
        {
            if (getRank().isAtLeast(Rank.TELNET_ADMIN))
            {
                if (plugin.btb != null)
                {
                    plugin.btb.killTelnetSessions(getName());
                }
            }

            plugin.lv.updateLogsRegistration(null, getName(), LogsRegistrationMode.DELETE);
        }
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

    public boolean isActive()
    {
        return this.active;
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

    public Rank getRank()
    {
        return this.rank;
    }

    public void setRank(final Rank rank)
    {
        this.rank = rank;
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

    public void setLoginMessage(final String loginMessage)
    {
        this.loginMessage = loginMessage;
    }
}
