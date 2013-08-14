package me.StevenLawson.TotalFreedomMod;

import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.ConfigurationSection;

public class TFM_Superadmin
{
    private final String name;
    private final String custom_login_message;
    private final boolean is_senior_admin;
    private final List<String> console_aliases;
    private List<String> ips;
    private Date last_login;
    private boolean is_activated;

    public TFM_Superadmin(String name, List<String> ips, Date last_login, String custom_login_message, boolean is_senior_admin, List<String> console_aliases, boolean is_activated)
    {
        this.name = name.toLowerCase();
        this.ips = ips;
        this.last_login = last_login;
        this.custom_login_message = custom_login_message;
        this.is_senior_admin = is_senior_admin;
        this.console_aliases = console_aliases;
        this.is_activated = is_activated;
    }

    public TFM_Superadmin(String name, ConfigurationSection section)
    {
        this.name = name.toLowerCase();
        this.ips = section.getStringList("ips");
        this.last_login = TFM_Util.stringToDate(section.getString("last_login", TFM_Util.dateToString(new Date(0L))));
        this.custom_login_message = section.getString("custom_login_message", "");
        this.is_senior_admin = section.getBoolean("is_senior_admin", false);
        this.console_aliases = section.getStringList("console_aliases");
        this.is_activated = section.getBoolean("is_activated", true);
    }

    @Override
    public String toString()
    {
        StringBuilder output = new StringBuilder();

        try
        {
            output.append("Name: ").append(this.name).append("\n");
            output.append("- IPs: ").append(StringUtils.join(this.ips, ", ")).append("\n");
            output.append("- Last Login: ").append(TFM_Util.dateToString(this.last_login)).append("\n");
            output.append("- Custom Login Message: ").append(this.custom_login_message).append("\n");
            output.append("- Is Senior Admin: ").append(this.is_senior_admin).append("\n");
            output.append("- Console Aliases: ").append(StringUtils.join(this.console_aliases, ", ")).append("\n");
            output.append("- Is Activated: ").append(this.is_activated);
        }
        catch (Exception ex)
        {
            TFM_Log.severe(ex);
        }

        return output.toString();
    }

    public String getName()
    {
        return name;
    }

    public List<String> getIps()
    {
        return ips;
    }

    public Date getLastLogin()
    {
        return last_login;
    }

    public String getCustomLoginMessage()
    {
        return custom_login_message;
    }

    public boolean isSeniorAdmin()
    {
        return is_senior_admin;
    }

    public List<String> getConsoleAliases()
    {
        return console_aliases;
    }

    public void setIps(List<String> ips)
    {
        this.ips = ips;
    }

    public void setLastLogin(Date last_login)
    {
        this.last_login = last_login;
    }

    public boolean isActivated()
    {
        return is_activated;
    }

    public void setActivated(boolean is_activated)
    {
        this.is_activated = is_activated;
    }
}
