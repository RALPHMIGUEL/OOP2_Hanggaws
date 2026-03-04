import java.util.ArrayList;
import java.util.List;

interface Committable {
    void commitOperation(Operation operation);                                      //Sabellano (3 classes)
}

interface WeaponSystem {
    int attack();
}

abstract class GameCharacter {

    private String name;
    private int health;
    private int attackPower;

    public GameCharacter(String name, int health, int attackPower) {
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    protected int getAttackPower() {
        return attackPower;
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public abstract int attack();                                                       // Sabellano (3 classes)
}

class StreetUnit extends GameCharacter implements Committable {                  //Zafra (3 classes)

    public StreetUnit(String name, int health, int attackPower) {
        super(name, health, attackPower);
    }

    @Override
    public int attack() {
        return getAttackPower();
    }

    @Override
    public void commitOperation(Operation operation) {
        System.out.println(getName() + " is joining operation: " + operation.getOperationName());
    }
}

class Brawler extends StreetUnit {

    public Brawler(String name) {
        super(name, 120, 25);
    }

    @Override
    public int attack() {
        return getAttackPower() + 10; // Extra melee bonus
    }
}

class Hacker extends StreetUnit {

    public Hacker(String name) {
        super(name, 80, 15);
    }

    @Override
    public int attack() {
        System.out.println(getName() + " performs a cyber attack!");
        return getAttackPower() + 5;
    }
}                                                                            //Zafra (3 classes)

abstract class LawEnforcement extends GameCharacter implements WeaponSystem {         //Rabaya(3 classes)

    public LawEnforcement(String name, int health, int attackPower) {
        super(name, health, attackPower);
    }
}

class PatrolOfficer extends LawEnforcement {

    public PatrolOfficer(String name) {
        super(name, 90, 20);
    }

    @Override
    public int attack() {
        System.out.println(getName() + " fires standard pistol!");
        return getAttackPower();
    }
}

class SwatTeam extends LawEnforcement {

    public SwatTeam(String name) {
        super(name, 150, 35);
    }

    @Override
    public int attack() {
        System.out.println(getName() + " launches tactical assault!");
        return getAttackPower() + 20;
    }
}    //Rabaya(3 classes)
