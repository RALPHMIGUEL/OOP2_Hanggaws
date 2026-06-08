package com.syndicate;

public class Firearm extends InventoryItem implements WeaponSystem {
    private int power;
    private int noise;

    public Firearm(String name, int power, int noise) {
        super(name, 25.0);
        this.power = power;
        this.noise = noise;
    }

    @Override
    public void executeStrategy(Hideout hideout, Operation operation) {
        operation.addSuccessModifier(power);
        operation.addHeat(noise);
        System.out.println(getName() + " adds firepower.");
    }

    @Override
    public int firePower() { return power; }

    @Override
    public int noiseLevel() { return noise; }
}
