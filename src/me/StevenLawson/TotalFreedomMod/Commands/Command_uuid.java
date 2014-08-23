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
import me.StevenLawson.TotalFreedomMod.TFM_UuidResolver;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandPermissions(level = AdminLevel.SENIOR, source = SourceType.ONLY_CONSOLE)
@CommandParameters(description = "Provides uuid tools", usage = "/<command> recalculate <admin | player>")
public class Command_uuid extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length < 2)
        {
            return false;
        }

        if ("recalculate".equals(args[0]))
        {

            if ("admin".equals(args[1]))
            {
                playerMsg("Recalculating admin UUIDs...");

                final Set<TFM_Admin> admins = TFM_AdminList.getAllAdmins();
                final List<String> names = new ArrayList<String>();

                for (TFM_Admin admin : admins)
                {
                    names.add(admin.getLastLoginName());
                }

                final Map<String, UUID> uuids = new TFM_UuidResolver(names).call();

                int updated = 0;
                for (String name : uuids.keySet())
                {
                    for (TFM_Admin admin : admins)
                    {
                        if (!admin.getLastLoginName().equalsIgnoreCase(name))
                        {
                            continue;
                        }

                        if (admin.getUniqueId().equals(uuids.get(name)))
                        {
                            continue;
                        }

                        TFM_AdminList.setUuid(admin, admin.getUniqueId(), uuids.get(name));
                        updated++;
                        break;
                    }
                }

                playerMsg("Done, recalculated " + updated + " UUIDs");
                return true;
            }

            if ("player".equals(args[1]))
            {
                playerMsg("Recalculating player UUIDs...");

                final Set<TFM_Player> players = TFM_PlayerList.getAllPlayers();

                final List<String> names = new ArrayList<String>();

                for (TFM_Player player : players)
                {
                    names.add(player.getLastLoginName());
                }

                final Map<String, UUID> uuids = new TFM_UuidResolver(names).call();

                int updated = 0;
                for (String name : uuids.keySet())
                {
                    for (TFM_Player player : players)
                    {
                        if (!player.getLastLoginName().equalsIgnoreCase(name))
                        {
                            continue;
                        }

                        if (player.getUniqueId().equals(uuids.get(name)))
                        {
                            continue;
                        }

                        TFM_PlayerList.setUuid(player, player.getUniqueId(), uuids.get(name));
                        updated++;
                        break;
                    }
                }

                playerMsg("Done, recalculated " + updated + " UUIDs");
                return true;

            }

            return false;
        }

        return false;
    }
}
