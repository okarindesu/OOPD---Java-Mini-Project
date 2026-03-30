package physics;

import Weapons.Projectile;
import engine.GameState;
import engine.LevelSelectionContext;
import entities.*;
import entities.Robot;
import utils.Vector2D;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class GameRenderer {
    private Canvas canvas ;
    private int width ;
    private int height ;

    private Font titleFont ;
    private Font uiFont ;
    private Font smallTitle , mediumTitle , bigTitle ;
    private Font smallUI , mediumUI , bigUI ;

    public GameRenderer(Canvas canvas , int width , int height) {
        this.canvas = canvas ;
        this.width = width ;
        this.height = height ;
        titleFont = loadFont("resources/fonts/p2p.ttf" , 20f) ;
        uiFont = loadFont("resources/fonts/sk_bo.ttf" , 20f) ;

        smallTitle = titleFont.deriveFont(14f) ;
        mediumTitle = titleFont.deriveFont(24f) ;
        bigTitle = titleFont.deriveFont(40f) ;

        smallUI = uiFont.deriveFont(14f) ;
        mediumUI = uiFont.deriveFont(24f) ;
        bigUI = uiFont.deriveFont(40f) ;
    }

    private Font loadFont(String path, float size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(path));
            return font.deriveFont(size);
        } catch (Exception e) {
            System.out.println("Failed to load font: " + path);
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, (int) size); // fallback
        }
    }

    public void render(LevelSelectionContext levelSelectionContext , Robot robot1 , Robot robot2 , Camera camera) {
        BufferStrategy bs = canvas.getBufferStrategy() ;
        if (bs == null) return ;
        Graphics g = bs.getDrawGraphics() ;

        GameState gameState = levelSelectionContext.getGameState() ;
        switch(gameState) {
            case LEVEL_SELECTION_STATE :
                levelSelectionRender(g , levelSelectionContext) ;
                break;
            case GAME_PLAYING_STATE:
                gameRender(g , robot1 , robot2 , levelSelectionContext.getLevel() , camera) ;
                break;
        }
        g.dispose() ;
        bs.show() ;
    }

    private void levelSelectionRender(Graphics g , LevelSelectionContext levelSelectionContext) {
        LevelInfo[] levels = levelSelectionContext.getLevels();
        int selectedLevel = levelSelectionContext.getSelectedLevel();

        Graphics2D g2 = (Graphics2D) g;

        // ===== BACKGROUND =====
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);

        // ===== TITLE =====
        g.setFont(bigTitle);
        g.setColor(Color.WHITE);

        String title = "RoboWars";
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (width - titleWidth) / 2, 80);

        g.setFont(mediumUI);
        String subtitle = "Select Terrain";
        int subWidth = g.getFontMetrics().stringWidth(subtitle);
        g.drawString(subtitle, (width - subWidth) / 2, 120);

        // ===== GRID CONFIG (720p FRIENDLY) =====
        int columns = 4;

        int spacingX = 30;
        int spacingY = 50;

        int totalSpacingX = (columns - 1) * spacingX;

        int cardWidth = (width - 160 - totalSpacingX) / columns;
        int cardHeight = (int)(cardWidth * 0.6f);

        int startX = (width - (columns * cardWidth + totalSpacingX)) / 2;
        int startY = 160;

        // ===== LEVEL GRID =====
        for (int i = 0; i < levels.length; i++) {

            int row = i / columns;
            int col = i % columns;

            int x = startX + col * (cardWidth + spacingX);
            int y = startY + row * (cardHeight + spacingY);

            LevelInfo lvl = levels[i];

            if (lvl.getPreviewImage() != null) {
                g.drawImage(lvl.getPreviewImage(), x, y, cardWidth, cardHeight, null);
            }

            // Highlight selected
            if (i == selectedLevel) {
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(4));

                // Outer glow
                g2.drawRect(x - 3, y - 3, cardWidth + 6, cardHeight + 6);

                // Light overlay
                g2.setColor(new Color(255, 255, 0, 40));
                g2.fillRect(x, y, cardWidth, cardHeight);
            }

            // Border
            g2.setColor(Color.WHITE);
            g2.setStroke(new BasicStroke(1));
            g2.drawRect(x, y, cardWidth, cardHeight);

            // Level title
            g.setFont(mediumUI);
            FontMetrics fm = g.getFontMetrics();

            String titleText = lvl.getTitle();
            int textWidth = fm.stringWidth(titleText);

