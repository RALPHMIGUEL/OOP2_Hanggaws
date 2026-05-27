package com.syndicate.ui;

import com.syndicate.*;
import com.syndicate.util.AssetLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

/**
 * Main game screen. Renders a persistent top-bar (funds/heat), a tab row
 * (Hideout / Shop / Mission), and swaps content panels via CardLayout.
 *
 * Visual style follows Hollow Ascent's inferno aesthetic:
 *   - Dark gradient backgrounds + city silhouettes
 *   - Hex-cornered panels with orange fire-borders
 *   - Lava-dividers, ember particles, vignette
 *   - Custom-painted buttons (same polygon-hex shape)
 */
public class GameScreen extends JPanel {

    // ── palette (matches Hollow Ascent's inferno theme) ──────────────────────
    static final Color FIRE_ORANGE  = new Color(255, 90,  0);
    static final Color FIRE_AMBER   = new Color(255, 180, 60);
    static final Color BLOOD_DARK   = new Color(14,  2,  10);
    static final Color BLOOD_MID    = new Color(36,  7,   7);
    static final Color PANEL_BORDER = new Color(110, 38, 80);
    static final Color TEXT_BRIGHT  = new Color(235, 215, 200);
    static final Color TEXT_AMBER   = new Color(255, 165, 55);
    static final Color TEXT_DIM     = new Color(130, 100, 80);

    // ── game state ────────────────────────────────────────────────────────────
    private Hideout hideout;

    // ── assets ────────────────────────────────────────────────────────────────
    private final Map<String, Image> imgs = new HashMap<>();

    // ── layout ────────────────────────────────────────────────────────────────
    private CardLayout cardLayout;
    private JPanel     cardPanel;
    private JLabel     fundsLabel, heatLabel;
    private JProgressBar heatBar;

    // ── sub-panels ────────────────────────────────────────────────────────────
    private HideoutPanel  hideoutPanel;
    private ShopPanel     shopPanel;
    private MissionPanel  missionPanel;

    // ── animation ────────────────────────────────────────────────────────────
    private double tick = 0;

    // ── tab state ─────────────────────────────────────────────────────────────
    private String activeTab = "hideout";
    private final String[] TABS     = {"hideout", "shop",     "mission"};
    private final String[] TAB_LBLS = {"🏠 HIDEOUT", "🛒 SHOP", "🎯 MISSION"};

    // ── construction ──────────────────────────────────────────────────────────

    public GameScreen(Hideout hideout, Runnable onShowCredits) {
        this.hideout = hideout;
        loadAssets();
        setLayout(new BorderLayout());
        setOpaque(false);

        add(buildTopBar(),  BorderLayout.NORTH);
        add(buildTabBar(),  BorderLayout.SOUTH);

        cardLayout = new CardLayout();
        cardPanel  = new JPanel(cardLayout) {
            @Override protected void paintComponent(Graphics g) { /* transparent */ }
        };
        cardPanel.setOpaque(false);

        hideoutPanel  = new HideoutPanel(this);
        shopPanel     = new ShopPanel(this, onShowCredits);
        missionPanel  = new MissionPanel(this);

        cardPanel.add(hideoutPanel,  "hideout");
        cardPanel.add(shopPanel,     "shop");
        cardPanel.add(missionPanel,  "mission");
        add(cardPanel, BorderLayout.CENTER);

        javax.swing.Timer t = new javax.swing.Timer(16, e -> { tick += 0.03; repaintTabBar(); });
        t.start();

        refresh();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        StartScreen.pixel(g2);
        drawBg(g2);
        drawEmbers(g2);
        drawVignette(g2);
        g2.dispose();
        super.paintComponent(g);
    }

    private void drawBg(Graphics2D g2) {
        Image bg = imgs.get("background");
        if (bg != null) {
            int w=getWidth(), h=getHeight();
            double sc = Math.max(w/(double)bg.getWidth(null), h/(double)bg.getHeight(null));
            int nw=(int)(bg.getWidth(null)*sc), nh=(int)(bg.getHeight(null)*sc);
            g2.drawImage(bg, (w-nw)/2, (h-nh)/2, nw, nh, this);
        } else {
            g2.setPaint(new GradientPaint(0,0,BLOOD_DARK,0,getHeight(),BLOOD_MID));
            g2.fillRect(0,0,getWidth(),getHeight());
        }
        g2.setColor(new Color(0,0,0,100));
        g2.fillRect(0,0,getWidth(),getHeight());
    }

