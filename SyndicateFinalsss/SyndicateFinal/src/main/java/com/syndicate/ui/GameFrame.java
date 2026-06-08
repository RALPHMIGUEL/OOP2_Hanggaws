package com.syndicate.ui;

import javax.swing.JFrame;

/**
 * Main application window.
 * Not full-screen (unlike Hollow Ascent) — sized to 980×700 so it
 * works on any monitor without needing a graphics-device query.
 */
public class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("SYNDICATE");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 720);
        setMinimumSize(new java.awt.Dimension(900, 650));
        setLocationRelativeTo(null);
        setResizable(true);
    }
}
