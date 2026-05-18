import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.*;

// --- PROJECTILE CLASS ---
class Bullet {
    double x, y;
    double dirX, dirY;
    int speed = 10;
    int size = 8;
    boolean isEnemyBullet;

    public Bullet(double x, double y, double targetX, double targetY, int speed, boolean isEnemyBullet) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.isEnemyBullet = isEnemyBullet;

        double dx = targetX - x;
        double dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist < 1) {
            dirX = 0;
            dirY = -speed;
        } else {
            dirX = dx / dist * speed;
            dirY = dy / dist * speed;
        }
    }

    public void update() {
        x += dirX;
        y += dirY;
    }

    public void draw(Graphics g) {
        g.setColor(isEnemyBullet ? new Color(255, 69, 0) : Color.YELLOW);
        g.fillOval((int)x, (int)y, size, size);
    }

    public Rectangle getBounds() {
        return new Rectangle((int)x, (int)y, size, size);
    }
}

// --- ENEMY / SECURITY CLASS ---
class Enemy {
    int x, y, width = 40, height = 40;
    int health;
    int maxHealth;
    boolean alive = true;
    boolean isSecurity = false;
    int shootCooldown = 30;
    int level; // 1 or 2

    public Enemy(int x, int y, boolean isSecurity, int level) {
        this.x = x;
        this.y = y;
        this.isSecurity = isSecurity;
        this.level = level;

        if (isSecurity) {
            if (level == 3) this.health = 14;
            else if (level == 2) this.health = 7;
            else this.health = 4;
        } else {
            if (level == 3) this.health = 8;
            else if (level == 2) this.health = 5;
            else this.health = 3;
        }
        this.maxHealth = this.health;
    }

    public void updateAI(Player player, ArrayList<Bullet> bullets) {
        if (!alive || !isSecurity) return;

        if (shootCooldown > 0) {
            shootCooldown--;
        } else {
            double targetX = player.x + player.width / 2.0;
            double targetY = player.y + player.height / 2.0;
            int shotSpeed = (level == 3) ? 12 : 8;
            bullets.add(new Bullet(this.x + 16, this.y + 16, targetX, targetY, shotSpeed, true));
            if (level == 3) {
                bullets.add(new Bullet(this.x + 16, this.y + 16, targetX + 20, targetY, shotSpeed, true));
            }
            int baseCooldown = (level == 3) ? 20 : (level == 2) ? 25 : 50;
            shootCooldown = baseCooldown + (int)(Math.random() * 20);
        }
    }

    public void draw(Graphics g) {
        if (!alive) return;

        Color bodyColor;
        if (isSecurity) {
            if (level == 3) bodyColor = new Color(120, 0, 0);
            else bodyColor = (level == 2) ? new Color(80, 0, 80) : new Color(25, 25, 112); // purple elite for L2
        } else {
            bodyColor = (level == 2) ? new Color(160, 0, 0) : new Color(200, 50, 50);
        }
        g.setColor(bodyColor);
        g.fillRect(x, y, width, height);

        // Visor
        g.setColor(isSecurity ? (level == 2 ? Color.MAGENTA : Color.CYAN) : Color.BLACK);
        g.fillRect(x + 5, y + 10, 30, 5);

        // Health bar background
        g.setColor(Color.RED);
        g.fillRect(x, y - 10, width, 5);
        g.setColor(Color.GREEN);
        g.fillRect(x, y - 10, (width * health) / maxHealth, 5);

        // Level 2 badge
        if (level == 2) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD, 8));
            g.drawString("II", x + 15, y + 30);
        }
    }

    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
}

// --- BUILDING CLASS (improved visuals) ---
class Building {
    int x, y, width, height;
    String name;
    Color color;
    Rectangle doorway;

    public Building(int x, int y, int width, int height, String name, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.name = name;
        this.color = color;
        this.doorway = new Rectangle(x + (width / 2) - 15, y + height - 5, 30, 12);
    }

