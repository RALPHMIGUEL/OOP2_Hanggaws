package com.syndicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SyndicateApp {
    public static void main(String[] args) {
        Hideout hideout = new Hideout();
        // starter crew
        hideout.recruit(new Brawler("Viktor"));
        hideout.recruit(new Hacker("Echo"));
        hideout.recruit(new Driver("Luna"));

        // starter inventory
        hideout.addItem(new Firearm("Pistol", 12, 8));
        hideout.addItem(new BurnerPhone());

        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n--- Syndicate Menu ---");
            System.out.println("1) View Hideout");
            System.out.println("2) Shop / Recruit");
            System.out.println("3) Save Game");
            System.out.println("4) Load Game");
            System.out.println("5) Run Mission");
            System.out.println("6) Quit");
            System.out.print("Choose> ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1": hideout.status(); break;
                case "2": Shop.openShop(hideout, sc); break;
                case "3":
                    try { GameIO.save(hideout, "save.txt"); System.out.println("Saved to save.txt"); }
                    catch (Exception e) { System.out.println("Save failed: " + e.getMessage()); }
                    break;
                case "4":
                    try { Hideout loaded = GameIO.load("save.txt"); hideout = loaded; System.out.println("Game loaded."); }
                    catch (Exception e) { System.out.println("Load failed: " + e.getMessage()); }
                    break;
                case "5":
                    if (hideout.getCrew().isEmpty()) { System.out.println("No crew to send."); break; }
                    System.out.println("Choose mission:\n1) Easy Job\n2) Heist (medium)\n3) Bank Robbery (hard)");
                    System.out.print("Mission> ");
                    String m = sc.nextLine().trim();
                    String missionName = "Easy Job"; int difficulty = 0;
                    if (m.equals("2")) { missionName = "Heist"; difficulty = 1; }
                    else if (m.equals("3")) { missionName = "Bank Robbery"; difficulty = 2; }

                    // select team
                    List<StreetUnit> team = new ArrayList<>();
                    System.out.println("Select crew indices (comma) from:");
                    for (int i=0;i<hideout.getCrew().size();i++) System.out.println(i + ") " + hideout.getCrew().get(i));
                    System.out.print("Indices> ");
                    String idxs = sc.nextLine().trim();
                    for (String p : idxs.split(",")) {
                        try { int ii = Integer.parseInt(p.trim()); if (ii>=0 && ii < hideout.getCrew().size()) team.add(hideout.getCrew().get(ii)); }
                        catch (Exception ex) {}
                    }
                    if (team.isEmpty()) { System.out.println("No valid team selected."); break; }

                    // select items
                    List<InventoryItem> items = new ArrayList<>();
                    if (!hideout.getInventory().isEmpty()) {
                        System.out.println("Select item indices (comma) from:");
                        for (int i=0;i<hideout.getInventory().size();i++) System.out.println(i + ") " + hideout.getInventory().get(i).getName());
                        System.out.print("Item indices> ");
                        String itx = sc.nextLine().trim();
                        for (String p : itx.split(",")) {
                            try { int ii = Integer.parseInt(p.trim()); if (ii>=0 && ii < hideout.getInventory().size()) items.add(hideout.getInventory().get(ii)); }
                            catch (Exception ex) {}
                        }
                    }

                    Operation op = new Operation(team, items, hideout, missionName, difficulty);
                    op.run();
                    break;
                case "6": running = false; break;
                default: System.out.println("Unknown option.");
            }
        }

        sc.close();
        System.out.println("Goodbye.");
    }
}
