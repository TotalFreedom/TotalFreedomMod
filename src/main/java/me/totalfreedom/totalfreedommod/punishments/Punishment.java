package me.totalfreedom.totalfreedommod.punishments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import net.pravian.aero.base.ConfigLoadable;
import net.pravian.aero.base.ConfigSavable;
import net.pravian.aero.base.Validatable;
import org.bukkit.configuration.ConfigurationSection;

public class Punishment implements ConfigLoadable, ConfigSavable, Validatable
{

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");

    @Getter
    @Setter
    private String username = null;
    @Getter
    private String ip = null;
    @Getter
    @Setter
    private String by = null;
    @Getter
    @Setter
    private PunishmentType type = null;
    @Getter
    @Setter
    private String reason = null;
    @Getter
    @Setter
    private Date issued_on = null;

    public Punishment()
    {
    }

    public Punishment(String username, String ip, String by, PunishmentType type, String reason)
    {
        this.username = username;
        this.ip = ip;
        this.by = by;
        this.type = type;
        this.reason = reason;
        this.issued_on = new Date();
    }

    @Override
    public void loadFrom(ConfigurationSection cs)
    {
        this.username = cs.getString("username", null);
        this.ip = cs.getString("ip", null);
        this.by = cs.getString("by", null);
        this.type = PunishmentType.valueOf(cs.getString("type", null).toUpperCase());
        this.reason = cs.getString("reason", null);
        try
        {
            this.issued_on = DATE_FORMAT.parse(cs.getString("issued_on", null));
        }
        catch (ParseException e)
        {
            this.issued_on = null;
        }
    }


    @Override
    public void saveTo(ConfigurationSection cs)
    {
        cs.set("username", username);
        cs.set("ip", ip);
        cs.set("by", by);
        cs.set("type", type.name().toLowerCase());
        cs.set("reason", reason);
        cs.set("issued_on", DATE_FORMAT.format(issued_on));
    }

    @Override
    public boolean isValid()
    {
        return username != null || ip != null;
    }
}
