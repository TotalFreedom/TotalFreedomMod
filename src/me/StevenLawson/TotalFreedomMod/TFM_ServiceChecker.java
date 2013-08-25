package me.StevenLawson.TotalFreedomMod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
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
    public final Map<String, TFM_ServiceChecker_ServiceStatus> services = new HashMap<String, TFM_ServiceChecker_ServiceStatus>();
    public String lastCheck = "Unknown";
    public String version = "1.0-Mojang";

    public TFM_ServiceChecker()
    {
        services.put("minecraft.net", new TFM_ServiceChecker_ServiceStatus("Minecraft.net"));
        services.put("account.mojang.com", new TFM_ServiceChecker_ServiceStatus("Mojang Account Website"));
        services.put("authserver.mojang.com", new TFM_ServiceChecker_ServiceStatus("Mojang Authentication"));
        services.put("skins.minecraft.net", new TFM_ServiceChecker_ServiceStatus("Minecraft Skins"));
        services.put("auth.mojang.com", new TFM_ServiceChecker_ServiceStatus("Mojang Authentiation (Legacy)"));
        services.put("login.minecraft.net", new TFM_ServiceChecker_ServiceStatus("Minecraft Logins (Legacy)"));
        services.put("session.minecraft.net", new TFM_ServiceChecker_ServiceStatus("Minecraft Sessions (Legacy)"));
    }

    @SuppressWarnings("unchecked")
    public BukkitRunnable getUpdateRunnable()
    {
        return new BukkitRunnable()
        {
            @Override
            public void run()
            {
                final String serviceCheckerURL = TFM_ConfigEntry.SERVICE_CHECKER_URL.getString();

                if (serviceCheckerURL == null || serviceCheckerURL.isEmpty())
                {
                    return;
                }

                try
                {
                    URL mojangStatus = new URL(serviceCheckerURL);
                    BufferedReader in = new BufferedReader(new InputStreamReader(mojangStatus.openStream()));
                    JSONArray statusJson = (JSONArray) JSONValue.parse(in.readLine());
                    in.close();

                    TFM_ServiceChecker serviceChecker = TFM_ServiceChecker.getInstance();

                    Iterator status_it = statusJson.iterator();
                    while (status_it.hasNext())
                    {
                        JSONObject service = (JSONObject) status_it.next();
                        Iterator serviceIt = service.entrySet().iterator();
                        while (serviceIt.hasNext())
                        {
                            Entry<String, String> pair = (Entry<String, String>) serviceIt.next();

                            if ("lastcheck".equals(pair.getKey()))
                            {
                                serviceChecker.lastCheck = pair.getValue();
                                continue;
                            }

                            if ("version".equals(pair.getKey()))
                            {
                                serviceChecker.version = pair.getValue();
                                continue;
                            }

                            if (pair.getValue().contains(":"))
                            {
                                String[] statusString = pair.getValue().split(":");
                                TFM_ServiceChecker_ServiceStatus status = serviceChecker.services.get(pair.getKey());
                                status.setColor(statusString[0]);
                                status.setMessage(statusString[1]);
                                status.setUptime(statusString[2]);
                            }
                            else
                            {
                                TFM_ServiceChecker_ServiceStatus status = serviceChecker.services.get(pair.getKey());
                                status.setColor(pair.getValue());
                                status.setMessage(("red".equals(pair.getValue()) ? "Offline" : ("yellow".equals(pair.getValue()) ? "Problem" : "Online")));
                            }
                        }
                    }

                }
                catch (Exception ex)
                {
                    TFM_Log.severe("Error updating mojang services from " + serviceCheckerURL);
                    TFM_Log.severe(ex);
                }
            }
        };
    }

    public List<TFM_ServiceChecker_ServiceStatus> getAllStatuses()
    {
        List<TFM_ServiceChecker_ServiceStatus> ServicesList = new ArrayList<TFM_ServiceChecker_ServiceStatus>();
        for (String key : services.keySet())
        {
            ServicesList.add(services.get(key));
        }
        return ServicesList;
    }

    public static TFM_ServiceChecker getInstance()
    {
        return TFM_ServiceCheckerHolder.INSTANCE;
    }

    private static class TFM_ServiceCheckerHolder
    {
        private static final TFM_ServiceChecker INSTANCE = new TFM_ServiceChecker();
    }

    public class TFM_ServiceChecker_ServiceStatus
    {
        private String name;
        private String uptime = "100.0"; // skins.minecraft.net, minecraft.net, etc..
        private ChatColor color = ChatColor.DARK_GREEN;
        private String message = "Online"; // Online, Offline, Quite Slow, 404 Error, 500 Error, etc..

        public TFM_ServiceChecker_ServiceStatus(String name)
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

            if (!TFM_ServiceChecker.getInstance().version.contains("Mojang"))
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
