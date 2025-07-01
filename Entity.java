/** 
 * The main Enity that represents whichever entity on the map.
 * It includes common aspects such as position x and y on the map and their health level. 
*/

public abstract class Entity {
    protected int x;
    protected int y;
    protected char symbol; // Initializes a symbol for each entity to be represented on the map.
    protected int health; // initializes health. It will be defined in subclasses. 

    public Entity(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHealth() {
        return health;
    }

    public char getSymbol() {
        return symbol;
    }

    // Setters
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setSymbol(char symbol) {
        this.symbol = symbol;
    }

    /** 
     * Abstract method for an entity's move. 
     * The Map parameter was given for ensuring each move to be valid 
     * avoiding obstacles or moving out of the permited borders. 
    */
    public abstract void move(Map map);
}
