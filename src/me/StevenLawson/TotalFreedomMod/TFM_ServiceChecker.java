package me.StevenLawson.TotalFreedomMod;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class TFM_ServiceChecker
{
    private static final Map<String, String[]> SERVICE_MAP = new HashMap<String, String[]>();
    public static String check_url = "http://xpaw.ru/mcstatus/status.json";
    public static String version = "1.0";
    public static String last_updated = ""; // On xpaw.ru

    static
    {
        // <"up", "down", "problem">, <"Online", "Quite Slow", "Error 505", etc>, <String (Uptime percentage)>
        SERVICE_MAP.put("website", new String[]
                {
                    "up", "Online", "100.00"
                });
        SERVICE_MAP.put("session", new String[]
                {
                    "up", "Online", "100.00"
                });
        SERVICE_MAP.put("login", new String[]
                {
                    "up", "Online", "100.00"
                });
        SERVICE_MAP.put("account", new String[]
                {
                    "up", "Online", "100.00"
                });
        SERVICE_MAP.put("skins", new String[]
                {
                    "up", "Online", "100.00"
                });
        SERVICE_MAP.put("realms", new String[]
                {
                    "up", "Online", "100.00"
                });
    }
    public static Runnable checker = new Runnable()
    {
        @Override
        public void run()
        {
            runCheck();
        }
    };

    public static void runCheck()
    {
        TotalFreedomMod.server.getScheduler().runTaskAsynchronously(TotalFreedomMod.plugin, new Runnable()
        {
            @Override
            public void run()
            {
                try
                {

                    /* // Nubcakes be 403'ing us >;o
                     BufferedReader in = new BufferedReader(new InputStreamReader(new URL(check_url).openStream()));
                     JSONObject service_json = (JSONObject) JSONValue.parse(in.readLine());
                     in.close();
                     */

                    // Well, lets bypass that! >:D
                    HttpURLConnection connection = (HttpURLConnection) new URL(check_url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0");
                    connection.setRequestProperty("Host", "xpaw.ru");
                    connection.setRequestProperty("Accept", "*/*");
                    connection.setUseCaches(false);
                    connection.setDoInput(true);
                    connection.setDoOutput(false);
                    InputStream is = connection.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(is));
                    JSONObject service_json = (JSONObject) JSONValue.parse(in.readLine());
                    in.close();
                    connection.disconnect();



                    version = String.valueOf(service_json.get("v"));
                    last_updated = (String) service_json.get("last_updated");

                    JSONObject services = (JSONObject) service_json.get("report");
                    for (String service : SERVICE_MAP.keySet())
                    {
                        JSONObject service_info = (JSONObject) services.get(service);
                        SERVICE_MAP.put(service, new String[]
                                {
                                    (String) service_info.get("status"),
                                    (String) service_info.get("title"),
                                    (String) service_info.get("uptime")
                                });
                    }
                }
                catch (Exception ex)
                {
                    TFM_Log.severe(ex);
                }
            }
        });
    }

    public static String getFormattedStatus(String service_name)
    {
        String[] service = SERVICE_MAP.get(service_name);
        String status = ("up".equals(service[0]) ? ChatColor.GREEN
                : ("down".equals(service[0]) ? ChatColor.RED : ChatColor.GOLD)).toString();

        status += service[1] + ChatColor.GRAY + " (";

        status += (Float.parseFloat(service[2]) >= 96.0 ? ChatColor.GREEN
                : (Float.parseFloat(service[2]) > 90.0 ? ChatColor.GOLD : ChatColor.RED));

        status += service[2] + "%" + ChatColor.GRAY + ")";

        return ChatColor.GRAY + WordUtils.capitalize(service_name) + ChatColor.WHITE + ": " + status;
    }

    public static List<String> getAllStatuses()
    {
        List<String> statuses = new ArrayList<String>();
        for (String status : SERVICE_MAP.keySet())
        {
            statuses.add(getFormattedStatus(status));
        }
        return statuses;
    }
}

/* // Mojang status
 public static final Map<String, String> SERVICE_MAP = new HashMap<String, String>();

 static
 {
 SERVICE_MAP.put("minecraft.net", "Minecraft.net");
 SERVICE_MAP.put("login.minecraft.net", "Minecraft Logins");
 SERVICE_MAP.put("session.minecraft.net", "Minecraft Multiplayer Sessions");
 SERVICE_MAP.put("account.mojang.com", "Mojang Accounts Website");
 SERVICE_MAP.put("auth.mojang.com", "Mojang Accounts Login");
 SERVICE_MAP.put("skins.minecraft.net", "Minecraft Skins");
 }
 server.getScheduler().runTaskAsynchronously(plugin, new Runnable()
 {
 @SuppressWarnings("unchecked")
 @Override
 public void run()
 {
 try
 {
 URL mojang_status = new URL("http://status.mojang.com/check");
 BufferedReader in = new BufferedReader(new InputStreamReader(mojang_status.openStream()));
 JSONArray status_json = (JSONArray) JSONValue.parse(in.readLine());
 in.close();

 Map<String, Boolean> service_status = new HashMap<String, Boolean>();

 Iterator status_it = status_json.iterator();
 while (status_it.hasNext())
 {
 JSONObject service = (JSONObject) status_it.next();
 Iterator service_it = service.entrySet().iterator();
 while (service_it.hasNext())
 {
 Entry<String, String> pair = (Entry<String, String>) service_it.next();
 service_status.put(pair.getKey(), (pair.getValue().equals("green") ? Boolean.TRUE : Boolean.FALSE));
 }
 }

 List<String> status_output = new ArrayList<String>();

 Iterator<Entry<String, Boolean>> output_it = service_status.entrySet().iterator();
 while (output_it.hasNext())
 {
 Entry<String, Boolean> pair = output_it.next();
 String service_name = pair.getKey();
 boolean service_online = pair.getValue().booleanValue();

 if (SERVICE_MAP.containsKey(service_name))
 {
 service_name = SERVICE_MAP.get(service_name);
 }

 status_output.add(String.format("%s is %s", service_name, (service_online ? ChatColor.GREEN + "ONLINE" + ChatColor.GRAY : ChatColor.RED + "OFFLINE" + ChatColor.GRAY)));
 }

 playerMsg(String.format("Mojang Service Status: %s.", StringUtils.join(status_output, ", ")), ChatColor.GRAY);
 }
 catch (Exception ex)
 {
 TFM_Log.severe(ex);
 }
 }
 });
 */
