package me.totalfreedom.totalfreedommod.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import me.totalfreedom.totalfreedommod.FreedomService;

public class CommandLoader extends FreedomService
{
    @Getter
    private final List<FreedomCommand> commands;

    public CommandLoader()
    {
        commands = new ArrayList<>();
    }

    @Override
    public void onStart()
    {
    }

    @Override
    public void onStop()
    {
    }

    public void add(FreedomCommand command)
    {
        commands.add(command);
        command.register();
    }

    public FreedomCommand getByName(String name)
    {
        for (FreedomCommand command : commands)
        {
            if (name.equals(command.getName()))
                return command;
        }
        return null;
    }

    public boolean isAlias(String alias)
    {
        for (FreedomCommand command : commands)
        {
            if (Arrays.asList(command.getAliases().split(",")).contains(alias))
                return true;
        }
        return false;
    }

    public int getCommandAmount()
    {
        return commands.size();
    }
}