    private void drawEmbers(Graphics2D g2) {
        int w=getWidth(), h=getHeight();
        for (int i=0;i<50;i++) {
            int x=(int)((i*143+tick*32)%(w+100))-50;
            int y=(int)(h*0.6)+((i*51)%Math.max(60,h/3));
            g2.setColor(new Color(255,80+i%50,10,35+(i%5)*14));
            g2.fillRect(x,y,2+(i%3),2+(i%3));
        }
    }

    private void drawVignette(Graphics2D g2) {
        int w=getWidth(), h=getHeight();
        RadialGradientPaint rg=new RadialGradientPaint(
            new Point2D.Float(w*0.5f,h*0.5f),Math.max(w,h)*0.7f,
            new float[]{0f,0.65f,1f},
            new Color[]{new Color(0,0,0,0),new Color(0,0,0,55),new Color(0,0,0,200)});
        g2.setPaint(rg); g2.fillRect(0,0,w,h);
    }

    // ── top bar ───────────────────────────────────────────────────────────────

    private JPanel buildTopBar() {
        JPanel bar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                g2.setPaint(new GradientPaint(0,0,new Color(8,0,8,230),getWidth(),0,new Color(26,5,5,230)));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(FIRE_ORANGE);
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(0,getHeight()-1,getWidth(),getHeight()-1);
            }
        };
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(7,18,7,18));
        bar.setPreferredSize(new Dimension(0, 52));

        JLabel logo = new JLabel("◈  SYNDICATE");
        logo.setFont(new Font("SansSerif", Font.BOLD, 22));
        logo.setForeground(FIRE_AMBER);
        bar.add(logo, BorderLayout.WEST);

        JPanel stats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        stats.setOpaque(false);

        // Save / Load
        JButton saveBtn = infernoBtn("💾 SAVE");
        saveBtn.addActionListener(e -> doSave());
        JButton loadBtn = infernoBtn("📂 LOAD");
        loadBtn.addActionListener(e -> doLoad());

        fundsLabel = statLabel("$500", TEXT_AMBER, "coin.png");
        heatLabel  = statLabel("Heat: 10", FIRE_ORANGE, "heat.png");

        heatBar = new JProgressBar(0, 100);
        heatBar.setPreferredSize(new Dimension(100, 12));
        heatBar.setForeground(FIRE_ORANGE);
        heatBar.setBackground(new Color(35,10,10));
        heatBar.setBorderPainted(false);
        heatBar.setValue(10);

        stats.add(saveBtn); stats.add(loadBtn);
        stats.add(Box.createHorizontalStrut(8));
        stats.add(fundsLabel); stats.add(heatLabel); stats.add(heatBar);
        bar.add(stats, BorderLayout.EAST);
        return bar;
    }

    private JLabel statLabel(String text, Color color, String iconFile) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.BOLD, 13));
        l.setForeground(color);
        Image ico = imgs.get(iconFile.replace(".png",""));
        if (ico != null) l.setIcon(new ImageIcon(ico.getScaledInstance(18,18,Image.SCALE_SMOOTH)));
        l.setIconTextGap(4);
        return l;
    }

    // ── tab bar ───────────────────────────────────────────────────────────────

    private JPanel tabBarPanel;

    private JPanel buildTabBar() {
        tabBarPanel = new JPanel(new GridLayout(1, TABS.length, 3, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                g2.setColor(new Color(8,0,8,230));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(FIRE_ORANGE);
                g2.drawLine(0,0,getWidth(),0);
            }
        };
        tabBarPanel.setOpaque(false);
        tabBarPanel.setBorder(BorderFactory.createEmptyBorder(5,8,5,8));
        tabBarPanel.setPreferredSize(new Dimension(0, 50));

        for (int i = 0; i < TABS.length; i++) {
            final String tab = TABS[i];
            final String lbl = TAB_LBLS[i];
            JButton btn = new JButton(lbl) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2=(Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    boolean active = tab.equals(activeTab);
                    boolean hover  = getModel().isRollover();
                    Color top = active ? new Color(50,10,8)  : hover ? new Color(30,6,6) : new Color(12,2,10);
                    Color bot = active ? new Color(80,18,10) : hover ? new Color(44,10,8): new Color(20,4,4);
                    int[] xs={10,getWidth()-10,getWidth()-1,getWidth()-10,10,1};
                    int[] ys={3,3,getHeight()/2,getHeight()-4,getHeight()-4,getHeight()/2};
                    g2.setPaint(new GradientPaint(0,0,top,0,getHeight(),bot));
                    g2.fillPolygon(xs,ys,6);
                    g2.setColor(active ? FIRE_AMBER : hover ? FIRE_ORANGE : PANEL_BORDER);
                    g2.setStroke(new BasicStroke(active?2f:1f));
                    g2.drawPolygon(xs,ys,6);
                    if (active) {
                        g2.setColor(new Color(255,120,0,50));
                        g2.fillPolygon(xs,ys,6);
                    }
                    g2.setFont(new Font("SansSerif",Font.BOLD,13));
                    FontMetrics fm=g2.getFontMetrics();
                    int tx=(getWidth()-fm.stringWidth(getText()))/2;
                    int ty=(getHeight()-fm.getHeight())/2+fm.getAscent();
                    g2.setColor(new Color(0,0,0,150)); g2.drawString(getText(),tx+1,ty+1);
                    g2.setColor(active ? FIRE_AMBER : TEXT_BRIGHT);
                    g2.drawString(getText(),tx,ty);
                    g2.dispose();
                }
            };
            btn.setContentAreaFilled(false); btn.setBorderPainted(false);
            btn.setFocusPainted(false); btn.setOpaque(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> switchTab(tab));
            tabBarPanel.add(btn);
        }
        return tabBarPanel;
    }

    private void repaintTabBar() { if (tabBarPanel != null) tabBarPanel.repaint(); }

    void switchTab(String tab) {
        activeTab = tab;
        cardLayout.show(cardPanel, tab);
        repaintTabBar();
        refresh();
    }

    // ── shared game operations ────────────────────────────────────────────────

    void refresh() {
        refreshStats();
        if ("hideout" .equals(activeTab)) hideoutPanel .refresh();
        if ("shop"    .equals(activeTab)) shopPanel    .refresh();
        if ("mission" .equals(activeTab)) missionPanel .refresh();
    }

    void refreshStats() {
        fundsLabel.setText(" $" + hideout.getFunds());
        int h = hideout.getHeat();
        heatLabel.setText(" Heat: " + h);
        heatBar.setValue(Math.min(h, 100));
        Color c = h < 40 ? new Color(60,200,80) : h < 70 ? new Color(220,190,40) : FIRE_ORANGE;
        heatLabel.setForeground(c);
        heatBar.setForeground(c);
    }

    private void doSave() {
        try { GameIO.save(hideout, "save.txt"); toast("Saved to save.txt", new Color(60,200,80)); }
        catch (Exception e) { toast("Save failed: " + e.getMessage(), FIRE_ORANGE); }
    }

    private void doLoad() {
        try {
            hideout = GameIO.load("save.txt");
            refresh();
            toast("Game loaded!", new Color(80,160,220));
        } catch (Exception e) { toast("Load failed: " + e.getMessage(), FIRE_ORANGE); }
    }

    Hideout getHideout() { return hideout; }
    Map<String,Image> getImgs() { return imgs; }

    // ── toast notifications ───────────────────────────────────────────────────

    void toast(String msg, Color color) {
        JWindow win = new JWindow(SwingUtilities.getWindowAncestor(this));
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(14,2,10,220)); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(color); g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
            }
        };
        p.setBorder(BorderFactory.createEmptyBorder(8,18,8,18));
        p.setOpaque(false);
        JLabel lbl=new JLabel(msg);
        lbl.setFont(new Font("SansSerif",Font.BOLD,14));
        lbl.setForeground(color);
        p.add(lbl);
        win.add(p); win.pack();
        Window owner = SwingUtilities.getWindowAncestor(this);
        if (owner != null) {
            Point loc = owner.getLocation();
            Dimension sz = owner.getSize();
            win.setLocation(loc.x+(sz.width-win.getWidth())/2, loc.y+sz.height-88);
        }
        win.setVisible(true);
        javax.swing.Timer toastTimer = new javax.swing.Timer(2200, e -> win.dispose());
        toastTimer.setRepeats(false);
        toastTimer.start();
    }

    // ── shared UI factories ───────────────────────────────────────────────────

    /** Dark inferno panel with title header — matches Hollow Ascent's drawInfernoPanel */
    static JPanel infernoPanel(String title) {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,0,0,120)); g2.fillRoundRect(8,8,getWidth(),getHeight(),10,10);
                g2.setPaint(new GradientPaint(0,0,new Color(12,2,10,215),0,getHeight(),new Color(34,6,6,215)));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(FIRE_ORANGE); g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                g2.setColor(PANEL_BORDER); g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(8,8,getWidth()-17,getHeight()-17,6,6);
            }
        };
        p.setOpaque(false);
        if (title != null && !title.isEmpty()) {
            JLabel h = new JLabel("  " + title);
            h.setFont(new Font("SansSerif",Font.BOLD,11));
            h.setForeground(TEXT_DIM);
            h.setBorder(BorderFactory.createMatteBorder(0,0,1,0,PANEL_BORDER));
            h.setPreferredSize(new Dimension(0,24));
            p.add(h, BorderLayout.NORTH);
        }
        return p;
    }

    /** Styled scroll pane */
    static JScrollPane darkScroll(JComponent c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setOpaque(false); sp.getViewport().setOpaque(false);
        sp.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        sp.getVerticalScrollBar().setUnitIncrement(14);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.getVerticalScrollBar().setBackground(new Color(14,2,10));
        return sp;
    }

    /** Small inferno-style button */
    static JButton infernoBtn(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color top = getModel().isPressed() ? new Color(40,8,8) : getModel().isRollover() ? new Color(32,6,6) : new Color(16,3,10);
                Color bot = getModel().isPressed() ? new Color(70,16,6): getModel().isRollover() ? new Color(52,11,7): new Color(30,6,6);
                int[] xs={8,getWidth()-8,getWidth()-1,getWidth()-8,8,1};
                int[] ys={3,3,getHeight()/2,getHeight()-4,getHeight()-4,getHeight()/2};
                g2.setPaint(new GradientPaint(0,0,top,0,getHeight(),bot));
                g2.fillPolygon(xs,ys,6);
                g2.setColor(getModel().isRollover() ? FIRE_AMBER : FIRE_ORANGE);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawPolygon(xs,ys,6);
                g2.setFont(new Font("SansSerif",Font.BOLD,12));
                FontMetrics fm=g2.getFontMetrics();
                int tx=(getWidth()-fm.stringWidth(getText()))/2;
                int ty=(getHeight()-fm.getHeight())/2+fm.getAscent();
                g2.setColor(new Color(40,5,0,150)); g2.drawString(getText(),tx+1,ty+1);
                g2.setColor(FIRE_AMBER); g2.drawString(getText(),tx,ty);
                g2.dispose();
            }
        };
        btn.setContentAreaFilled(false); btn.setBorderPainted(false);
        btn.setFocusPainted(false); btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    /** FlowLayout that wraps correctly inside scroll panes */
    static class WrapLayout extends FlowLayout {
        WrapLayout(int align, int hg, int vg) { super(align, hg, vg); }
        @Override public Dimension preferredLayoutSize(Container t) { return layout(t,true); }
        @Override public Dimension minimumLayoutSize(Container t)   { return layout(t,false); }
        private Dimension layout(Container t, boolean pref) {
            synchronized(t.getTreeLock()) {
                int maxW = t.getSize().width;
                if (maxW==0) maxW=Integer.MAX_VALUE;
                Insets ins=t.getInsets();
                maxW -= ins.left+ins.right+getHgap()*2;
                Dimension dim=new Dimension(0,0);
                int rowW=0, rowH=0;
                for (int i=0;i<t.getComponentCount();i++) {
                    Component m=t.getComponent(i);
                    if (!m.isVisible()) continue;
                    Dimension d=pref?m.getPreferredSize():m.getMinimumSize();
                    if (rowW+d.width>maxW) { addRow(dim,rowW,rowH); rowW=0; rowH=0; }
                    if (rowW!=0) rowW+=getHgap();
                    rowW+=d.width; rowH=Math.max(rowH,d.height);
                }
                addRow(dim,rowW,rowH);
                dim.width  += ins.left+ins.right+getHgap()*2;
                dim.height += ins.top+ins.bottom+getVgap()*2;
                return dim;
            }
        }
        private void addRow(Dimension d,int rw,int rh){d.width=Math.max(d.width,rw);if(d.height>0)d.height+=getVgap();d.height+=rh;}
    }

    // ── asset loading ─────────────────────────────────────────────────────────

    private void loadAssets() {
        String[] names = {
            "background","brawler","hacker","driver",
            "firearm","burner_phone","medkit","explosive",
            "patrol_officer","swat_team",
            "card_brawler","card_hacker","card_driver",
            "mission_easy","mission_medium","mission_hard",
            "success_banner","fail_banner",
            "coin","heat","heart","ghost","grim_reaper"
        };
        for (String n : names) {
            Image i = AssetLoader.loadImage(n + ".png");
            if (i != null) imgs.put(n, i);
        }
    }
}
