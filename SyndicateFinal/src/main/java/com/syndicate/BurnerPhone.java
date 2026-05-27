package com.syndicate;

public class BurnerPhone extends InventoryItem {
    public BurnerPhone() { super("Burner Phone", 2.0); }

    @Override
    public void executeStrategy(Hideout hideout, Operation operation) {
        // Lowers heat moderately
        operation.addHeat(-15);
        operation.addSuccessModifier(5);
        System.out.println(getName() + " helps cover traces and reduces Heat.");
    }
}
