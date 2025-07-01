/** 
 * This class manages the game logic for the Swing-based Graphical User Interface (GUI).
 * It orchestrates the map, entities, game loop, and user interactions.
 */

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class GameGUI extends JFrame {

    private Map map;
    private Player player;
    private List<Fighter> fighters;
    private int knightsCount;
    private int monstersCount;
    private boolean gameOver;
    private boolean paused;

    private BoardPanel boardPanel; // Custom JPanel for drawing the game board
    private JLabel infoLabel;      // Label to display game information (counts, player position)
    private JLabel pauseInfoLabel; // Label to display pause information (total health)
    private JPanel controlPanel;   // Panel for game information and controls

    // Constants for rendering (adjust as needed for desired tile size)
    public static final int TILE_SIZE = 40; // Size of each cell/tile in pixels

    /**
     * Constructor for the GameGUI class.
     * Initializes the game state and sets up the Swing GUI.
     */
    public GameGUI() {
        // Initialize game state variables
        fighters = new ArrayList<>();
        gameOver = false;
        paused = false;

        // Set up the JFrame (main window)
        setTitle("Knights vs. Monsters");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Closes operation
        setResizable(false); // Prevents resizing for simpler layout management

        // Get map dimensions from user via a dialog
        int width = 0;
        int height = 0;
        boolean dimensionsValid = false;
        while (!dimensionsValid) {
            try {
                String widthStr = JOptionPane.showInputDialog(this, "Enter map width (e.g. 20):", "Διαστάσεις Χάρτη", JOptionPane.QUESTION_MESSAGE);
                if (widthStr == null) { // User clicked Cancel
                    System.exit(0);
                }
                width = Integer.parseInt(widthStr);

                String heightStr = JOptionPane.showInputDialog(this, "Enter map height (e.g. 15):", "Διαστάσεις Χάρτη", JOptionPane.QUESTION_MESSAGE);
                if (heightStr == null) { // User clicked Cancel
                    System.exit(0);
                }
                height = Integer.parseInt(heightStr);

                if (width > 0 && height > 0) {
                    dimensionsValid = true;
                } else {
                    JOptionPane.showMessageDialog(this, "Width and height must be positive numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid whole numbers for dimensions.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }


        // Initialize the Map object.
        // The Map's getEntityAt method is overridden here to allow it to query entities from Game.
        map = new Map(width, height) {
            @Override
            public Entity getEntityAt(int x, int y) {
                // Checks if the player is at the given coordinates
                if (player != null && player.getX() == x && player.getY() == y) {
                    return player;
                }
                // Checks if any fighter (Knight or Monster) is at the given coordinates
                for (Fighter fighter : fighters) {
                    if (fighter.getX() == x && fighter.getY() == y) {
                        return fighter;
                    }
                }
                return null; // No entity found at these coordinates
            }
        };
        map.placingElementsOnMap(); // Initializes terrain elements on the map once.

        // Initializes entities (player, knights, monsters)
        initializeEntities(width, height);

        // Sets up the GUI components
        setupGUI();

        // Adds KeyListener for player input
        addKeyListener(new GameKeyListener());
        setFocusable(true); // Ensures the JFrame can receive key events
        requestFocusInWindow(); // Requests focus immediately

        // Packs the frame and makes it visible
        pack();
        setLocationRelativeTo(null); // Centers the window on the screen
        setVisible(true);

        // Initials display update
        updateDisplay();
    }

    /**
     * Initializes the entities (Knights, Monsters, Player) and places them on the map.
     */
    private void initializeEntities(int width, int height) {
        // Creates and places the player avatar.
        player = new Player(0, 0); // Initials coordinates are placeholders for placeEntityOnMap
        if (!map.placeEntityOnMap(player)) {
            JOptionPane.showMessageDialog(this, "unable to place player. GAME OVER!", "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Calculates the maximum number of fighters for each team based on map size.
        int maxFighters = (width * height) / 15;
        Random rand = new Random();

        knightsCount = 0;
        monstersCount = 0;

        // Loops to place Knights and Monsters, balancing their counts.
        for (int i = 0; i < maxFighters; i++) {
            if (knightsCount <= monstersCount) {
                Knight knight = new Knight(0, 0);
                if (map.placeEntityOnMap(knight)) {
                    fighters.add(knight);
                    knightsCount++;
                }
            } else {
                Monster monster = new Monster(0, 0);
                if (map.placeEntityOnMap(monster)) {
                    fighters.add(monster);
                    monstersCount++;
                }
            }
        }
        System.out.println("Initial placement of entities: Knights: " + knightsCount + ", Monsters: " + monstersCount);
    }

    /**
     * Sets up the main GUI components: the board panel and the info panel.
     */
    private void setupGUI() {
        setLayout(new BorderLayout()); // Uses BorderLayout for main frame

        // Creates the custom drawing panel (BoardPanel)
        boardPanel = new BoardPanel(map, player, fighters);
        boardPanel.setPreferredSize(new Dimension(map.getWidth() * TILE_SIZE, map.getHeight() * TILE_SIZE));
        add(boardPanel, BorderLayout.CENTER); // Adds board to the center of the frame

        // Creates a panel for information labels at the bottom
        controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS)); // Stacks labels vertically
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Adds padding

        infoLabel = new JLabel("info...");
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centers align the label
        controlPanel.add(infoLabel);

        pauseInfoLabel = new JLabel(""); // This label will only be visible when paused
        pauseInfoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        controlPanel.add(pauseInfoLabel);

        add(controlPanel, BorderLayout.SOUTH); // Adds info panel to the bottom
    }

    /**
     * Updates the display elements (board and info labels).
     */
    private void updateDisplay() {
        // Update the main info label
        infoLabel.setText(String.format("Knights: %d | Monsters: %d | Player: (%d, %d)",
                knightsCount, monstersCount, player.getX(), player.getY()));

        // Updates the board's internal representation
        map.updateMap(getAllEntities()); // Ensures Map's char[][] is up-to-date with entity symbols

        // Requests a repaint of the board panel to draw updated state
        boardPanel.repaint();

        // Shows/hides pause info based on game state
        if (paused) {
            displayPauseInfo();
            pauseInfoLabel.setVisible(true);
        } else {
            pauseInfoLabel.setVisible(false);
        }
    }

    /**
     * Executes one game turn (fighters' movement and interactions).
     */
    private void runGameTurn() {
        if (gameOver || paused) {
            return;
        }

        List<Fighter> fightersToRemove = new ArrayList<>();
        Random rand = new Random();

        // --- Phase 1: Fighter Movement ---
        for (Fighter fighter : fighters) {
            fighter.move(map);
        }

        // --- Phase 2: Interactions (Attack/Heal) ---
        // Creates a copy of fighters list to avoid ConcurrentModificationException
        // if fighters are removed during iteration.
        List<Fighter> currentFighters = new ArrayList<>(fighters);
        for (Fighter fighter : currentFighters) {
            if (!fighter.isAlive()) continue;

            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    if (dx == 0 && dy == 0) continue;

                    int neighborX = fighter.getX() + dx;
                    int neighborY = fighter.getY() + dy;

                    if (map.isValidPosition(neighborX, neighborY)) {
                        Entity neighbor = map.getEntityAt(neighborX, neighborY);

                        if (neighbor instanceof Fighter) {
                            Fighter target = (Fighter) neighbor;

                            // Ensures fighters only interact once per pair per turn to avoid double processing
                            if (fighter.equals(target)) continue; // Don't interact with self

                            if (fighter.getClass().equals(target.getClass())) {
                                // --- Same Team Interaction (Healing) ---
                                // If fighter needs healing AND target has medicine AND target is chosen to give.
                                if (target.getMedicine() > 0 && fighter.getHealth() < 3) {
                                    if (rand.nextBoolean()) { // 50% chance to give/receive
                                        fighter.setHealth(fighter.getHealth() + 1); // Receiver's health goes up
                                        target.setMedicine(target.getMedicine() - 1); // Giver's medicine goes down
                                        System.out.println(target.getSymbol() + " at (" + target.getX() + "," + target.getY() + ") gave medicine to " + fighter.getSymbol() + " at (" + fighter.getX() + "," + fighter.getY() + "). " + fighter.getSymbol() + " Health: " + fighter.getHealth());
                                    }
                                }
                            } else {
                                // --- Opposite Team Interaction (Attack) ---
                                if (fighter.getAttackPower() >= target.getAttackPower()) {
                                    fighter.attack(target);
                                    if (!target.isAlive()) {
                                        fightersToRemove.add(target);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // --- Phase 3: Remove Defeated Fighters ---
        // Removes fighters that were defeated in this turn.
        for (Fighter deadFighter : fightersToRemove) {
            if (fighters.contains(deadFighter)) { // Prevents removing already removed fighters
                fighters.remove(deadFighter);
                if (deadFighter instanceof Knight) {
                    knightsCount--;
                } else if (deadFighter instanceof Monster) {
                    monstersCount--;
                }
                System.out.println(deadFighter.getSymbol() + " at (" + deadFighter.getX() + "," + deadFighter.getY() + ") was defeated!");
            }
        }
        checkGameEnd(); // Checks for game end after processing all interactions and removals.
        updateDisplay(); // Refreshs GUI after turn
    }

    /**
     * Checks if the game has ended by checking if either team's count has reached zero.
     */
    private void checkGameEnd() {
        if (gameOver) return; // Already game over

        String message = null;
        if (knightsCount == 0) {
            message = "All the Knights were defeated! The Monsters won!";
        } else if (monstersCount == 0) {
            message = "All the Monsters were defeated! The Knights won!";
        }

        if (message != null) {
            gameOver = true;
            JOptionPane.showMessageDialog(this, message, "End of the Game!", JOptionPane.INFORMATION_MESSAGE);
            // Optionally, prompt to play again or exit
            int choice = JOptionPane.showConfirmDialog(this, "Would you like to play again;", "End of the game", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                // Restarts game (requires re-initialization logic)
                dispose(); // Closes current window
                SwingUtilities.invokeLater(() -> new Game()); // Creates a new game instance on EDT
            } else {
                System.exit(0); // Exits the application
            }
        }
    }

    /**
     * Toggles the game's pause state.
     */
    private void togglePause() {
        paused = !paused;
        if (paused) {
            JOptionPane.showMessageDialog(this, "Game paused. Type 'p' to resume..", "Pause", JOptionPane.INFORMATION_MESSAGE);
            displayPauseInfo(); // Updates pause info label immediately
            pauseInfoLabel.setVisible(true);
        } else {
            pauseInfoLabel.setVisible(false);
        }
        updateDisplay(); // Refreshs GUI
    }

    /**
     * Displays game information (total health) when the game is paused.
     * Updates the pauseInfoLabel.
     */
    private void displayPauseInfo() {
        int totalMonsterHealth = 0;
        int totalKnightHealth = 0;

        for (Fighter fighter : fighters) {
            if (fighter instanceof Monster) {
                totalMonsterHealth += fighter.getHealth();
            } else if (fighter instanceof Knight) {
                totalKnightHealth += fighter.getHealth();
            }
        }

        pauseInfoLabel.setText(String.format("<html><b>--- Pause Information ---</b><br>" +
                "Total Health Monsters: %d<br>" +
                "Total Health Knights: %d</html>",
                totalMonsterHealth, totalKnightHealth));
    }

    /**
     * Helper method to get a combined list of all active entities (player and fighters).
     * Returns a List containing all active Entity objects.
     */
    private List<Entity> getAllEntities() {
        List<Entity> allEntities = new ArrayList<>(fighters);
        allEntities.add(player);
        return allEntities;
    }

    /**
     * KeyListener implementation for handling player input.
     */
    private class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (gameOver) return; // Ignores input if game is over

            int playerNewX = player.getX();
            int playerNewY = player.getY();
            boolean moved = false;

            // Handles player movement based on key presses
            if (!paused) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_W: playerNewY--; moved = true; break;
                    case KeyEvent.VK_S: playerNewY++; moved = true; break;
                    case KeyEvent.VK_A: playerNewX--; moved = true; break;
                    case KeyEvent.VK_D: playerNewX++; moved = true; break;
                }

                if (moved) {
                    if (player.movePlayer(playerNewX, playerNewY, map)) {
                        runGameTurn(); // A player move triggers a game turn
                    } else {
                        JOptionPane.showMessageDialog(GameGUI.this, "You can't move there!", "Invalid Movement", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }

            // Handles pause/quit keys (always active, even if paused or not a move key)
            switch (e.getKeyCode()) {
                case KeyEvent.VK_P: togglePause(); break;
                case KeyEvent.VK_Q: System.exit(0); break; // Simple exit on 'q'
            }
        }
    }
}
