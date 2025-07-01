/** 
 * The Main class calls and runs the game class. 
*/

import java.util.Scanner;
import javax.swing.SwingUtilities;

/**
 * MainApplication provides a single entry point for the game,
 * allowing the user to choose between the terminal-based version and the GUI-based (Swing) version.
 */
public class Main {

    public static void main(String[] args) {
        Scanner chooserScanner = new Scanner(System.in);
        System.out.println("Choose one of the bellow options:");
        System.out.println("1. Terminal-based game");
        System.out.println("2. GUI (Swing)-Based game");
        System.out.print("Enter a number (1 or 2): ");

        int choice = -1;
        boolean validChoice = false;
        while (!validChoice) {
            try {
                choice = chooserScanner.nextInt();
                if (choice == 1 || choice == 2) {
                    validChoice = true;
                } else {
                    System.out.print("Invalid selection. Please enter 1 or 2: ");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.print("Invalid input. Please enter a number (1 or 2): ");
                chooserScanner.next(); // Consumes the invalid input
            }
        }
        
        if (choice == 1) {
            System.out.println("Launch Terminal version...");
            Game terminalGame = new Game(); // Creates a new instance of the core game
            terminalGame.start(); // Starts the terminal-specific game loop
        } else { // choice == 2
            System.out.println("Launch GUI version (Swing)...");
            // Launchs the GUI on the Event Dispatch Thread
            SwingUtilities.invokeLater(() -> new GameGUI());
        }
    }
}
