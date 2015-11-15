package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.Bridge.TFM_DisguiserBridge;
import me.StevenLawson.TotalFreedomMod.TFM_Log;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerRank;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import pgDev.bukkit.DisguiseCraft.DisguiseCraft;
import pgDev.bukkit.DisguiseCraft.api.DisguiseCraftAPI;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters(description = "Lists all disguised players", usage = "/<command> [-a]")
public class Command_ulist extends TFM_Command
{
    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)
    {
        if (args.length == 0)
        {
            TFM_DisguiserBridge.listDisguisedPlayers(sender);
        }
        if (args.length == 1)
        {
            if (args[0].equalsIgnoreCase("-a"))
            {
                double radius = 20.0;

                if (args.length >= 1)
                {
                    try
                    {
                        radius = Math.max(1.0, Math.min(100.0, Double.parseDouble(args[0])));
                    }
                    catch (NumberFormatException ex)
                    {
                    }
                }

                List<String> foundPlayers = new ArrayList<String>();
                List<String> foundDisguisedPlayers = new ArrayList<String>();

                final Vector senderPos = sender_p.getLocation().toVector();
                final List<Player> players = sender_p.getWorld().getPlayers();
                for (final Player player : players)
                {
                    if (player.equals(sender_p))
                    {
                        continue;
                    }

                    final Location targetPos = player.getLocation();
                    final Vector targetPosVec = targetPos.toVector();

                    boolean inRange = false;
                    try
                    {
                        inRange = targetPosVec.distanceSquared(senderPos) < (radius * radius);
                    }
                    catch (IllegalArgumentException ex)
                    {
                    }

                    if (inRange)
                    {
                        foundPlayers.add(player.getName());
                        try
                        {
                            DisguiseCraftAPI api = DisguiseCraft.getAPI();
                            if (api != null)
                            {
                                if (api.isDisguised(player))
                                {
                                    foundDisguisedPlayers.add(TFM_PlayerRank.fromSender(player).getPrefix() + player.getName() + ChatColor.GOLD + "(" + api.getDisguise(player).type.name() + ")" + ChatColor.RESET);
                                }
                            }
                        }
                        catch (Exception ex)
                        {
                            TFM_Log.severe(ex);
                        }

                        if (foundPlayers.isEmpty())
                        {
                            playerMsg("There are no nearby players.");
                            return true;
                        }

                        if (!(foundPlayers.isEmpty()) && foundDisguisedPlayers.isEmpty())
                        {
                            playerMsg("There are no nearby disguised players.");
                            return true;
                        }

                        else
                        {
                            final StringBuilder onlineUsers = new StringBuilder();
                            onlineUsers.append("Nearby Disguised Players: ");
                            onlineUsers.append(StringUtils.join(foundDisguisedPlayers, ChatColor.WHITE + ", "));
                            sender.sendMessage(onlineUsers.toString());
                            return true;
                        }

                    }

                }
            }
        }
        return false;
    }
}
