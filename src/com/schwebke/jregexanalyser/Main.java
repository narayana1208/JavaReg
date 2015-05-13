/*
 * Copyright (c) 2011, Kai Schwebke. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Kai Schwebke (kai@schwebke.com)
 * or visit www.schwebke.com if you need additional information or have any
 * questions.
 */

/*
 * $Id: Main.java 756 2012-08-25 01:54:13Z kai $
 */

package com.schwebke.jregexanalyser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

/**
 * jRegExAnalyser
 *
 * Main Class
 *
 * Handle preferences, start GUI.
 * 
 */
public class Main {

    public static final String version = "jRegExAnalyser 1.4.0 (2012-08-25, r"+Version.revision+")";
    public static final String onlineVersionURL = "http://jregexanalyser.schwebke.com/jregexanalyser/version.txt";

    public static final String about = version+"\n\n"+
            "Copyright (C) 2010, 2011, 2012 Kai G. Schwebke\n\n" +
            "This software is licensed under the\n"+
            "GNU General Public License version 2\n"+
            "with Classpath exception.\n\n"+
            "For more information, please visit\n"+
            "jregexanalyser.schwebke.com.";

    public static final long sessionStart = System.currentTimeMillis();

    // preferences
    public static final Properties prefs;

    // persistent storage -- one of these two should be set to non-null by loadPrefs
    private static javax.jnlp.FileContents prefFC;
    private static File prefFile;
    static {
        prefs = new Properties();
    }
    
    /**
     * return true, if file operation from loadPrefs() results in an exception other than IO,
     * which most likely indicates that we run in a sandbox.
     */
    public static boolean isWebStart() 
    {
        return webStart;
    }
    private static boolean webStart = false;

    /**
     * load properties from permanent storage
     */
    private static void loadPrefs() 
    {
        prefFC = null;
        prefFile = null;
        try 
        {
            // load from home
            prefFile = new File(System.getProperty("user.home")+"/.jregexanalyser");
            InputStream pIn = new FileInputStream(prefFile);
            prefs.loadFromXML(pIn);
        } 
        catch (IOException ioe) 
        {
            // plain io exception, file may be missing and will be created later
        }
        catch (Exception e) 
        {
            // other exception, we may run in a sandbox
            // use fall back prefs (if not started as local app)
            webStart = true;
            try 
            {
                javax.jnlp.BasicService bs =
                        (javax.jnlp.BasicService)javax.jnlp.ServiceManager.lookup("javax.jnlp.BasicService");
                javax.jnlp.PersistenceService ps =
                        (javax.jnlp.PersistenceService)javax.jnlp.ServiceManager.lookup("javax.jnlp.PersistenceService");
                URL prefsUrl = new URL(bs.getCodeBase(), "jregexanalyser");
                try 
                {
                    ps.create(prefsUrl, 1024*256);
                }
                catch (IOException ioe) 
                {
                    // ignore (storage may already exist)
                }
                prefFC = ps.get(prefsUrl);
                prefs.loadFromXML(prefFC.getInputStream());

            } 
            catch (Exception e2) 
            {
                // ignore
            }
        }
    }

    /**
     * write properties to permanent storage
     */
    private static void persistPrefs() 
    {
        try 
        {
            OutputStream pOut = null;
            if (prefFile != null) 
            {
                pOut = new FileOutputStream(prefFile);
            } 
            else if (prefFC != null) 
            {
                pOut = prefFC.getOutputStream(true);
            }
            prefs.storeToXML(pOut, version);
        }
        catch (Exception e) 
        {
            // ignore
        }
    }

    public static final String defaultLookAndFeel = "Nimbus";
    public static String getLookAndFeel() {
        return prefs.getProperty("LookAndFeel", Main.defaultLookAndFeel);
    }
    public static void setLookAndFeel(String laf) {
        prefs.setProperty("LookAndFeel", laf);
        persistPrefs();
    }

    public static String getCharset() {
        return prefs.getProperty("Charset", Charset.defaultCharset().name());
    }
    public static void setCharset(String charset) {
        prefs.setProperty("Charset", charset);
        persistPrefs();
    }

