package com.syndicate.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

/**
 * Generates all PNG assets programmatically using Java2D.
 * Called automatically by Main.java on first run when assets/ is missing.
 * No external tools required — pure Java only.
 */
public class AssetGenerator {

    public static void generateAll() throws Exception {
        File dir = AssetLoader.getAssetsDir();
        dir.mkdirs();
        System.out.println("Generating assets in: " + dir.getAbsolutePath());

        makeBackground(dir);
        makeStartBg(dir);
        makeLogo(dir);
        makeHexBtn(dir, "btn_start.png",         "START GAME", new Color(255,90,0),  false);
        makeHexBtn(dir, "btn_start_pressed.png",  "START GAME", new Color(255,150,50), true);
        makeHexBtn(dir, "btn_menu.png",           "MAIN MENU",  new Color(200,70,0),  false);
        makeHexBtn(dir, "btn_menu_pressed.png",   "MAIN MENU",  new Color(255,130,40), true);
        makeCreditsHeader(dir);
        makeBrawler(dir);
        makeHacker(dir);
        makeDriver(dir);
        makeFirearm(dir);
        makeBurnerPhone(dir);
        makeMedkit(dir);
        makeExplosive(dir);
        makePatrolOfficer(dir);
        makeSwatTeam(dir);
        makeCard(dir, "card_brawler.png", new Color(140,30,30));
        makeCard(dir, "card_hacker.png",  new Color(20,110,60));
        makeCard(dir, "card_driver.png",  new Color(20,55,140));
        makeMissionTile(dir, "mission_easy.png",   new Color(40,120,40),  "EASY JOB");
        makeMissionTile(dir, "mission_medium.png", new Color(160,120,10), "HEIST");
        makeMissionTile(dir, "mission_hard.png",   new Color(140,20,20),  "BANK ROBBERY");
        makeBanner(dir, "success_banner.png", new Color(20,80,20), new Color(60,200,60), "OPERATION SUCCEEDED");
        makeBanner(dir, "fail_banner.png",    new Color(80,20,20), new Color(200,60,60), "OPERATION FAILED");
        makeCoin(dir);
        makeHeat(dir);
        makeHeart(dir);
        makeGhost(dir);
        makeGrimReaper(dir);
        System.out.println("Assets ready.");
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    static BufferedImage blank(int w, int h) { return new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB); }

    static Graphics2D g2(BufferedImage img) {
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g;
    }

    static void save(BufferedImage img, File dir, String name) throws Exception {
        ImageIO.write(img, "PNG", new File(dir, name));
    }

    static void infernoPanel(Graphics2D g, int x, int y, int w, int h, int arc) {
        g.setColor(new Color(0,0,0,130));
        g.fillRoundRect(x+8,y+8,w,h,arc,arc);
        g.setPaint(new GradientPaint(x,y,new Color(12,0,10,235),x,y+h,new Color(38,7,7,235)));
        g.fillRoundRect(x,y,w,h,arc,arc);
        g.setColor(new Color(255,90,0)); g.setStroke(new BasicStroke(3));
        g.drawRoundRect(x,y,w,h,arc,arc);
        g.setColor(new Color(110,38,80)); g.setStroke(new BasicStroke(1));
        g.drawRoundRect(x+10,y+10,w-20,h-20,arc-4,arc-4);
    }

    static void drawEmbers(Graphics2D g, int w, int h) {
        Random r = new Random(42);
        for (int i=0;i<55;i++) {
            int x=(int)((i*137+120)%(w+100))-50;
            int y=(int)(h*0.6)+r.nextInt(Math.max(1,h/3));
            g.setColor(new Color(255,80+r.nextInt(60),10,40+r.nextInt(60)));
            g.fillRect(x,y,2+(i%3),2+(i%3));
        }
    }

    static void drawVignette(Graphics2D g, int w, int h) {
        RadialGradientPaint rg=new RadialGradientPaint(
            new Point2D.Float(w*0.5f,h*0.48f),Math.max(w,h)*0.66f,
            new float[]{0f,0.6f,1f},
            new Color[]{new Color(0,0,0,0),new Color(0,0,0,70),new Color(0,0,0,230)});
        g.setPaint(rg); g.fillRect(0,0,w,h);
    }

