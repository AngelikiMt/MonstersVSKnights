/** 
 * Inherits from the Fighter class.
*/

import java.util.Random;

public class Knight extends Fighter {
    public Knight(int x, int y) {
        super(x, y);
        this.symbol = 'K';
    }

    @Override
    public void move(Map map) {
        Random rand = new Random(); // for randomly moving on the map.
        int newX = this.x;
        int newY = this.y;
        int direction = rand.nextInt(4);

        switch (direction) {
            case 0: // up
                newY--;
                break;
            case 1: // down
                newY++;
                break;
            case 2: // left
                newX--;
                break;
            case 3: // right
                newX++;
                break;
        }

        /** 
         * Checks if the entity move inside map's borders 
         * by calling the isValidPosition method from the map instance of the Map class. 
        */
        if (map.isValidPosition(newX, newY) && map.isFreeSpace(newX, newY)) {
            this.x = newX;
            this.y = newY;
        }
    }
}