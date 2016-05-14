package me.totalfreedom.totalfreedommod;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.command.Command_logs;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class LogViewer extends FreedomService
{

    public LogViewer(TotalFreedomMod plugin)
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
    }

    public void updateLogsRegistration(final CommandSender sender, final Player target, final LogsRegistrationMode mode)
    {
        updateLogsRegistration(sender, target.getName(), target.getAddress().getAddress().getHostAddress().trim(), mode);
    }

    public void updateLogsRegistration(final CommandSender sender, final String targetName, final String targetIP, final LogsRegistrationMode mode)
    {
        final String logsRegisterUrl = ConfigEntry.LOGS_URL.getString();
        final String logsRegisterPassword = ConfigEntry.LOGS_SECRET.getString();

        if (logsRegisterUrl == null || logsRegisterPassword == null || logsRegisterUrl.isEmpty() || logsRegisterPassword.isEmpty())
        {
            return;
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if (sender != null)
                    {
                        sender.sendMessage(ChatColor.YELLOW + "Connecting...");
                    }

                    URL url = new URLBuilder(logsRegisterUrl)
                            .addQueryParameter("mode", mode.toString())
                            .addQueryParameter("password", logsRegisterPassword)
                            .addQueryParameter("name", targetName)
                            .addQueryParameter("ip", targetIP)
                            .getURL();

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setConnectTimeout(1000 * 5);
                    connection.setReadTimeout(1000 * 5);
                    connection.setUseCaches(false);
                    connection.setRequestMethod("HEAD");

                    final int responseCode = connection.getResponseCode();

                    if (sender != null)
                    {
                        if (!plugin.isEnabled())
                        {
                            return;
                        }

                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                if (responseCode == 200)
                                {
                                    sender.sendMessage(ChatColor.GREEN + "Registration " + mode.toString() + "d.");
                                }
                                else
                                {
                                    sender.sendMessage(ChatColor.RED + "Error contacting logs registration server.");
                                }
                            }
                        }.runTask(plugin);
                    }
                }
                catch (Exception ex)
                {
                    FLog.severe(ex);
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void deactivateSuperadmin(Admin superadmin)
    {
        for (String ip : superadmin.getIps())
        {
            updateLogsRegistration(null, superadmin.getName(), ip, LogsRegistrationMode.DELETE);
        }
    }

    public static enum LogsRegistrationMode
    {

        UPDATE("update"), DELETE("delete");
        private final String mode;

        private LogsRegistrationMode(String mode)
        {
            this.mode = mode;
        }

        @Override
        public String toString()
        {
            return mode;
        }
    }

    private static class URLBuilder
    {

        private final String requestPath;
        private final Map<String, String> queryStringMap = new HashMap<>();

        private URLBuilder(String requestPath)
        {
            this.requestPath = requestPath;
        }

        public URLBuilder addQueryParameter(String key, String value)
        {
            queryStringMap.put(key, value);
            return this;
        }

        public URL getURL() throws MalformedURLException
        {
            List<String> pairs = new ArrayList<>();
            Iterator<Map.Entry<String, String>> it = queryStringMap.entrySet().iterator();
            while (it.hasNext())
            {
                Map.Entry<String, String> pair = it.next();
                pairs.add(pair.getKey() + "=" + pair.getValue());
            }

            return new URL(requestPath + "?" + StringUtils.join(pairs, "&"));
        }
    }

}
