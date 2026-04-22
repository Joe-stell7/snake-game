import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Font;
import java.util.*;
import java.util.prefs.Preferences;

public class SnakeGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            
            GamePanel gamePanel = new GamePanel();
            frame.add(gamePanel);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

class GamePanel extends JPanel {
    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;
    private static final int CELL_SIZE = 30;
    private static final int PANEL_SIZE = GRID_WIDTH * CELL_SIZE;
    
    // Speed levels: 6 levels with decreasing delays (slower to faster)
    private final int[] SPEED_LEVELS = {250, 200, 150, 125, 100, 75}; // ms for levels 1-6
    private static final int POINTS_PER_LEVEL = 30; // Level up every 30 points (3 food)
    
    // Direction constants
    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;
    
    private int highScore = 0;
    private int currentLevel = 1;
    private boolean showStartScreen = true;
    private boolean paused = false;  // NEW - Pause functionality
    private Preferences prefs;
    private LinkedList<Point> snakeSegments;
    private Point food;
    private int direction = RIGHT;
    private int nextDirection = RIGHT;
    private int score = 0;
    private boolean gameOver = false;
    private javax.swing.Timer gameTimer;
    private Random random;
    
    public GamePanel() {
        setBackground(new Color(40, 40, 40));
        setPreferredSize(new Dimension(PANEL_SIZE, PANEL_SIZE));
        setFocusable(true);
        random = new Random();
        snakeSegments = new LinkedList<>();
        
        // Load high score
        prefs = Preferences.userNodeForPackage(GamePanel.class);
        highScore = prefs.getInt("snakeHighScore", 0);

        // Initialize snake
        snakeSegments.add(new Point(10, 10));
        snakeSegments.add(new Point(9, 10));
        snakeSegments.add(new Point(8, 10));
        
        spawnFood();
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
        
        // Create timer but don't start yet
        gameTimer = new javax.swing.Timer(SPEED_LEVELS[0], e -> moveSnake());
    }
    
    private void spawnFood() {
        Point newFood;
        boolean valid;
        do {
            valid = true;
            newFood = new Point(random.nextInt(GRID_WIDTH), random.nextInt(GRID_HEIGHT));
            for (Point segment : snakeSegments) {
                if (segment.equals(newFood)) {
                    valid = false;
                    break;
                }
            }
        } while (!valid);
        food = newFood;
    }
    
    private void handleKeyPress(KeyEvent e) {
        int key = e.getKeyCode();
        
        // SPACE ALWAYS resets/starts
        if (key == KeyEvent.VK_SPACE) {
            resetGame();
            return;
        }
        
        // P toggles pause (works anytime except start screen)
        if (key == KeyEvent.VK_P && !showStartScreen) {
            paused = !paused;
            if (!paused) {
                gameTimer.start();  // Resume
            } else {
                gameTimer.stop();   // Pause
            }
            return;
        }
        
        // Movement keys ONLY during active gameplay AND not paused
        if (!showStartScreen && !gameOver && !paused) {
            if (key == KeyEvent.VK_UP && direction != DOWN) {
                nextDirection = UP;
            } else if (key == KeyEvent.VK_DOWN && direction != UP) {
                nextDirection = DOWN;
            } else if (key == KeyEvent.VK_LEFT && direction != RIGHT) {
                nextDirection = LEFT;
            } else if (key == KeyEvent.VK_RIGHT && direction != LEFT) {
                nextDirection = RIGHT;
            }
        }
    }
    
    private void resetGame() {
        // FULL CLEAN RESET
        snakeSegments.clear();
        snakeSegments.add(new Point(10, 10));
        snakeSegments.add(new Point(9, 10));
        snakeSegments.add(new Point(8, 10));
        direction = RIGHT;
        nextDirection = RIGHT;
        score = 0;
        currentLevel = 1;
        gameOver = false;
        paused = false;  // Reset pause state
        showStartScreen = false;  // HIDE start screen
        spawnFood();
        gameTimer.stop();
        gameTimer.setDelay(SPEED_LEVELS[0]);
        gameTimer.start();  // START THE GAME
        repaint();
    }
    
