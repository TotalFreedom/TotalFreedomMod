package me.StevenLawson.TotalFreedomMod;

import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TFM_Ban
{
    public static final Pattern IP_BAN_REGEX;
    public static final Pattern UUID_BAN_REGEX;

    static
    {
        // 192.168.1.254:LocalHost:DarthSalamon:0:none
        // 127.0.*.*:TestUserName:BannedByNotch:123567:Test reason
        IP_BAN_REGEX = Pattern.compile(
                "^((?:(?:\\*|(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))\\.){3}(?:\\*|(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)))"
                + ":([\\w\\s]+)"
                + ":([\\w]+)"
                + ":(\\d+)"
                + ":([\\s\\S]+)$");
        // 245d2f30-61fb-4840-9cd3-298b3920f4a4:Cobrex:DarthSalamon:0:Example reason
        UUID_BAN_REGEX = Pattern.compile(
                "^([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})"
                + ":([\\w\\s]+)"
                + ":([\\w]+)"
                + ":(\\d+)"
                + ":([\\s\\S]+)$");
    }
    private boolean complete;
    private String subject; // uuid or IP
    private String lastLoginName;
    private String by;
    private long expireUnix;
    private String reason;

    public TFM_Ban(UUID uuid, String lastLoginName)
    {
        this(uuid, lastLoginName, null, null, null);
    }

    public TFM_Ban(String ip, String lastLoginName)
    {
        this(ip, lastLoginName, null, null, null);
    }

    public TFM_Ban(UUID uuid, String lastLoginName, String sender, Date expire, String reason)
    {
        this(uuid.toString(), lastLoginName, sender, expire, reason);
    }

    public TFM_Ban(String subject, String lastLoginName, String sender, Date expire, String reason)
    {
        this.subject = subject;
        this.lastLoginName = (lastLoginName == null ? "none" : lastLoginName);
        this.by = (sender == null ? "none" : sender);
        this.expireUnix = (expire == null ? 0 : TFM_Util.getUnixTime(expire));
        this.reason = (reason == null ? "none" : reason);
        complete = true;
    }

    public TFM_Ban(String banString, boolean ip)
    {
        final Matcher matcher;

        if (ip)
        {
            matcher = IP_BAN_REGEX.matcher(banString);
        }
        else
        {
            matcher = UUID_BAN_REGEX.matcher(banString);
        }

        complete = false;

        if (!matcher.find())
        {
            return;
        }

        subject = matcher.group(1);
        lastLoginName = matcher.group(2);
        by = matcher.group(3);
        expireUnix = Long.valueOf(matcher.group(4));
        reason = TFM_Util.colorize(matcher.group(5));

        complete = true;
    }

    public String getSubject()
    {
        return subject;
    }

    public String getLastLoginName()
    {
        return lastLoginName;
    }

    public String getBannedBy()
    {
        return by;
    }

    public long getExpireUnix()
    {
        return expireUnix;
    }

    public String getReason()
    {
        return reason;
    }

    public boolean isExpired()
    {
        return expireUnix != 0 && expireUnix < TFM_Util.getUnixTime();
    }

    public boolean isComplete()
    {
        return complete;
    }

    // subject:lastLoginName:bannedBy:expireUnix:reason
    @Override
    public String toString()
    {
        return subject + ":" + lastLoginName + ":" + by + ":" + expireUnix + ":" + TFM_Util.decolorize(reason);
    }
}
