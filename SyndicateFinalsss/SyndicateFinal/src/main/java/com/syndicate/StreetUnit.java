package com.syndicate;

public abstract class StreetUnit extends GameCharacter implements Committable {
    protected int stealth;
    protected int skill;

    public StreetUnit(String name, int health, String location, int stealth, int skill) {
        super(name, health, location);
        this.stealth = stealth;
        this.skill = skill;
    }

    @Override
    public double calculateRisk() {
        // lower stealth/skill increases risk
        double base = 10.0;
        base += Math.max(0, 20 - stealth);
        base += Math.max(0, 20 - skill) * 0.5;
        return base;
    }

    @Override
    public String getName() { return super.getName(); }
}
