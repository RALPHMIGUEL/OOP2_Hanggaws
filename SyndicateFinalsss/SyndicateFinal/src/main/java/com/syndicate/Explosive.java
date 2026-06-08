package com.syndicate;

public class Explosive extends InventoryItem implements WeaponSystem {
    private int blastPower;
    private int blastNoise;

    public Explosive() {
        super("Explosive", 30.0);
        this.blastPower = 30;
        this.blastNoise = 40;
    }

    @Override
    public void executeStrategy(Hideout hideout, Operation operation) {
        operation.addSuccessModifier(blastPower);
        operation.addHeat(blastNoise);
        System.out.println(getName() + " causes big damage but lots of Heat.");
    }

    @Override
    public int firePower() { return blastPower; }

    @Override
    public int noiseLevel() { return blastNoise; }
}
