package com.syndicate.ui;

import com.syndicate.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Mission tab: select a mission tile, check off crew and items,
 * see the operation log rendered in a green-on-black terminal,
 * and a result banner overlay (success / failure).
 *
 * Overlays are drawn on top via paintComponent, matching
 * Hollow Ascent's drawGameOverOverlay / drawLevelCompleteOverlay pattern.
 */
public class MissionPanel extends JPanel {

    private final GameScreen gs;

    private JPanel   missionTiles;
    private JPanel   crewChecks;
    private JPanel   itemChecks;
    private JTextArea logArea;
    private JLabel   resultLbl;

    private String selectedMission    = "Easy Job";
    private int    selectedDifficulty = 0;
    private boolean showResult = false;
    private boolean lastSuccess = false;

    // ── construction ──────────────────────────────────────────────────────────

    public MissionPanel(GameScreen gs) {
        this.gs = gs;
        setOpaque(false);
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));

        JLabel title = sectionTitle("RUN A MISSION");
        add(title, BorderLayout.NORTH);

        // ── left column ──
        JPanel left = new JPanel(new BorderLayout(0, 10));
        left.setOpaque(false);
        left.setPreferredSize(new Dimension(320, 0));

        JPanel tileBox = GameScreen.infernoPanel("SELECT MISSION");
        missionTiles = new JPanel(new GridLayout(3, 1, 0, 7));
        missionTiles.setOpaque(false);
        missionTiles.setBorder(BorderFactory.createEmptyBorder(7,7,7,7));
        tileBox.add(missionTiles, BorderLayout.CENTER);
        left.add(tileBox, BorderLayout.NORTH);

        JPanel crewBox = GameScreen.infernoPanel("SELECT CREW");
        crewChecks = new JPanel(new GameScreen.WrapLayout(FlowLayout.LEFT, 6, 5));
        crewChecks.setOpaque(false);
        crewBox.add(GameScreen.darkScroll(crewChecks), BorderLayout.CENTER);
        left.add(crewBox, BorderLayout.CENTER);

        JPanel itemBox = GameScreen.infernoPanel("SELECT ITEMS");
        itemBox.setPreferredSize(new Dimension(0, 120));
        itemChecks = new JPanel(new GameScreen.WrapLayout(FlowLayout.LEFT, 6, 5));
        itemChecks.setOpaque(false);
        itemBox.add(GameScreen.darkScroll(itemChecks), BorderLayout.CENTER);
        left.add(itemBox, BorderLayout.SOUTH);

        // ── right column: terminal log ──
        JPanel right = GameScreen.infernoPanel("OPERATION LOG");

        logArea = new JTextArea("Select crew, items, and a mission tile to launch.\n");
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setForeground(new Color(0, 220, 95));
        logArea.setBackground(new Color(4, 10, 4));
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createEmptyBorder());
        logScroll.getViewport().setBackground(new Color(4,10,4));
        right.add(logScroll, BorderLayout.CENTER);

        resultLbl = new JLabel();
        resultLbl.setHorizontalAlignment(SwingConstants.CENTER);
        resultLbl.setPreferredSize(new Dimension(0, 82));
        right.add(resultLbl, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
        split.setOpaque(false); split.setBorder(BorderFactory.createEmptyBorder());
        split.setDividerSize(4); split.setDividerLocation(320); split.setResizeWeight(0.33);
        add(split, BorderLayout.CENTER);
    }

    // ── refresh ───────────────────────────────────────────────────────────────

    void refresh() {
        buildMissionTiles();
        buildCrewChecks();
        buildItemChecks();
        resultLbl.setIcon(null);
        showResult = false;
        repaint();
    }

    private void buildMissionTiles() {
        Map<String,Image> imgs = gs.getImgs();
        missionTiles.removeAll();
        String[][] missions = {
            {"Easy Job",     "0", "mission_easy"},
            {"Heist",        "1", "mission_medium"},
            {"Bank Robbery", "2", "mission_hard"}
        };
        for (String[] m : missions) {
            final String mName = m[0];
            final int    mDiff = Integer.parseInt(m[1]);
            final String mKey  = m[2];
            JButton btn = new JButton(mName) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2=(Graphics2D)g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    Image tile = imgs.get(mKey);
                    if (tile != null) g2.drawImage(tile,0,0,getWidth(),getHeight(),null);
                    else {
                        g2.setPaint(new GradientPaint(0,0,new Color(12,2,10),0,getHeight(),new Color(34,6,6)));
                        g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                    }
                    if (mName.equals(selectedMission)) {
                        g2.setColor(new Color(255,255,255,50));
                        g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                        g2.setColor(Color.WHITE); g2.setStroke(new BasicStroke(2.5f));
                        g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,10,10);
                    } else if (getModel().isRollover()) {
                        g2.setColor(new Color(255,255,255,20));
                        g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                    }
                    // label
                    g2.setFont(new Font("SansSerif",Font.BOLD,14));
                    FontMetrics fm=g2.getFontMetrics();
                    int tx=(getWidth()-fm.stringWidth(getText()))/2, ty=(getHeight()+fm.getAscent())/2-2;
                    g2.setColor(new Color(0,0,0,150)); g2.drawString(getText(),tx+1,ty+1);
                    g2.setColor(Color.WHITE); g2.drawString(getText(),tx,ty);
                    g2.dispose();
                }
            };
            btn.setContentAreaFilled(false); btn.setBorderPainted(false);
            btn.setFocusPainted(false); btn.setOpaque(false);
            btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                selectedMission    = mName;
                selectedDifficulty = mDiff;
                missionTiles.revalidate(); missionTiles.repaint();
                runMission();
            });
            missionTiles.add(btn);
        }
        missionTiles.revalidate(); missionTiles.repaint();
    }

    private void buildCrewChecks() {
        crewChecks.removeAll();
        for (StreetUnit u : gs.getHideout().getCrew()) {
            crewChecks.add(styledCB(u.getName() + " [" + u.getClass().getSimpleName() + "]", true));
        }
        crewChecks.revalidate(); crewChecks.repaint();
    }

    private void buildItemChecks() {
        itemChecks.removeAll();
        for (InventoryItem it : gs.getHideout().getInventory()) {
            itemChecks.add(styledCB(it.getName(), false));
        }
        itemChecks.revalidate(); itemChecks.repaint();
    }

    // ── run ───────────────────────────────────────────────────────────────────

    private void runMission() {
        List<StreetUnit> team = new ArrayList<>();
        Component[] cc = crewChecks.getComponents();
        List<StreetUnit> allCrew = gs.getHideout().getCrew();
        for (int i = 0; i < cc.length; i++) {
            if (cc[i] instanceof JCheckBox && ((JCheckBox)cc[i]).isSelected() && i < allCrew.size())
                team.add(allCrew.get(i));
        }
        if (team.isEmpty()) { gs.toast("Select at least one crew member!", GameScreen.FIRE_ORANGE); return; }

        List<InventoryItem> items = new ArrayList<>();
        Component[] ic = itemChecks.getComponents();
        List<InventoryItem> allItems = gs.getHideout().getInventory();
        for (int i = 0; i < ic.length; i++) {
            if (ic[i] instanceof JCheckBox && ((JCheckBox)ic[i]).isSelected() && i < allItems.size())
                items.add(allItems.get(i));
        }

        Operation op = new Operation(team, items, gs.getHideout(), selectedMission, selectedDifficulty);
        lastSuccess = op.run();
        showResult  = true;

        StringBuilder sb = new StringBuilder();
        for (String l : op.getLog()) sb.append(l).append('\n');
        logArea.setText(sb.toString());
        logArea.setCaretPosition(0);

        Map<String,Image> imgs = gs.getImgs();
        Image rImg = imgs.get(lastSuccess ? "success_banner" : "fail_banner");
        if (rImg != null) resultLbl.setIcon(new ImageIcon(rImg.getScaledInstance(380,74,Image.SCALE_SMOOTH)));

        gs.refreshStats();

        // rebuild crew checks (HP may have changed)
        buildCrewChecks();
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private JCheckBox styledCB(String text, boolean selected) {
        JCheckBox cb = new JCheckBox(text, selected);
        cb.setFont(new Font("SansSerif",Font.PLAIN,12));
        cb.setForeground(GameScreen.TEXT_BRIGHT);
        cb.setOpaque(false);
        return cb;
    }

    private JLabel sectionTitle(String t) {
        JLabel l = new JLabel(t);
        l.setFont(new Font("SansSerif",Font.BOLD,18));
        l.setForeground(GameScreen.FIRE_AMBER);
        l.setBorder(BorderFactory.createEmptyBorder(0,0,5,0));
        return l;
    }
}
