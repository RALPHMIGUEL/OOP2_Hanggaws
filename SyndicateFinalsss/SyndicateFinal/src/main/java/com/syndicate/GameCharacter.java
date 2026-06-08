package com.syndicate;

public abstract class GameCharacter {
    private String name;
    private int health;
    private String location;

    public GameCharacter(String name, int health, String location) {
        this.name = name;
        this.health = health;
        this.location = location;
    }

    public String getName() { return name; }
    public int getHealth() { return health; }
    public String getLocation() { return location; }

    protected void setHealth(int health) { this.health = health; }
    public void takeDamage(int dmg) { this.health = Math.max(0, this.health - dmg); }
    public void heal(int amount) { this.health += amount; }
    public boolean isAlive() { return this.health > 0; }
    public void setLocation(String location) { this.location = location; }

    @Override
    public String toString() {
        return String.format("%s (HP:%d) @%s", name, health, location);
    }
}