    public void draw(Graphics g) {
        // === Main building body ===
        g.setColor(color);
        g.fillRect(x, y, width, height);

        // === Brick texture (horizontal lines) ===
        g.setColor(color.darker());
        for (int row = y + 20; row < y + height - 10; row += 12) {
            g.drawLine(x + 2, row, x + width - 2, row);
        }
        // Offset brick columns
        for (int row = y + 20; row < y + height - 10; row += 24) {
            for (int col = x + 2; col < x + width - 2; col += 20) {
                g.drawLine(col, row, col, row + 12);
            }
        }
        for (int row = y + 32; row < y + height - 10; row += 24) {
            for (int col = x + 12; col < x + width - 2; col += 20) {
                g.drawLine(col, row, col, row + 12);
            }
        }

        // === Roof / cornice ===
        g.setColor(color.darker().darker());
        g.fillRect(x - 4, y, width + 8, 18);
        g.setColor(color.brighter());
        g.drawLine(x - 4, y, x + width + 4, y);
        g.drawLine(x - 4, y + 18, x + width + 4, y + 18);

        // === Windows ===
        drawWindows(g);

        // === Door ===
        g.setColor(new Color(101, 67, 33));
        g.fillRect(doorway.x, doorway.y, doorway.width, doorway.height);
        // Door frame
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(doorway.x - 1, doorway.y - 1, doorway.width + 2, doorway.height + 2);
        // Door handle
        g.setColor(new Color(218, 165, 32));
        g.fillOval(doorway.x + doorway.width - 7, doorway.y + doorway.height / 2 - 2, 4, 4);

        // === Building name label ===
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(x + 8, y + 20, width - 16, 16);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.drawString(name, x + 12, y + 32);

        // === Building outline ===
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width, height);
    }

    private void drawWindows(Graphics g) {
        // Two rows of windows
        int[] windowRowsY = { y + 42, y + 65 };
        int windowsPerRow = Math.max(1, (width - 30) / 32);
        int winW = 20, winH = 16;
        int startX = x + 15;

        for (int ry : windowRowsY) {
            if (ry + winH >= y + height - 15) continue; // Don't overlap door area
            for (int wi = 0; wi < windowsPerRow; wi++) {
                int wx = startX + wi * 32;
                if (wx + winW > x + width - 10) break;
                // Window frame
                g.setColor(new Color(60, 60, 60));
                g.fillRect(wx - 1, ry - 1, winW + 2, winH + 2);
                // Glass with slight blue tint
                g.setColor(new Color(173, 216, 230, 200));
                g.fillRect(wx, ry, winW, winH);
                // Windowpane cross
                g.setColor(new Color(255, 255, 255, 120));
                g.drawLine(wx + winW / 2, ry, wx + winW / 2, ry + winH);
                g.drawLine(wx, ry + winH / 2, wx + winW, ry + winH / 2);
            }
        }
    }

    public Rectangle getSolidBounds() {
        return new Rectangle(x, y, width, height - 5);
    }
}

// --- PLAYER CLASS ---
class Player {
    String name;
    int x, y, speed = 4;
    int width = 32, height = 32;
    Color color;
    String direction = "DOWN";
    int health = 10;
    int maxHealth = 10;
    int wallet = 0;
    boolean hasCash = false; // Carrying loot from vault
    int weaponLevel = 1;
    int armorLevel = 1;

    public Player(String name, Color color, int x, int y) {
        this.name = name;
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public int getWeaponDamage() {
        return 1 + (weaponLevel - 1) * 1;
    }

    public int getDamageReduction() {
        return armorLevel - 1;
    }

    public String getWeaponName() {
        switch (weaponLevel) {
            case 2: return "Pistol";
            case 3: return "Rifle";
            default: return "Fists";
        }
    }

    public String getArmorName() {
        switch (armorLevel) {
            case 2: return "Vest";
            case 3: return "Heavy";
            default: return "None";
        }
    }

    public void upgradeWeapon(int level) {
        weaponLevel = Math.max(weaponLevel, level);
    }

    public void upgradeArmor(int level) {
        armorLevel = Math.max(armorLevel, level);
    }

    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
        g.setColor(Color.WHITE);
        if (direction.equals("UP"))    { g.fillRect(x+4, y+4, 6, 6);  g.fillRect(x+22, y+4, 6, 6); }
        else if (direction.equals("DOWN"))  { g.fillRect(x+4, y+22, 6, 6); g.fillRect(x+22, y+22, 6, 6); }
        else if (direction.equals("LEFT"))  { g.fillRect(x+4, y+8, 6, 16); }
        else                                { g.fillRect(x+22, y+8, 6, 16); }

        // Cash bag icon if carrying loot
        if (hasCash) {
            g.setColor(new Color(218, 165, 32));
            g.fillOval(x + 10, y - 14, 12, 12);
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 8));
            g.drawString("$", x + 13, y - 5);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 11));
        g.drawString(name, x - 5, y - 18);
    }

    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
}

// --- MAIN GAMEPANEL ENGINE ---
class GamePanel extends JPanel implements ActionListener {
    private Player player;
    private ArrayList<Bullet> bullets = new ArrayList<>();
    private ArrayList<Enemy> townEnemies = new ArrayList<>();
    private ArrayList<Enemy> bankSecurity = new ArrayList<>();
    private ArrayList<Building> townBuildings = new ArrayList<>();

    private Timer gameLoop;
    private Set<Integer> activeKeys = new HashSet<>();
    private int shootCooldown = 0;
    private int mouseX = 0;
    private int mouseY = 0;
    private String shopMessage = "";
    private Rectangle shopButton1 = new Rectangle(180, 130, 220, 40);
    private Rectangle shopButton2 = new Rectangle(180, 190, 220, 40);
    private Rectangle shopButton3 = new Rectangle(180, 250, 220, 40);
    private Rectangle shopButton4 = new Rectangle(180, 310, 220, 40);
    private Rectangle restartButton = new Rectangle(330, 360, 140, 40);

    private enum LocationState { TOWN, HOUSE_INTERIOR, BANK_INTERIOR, SHOP }
    private LocationState currentLocation = LocationState.TOWN;

