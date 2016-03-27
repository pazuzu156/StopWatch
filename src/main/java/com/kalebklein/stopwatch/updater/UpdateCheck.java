package com.kalebklein.stopwatch.updater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.kalebklein.stopwatch.StopWatchWindow;


public class UpdateCheck implements Runnable
{
    private JFrame context;
    private JFrame contextFrom;

    public UpdateCheck(JFrame context, JFrame contextFrom)
    {
        this.context = context;
        this.contextFrom = contextFrom;
    }

    @Override
    public void run()
    {
        try
        {
            URL url = new URL("http://cdn.kalebklein.com/sw/version.txt");
            InputStream is = url.openStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
            String content = "";
            while((line = reader.readLine()) != null)
            {
                content += line;
            }
            int version_code = Integer.parseInt(content);

            if(StopWatchWindow.VERSION_CODE < version_code)
            {
                int option = JOptionPane.showConfirmDialog(context, "There's a new update out. Would you like to update now?", "Check for Updates", JOptionPane.YES_NO_OPTION);
                if(option == JOptionPane.YES_OPTION)
                {
                    context.dispose();
                    contextFrom.dispose();
                    new UpdateWindow();
                }
            }
            else
            {
                JOptionPane.showMessageDialog(context, "You are currently up to date!", "Check for Updates", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        catch (MalformedURLException e)
        {
            JOptionPane.showMessageDialog(context, "Error Code: SWE1\n\nIf you see this, contact me and let me know with the error code being displayed.", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(context, "Error connecting to server! Please try again later.", "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }
}
