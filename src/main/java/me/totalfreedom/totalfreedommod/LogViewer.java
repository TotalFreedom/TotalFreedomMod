package me.totalfreedom.totalfreedommod;

import java.io.UnsupportedEncodingException;
import java.net.*;
import java.security.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
        updateLogsRegistration(sender, target.getName(), mode);
    }

    public void updateLogsRegistration(final CommandSender sender, final String targetName, final LogsRegistrationMode mode)
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

                    final String key = SecureCodeGenerator.generateCode(20);

                    final URL urlAdd = new URLBuilder(logsRegisterUrl)
                            .addQueryParameter("mode", mode.name())
                            .addQueryParameter("password", logsRegisterPassword)
                            .addQueryParameter("name", targetName)
                            .addQueryParameter("key", key)
                            .getURL();

                    final HttpURLConnection connection = (HttpURLConnection) urlAdd.openConnection();
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
                                    if (mode == LogsRegistrationMode.ADD)
                                    {
                                        String link = null;
                                        try
                                        {
                                            final URL urlVerify = new URLBuilder(logsRegisterUrl)
                                                    .addQueryParameter("mode", LogsRegistrationMode.VERIFY.name())
                                                    .addQueryParameter("name", targetName)
                                                    .addQueryParameter("key", key)
                                                    .getURL();
                                            link = urlVerify.toString();
                                        }
                                        catch (Exception ex)
                                        {
                                            FLog.severe(ex);
                                        }

                                        sender.sendMessage(ChatColor.GREEN + "Open this link to verify your logviewer registration:\n" + ChatColor.DARK_GREEN + link);
                                    }
                                    else
                                    {
                                        sender.sendMessage(ChatColor.GREEN + "Logviewer access revoked successfully.");
                                    }
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

    public static enum LogsRegistrationMode
    {

        ADD, DELETE, VERIFY;
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
                try
                {
                    pairs.add(URLEncoder.encode(pair.getKey(), "UTF-8") + "=" + URLEncoder.encode(pair.getValue(), "UTF-8"));
                }
                catch (UnsupportedEncodingException ex)
                {
                    FLog.severe(ex);
                }
            }

            return new URL(requestPath + "?" + StringUtils.join(pairs, "&"));
        }
    }

    private static class SecureCodeGenerator
    {

        private static final String CHARACTER_SET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        public static String generateCode(final int length)
        {
            SecureRandom random;
            try
            {
                random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            }
            catch (NoSuchAlgorithmException | NoSuchProviderException ex)
            {
                random = new SecureRandom();
                FLog.severe(ex);
            }

            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length; i++)
            {
                sb.append(CHARACTER_SET.charAt(random.nextInt(CHARACTER_SET.length())));
            }
            return sb.toString();
        }
    }
}
