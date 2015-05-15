package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import me.StevenLawson.TotalFreedomMod.TFM_Admin;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_Player;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerList;
import me.StevenLawson.TotalFreedomMod.TFM_UuidManager;
import me.StevenLawson.TotalFreedomMod.TFM_UuidManager.TFM_UuidResolver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Provides uuid tools", usage = "/<command> <purge | recalculate>")
public class Command_uuid extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length != 1)
        {
            return false;
        }

        if ("purge".equals(args[0]))
        {
            playerMsg("Purged " + TFM_UuidManager.purge() + " cached UUIDs.");
            return true;
        }

        if ("recalculate".equals(args[0]))
        {
            playerMsg("Recalculating UUIDs...");

            // Playerlist uuids
            final Set<TFM_Player> players = TFM_PlayerList.getAllPlayers();
            final List<String> names = new ArrayList<String>();

            for (TFM_Player player : players)
            {
                names.add(player.getLastLoginName());
            }

            final Map<String, UUID> playerUuids = new TFM_UuidResolver(names).call();

            int updated = 0;
            for (String name : playerUuids.keySet())
            {
                for (TFM_Player player : players)
                {
                    if (!player.getLastLoginName().equalsIgnoreCase(name))
                    {
                        continue;
                    }

                    if (player.getUniqueId().equals(playerUuids.get(name)))
                    {
                        continue;
                    }

                    TFM_PlayerList.setUniqueId(player, playerUuids.get(name));
                    TFM_UuidManager.rawSetUUID(name, playerUuids.get(name));
                    updated++;
                    break;
                }
            }

            playerMsg("Recalculated " + updated + " player UUIDs");
            names.clear();

            // Adminlist UUIDs
            final Set<TFM_Admin> admins = TFM_AdminList.getAllAdmins();
            for (TFM_Admin admin : admins)
            {
                names.add(admin.getLastLoginName());
            }

            final Map<String, UUID> adminUuids = new TFM_UuidResolver(names).call();

            updated = 0;
            for (String name : adminUuids.keySet())
            {
                for (TFM_Admin admin : admins)
                {
                    if (!admin.getLastLoginName().equalsIgnoreCase(name))
                    {
                        continue;
                    }

                    if (admin.getUniqueId().equals(adminUuids.get(name)))
                    {
                        continue;
                    }

                    TFM_AdminList.setUuid(admin, admin.getUniqueId(), adminUuids.get(name));
                    TFM_UuidManager.rawSetUUID(name, adminUuids.get(name));
                    updated++;
                    break;
                }
            }

            playerMsg("Recalculated " + updated + " admin UUIDs");

            return true;
        }

        return false;
    }
}
