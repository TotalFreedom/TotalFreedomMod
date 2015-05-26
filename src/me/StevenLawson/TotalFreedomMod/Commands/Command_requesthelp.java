package me.StevenLawson.TotalFreedomMod.Commands;

import me.StevenLawson.TotalFreedomMod.TFM_PlayerData;
import me.StevenLawson.TotalFreedomMod.TFM_Util;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

@CommandPermissions(level = AdminLevel.SUPER, source = SourceType.BOTH)
@CommandParameters( description = "Request help", usage = "/<command>", aliases = "ineedhelp") {

    @Override
    public boolean run(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        TFM_Util.adminAction(sender.getName() + "Requesting help");
        sender.sendMessage("You have requested help, a staff member will be with your shortly");
        return true;
    }
    
}
