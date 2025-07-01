/**
 * BoardPanel is a custom JPanel responsible for drawing the game map and all entities
 * (Player, Knights, Monsters) on a Swing GUI.
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;
import javax.swing.JPanel;

class BoardPanel extends JPanel {
    private Map map;
    private final Player player;
    private List<Fighter> fighters;

    // Colors for drawing (replace with ImageIcons for sprites)
    private static final Color EARTH_COLOR = new Color(139, 69, 19); // SaddleBrown
    private static final Color WATER_COLOR = new Color(0, 100, 200); // Darker blue
    private static final Color TREE_COLOR = new Color(34, 139, 34); // ForestGreen
    private static final Color KNIGHT_COLOR = Color.BLUE;
    private static final Color MONSTER_COLOR = Color.RED;
    private static final Color PLAYER_COLOR = Color.YELLOW;

    /**
     * Constructor for BoardPanel.
     */
    public BoardPanel(Map map, Player player, List<Fighter> fighters) {
        this.map = map;
        this.player = player;
        this.fighters = fighters;
        setDoubleBuffered(true); // Enables double buffering for smoother animation (if any)
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Calls JPanel's paintComponent method first
        Graphics2D g2d = (Graphics2D) g; // Casts to Graphics2D for more advanced drawing

        // Draws the map terrain
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                char terrainChar = map.getGrid()[y][x]; // Assuming Map class has a getGrid() method

                Color tileColor;

                switch (terrainChar) {
                    case '.': tileColor = EARTH_COLOR; break;
                    case '~': tileColor = WATER_COLOR; break;
                    case '%': tileColor = TREE_COLOR; break;
                    default: tileColor = Color.BLACK; // Default for unknown terrain
                }

                // Draws solid color rectangle for terrain
                g2d.setColor(tileColor);
                g2d.fillRect(x * GameGUI.TILE_SIZE, y * GameGUI.TILE_SIZE, GameGUI.TILE_SIZE, GameGUI.TILE_SIZE);
            }
        }

        // Draws entities on top of the terrain
        // Draws Player
        if (player != null) {
            g2d.setColor(PLAYER_COLOR);
            // Draws a circle for the player
            g2d.fillOval(player.getX() * GameGUI.TILE_SIZE + GameGUI.TILE_SIZE / 4,
                         player.getY() * GameGUI.TILE_SIZE + GameGUI.TILE_SIZE / 4,
                         GameGUI.TILE_SIZE / 2, GameGUI.TILE_SIZE / 2);
        }

        // Draws Fighters (Knights and Monsters)
        for (Fighter fighter : fighters) {
            if (fighter.isAlive()) { // Only draw if alive
                int drawX = fighter.getX() * GameGUI.TILE_SIZE;
                int drawY = fighter.getY() * GameGUI.TILE_SIZE;

                if (fighter instanceof Knight) {
                    g2d.setColor(KNIGHT_COLOR);
                } else if (fighter instanceof Monster) {
                    g2d.setColor(MONSTER_COLOR);
                }

                // Draws filled rectangle for fighters (or image)
                g2d.fillRect(drawX + GameGUI.TILE_SIZE / 8, drawY + GameGUI.TILE_SIZE / 8,
                             GameGUI.TILE_SIZE * 3 / 4, GameGUI.TILE_SIZE * 3 / 4);
                g2d.setColor(Color.WHITE); // Health text color
                g2d.drawString(String.valueOf(fighter.getHealth()), drawX + GameGUI.TILE_SIZE / 3, drawY + GameGUI.TILE_SIZE * 2 / 3);
            }
        }
    }
}
