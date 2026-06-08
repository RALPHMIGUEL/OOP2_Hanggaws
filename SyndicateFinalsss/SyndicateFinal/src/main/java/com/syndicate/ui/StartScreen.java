package com.syndicate.ui;

import com.syndicate.util.AssetLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Animated start screen with inferno cityscape aesthetic.
 * Follows the same paint-driven, timer-animated pattern as Hollow Ascent's
 * StartScreen.
 */
public class StartScreen extends JPanel {

    private final Runnable onStartGame;
    private final Timer    animTimer;
    private double tick = 0;

    // Assets
    private final ImageIcon bgIcon     = AssetLoader.loadIcon("start_bg.png");
    private final ImageIcon logoIcon   = AssetLoader.loadIcon("logo.png");
    private final ImageIcon grimIcon   = AssetLoader.loadIcon("Agent.png");
    private final HexButton startBtn   = new HexButton("btn_start.png", "btn_start_pressed.png");

    // ── construction ──────────────────────────────────────────────────────────

    public StartScreen(Runnable onStartGame) {
        this.onStartGame = onStartGame;
        setLayout(null);
        setOpaque(true);
        setFocusable(true);

        startBtn.addActionListener(e -> this.onStartGame.run());
        add(startBtn);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { layoutButton(); }
        });

        animTimer = new Timer(16, e -> {
            tick += 0.032;
            layoutButton();
            repaint();
        });
        animTimer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        requestFocusInWindow();
        layoutButton();
    }

    // ── button layout ─────────────────────────────────────────────────────────

    private void layoutButton() {
        int w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;
        int bw = clamp((int)(w * 0.40), 380, 520);
        int bh = (int)(bw * (110.0 / 440.0));
        int x  = (w - bw) / 2;
        int y  = h - bh - clamp((int)(h * 0.07), 44, 78);
        startBtn.setBounds(x, y, bw, bh);
    }

    // ── paint ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        pixel(g2);
        int w = getWidth(), h = getHeight();

        drawBg(g2, w, h);
        drawAtmosphere(g2, w, h);
        drawLogo(g2, w, h);
        drawHero(g2, w, h);
        drawEmbers(g2, w, h);
        drawVignette(g2, w, h);


        g2.dispose();
    }

    private void drawBg(Graphics2D g2, int w, int h) {
        if (bgIcon != null && bgIcon.getIconWidth() > 0) {
            drawCover(g2, bgIcon, w, h, 0.5, 0.5);
        } else {
            g2.setPaint(new GradientPaint(0, 0, new Color(6,1,10), 0, h, new Color(20,4,6)));
            g2.fillRect(0, 0, w, h);
        }
    }

    private void drawAtmosphere(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(5, 1, 5, 60));
        g2.fillRect(0, 0, w, h);
        g2.setPaint(new GradientPaint(0, 0, new Color(0,0,0,65), 0, h, new Color(0,0,0,15)));
        g2.fillRect(0, 0, w, h);
    }

    private void drawLogo(Graphics2D g2, int w, int h) {
        if (logoIcon == null || logoIcon.getIconWidth() <= 0) return;
        int lw = clamp((int)(w * 0.46), 440, 660);
        int lh = (int)(lw * (logoIcon.getIconHeight() / (double)logoIcon.getIconWidth()));
        int x  = (w - lw) / 2;
        int y  = clamp((int)(h * 0.05), 22, 55) + (int)(Math.sin(tick) * 7);
        drawGlow(g2, x + lw/2, y + lh/2, (int)(lw*0.45), new Color(255, 70, 0, 40));
        g2.drawImage(logoIcon.getImage(), x, y, lw, lh, this);
    }

    private void drawHero(Graphics2D g2, int w, int h) {
        if (grimIcon == null || grimIcon.getIconWidth() <= 0) return;
        int gh = clamp((int)(h * 0.22), 110, 160);
        int gw = (int)(gh * (grimIcon.getIconWidth() / (double)grimIcon.getIconHeight()));
        int gx = (w - gw) / 2;
        int gy = (int)(h * 0.52) + (int)(Math.sin(tick + 1.8) * 5);
        drawGlow(g2, gx + gw/2, gy + gh/2, gh/2, new Color(255, 65, 0, 35));
        g2.drawImage(grimIcon.getImage(), gx, gy, gw, gh, this);
    }

    private void drawEmbers(Graphics2D g2, int w, int h) {
        for (int i = 0; i < 65; i++) {
            int x = (int)((i * 141 + tick * 38) % (w + 140)) - 70;
            int y = (int)(h * 0.65) + ((i * 57) % Math.max(60, h / 3));
            int s = 2 + (i % 3);
            g2.setColor(new Color(255, 82 + i%40, 12, 40 + (i%5)*16));
            g2.fillRect(x, y, s, s);
        }
    }



    private void drawVignette(Graphics2D g2, int w, int h) {
        RadialGradientPaint rg = new RadialGradientPaint(
            new Point2D.Float(w * 0.5f, h * 0.48f), Math.max(w, h) * 0.66f,
            new float[]{0f, 0.60f, 1f},
            new Color[]{new Color(0,0,0,0), new Color(0,0,0,68), new Color(0,0,0,230)});
        g2.setPaint(rg);
        g2.fillRect(0, 0, w, h);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private void drawGlow(Graphics2D g2, int cx, int cy, int r, Color c) {
        RadialGradientPaint rg = new RadialGradientPaint(
            new Point2D.Float(cx, cy), r,
            new float[]{0f, 0.55f, 1f},
            new Color[]{c, new Color(c.getRed(),c.getGreen(),c.getBlue(),c.getAlpha()/3), new Color(0,0,0,0)});
        g2.setPaint(rg);
        g2.fillOval(cx - r, cy - r, r * 2, r * 2);
    }

    private void drawCover(Graphics2D g2, ImageIcon ico, int w, int h, double fx, double fy) {
        int iw = ico.getIconWidth(), ih = ico.getIconHeight();
        double scale = Math.max(w / (double)iw, h / (double)ih);
        int nw = (int)Math.ceil(iw * scale);
        int nh = (int)Math.ceil(ih * scale);
        int x  = (int)((w - nw) * fx);
        int y  = (int)((h - nh) * fy);
        g2.drawImage(ico.getImage(), x, y, nw, nh, this);
    }

    static void pixel(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
    }

    static int clamp(int v, int min, int max) { return Math.max(min, Math.min(max, v)); }

    // ── inner class: image-based hex button ───────────────────────────────────

    static class HexButton extends JButton {
        private final ImageIcon normal;
        private final ImageIcon pressed;

        HexButton(String normalName, String pressedName) {
            normal  = AssetLoader.loadIcon(normalName);
            pressed = AssetLoader.loadIcon(pressedName);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            pixel(g2);
            ImageIcon ico = (getModel().isPressed() || getModel().isRollover()) ? pressed : normal;
            if (ico != null && ico.getIconWidth() > 0) {
                g2.drawImage(ico.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
            g2.dispose();
        }
    }
}
