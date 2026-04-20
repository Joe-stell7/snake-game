import javax.swing.*;
import java.awt.*;
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
    
    private LinkedList<Point> snakeSegments;
    
    public GamePanel() {
        setBackground(new Color(40, 40, 40)); // Dark gray
        setPreferredSize(new java.awt.Dimension(PANEL_SIZE, PANEL_SIZE));
        snakeSegments = new LinkedList<>();
        
        // Initialize snake with 3 segments, starting near center, facing right
        // Head at (10, 10), body segments extending left
        snakeSegments.add(new Point(10, 10)); // Head
        snakeSegments.add(new Point(9, 10));  // Body
        snakeSegments.add(new Point(8, 10));  // Tail
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
    }
}
