package Views;

import Controllers.GameController;
import Models.*;

import java.util.ArrayList;
import java.util.Scanner;

public class GameView {
    private boolean isFirstEntry = true;

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
        String name = input.nextLine();
        return name;
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

    public void showMap(String playerRoom, Room currentRoom) {
        // TODO: Map showing the different floors
    }

    public void printHelpList() {
        outputString("The navigation commands are:");
        outputString("N");
        outputString("E");
        outputString("S");
        outputString("W");
        outputString("BACK");
        outputString("MAP");
        outputString("LOCATION");
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
        outputString("PICKUP");
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
        switch (command) {
            case "N":
                outputString("to go to the room to the North");
                break;
            case "E":
                outputString("to go to the room to the East");
                break;
            case "S":
                outputString("to go to the room to the South");
                break;
            case "W":
                outputString("to go to the room to the West");
                break;
            case "BACK":
                outputString("to go to the last visited room");
                break;
            case "MAP":
                outputString("Displays a map of the game showing explored locations.");
                break;
            case "LOCATION":
                outputString("Provides the player’s current location and a room description.");
                break;
            case "EXPLORE":
                outputString("Provides a detailed description of the current room.");
                break;
            case "SEARCH":
                outputString("Searches the room for hidden objects, loot, or clues.");
                break;
            case "INTERACT":
                outputString("Engages with objects, puzzles, or mechanisms.");
                break;
            case "LISTEN":
                outputString("Detects subtle auditory cues like enemy movements or hidden dangers.");
                break;
            case "STEALTH":
                outputString("Attempts to move undetected through an area, avoiding enemies or traps.");
                break;
            case "UNLOCK DOOR ":
                outputString("Attempts to open a locked passage; success will depend on if the player has collected the required number of keys.\n");
                break;
            case "FIGHT":
                outputString("Initiates combat with an enemy.");
                break;
            case "BLOCK":
                outputString("Defends against an enemy’s attack, reducing damage.");
                break;
            case "FLEE":
                outputString("Attempts to escape combat; success depends on the enemy.");
                break;
            case "STATS":
                outputString("Displays the player’s current health, strength, and defense.\n");
                break;
            case "SOLVE":
                outputString("Attempts to solve a puzzle by entering an answer or interacting with objects.");
                break;
            case "IGNORE":
                outputString("Skips a puzzle if allowed (may block progress or rewards).");
                outputString(" -OR- ");
                outputString("Leaves an item behind instead of picking it up.");
                break;
            case "HINT":
                outputString("(For applicable puzzles) Provides a hint at the cost of a small penalty.\n");
                break;
            case "PICKUP":
                outputString("Adds an item to the player's inventory, provided they don’t already have one of that type.");
                break;
            case "SWAP":
                outputString("Exchanges an equipped item for a newly found one.");
                break;
            case "SEE":
                outputString("Revisits a previously ignored item to pick it up, swap, or leave it.");
                break;
            case "INVENTORY":
                outputString("Displays all currently held items and their effects.");
                break;
            case "USE":
                outputString("Uses a consumable item if available and not on cooldown.");
                break;
            case "HELP":
                outputString("Displays a categorized list of all available commands.");
                break;
            case "LOAD":
                outputString("LOAD <fileName> - Loads a previously saved game.");
                break;
            case "SAVE":
                outputString("SAVE <fileName> - Saves your current game.");
                break;
            case "EXIT":
                outputString("Ends the game session.");
                break;
        }
    }

    // General output function
    public void outputString(String message) {
        System.out.println(message);
    }

}
