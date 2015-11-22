package me.totalfreedom.totalfreedommod.blocking.command;

public enum CommandBlockerAction
{

    BLOCK("b"),
    BLOCK_AND_EJECT("a"),
    BLOCK_UNKNOWN("u");
    private final String token;

    private CommandBlockerAction(String token)
    {
        this.token = token;
    }

    public String getToken()
    {
        return this.token;
    }

    public static CommandBlockerAction fromToken(String token)
    {
        for (CommandBlockerAction action : CommandBlockerAction.values())
        {
            if (action.getToken().equalsIgnoreCase(token))
            {
                return action;
            }
        }
        return null;
    }
}
