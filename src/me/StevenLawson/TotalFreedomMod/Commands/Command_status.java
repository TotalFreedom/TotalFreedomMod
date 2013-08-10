package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

        return true;
    }
}
