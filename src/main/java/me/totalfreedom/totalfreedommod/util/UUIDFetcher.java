package me.totalfreedom.totalfreedommod.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

// UUIDFetcher retrieves UUIDs from usernames via web requests to Mojang.
public class UUIDFetcher
{

    private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";

    public static UUID fetch(String name)
    {
        try
        {
            Gson gson = new GsonBuilder().create();
            UUID uuid;
            String body = gson.toJson(name);
            URL url = new URL(PROFILE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream stream = connection.getOutputStream();
            stream.write(body.getBytes());
            stream.flush();
            stream.close();
            FetchedUuid[] id = gson.fromJson(
                    new InputStreamReader(connection.getInputStream()),
                    FetchedUuid[].class);

            if (id.length == 0 || id[0].getID() == null)
            {
                return null;
            }

            String idd = id[0].getID();
            uuid = UUID.fromString(idd.substring(0, 8) + "-" + idd.substring(8, 12)
                    + "-" + idd.substring(12, 16) + "-" + idd.substring(16, 20) + "-"
                    + idd.substring(20, 32));
            return uuid;
        }
        catch (IOException ex)
        {
            FLog.severe(ex);
        }
        return null;
    }

    private class FetchedUuid
    {

        private String id;

        public String getID()
        {
            return id;
        }
    }
}