package com.syndicate;

public class Hacker extends StreetUnit {
    public Hacker(String name) {
        super(name, 70, "Hideout", 14, 18);
    }

    @Override
    public void executeStrategy(Hideout hideout, Operation operation) {
        // Hacker reduces heat and increases success quietly
        operation.addSuccessModifier(10);
        operation.addHeat(-20);
        System.out.println(getName() + " hacks systems and keeps Heat low.");
    }
}