    static void drawPerson(Graphics2D g,Color skin,Color shirt,Color pants,int cx,int cy){
        g.setColor(skin);  g.fillOval(cx-9,cy-26,18,18);
        g.setColor(shirt); g.fillRoundRect(cx-11,cy-8,22,20,6,6);
        g.setColor(pants); g.fillRect(cx-9,cy+12,7,15); g.fillRect(cx+2,cy+12,7,15);
    }

    // ── backgrounds ───────────────────────────────────────────────────────────

    static void makeBackground(File dir) throws Exception {
        int W=960,H=700;
        BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        g.setPaint(new GradientPaint(0,0,new Color(8,2,12),0,H,new Color(22,5,8)));
        g.fillRect(0,0,W,H);
        g.setColor(new Color(14,4,10));
        int[] bx={0,70,130,200,280,360,440,510,590,660,740,810,860,900};
        int[] bh={180,260,160,310,230,185,270,210,250,290,170,230,185,200};
        for(int i=0;i<bx.length;i++){int bw=i+1<bx.length?bx[i+1]-bx[i]:W-bx[i];g.fillRect(bx[i],H-bh[i],bw,bh[i]);}
        Random rng=new Random(7);
        for(int bi=0;bi<bx.length;bi++){int bw=bi+1<bx.length?bx[bi+1]-bx[bi]:40;
            for(int wy=H-bh[bi]+10;wy<H-10;wy+=18) for(int wx=bx[bi]+5;wx<bx[bi]+bw-5;wx+=12)
                if(rng.nextFloat()>0.45f){g.setColor(new Color(255,220,80,50+rng.nextInt(40)));g.fillRect(wx,wy,5,7);}}
        drawEmbers(g,W,H); drawVignette(g,W,H);
        save(img,dir,"background.png");
    }

    static void makeStartBg(File dir) throws Exception {
        int W=960,H=700;
        BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        g.setPaint(new GradientPaint(0,0,new Color(6,1,10),0,H,new Color(20,4,6)));
        g.fillRect(0,0,W,H);
        g.setColor(new Color(255,60,0,12)); g.setStroke(new BasicStroke(1));
        for(int x=0;x<W;x+=60) for(int y=0;y<H;y+=60) g.drawRect(x,y,58,58);
        g.setColor(new Color(10,3,7));
        int[] bx={0,90,160,240,320,400,480,540,620,700,770,840,890};
        int[] bh={200,280,170,320,250,195,275,215,255,295,175,235,200};
        for(int i=0;i<bx.length;i++){int bw=i+1<bx.length?bx[i+1]-bx[i]:W-bx[i];g.fillRect(bx[i],H-bh[i],bw,bh[i]);}
        Random rng=new Random(13);
        for(int bi=0;bi<bx.length;bi++){int bw=bi+1<bx.length?bx[bi+1]-bx[bi]:40;
            for(int wy=H-bh[bi]+8;wy<H-8;wy+=16) for(int wx=bx[bi]+4;wx<bx[bi]+bw-4;wx+=11)
                if(rng.nextFloat()>0.5f){g.setColor(new Color(255,210,60,45+rng.nextInt(35)));g.fillRect(wx,wy,4,6);}}
        drawEmbers(g,W,H); drawVignette(g,W,H);
        save(img,dir,"start_bg.png");
    }

    static void makeLogo(File dir) throws Exception {
        int W=540,H=130;
        BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        RadialGradientPaint rg=new RadialGradientPaint(new Point2D.Float(W/2f,H/2f),W*0.55f,
            new float[]{0f,0.5f,1f},new Color[]{new Color(255,80,0,60),new Color(200,40,0,20),new Color(0,0,0,0)});
        g.setPaint(rg); g.fillRect(0,0,W,H);
        int[] hxT={14,W-14,W-2,W-14,14,2},hyT={H/2,H/2,H/4,4,4,H/4};
        int[] hx={14,W-14,W-2,W-14,14,2},hy={H/2,H/2,H*3/4,H-4,H-4,H*3/4};
        g.setPaint(new GradientPaint(0,0,new Color(10,1,8,220),0,H,new Color(34,6,6,220)));
        g.fillPolygon(hxT,hyT,6); g.fillPolygon(hx,hy,6);
        g.setColor(new Color(255,80,0)); g.setStroke(new BasicStroke(3));
        g.drawPolygon(hxT,hyT,6); g.drawPolygon(hx,hy,6);
        g.setFont(new Font("SansSerif",Font.BOLD,64));
        FontMetrics fm=g.getFontMetrics(); int tx=(W-fm.stringWidth("SYNDICATE"))/2;
        g.setColor(new Color(40,4,0,180)); g.drawString("SYNDICATE",tx+3,86+3);
        g.setColor(new Color(255,168,60)); g.drawString("SYNDICATE",tx,86);
        g.setFont(new Font("SansSerif",Font.BOLD,16));
        fm=g.getFontMetrics(); tx=(W-fm.stringWidth("CRIME MANAGEMENT SYSTEM"))/2;
        g.setColor(new Color(180,100,20,160)); g.drawString("CRIME MANAGEMENT SYSTEM",tx,112);
        save(img,dir,"logo.png");
    }

