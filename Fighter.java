/** 
 * Inherits from the Entity class. It represents entities that fight.
 * It includes attackPower, defence and medicine fields.
*/

import java.util.Random;

public abstract class Fighter extends Entity {
    protected int attackPower; // [1-3]
    protected int defence; // [1-2]
    protected int medicine; // [0-2]
    
    public Fighter(int x, int y) {
        super(x, y); // Inherites x and y fields from the Entity class.
        Random rand = new Random();
        this.health = 3; // Fighters initially have health level 3
        this.attackPower = rand.nextInt(3) + 1; // Gets a random number between [1-3]
        this.defence = rand.nextInt(2) + 1; // Gets a random number between [1-2]
        this.medicine = rand.nextInt(3); // Gets a random number between [0-2]
    }

    // Getters
    public int getAttackPower() {
        return attackPower;
    }

    public int getDefence() {
        return defence;
    }

    public int getMedicine() {
        return medicine;
    }

    // Setters
    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public void setDefence(int defence) {
        this.defence = defence;
    }

    public void setMedicine(int medicine) {
        this.medicine = medicine;
    }

    /** 
     * Uses the getsDamage method to decrease the health of the entity that gets attacked.
     * Systems out the appropriate message.
    */
    public void attack(Fighter target) {
        // The entity attacks only if it's attackPower >= attackPower of the other entity.
        if (this.attackPower >= target.getAttackPower()) {
            int damage = this.attackPower - target.getDefence();
            if (damage > 0) {
                // If the damage is more than 0 then the health of the attacked entity decreases
                target.getsDamage(damage);
                System.out.println(this.getSymbol() + " at (" + this.x + "," + this.y + ") attacked " + target.getSymbol() + " at (" + target.getX() + "," + target.getY() + ") for " + damage + " damage. " + target.getSymbol() + " health: " + target.getHealth());
            } else {
                System.out.println(this.getSymbol() + " at (" + this.x + "," + this.y + ") attacked " + target.getSymbol() + " at (" + target.getX() + "," + target.getY() + ") but dealt no damage.");
            }
        }
    }

    /** 
     * Shows the quantity of the damage that a Fighter entity gets when another entity attack.
     * For each attack that an entity gets, it's health level decreases.
    */
    public void getsDamage(int damage) {
        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    /** 
     * Method for applying medicine to another entity that is part of the same team. 
    */
    public void useMedicine() {
        if (this.medicine > 0) {
            this.health ++;
            this.medicine--;
        }
    }

    /** 
     * Checks if an entity is alive.
     * When an entity's health is equal or less than 0, it disappears from the map.
    */
    public boolean isAlive() {
        return this.health > 0;
    }
}
