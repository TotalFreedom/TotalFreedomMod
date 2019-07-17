package me.totalfreedom.totalfreedommod.admin;

import com.google.common.collect.Lists;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.base.ConfigLoadable;
import net.pravian.aero.base.ConfigSavable;
import net.pravian.aero.base.Validatable;
import org.apache.commons.lang3.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class ActivityLogEntry implements ConfigLoadable, ConfigSavable, Validatable
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
    private List<String> timestamps = Lists.newArrayList();
    @Getter
    @Setter
    private List<String> durations = Lists.newArrayList();

    public static final String FILENAME = "activitylog.yml";

    public ActivityLogEntry(Player player)
    {
        this.configKey = player.getName().toLowerCase();
        this.name = player.getName();
    }

    public ActivityLogEntry(String configKey)
    {
        this.configKey = configKey;
    }

    public void loadFrom(Player player)
    {
        configKey = player.getName().toLowerCase();
        name = player.getName();
    }

    @Override
    public void loadFrom(ConfigurationSection cs)
    {
        name = cs.getString("username", configKey);
        ips.clear();
        ips.addAll(cs.getStringList("ips"));
        timestamps.clear();
        timestamps.addAll(cs.getStringList("timestamps"));
        durations.clear();
        durations.addAll(cs.getStringList("durations"));
    }

    @Override
    public void saveTo(ConfigurationSection cs)
    {
        Validate.isTrue(isValid(), "Could not save activity entry: " + name + ". Entry not valid!");
        cs.set("username", name);
        cs.set("ips", Lists.newArrayList(ips));
        cs.set("timestamps", Lists.newArrayList(timestamps));
        cs.set("durations", Lists.newArrayList(durations));
    }

    public void addLogin()
    {
        Date currentTime = Date.from(Instant.now());
        timestamps.add("Login: " + FUtil.dateToString(currentTime));
    }
    public void addLogout()
    {
        String lastLoginString = timestamps.get(timestamps.size() - 1);
        Date currentTime = Date.from(Instant.now());
        timestamps.add("Logout: " + FUtil.dateToString(currentTime));
        lastLoginString = lastLoginString.replace("Login: ", "");
        Date lastLogin = FUtil.stringToDate(lastLoginString);

        long duration = currentTime.getTime() - lastLogin.getTime();
        long seconds = duration / 1000 % 60;
        long minutes = duration / (60 * 1000) % 60;
        long hours = duration / (60 * 60 * 1000);
        durations.add(hours + " hours, " + minutes + " minutes, and " + seconds + " seconds");
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
                && name != null;
    }
}
