package me.totalfreedom.totalfreedommod.amp;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import me.totalfreedom.totalfreedommod.TotalFreedomMod;
import me.totalfreedom.totalfreedommod.util.FLog;
import org.bukkit.scheduler.BukkitRunnable;

public class AMPManager
{
    private TotalFreedomMod plugin;
    private String url, username, password;
    private String sessionID;


    public AMPManager(TotalFreedomMod plugin, String url, String username, String password)
    {
        this.plugin = plugin;
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public void connectAsync(final LoginCallback callback)
    {

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                String apiEndpoint = url + AMPEndpoints.LOGIN.toString();
                String body = String.format(AMPEndpoints.LOGIN.getParameters(), username, password);
                try
                {
                    LoginResult resp = new Gson().fromJson(postRequestToEndpoint(apiEndpoint, body), LoginResult.class);
                    if (!resp.getSuccess())
                    {
                        FLog.severe("AMP login unsuccessful. Check if login details are correct.");
                        sessionID = "";
                        callback.loginDone(false);
                        return;
                    }
                    sessionID = resp.getSessionID();
                    callback.loginDone(true);
                }
                catch (IOException ex)
                {
                    FLog.severe("Could not login to AMP. Check if URL is correct. Stacktrace: " + ex.getMessage());
                    sessionID = "";
                    callback.loginDone(false);

                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void restartAsync()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                final String apiEndpoint = url + AMPEndpoints.RESTART.toString();
                final String body = String.format(AMPEndpoints.RESTART.getParameters(), sessionID);
                try
                {
                    String resp = postRequestToEndpoint(apiEndpoint, body);
                    if (resp.contains("Unauthorized Access"))
                    {
                        //try connecting one more time
                        LoginCallback callback = new LoginCallback()
                        {
                            @Override
                            public void loginDone(boolean success)
                            {
                                if (!success)
                                {
                                    FLog.severe("Failed to connect to AMP. Did the panel go down? Were panel user details changed/deleted? Check for more info above. Connection was successful when plugin started, but unsuccessful now." +
                                            " Using server.shutdown() instead.");
                                    plugin.getServer().shutdown();
                                    return;
                                }
                                try
                                {
                                    String response = postRequestToEndpoint(apiEndpoint, body);
                                    if (response.contains("Unauthorized Access"))
                                    {
                                        FLog.severe("Contact a developer. Panel gives Session ID but trying to use it gives a no perms error. The panel user set in config doesn't" +
                                                " have perms to restart server. Using server.shutdown() instead. ");
                                        plugin.getServer().shutdown();

                                    }
                                }
                                catch (IOException e)
                                {
                                    FLog.severe("Could not restart. Using server.shutdown() instead. Stacktrace" + e.getMessage());
                                    plugin.getServer().shutdown();
                                }
                            }
                        };
                        plugin.amp.ampManager.connectAsync(callback);
                    }
                }
                catch (IOException ex)
                {
                    FLog.severe("Could not restart. Using server.shutdown() instead. Stacktrace: " + ex.getMessage());
                    plugin.getServer().shutdown();
                }
            }
        }.runTaskAsynchronously(plugin);
    }


    private String postRequestToEndpoint(String endpoint, String body) throws IOException
    {
        URL url = new URL(endpoint);
        if (endpoint.startsWith("https://"))
        {
            HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(body);
            outputStream.flush();
            outputStream.close();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }
        else
        {
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(body);
            outputStream.flush();
            outputStream.close();
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null)
            {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        }
    }

}