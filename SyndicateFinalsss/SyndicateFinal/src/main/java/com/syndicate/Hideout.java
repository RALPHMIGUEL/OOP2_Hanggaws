package com.syndicate;

import java.util.ArrayList;
import java.util.List;

public class Hideout {
    private List<StreetUnit> crew = new ArrayList<>();
    private List<InventoryItem> inventory = new ArrayList<>();
    private int heat = 10;
    private int funds = 500;

    public void recruit(StreetUnit s) { crew.add(s); }
    public void addItem(InventoryItem i) { inventory.add(i); }
    public List<StreetUnit> getCrew() { return crew; }
    public List<InventoryItem> getInventory() { return inventory; }
    public int getHeat() { return heat; }
    public void addHeat(int delta) { heat = Math.max(0, heat + delta); }
    public int getFunds() { return funds; }
    public void addFunds(int amt) { funds += amt; }

    public void removeFunds(int amt) { funds = Math.max(0, funds - amt); }
    public void clearCrew() { crew.clear(); }
    public void clearInventory() { inventory.clear(); }
    public void setHeat(int h) { heat = Math.max(0, h); }
    public void setFunds(int f) { funds = f; }

    public Operation prepareOperation(List<StreetUnit> team, List<InventoryItem> items) {
        return new Operation(team, items, this);
    }

    public void status() {
        System.out.println("--- Hideout Status ---");
        System.out.println("Funds: $" + funds + " | Heat: " + heat);
        System.out.println("Crew:");
        for (StreetUnit s : crew) System.out.println(" - " + s);
        System.out.println("Inventory:");
        for (InventoryItem i : inventory) System.out.println(" - " + i.getName());
        System.out.println("----------------------");
    }
}
