/** 
 * Inherits from the Entity class. 
 * It represents player's avatar. It cannot attack or be attacked. 
*/

public class Player extends Entity {
    public Player(int x, int y) {
        super(x, y);
        this.symbol = 'A';
        this.health = Integer.MAX_VALUE; // The player has the maximum health level as he cannot been attacked.
    }

    @Override
    public void move(Map map) {} // Player's moves are not random like the other entities.

    /** 
     * Function for moing player into a new position.
     * It checks if the player moves inside map's borders and makes the move. 
    */ 
    public boolean movePlayer(int newX, int newY, Map map) {
        if (map.isValidPosition(newX, newY) && map.isFreeSpace(newX, newY)) {
            this.x = newX;
            this.y = newY;
            return true;
        }
        return false;
    }
}