    // Heist & level state
    private Rectangle bankVault = new Rectangle(360, 100, 80, 60);
    private boolean vaultHasCash = true;
    private boolean alarmTriggered = false;
    private int currentLevel = 1;
    private boolean heistCompletePopup = false;
    private long heistCompleteTime = 0;
    private int bankWave = 0;
    private boolean gameCompletePopup = false;

    // House table rectangle (where player drops cash)
    private Rectangle houseTable = new Rectangle(200, 150, 80, 60);

    private int returnTownX, returnTownY;
    private Rectangle interiorExitMat = new Rectangle(375, 520, 50, 40);

    public GamePanel(String name, Color choice) {
        this.player = new Player(name, choice, 200, 350);
        this.setFocusable(true);

        townBuildings.add(new Building(100, 80, 160, 130, "MY HOUSE", new Color(160, 100, 40)));
        townBuildings.add(new Building(100, 320, 160, 130, "ARMOR SHOP", new Color(80, 120, 80)));
        townBuildings.add(new Building(480, 70, 200, 150, "CITY BANK", new Color(90, 110, 130)));

        currentLocation = LocationState.HOUSE_INTERIOR;
        returnTownX = 165;
        returnTownY = 230;
        player.x = 200;
        player.y = 300;

        spawnTownEnemies(1);

        this.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) { activeKeys.add(e.getKeyCode()); }
            public void keyReleased(KeyEvent e) { activeKeys.remove(e.getKeyCode()); }
        });

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (currentLocation == LocationState.SHOP) {
                        handleShopClick(e.getX(), e.getY());
                    } else if (player.health <= 0) {
                        handleRestartClick(e.getX(), e.getY());
                    } else {
                        shootAt(e.getX(), e.getY());
                    }
                }
            }
        });

        gameLoop = new Timer(16, this);
        gameLoop.start();
    }

    private void spawnTownEnemies(int level) {
        townEnemies.clear();
        townEnemies.add(new Enemy(520, 480, false, level));
        townEnemies.add(new Enemy(620, 420, false, level));
        if (level >= 2) {
            townEnemies.add(new Enemy(560, 350, false, level));
            townEnemies.add(new Enemy(680, 450, false, level));
        }
        if (level >= 3) {
            townEnemies.add(new Enemy(540, 280, false, level));
            townEnemies.add(new Enemy(700, 320, false, level));
        }
    }

    private void resetBankForNextLevel() {
        vaultHasCash = true;
        alarmTriggered = false;
        bankSecurity.clear();
        bullets.clear();
        player.hasCash = false;
        bankWave = 0;
    }

    // ===== DRAWING =====

    private void drawTownOverworld(Graphics g) {
        // Grass base
        g.setColor(new Color(56, 142, 60));
        g.fillRect(0, 0, getWidth(), getHeight());

        // Grass texture patches
        g.setColor(new Color(46, 125, 50));
        for (int gx = 0; gx < getWidth(); gx += 40) {
            for (int gy = 0; gy < getHeight(); gy += 40) {
                if ((gx / 40 + gy / 40) % 2 == 0) g.fillRect(gx, gy, 40, 40);
            }
        }

        // Roads
        g.setColor(new Color(80, 80, 80));
        g.fillRect(0, 240, getWidth(), 60);
        g.fillRect(375, 0, 60, getHeight());

        // Road center markings
        g.setColor(new Color(255, 235, 59));
        for (int rx = 0; rx < getWidth(); rx += 50) g.fillRect(rx, 267, 30, 6);
        for (int ry = 0; ry < getHeight(); ry += 50) g.fillRect(401, ry, 6, 30);

        // Sidewalk kerbs
        g.setColor(new Color(180, 180, 180));
        g.fillRect(0, 235, getWidth(), 7);
        g.fillRect(0, 297, getWidth(), 7);
        g.fillRect(370, 0, 7, getHeight());
        g.fillRect(433, 0, 7, getHeight());

        // Level indicator on road
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.drawString("LEVEL " + currentLevel, 10, getHeight() - 10);

        for (Building b : townBuildings) b.draw(g);
        for (Enemy en : townEnemies) en.draw(g);
        for (Bullet b : bullets) b.draw(g);
    }

    private void drawHouseInterior(Graphics g) {
        // Floor
        g.setColor(new Color(200, 170, 120));
        g.fillRect(100, 50, 600, 500);

        // Wallpaper
        g.setColor(new Color(230, 210, 180));
        g.fillRect(100, 50, 600, 100);
        // Wallpaper pattern
        g.setColor(new Color(210, 185, 150));
        for (int wx = 100; wx < 700; wx += 30) {
            g.drawLine(wx, 50, wx, 150);
        }

        // Baseboard
        g.setColor(new Color(160, 120, 80));
        g.fillRect(100, 145, 600, 8);

        // Walls
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 100, getHeight());
        g.fillRect(700, 0, getWidth() - 700, getHeight());
        g.fillRect(100, 0, 600, 50);
        g.fillRect(100, 550, 600, getHeight() - 550);

        // Exit mat
        g.setColor(new Color(180, 30, 30));
        g.fillRect(interiorExitMat.x, interiorExitMat.y, interiorExitMat.width, interiorExitMat.height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 9));
        g.drawString("EXIT", interiorExitMat.x + 8, interiorExitMat.y + 15);

        // Table (improved)
        drawHouseTable(g);

        // Couch
        g.setColor(new Color(70, 100, 160));
        g.fillRect(450, 200, 160, 70);
        g.setColor(new Color(50, 75, 130));
        g.fillRect(450, 200, 160, 18);
        g.fillRect(450, 200, 18, 70);
        g.fillRect(592, 200, 18, 70);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        g.drawString("Couch", 505, 245);

        // Lamp
        g.setColor(new Color(80, 80, 80));
        g.fillRect(420, 330, 10, 50);
        g.setColor(new Color(255, 220, 100));
        int[] lx = {410, 440, 425}; int[] ly = {330, 330, 310};
        g.fillPolygon(lx, ly, 3);
    }

    private void drawHouseTable(Graphics g) {
        // Table legs
        g.setColor(new Color(80, 50, 10));
        g.fillRect(houseTable.x + 5,  houseTable.y + houseTable.height - 5, 8, 15);
        g.fillRect(houseTable.x + houseTable.width - 13, houseTable.y + houseTable.height - 5, 8, 15);

        // Table surface
        g.setColor(new Color(120, 70, 20));
        g.fillRect(houseTable.x, houseTable.y, houseTable.width, houseTable.height);

        // Shine on table
        g.setColor(new Color(160, 110, 60));
        g.drawLine(houseTable.x + 5, houseTable.y + 4, houseTable.x + houseTable.width - 5, houseTable.y + 4);

        // Table border
        g.setColor(new Color(60, 30, 5));
        g.drawRect(houseTable.x, houseTable.y, houseTable.width, houseTable.height);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.drawString("TABLE", houseTable.x + 20, houseTable.y + 25);

        // Prompt if player has cash
        if (player.hasCash) {
            g.setColor(new Color(255, 220, 0));
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.drawString("[ E ] Drop Cash", houseTable.x - 10, houseTable.y - 8);
        }
    }

    private void drawBankInterior(Graphics g) {
        // Alarm flash
        if (alarmTriggered && (System.currentTimeMillis() / 200) % 2 == 0) {
            g.setColor(new Color(255, 180, 180));
        } else {
            g.setColor(new Color(220, 230, 245));
        }
        g.fillRect(100, 50, 600, 500);

        // Tile floor
        g.setColor(new Color(200, 210, 225, 80));
        for (int tx = 100; tx < 700; tx += 50) {
            for (int ty = 50; ty < 550; ty += 50) {
                g.drawRect(tx, ty, 50, 50);
            }
        }

        // Walls
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 100, getHeight());
        g.fillRect(700, 0, getWidth() - 700, getHeight());
        g.fillRect(100, 0, 600, 50);
        g.fillRect(100, 550, 600, getHeight() - 550);

        // Wall trim
        g.setColor(new Color(100, 120, 150));
        g.fillRect(100, 50, 600, 12);
        g.fillRect(100, 538, 600, 12);

        // Bank counter / teller desks
        g.setColor(new Color(160, 120, 80));
        g.fillRect(160, 230, 200, 25);
        g.fillRect(440, 230, 200, 25);

        // Exit mat
        g.setColor(new Color(180, 30, 30));
        g.fillRect(interiorExitMat.x, interiorExitMat.y, interiorExitMat.width, interiorExitMat.height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 9));
        g.drawString("EXIT", interiorExitMat.x + 8, interiorExitMat.y + 15);

        // Vault
        drawVault(g);

        for (Enemy guard : bankSecurity) guard.draw(g);
        for (Bullet b : bullets) b.draw(g);
    }

    private void drawVault(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // Vault shadow
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillRect(bankVault.x + 4, bankVault.y + 4, bankVault.width, bankVault.height);

        // Vault body
        if (vaultHasCash) {
            g2.setColor(new Color(160, 130, 30));
        } else {
            g2.setColor(new Color(90, 90, 90));
        }
        g2.fillRect(bankVault.x, bankVault.y, bankVault.width, bankVault.height);

        // Vault door frame
        g2.setColor(new Color(50, 50, 50));
        g2.setStroke(new BasicStroke(3));
        g2.draw(new java.awt.geom.RoundRectangle2D.Float(bankVault.x + 5, bankVault.y + 5, bankVault.width - 10, bankVault.height - 10, 8, 8));
        g2.setStroke(new BasicStroke(1));  // reset

        // Vault dial/wheel
        g2.setColor(new Color(200, 200, 200));
        g2.fillOval(bankVault.x + bankVault.width / 2 - 10, bankVault.y + bankVault.height / 2 - 10, 20, 20);
        g2.setColor(Color.DARK_GRAY);
        g2.drawLine(bankVault.x + bankVault.width / 2, bankVault.y + bankVault.height / 2 - 10,
                   bankVault.x + bankVault.width / 2, bankVault.y + bankVault.height / 2);

        // Vault label
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 10));
        g2.drawString(vaultHasCash ? "CASH DEPOT" : "CLEANED OUT",
                     bankVault.x + 4, bankVault.y + bankVault.height - 8);

        // Highlight if player can loot
        if (vaultHasCash && !player.hasCash &&
            player.getBounds().intersects(new Rectangle(bankVault.x - 20, bankVault.y - 20,
                                                        bankVault.width + 40, bankVault.height + 40))) {
            g2.setColor(new Color(255, 215, 0, 180));
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.drawString("[ E ] Loot!", bankVault.x + 5, bankVault.y - 8);
        }
    }

    private void drawShopInterior(Graphics g) {
        g.setColor(new Color(60, 30, 20));
        g.fillRect(100, 50, 600, 500);

        g.setColor(new Color(180, 180, 180));
        g.fillRoundRect(150, 80, 500, 420, 20, 20);
        g.setColor(new Color(40, 40, 40));
        g.drawRoundRect(150, 80, 500, 420, 20, 20);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("ARMOR SHOP", 300, 120);

        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Wallet: $" + player.wallet, 170, 150);
        g.drawString("Current Weapon: " + player.getWeaponName(), 170, 175);
        g.drawString("Current Armor: " + player.getArmorName(), 170, 195);

        drawShopButton(g, shopButton1, "1) Pistol - $5000 (+1 damage)", player.weaponLevel >= 2);
        drawShopButton(g, shopButton2, "2) Rifle  - $10000 (+2 damage)", player.weaponLevel >= 3);
        drawShopButton(g, shopButton3, "3) Vest   - $5000 (-1 damage taken)", player.armorLevel >= 2);
        drawShopButton(g, shopButton4, "4) Heavy Armor - $12000 (-2 damage taken)", player.armorLevel >= 3);

        g.setColor(new Color(255, 220, 120));
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString(shopMessage, 170, 370);

        g.setColor(new Color(255, 255, 255, 180));
        g.drawString("Click a purchase or press E near the exit mat.", 170, 400);

        g.setColor(new Color(180, 30, 30));
        g.fillRect(interiorExitMat.x, interiorExitMat.y, interiorExitMat.width, interiorExitMat.height);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 9));
        g.drawString("EXIT", interiorExitMat.x + 8, interiorExitMat.y + 15);
    }

    private void drawShopButton(Graphics g, Rectangle rect, String text, boolean owned) {
        g.setColor(owned ? new Color(100, 100, 100) : new Color(70, 130, 180));
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);
        g.setColor(Color.WHITE);
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString(text, rect.x + 10, rect.y + 26);
    }

    private void drawAimCursor(Graphics g) {
        g.setColor(new Color(255, 255, 255, 180));
        g.drawOval(mouseX - 12, mouseY - 12, 24, 24);
        g.drawLine(mouseX - 16, mouseY, mouseX + 16, mouseY);
        g.drawLine(mouseX, mouseY - 16, mouseX, mouseY + 16);
    }

    private void drawRestartButton(Graphics g) {
        g.setColor(new Color(150, 20, 20));
        g.fillRoundRect(restartButton.x, restartButton.y, restartButton.width, restartButton.height, 12, 12);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString("Restart", restartButton.x + 35, restartButton.y + 26);
    }

    private void drawGameCompletePopup(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 0, 0, 200));
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(new Color(30, 120, 30));
        g2.fillRoundRect(150, 180, 500, 240, 20, 20);
        g2.setColor(new Color(255, 215, 0));
        g2.setStroke(new BasicStroke(4));
        g2.drawRoundRect(150, 180, 500, 240, 20, 20);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 32));
        g2.drawString("CONGRATULATIONS!", 210, 260);
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        g2.drawString("You finished the game and escaped with the loot.", 210, 310);
        g2.drawString("Thanks for playing!", 210, 340);

        drawRestartButton(g);
    }

    private void handleRestartClick(int x, int y) {
        if (restartButton.contains(x, y)) {
            restartGame();
        }
    }

    private void restartGame() {
        currentLevel = 1;
        currentLocation = LocationState.HOUSE_INTERIOR;
        player.health = player.maxHealth;
        player.wallet = 0;
        player.weaponLevel = 1;
        player.armorLevel = 1;
        player.hasCash = false;
        player.x = 200;
        player.y = 300;
        player.direction = "DOWN";
        shopMessage = "";
        gameCompletePopup = false;
        heistCompletePopup = false;
        spawnTownEnemies(1);
        resetBankForNextLevel();
        bullets.clear();
        bankSecurity.clear();
        activeKeys.clear();
    }

    private void shootAt(int targetX, int targetY) {
        if (shootCooldown > 0 || player.health <= 0) return;

        double centerX = player.x + player.width / 2.0;
        double centerY = player.y + player.height / 2.0;
        int speed = 10 + player.weaponLevel * 2;
        bullets.add(new Bullet(centerX, centerY, targetX, targetY, speed, false));
        shootCooldown = 15;
    }

    private int getLevelCashReward() {
        switch (currentLevel) {
            case 2: return 15000;
            case 3: return 22000;
            default: return 10000;
        }
    }

    private void spawnBankWave(int wave) {
        bankSecurity.clear();
        alarmTriggered = true;
        if (wave == 1) {
            bankSecurity.add(new Enemy(150, 120, true, 3));
            bankSecurity.add(new Enemy(580, 120, true, 3));
        } else if (wave == 2) {
            bankSecurity.add(new Enemy(250, 300, true, 3));
            bankSecurity.add(new Enemy(480, 300, true, 3));
        } else if (wave == 3) {
            Enemy boss = new Enemy(360, 200, true, 3);
            boss.width = 55;
            boss.height = 55;
            bankSecurity.add(boss);
        }
    }

    private void handleShopClick(int x, int y) {
        if (shopButton1.contains(x, y)) {
            if (player.wallet >= 5000 && player.weaponLevel < 2) {
                player.wallet -= 5000;
                player.upgradeWeapon(2);
                shopMessage = "Pistol purchased!";
            } else if (player.weaponLevel >= 2) {
                shopMessage = "Pistol already owned.";
            } else {
                shopMessage = "Not enough cash for Pistol.";
            }
            return;
        }
        if (shopButton2.contains(x, y)) {
            if (player.wallet >= 10000 && player.weaponLevel < 3) {
                player.wallet -= 10000;
                player.upgradeWeapon(3);
                shopMessage = "Rifle purchased!";
            } else if (player.weaponLevel >= 3) {
                shopMessage = "Rifle already owned.";
            } else {
                shopMessage = "Not enough cash for Rifle.";
            }
            return;
        }
        if (shopButton3.contains(x, y)) {
            if (player.wallet >= 5000 && player.armorLevel < 2) {
                player.wallet -= 5000;
                player.upgradeArmor(2);
                shopMessage = "Vest purchased!";
            } else if (player.armorLevel >= 2) {
                shopMessage = "Vest already owned.";
            } else {
                shopMessage = "Not enough cash for Vest.";
            }
            return;
        }
        if (shopButton4.contains(x, y)) {
            if (player.wallet >= 12000 && player.armorLevel < 3) {
                player.wallet -= 12000;
                player.upgradeArmor(3);
                shopMessage = "Heavy Armor purchased!";
            } else if (player.armorLevel >= 3) {
                shopMessage = "Heavy Armor already owned.";
            } else {
                shopMessage = "Not enough cash for Heavy Armor.";
            }
            return;
        }

        if (interiorExitMat.contains(x, y)) {
            currentLocation = LocationState.TOWN;
            player.x = returnTownX;
            player.y = returnTownY;
            bullets.clear();
            activeKeys.clear();
            shopMessage = "";
        }
    }

    // ===== HEIST COMPLETE OVERLAY =====
    private void drawHeistCompletePopup(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        // Semi-transparent overlay
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Popup box
        int pw = 400, ph = 220;
        int px = (getWidth() - pw) / 2;
        int py = (getHeight() - ph) / 2;

        g2.setColor(new Color(20, 20, 50));
        g2.fillRoundRect(px, py, pw, ph, 20, 20);
        g2.setColor(new Color(218, 165, 32));
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(px, py, pw, ph, 20, 20);

        // Trophy icon (simple)
        g2.setColor(new Color(218, 165, 32));
        g2.fillOval(px + 175, py + 20, 50, 50);
        g2.setColor(new Color(180, 130, 20));
        g2.fillRect(px + 190, py + 65, 20, 10);
        g2.fillRect(px + 180, py + 74, 40, 8);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        g2.drawString("HEIST " + (currentLevel) + " COMPLETE!", px + 30, py + 115);

        g2.setColor(new Color(255, 220, 50));
        g2.setFont(new Font("Arial", Font.PLAIN, 16));
        g2.drawString("$10,000 secured. You're a legend.", px + 50, py + 145);

        g2.setColor(Color.CYAN);
        g2.setFont(new Font("Arial", Font.BOLD, 14));
        g2.drawString("Proceeding to Level " + (currentLevel + 1) + "...", px + 100, py + 175);

        // Auto-advance after 3 seconds
        if (System.currentTimeMillis() - heistCompleteTime > 3000) {
            advanceToNextLevel();
        }
    }

    private void advanceToNextLevel() {
        heistCompletePopup = false;
        player.hasCash = false;
        player.health = player.maxHealth; // Restore health for next level
        player.x = 200; player.y = 300;
        player.direction = "DOWN";
        bullets.clear();
        activeKeys.clear();
        if (currentLevel < 3) {
            currentLevel++;
            currentLocation = LocationState.HOUSE_INTERIOR;
            spawnTownEnemies(currentLevel);
            resetBankForNextLevel();
        } else {
            completeGame();
        }
    }

    private void completeGame() {
        gameCompletePopup = true;
        currentLocation = LocationState.TOWN;
        bankSecurity.clear();
        townEnemies.clear();
        resetBankForNextLevel();
    }

    // ===== ACTION PERFORMED =====
    @Override
    public void actionPerformed(ActionEvent e) {
        if (!heistCompletePopup && player.health > 0) {
            updateMovement();
            updateProjectiles();

            if (currentLocation == LocationState.BANK_INTERIOR) {
                for (Enemy guard : bankSecurity) {
                    guard.updateAI(player, bullets);
                }
            }
        }
        if (shootCooldown > 0) shootCooldown--;
        repaint();
    }

    private void updateMovement() {
        int oldX = player.x;
        int oldY = player.y;

        if (activeKeys.contains(KeyEvent.VK_W)) { player.y -= player.speed; player.direction = "UP"; }
        if (activeKeys.contains(KeyEvent.VK_S)) { player.y += player.speed; player.direction = "DOWN"; }
        if (activeKeys.contains(KeyEvent.VK_A)) { player.x -= player.speed; player.direction = "LEFT"; }
        if (activeKeys.contains(KeyEvent.VK_D)) { player.x += player.speed; player.direction = "RIGHT"; }

        if (activeKeys.contains(KeyEvent.VK_SPACE) && shootCooldown <= 0 && currentLocation != LocationState.SHOP) {
            shootAt(player.x + player.width / 2, player.y + player.height / 2);
        }

        // === E key: interact with vault, table, or exit on shop interior ===
        if (activeKeys.contains(KeyEvent.VK_E)) {
            if (currentLocation == LocationState.BANK_INTERIOR && vaultHasCash
                    && player.getBounds().intersects(new Rectangle(bankVault.x - 20, bankVault.y - 20,
                                                                   bankVault.width + 40, bankVault.height + 40))) {
                triggerHeistEvent();
                activeKeys.remove(KeyEvent.VK_E);
            }

            if (currentLocation == LocationState.HOUSE_INTERIOR && player.hasCash
                    && player.getBounds().intersects(new Rectangle(houseTable.x - 20, houseTable.y - 20,
                                                                   houseTable.width + 40, houseTable.height + 40))) {
                // Drop cash on table -> trigger completion!
                player.wallet += getLevelCashReward();
                heistCompletePopup = true;
                heistCompleteTime = System.currentTimeMillis();
                activeKeys.remove(KeyEvent.VK_E);
            }

            if (currentLocation == LocationState.SHOP && player.getBounds().intersects(interiorExitMat)) {
                currentLocation = LocationState.TOWN;
                player.x = returnTownX;
                player.y = returnTownY;
                bullets.clear();
                activeKeys.clear();
                shopMessage = "";
            }
        }

        if (currentLocation == LocationState.TOWN) {
            player.x = Math.max(0, Math.min(player.x, getWidth() - player.width));
            player.y = Math.max(0, Math.min(player.y, getHeight() - player.height));

            for (Building b : townBuildings) {
                if (player.getBounds().intersects(b.doorway)) {
                    returnTownX = b.doorway.x;
                    returnTownY = b.doorway.y + 25;
                    bullets.clear();
                    if (b.name.equals("MY HOUSE")) currentLocation = LocationState.HOUSE_INTERIOR;
                    else if (b.name.equals("CITY BANK")) currentLocation = LocationState.BANK_INTERIOR;
                    else if (b.name.equals("ARMOR SHOP")) currentLocation = LocationState.SHOP;
                    player.x = 385; player.y = 460;
                    activeKeys.clear();
                    return;
                }
                if (player.getBounds().intersects(b.getSolidBounds())) {
                    player.x = oldX; player.y = oldY;
                }
            }
        } else {
            player.x = Math.max(105, Math.min(player.x, 695 - player.width));
            player.y = Math.max(55, Math.min(player.y, 545 - player.height));

            if (player.getBounds().intersects(interiorExitMat)) {
            if (currentLocation == LocationState.BANK_INTERIOR && player.hasCash
                    && bankSecurity.stream().anyMatch(g -> g.alive)) {
                // can't escape until all alarm guards are defeated
                return;
            }
            currentLocation = LocationState.TOWN;
            player.x = returnTownX;
            player.y = returnTownY;
            bullets.clear();
            activeKeys.clear();
        }
        }
    }

    private void triggerHeistEvent() {
        vaultHasCash = false;
        alarmTriggered = true;
        player.hasCash = true; // Player now carries the loot

        if (currentLevel == 3) {
            bankWave = 1;
            spawnBankWave(bankWave);
        } else {
            bankSecurity.add(new Enemy(150, 120, true, currentLevel));
            bankSecurity.add(new Enemy(580, 120, true, currentLevel));
            bankSecurity.add(new Enemy(250, 300, true, currentLevel));
            bankSecurity.add(new Enemy(480, 300, true, currentLevel));
            if (currentLevel == 2) {
                bankSecurity.add(new Enemy(350, 200, true, 2));
                bankSecurity.add(new Enemy(160, 400, true, 2));
            }
        }
    }

    private void updateProjectiles() {
        Iterator<Bullet> bIter = bullets.iterator();
        while (bIter.hasNext()) {
            Bullet b = bIter.next();
            b.update();

            if (b.x < 0 || b.x > getWidth() || b.y < 0 || b.y > getHeight()) {
                bIter.remove();
                continue;
            }

            if (b.isEnemyBullet) {
                if (b.getBounds().intersects(player.getBounds())) {
                    int damage = Math.max(1, 1 - player.getDamageReduction());
                    player.health -= damage;
                    bIter.remove();
                    continue;
                }
            } else {
                ArrayList<Enemy> activeTargets = (currentLocation == LocationState.BANK_INTERIOR) ? bankSecurity : townEnemies;
                boolean hitRegistered = false;
                for (Enemy enemy : activeTargets) {
                    if (enemy.alive && b.getBounds().intersects(enemy.getBounds())) {
                        enemy.health -= player.getWeaponDamage();
                        if (enemy.health <= 0) enemy.alive = false;
                        bIter.remove();
                        hitRegistered = true;
                        break;
                    }
                }
                if (hitRegistered) continue;
            }
        }

        if (currentLocation == LocationState.BANK_INTERIOR && currentLevel == 3 && bankWave > 0
                && bankSecurity.stream().noneMatch(g -> g.alive)) {
            if (bankWave < 3) {
                bankWave++;
                spawnBankWave(bankWave);
            }
        }
    }

    // ===== PAINT =====
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        switch (currentLocation) {
            case TOWN:           drawTownOverworld(g);  break;
            case HOUSE_INTERIOR: drawHouseInterior(g);  break;
            case BANK_INTERIOR:  drawBankInterior(g);   break;
            case SHOP:           drawShopInterior(g);   break;
        }

        if (currentLocation != LocationState.SHOP) {
            drawAimCursor(g);
        }

        if (player.health > 0) {
            player.draw(g);
        } else {
            g.setColor(new Color(0, 0, 0, 220));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("HEIST FAILED - WASTED", 190, 300);
            drawRestartButton(g);
            return;
        }

        // HUD
        g.setColor(new Color(15, 15, 30, 210));
        g.fillRoundRect(10, 10, 260, 130, 10, 10);
        g.setColor(new Color(218, 165, 32));
        g.drawRoundRect(10, 10, 260, 130, 10, 10);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Courier New", Font.BOLD, 12));
        g.drawString("LOCATION: " + currentLocation, 15, 28);
        g.drawString("CASH:     $" + player.wallet, 15, 43);
        g.drawString("LEVEL:    " + currentLevel, 15, 58);
        g.drawString("WEAPON:   " + player.getWeaponName(), 15, 73);
        g.drawString("ARMOR:    " + player.getArmorName(), 15, 88);

        g.drawString("VITALITY: ", 15, 103);
        g.setColor(new Color(100, 0, 0));
        g.fillRoundRect(90, 93, 100, 12, 4, 4);
        g.setColor(new Color(0, 200, 80));
        g.fillRoundRect(90, 93, player.health * 10, 12, 4, 4);

        g.setColor(Color.WHITE);
        if (currentLocation == LocationState.BANK_INTERIOR) {
            if (vaultHasCash) {
                g.drawString("STATUS:   APPROACH VAULT [E]", 15, 120);
            } else {
                long aliveGuards = bankSecurity.stream().filter(u -> u.alive).count();
                if (player.hasCash && aliveGuards > 0) {
                    g.setColor(Color.ORANGE);
                    g.drawString("STATUS:   EXIT LOCKED - KILL ALL GUARDS", 15, 120);
                } else {
                    g.setColor(aliveGuards > 0 ? Color.ORANGE : Color.GREEN);
                    g.drawString(aliveGuards > 0 ? "ALARM:  GUARDS REMAIN (" + aliveGuards + ")" : "FLEE! EXIT OPEN", 15, 120);
                }
            }
        } else if (currentLocation == LocationState.HOUSE_INTERIOR && player.hasCash) {
            g.setColor(new Color(255, 220, 0));
            g.drawString("STATUS:   BRING CASH TO TABLE!", 15, 120);
        } else if (currentLocation == LocationState.SHOP) {
            g.setColor(new Color(255, 220, 0));
            g.drawString("STATUS:   SHOPPE - CLICK TO BUY", 15, 120);
        } else {
            g.drawString("STATUS:   FREE ROAMING", 15, 120);
        }

        // Heist complete popup (drawn last, on top of everything)
        if (heistCompletePopup) {
            drawHeistCompletePopup(g);
        }
        if (gameCompletePopup) {
            drawGameCompletePopup(g);
        }
    }
}

// --- APP FRAME ENTRY POINT ---
public class SyndicateApp {
    public static void main(String[] args) {
        String name = JOptionPane.showInputDialog("Agent Name:");
        JFrame frame = new JFrame("Syndicate: Crime Town Retro");
        GamePanel game = new GamePanel(name != null && !name.trim().isEmpty() ? name : "Agent", Color.CYAN);
        frame.add(game);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
        game.requestFocusInWindow();
    }
}
