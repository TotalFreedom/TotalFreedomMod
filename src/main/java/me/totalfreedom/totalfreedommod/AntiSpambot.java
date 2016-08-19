package me.totalfreedom.totalfreedommod;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public class AntiSpambot extends FreedomService
{

    public static final long IP_THROTTLE_SECS_NORMAL = 30L;
    public static final long IP_THROTTLE_SECS_ATTACK = 60L;

    // True if the server is currently rate-limiting potential spambot usernames/IPs due to a possible
    // spambot attack. This is automatically reset to false after a short period.
    private final AtomicBoolean attackDetected = new AtomicBoolean(false);

    // Async!!
    private final Map<String, Long> ipLogins = new HashMap<>();

    public AntiSpambot(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
    }

    @Override
    protected void onStop()
    {
        ipLogins.clear();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void throttle(AsyncPlayerPreLoginEvent event)
    {
        final String ip = event.getAddress().getHostAddress().trim();
        final long time = FUtil.getUnixTime();

        boolean attack = attackDetected.get();
        final long rate = attack ? IP_THROTTLE_SECS_ATTACK : IP_THROTTLE_SECS_NORMAL;

        // Cleanup
        if (!attack && ipLogins.size() > 20)
        {
            for (Entry<String, Long> entry : ipLogins.entrySet())
            {
                long diff = time - entry.getValue();

                if (diff > rate)
                {
                    ipLogins.remove(entry.getKey());
                }
            }
        }

        // New IP
        if (!ipLogins.containsKey(ip))
        {
            ipLogins.put(ip, time);
            return;
        }

        long diff = time - ipLogins.get(ip);

        if (diff <= rate)
        {
            event.disallow(Result.KICK_OTHER, ChatColor.RED + "You must wait " + diff + " more seconds before logging in again.");
        }
        else
        {
            ipLogins.put(ip, time);
        }
    }

    @EventHandler
    public void spambot(AsyncPlayerPreLoginEvent event)
    {
        if (!attackDetected.get())
        {
            return;
        }

        if (isPossibleSpambot(event.getName()))
        {
            event.disallow(Result.KICK_OTHER, ChatColor.RED + "The server is currently rate-limiting potential spambots");
        }
    }

    public static boolean isPossibleSpambot(String username)
    {

        int points = 0;

        int len = username.length();
        if (len > 12)
        {
            points++;
        }

        if (len > 15)
        {
            points++;
        }

        if (len > 19)
        {
            points++;
        }

        if (len == 32 || len == 16)
        {
            points += 2;
        }

        int digits = 0;
        int alpha = 0;
        int upper = 0;

        CharType prevType = null;
        int highCons = 0;
        int currCons = 1;

        for (char c : username.toCharArray())
        {
            CharType type = CharType.from(c);
            if (type == prevType)
            {
                currCons++;
            }
            else
            {
                highCons = currCons > highCons ? currCons : highCons;
                currCons = 1;
            }

            if (type == null)
            {
                continue;
            }

            switch (type)
            {
                case DIGIT:
                    digits++;
                    break;
                case ALPHA_HIGH:
                    alpha++;
                    upper++;
                    break;
                case ALPHA_LOW:
                    alpha++;
                    upper++;
                    break;
            }

            prevType = type;
        }

        if (Math.abs(digits - alpha) <= 2)
        {
            points++;
        }

        if (Math.abs(alpha - upper) <= 2)
        {
            points++;
        }

        if (alpha == 0 || alpha == len || alpha == upper)
        {
            points++;
        }

        if (highCons <= 2 || highCons >= 10)
        {
            points++;
        }

        return points >= 4;
    }

    public static enum CharType
    {

        DIGIT,
        ALPHA_LOW,
        ALPHA_HIGH,
        OTHER;

        public static CharType from(char c)
        {
            if (Character.isDigit(c))
            {
                return DIGIT;
            }
            else if (Character.isAlphabetic(c))
            {
                if (Character.isUpperCase(c))
                {
                    return ALPHA_HIGH;
                }
                else
                {
                    return ALPHA_LOW;
                }
            }
            else
            {
                return OTHER;
            }

        }
    }

}
