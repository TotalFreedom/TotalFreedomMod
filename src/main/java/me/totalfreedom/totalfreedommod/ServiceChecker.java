package me.totalfreedom.totalfreedommod;

import com.google.common.collect.Maps;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import lombok.Getter;
import lombok.Setter;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class ServiceChecker extends FreedomService
{

    public static final long SERVICE_CHECKER_RATE = 120L;

    @Getter
    private final Map<String, ServiceStatus> services = Maps.newHashMap();
    //
    private BukkitTask task;
    private URL url = null;
    @Getter
    private String lastCheck = "Never";
    @Getter
    private String version = "Mojang";

    public ServiceChecker(TotalFreedomMod plugin)
    {
        super(plugin);

        services.put("minecraft.net", new ServiceStatus("Minecraft.net"));
        services.put("account.mojang.com", new ServiceStatus("Mojang Account Website"));
        services.put("authserver.mojang.com", new ServiceStatus("Mojang Authentication"));
        services.put("sessionserver.mojang.com", new ServiceStatus("Mojang Multiplayer sessions"));
        services.put("skins.minecraft.net", new ServiceStatus("Minecraft Skins"));
        services.put("auth.mojang.com", new ServiceStatus("Mojang Authentiation (Legacy)"));
        services.put("session.minecraft.net", new ServiceStatus("Minecraft Sessions (Legacy)"));
    }

    @Override
    protected void onStart()
    {
        final String serviceCheckerURL = ConfigEntry.SERVICE_CHECKER_URL.getString();

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
            FLog.severe("Invalid ServiceChecker URL, disabling service checker");
            return;
        }

        task = getUpdateRunnable().runTaskTimerAsynchronously(TotalFreedomMod.plugin, 40L, SERVICE_CHECKER_RATE * 20L);
    }

    @Override
    protected void onStop()
    {
        try
        {
            task.cancel();
        }
        catch (Exception ex)
        {
        }
        finally
        {
            task = null;
        }
    }

    public BukkitRunnable getUpdateRunnable()
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
                    FLog.severe("Error updating mojang services from " + url);
                    FLog.severe(ex);
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
                    lastCheck = FUtil.dateToString(new Date());
                }
            }
        };
    }

    public List<ServiceStatus> getAllStatuses()
    {
        List<ServiceStatus> servicesList = new ArrayList<>();
        for (String key : services.keySet())
        {
            servicesList.add(services.get(key));
        }
        return servicesList;
    }

    public class ServiceStatus
    {

        @Getter
        private final String name;
        @Getter
        @Setter
        private String uptime = "100.0"; // skins.minecraft.net, minecraft.net, etc..
        @Getter
        private ChatColor color = ChatColor.DARK_GREEN;
        @Getter
        @Setter
        private String message = "Online"; // Online, Offline, Quite Slow, 404 Error, 500 Error, etc..

        public ServiceStatus(String name)
        {
            this.name = name;
        }

        public float getUptimeFloat()
        {
            return Float.parseFloat(uptime);
        }

        public ChatColor getUptimeColor()
        {
            return (getUptimeFloat() > 95 ? ChatColor.GREEN : (getUptimeFloat() > 90 ? ChatColor.GOLD : ChatColor.RED));
        }

        public String getFormattedStatus()
        {
            String status = ChatColor.BLUE + "- " + ChatColor.GRAY + name + ChatColor.WHITE + ": " + color + message + ChatColor.WHITE;

            if (!version.contains("Mojang"))
            {
                status += " (" + getUptimeColor() + getUptime() + ChatColor.WHITE + "%)";
            }

            return status;
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
    }

}
