package com.syndicate;

import java.util.List;

public class Operation {
    private List<StreetUnit> crew;
    private List<InventoryItem> items;
    private Hideout hideout;

    private double successModifier = 0;
    private int heatChange = 0;
    private String missionName = "Unknown";
    private int missionDifficulty = 0; // 0 easy, 1 medium, 2 hard

    public Operation(List<StreetUnit> crew, List<InventoryItem> items, Hideout hideout) {
        this(crew, items, hideout, "Job", 0);
    }

    public Operation(List<StreetUnit> crew, List<InventoryItem> items, Hideout hideout, String missionName, int difficulty) {
        this.crew = crew;
        this.items = items;
        this.hideout = hideout;
        this.missionName = missionName;
        this.missionDifficulty = difficulty;
    }

    public void addSuccessModifier(double v) { successModifier += v; }
    public void addHeat(int v) { heatChange += v; }

    public boolean run() {
        System.out.println("--- Operation Start ---");
        double baseChance = 30.0;
        // mission difficulty modifier
        if (missionDifficulty == 1) baseChance -= 10; // medium
        if (missionDifficulty == 2) baseChance -= 25; // hard

        System.out.println("Mission: " + missionName + " (difficulty: " + missionDifficulty + ")");

        // Each crew member and item executes its strategy
        for (StreetUnit s : crew) s.executeStrategy(hideout, this);
        for (InventoryItem it : items) it.executeStrategy(hideout, this);

        // heat effect
        int projectedHeat = Math.max(0, hideout.getHeat() + heatChange);
        System.out.println("Heat after operation adjustments: " + projectedHeat);

        // Law enforcement reaction
        // enemy selection takes into account heat and mission difficulty
        LawEnforcement enemy;
        int threat = projectedHeat + missionDifficulty * 10;
        if (threat > 60) enemy = new SwatTeam(); else enemy = new PatrolOfficer();
        System.out.println("Encountered: " + enemy.getName());

        // Add enemy difficulty
        double enemyPenalty = enemy.getPower();

        double chance = baseChance + successModifier - enemyPenalty;
        chance = Math.max(5, Math.min(95, chance));

        System.out.printf("Computed success chance: %.1f%%\n", chance);
        boolean success = Math.random() * 100 < chance;

        // Apply heat changes to hideout
        hideout.addHeat(heatChange);

        if (success) {
            System.out.println("Operation succeeded! Loot acquired.");
            hideout.addFunds(100 + (int)successModifier);
            hideout.addHeat(5);
        } else {
            System.out.println("Operation failed. Retreating... ");
            hideout.addHeat(10 + enemy.getPower());
            // casualties: some crew take damage
            for (StreetUnit s : crew) s.takeDamage(5 + enemy.getPower()/2);
        }

        System.out.println("--- Operation End ---");
        return success;
    }
}
