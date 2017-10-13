package me.totalfreedom.totalfreedommod.blocking.command;

import lombok.Getter;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBlockerEntry
{

    @Getter
    private final CommandBlockerRank rank;
    @Getter
    private final CommandBlockerAction action;
    @Getter
    private final String command;
    @Getter
    private final String subCommand;
    @Getter
    private final String message;
    
    public CommandBlockerEntry(final CommandBlockerRank rank, final CommandBlockerAction action, final String command, final String message) {
        this(rank, action, command, null, message);
    }
    
    public CommandBlockerEntry(final CommandBlockerRank rank, final CommandBlockerAction action, final String command, final String subCommand, final String message) {
        this.rank = rank;
        this.action = action;
        this.command = command;
        this.subCommand = ((subCommand == null) ? null : subCommand.toLowerCase().trim());
        this.message = ((message == null || message.equals("_")) ? "That command is blocked." : message);
    }
    
    public void doActions(final CommandSender sender) {
        if (this.action == CommandBlockerAction.BLOCK_AND_EJECT && sender instanceof Player) {
            TotalFreedomMod.plugin().ae.autoEject((Player)sender, "You used a prohibited command: " + this.command);
            FUtil.bcastMsg(sender.getName() + " was automatically kicked for using harmful commands.", ChatColor.RED);
            return;
        }
        if (this.action == CommandBlockerAction.BLOCK_UNKNOWN) {
            FUtil.playerMsg(sender, "Unknown command. Type \"help\" for help.", ChatColor.RESET);
            return;
        }
        FUtil.playerMsg(sender, FUtil.colorize(this.message));
    }
    
    public CommandBlockerRank getRank() {
        return this.rank;
    }
    
    public CommandBlockerAction getAction() {
        return this.action;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public String getSubCommand() {
        return this.subCommand;
    }
    
    public String getMessage() {
        return this.message;
    }
}
