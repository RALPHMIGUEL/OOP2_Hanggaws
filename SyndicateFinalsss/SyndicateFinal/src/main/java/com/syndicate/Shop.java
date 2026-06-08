package com.syndicate;

import java.util.Scanner;

public class Shop {
    public static void openShop(Hideout hideout, Scanner sc) {
        boolean inShop = true;
        while (inShop) {
            System.out.println("\n--- Shop ---");
            System.out.println("1) Recruit Hacker ($150)");
            System.out.println("2) Recruit Driver ($120)");
            System.out.println("3) Buy Medkit ($40)");
            System.out.println("4) Buy Explosive ($150)");
            System.out.println("5) Exit Shop");
            System.out.print("Choice> ");
            String c = sc.nextLine().trim();
            switch (c) {
                case "1":
                    if (hideout.getFunds() >= 150) { hideout.recruit(new Hacker("Hacker" + (hideout.getCrew().size()+1))); hideout.removeFunds(150); System.out.println("Hacker recruited."); }
                    else System.out.println("Not enough funds.");
                    break;
                case "2":
                    if (hideout.getFunds() >= 120) { hideout.recruit(new Driver("Driver" + (hideout.getCrew().size()+1))); hideout.removeFunds(120); System.out.println("Driver recruited."); }
                    else System.out.println("Not enough funds.");
                    break;
                case "3":
                    if (hideout.getFunds() >= 40) { hideout.addItem(new Medkit(15)); hideout.removeFunds(40); System.out.println("Medkit purchased."); }
                    else System.out.println("Not enough funds.");
                    break;
                case "4":
                    if (hideout.getFunds() >= 150) { hideout.addItem(new Explosive()); hideout.removeFunds(150); System.out.println("Explosive purchased."); }
                    else System.out.println("Not enough funds.");
                    break;
                case "5": inShop = false; break;
                default: System.out.println("Unknown option.");
            }
        }
    }
}