    static void makeHexBtn(File dir,String fname,String text,Color edge,boolean pressed) throws Exception {
        int W=440,H=110;
        BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        int[] xs={20,W-20,W-4,W-20,20,4},ys={8,8,H/2,H-9,H-9,H/2};
        g.setColor(new Color(0,0,0,110));
        int[] sxs={xs[0]+7,xs[1]+7,xs[2]+7,xs[3]+7,xs[4]+7,xs[5]+7};
        int[] sys={ys[0]+8,ys[1]+8,ys[2]+8,ys[3]+8,ys[4]+8,ys[5]+8};
        g.fillPolygon(sxs,sys,6);
        Color top=pressed?new Color(40,8,10):new Color(18,4,10);
        Color bot=pressed?new Color(70,16,8):new Color(44,10,8);
        g.setPaint(new GradientPaint(0,0,top,0,H,bot)); g.fillPolygon(xs,ys,6);
        g.setColor(edge); g.setStroke(new BasicStroke(3)); g.drawPolygon(xs,ys,6);
        g.setStroke(new BasicStroke(1)); g.setColor(new Color(115,40,75));
        g.drawLine(36,18,W-36,18); g.drawLine(36,H-19,W-36,H-19);
        g.setFont(new Font("SansSerif",Font.BOLD,28));
        FontMetrics fm=g.getFontMetrics(); int tx=(W-fm.stringWidth(text))/2,ty=(H-fm.getHeight())/2+fm.getAscent();
        g.setColor(new Color(40,5,0,160)); g.drawString(text,tx+2,ty+2);
        g.setColor(new Color(255,180,70)); g.drawString(text,tx,ty);
        save(img,dir,fname);
    }

    static void makeCreditsHeader(File dir) throws Exception {
        int W=620,H=80;
        BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        int[] xs={30,W-30,W-6,W-30,30,6},ys={6,6,H/2,H-7,H-7,H/2};
        g.setPaint(new GradientPaint(0,0,new Color(14,2,12),0,H,new Color(36,8,8)));
        g.fillPolygon(xs,ys,6);
        g.setColor(new Color(255,90,0)); g.setStroke(new BasicStroke(2)); g.drawPolygon(xs,ys,6);
        g.setColor(new Color(100,35,80)); g.setStroke(new BasicStroke(1));
        g.drawLine(50,H/2,W-50,H/2);
        g.setFont(new Font("SansSerif",Font.BOLD,28));
        FontMetrics fm=g.getFontMetrics(); int tx=(W-fm.stringWidth("SYNDICATE"))/2;
        g.setColor(new Color(35,3,0,160)); g.drawString("SYNDICATE",tx+2,46+2);
        g.setColor(new Color(255,165,55)); g.drawString("SYNDICATE",tx,46);
        save(img,dir,"credits_header.png");
    }

    // ── characters ────────────────────────────────────────────────────────────

    static void makeBrawler(File dir) throws Exception {
        int W=80,H=80; BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        RadialGradientPaint rg=new RadialGradientPaint(new Point2D.Float(W/2f,H/2f),W/2f,
            new float[]{0f,1f},new Color[]{new Color(200,40,40,80),new Color(0,0,0,0)});
        g.setPaint(rg); g.fillOval(0,0,W,H);
        drawPerson(g,new Color(220,160,100),new Color(160,25,25),new Color(50,50,70),W/2,50);
        g.setColor(new Color(220,160,100)); g.fillOval(W/2-22,44,11,11); g.fillOval(W/2+11,44,11,11);
        g.setFont(new Font("SansSerif",Font.BOLD,9));
        g.setColor(new Color(0,0,0,150)); g.drawString("BRAWLER",W/2-20,H-1);
        g.setColor(new Color(255,150,50)); g.drawString("BRAWLER",W/2-21,H-2);
        save(img,dir,"brawler.png");
    }

