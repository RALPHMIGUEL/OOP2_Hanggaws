package com.syndicate;

public class Brawler extends StreetUnit {
    public Brawler(String name) {
        super(name, 120, "Hideout", 5, 8);
    }

    @Override
    public void executeStrategy(Hideout hideout, Operation operation) {
        // Brawler contributes raw power to success but raises heat
        operation.addSuccessModifier(15);
        operation.addHeat(10);
        System.out.println(getName() + " charges in and brawls enemies.");
    }
}
