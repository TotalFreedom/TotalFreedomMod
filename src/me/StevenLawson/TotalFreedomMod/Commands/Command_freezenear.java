package me.StevenLawson.TotalFreedomMod.Commands;

import java.util.ArrayList;
import java.util.List;
import me.StevenLawson.TotalFreedomMod.TFM_AdminList;
import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.ONLY_IN_GAME)
@CommandParameters(description = "Freezes all nearby players.", usage = "/<command> [radius]")
public class Command_freezenear extends TFM_Command {

    @Override
    public boolean run(CommandSender sender, Player sender_p, Command cmd, String commandLabel, String[] args, boolean senderIsConsole) {
        double radius = 20.0;

        if (args.length >= 1) {
            try {
                radius = Math.max(1.0, Math.min(100.0, Double.parseDouble(args[0])));
            } catch (NumberFormatException ex) {
            }
        }

        List<String> frozenPlayers = new ArrayList<String>();

        final Vector senderPos = sender_p.getLocation().toVector();
        final List<Player> players = sender_p.getWorld().getPlayers();
        for (final Player player : players) {
            if (player.equals(sender_p)) {
                continue;
            }
            if (TFM_AdminList.isSuperAdmin(sender_p)) {
                continue;
            }

            final Location targetPos = player.getLocation();
            final Vector targetPosVec = targetPos.toVector();

            boolean inRange = false;
            try {
                inRange = targetPosVec.distanceSquared(senderPos) < (radius * radius);
            } catch (IllegalArgumentException ex) {
            }

            if (inRange) {
                final TFM_PlayerData playerdata = TFM_PlayerData.getPlayerData(player);
                playerdata.setFrozen(true);
                player.sendMessage(ChatColor.RED + "You have been frozen due to rulebreakers. You will be unfrozen shortly.");
            }
        }

        if (frozenPlayers.isEmpty()) {
            playerMsg("There are no nearby players.");
        } else {
            Bukkit.broadcastMessage(ChatColor.AQUA + "Freezing all players within " + radius + " blocks of " + sender.getName());
            playerMsg("Froze " + frozenPlayers.size() + " players: " + StringUtils.join(frozenPlayers, ", "));
        }

        return true;
    }
}