    static void makeHacker(File dir) throws Exception {
        int W=80,H=80; BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        RadialGradientPaint rg=new RadialGradientPaint(new Point2D.Float(W/2f,H/2f),W/2f,
            new float[]{0f,1f},new Color[]{new Color(0,180,80,80),new Color(0,0,0,0)});
        g.setPaint(rg); g.fillOval(0,0,W,H);
        drawPerson(g,new Color(200,175,155),new Color(18,110,65),new Color(35,35,55),W/2,50);
        g.setColor(new Color(30,30,40)); g.fillRoundRect(W/2-14,44,28,14,3,3);
        g.setColor(new Color(0,220,100,200)); g.fillRect(W/2-12,46,24,10);
        g.setColor(new Color(0,255,100,60)); for(int y=47;y<56;y+=2) g.drawLine(W/2-11,y,W/2+11,y);
        g.setFont(new Font("SansSerif",Font.BOLD,9));
        g.setColor(new Color(0,0,0,150)); g.drawString("HACKER",W/2-17,H-1);
        g.setColor(new Color(60,220,110)); g.drawString("HACKER",W/2-18,H-2);
        save(img,dir,"hacker.png");
    }

    static void makeDriver(File dir) throws Exception {
        int W=80,H=80; BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        RadialGradientPaint rg=new RadialGradientPaint(new Point2D.Float(W/2f,H/2f),W/2f,
            new float[]{0f,1f},new Color[]{new Color(40,80,200,80),new Color(0,0,0,0)});
        g.setPaint(rg); g.fillOval(0,0,W,H);
        drawPerson(g,new Color(205,160,110),new Color(25,50,150),new Color(40,40,65),W/2,50);
        g.setColor(new Color(170,120,50)); g.setStroke(new BasicStroke(2.5f));
        g.drawOval(W/2-11,39,22,16); g.drawLine(W/2,39,W/2,55); g.drawLine(W/2-11,47,W/2+11,47);
        g.setStroke(new BasicStroke(1));
        g.setFont(new Font("SansSerif",Font.BOLD,9));
        g.setColor(new Color(0,0,0,150)); g.drawString("DRIVER",W/2-15,H-1);
        g.setColor(new Color(80,130,255)); g.drawString("DRIVER",W/2-16,H-2);
        save(img,dir,"driver.png");
    }

    // ── items ─────────────────────────────────────────────────────────────────

    static void makeFirearm(File dir) throws Exception {
        int W=80,H=80; BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        g.setColor(new Color(40,40,50)); g.fillRoundRect(8,30,50,13,4,4);
        g.fillRoundRect(18,43,14,17,4,4); g.fillRect(53,32,16,8);
        g.setColor(new Color(100,100,120)); g.setStroke(new BasicStroke(1)); g.drawLine(10,33,53,33);
        g.setFont(new Font("SansSerif",Font.BOLD,8)); g.setColor(new Color(255,180,60)); g.drawString("FIREARM",12,18);
        save(img,dir,"firearm.png");
    }

    static void makeBurnerPhone(File dir) throws Exception {
        int W=80,H=80; BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        g.setColor(new Color(28,28,34)); g.fillRoundRect(22,6,36,62,8,8);
        g.setColor(new Color(15,85,50)); g.fillRoundRect(26,12,28,44,4,4);
        g.setColor(new Color(0,190,75,100)); for(int y=14;y<56;y+=4) g.drawLine(27,y,53,y);
        g.setColor(new Color(50,50,60)); g.fillOval(33,58,14,6);
        g.setFont(new Font("SansSerif",Font.BOLD,7)); g.setColor(new Color(255,180,60)); g.drawString("BURNER",16,H);
        save(img,dir,"burner_phone.png");
    }

