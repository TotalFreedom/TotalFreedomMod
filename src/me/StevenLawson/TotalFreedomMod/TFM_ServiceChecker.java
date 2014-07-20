package me.StevenLawson.TotalFreedomMod;

import me.StevenLawson.TotalFreedomMod.Config.TFM_ConfigEntry;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class TFM_ServiceChecker
{
    public static final Map<String, ServiceStatus> services;
    private static URL url;
    private static String lastCheck;
    private static String version;

    private TFM_ServiceChecker()
    {
        throw new AssertionError();
    }

    static
    {
        lastCheck = "Unknown";
        version = "1.0-Mojang";
        services = new HashMap<String, ServiceStatus>();
        services.put("minecraft.net", new ServiceStatus("Minecraft.net"));
        services.put("account.mojang.com", new ServiceStatus("Mojang Account Website"));
        services.put("authserver.mojang.com", new ServiceStatus("Mojang Authentication"));
        services.put("sessionserver.mojang.com", new ServiceStatus("Mojang Multiplayer sessions"));
        services.put("skins.minecraft.net", new ServiceStatus("Minecraft Skins"));
        services.put("auth.mojang.com", new ServiceStatus("Mojang Authentiation (Legacy)"));
        services.put("session.minecraft.net", new ServiceStatus("Minecraft Sessions (Legacy)"));
    }

    public static void start()
    {
        final String serviceCheckerURL = TFM_ConfigEntry.SERVICE_CHECKER_URL.getString();

        if (serviceCheckerURL == null || serviceCheckerURL.isEmpty())
        {
            return;
        }

        try
        {
            url = new URL(serviceCheckerURL);
        }
        catch (MalformedURLException ex)
        {
            TFM_Log.severe("Invalid ServiceChecker URL, disabling service checker");
            return;
        }

        getUpdateRunnable().runTaskTimerAsynchronously(TotalFreedomMod.plugin, 40L, TotalFreedomMod.SERVICE_CHECKER_RATE * 20L);
    }

    public static BukkitRunnable getUpdateRunnable()
    {
        return new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (url == null)
                {
                    return;
                }

                final JSONArray statusJson;
                try
                {
                    final BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    statusJson = (JSONArray) JSONValue.parse(in.readLine());
                    in.close();
                }
                catch (Exception ex)
                {
                    TFM_Log.severe("Error updating mojang services from " + url);
                    TFM_Log.severe(ex);
                    return;
                }

                final Iterator status = statusJson.iterator();
                while (status.hasNext())
                {
                    final Iterator serviceIt = ((JSONObject) status.next()).entrySet().iterator();
                    while (serviceIt.hasNext())
                    {
                        @SuppressWarnings("unchecked")
                        final Entry<String, String> pair = (Entry<String, String>) serviceIt.next();

                        if ("lastcheck".equals(pair.getKey()))
                        {
                            lastCheck = pair.getValue();
                            continue;
                        }

                        if ("version".equals(pair.getKey()))
                        {
                            version = pair.getValue();
                            continue;
                        }

                        final ServiceStatus service = services.get(pair.getKey());
                        if (service == null)
                        {
                            continue;
                        }

                        if (pair.getValue().contains(":"))
                        {
                            String[] statusString = pair.getValue().split(":");
                            service.setColor(statusString[0]);
                            service.setMessage(statusString[1]);
                            service.setUptime(statusString[2]);
                        }
                        else
                        {
                            service.setColor(pair.getValue());
                            service.setMessage(("red".equals(pair.getValue()) ? "Offline" : ("yellow".equals(pair.getValue()) ? "Problem" : "Online")));
                        }
                    }
                }
                if (lastCheck.equals("Unknown"))
                {
                    lastCheck = TFM_Util.dateToString(new Date());
                }
            }
        };
    }

    public static List<ServiceStatus> getAllStatuses()
    {
        List<ServiceStatus> servicesList = new ArrayList<ServiceStatus>();
        for (String key : services.keySet())
        {
            servicesList.add(services.get(key));
        }
        return servicesList;
    }

    public static String getLastCheck()
    {
        return lastCheck;
    }

    public static String getVersion()
    {
        return version;
    }

    public static class ServiceStatus
    {
        private String name;
        private String uptime = "100.0"; // skins.minecraft.net, minecraft.net, etc..
        private ChatColor color = ChatColor.DARK_GREEN;
        private String message = "Online"; // Online, Offline, Quite Slow, 404 Error, 500 Error, etc..

        public ServiceStatus(String name)
        {
            this.name = name;
        }

        public String getName()
        {
            return name;
        }

        public String getUptime()
        {
            return uptime;
        }

        public float getUptimeFloat()
        {
            return Float.parseFloat(uptime);
        }

        public ChatColor getUptimeColor()
        {
            return (getUptimeFloat() > 95 ? ChatColor.GREEN : (getUptimeFloat() > 90 ? ChatColor.GOLD : ChatColor.RED));
        }

        public ChatColor getColor()
        {
            return color;
        }

        public String getMessage()
        {
            return message;
        }

        public String getFormattedStatus()
        {
            String status = ChatColor.BLUE + "- " + ChatColor.GRAY + name + ChatColor.WHITE + ": " + color + message + ChatColor.WHITE;

            if (!TFM_ServiceChecker.version.contains("Mojang"))
            {
                status += " (" + getUptimeColor() + getUptime() + ChatColor.WHITE + "%)";
            }

            return status;
        }

        public void setUptime(String uptime)
        {
            this.uptime = uptime;
        }

        public void setColor(ChatColor color)
        {
            this.color = color;
        }

        public void setColor(String color)
        {
            if ("green".equals(color))
            {
                this.color = ChatColor.DARK_GREEN;
            }
            else if ("yellow".equals(color))
            {
                this.color = ChatColor.YELLOW;
            }
            else
            {
                this.color = ChatColor.RED;
            }
        }

        public void setMessage(String message)
        {
            this.message = message;
        }
    }
}
