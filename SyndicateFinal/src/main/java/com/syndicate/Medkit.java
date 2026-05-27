package com.syndicate;

public class Medkit extends InventoryItem {
    private int healAmount;

    public Medkit(int healAmount) {
        super("Medkit", 3.0);
        this.healAmount = healAmount;
    }

    @Override
    public void executeStrategy(Hideout hideout, Operation operation) {
        // heals crew a bit after an operation
        for (StreetUnit s : hideout.getCrew()) {
            if (s.isAlive()) s.heal(healAmount);
        }
        System.out.println(getName() + " heals the crew (+" + healAmount + ").");
    }
}