    static void makeMedkit(File dir) throws Exception {
        int W=80,H=80; BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        g.setColor(new Color(180,30,30)); g.fillRoundRect(8,8,64,64,12,12);
        g.setColor(new Color(220,50,50)); g.setStroke(new BasicStroke(1)); g.drawRoundRect(10,10,60,60,10,10);
        g.setColor(Color.WHITE); g.fillRect(30,16,20,48); g.fillRect(16,30,48,20);
        save(img,dir,"medkit.png");
    }

    static void makeExplosive(File dir) throws Exception {
        int W=80,H=80; BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        g.setColor(new Color(50,50,55)); g.fillRoundRect(20,20,40,46,10,10);
        g.setColor(new Color(160,25,25)); g.setStroke(new BasicStroke(2.5f));
        for(int y=26;y<66;y+=8) g.drawLine(22,y,58,y);
        g.setColor(new Color(170,115,35)); g.setStroke(new BasicStroke(2)); g.drawLine(40,20,54,4);
        g.setColor(Color.YELLOW); g.fillOval(51,1,8,8);
        g.setStroke(new BasicStroke(1)); g.setFont(new Font("SansSerif",Font.BOLD,8));
        g.setColor(new Color(255,180,60)); g.drawString("BOOM",24,18);
        save(img,dir,"explosive.png");
    }

    // ── enemies ───────────────────────────────────────────────────────────────

    static void makePatrolOfficer(File dir) throws Exception {
        int W=80,H=80; BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        drawPerson(g,new Color(215,165,105),new Color(25,50,115),new Color(18,35,95),W/2,50);
        g.setColor(new Color(18,35,95)); g.fillRect(W/2-11,22,22,6); g.fillRoundRect(W/2-13,25,26,4,3,3);
        g.setColor(new Color(210,190,35));
        g.fillPolygon(new int[]{W/2-4,W/2,W/2+4,W/2+5,W/2-5},new int[]{43,40,43,48,48},5);
        g.setFont(new Font("SansSerif",Font.BOLD,7)); g.setColor(new Color(255,180,60)); g.drawString("OFFICER",W/2-17,H);
        save(img,dir,"patrol_officer.png");
    }

    static void makeSwatTeam(File dir) throws Exception {
        int W=80,H=80; BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        RadialGradientPaint rg=new RadialGradientPaint(new Point2D.Float(W/2f,H/2f),W/2f,
            new float[]{0f,1f},new Color[]{new Color(200,0,0,90),new Color(0,0,0,0)});
        g.setPaint(rg); g.fillOval(0,0,W,H);
        drawPerson(g,new Color(140,125,105),new Color(30,30,30),new Color(22,22,22),W/2,50);
        g.setColor(new Color(28,28,28)); g.fillOval(W/2-11,20,22,22);
        g.setColor(new Color(0,70,150,180)); g.fillOval(W/2-7,26,14,10);
        g.setFont(new Font("SansSerif",Font.BOLD,8)); g.setColor(new Color(255,210,40)); g.drawString("SWAT",W/2-11,H-4);
        save(img,dir,"swat_team.png");
    }

    // ── cards / tiles / banners ───────────────────────────────────────────────

    static void makeCard(File dir,String fname,Color accent) throws Exception {
        int W=130,H=182; BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        g.setPaint(new GradientPaint(0,0,new Color(14,2,12,235),0,H,new Color(28,5,5,235)));
        g.fillRoundRect(0,0,W,H,14,14);
        g.setColor(accent); g.setStroke(new BasicStroke(2)); g.drawRoundRect(1,1,W-2,H-2,14,14);
        g.setPaint(new GradientPaint(0,0,accent,W,0,accent.darker()));
        g.fillRoundRect(0,0,W,38,14,14); g.fillRect(0,20,W,18);
        g.setColor(new Color(100,35,78)); g.setStroke(new BasicStroke(1));
        g.drawRoundRect(8,44,W-16,H-52,8,8);
        save(img,dir,fname);
    }

