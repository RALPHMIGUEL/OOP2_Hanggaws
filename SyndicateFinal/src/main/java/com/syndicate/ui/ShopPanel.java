package com.syndicate.ui;

import com.syndicate.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Shop tab: 8 purchasable items / recruits in an inferno-styled grid.
 */
public class ShopPanel extends JPanel {

    private final GameScreen gs;
    private final Runnable   onShowCredits;
    private JPanel shopGrid;

    // ── shop catalogue ────────────────────────────────────────────────────────

    private static class Entry {
        final String name, desc, sprKey;
        final int cost;
        final java.util.function.Consumer<Hideout> buy;
        Entry(String n, String d, int c, String s, java.util.function.Consumer<Hideout> b) {
            name=n; desc=d; cost=c; sprKey=s; buy=b;
        }
    }

    private List<Entry> entries() {
        Hideout h = gs.getHideout();
        List<Entry> list = new ArrayList<>();
        list.add(new Entry("Recruit Hacker",  "Hacks systems, slashes Heat.",    150, "hacker",
            ho -> ho.recruit(new Hacker("Hacker"+(ho.getCrew().size()+1)))));
        list.add(new Entry("Recruit Driver",  "Expert getaway driver.",           120, "driver",
            ho -> ho.recruit(new Driver("Driver"+(ho.getCrew().size()+1)))));
        list.add(new Entry("Recruit Brawler", "Raw muscle & intimidation.",       100, "brawler",
            ho -> ho.recruit(new Brawler("Brawler"+(ho.getCrew().size()+1)))));
        list.add(new Entry("Medkit",          "Heals the whole crew after ops.",   40, "medkit",
            ho -> ho.addItem(new Medkit(15))));
        list.add(new Entry("Explosive",       "High damage — raises Heat a lot.", 150, "explosive",
            ho -> ho.addItem(new Explosive())));
        list.add(new Entry("Burner Phone",    "Drops Heat by 15.",                 60, "burner_phone",
            ho -> ho.addItem(new BurnerPhone())));
        list.add(new Entry("Pistol",          "Reliable sidearm, +12 power.",      80, "firearm",
            ho -> ho.addItem(new Firearm("Pistol", 12, 8))));
        list.add(new Entry("Sniper Rifle",    "High power, whisper-quiet.",        200, "firearm",
            ho -> ho.addItem(new Firearm("Sniper Rifle", 22, 4))));
        return list;
    }

    // ── construction ──────────────────────────────────────────────────────────

    public ShopPanel(GameScreen gs, Runnable onShowCredits) {
        this.gs = gs;
        this.onShowCredits = onShowCredits;
        setOpaque(false);
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel title = sectionTitle("BLACK MARKET SHOP");
        add(title, BorderLayout.NORTH);

        shopGrid = new JPanel(new GridLayout(2, 4, 12, 12));
        shopGrid.setOpaque(false);
        JScrollPane sp = GameScreen.darkScroll(shopGrid);
        add(sp, BorderLayout.CENTER);

        // Credits link bottom-right
        JButton cBtn = GameScreen.infernoBtn("📜 CREDITS");
        cBtn.addActionListener(e -> { if (onShowCredits != null) onShowCredits.run(); });
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        south.setOpaque(false);
        south.add(cBtn);
        add(south, BorderLayout.SOUTH);
    }

    void refresh() {
        shopGrid.removeAll();
        for (Entry e : entries()) shopGrid.add(buildCard(e));
        shopGrid.revalidate(); shopGrid.repaint();
    }

    // ── card ──────────────────────────────────────────────────────────────────

    private JPanel buildCard(Entry entry) {
        Map<String,Image> imgs = gs.getImgs();

        JPanel card = new JPanel(new BorderLayout(0,4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,new Color(12,2,10,215),0,getHeight(),new Color(32,6,6,215)));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                g2.setColor(GameScreen.PANEL_BORDER); g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.setColor(new Color(255,255,255,12));
                g2.fillRoundRect(4,4,getWidth()-8,getHeight()/2,8,8);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(9,9,9,9));

        JLabel sprLbl = new JLabel();
        Image spr = imgs.get(entry.sprKey);
        if (spr != null) sprLbl.setIcon(new ImageIcon(spr.getScaledInstance(58,58,Image.SCALE_SMOOTH)));
        sprLbl.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel nameLbl = new JLabel(entry.name, SwingConstants.CENTER);
        nameLbl.setFont(new Font("SansSerif",Font.BOLD,13));
        nameLbl.setForeground(GameScreen.TEXT_BRIGHT);

        JLabel descLbl = new JLabel("<html><center>"+entry.desc+"</center></html>", SwingConstants.CENTER);
        descLbl.setFont(new Font("SansSerif",Font.PLAIN,11));
        descLbl.setForeground(GameScreen.TEXT_DIM);

        JButton buyBtn = GameScreen.infernoBtn("BUY  $" + entry.cost);
        buyBtn.addActionListener(e -> {
            Hideout h = gs.getHideout();
            if (h.getFunds() < entry.cost) {
                gs.toast("Not enough funds! Need $" + entry.cost, GameScreen.FIRE_ORANGE);
            } else {
                h.removeFunds(entry.cost);
                entry.buy.accept(h);
                gs.refreshStats();
                gs.toast("Purchased: " + entry.name, new Color(60,200,80));
            }
        });

        JPanel info = new JPanel(new BorderLayout(0,3));
        info.setOpaque(false);
        info.add(nameLbl, BorderLayout.NORTH);
        info.add(descLbl, BorderLayout.CENTER);
        info.add(buyBtn,  BorderLayout.SOUTH);

        card.add(sprLbl, BorderLayout.CENTER);
        card.add(info,   BorderLayout.SOUTH);
        return card;
    }

    private JLabel sectionTitle(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("SansSerif",Font.BOLD,18));
        l.setForeground(GameScreen.FIRE_AMBER);
        l.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
        return l;
    }
}
