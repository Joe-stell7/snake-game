import javax.swing.*;

public class SnakeGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 600);
            
            GamePanel gamePanel = new GamePanel();
            frame.add(gamePanel);
            frame.setVisible(true);
        });
    }
}

class GamePanel extends JPanel {
    // Game logic goes here later
}
