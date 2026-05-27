package com.syndicate;

public class PatrolOfficer extends LawEnforcement {
    public PatrolOfficer() { super("Patrol Officer", 8); }

    @Override
    public int attack() {
        return 6 + (int)(Math.random() * power);
    }
}
