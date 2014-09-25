package me.StevenLawson.TotalFreedomMod.Config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.StevenLawson.TotalFreedomMod.TFM_Log;

public class TFM_YamlPreprocessor
{
    private final File file;

    private static final Pattern INCLUDE_LINE = Pattern.compile("^\\s*\\#\\s*\\$include:\\s*(.+)$");

    public TFM_YamlPreprocessor(final File file) throws IOException
    {
        if (file == null)
        {
            throw new NullPointerException();
        }

        if (!file.exists())
        {
            throw new FileNotFoundException();
        }

        if (file.isDirectory())
        {
            throw new IOException();
        }

        this.file = file;
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();

        BufferedReader reader = null;
        try
        {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));

            String line;
            while ((line = reader.readLine()) != null)
            {
                final Matcher includeMatch = INCLUDE_LINE.matcher(line);
                if (includeMatch.find())
                {
                    final String fileName = includeMatch.group(1);
                    try
                    {
                        sb.append(new TFM_YamlPreprocessor(new File(fileName)).toString().trim());
                    }
                    catch (final IOException ex)
                    {
                        TFM_Log.severe(ex);
                    }
                }
                else
                {
                    sb.append(line);
                }

                sb.append('\n');
            }
        }
        catch (final IOException ex)
        {
            TFM_Log.severe(ex);
        }
        finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                }
                catch (final IOException ex)
                {
                    TFM_Log.severe(ex);
                }
            }
        }

        return sb.toString();
    }
}