// Center text relative to card
            int textX = x + (cardWidth - textWidth) / 2;
            int textY = y + cardHeight + 25;

            g.drawString(titleText, textX, textY);
        }

        // ===== CONTROLS =====
        g.setFont(smallUI);

        String label = "SELECT :";
        String movement = "UP / DOWN / LEFT / RIGHT";
        String plus = "+";
        String enter = "ENTER";

        int padding = 12;
        int boxHeight = 32;
        int gap = 15;

        FontMetrics fm = g.getFontMetrics();

        int labelWidth = fm.stringWidth(label);

// Calculate widths
        int moveW = fm.stringWidth(movement) + padding * 2;
        int plusW = fm.stringWidth(plus) + padding * 2;
        int enterW = fm.stringWidth(enter) + padding * 2;

// Total width
        int totalWidth = labelWidth + 20 + moveW + gap + plusW + gap + enterW;

// Center horizontally
        int x = (width - totalWidth) / 2;
        int y = height - 70;

// Draw label
        g.setColor(Color.WHITE);
        g.drawString(label, x, y + 22);
        x += labelWidth + 20;

// ===== MOVEMENT BOX =====
        g.drawRect(x, y, moveW, boxHeight);

// Center text
        int moveTextX = x + (moveW - fm.stringWidth(movement)) / 2;
        g.drawString(movement, moveTextX, y + 22);

        x += moveW + gap;

// ===== PLUS BOX =====
        g.drawRect(x, y, plusW, boxHeight);
        int plusTextX = x + (plusW - fm.stringWidth(plus)) / 2;
        g.drawString(plus, plusTextX, y + 22);

        x += plusW + gap;

