package me.StevenLawson.TotalFreedomMod.HTTPD;

import java.util.Collection;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Module_list extends TFM_HTTPD_Module
{
    public Module_list(NanoHTTPD.HTTPSession session)
    {
        super(session);
    }

    @Override
    public String getBody()
    {
        final StringBuilder body = new StringBuilder();

        final Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();

        body.append("<p>There are ").append(onlinePlayers.size()).append("/").append(Bukkit.getMaxPlayers()).append(" players online:</p>\r\n");

        body.append("<ul>\r\n");

        for (Player player : onlinePlayers)
        {
            String prefix = "";
            if (TFM_AdminList.isSuperAdmin(player))
            {
                if (TFM_AdminList.isSeniorAdmin(player))
                {
                    prefix = "[SrA]";
                }
                else
                {
                    prefix = "[SA]";
                }

                if (TFM_Util.DEVELOPERS.contains(player.getName()))
                {
                    prefix = "[Dev]";
                }

                if (player.getName().equals("markbyron"))
                {
                    prefix = "[Owner]";
                }
            }
            else
            {
                if (player.isOp())
                {
                    prefix = "[OP]";
                }
            }

            body.append("<li>").append(prefix).append(player.getName()).append("</li>\r\n");
        }

        body.append("</ul>\r\n");

        return body.toString();
    }

    @Override
    public String getTitle()
    {
        return "Total Freedom - Online Users";
    }
}
