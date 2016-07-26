package me.unraveledmc.unraveledmcmod.command;

import lombok.Getter;
import me.unraveledmc.unraveledmcmod.FreedomService;
import me.unraveledmc.unraveledmcmod.UnraveledMCMod;
import me.unraveledmc.unraveledmcmod.util.FLog;
import net.pravian.aero.command.handler.SimpleCommandHandler;
import org.bukkit.ChatColor;

public class CommandLoader extends FreedomService
{
    public int totalCommands;

    @Getter
    private final SimpleCommandHandler<UnraveledMCMod> handler;

    public CommandLoader(UnraveledMCMod plugin)
    {
        super(plugin);

        handler = new SimpleCommandHandler<>(plugin);
    }

    @Override
    protected void onStart()
    {
        handler.clearCommands();
        handler.setExecutorFactory(new FreedomCommandExecutor.FreedomExecutorFactory(plugin));
        handler.setCommandClassPrefix("Command_");
        handler.setPermissionMessage(ChatColor.RED + "You do not have permission to use this command.");
        handler.setOnlyConsoleMessage(ChatColor.RED + "This command can only be used from the console.");
        handler.setOnlyPlayerMessage(ChatColor.RED + "This command can only be used by players.");

        handler.loadFrom(FreedomCommand.class.getPackage());
        handler.registerAll("UnraveledMCMod", true);
        totalCommands = handler.getExecutors().size();

        FLog.info("Loaded " + totalCommands + " commands.");
    }

    @Override
    protected void onStop()
    {
        handler.clearCommands();
    }

}
