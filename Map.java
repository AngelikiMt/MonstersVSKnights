/** 
 * Map class represents the game's world. 
 * It handles everything related to the physical layout of the world, 
 * such as its size, the types of terrain and where the game entities are located.
 * There are no setters for heigth and width, as map's directions will be set via mapDimensionsArray. 
*/

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {
    private char[][] mapDimensionsArray; // A 2D array of characters (earth, water, trees). It holds map's layout.
    private int width; // Stores the width of the map
    private int height; // Stores the heigth of the map
    private Random rand; // An instance of the random class. For randomly placing elements on the map (earth, water, trees or entities).

    // Constructor that is called when creating a new Map object
    public Map(int width, int height) {
        this.width = width;
        this.height = height;
        this.mapDimensionsArray = new char[width][height]; // Initializes the 2D array
        this.rand = new Random(); // Initializes the Random object
        placingElementsOnMap(); // Calling the method for initialize map with terrain types.
    }

    // Getters
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /** 
     * Fills the mapDimensionsArray with terrain types [earth (.), water (~) and trees (%)].
     * It is private as it is an internal helper method called by the constructor.
     * Random class is used for randomly place each element on the map. 
    */
    public void placingElementsOnMap() {
        // The elements cannot be placed out of the 2D array height or width.
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int elementType = rand.nextInt(10); // Chooses a random number between 0-9.
                if (elementType < 7) { // 70% chance for a cell to be earth
                    mapDimensionsArray[i][j] = '.'; // Then at the specific coordinate the earth element is placed
                } else if (elementType < 9) { // 20% chance for a cell to be trees
                    mapDimensionsArray[i][j] = '%'; // Then at the specific coordinate the trees element is placed
                } else { // 10% chance for a cell to be water
                    mapDimensionsArray[i][j] = '~'; // Then at the specific coordinate the water element is placed
                }
            }
        }
    }

    /** 
     * Checks if a gives (x,y) coordinate pair falls within the boundaries of the map.
    */
    public boolean isValidPosition(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return true;
        } else {
            return false;
        }
    }

    /** 
     * Checks if a specific mapp xell at (x,y) is a valid place for an entity to move or be placed 
     * (an entity can move on earth) 
    */
    public boolean isFreeSpace(int x, int y) {
        return mapDimensionsArray[y][x] != '~' && mapDimensionsArray[y][x] != '%';
    }

    /** 
     * Places an entity on the map on a random available (empty and not on an obstacle) spot on the map. 
    */
    public boolean placeEntityOnMap(Entity entity) {
        // It picks a random starting point to begin searching for a free spot.
        int startX = rand.nextInt(width);
        int startY = rand.nextInt(height);

        for (int i = 0; i < width * height; i++) { // width * height is the total number of cells on the map
            // Visits every cell of the map starting from a random point and wrapping arround
            int currentX = (startX + i % width) % width;
            int currentY = (startY + i / width) % height;
        
            // Checks if the terrain at (currentX, currentY) is not water or trees
            // Checks if there is already another entity at the specific position
            if (isFreeSpace(currentX, currentY) && getEntityAt(currentX, currentY) == null) {
                // If a suitable spot is found, the entity's coordinates are updated.
                entity.setX(currentX);
                entity.setY(currentY);
                return true;
            }
        }
        return false; // No free space found
    }

    /** 
     * Returns the entity object that is currently at the specified (x,y) coordinates
     * Returns null if a position does not exists. 
     * Here null is a placeholder as this function is managed by the Game class. 
    */
    public Entity getEntityAt(int x, int y) {
        return null;
    }

    List<Entity> entities = new ArrayList<>();
    /** 
     * Updates map with current enities' positions. 
    */
    public void updateMap(List<Entity> entities) {
        for (Entity entity : entities) {
            if (isValidPosition(entity.getX(), entity.getY())) {
                mapDimensionsArray[entity.getY()][entity.getX()] = entity.getSymbol();
            }
        }
    }

    /** 
     * Displays the current state of the map on the console. 
    */
    public void display() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(mapDimensionsArray[i][j]); // Prints each character side-by-side
            }
            System.out.println(); // Prints an extra new line after each row
        }
        System.out.println(); // Prints a new line at the end
    }

    /**
     * Returns the 2D character array representing the map grid.
    */
    public char[][] getGrid() {
        return mapDimensionsArray;
    }
}
