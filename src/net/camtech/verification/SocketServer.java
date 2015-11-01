package net.camtech.verification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

public class SocketServer implements Runnable
{
    public ServerSocket sock;
    private Socket client;

    public SocketServer()
    {
        try
        {
            sock = new ServerSocket(1999);
        }
        catch (IOException ex)
        {
            Logger.getLogger(SocketServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run()
    {
        while (true)
        {
            try
            {
                Socket clientSocket = sock.accept();
                System.out.println(clientSocket.getInetAddress() + " connected!");
                PrintWriter out
                        = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSocket.getInputStream()));
                CamVerifyEvent event = new CamVerifyEvent(out, in, clientSocket.getInetAddress().getHostAddress());
                Bukkit.getServer().getPluginManager().callEvent(event);
                System.out.println(event.getIp() + " has connected to CamVerify.");
                
            }
            catch (IOException e)
            {

            }
        }
    }
    
    
}