package com.syndicate;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs a single operation (mission) and records a log of all events
 * so the GUI can display them in the terminal panel.
 */
public class Operation {

    private final List<StreetUnit>    crew;
    private final List<InventoryItem> items;
    private final Hideout             hideout;

    private double successModifier = 0;
    private int    heatChange      = 0;
    private final String missionName;
    private final int    missionDifficulty;
    private final List<String> log = new ArrayList<>();

    // ── constructors ──────────────────────────────────────────────────────────

    public Operation(List<StreetUnit> crew, List<InventoryItem> items, Hideout hideout) {
        this(crew, items, hideout, "Job", 0);
    }

    public Operation(List<StreetUnit> crew, List<InventoryItem> items,
                     Hideout hideout, String missionName, int difficulty) {
        this.crew              = crew;
        this.items             = items;
        this.hideout           = hideout;
        this.missionName       = missionName;
        this.missionDifficulty = difficulty;
    }

    // ── modifiers (called by Committable strategies) ──────────────────────────

    public void addSuccessModifier(double v) { successModifier += v; }
    public void addHeat(int v)               { heatChange      += v; }

    // ── accessors ─────────────────────────────────────────────────────────────

    public List<String> getLog() { return log; }

    // ── execution ─────────────────────────────────────────────────────────────

    public boolean run() {
        log.clear();
        log.add("═══════════ OPERATION START ═══════════");

        String[] diffNames = {"Easy", "Medium", "Hard"};
        log.add("Mission : " + missionName);
        log.add("Difficulty : " + diffNames[Math.min(missionDifficulty, 2)]);
        log.add("");

        // Base success chance (higher = easier)
        double baseChance = 30.0;
        if (missionDifficulty == 1) baseChance -= 10;
        if (missionDifficulty == 2) baseChance -= 25;

        // Each crew member executes their strategy
        log.add("── CREW ──────────────────────────────");
        for (StreetUnit s : crew) {
            s.executeStrategy(hideout, this);
            log.add("▸ " + s.getName() + " [" + s.getClass().getSimpleName() + "] committed.");
        }

        // Each item executes its strategy
        log.add("");
        log.add("── ITEMS ─────────────────────────────");
        if (items.isEmpty()) {
            log.add("  (no items equipped)");
        } else {
            for (InventoryItem it : items) {
                it.executeStrategy(hideout, this);
                log.add("▸ " + it.getName() + " deployed.");
            }
        }

        // Determine law-enforcement response
        log.add("");
        log.add("── THREAT ASSESSMENT ────────────────");
        int projectedHeat = Math.max(0, hideout.getHeat() + heatChange);
        log.add("Projected heat : " + projectedHeat);

        LawEnforcement enemy;
        int threat = projectedHeat + missionDifficulty * 10;
        if (threat > 60) {
            enemy = new SwatTeam();
        } else {
            enemy = new PatrolOfficer();
        }
        log.add("Encountered    : " + enemy.getName());

        // Final chance calculation
        double chance = Math.max(5, Math.min(95,
                baseChance + successModifier - enemy.getPower()));
        log.add(String.format("Success chance : %.1f%%", chance));

        // Roll
        boolean success = Math.random() * 100 < chance;
        hideout.addHeat(heatChange);

        log.add("");
        log.add("── RESULT ────────────────────────────");
        if (success) {
            int reward = 100 + (int) successModifier;
            hideout.addFunds(reward);
            hideout.addHeat(5);
            log.add("✔ SUCCESS — loot acquired: $" + reward);
            log.add("  Hideout heat +5 (success premium)");
        } else {
            int extraHeat = 10 + enemy.getPower();
            hideout.addHeat(extraHeat);
            log.add("✘ FAILED — retreating under fire.");
            log.add("  Hideout heat +" + extraHeat);
            for (StreetUnit s : crew) {
                int dmg = 5 + enemy.getPower() / 2;
                s.takeDamage(dmg);
                log.add("  " + s.getName() + " took " + dmg + " damage  (HP: " + s.getHealth() + ")");
            }
        }

        log.add("");
        log.add("═══════════  OPERATION END  ═══════════");
        return success;
    }
}
