/** 
 * This class manages the game logic for the terminal-based option.
 * It manages the entities and their interactions, the map and the game flow.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Game {
    private Map map;
    private Player player;
    private List<Fighter> fighters; // A list of Knights and Monsters
    private int knightsCount;
    private int monstersCount;
    private Scanner scanner;
    private boolean gameOver;
    private boolean paused;
    
    // Constructor
    public Game() {
        scanner = new Scanner(System.in); // Initializes a Scanner to read user input from the console.
        fighters = new ArrayList<>(); // Initializes an empty list to hold all active Knight and Monster objects.
        gameOver = false; // Sets the game over flag to false initially.
        paused = false; // Sets the paused flag to false initially.
    }

    /** 
     * Starts the game.
     * Asks the map dimensions from the user and initializes the world of the game. 
    */
    public void start() {
        System.out.println("Welcome to the Knights VS Monsters game!");
        System.out.print("Enter the width of your map: ");
        int width = scanner.nextInt();
        System.out.print("Enter the height of your map: ");
        int height = scanner.nextInt();
        scanner.nextLine(); // Cleaning buffer

        // Creates a new Map object with the specified dimensions given by the user.
        map = new Map(width, height) {
            @Override // Overrides the getEntityAt method from the Map class to provide access to the Game's enity lists.
            public Entity getEntityAt(int x, int y) {
                // Checks if the player is at the given coordinates.
                if (player.getX() == x && player.getY() == y) {
                    return player;
                }
                // Checks if any fighter is at the given coordinates
                for (Fighter fighter : fighters) {
                    if (fighter.getX() == x && fighter.getY() == y) {
                        return fighter;
                    }
                }
                return null; // No entity found at these coordinates
            }
        };
        // Calls the method for initialize and place the game entities on the map
        initializeEntities(width, height);
        // Calls the method for starting the main game loop
        gameLoop();
    }

    /** 
     * Initializes the entities and places them on the map. 
    */
    private void initializeEntities(int width, int height) {
        // Creates and places the player avatar.
        player = new Player(0, 0); // Initial coordinates are placeholders, placeEntityOnMap will update them.
        if (!map.placeEntityOnMap(player)) { // Tries to Place the player on a random free spot.
            System.err.println("Player couldn't be placed on the map. The game will be terminated");
            System.exit(1); // Exits the program if the player can't be placed.
        }

        // Calculates the maximum number of fighters for each team based on map size.
        // (width * height) / 15 ensures that each team has a maximum of 1/15th of the map's total cells.
        int maxFighters = (width * height) / 15;
        Random rand = new Random();

        knightsCount = 0;
        monstersCount = 0;

        // Places Knights and Monsters. It tries to keep the counts balanced (knightsCount <= monstersCount).
        for (int i = 0; i < maxFighters; i++) {
            if (knightsCount <= monstersCount) { // If Knights are less than or equal to Monsters, add a Knight.
                Knight knight = new Knight(0, 0); // Creates a new Knight object with coordinates placeholder.
                if (map.placeEntityOnMap(knight)) { // Tries to place the knight on the map.
                    fighters.add(knight); // If successful, adds the knight to the main fighters list.
                    knightsCount++; // Increments Knight count.
                }
            } else {
                Monster monster = new Monster(0, 0);
                if (map.placeEntityOnMap(monster)) {
                    fighters.add(monster);
                    monstersCount++;
                }
            }
        }
        System.out.println("First placement of entities: ");
        System.out.println("Knights: " + knightsCount + ", Monsters: " + monstersCount);
    }

    /** 
     * Main Game Loop. 
    */
    private void gameLoop() {
        while (!gameOver) {
            clearConsole(); // Clears the console for a clean display in each turn.
            map.updateMap(getAllEntities()); // Updates the map's internal array with the current positions. of the entities.
            map.display();
            System.out.println("Total Knights: " + knightsCount + ", total Monsters: " + monstersCount);
            System.out.println("Player is at position: (" + player.getX() + ", " + player.getY() + ")");

            if (!paused) { // If the game is not paused, allow player movement and game progression.
                // Player's move (w:up, s:down, a:left, d:right)
                System.out.print("Move (w/a/s/d), Pause (p), Exit (q): ");
                String input= scanner.nextLine().toLowerCase();

                // Handles player's input.
                switch(input) {
                    case "w": movePlayer(-1, 0); break; // Up
                    case "s": movePlayer(1, 0); break; // Down
                    case "a": movePlayer(0, -1); break; // Left
                    case "d": movePlayer(0, 1); break; // Right
                    case "p": togglePause(); break; // Pause game
                    case "q": quitGame(); break; // Exit game
                    default: System.out.println("No valid move. Please, try again."); break;
                }
            } else { // If the game is paused, the system allows the player only to unpause the game or quitting.
                // Display info when in pause
                displayPauseInfo();
                System.out.print("Enter 'p' for continue playing or 'q' for exit the game: ");
                String input = scanner.nextLine().toLowerCase();
                if (input.equals("p")) {
                    togglePause();
                } else if (input.equals("q")) {
                    quitGame();
                }
                continue; // If none of the aboce contitions is try, then the game stays on pause.
            }
            if (!gameOver && !paused) {
                // Fighters turn to move
                fightersTurn();
                checkGameEnd();
            }
            try {
                TimeUnit.MILLISECONDS.sleep(200); // Delays for visuals
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("Game over!");
    }

    /** 
     * Moves player 
    */
    private void movePlayer(int dy, int dx) {
        int newX = player.getX() + dx; // Calculates potential new X position.
        int newY = player.getY() + dy; // Calculates potential new Y position.

        // Calls the player's own movePlayer method, which checks validity and updates position.
        if (player.movePlayer(newX, newY, map)) {
            // Player successfully moved! Logic here will be handled in the gameLoop().
        } else {
            System.out.println("Tou cannot move there!");
        }
    }

    /** 
     * Fighter turn.
     * Moves the Fighters (Knights and Monsters). It includes movements and interactions 
    */
    private void fightersTurn() {
        List<Fighter> fightersToRemove = new ArrayList<>(); // List to store fighters that are defeated in this turn.
        Random rand = new Random();

        // Fighter's movement
        // Iterates through all active fighters and make them move accordingly with their rules.
        for (Fighter fighter : fighters) {
            fighter.move(map); // Calls the polymorphic move() method
        }

        // Interactions (attack or heal)
        for (Fighter fighter : fighters) {
            if (!fighter.isAlive()) {
                continue; // Skip interactions if the fighter is already defeated
            }
            // Checks positions around the fighter for interaction
            for (int dy = -1; dy <= 1; dy++) {
                for (int dx = -1; dx <= 1; dx++) {
                    if (dx == 0 && dy == 0) {
                        continue; // Skips the fighter's own position.
                    }
                    int neighborX = fighter.getX() + dx;
                    int neighborY = fighter.getY() + dy;

                    // Ensures the neighbor position is within map boundaries.
                    if (map.isValidPosition(neighborX, neighborY)) {
                        Entity neighbor = map.getEntityAt(neighborX, neighborY); // Get entity at neighbor position.

                        if (neighbor instanceof Fighter) {
                            Fighter target = (Fighter) neighbor;

                            if (fighter.getClass().equals(target.getClass())) {
                                
                                // Same team - Interaction: Healing
                                // A fighter can heal if its health is not full (health < 3) and it has medicine.
                                if (fighter.getHealth() < 3 && fighter.getMedicine() > 0) {
                                    if (rand.nextBoolean()) { // Random medicine
                                        fighter.useMedicine();
                                        target.setMedicine(target.getMedicine() - 1); // Heals the fighter (health increases, medicine decreases for the giver).
                                        System.out.println(fighter.getSymbol() + " at (" + fighter.getX()  + ", " + fighter.getY() + ") received medicine from " + target.getSymbol() + ". Health: " + fighter.getHealth());
                                    }
                                }
                            } else {
                                // Opposite team - Interaction: attack
                                if (fighter.getAttackPower() >= target.getAttackPower()) {
                                    fighter.attack(target); // Calls the attack method on the attacker.
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

        // Removes dead Fighters
        // Iterate through the list of fighters marked for removal and remove them from the main list.
        for (Fighter deadFighter : fightersToRemove) {
            fighters.remove(deadFighter);
            if (deadFighter instanceof Knight) {
                knightsCount--;
            } else if (deadFighter instanceof Monster) {
                monstersCount--;
            }
            System.out.println(deadFighter.getSymbol() + " at (" + deadFighter.getX() + ", " + deadFighter.getY() +  ") was defeated!");
        }
    }

    /** 
     * Checks if the game is over 
    */
    private void checkGameEnd() {
        if (knightsCount == 0) {
            System.out.println("All knights have been defeated! Monsters have WON!");
            gameOver = true;
        } else if (monstersCount == 0) {
            System.out.println("All monsters have been defeated! Knights have WON!");
            gameOver = true;

        }
    }

    /** 
     * Pauses the game or continues it. 
    */
    private void togglePause() {
        paused = !paused;
        System.out.println(paused ? "Game is on pause." : "Game continues.");
    }

    /** 
     * Displays info when game is on pause. 
    */
    private void displayPauseInfo() {
        int totalMonsterHealth = 0;
        int totalKnightHealth = 0;

        // Calculates total health for each team by iterating through active fighters.
        for (Fighter fighter : fighters) {
            if (fighter instanceof Monster) {
                totalMonsterHealth += fighter.getHealth();
            } else if (fighter instanceof Knight) {
                totalKnightHealth += fighter.getHealth();
            }
        }

        System.out.println("\n--- Game info ---");
        System.out.println("Active Monsters: " + monstersCount);
        System.out.println("Active Knights: " + knightsCount);
        System.out.println("Total health of Monsters: " + totalMonsterHealth);
        System.out.println("Total health of Knights: " + totalKnightHealth);
        System.out.println("-------------------------------\n");    
    }

    /** 
     * Game over 
    */
    private void quitGame() {
        gameOver = true;
        System.out.println("Player terminated the game.");
    }

    /** 
     * Returns a list with all active entties (Players and Fighters). 
    */
    private List<Entity> getAllEntities() {
        List<Entity> allEntities = new ArrayList<>(fighters);
        allEntities.add(player);
        return allEntities;
    }

    /** 
     * Clears console 
    */
    private void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\\033[H\\033[2J");
                System.out.flush();
            }
        } catch (final Exception e) {
            System.err.println("exception: " + e);
        }
    }
}

