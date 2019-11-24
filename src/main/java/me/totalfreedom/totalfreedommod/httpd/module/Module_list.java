package me.totalfreedom.totalfreedommod.httpd.module;

import java.util.Collection;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.admin.Admin;
import me.totalfreedom.totalfreedommod.config.ConfigEntry;
import me.totalfreedom.totalfreedommod.httpd.NanoHTTPD;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Module_list extends HTTPDModule
{

    public Module_list(TotalFreedomMod plugin, NanoHTTPD.HTTPSession session)
    {
        super(plugin, session);
    }

    @Override
    public NanoHTTPD.Response getResponse()
    {
        if (params.get("json") != null && params.get("json").equals("true"))
        {
            final JSONObject responseObject = new JSONObject();

            final JSONArray operators = new JSONArray();
            final JSONArray imposters = new JSONArray();
            final JSONArray masterbuilders = new JSONArray();
            final JSONArray superadmins = new JSONArray();
            final JSONArray telnetadmins = new JSONArray();
            final JSONArray senioradmins = new JSONArray();
            final JSONArray developers = new JSONArray();
            final JSONArray assistant_executives = new JSONArray();
            final JSONArray executives = new JSONArray();
            final JSONArray owners = new JSONArray();

            for (Player player : Bukkit.getOnlinePlayers())
            {

                if (isImposter(player))
                {
                    imposters.add(player.getName());
                }

                if (plugin.mbl.isMasterBuilder(player))
                {
                    masterbuilders.add(player.getName());
                }

                if (FUtil.DEVELOPERS.contains(player.getName()))
                {
                    developers.add(player.getName());
                }

                if (ConfigEntry.SERVER_ASSISTANT_EXECUTIVES.getList().contains(player.getName()) && !FUtil.DEVELOPERS.contains(player.getName()))
                {
                    assistant_executives.add(player.getName());
                }

                if (ConfigEntry.SERVER_EXECUTIVES.getList().contains(player.getName()) && !FUtil.DEVELOPERS.contains(player.getName()))
                {
                    executives.add(player.getName());
                }

                if (ConfigEntry.SERVER_OWNERS.getList().contains(player.getName()))
                {
                    owners.add(player.getName());
                }

                if (!plugin.al.isAdmin(player) && !hasSpecialTitle(player))
                {
                    operators.add(player.getName());
                }

                if (!hasSpecialTitle(player) && plugin.al.isAdmin(player))
                {
                    Admin admin = plugin.al.getAdmin(player);
                    switch (admin.getRank())
                    {
                        case SUPER_ADMIN:
                            superadmins.add(player.getName());
                            break;
                        case TELNET_ADMIN:
                            telnetadmins.add(player.getName());
                            break;
                        case SENIOR_ADMIN:
                            senioradmins.add(player.getName());
                            break;
                    }
                }

            }

            responseObject.put("operators", operators);
            responseObject.put("imposters", imposters);
            responseObject.put("masterbuilders", masterbuilders);
            responseObject.put("superadmins", superadmins);
            responseObject.put("telnetadmins", telnetadmins);
            responseObject.put("senioradmins", senioradmins);
            responseObject.put("developers", developers);
            responseObject.put("assistant_executives", assistant_executives);
            responseObject.put("executives", executives);
            responseObject.put("owners", owners);
            responseObject.put("online", server.getOnlinePlayers().size());
            responseObject.put("max", server.getMaxPlayers());

            final NanoHTTPD.Response response = new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_JSON, responseObject.toString());
            response.addHeader("Access-Control-Allow-Origin", "*");
            return response;
        }
        else
        {
            final StringBuilder body = new StringBuilder();

            final Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

            body.append("<p>There are ").append(onlinePlayers.size()).append("/").append(Bukkit.getMaxPlayers()).append(" players online:</p>\r\n");

            body.append("<ul>\r\n");

            for (Player player : onlinePlayers)
            {
                String tag = plugin.rm.getDisplay(player).getTag();
                body.append("<li>").append(tag).append(player.getName()).append("</li>\r\n");
            }

            body.append("</ul>\r\n");

            final NanoHTTPD.Response response = new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_HTML, body.toString());

            return response;
        }
    }

    public boolean isImposter(Player player)
    {
        if (plugin.al.isAdminImpostor(player) || plugin.pv.isPlayerImpostor(player) || plugin.mbl.isMasterBuilderImpostor(player))
        {
            return true;
        }
        return false;
    }

    public boolean hasSpecialTitle(Player player)
    {
        if (FUtil.DEVELOPERS.contains(player.getName()) || ConfigEntry.SERVER_EXECUTIVES.getList().contains(player.getName()) || ConfigEntry.SERVER_ASSISTANT_EXECUTIVES.getList().contains(player.getName()) || ConfigEntry.SERVER_OWNERS.getList().contains(player.getName()))
        {
            return true;
        }
        return false;
    }

    @Override
    public String getTitle()
    {
        return "Total Freedom - Online Players";
    }
}
