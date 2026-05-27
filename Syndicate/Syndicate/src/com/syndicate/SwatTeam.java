package com.syndicate;

public class SwatTeam extends LawEnforcement {
    public SwatTeam() { super("SWAT Team", 20); }

    @Override
    public int attack() {
        return 15 + (int)(Math.random() * power);
    }
}
