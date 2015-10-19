package me.totalfreedom.totalfreedommod.banning;

import com.google.common.collect.Lists;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FUtil;
import net.pravian.aero.base.ConfigLoadable;
import net.pravian.aero.base.ConfigSavable;
import net.pravian.aero.base.Validatable;
import net.pravian.aero.util.Ips;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class FBan implements ConfigLoadable, ConfigSavable, Validatable
{
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");

    @Getter
    @Setter
    private String username = null;
    @Getter
    private final List<String> ips = Lists.newArrayList();
    @Getter
    @Setter
    private String by = null;
    @Getter
    @Setter
    private String reason = null; // Unformatted, &[0-9,a-f] instead of ChatColor
    @Getter
    @Setter
    private long expiryUnix = -1;

    public FBan()
    {
    }

    public FBan(String username, String ip, String by, Date expire, String reason)
    {
        this(username,
                new String[]
                {
                    ip
                },
                by,
                expire,
                reason);
    }

    public FBan(String username, String[] ips, String by, Date expire, String reason)
    {
        this.username = username;
        if (ips != null)
        {
            this.ips.addAll(Arrays.asList(ips));
        }
        this.by = by;
        this.expiryUnix = FUtil.getUnixTime(expire);
    }

    //
    // For player IP
    public static FBan forPlayerIp(Player player, CommandSender by)
    {
        return forPlayerIp(player, by, null, null);
    }

    public static FBan forPlayerIp(Player player, CommandSender by, Date expiry, String reason)
    {
        return new FBan(null, new String[]
        {
            Ips.getIp(player)
        }, by.getName(), expiry, reason);
    }

    public static FBan forPlayerIp(String ip, CommandSender by, Date expiry, String reason)
    {
        return new FBan(null, ip, by.getName(), expiry, reason);
    }

    //
    // For player name
    public static FBan forPlayerName(Player player, CommandSender by, Date expiry, String reason)
    {
        return forPlayerName(player.getName(), by, expiry, reason);
    }

    public static FBan forPlayerName(String player, CommandSender by, Date expiry, String reason)
    {
        return new FBan(player,
                (String[]) null,
                by.getName(),
                expiry,
                reason);
    }

    //
    // For player
    public static FBan forPlayer(Player player, CommandSender by)
    {
        return forPlayerName(player, by, null, null);
    }

    public static FBan forPlayer(Player player, CommandSender by, Date expiry, String reason)
    {
        return new FBan(player.getName(),
                Ips.getIp(player),
                by.getName(),
                expiry,
                reason);
    }

    public static FBan forPlayerFuzzy(Player player, CommandSender by, Date expiry, String reason)
    {
        return new FBan(player.getName(),
                FUtil.getFuzzyIp(Ips.getIp(player)),
                by.getName(),
                expiry,
                reason);
    }

    public boolean hasUsername()
    {
        return username != null && !username.isEmpty();
    }

    public boolean addIp(String ip)
    {
        return ips.add(ip);
    }

    public boolean removeIp(String ip)
    {
        return ips.remove(ip);
    }

    public boolean hasIps()
    {
        return !ips.isEmpty();
    }

    public boolean hasExpiry()
    {
        return expiryUnix > 0;
    }

    public Date getExpiryDate()
    {
        return FUtil.getUnixDate(expiryUnix);
    }

    public boolean isExpired()
    {
        return hasExpiry() && expiryUnix < FUtil.getUnixTime();
    }

    public String bakeKickMessage()
    {
        final StringBuilder message = new StringBuilder(ChatColor.GOLD + "You");

        message.append(!hasUsername() ? "r IP address is" : " are").append(" temporarily banned from this server.");
        message.append("\nAppeal at ").append(ChatColor.BLUE)
                .append(ConfigEntry.SERVER_BAN_URL.getString());

        if (reason != null)
        {
            message.append("\n").append(ChatColor.RED).append("Reason: ").append(ChatColor.GOLD)
                    .append(ChatColor.translateAlternateColorCodes('&', reason));
        }

        if (by != null)
        {
            message.append("\n").append(ChatColor.RED).append("Banned by: ").append(ChatColor.GOLD)
                    .append(by);
        }

        if (getExpiryUnix() != 0)
        {
            message.append("\n").append(ChatColor.RED).append("Expires: ").append(ChatColor.GOLD)
                    .append(DATE_FORMAT.format(FUtil.getUnixDate(expiryUnix)));
        }

        return message.toString();
    }

    @Override
    public boolean equals(Object object)
    {
        if (object == null)
        {
            return false;
        }

        if (!(object instanceof FBan))
        {
            return false;
        }

        final FBan ban = (FBan) object;
        if (hasIps() != ban.hasIps()
                || hasUsername() != hasUsername())
        {
            return false;
        }

        if (hasIps() && !(getIps().equals(ban.getIps())))
        {
            return false;
        }

        if (hasUsername() && !(getUsername().equalsIgnoreCase(ban.getUsername())))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 79 * hash + (this.username != null ? this.username.toLowerCase().hashCode() : 0);
        hash = 79 * hash + (this.ips != null ? this.ips.hashCode() : 0);
        return hash;
    }

    @Override
    public void loadFrom(ConfigurationSection cs)
    {
        this.username = cs.getString("username", null);
        this.ips.clear();
        this.ips.addAll(cs.getStringList("ips"));
        this.by = cs.getString("by", null);
        this.reason = cs.getString("reason", null);
        this.expiryUnix = cs.getLong("expiry_unix", -1);
    }

    @Override
    public void saveTo(ConfigurationSection cs)
    {
        cs.set("username", username);
        cs.set("ips", ips.isEmpty() ? null : ips);
        cs.set("by", by);
        cs.set("reason", reason);
        cs.set("expiry_unix", expiryUnix > 0 ? expiryUnix : null);
    }

    @Override
    public boolean isValid()
    {
        return username != null || !ips.isEmpty();
    }
}
