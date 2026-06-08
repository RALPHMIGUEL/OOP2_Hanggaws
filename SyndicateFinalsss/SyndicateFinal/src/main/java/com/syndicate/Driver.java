package com.syndicate;

public class Driver extends StreetUnit {
    public Driver(String name) {
        super(name, 90, "Hideout", 10, 12);
    }

    @Override
    public void executeStrategy(Hideout hideout, Operation operation) {
        // Driver improves getaway success
        operation.addSuccessModifier(12);
        operation.addHeat(5);
        System.out.println(getName() + " sets up the perfect getaway.");
    }
}
