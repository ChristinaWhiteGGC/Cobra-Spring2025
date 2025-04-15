package Controllers;

import Models.*;
import java.util.Map;

import Views.GameView;

public class GameController {

    // Array list to store the rooms in
    private final Map<Integer, Room> roomsList;

    private final Map<String, Artifact> artifactList;

    private Player player;

    private final GameView view;

    private final GameStateManager gsm;

    public GameController(GameView view, GameStateManager gsm) {
        this.view = view;
        this.gsm = gsm;
        this.roomsList = gsm.getRooms();
        this.artifactList = gsm.getArtifacts();
    }

    public void initialize() {
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
                    switch (command[0].toUpperCase()) {
                        case "N", "E", "S", "W" -> {
                            isMovingRooms = true;
                            nextRoomIndex = player.getRoom().getExit(command[0].toUpperCase());
                            if (nextRoomIndex == 0) {
                                view.outputString("You can't go this way.");
                            }
                        }
                        case "HELP" -> {
                            if (command.length == 1) {
                                view.printHelpList();
                            } else {
                                view.printDetailedHelp(command[1]);
                            }
                        }
                        case "EXIT", "X" -> {
                            view.outputString("Thanks for playing Pyramid Plunder!");
                            System.exit(0);
                        }
                        case "SAVE" -> {
                            isMovingRooms = false;
                            // Check if player entered a filename after "SAVE"
                            if (command.length < 2 || command[1].isEmpty()) {
                                view.outputString("Please enter the player name you'd like to save your game under.");
                                view.outputString("Example: SAVE + yourname");
                                break;
                            }

                            if (GameStateManager.save(command[1], roomsList, artifactList, player)) {
                                view.outputString("Successfully saved game data to: " + command[1]);
                            } else {
                                view.outputString("An error occurred while trying to save the game.");
                            }
                        }
                        case "LOAD" -> {
                            isMovingRooms = false;
                            // Check if player entered a filename after "LOAD"
                            if (command.length < 2 || command[1].isEmpty()) {
                                view.outputString("Please enter the name of the file you'd like to load.");
                                view.outputString("Example: LOAD + yourname");
                                break;
                            }

                            if (GameStateManager.load(command[1], roomsList, artifactList, player)) {
                                view.outputString("Welcome back to the game " + player.getName() + "!");
                                view.outputString("You can continue where you left off.");
                            } else {
                                view.outputString("Failed to load the game. Please make sure the file exists and is valid.");
                            }
                        }
                        default ->
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