// ===== ENTER BOX =====
        g.drawRect(x, y, enterW, boxHeight);
        int enterTextX = x + (enterW - fm.stringWidth(enter)) / 2;
        g.drawString(enter, enterTextX, y + 22);
    }

    private void gameRender(Graphics g , Robot robot1 , Robot robot2 , Level level , Camera camera) {
        camera.setCameraX(robot1.getPosition().getVector2DX() - width / 2) ;
        camera.setCameraY(robot1.getPosition().getVector2DY() - height / 2) ;

        g.setColor(Color.BLACK) ;
        g.fillRect(0 , 0 , width , height) ;

        drawLevel(g , level , camera) ;
        drawRobot(g , robot1 , Color.RED) ;
        drawRobot(g , robot2 , Color.CYAN) ;
        drawProjectiles(g , robot1) ;
        drawProjectiles(g , robot2) ;
        drawUI(g , robot1 , robot2) ;

        g.setColor(Color.WHITE) ;
        g.drawString("RoboWars" , 10 , 20) ;
    }

    private void drawProjectiles(Graphics g , Robot robot) {
        List<Projectile> p1 = robot.getHandGun().getProjectileSystem().getProjectiles() ;

        for(Projectile p : p1) {
            if(p.getHasHitTile() || p.getHasHitRobot() || p.getHasReachedBoundary()) continue ;

            int pX = (int) p.getPosition().getVector2DX() ;
            int pY = (int) p.getPosition().getVector2DY() ;

            g.setColor(Color.RED) ;
            g.fillOval(pX , pY , (int) p.getProjectileRadius() , (int) p.getProjectileRadius()) ;
        }
    }

    private void drawRobot(Graphics g , Robot robot , Color color) {
        if(robot.gameOverforRobo) return ;
        g.setColor(color) ;
        Vector2D vec = robot.getPosition() ;
        float roboWidth = robot.getRoboWidth() ;
        float roboHeight = robot.getRoboHeight() ;

        BufferedImage sprite = robot.getAnimationManager().getCurrentFrame() ;

        boolean isHit = false ;
        if(robot.isHit && (System.currentTimeMillis() - robot.hitTime < robot.HIT_DURATION)) isHit = true ;

        if(sprite != null) {
            if(isHit) sprite = toGrayScale(sprite) ;
            g.drawImage(sprite , (int) vec.getVector2DX() , (int) vec.getVector2DY() , (int) roboWidth , (int) roboHeight , null) ;
        }
        else {
            if (isHit) g.setColor(Color.DARK_GRAY);
            else g.setColor(color);
            g.fillRect((int) vec.getVector2DX(), (int) vec.getVector2DY(), (int) roboWidth, (int) roboHeight);
        }
    }

    private BufferedImage toGrayScale(BufferedImage image) {
        BufferedImage grayscale = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        // Apply grayscale filter while preserving transparency
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getRGB(x, y);

                // Extract color components
                int alpha = (argb >> 24) & 0xFF;
                int red = (argb >> 16) & 0xFF;
                int green = (argb >> 8) & 0xFF;
                int blue = argb & 0xFF;

                // Calculate grayscale value using luminosity formula
                int gray = (int)(0.299 * red + 0.587 * green + 0.114 * blue);

                // Combine alpha with grayscale value
                int grayscaleArgb = (alpha << 24) | (gray << 16) | (gray << 8) | gray;
                grayscale.setRGB(x, y, grayscaleArgb);
            }
        }

        return grayscale;
    }

    private void drawLevel(Graphics g , Level level , Camera camera) {
        drawBackground(g , level , camera) ;

        g.setColor(Color.BLUE) ;
        int levelSize = level.getLevelSize() ;
        for(int i = 0 ; i < levelSize ; i++) {
            Tile tile = level.findTile(i) ;
            if(tile != null) drawTile(g , tile) ;
        }
    }

    private void drawUI(Graphics g, Robot robot1 , Robot robot2) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        // Title
        g.drawString("RoboWars - 2 Player", 10, 20);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("P1: WASD + Q | P2: Arrows + Space", 10, 35);

        // Player 1 Stats
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("P1", 10, 60);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Health: " + String.format("%.0f", robot1.getHealth()) + "/" + (int)Robot.getMaxHealth(), 10, 75);
        g.drawString("Lives: " + robot1.getLives(), 10, 90);

        // Health bar for P1
        drawHealthBar(g, 10, 100, 150, 15, robot1.getHealth(), Robot.getMaxHealth(), Color.RED);

        // Player 2 Stats
        g.setColor(Color.CYAN);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("P2", width - 50, 60);
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.drawString("Health: " + String.format("%.0f", robot2.getHealth()) + "/" + (int)Robot.getMaxHealth(), width - 180, 75);
        g.drawString("Lives: " + robot2.getLives(), width - 180, 90);

        // Health bar for P2
        drawHealthBar(g, width - 160, 100, 150, 15, robot2.getHealth(), Robot.getMaxHealth(), Color.CYAN);
    }

    private void drawHealthBar(Graphics g, int x, int y, int width, int height, float currentHealth, float maxHealth, Color color) {
        g.setColor(Color.GRAY);
        g.fillRect(x, y, width, height);

        float percentage = currentHealth / maxHealth;
        int filledWidth = (int) (width * percentage);

        g.setColor(color);
        g.fillRect(x, y, filledWidth, height);

        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);
    }

    private void drawBackground(Graphics g , Level level , Camera camera) {
        float scale = 2.0f ; // Temporary Change
        for(ParallaxObject obj : level.getParallaxObjects()) {
            float depth = obj.getDepth() ;

            float drawX = obj.getPosition().getVector2DX() - camera.getCameraX() * depth ;
            float drawY = obj.getPosition().getVector2DY() - camera.getCameraY() * depth ;
            int w = (int)(obj.getImage().getWidth() * scale);
            int h = (int)(obj.getImage().getHeight() * scale);

            g.drawImage(
                   obj.getImage(),
                    (int) drawX,
                    (int) drawY,
                    w,
                    h,
                    null
            );
        }
    }

    private void drawTile(Graphics g , Tile tile) {
        BufferedImage tex = tile.getTexture() ;

        int tileX = (int) tile.getPosition().getVector2DX() ;
        int tileY = (int) tile.getPosition().getVector2DY() ;

        int tileW = (int) tile.getTileWidth() ;
        int tileH = (int) tile.getTileHeight() ;

        int texW = tex.getWidth() ;
        int texH = tex.getHeight() ;

        for(int x = 0 ; x < tileW ; x += texW) {
            for(int y = 0 ; y < tileH ; y += texH) {
                int drawW = Math.min(texW , tileW - x) ;
                int drawH = Math.min(texH , tileH - y) ;

                g.drawImage(
                        tex,
                        tileX + x,
                        tileY + y,
                        tileX + x + drawW,
                        tileY + y + drawH,
                        0,
                        0,
                        drawW,
                        drawH,
                        null
                );
            }
        }
    }
}
