package com.syndicate.ui;

import com.syndicate.util.AssetLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * Credits / game-complete screen.
 * Follows the same layered-panel paint pattern as Hollow Ascent's
 * GameCompleteScreen: animated background → dark panel → credits text → button.
 */
public class CreditsScreen extends JPanel {

    private final Runnable onReturnToMenu;
    private final Timer    animTimer;
    private double tick = 0;

    private final ImageIcon bgIcon      = AssetLoader.loadIcon("background.png");
    private final ImageIcon headerIcon  = AssetLoader.loadIcon("credits_header.png");
    private final ImageIcon ghostIcon   = AssetLoader.loadIcon("ghost.png");
    private final StartScreen.HexButton backBtn =
            new StartScreen.HexButton("btn_menu.png", "btn_menu_pressed.png");

    // ── construction ──────────────────────────────────────────────────────────

    public CreditsScreen(Runnable onReturnToMenu) {
        this.onReturnToMenu = onReturnToMenu;
        setLayout(null);
        setOpaque(true);
        setFocusable(true);

        backBtn.addActionListener(e -> this.onReturnToMenu.run());
        add(backBtn);

        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { layoutButton(); }
        });

        animTimer = new Timer(16, e -> { tick += 0.032; layoutButton(); repaint(); });
        animTimer.start();
    }

    @Override public void addNotify() { super.addNotify(); requestFocusInWindow(); layoutButton(); }

    private void layoutButton() {
        int w = getWidth(), h = getHeight();
        if (w <= 0 || h <= 0) return;
        int bw = StartScreen.clamp((int)(w * 0.34), 340, 470);
        int bh = (int)(bw * (110.0 / 440.0));
        backBtn.setBounds((w - bw)/2, h - bh - StartScreen.clamp((int)(h*0.055), 32, 60), bw, bh);
    }

    // ── paint ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        StartScreen.pixel(g2);
        int w = getWidth(), h = getHeight();

        drawBg(g2, w, h);
        g2.setColor(new Color(0,0,0,100));
        g2.fillRect(0,0,w,h);
        drawEmbers(g2, w, h);
        drawPanel(g2, w, h);
        drawCredits(g2, w, h);
        drawGhost(g2, w, h);
        drawVignette(g2, w, h);
        g2.dispose();
    }

    private void drawBg(Graphics2D g2, int w, int h) {
        if (bgIcon != null && bgIcon.getIconWidth() > 0) {
            int iw = bgIcon.getIconWidth(), ih = bgIcon.getIconHeight();
            double sc = Math.max(w/(double)iw, h/(double)ih);
            int nw=(int)Math.ceil(iw*sc), nh=(int)Math.ceil(ih*sc);
            g2.drawImage(bgIcon.getImage(), (w-nw)/2, (h-nh)/2, nw, nh, this);
        } else {
            g2.setPaint(new GradientPaint(0,0,new Color(6,1,10),0,h,new Color(20,4,6)));
            g2.fillRect(0,0,w,h);
        }
    }

    private Rectangle panelRect(int w, int h) {
        int pw = StartScreen.clamp((int)(w*0.55), 560, 780);
        int ph = StartScreen.clamp((int)(h*0.74), 460, 620);
        return new Rectangle((w-pw)/2, StartScreen.clamp((int)(h*0.04),24,40), pw, ph);
    }

    private void drawPanel(Graphics2D g2, int w, int h) {
        Rectangle p = panelRect(w, h);
        // shadow
        g2.setColor(new Color(0,0,0,130));
        g2.fillRect(p.x+12, p.y+12, p.width, p.height);
        // body
        g2.setPaint(new GradientPaint(p.x,p.y,new Color(10,2,10,230),p.x,p.y+p.height,new Color(34,6,6,230)));
        g2.fillRect(p.x, p.y, p.width, p.height);
        // borders
        g2.setColor(new Color(60,44,58)); g2.drawRect(p.x,p.y,p.width,p.height);
        g2.setColor(new Color(255,88,0)); g2.setStroke(new BasicStroke(2));
        g2.drawRect(p.x+7,p.y+7,p.width-14,p.height-14);
        g2.setColor(new Color(88,40,82)); g2.setStroke(new BasicStroke(1));
        g2.drawRect(p.x+14,p.y+14,p.width-28,p.height-28);
        g2.setColor(new Color(30,18,30));
        g2.drawRect(p.x+22,p.y+22,p.width-44,p.height-44);

        // header image
        if (headerIcon != null) {
            int hw = StartScreen.clamp((int)(p.width*0.76), 480, 640);
            int hh = (int)(hw * (headerIcon.getIconHeight()/(double)headerIcon.getIconWidth()));
            g2.drawImage(headerIcon.getImage(), p.x+(p.width-hw)/2, p.y+12, hw, hh, this);
        } else {
            drawPixelText(g2, "SYNDICATE", p.x+p.width/2, p.y+52, 36, true, new Color(255,165,55));
        }
        g2.setStroke(new BasicStroke(1));
    }

    private void drawCredits(Graphics2D g2, int w, int h) {
        Rectangle p = panelRect(w, h);
        int cx = p.x + p.width/2;
        int y  = p.y + StartScreen.clamp((int)(p.height*0.24), 120, 155);

        drawLavaDivider(g2, cx, y-10, 220);
        drawPixelText(g2, "DEVELOPERS", cx, y+8, 22, true, new Color(235,215,200));
        y += 48;

        String[] devs = {"Hanggaws, Ralph Miguel", "Team Member 2", "Team Member 3", "Team Member 4"};
        for (String d : devs) {
            drawPixelText(g2, d, cx, y, 21, false, new Color(255,148,32));
            y += 32;
        }

        y += 14;
        drawLavaDivider(g2, cx, y-10, 220);
        drawPixelText(g2, "SPECIAL THANKS", cx, y+8, 22, true, new Color(235,215,200));
        y += 48;
        drawPixelText(g2, "Object-Oriented Programming 2", cx, y, 20, false, new Color(255,122,18));

        y += 44;
        drawLavaDivider(g2, cx, y-10, 220);
        drawPixelText(g2, "A FINAL REQUIREMENT", cx, y+8, 18, true, new Color(235,215,200));
        y += 30;
        drawPixelText(g2, "OOP2 — 2024", cx, y, 18, false, new Color(255,215,200));
    }

    private void drawGhost(Graphics2D g2, int w, int h) {
        if (ghostIcon == null) return;
        Rectangle p = panelRect(w, h);
        int gh = StartScreen.clamp((int)(h*0.13), 85, 120);
        int gw = (int)(gh * (ghostIcon.getIconWidth()/(double)ghostIcon.getIconHeight()));
        int x  = p.x + p.width - gw - 48;
        int y  = p.y + p.height/2 - 10 + (int)(Math.sin(tick+1.4)*6);
        g2.drawImage(ghostIcon.getImage(), x, y, gw, gh, this);
    }

    private void drawEmbers(Graphics2D g2, int w, int h) {
        for (int i = 0; i < 50; i++) {
            int x = (int)((i*155 + tick*30) % (w+110)) - 55;
            int y = (int)(h*0.65) + ((i*45) % Math.max(50, h/4));
            g2.setColor(new Color(255,82,10,38+(i%4)*18));
            g2.fillRect(x, y, 2+(i%3), 2+(i%3));
        }
    }

    private void drawVignette(Graphics2D g2, int w, int h) {
        RadialGradientPaint rg = new RadialGradientPaint(
            new Point2D.Float(w*0.5f, h*0.48f), Math.max(w,h)*0.68f,
            new float[]{0f,0.68f,1f},
            new Color[]{new Color(0,0,0,0),new Color(0,0,0,72),new Color(0,0,0,222)});
        g2.setPaint(rg);
        g2.fillRect(0,0,w,h);
    }

    private void drawPixelText(Graphics2D g2, String text, int cx, int y, int size, boolean bold, Color color) {
        g2.setFont(new Font("Monospaced", bold ? Font.BOLD : Font.PLAIN, size));
        FontMetrics fm = g2.getFontMetrics();
        int x = cx - fm.stringWidth(text)/2;
        g2.setColor(new Color(0,0,0,200)); g2.drawString(text, x+2, y+2);
        g2.setColor(new Color(70,25,15,110)); g2.drawString(text, x+1, y+1);
        g2.setColor(color); g2.drawString(text, x, y);
    }

    private void drawLavaDivider(Graphics2D g2, int cx, int y, int hw) {
        g2.setColor(new Color(96,33,78,175));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(cx-hw, y, cx-18, y);
        g2.drawLine(cx+18, y, cx+hw, y);
        g2.setColor(new Color(255,88,0,195));
        g2.fillRect(cx-5, y-5, 10, 10);
        g2.setStroke(new BasicStroke(1));
    }
}
