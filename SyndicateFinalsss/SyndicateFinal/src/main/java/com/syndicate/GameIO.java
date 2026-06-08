package com.syndicate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GameIO {
    public static void save(Hideout h, String path) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(path))) {
            w.write("FUNDS:" + h.getFunds() + "\n");
            w.write("HEAT:" + h.getHeat() + "\n");
            for (StreetUnit s : h.getCrew()) {
                w.write("CREW:" + s.getName() + "|" + s.getClass().getSimpleName() + "|" + s.getHealth() + "\n");
            }
            for (InventoryItem it : h.getInventory()) {
                w.write("INV:" + it.getName() + "|" + it.getClass().getSimpleName() + "\n");
            }
        }
    }

    public static Hideout load(String path) throws IOException {
        Hideout h = new Hideout();
        try (BufferedReader r = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = r.readLine()) != null) {
                if (line.startsWith("FUNDS:")) h.setFunds(Integer.parseInt(line.substring(6)));
                else if (line.startsWith("HEAT:")) h.setHeat(Integer.parseInt(line.substring(5)));
                else if (line.startsWith("CREW:")) {
                    String[] parts = line.substring(5).split("\\|");
                    if (parts.length >= 3) {
                        String name = parts[0];
                        String type = parts[1];
                        int hp = Integer.parseInt(parts[2]);
                        StreetUnit s = createUnit(type, name);
                        if (s != null) s.setLocation("Hideout");
                        if (s != null) {
                            s.takeDamage(s.getHealth()-hp); // adjust health to saved hp
                            h.recruit(s);
                        }
                    }
                } else if (line.startsWith("INV:")) {
                    String[] parts = line.substring(4).split("\\|");
                    if (parts.length >= 2) {
                        String name = parts[0];
                        String type = parts[1];
                        InventoryItem it = createItem(type, name);
                        if (it != null) h.addItem(it);
                    }
                }
            }
        }
        return h;
    }

    private static StreetUnit createUnit(String type, String name) {
        switch (type) {
            case "Brawler": return new Brawler(name);
            case "Hacker": return new Hacker(name);
            case "Driver": return new Driver(name);
            default: return null;
        }
    }

    private static InventoryItem createItem(String type, String name) {
        switch (type) {
            case "Firearm": return new Firearm(name, 12, 8);
            case "BurnerPhone": return new BurnerPhone();
            case "Medkit": return new Medkit(10);
            case "Explosive": return new Explosive();
            default: return null;
        }
    }
}
