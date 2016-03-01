package me.totalfreedom.totalfreedommod.commands;

import lombok.Getter;
import me.totalfreedom.totalfreedommod.FreedomService;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.util.FLog;
import net.pravian.aero.command.handler.SimpleCommandHandler;
import org.bukkit.ChatColor;

public class CommandLoader extends FreedomService
{

    @Getter
    private final SimpleCommandHandler<TotalFreedomMod> handler;

    public CommandLoader(TotalFreedomMod plugin)
    {
        super(plugin);

        handler = new SimpleCommandHandler<TotalFreedomMod>(plugin);
    }

    @Override
    protected void onStart()
    {
        handler.clearCommands();
        handler.setExecutorFactory(new FreedomCommandExecutor.FreedomExecutorFactory());
        handler.setCommandClassPrefix("Command_");
        handler.setPermissionMessage(ChatColor.YELLOW + "You do not have permission to use this command.");

        handler.loadFrom(FreedomCommand.class.getPackage());
        handler.registerAll("TotalFreedomMod", true);

        FLog.info("Loaded " + handler.getExecutors().size() + " commands");
    }

    @Override
    protected void onStop()
    {
        handler.clearCommands();
    }

}
