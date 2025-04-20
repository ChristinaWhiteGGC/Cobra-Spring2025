package Views;
import Models.*;
import java.util.Scanner;

public class GameView {
    // printRoom method tells the user the room they are in and informs them if they have previously visited the room
    public void printRoom(Room room) {
        System.out.println("Welcome to the " + room.getName() + ".");
        if (room.getIsVisited()) {
            System.out.println("You have visited this room.");
            room.setVisited();
        } else {
            System.out.println(room.getDescription());
        }
    }

    public String getPlayerName() {
        System.out.println("Please enter your name: ");
        Scanner input = new Scanner(System.in);
        return input.nextLine();
    }

    public void printPlayerStats(Player p) {
        outputString("-Player-");
        outputString("Name: " + p.getName());
        outputString("Health: " + p.getHp());
        outputString("Damage: " + p.getStr());
    }

    public void printStats(Player p) {
        printPlayerStats(p);
        // TODO: Implement monster stats?
    }

    // getRoomInput method asks user where they would like to go in the game and verifies if input is valid
    // returns directional input as a string or tells the user to reenter valid direction.
    public String[] getRoomInput() {
        System.out.println("What would you like to do?");
        System.out.println("Enter command: ");
        Scanner userInput = new Scanner(System.in);
        String command = userInput.nextLine();
        return command.split(" ");
    }

    public String getAnswer() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public void printHelpList() {
        outputString("The navigation commands are:");
        outputString("N");
        outputString("E");
        outputString("S");
        outputString("W");
        outputString("BACK");
        outputString("MAP");
        outputString("LOCATION OR WHEREAMI");
        outputString("\nExploration & Interaction Commands:");
        outputString("EXPLORE");
        outputString("SEARCH");
        outputString("INTERACT");
        outputString("LISTEN");
        outputString("STEALTH");
        outputString("UNLOCK DOOR");
        outputString("\nCombat Commands:");
        outputString("FIGHT");
        outputString("BLOCK");
        outputString("FLEE ");
        outputString("STATS");
        outputString("\nPuzzle Commands:");
        outputString("SOLVE");
        outputString("IGNORE");
        outputString("HINT");
        outputString("\nItem & Inventory Commands:");
        outputString("PICKUP <item>");
        outputString("SWAP");
        outputString("IGNORE");
        outputString("SEE <item>");
        outputString("INVENTORY");
        outputString("USE <item>");
        outputString("\nGeneral Commands:");
        outputString("HELP");
        outputString("HELP <command>");
        outputString("LOAD <fileName>");
        outputString("SAVE <fileName>");
        outputString("EXIT");
    }

    public void printDetailedHelp(String command) {
        switch (command.toUpperCase()) {
            case "N" -> outputString("Directional input to go to the room to the North");
            case "E" -> outputString("Directional input to go to the room to the East");
            case "S" -> outputString("Directional input to go to the room to the South");
            case "W" -> outputString("Directional input to go to the room to the West");
            case "BACK" -> outputString("Directional input to go to the last visited room");
            case "MAP" -> outputString("Displays a map of the game showing explored locations.");
            case "LOCATION", "WHEREAMI" -> outputString("Provides the player’s current location and a room description.");
            case "EXPLORE" -> outputString("Provides a detailed description of the current room.");
            case "SEARCH" -> outputString("Searches the room for hidden objects, loot, or clues.");
            case "INTERACT" -> outputString("Engages with objects, puzzles, or mechanisms.");
            case "LISTEN" -> outputString("Detects subtle auditory cues like enemy movements or hidden dangers.");
            case "STEALTH" -> outputString("Attempts to move undetected through an area, avoiding enemies or traps.");
            case "UNLOCK DOOR " ->
                    outputString("Attempts to open a locked passage; success will depend on if the player has collected the required number of keys.\n");
            case "FIGHT" -> outputString("Initiates combat with an enemy.");
            case "BLOCK" -> outputString("Defends against an enemy’s attack, reducing damage.");
            case "FLEE" -> outputString("Attempts to escape combat; success depends on the enemy.");
            case "STATS" -> outputString("Displays the player’s current health, strength, and defense.\n");
            case "SOLVE" ->
                    outputString("Attempts to solve a puzzle by entering an answer or interacting with objects.");
            case "IGNORE" -> {
                outputString("Skips a puzzle if allowed (may block progress or rewards).");
                outputString(" -OR- ");
                outputString("Leaves an item behind instead of picking it up.");
            }
            case "HINT" -> outputString("(For applicable puzzles) Provides a hint at the cost of a small penalty.\n");
            case "PICKUP <item>" ->
                    outputString("Adds an item to the player's inventory, provided they don’t already have one of that type.");
            case "SWAP" -> outputString("Exchanges an equipped item for a newly found one.");
            case "SEE" -> outputString("Revisits a previously ignored item to pick it up, swap, or leave it.");
            case "INVENTORY" -> outputString("Displays all currently held items and their effects.");
            case "USE" -> outputString("Uses a consumable item if available and not on cool-down.");
            case "HELP" -> outputString("Displays a categorized list of all available commands.");
            case "LOAD" -> outputString("LOAD <fileName> - Loads a previously saved game.");
            case "SAVE" -> outputString("SAVE <fileName> - Saves your current game.");
            case "EXIT" -> outputString("Ends the game session.");
        }
    }

    // General output function
    public void outputString(String message) {
        System.out.println(message);
    }
    public void printGameTitle() {
        System.out.println("           /\\");
        System.out.println("          /  \\");
        System.out.println("         /    \\");
        System.out.println("        /      \\");
        System.out.println("       /        \\");
        System.out.println("      /  PYRAMID \\");
        System.out.println("     /    PLUNDER \\");
        System.out.println("    /______________\\");
        System.out.println("    | []  ||  [] [] |");
        System.out.println("    |     ||        |");
        System.out.println("    | []  ||  [] [] |");
        System.out.println("    |     ||        |");
        System.out.println("    | []  ||  [] [] |");
        System.out.println("    |     ||        |");
        System.out.println("    |____/||\\______|");
    }
    public void showMap() {
        String map = """
                       
                                          [ R30 ]    
                       Floor 3
                               
                                 [ R26 ]--[ R25]--[ R24 ]
                                    |                |
                                 [ R27 ]           [ R23 ]
                                     |                |
                                 [ R28 ]--[ R29 ]  [ R22 ]
                                         
                       Floor 2
                       
                       [ R04 ]           [ R01 ]           [ R07 ]
                          |		            |	              |
                       [ R03 ]-----------[ R02 ]--[ R05 ]--[ R06 ]
                           		                              |
                       [ R12 ]--[ R11 ]--[ R10 ]--[ R09 ]--[ R08 ]
                             		        |                      
                       [ R20 ]--[ R21 ]  [ R13 ]--[ R14 ]--[ R15 ]
                          |                 |                 |                              \s
                       [ R19 ]--[ R18 ]--[ R17 ]           [ R13 ]
                       
                       Floor 1
                                       
                 
                """;

        // Display the map
        System.out.printf(map);
    }
}
