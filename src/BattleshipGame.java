import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class BattleshipGame extends JFrame {
    private final int GRID_SIZE = 10;
    private final JButton[][] boardButtons = new JButton[GRID_SIZE][GRID_SIZE];
    private int[][] shipGrid = new int[GRID_SIZE][GRID_SIZE]; // 0 = empty, 1 = ship segment
    private int missCounter = 0, strikeCounter = 0, totalMissCounter = 0, totalHitCounter = 0;

    private JLabel missLabel, strikeLabel, totalMissLabel, totalHitLabel;

    public BattleshipGame() {
        setTitle("Battleship Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create game board panel
        JPanel boardPanel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton button = new JButton();
                button.setBackground(Color.CYAN);
                boardButtons[row][col] = button;
                button.addActionListener(new CellClickHandler(row, col));
                boardPanel.add(button);
            }
        }
        add(boardPanel, BorderLayout.CENTER);

        // Create status and control panel
        JPanel statusPanel = new JPanel(new GridLayout(2, 2));
        missLabel = new JLabel("Miss Counter: 0");
        strikeLabel = new JLabel("Strike Counter: 0");
        totalMissLabel = new JLabel("Total Misses: 0");
        totalHitLabel = new JLabel("Total Hits: 0");
        statusPanel.add(missLabel);
        statusPanel.add(strikeLabel);
        statusPanel.add(totalMissLabel);
        statusPanel.add(totalHitLabel);

        JPanel controlPanel = new JPanel();
        JButton playAgainButton = new JButton("Play Again");
        JButton quitButton = new JButton("Quit");

        playAgainButton.addActionListener(e -> startNewGame());
        quitButton.addActionListener(e -> System.exit(0));

        controlPanel.add(playAgainButton);
        controlPanel.add(quitButton);

        add(statusPanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.SOUTH);

        startNewGame(); // initialize the board with ships
    }

    private void startNewGame() {
        // Reset the grid and counters
        missCounter = 0;
        strikeCounter = 0;
        totalMissCounter = 0;
        totalHitCounter = 0;
        updateLabels();
        initializeBoard();
    }

    private void initializeBoard() {
        shipGrid = new int[GRID_SIZE][GRID_SIZE];
        Random rand = new Random();

        int[] shipSizes = {5, 4, 3, 3, 2};
        for (int size : shipSizes) {
            placeShip(size, rand);
        }

        // Reset all board buttons
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                boardButtons[row][col].setEnabled(true);
                boardButtons[row][col].setBackground(Color.CYAN);
                boardButtons[row][col].setText("");
            }
        }
    }

    private void placeShip(int size, Random rand) {
        boolean placed = false;
        while (!placed) {
            int row = rand.nextInt(GRID_SIZE);
            int col = rand.nextInt(GRID_SIZE);
            boolean horizontal = rand.nextBoolean();

            if (canPlaceShip(row, col, size, horizontal)) {
                for (int i = 0; i < size; i++) {
                    if (horizontal) {
                        shipGrid[row][col + i] = 1;
                    } else {
                        shipGrid[row + i][col] = 1;
                    }
                }
                placed = true;
            }
        }
    }

    private boolean canPlaceShip(int row, int col, int size, boolean horizontal) {
        if (horizontal && col + size > GRID_SIZE) return false;
        if (!horizontal && row + size > GRID_SIZE) return false;

        for (int i = 0; i < size; i++) {
            if (horizontal && shipGrid[row][col + i] == 1) return false;
            if (!horizontal && shipGrid[row + i][col] == 1) return false;
        }
        return true;
    }

    private void updateLabels() {
        missLabel.setText("Miss Counter: " + missCounter);
        strikeLabel.setText("Strike Counter: " + strikeCounter);
        totalMissLabel.setText("Total Misses: " + totalMissCounter);
        totalHitLabel.setText("Total Hits: " + totalHitCounter);
    }

    private void checkGameOver() {
        if (strikeCounter >= 3) {
            int result = JOptionPane.showConfirmDialog(this, "You have lost the game! Would you like to play again?", "Game Over", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                startNewGame();
            } else {
                System.exit(0);
            }
        } else if (totalHitCounter >= 17) { // All ship cells have been hit
            int result = JOptionPane.showConfirmDialog(this, "You have won the game! Would you like to play again?", "Victory!", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                startNewGame();
            } else {
                System.exit(0);
            }
        }
    }

    private class CellClickHandler implements ActionListener {
        private final int row;
        private final int col;

        public CellClickHandler(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JButton button = boardButtons[row][col];
            if (shipGrid[row][col] == 1) { // HIT
                button.setBackground(Color.RED);
                totalHitCounter++;
                missCounter = 0;
                if (isShipSunk(row, col)) {
                    JOptionPane.showMessageDialog(BattleshipGame.this, "You sunk a ship!");
                }
            } else { // MISS
                button.setBackground(Color.YELLOW);
                missCounter++;
                totalMissCounter++;
                if (missCounter == 5) {
                    missCounter = 0;
                    strikeCounter++;
                }
            }
            updateLabels();
            button.setEnabled(false);
            checkGameOver(); // Check after each move if game should end
        }

        private boolean isShipSunk(int row, int col) {
            // Implement logic to check if entire ship is hit (this will depend on your ship placement logic)
            // For now, we'll return false as a placeholder
            return false;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BattleshipGame().setVisible(true));
    }
}
