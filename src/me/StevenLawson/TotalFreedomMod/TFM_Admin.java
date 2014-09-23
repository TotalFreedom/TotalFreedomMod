package me.StevenLawson.TotalFreedomMod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

public class TFM_Admin
{
    private final UUID uuid;
    private String lastLoginName;
    private final String loginMessage;
    private final boolean isSeniorAdmin;
    private final boolean isTelnetAdmin;
    private final List<String> consoleAliases;
    private final List<String> ips;
    private Date lastLogin;
    private boolean isActivated;

    public TFM_Admin(UUID uuid, String lastLoginName, Date lastLogin, String loginMessage, boolean isSeniorAdmin, boolean isTelnetAdmin, boolean isActivated)
    {
        this.uuid = uuid;
        this.lastLoginName = lastLoginName;
        this.ips = new ArrayList<String>();
        this.lastLogin = lastLogin;
        this.loginMessage = loginMessage;
        this.isSeniorAdmin = isSeniorAdmin;
        this.isTelnetAdmin = isTelnetAdmin;
        this.consoleAliases = new ArrayList<String>();
        this.isActivated = isActivated;
    }

    public TFM_Admin(UUID uuid, ConfigurationSection section)
    {
        this.uuid = uuid;
        this.lastLoginName = section.getString("last_login_name");
        this.ips = section.getStringList("ips");
        this.lastLogin = TFM_Util.stringToDate(section.getString("last_login", TFM_Util.dateToString(new Date(0L))));
        this.loginMessage = section.getString("custom_login_message", "");
        this.isSeniorAdmin = section.getBoolean("is_senior_admin", false);
        this.isTelnetAdmin = section.getBoolean("is_telnet_admin", false);
        this.consoleAliases = section.getStringList("console_aliases");
        this.isActivated = section.getBoolean("is_activated", true);
    }

    @Override
    public String toString()
    {
        final StringBuilder output = new StringBuilder();

        output.append("UUID: ").append(uuid.toString()).append("\n");
        output.append("- Last Login Name: ").append(lastLoginName).append("\n");
        output.append("- IPs: ").append(StringUtils.join(ips, ", ")).append("\n");
        output.append("- Last Login: ").append(TFM_Util.dateToString(lastLogin)).append("\n");
        output.append("- Custom Login Message: ").append(loginMessage).append("\n");
        output.append("- Is Senior Admin: ").append(isSeniorAdmin).append("\n");
        output.append("- Is Telnet Admin: ").append(isTelnetAdmin).append("\n");
        output.append("- Console Aliases: ").append(StringUtils.join(consoleAliases, ", ")).append("\n");
        output.append("- Is Activated: ").append(isActivated);

        return output.toString();
    }

    public UUID getUniqueId()
    {
        return uuid;
    }

    public void setLastLoginName(String lastLoginName)
    {
        this.lastLoginName = lastLoginName;
    }

    public String getLastLoginName()
    {
        return lastLoginName;
    }

    public List<String> getIps()
    {
        return Collections.unmodifiableList(ips);
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

    public Date getLastLogin()
    {
        return lastLogin;
    }

    public String getCustomLoginMessage()
    {
        return loginMessage;
    }

    public boolean isSeniorAdmin()
    {
        return isSeniorAdmin;
    }

    public boolean isTelnetAdmin()
    {
        return isTelnetAdmin;
    }

    public List<String> getConsoleAliases()
    {
        return Collections.unmodifiableList(consoleAliases);
    }

    public void setLastLogin(Date lastLogin)
    {
        this.lastLogin = lastLogin;
    }

    public boolean isActivated()
    {
        return isActivated;
    }

    public void setActivated(boolean isActivated)
    {
        this.isActivated = isActivated;
    }
}