    private void moveSnake() {
        // STOP movement on start screen, game over, OR paused
        if (showStartScreen || gameOver || paused) {
            return;
        }
        
        direction = nextDirection;
        
        Point head = snakeSegments.getFirst();
        Point newHead = new Point(head.x, head.y);
        
        switch (direction) {
            case UP:
                newHead.y--;
                break;
            case DOWN:
                newHead.y++;
                break;
            case LEFT:
                newHead.x--;
                break;
            case RIGHT:
                newHead.x++;
                break;
        }
        
        // Wall collision
        if (newHead.x < 0 || newHead.x >= GRID_WIDTH || newHead.y < 0 || newHead.y >= GRID_HEIGHT) {
            endGame();
            return;
        }
        
        // Self collision
        for (Point segment : snakeSegments) {
            if (segment.equals(newHead)) {
                endGame();
                return;
            }
        }
        
        snakeSegments.addFirst(newHead);
        
        // Food eaten - check for level up
        if (newHead.equals(food)) {
            score += 10;
            spawnFood();
            
            // Calculate new level (every 30 points = 1 level)
            int newLevel = Math.min((score / POINTS_PER_LEVEL) + 1, 6);
            if (newLevel > currentLevel) {
                currentLevel = newLevel;
                gameTimer.setDelay(SPEED_LEVELS[currentLevel - 1]);
            }
        } else {
            snakeSegments.removeLast();
        }
        
        repaint();
    }
    
    private void endGame() {
        if (score > highScore) {
            highScore = score;
            prefs.putInt("snakeHighScore", highScore);
        }
        gameOver = true;
        gameTimer.stop();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // START SCREEN - Draw FIRST (covers everything)
        if (showStartScreen) {
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 60));
            String title = "SNAKE GAME";
            FontMetrics fm = g2d.getFontMetrics();
            int titleX = (PANEL_SIZE - fm.stringWidth(title)) / 2;
            g2d.drawString(title, titleX, PANEL_SIZE / 2 - 40);
            
            g2d.setFont(new Font("Arial", Font.BOLD, 30));
            String startText = "Press SPACE to Play";
            FontMetrics fm2 = g2d.getFontMetrics();
            int startX = (PANEL_SIZE - fm2.stringWidth(startText)) / 2;
            g2d.drawString(startText, startX, PANEL_SIZE / 2 + 40);
            
            g2d.setFont(new Font("Arial", Font.BOLD, 20));
            g2d.drawString("High Score: " + highScore, 20, PANEL_SIZE - 30);
            return;
        }
        
        // GAME SCREEN - Draw grid
        g2d.setColor(new Color(60, 60, 60));
        for (int i = 0; i <= GRID_WIDTH; i++) {
            g2d.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, GRID_HEIGHT * CELL_SIZE);
        }
        for (int i = 0; i <= GRID_HEIGHT; i++) {
            g2d.drawLine(0, i * CELL_SIZE, GRID_WIDTH * CELL_SIZE, i * CELL_SIZE);
        }
        
        // Draw snake
        g2d.setColor(new Color(0, 255, 0));
        for (Point segment : snakeSegments) {
            g2d.fillRect(segment.x * CELL_SIZE, segment.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        
        // Draw food
        if (food != null) {
            g2d.setColor(new Color(255, 0, 0));
            g2d.fillRect(food.x * CELL_SIZE, food.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        
        // Draw info
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("Score: " + score, 15, 30);
        g2d.drawString("High Score: " + highScore, 15, 55);
        g2d.drawString("Level: " + currentLevel + "/6", 15, 80);
        g2d.drawString("Speed: " + SPEED_LEVELS[currentLevel-1] + "ms", 15, 105);
        
        // PAUSE OVERLAY
        if (paused) {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, PANEL_SIZE, PANEL_SIZE);
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 50));
            String pauseText = "PAUSED";
            FontMetrics fmPause = g2d.getFontMetrics();
            int pauseX = (PANEL_SIZE - fmPause.stringWidth(pauseText)) / 2;
            g2d.drawString(pauseText, pauseX, PANEL_SIZE / 2);
            
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String resumeText = "Press P to Continue";
            FontMetrics fmResume = g2d.getFontMetrics();
            int resumeX = (PANEL_SIZE - fmResume.stringWidth(resumeText)) / 2;
            g2d.drawString(resumeText, resumeX, PANEL_SIZE / 2 + 60);
            return;  // Don't draw game over overlay
        }
        
        // Game over overlay
        if (gameOver) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            g2d.setColor(new Color(0, 0, 0, 200));
            g2d.fillRect(0, 0, panelWidth, panelHeight);
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 50));
            String gameOverText = "GAME OVER";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (panelWidth - fm.stringWidth(gameOverText)) / 2;
            int y = (panelHeight / 2) - 60;
            g2d.drawString(gameOverText, x, y);
            
            g2d.setFont(new Font("Arial", Font.BOLD, 32));
            String scoreText = "Score: " + score + "  High Score: " + highScore;
            FontMetrics fm2 = g2d.getFontMetrics();
            int scoreX = (panelWidth - fm2.stringWidth(scoreText)) / 2;
            g2d.drawString(scoreText, scoreX, y + 80);
            
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String resetText = "Press SPACE to Play Again";
            FontMetrics fm3 = g2d.getFontMetrics();
            int resetX = (panelWidth - fm3.stringWidth(resetText)) / 2;
            g2d.drawString(resetText, resetX, y + 140);
        }
    }
}