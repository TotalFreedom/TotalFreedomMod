package me.totalfreedom.totalfreedommod.amp;


public enum AMPEndpoints
{
    LOGIN("/API/Core/Login" , "{username:\"%s\", password:\"%s\", token:\"\", rememberMe:false}"),
    RESTART("/API/Core/Restart", "{SESSIONID:\"%s\"}");

    private final String text;
    private final String parameters;

    AMPEndpoints(String text, String parameters)
    {
        this.text = text;
        this.parameters = parameters;
    }

    @Override
    public String toString()
    {
        return text;
    }
    public String getParameters()
    {
        return parameters;
    }
}
