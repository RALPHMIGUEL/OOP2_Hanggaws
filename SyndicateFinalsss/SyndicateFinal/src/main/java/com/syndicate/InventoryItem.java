package com.syndicate;

public abstract class InventoryItem implements Committable {
    protected String name;
    protected double baseRisk;

    public InventoryItem(String name, double baseRisk) {
        this.name = name;
        this.baseRisk = baseRisk;
    }

    @Override
    public double calculateRisk() { return baseRisk; }

    @Override
    public String getName() { return name; }
}
