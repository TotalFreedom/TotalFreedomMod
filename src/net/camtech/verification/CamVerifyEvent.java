package net.camtech.verification;

import java.io.BufferedReader;
import java.io.PrintWriter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class CamVerifyEvent extends Event
{
    private static final HandlerList handlers = new HandlerList();
    private PrintWriter out;
    private BufferedReader in;
    private String ip;

    public CamVerifyEvent(PrintWriter out, BufferedReader in, String ip)
    {
        this.out = out;
        this.in = in;
        this.ip = ip;
    }
    
    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
    
    public static HandlerList getHandlerList()
    {
        return handlers;
    }
    
    public PrintWriter getOut()
    {
        return this.out;
    }
    
    public BufferedReader getIn()
    {
        return this.in;
    } 
    
    public String getIp()
    {
        return this.ip;
    }
    
}