package me.totalfreedom.totalfreedommod.commands;

import lombok.Getter;
import me.totalfreedom.totalfreedommod.util.FLog;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import net.pravian.aero.command.handler.SimpleCommandHandler;
import net.pravian.aero.component.service.AbstractService;

public class CommandLoader extends AbstractService<TotalFreedomMod>
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
        handler.setExecutorFactory(new FreedomCommandExecutor.TFM_CommandExecutorFactory());
        handler.setCommandClassPrefix("Command_");
        handler.loadFrom(FreedomCommand.class.getPackage());
        handler.registerAll();

        FLog.info("Loaded" + handler.getExecutors().size() + " commands");
    }

    @Override
    protected void onStop()
    {
        handler.clearCommands();
    }

}
