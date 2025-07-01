/** Inherites from Fighter class. */

import java.util.Random;

public class Monster extends Fighter {
    public Monster(int x, int y) {
        super(x, y); // Inherites x and y from the Fighter class
        this.symbol = 'M';
    }

    @Override
    public void move(Map map) {
        Random rand = new Random(); // For randomly move on map.
        int newX = this.x;
        int newY = this.y;
        int direction = rand.nextInt(8);

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
            case 4: // diagonally up-left
                newX--;
                newY--;
                break;
            case 5: // diagonally up-right
                newX++;
                newY--;
                break;
            case 6: // diagonally down-left
                newX--;
                newY++;
                break;
            case 7: // diagonally down-right
                newX++;
                newY++;
                break;
        }

        // Checks if the entity moves inside the maps borders.
        if (map.isValidPosition(newX, newY) && map.isFreeSpace(newX, newY)) {
            this.x = newX;
            this.y = newY;
        }
    }
}