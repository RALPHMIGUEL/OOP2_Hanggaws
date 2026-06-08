package com.syndicate;

public abstract class LawEnforcement {
    protected String name;
    protected int power;

    public LawEnforcement(String name, int power) {
        this.name = name;
        this.power = power;
    }

    public abstract int attack();

    public String getName() { return name; }
    public int getPower() { return power; }
}
