package me.StevenLawson.TotalFreedomMod.Commands;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

@CommandPermissions(level = AdminLevel.ALL, source = SourceType.BOTH)
@CommandParameters(description = "Show misc. server info.", usage = "/<command>")
public class Command_status extends TFM_Command
{
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

    @Override
    public boolean run(final CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        playerMsg("For information about TotalFreedomMod, try /tfm", ChatColor.GREEN); // Temporary

        playerMsg("Server is currently running with 'online-mode=" + (server.getOnlineMode() ? "true" : "false") + "'.", ChatColor.YELLOW);
        playerMsg("Loaded worlds:", ChatColor.BLUE);
        int i = 0;
        for (World world : server.getWorlds())
        {
            playerMsg(String.format("World %d: %s - %d players.", i++, world.getName(), world.getPlayers().size()), ChatColor.BLUE);
        }
        
        /* Moved to TFM_ServiceChecker and Command_services
        new BukkitRunnable()
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

                    final List<String> status_output = new ArrayList<String>();

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

                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            playerMsg(String.format("Mojang Service Status: %s.", StringUtils.join(status_output, ", ")), ChatColor.GRAY);
                        }
                    }.runTask(plugin);
                }
                catch (Exception ex)
                {
                    TFM_Log.severe(ex);
                }
            }
        }.runTaskAsynchronously(plugin);
        */

        return true;
    }
}
