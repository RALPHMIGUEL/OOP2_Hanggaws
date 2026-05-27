import com.syndicate.*;
import com.syndicate.ui.*;
import com.syndicate.util.AssetGenerator;
import com.syndicate.util.AssetLoader;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Syndicate — entry point.
 *
 * On first launch, assets/ PNGs are generated automatically via AssetGenerator
 * (pure Java2D — no external tools or files needed).
 *
 * Screen flow (mirrors Hollow Ascent's Main.java swap pattern):
 *   StartScreen  →  GameScreen (Hideout / Shop / Mission tabs)
 *                          ↘  CreditsScreen  →  StartScreen
 *
 * To compile and run from the project root:
 *   javac -d out $(find src -name "*.java")
 *   java  -cp out Main
 */
public class Main {

    public static void main(String[] args) {

        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Generate PNG assets if assets/ folder is missing or empty
        generateAssetsIfNeeded();

        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setBackground(new Color(8, 1, 10));
            showStartScreen(frame);
            frame.setVisible(true);
        });
    }

    // ── asset bootstrap ───────────────────────────────────────────────────────

    private static void generateAssetsIfNeeded() {
        File dir = AssetLoader.getAssetsDir();
        File marker = new File(dir, "background.png");
        if (!marker.exists()) {
            System.out.println("First run — generating assets...");
            try {
                AssetGenerator.generateAll();
            } catch (Exception e) {
                System.err.println("Asset generation failed: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    // ── screen transitions ────────────────────────────────────────────────────

    private static void showStartScreen(GameFrame frame) {
        StartScreen screen = new StartScreen(() -> showGameScreen(frame, newHideout()));
        swap(frame, screen);
    }

    private static void showGameScreen(GameFrame frame, Hideout hideout) {
        Runnable onCredits = () -> showCreditsScreen(frame, hideout);
        swap(frame, new GameScreen(hideout, onCredits));
    }

    private static void showCreditsScreen(GameFrame frame, Hideout hideout) {
        swap(frame, new CreditsScreen(() -> showStartScreen(frame)));
    }

    /** Replace entire frame content — same pattern as Hollow Ascent. */
    private static void swap(GameFrame frame, JPanel panel) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    /** Starter hideout matching the original SyndicateApp defaults. */
    private static Hideout newHideout() {
        Hideout h = new Hideout();
        h.recruit(new Brawler("Viktor"));
        h.recruit(new Hacker("Echo"));
        h.recruit(new Driver("Luna"));
        h.addItem(new Firearm("Pistol", 12, 8));
        h.addItem(new BurnerPhone());
        return h;
    }
}
