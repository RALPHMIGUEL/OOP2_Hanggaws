package com.syndicate.util;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Resolves and loads PNG/GIF assets from the assets/ folder.
 * Mirrors the multi-candidate resolution strategy used in Hollow Ascent.
 */
public class AssetLoader {

    private static File assetsDir = null;

    /** Returns the resolved assets directory (cached after first call). */
    public static File getAssetsDir() {
        if (assetsDir != null) return assetsDir;

        String[] candidates = {
            "assets",
            "SyndicateFinal/assets",
            "../assets",
            "../SyndicateFinal/assets"
        };

        for (String c : candidates) {
            File f = new File(c);
            if (f.exists() && f.isDirectory()) {
                assetsDir = f;
                return f;
            }
        }

        // Fallback — won't throw, just returns "assets" relative to cwd
        assetsDir = new File("assets");
        return assetsDir;
    }

    /**
     * Loads a static PNG using ImageIO (best for non-animated images).
     * Returns null on failure; callers must handle gracefully.
     */
    public static Image loadImage(String filename) {
        File f = new File(getAssetsDir(), filename);
        try {
            return ImageIO.read(f);
        } catch (IOException e) {
            System.err.println("AssetLoader: cannot read " + f.getAbsolutePath());
            return null;
        }
    }

    /**
     * Loads an ImageIcon (supports animated GIF).
     * Falls back to classpath resource, then returns null.
     */
    public static ImageIcon loadIcon(String filename) {
        File f = new File(getAssetsDir(), filename);
        if (f.exists()) return new ImageIcon(f.getAbsolutePath());
        URL url = AssetLoader.class.getResource("/assets/" + filename);
        if (url != null) return new ImageIcon(url);
        System.err.println("AssetLoader: missing asset: " + filename);
        return null;
    }
}
