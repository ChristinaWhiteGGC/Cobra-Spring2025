package Controllers;

import Exceptions.*;
import Models.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Views.GameView;

public class GameController {

    // Array list to store the rooms in
    private Map<Integer, Room> roomsList;

    Map<String, Artifact> artifactList;

    private Player player;

    private Room currentRoom;

    private final GameView view;

    private GameStateManager gsm;

    public GameController(GameView view, GameStateManager gsm) {
        this.view = view;
        this.gsm = gsm;
        this.roomsList = gsm.getRooms();
        this.artifactList = gsm.getArtifacts();
    }

    public void initialize() throws Exception {
        // Map puzzles to their initial rooms
        String playerName = "";
        while (playerName == null || playerName.trim().isEmpty()) {
            playerName = view.getPlayerName();
            if (playerName == null || playerName.trim().isEmpty()) {
                view.outputString("Please enter a valid name to start the game.");
            }
        }

        // Instantiates a player and sets the room to the first room in the list
        player = new Player(playerName, 100, 0, 0);
        gsm.resetGame(player);
        view.outputString("Welcome to Pyramid Plunder " + playerName + "! Explore the rooms, solve puzzles, fight monsters, and find items.");
        startGame();
    }

    public void startGame() {
        // enters game at room index 0 (library)
        boolean isEntranceToGame = true;

        int nextRoomIndex = 1;

        // Controls whether player has input a direction
        // Resets on each iteration, defaults to true for first iteration of while loop to print the
        // room player starts in when game is initialized.
        boolean isMovingRooms = true;

        while (true) {
            // If we are moving rooms, lets display the room message,
            // and if applicable the puzzle attached to the room
            if (isMovingRooms) {
                // Print the room description
                    view.printRoom(player.getRoom());
                    isMovingRooms = false;
            }

            // keeps first room as not visited for 1st entry
            // any future visits to any room will set the visited status
            if (!isEntranceToGame) {
                player.getRoom().setVisited();
            }

            // Ensure that the above if condition will now function properly and not be skipped
            isEntranceToGame = false;

            /* the do-while loop asks the user for directional input
                and validates that it is a direction they can go, if they input an invalid
                direction then it repeats the request for them to input a valid direction
            */
            do {
                String[] command = view.getRoomInput();
                // Check if input is empty or invalid
                if (command.length == 0 || command[0].isEmpty()) {
                    view.outputString("Invalid input received. Please enter a command.");
                    continue;  // Ask for input again
                }
                try {
                    switch (command[0]) {
                        case "N":
                        case "E":
                        case "S":
                        case "W":
                            isMovingRooms = true;
                            nextRoomIndex = player.getRoom().getExit(command[0]);
                            if (nextRoomIndex == 0) {
                                view.outputString("You can't go this way.");
                            }
                            break;
                        case "EXIT", "X":
                            view.outputString("Thanks for playing Pyramid Plunder!");
                            System.exit(0);
                        case "SAVE":
                            // TODO: Implement save
                            break;
                        case "LOAD":
                            // TODO: Implement load
                            break;
                        default:
                            view.outputString("Invalid input received. Please enter a valid command. Enter 'help' to see list of valid commands.");
                    }
                } catch (Exception e) {
                    view.outputString(e.getMessage());
                }
            } while (nextRoomIndex == 0);
            // after valid input is received current room is set to room entered by user
            player.setRoom(getRoom(nextRoomIndex));
        }

    }

    public Room getRoom(int roomIndex) {
        return roomsList.get(roomIndex);
    }
}