    static void makeMissionTile(File dir,String fname,Color col,String label) throws Exception {
        int W=220,H=112; BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        g.setPaint(new GradientPaint(0,0,col.darker().darker(),W,H,col.darker()));
        g.fillRoundRect(0,0,W,H,14,14);
        g.setColor(col); g.setStroke(new BasicStroke(2)); g.drawRoundRect(1,1,W-2,H-2,14,14);
        g.setColor(new Color(255,255,255,18)); g.fillRoundRect(4,4,W-8,H/2,10,10);
        g.setFont(new Font("SansSerif",Font.BOLD,17));
        FontMetrics fm=g.getFontMetrics(); int tx=(W-fm.stringWidth(label))/2;
        g.setColor(new Color(0,0,0,160)); g.drawString(label,tx+2,68+2);
        g.setColor(Color.WHITE); g.drawString(label,tx,68);
        save(img,dir,fname);
    }

    static void makeBanner(File dir,String fname,Color bg,Color border,String text) throws Exception {
        int W=420,H=90; BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        g.setPaint(new GradientPaint(0,0,bg,W,0,bg.brighter())); g.fillRoundRect(0,0,W,H,16,16);
        g.setColor(border); g.setStroke(new BasicStroke(2)); g.drawRoundRect(1,1,W-2,H-2,16,16);
        g.setFont(new Font("SansSerif",Font.BOLD,24));
        FontMetrics fm=g.getFontMetrics(); int tx=(W-fm.stringWidth(text))/2;
        g.setColor(new Color(0,0,0,150)); g.drawString(text,tx+2,56+2);
        g.setColor(Color.WHITE); g.drawString(text,tx,56);
        save(img,dir,fname);
    }

    // ── icons ─────────────────────────────────────────────────────────────────

    static void makeCoin(File dir) throws Exception {
        int S=32; BufferedImage img=blank(S,S); Graphics2D g=g2(img);
        g.setColor(new Color(210,175,25)); g.fillOval(2,2,28,28);
        g.setColor(new Color(170,135,15)); g.setStroke(new BasicStroke(2)); g.drawOval(2,2,28,28);
        g.setColor(new Color(235,210,55)); g.setFont(new Font("SansSerif",Font.BOLD,16)); g.drawString("$",10,22);
        save(img,dir,"coin.png");
    }

    static void makeHeat(File dir) throws Exception {
        int S=32; BufferedImage img=blank(S,S); Graphics2D g=g2(img);
        g.setColor(new Color(210,55,15));
        g.fillPolygon(new int[]{16,8,12,5,16,27,20,24},new int[]{2,14,11,26,20,26,11,14},8);
        g.setColor(new Color(255,150,35)); g.fillOval(12,14,8,12);
        g.setColor(new Color(255,215,90)); g.fillOval(14,17,4,7);
        save(img,dir,"heat.png");
    }

    static void makeHeart(File dir) throws Exception {
        int S=32; BufferedImage img=blank(S,S); Graphics2D g=g2(img);
        g.setColor(new Color(210,35,55));
        g.fillOval(4,6,14,14); g.fillOval(14,6,14,14);
        g.fillPolygon(new int[]{4,16,28},new int[]{14,30,14},3);
        save(img,dir,"heart.png");
    }

    static void makeGhost(File dir) throws Exception {
        int W=80,H=90; BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        g.setColor(new Color(220,220,235,210));
        g.fillOval(15,5,50,50); g.fillRect(15,30,50,40);
        for(int i=0;i<5;i++){int bx=15+i*10;g.fillOval(bx,70,10,i%2==0?12:8);}
        g.setColor(new Color(50,30,80)); g.fillOval(25,20,10,10); g.fillOval(45,20,10,10);
        g.setColor(new Color(200,180,255,50)); g.fillOval(8,0,64,80);
        save(img,dir,"ghost.png");
    }

    static void makeGrimReaper(File dir) throws Exception {
        int W=100,H=120; BufferedImage img=blank(W,H); Graphics2D g=g2(img);
        g.setColor(new Color(20,10,20,230));
        g.fillOval(30,10,40,36);
        g.fillPolygon(new int[]{18,82,90,10},new int[]{40,40,120,120},4);
        g.setColor(new Color(12,6,16,240)); g.fillOval(24,4,52,46);
        g.setColor(new Color(170,155,120)); g.setStroke(new BasicStroke(3)); g.drawLine(80,15,55,110);
        g.setStroke(new BasicStroke(2)); g.drawArc(48,4,40,38,30,200);
        g.setColor(new Color(255,90,0,200)); g.fillOval(37,18,10,8); g.fillOval(53,18,10,8);
        save(img,dir,"grim_reaper.png");
    }
}