    public static int getHistorySize() 
    {
        return Integer.parseInt(prefs.getProperty("HistorySize", "5"));
    }
    public static void setHistorySize(int value) 
    {
        prefs.setProperty("HistorySize", String.format("%d", value));
        persistPrefs();
    }

    public static String getHistoryEntry(int i) 
    {
        return prefs.getProperty(String.format("HistoryEntry.%d", i));
    }

    public static void setHistoryEntry(int i, String s) 
    {
        prefs.setProperty(String.format("HistoryEntry.%d", i), s);
        persistPrefs();
    }

    public static String getRecentTestText(int i) 
    {
        return prefs.getProperty(String.format("RecentTestText.%d", i));
    }

    public static void setRecentTestText(int i, String s) 
    {
        prefs.setProperty(String.format("RecentTestText.%d", i), s);
        persistPrefs();
    }

    public static boolean getPermanentMatching() 
    {
        return Boolean.parseBoolean(prefs.getProperty("PermanentMatching", "True"));
    }

    public static void setPermanentMatching(boolean value) 
    {
        prefs.setProperty("PermanentMatching", value?"True":"False");
        persistPrefs();
    }

    public static boolean getCheckVersion() 
    {
        return Boolean.parseBoolean(prefs.getProperty("CheckVersion", "True"));
    }

    public static void setCheckVersion(boolean value) 
    {
        prefs.setProperty("CheckVersion", value?"True":"False");
        persistPrefs();
    }
    
    public static boolean getWrapText() 
    {
        return Boolean.parseBoolean(prefs.getProperty("WrapText", "False"));
    }

    public static void setWrapText(boolean value) 
    {
        prefs.setProperty("WrapText", value?"True":"False");
        persistPrefs();
    }

    public static File getLastDir() 
    {
        File lastDir = new File(prefs.getProperty("LastDir", "."));
        if (!lastDir.isDirectory()) 
        {
            lastDir = new File(".");
        }
        if (!lastDir.isDirectory()) 
        {
            return null;
        }
        return lastDir;
    }
    public static void setLastDir(File lastDir) 
    {
        try 
        {
            prefs.setProperty("LastDir", lastDir.getCanonicalPath());
        } 
        catch (IOException e) 
        {
            // ignore
        }
        persistPrefs();
    }

    /**
     * application main entry
     */
    public static void main(String[] args) {
        loadPrefs();

        // set look'n'feel and create main GUI window
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                com.schwebke.CatchingEventQueue.installErrorDialogueHandler();
                String laf = getLookAndFeel();
                try {
                    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        if (laf.equals(info.getName())) {
                            UIManager.setLookAndFeel(info.getClassName());
                            break;
                        }
                    }
                } catch (Exception e) {
                    // ignore
                }

                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
                mainFrame.startBackgroundTasks();

                if (isWebStart()) {
                    JOptionPane.showMessageDialog(
                            mainFrame,
                            "It looks like you are using Java WebStart.\n"+
                            "Cut'n'Paste may not work like you expect.\n"+
                            "Please install the .jar locally if you like to use this functionality.\n\n"+
                            "See jregexanalyser.schwebke.com for further information.",
                            "Sandbox Detected",
                            JOptionPane.OK_OPTION|JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

    /**
     * translate a string to a Java string literal
     * (using the escape sequences according to Java language spec 3.10.6)
     */
    public static String toLiteral(String s) {
        StringBuilder r = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            switch (c) {
                case '\b':
                    r.append("\\b");
                    break;
                case '\t':
                    r.append("\\t");
                    break;
                case '\n':
                    r.append("\\n");
                    break;
                case '\f':
                    r.append("\\f");
                    break;
                case '\r':
                    r.append("\\r");
                    break;
                case '"':
                    r.append("\\\"");
                    break;
                case '\'':
                    r.append("\\\'");
                    break;
                case '\\':
                    r.append("\\\\");
                    break;
                default:
                    if ((c < '\u0020')||(c > '\u00FF')) {
                        r.append(String.format("\\u%04X", (int)c));
                    } else {
                        r.append(c);
                    }
            }
        }
        return r.toString();
    }

}
