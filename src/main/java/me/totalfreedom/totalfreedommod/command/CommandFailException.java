package me.totalfreedom.totalfreedommod.command;

public class CommandFailException extends RuntimeException
{

    private static final long serialVersionUID = -92333791173123L;

    public CommandFailException(String message)
    {
        super(message);
    }

}
