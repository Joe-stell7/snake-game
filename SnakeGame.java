import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;


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
    private static final int PANEL_SIZE = GRID_WIDTH * CELL_SIZE; // 600
    private static final int TIMER_DELAY = 150; // milliseconds
    
    // Direction constants
    private static final int UP = 0;
    private static final int DOWN = 1;
    private static final int LEFT = 2;
    private static final int RIGHT = 3;
    
    private LinkedList<Point> snakeSegments;
    private Point food;
    private int direction = RIGHT;
    private int nextDirection = RIGHT;
    private int score = 0;
    private boolean gameOver = false;
    private javax.swing.Timer gameTimer;
    private Random random;
    
    public GamePanel() {
        setBackground(new Color(40, 40, 40)); // Dark gray
        setPreferredSize(new java.awt.Dimension(PANEL_SIZE, PANEL_SIZE));
        setFocusable(true);
        random = new Random();
        snakeSegments = new LinkedList<>();
        
        // Initialize snake with 3 segments, starting near center, facing right
        // Head at (10, 10), body segments extending left
        snakeSegments.add(new Point(10, 10)); // Head
        snakeSegments.add(new Point(9, 10));  // Body
        snakeSegments.add(new Point(8, 10));  // Tail
        
        // Spawn initial food
        spawnFood();
        
        // Add keyboard listener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        });
        
        // Start game timer
        gameTimer = new javax.swing.Timer(TIMER_DELAY, e -> moveSnake());
        gameTimer.start();
    }
    
    private void spawnFood() {
        Point newFood;
        boolean valid;
        do {
            valid = true;
            newFood = new Point(random.nextInt(GRID_WIDTH), random.nextInt(GRID_HEIGHT));
            // Check if food spawns on snake
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
        
        if (key == KeyEvent.VK_R && gameOver) {
            resetGame();
        } else if (!gameOver) {
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
        snakeSegments.clear();
        snakeSegments.add(new Point(10, 10)); // Head
        snakeSegments.add(new Point(9, 10));  // Body
        snakeSegments.add(new Point(8, 10));  // Tail
        direction = RIGHT;
        nextDirection = RIGHT;
        score = 0;
        gameOver = false;
        spawnFood();
        gameTimer.start();
        repaint();
    }
    
    private void moveSnake() {
        if (gameOver) {
            return;
        }
        
        direction = nextDirection;
        
        // Get current head position
        Point head = snakeSegments.getFirst();
        Point newHead = new Point(head.x, head.y);
        
        // Move head in current direction
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
        
        // Handle collision with walls
        if (newHead.x < 0 || newHead.x >= GRID_WIDTH || newHead.y < 0 || newHead.y >= GRID_HEIGHT) {
            endGame();
            return;
        }
        
        // Handle collision with own body
        for (Point segment : snakeSegments) {
            if (segment.equals(newHead)) {
                endGame();
                return;
            }
        }
        
        // Add new head
        snakeSegments.addFirst(newHead);
        
        // Check if food is eaten
        if (newHead.equals(food)) {
            score += 10;
            spawnFood();
        } else {
            // Remove tail to keep snake the same length (unless food was eaten)
            snakeSegments.removeLast();
        }
        
        // Repaint
        repaint();
    }
    
    private void endGame() {
        gameOver = true;
        gameTimer.stop();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Draw grid
        g2d.setColor(new Color(60, 60, 60));
        for (int i = 0; i <= GRID_WIDTH; i++) {
            g2d.drawLine(i * CELL_SIZE, 0, i * CELL_SIZE, GRID_HEIGHT * CELL_SIZE);
        }
        for (int i = 0; i <= GRID_HEIGHT; i++) {
            g2d.drawLine(0, i * CELL_SIZE, GRID_WIDTH * CELL_SIZE, i * CELL_SIZE);
        }
        
        // Draw snake in green
        g2d.setColor(new Color(0, 255, 0));
        for (Point segment : snakeSegments) {
            g2d.fillRect(segment.x * CELL_SIZE, segment.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        
        // Draw food in red
        if (food != null) {
            g2d.setColor(new Color(255, 0, 0));
            g2d.fillRect(food.x * CELL_SIZE, food.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }
        
        // Draw score in top-left (before game over overlay so it appears on top)
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 18));
        g2d.drawString("Score: " + score, 15, 35);
        
        // Draw game over message
        if (gameOver) {
            int panelWidth = getWidth();
            int panelHeight = getHeight();
            
            g2d.setColor(new Color(0, 0, 0, 200)); // Semi-transparent black overlay
            g2d.fillRect(0, 0, panelWidth, panelHeight);
            
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Arial", Font.BOLD, 50));
            String gameOverText = "GAME OVER";
            FontMetrics fm = g2d.getFontMetrics();
            int x = (panelWidth - fm.stringWidth(gameOverText)) / 2;
            int y = (panelHeight / 2) - 60;
            g2d.drawString(gameOverText, x, y);
            
            g2d.setFont(new Font("Arial", Font.BOLD, 32));
            String scoreText = "Final Score: " + score;
            FontMetrics fm2 = g2d.getFontMetrics();
            int scoreX = (panelWidth - fm2.stringWidth(scoreText)) / 2;
            g2d.drawString(scoreText, scoreX, y + 80);
            
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            String resetText = "Press R to play again";
            FontMetrics fm3 = g2d.getFontMetrics();
            int resetX = (panelWidth - fm3.stringWidth(resetText)) / 2;
            g2d.drawString(resetText, resetX, y + 140);
        }
    }
}
