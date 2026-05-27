package com.syndicate.ui;

import com.syndicate.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Map;

/**
 * Hideout tab: crew cards with HP bars + inventory item cards.
 * Painted in the Hollow Ascent inferno style.
 */
public class HideoutPanel extends JPanel {

    private final GameScreen gs;
    private JPanel crewGrid;
    private JPanel invGrid;

    public HideoutPanel(GameScreen gs) {
        this.gs = gs;
        setOpaque(false);
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel title = sectionTitle("HIDEOUT");
        add(title, BorderLayout.NORTH);

        JPanel centre = new JPanel(new GridLayout(1, 2, 14, 0));
        centre.setOpaque(false);

        JPanel crewBox = GameScreen.infernoPanel("CREW");
        crewGrid = new JPanel(new GameScreen.WrapLayout(FlowLayout.LEFT, 10, 10));
        crewGrid.setOpaque(false);
        crewBox.add(GameScreen.darkScroll(crewGrid), BorderLayout.CENTER);
        centre.add(crewBox);

        JPanel invBox = GameScreen.infernoPanel("INVENTORY");
        invGrid = new JPanel(new GameScreen.WrapLayout(FlowLayout.LEFT, 10, 10));
        invGrid.setOpaque(false);
        invBox.add(GameScreen.darkScroll(invGrid), BorderLayout.CENTER);
        centre.add(invBox);

        add(centre, BorderLayout.CENTER);
    }

    void refresh() {
        crewGrid.removeAll();
        for (StreetUnit u : gs.getHideout().getCrew()) crewGrid.add(buildCrewCard(u));
        invGrid.removeAll();
        for (InventoryItem i : gs.getHideout().getInventory()) invGrid.add(buildItemCard(i));
        crewGrid.revalidate(); crewGrid.repaint();
        invGrid.revalidate();  invGrid.repaint();
    }

    // ── crew card ─────────────────────────────────────────────────────────────

    private JPanel buildCrewCard(StreetUnit unit) {
        String type    = unit.getClass().getSimpleName().toLowerCase();
        String cardKey = "card_" + type;
        Map<String,Image> imgs = gs.getImgs();

        JPanel card = new JPanel(new BorderLayout(0, 3)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Image bg = imgs.get(cardKey);
                if (bg != null) g2.drawImage(bg, 0,0,getWidth(),getHeight(), null);
                else {
                    g2.setPaint(new GradientPaint(0,0,new Color(12,2,10),0,getHeight(),new Color(30,5,5)));
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                    g2.setColor(GameScreen.FIRE_ORANGE); g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                }
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(130, 182));
        card.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));

        // sprite
        Image spr = imgs.get(type);
        JLabel sprLbl = new JLabel();
        if (spr != null) sprLbl.setIcon(new ImageIcon(spr.getScaledInstance(64,64,Image.SCALE_SMOOTH)));
        sprLbl.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel sprHolder = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 36));
        sprHolder.setOpaque(false);
        sprHolder.add(sprLbl);

        // name
        JLabel nameLbl = new JLabel(unit.getName(), SwingConstants.CENTER);
        nameLbl.setFont(new Font("SansSerif",Font.BOLD,13));
        nameLbl.setForeground(GameScreen.TEXT_BRIGHT);

        // HP bar
        int maxHp = unit instanceof Brawler ? 120 : unit instanceof Hacker ? 70 : 90;
        int hp    = unit.getHealth();
        JProgressBar hpBar = new JProgressBar(0, maxHp);
        hpBar.setValue(hp);
        hpBar.setString(hp + "/" + maxHp);
        hpBar.setStringPainted(true);
        hpBar.setFont(new Font("SansSerif",Font.BOLD,9));
        Color barC = hp > maxHp*0.6 ? new Color(60,200,80) : hp > maxHp*0.3 ? new Color(220,190,40) : GameScreen.FIRE_ORANGE;
        hpBar.setForeground(barC);
        hpBar.setBackground(new Color(20,4,8));
        hpBar.setBorderPainted(false);

        // class badge
        JLabel typeLbl = new JLabel(unit.getClass().getSimpleName().toUpperCase(), SwingConstants.CENTER);
        typeLbl.setFont(new Font("SansSerif",Font.BOLD,8));
        typeLbl.setForeground(GameScreen.TEXT_DIM);

        JPanel bot = new JPanel(new BorderLayout(0,2));
        bot.setOpaque(false);
        bot.add(nameLbl, BorderLayout.NORTH);
        bot.add(hpBar,   BorderLayout.CENTER);
        bot.add(typeLbl, BorderLayout.SOUTH);

        card.add(sprHolder, BorderLayout.CENTER);
        card.add(bot,       BorderLayout.SOUTH);
        return card;
    }

    // ── inventory card ────────────────────────────────────────────────────────

    private JPanel buildItemCard(InventoryItem item) {
        String sprKey = itemSpriteKey(item);
        Map<String,Image> imgs = gs.getImgs();

        JPanel card = new JPanel(new BorderLayout(0,3)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,new Color(12,2,10),0,getHeight(),new Color(28,5,5)));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.setColor(GameScreen.PANEL_BORDER); g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(110, 120));
        card.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));

        JLabel sprLbl = new JLabel();
        Image spr = imgs.get(sprKey);
        if (spr != null) sprLbl.setIcon(new ImageIcon(spr.getScaledInstance(54,54,Image.SCALE_SMOOTH)));
        sprLbl.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel nameLbl = new JLabel("<html><center>"+item.getName()+"</center></html>", SwingConstants.CENTER);
        nameLbl.setFont(new Font("SansSerif",Font.BOLD,10));
        nameLbl.setForeground(GameScreen.TEXT_BRIGHT);

        card.add(sprLbl,  BorderLayout.CENTER);
        card.add(nameLbl, BorderLayout.SOUTH);
        return card;
    }

    private String itemSpriteKey(InventoryItem item) {
        if (item instanceof Firearm)     return "firearm";
        if (item instanceof BurnerPhone) return "burner_phone";
        if (item instanceof Medkit)      return "medkit";
        if (item instanceof Explosive)   return "explosive";
        return "firearm";
    }

    private JLabel sectionTitle(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("SansSerif",Font.BOLD,18));
        l.setForeground(GameScreen.FIRE_AMBER);
        l.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
        return l;
    }
}
