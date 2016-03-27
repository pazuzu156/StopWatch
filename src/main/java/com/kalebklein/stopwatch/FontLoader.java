package com.kalebklein.stopwatch;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.FileInputStream;

public class FontLoader
{
    private Font appFont;
    private boolean fontLoaded = false;

    public FontLoader()
    {
        initFontLoader();
    }

    private void initFontLoader()
    {
        try
        {
            FileInputStream is = new FileInputStream("res/fonts/ubuntu.ttf");
            appFont = Font.createFont(Font.TRUETYPE_FONT, is);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(appFont);
            fontLoaded = true;
        }
        catch (Exception e)
        {
            System.out.println("Error loading fonts! Reverting to defaults.");
        }
    }

    public boolean isFontLoaded()
    {
        return this.fontLoaded;
    }

    public Font getFont()
    {
        return this.appFont;
    }

    public Font applyFont(Font font, int style, int size)
    {
        return new Font(font.getFontName(), style, size);
    }
}
