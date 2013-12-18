package me.StevenLawson.TotalFreedomMod.HTTPD;

import me.StevenLawson.TotalFreedomMod.TFM_SuperadminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import me.StevenLawson.TotalFreedomMod.TotalFreedomMod;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Module_players extends TFM_HTTPD_Module
{
    public Module_players(NanoHTTPD.HTTPSession session)
    {
        super(session);
    }

    @Override
    public NanoHTTPD.Response getResponse()
    {
        final JSONObject responseObject = new JSONObject();

        final JSONArray players = new JSONArray();
        final JSONArray superadmins = new JSONArray();
        final JSONArray telnetadmins = new JSONArray();
        final JSONArray senioradmins = new JSONArray();
        final JSONArray developers = new JSONArray();

        // All online players
        for (Player player : TotalFreedomMod.server.getOnlinePlayers())
        {
            players.add(player.getName());
        }

        // Super admins (non-telnet and non-senior)
        for (String superadmin : TFM_SuperadminList.getSuperadminNames())
        {
            if (TFM_SuperadminList.getSenioradminNames().contains(superadmin))
            {
                continue;
            }

            if (TFM_SuperadminList.getTelnetadminNames().contains(superadmin))
            {
                continue;
            }

            superadmins.add(getName(superadmin));
        }

        // Telnet admins (non-senior)
        for (String telnetadmin : TFM_SuperadminList.getTelnetadminNames())
        {
            if (TFM_SuperadminList.getSenioradminNames().contains(telnetadmin))
            {
                continue;
            }
            telnetadmins.add(getName(telnetadmin));
        }

        // Senior admins
        for (String senioradmin : TFM_SuperadminList.getSenioradminNames())
        {
            senioradmins.add(getName(senioradmin));
        }

        // Developers
        developers.addAll(TFM_Util.DEVELOPERS);

        responseObject.put("players", players);
        responseObject.put("superadmins", superadmins);
        responseObject.put("telnetadmins", telnetadmins);
        responseObject.put("senioradmins", senioradmins);
        responseObject.put("developers", developers);

        final NanoHTTPD.Response response = new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_JSON, responseObject.toString());
        response.addHeader("Access-Control-Allow-Origin", "*");
        return response;
    }

    private String getName(String caseInsensitiveName)
    {
        final OfflinePlayer player = Bukkit.getOfflinePlayer(caseInsensitiveName);
        if (player == null)
        {
            return caseInsensitiveName;
        }
        else
        {
            return player.getName();
        }
    }
}
