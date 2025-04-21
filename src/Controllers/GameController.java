package Controllers;

import Models.*;

import java.io.IOException;
import java.util.*;

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
        gsm.resetGame();
        gsm.mapGame(player);
        view.printGameTitle();
        view.outputString("Welcome to Pyramid Plunder " + playerName + "! Explore the rooms, solve puzzles, fight monsters, and find items.");
        startGame();
    }

    public void startGame() {
        // enters game at room index 0 (library)
        boolean isEntranceToGame = true;

        int nextRoomIndex = 1;
        player.setRoom(getRoom(nextRoomIndex));


        // Controls whether player has input a direction
        // Resets on each iteration, defaults to true for first iteration of while loop to print the
        // room player starts in when game is initialized.
        boolean isMovingRooms = true;

        while (true) {
            boolean hasMovedRooms = false;
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

            //This will try to get the monster currently in the room

            Monster monster = GameStateManager.getMonsterInRoom(player.getRoom().getRoomId());
            if (monster != null && monster.isDefeated() == false) {

                Scanner sc = new Scanner(System.in);
                view.outputString("You encounter " + monster.getName() + "!. Do you want to fight or flee?");
                System.out.println("Enter fight or flee: ");
                String choice = sc.nextLine();
                if (choice.equalsIgnoreCase("fight")) {
                    int nextRoom = fightMonster(monster);
                    if (nextRoom != -1) {
                        isMovingRooms = true;
                        nextRoomIndex = nextRoom;
                    }
                } else if (choice.equalsIgnoreCase("flee")) {
                    view.outputString("You flee from " + monster.getName() + ".");
                    nextRoomIndex = player.getPriorRoom();
                    player.setRoom(getRoom(nextRoomIndex));
                    isMovingRooms = true;
                } else {
                    System.out.println("Invalid choice: Fight or flee");
                    continue;
                }
            }

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
                            int roomIndex = player.getRoom().getExit(command[0].toUpperCase());
                            Room r = getRoom(roomIndex);
                            if (r != null) {
                                if (!canProceed(player.getRoom(), r)) {
                                    break;
                                }
                                if (nextRoomIndex != 0 && r.canNavigateTo(player)) {
                                    isMovingRooms = true;
                                    nextRoomIndex = roomIndex;
                                    hasMovedRooms = true;
                                    int currentFloor = player.getRoom().getFloorNumber();
                                    boolean hasVisitedAllRoomsOnFloor = true;
                                    for (Room room : roomsList.values()) {
                                        if (room.getFloorNumber() == currentFloor) {
                                            if (!room.getIsVisited()) {
                                                hasVisitedAllRoomsOnFloor = false;
                                            }
                                        }
                                    }
                                    if (hasVisitedAllRoomsOnFloor) {
                                        view.outputString("You have visited all rooms on the floor.");
                                    }
                                } else {
                                    isMovingRooms = false;
                                    if (nextRoomIndex == 0) {
                                        view.outputString("You can't go this way.");
                                    } else {
                                        view.outputString("Insufficient numbers of keys obtained to go here. You must have " + r.getLockConditions() + " keys. You currently have " + player.getKeys().size() + ".");
                                    }
                                }
                            } else {
                                view.outputString("There is no room in this direction. Please choose another.");
                            }
                        }
                        case "ROOM" -> {
                            int roomIndex = Integer.parseInt(command[1]);
                            nextRoomIndex = roomIndex;
                            isMovingRooms = true;
                        }
                        case "BACK" -> {
                            isMovingRooms = true;
                            nextRoomIndex = player.getPriorRoom();
                        }
                        case "LOCATION", "WHEREAMI" -> {
                            isMovingRooms = false;
                            view.outputString("You are currently in room #: " + player.getRoom().getID() + " " + player.getRoom().getName());
                            view.outputString(player.getRoom().getDescription() + "\n");
                        }
                        case "MAP" -> {
                            isMovingRooms = false;
                            view.showMap();
                        }
                        case "STATS" -> {
                            isMovingRooms = false;
                            view.outputString("Health: " + player.getHp());
                            view.outputString("Defense: " + player.getDef());
                            view.outputString("Strength: " + player.getStr());
                        }
                        case "EXPLORE" -> {
                            isMovingRooms = false;
                            view.outputString("Detailed description of your current room: \n" + player.getRoom().getDescription() + "\n");
                        }
                        case "HELP" -> {
                            if (command.length == 1) {
                                view.printHelpList();
                            } else {
                                view.printDetailedHelp(command[1]);
                            }
                        }
                        case "SEARCH" -> {
                            isMovingRooms = false;
                            if (!player.getRoom().getArtifacts().isEmpty()) {
                                view.outputString("You look around the room and notice the following item(s):");
                                player.getRoom().getArtifacts().forEach((Artifact a) -> {
                                    if (!a.getType().equals("key") && !a.getType().equals("object")) {
                                        view.outputString("Item found: " + a.getName());
                                        view.outputString("Type: " + a.getType());
                                        view.outputString("Description: " + a.getDescription());
                                        view.outputString("Effect: " + a.getTextEffect());
                                        view.outputString("Options: PICKUP " + a.getName() + " | IGNORE " + a.getName() + " | SWAP " + a.getName());
                                    } else if (a.getType().equals("object")) {
                                        view.outputString("Item found: " + a.getName());
                                        view.outputString("Description: " + a.getDescription());
                                        view.outputString("Options: PICKUP " + a.getName());
                                    }
                                });
                            } else {
                                view.outputString("There are no items in this room.");
                            }
                            if (player.getRoom().getPuzzle() != null && !player.getRoom().getPuzzle().getIsSolved()) {
                                view.outputString("Puzzle: " + player.getRoom().getPuzzle().getName());
                            }
                        }
                        case "PICKUP" -> {
                            isMovingRooms = false;
                            // Check if player entered a filename after "SAVE"
                            if (command.length < 2 || command[1].isEmpty()) {
                                view.outputString("Please enter the name of the item you'd like to pick up.");
                                break;
                            }

                            StringBuilder itemNameBuilder = new StringBuilder();
                            for (int i = 1; i < command.length; i++) {
                                itemNameBuilder.append(command[i]).append(" ");
                            }
                            String itemName = itemNameBuilder.toString().trim();

                            Artifact foundArtifact = null;
                            for (Artifact a : player.getRoom().getArtifacts()) {
                                if (a.getName().equalsIgnoreCase(itemName)) {
                                    foundArtifact = a;
                                    break;
                                }
                            }

                            if (foundArtifact == null) {
                                view.outputString("This item is not located in the room.");
                            } else {
                                Artifact existing = player.getArtifactByType(foundArtifact.getType());
                                if (!foundArtifact.getType().equals("object")) {
                                    if (existing != null) {
                                        view.outputString("You already have a " + foundArtifact.getType() + " equipped.");
                                        view.outputString("Use the 'SWAP " + foundArtifact.getName() + "' command to replace it.");
                                    } else {
                                        foundArtifact.pickup(player);
                                        view.outputString(foundArtifact.getName() + " has been added to your inventory.");
                                    }
                                } else {
                                    foundArtifact.pickup(player);
                                    view.outputString(foundArtifact.getTextEffect());
                                }
                            }
                        }
                        case "USE" -> {
                            isMovingRooms = false;
                            if (command.length < 2 || command[1].isEmpty()) {
                                view.outputString("Please enter the name of the item you'd like to use.");
                                break;
                            }
                            StringBuilder itemNameBuilder = new StringBuilder();
                            for (int i = 1; i < command.length; i++) {
                                itemNameBuilder.append(command[i]).append(" ");
                            }
                            String itemName = itemNameBuilder.toString().trim();

                            Artifact itemToUse = null;
                            for (Artifact artifact : player.getInventory()) {
                                if (artifact.getName().equalsIgnoreCase(itemName)) {
                                    itemToUse = artifact;
                                    break;
                                }
                            }

                            if (itemToUse == null) {
                                view.outputString("You don't have an item named '" + itemName + "' in your inventory.");
                                break;
                            }
                            if (itemName.equalsIgnoreCase("amulet")) {
                                if (player.getRoom().getPuzzle() == null) {
                                    view.outputString("You have no puzzles in this room to solve.");
                                    break;
                                }

                                if (player.getRoom().getPuzzle().getIsSolved()) {
                                    view.outputString("This puzzle is already solved.");
                                } else {
                                    player.getRoom().getPuzzle().setIsSolved(true);
                                    ArrayList<Artifact> loot = player.getRoom().getLoot();
                                    for (Artifact a : loot) {
                                        view.outputString("You received the following loot: " + a.getName());
                                    }
                                    player.getRoom().playerGetsLoot(player);
                                    player.incrementAmuletUses();
                                    if (player.getAmuletUses() >= 3) {
                                        view.outputString("You have used the Amulet 3 times. Its power has faded.");
                                        player.removeFromInventory(itemToUse);
                                        break;
                                    }
                                    view.outputString("You used the Amulet to solve the puzzle. (" +
                                            player.getAmuletUses() + "/3 uses)");
                                }
                                break;
                            }
                            if (itemToUse instanceof Consumable) {
                                Consumable consumable = (Consumable) itemToUse;
                                if (consumable.isUsable()) {
                                    consumable.applyEffects(player);
                                    consumable.setCooldown();
                                    view.outputString("You used " + consumable.getName() + ".");

                                } else {
                                    view.outputString(consumable.getName() + " is on cooldown. It will be usable in " + consumable.getRoomsUntilUsable() + " room(s).");
                                }
                            } else {
                                view.outputString(itemToUse.getName() + " cannot be used.");
                            }
                        }
                        case "SWAP" -> {
                            isMovingRooms = false;

                            if (command.length < 2 || command[1].isEmpty()) {
                                view.outputString("Please specify the name of the item you'd like to swap.");
                                break;
                            }

                            // Reconstruct item name
                            StringBuilder swapItemNameBuilder = new StringBuilder();
                            for (int i = 1; i < command.length; i++) {
                                swapItemNameBuilder.append(command[i]).append(" ");
                            }
                            String swapItemName = swapItemNameBuilder.toString().trim();

                            // Find item in room
                            Artifact itemInRoom = null;
                            for (Artifact artifact : player.getRoom().getArtifacts()) {
                                if (artifact.getName().equalsIgnoreCase(swapItemName)) {
                                    itemInRoom = artifact;
                                    break;
                                }
                            }

                            if (itemInRoom == null) {
                                view.outputString("There is no item named '" + swapItemName + "' in this room.");
                                break;
                            }

                            // Find matching type in inventory
                            Artifact playerItem = player.getArtifactByType(itemInRoom.getType());
                            if (playerItem == null) {
                                view.outputString("You don't have an item of type '" + itemInRoom.getType() + "' to swap.");
                                view.outputString("Use 'PICKUP " + itemInRoom.getName() + "' instead.");
                                break;
                            }

                            // Perform the swap
                            player.getRoom().getArtifacts().add(playerItem);
                            player.removeFromInventory(playerItem);
                            playerItem.removeEffects(player);

                            // Remove current item
                            player.addToInventory(itemInRoom);
                            itemInRoom.applyEffects(player);
                            player.getRoom().getArtifacts().remove(itemInRoom);            // Remove room item

                            view.outputString("You swapped your " + playerItem.getName() +
                                    " for " + itemInRoom.getName() + ".");
                        }
                        case "IGNORE" -> {
                            isMovingRooms = false;
                            if (command.length < 2 || command[1].isEmpty()) {
                                view.outputString("Please specify the name of the item you'd like to ignore.");
                                break;
                            }

                            StringBuilder ignoreItemBuilder = new StringBuilder();
                            for (int i = 1; i < command.length; i++) {
                                ignoreItemBuilder.append(command[i]).append(" ");
                            }
                            String ignoredItemName = ignoreItemBuilder.toString().trim();

                            Artifact itemToIgnore = null;
                            for (Artifact artifact : player.getRoom().getArtifacts()) {
                                if (artifact.getName().equalsIgnoreCase(ignoredItemName)) {
                                    itemToIgnore = artifact;
                                    break;
                                }
                            }

                            if (itemToIgnore == null) {
                                view.outputString("There is no item named '" + ignoredItemName + "' in this room.");
                            } else {
                                view.outputString("You ignored the " + itemToIgnore.getName() + ".");
                            }
                        }
                        case "INVENTORY" -> {
                            isMovingRooms = false;
                            player.getInventory().forEach((Artifact a) -> {
                                view.outputString(a.getName() + " - " + a.getTextEffect());
                            });
                            if (!player.getKeys().isEmpty()) {
                                view.outputString("Keys:");
                                player.getKeys().forEach((Artifact a) -> {
                                    view.outputString(a.getName());
                                });
                            }
                        }
                        case "LISTEN" -> {
                            isMovingRooms = false;
                            Monster m = player.getRoom().getMonster();
                            if (m != null) {
                                view.outputString("You detect movement from " + m.getName());
                            } else {
                                view.outputString("You don't detect anything out of the ordinary.");
                            }
                        }
                        case "INTERACT" -> {
                            isMovingRooms = false;
                            String target = String.join(" ", Arrays.copyOfRange(command, 1, command.length)).trim();
                            while (target.isEmpty()) {
                                view.outputString("You haven't specified a target.");
                                command = view.getRoomInput();
                            }
                            for (Artifact a : player.getRoom().getArtifacts()) {
                                if (target.equalsIgnoreCase(a.getName())) {
                                    view.outputString(a.getName() + " - " + a.getTextEffect());
                                }
                            }
                            if (player.getRoom().getMonster() != null && target.equalsIgnoreCase(player.getRoom().getMonster().getName())) {
                                view.outputString("A" + player.getRoom().getMonster().getName() + " is in this room.");
                            }
                            if (player.getRoom().getPuzzle() != null && target.equalsIgnoreCase(player.getRoom().getPuzzle().getName())) {
                                view.outputString("This is a puzzle!");
                                view.outputString("Would you like to solve or ignore?");
                                command = view.getRoomInput();
                                if (command[0].equalsIgnoreCase("ignore")) {
                                    view.outputString("WARNING: Ignoring a puzzle can deny progress and stop rewards! Are you sure? (Y/N)");
                                    String answer = view.getAnswer();
                                    if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                                        int roomNum = player.getPriorRoom();
                                        player.setRoom(roomsList.get(roomNum));
                                        view.outputString("You've ignored the puzzle and backtracked.");
                                    }
                                } else if (command[0].equalsIgnoreCase("solve") && !player.getRoom().getPuzzle().getIsSolved()) {
                                    Puzzle puzzle = player.getRoom().getPuzzle();
                                    view.outputString(puzzle.getName());
                                    view.outputString(puzzle.getDescriptions().get(0));
                                    String answer;
                                    switch (puzzle) {
                                        case Puzzle.StandardPuzzle standardPuzzle -> {
                                            int i = 0;
                                            int attempts = 0;
                                            String[] paths = {"1", "2", "3"};
                                            Random random = new Random();
                                            String correctPath = paths[random.nextInt(paths.length)];
                                            if (standardPuzzle.getName().equalsIgnoreCase("Pit Crossing")) {
                                                view.outputString(Arrays.toString(paths));
                                            }
                                            do {
                                                answer = view.getAnswer();
                                                if (answer.equalsIgnoreCase("hint")) {
                                                    if (i < attempts && i < puzzle.getHints().size()) {
                                                        view.outputString("Hint " + (i + 1) + ": " + puzzle.getHints().get(i));
                                                        i++;
                                                    } else if (i >= puzzle.getHints().size()) {
                                                        view.outputString("All hints have been given.");
                                                    } else if (attempts == 0) {
                                                        view.outputString("Try first before hints.");
                                                    } else {
                                                        view.outputString("Try again first before another hint.");
                                                    }
                                                }
                                                if (standardPuzzle.solve(answer) || (answer.equalsIgnoreCase(correctPath) && standardPuzzle.getName().equalsIgnoreCase("Pit Crossing"))) {
                                                    standardPuzzle.setIsSolved(true);
                                                    view.outputString("Correct! You solved the puzzle.");
                                                    List<Artifact> loot = player.getRoom().getLoot();
                                                    if (loot.size() > 0) {
                                                        player.getRoom().playerGetsLoot(player);
                                                        if (standardPuzzle.getName().equalsIgnoreCase("Pit Crossing")) {
                                                            view.outputString("A " + loot.get(loot.size() - 1).getName() + " was found in the room!");
                                                        } else {
                                                            view.outputString("Reward: " + loot.get(loot.size() - 1).getName() + "!");
                                                        }
                                                    }
                                                } else if (!answer.equalsIgnoreCase("hint")) {
                                                    player.setHp(player.getHp() - 5);
                                                    attempts++;
                                                    view.outputString("Wrong! You took 5 damage!");
                                                }
                                                if (player.getHp() <= 0) {
                                                    view.outputString("GAME OVER!");
                                                    view.outputString("Would you like to restart? (Y/N)");
                                                    answer = view.getAnswer();
                                                    if (answer.equalsIgnoreCase("y") || answer.equalsIgnoreCase("yes")) {
                                                        gsm.resetGame();
                                                        gsm.mapGame(player);
                                                        view.printGameTitle();
                                                        view.outputString("Welcome to Pyramid Plunder " + player.getName() + "! Explore the rooms, solve puzzles, fight monsters, and find items.");
                                                        startGame();
                                                    } else if (answer.equalsIgnoreCase("n") || answer.equalsIgnoreCase("no")) {
                                                        System.exit(0);
                                                    } else {
                                                        view.outputString("Invalid Input.");
                                                    }
                                                }
                                            } while (!standardPuzzle.getIsSolved());
                                        }
                                        case Puzzle.BooleanPuzzle boolPuzzle -> {
                                            String condition = boolPuzzle.getCondition();
                                            boolean torchOn = false;
                                            boolean isStelthed = false;
                                            if (condition.equalsIgnoreCase("light")) {
                                                if (boolPuzzle.solve(roomsList.get(6).getPuzzle().getIsSolved())) {
                                                    boolPuzzle.setIsSolved(true);
                                                    view.outputString("The light made the scarabs scatter away!");
                                                    List<Artifact> loot = player.getRoom().getLoot();
                                                    player.getRoom().playerGetsLoot(player);
                                                    view.outputString("Reward: " + loot.get(loot.size() - 1).getName() + "!");
                                                } else {
                                                    view.outputString("You have not met the conditions.");
                                                    player.setHp(player.getHp() - 5);
                                                    view.outputString("You took 5 damage!");
                                                }
                                            } else if (condition.equalsIgnoreCase("torch")) {
                                                if (boolPuzzle.solve(torchOn)) {
                                                    view.outputString("The torch lights up the room. It's safe to proceed.");
                                                    List<Artifact> loot = player.getRoom().getLoot();
                                                    player.getRoom().playerGetsLoot(player);
                                                    view.outputString("A " + loot.get(loot.size() - 1).getName() + " was found in the room!");
                                                } else {
                                                    player.setHp(player.getHp() - 5);
                                                    view.outputString("It was too dark to proceed. You took 5 damage!");
                                                }
                                            } else if (condition.equalsIgnoreCase("stealth")) {
                                                if (boolPuzzle.solve(isStelthed)) {
                                                    view.outputString("You were able to sneak up and disable the statue.");
                                                    List<Artifact> loot = player.getRoom().getLoot();
                                                    player.getRoom().playerGetsLoot(player);
                                                    view.outputString("A " + loot.get(loot.size() - 1).getName() + " was found in the room!");
                                                } else {
                                                    player.setHp(player.getHp() - 5);
                                                    view.outputString("The statue zapped you! You took 5 damage! ");
                                                }
                                            }
                                        }
                                        case Puzzle.SequencePuzzle seqPuzzle -> {
                                            List<List<String>> colorTiles = seqPuzzle.generateColorTiles();
                                            int i = 0;
                                            while (!seqPuzzle.isComplete() && player.getHp() > 0) {
                                                if (seqPuzzle.getName().equalsIgnoreCase("Arrow Trap")) {
                                                    view.outputString(String.valueOf(colorTiles.get(i)));
                                                }
                                                view.outputString(seqPuzzle.getCurrentDescription());
                                                answer = view.getAnswer();
                                                int count = 0;
                                                if (colorTiles.get(i).contains(answer)) {

                                                    for (String color : colorTiles.get(i)) {
                                                        if (color.equalsIgnoreCase(answer)) {
                                                            count++;
                                                        }
                                                    }
                                                }
                                                if (seqPuzzle.solve(answer) || count >= 2) {
                                                    view.outputString("Correct! Solved Riddle " + seqPuzzle.getIndex());
                                                    i++;
                                                } else {
                                                    view.outputString("Wrong!");
                                                    player.setHp(player.getHp() - 5);
                                                    view.outputString("You took 5 damage!");
                                                    view.outputString("Try again.");
                                                }
                                                if (seqPuzzle.isComplete()) {
                                                    seqPuzzle.setIsSolved(true);
                                                    view.outputString("You solved all riddles!");
                                                    List<Artifact> loot = player.getRoom().getLoot();
                                                    player.getRoom().playerGetsLoot(player);
                                                    view.outputString("Reward: " + loot.get(loot.size() - 1).getName() + "!");
                                                    break;
                                                }
                                            }
                                        }
                                        case Puzzle.MultiPuzzle multiPuzzle -> {
                                            if (target.equalsIgnoreCase("Balancing Scale")) {
                                                multiPuzzle.generateBalancePuzzle();
                                                view.outputString(String.valueOf(multiPuzzle.getRightAnswers()));
                                                int i = 0;
                                                int attempts = 0;
                                                do {
                                                    answer = view.getAnswer();
                                                    if (answer.equalsIgnoreCase("hint")) {
                                                        if (i < attempts && i < puzzle.getHints().size()) {
                                                            view.outputString("Hint " + (i + 1) + ": " + puzzle.getHints().get(i));
                                                            i++;
                                                        } else if (i >= puzzle.getHints().size()) {
                                                            view.outputString("All hints have been given.");
                                                        } else if (attempts == 0) {
                                                            view.outputString("Try first before hints.");
                                                        } else {
                                                            view.outputString("Try again first before another hint.");
                                                        }
                                                    }
                                                    List<String> sections = List.of(answer.split(" "));
                                                    int sumOfWeight = multiPuzzle.sumWeights(sections);
                                                    if (multiPuzzle.solve(sumOfWeight)) {
                                                        view.outputString("Correct!");
                                                        multiPuzzle.setIsSolved(true);
                                                        view.outputString("You solved the puzzle!");
                                                        List<Artifact> loot = player.getRoom().getLoot();
                                                        player.getRoom().playerGetsLoot(player);
                                                        view.outputString("Reward: " + loot.get(loot.size() - 1).getName() + "!");
                                                    } else if (!answer.equalsIgnoreCase("hint")) {
                                                        if (sumOfWeight < 100) {
                                                            view.outputString("Not enough weight.");
                                                        }
                                                        if (sumOfWeight > 100) {
                                                            view.outputString("Too much weight.");
                                                        }
                                                        view.outputString("Wrong! You took 5 damage!");
                                                        player.setHp(player.getHp() - 5);
                                                        attempts++;
                                                    }
                                                } while (player.getHp() > 0 && !puzzle.getIsSolved());
                                            }
                                            if (target.equalsIgnoreCase("Light the Torches")) {
                                                List<String> torches = new ArrayList<>(List.of("1", "2", "3"));
                                                view.outputString(String.valueOf(torches));
                                                Collections.shuffle(torches);
                                                int i = 0;
                                                int attempts = 0;
                                                do {
                                                    answer = view.getAnswer();
                                                    if (answer.equalsIgnoreCase("hint")) {
                                                        if (i < attempts && i < puzzle.getHints().size()) {
                                                            view.outputString("Hint " + (i + 1) + ": " + puzzle.getHints().get(i));
                                                            i++;
                                                        } else if (i >= puzzle.getHints().size()) {
                                                            view.outputString("All hints have been given.");
                                                        } else if (attempts == 0) {
                                                            view.outputString("Try first before hints.");
                                                        } else {
                                                            view.outputString("Try again first before another hint.");
                                                        }
                                                    }
                                                    List<String> sections = List.of(answer.split(" "));
                                                    if (multiPuzzle.solve(torches, sections)) {
                                                        view.outputString("Correct!");
                                                        multiPuzzle.setIsSolved(true);
                                                        view.outputString("You solved the puzzle!");
                                                        player.getRoom().playerGetsLoot(player);
                                                    } else if (!answer.equalsIgnoreCase("hint")) {
                                                        view.outputString("Wrong! You took 5 damage!");
                                                        player.setHp(player.getHp() - 5);
                                                        attempts++;
                                                    }
                                                } while (player.getHp() > 0 && !puzzle.getIsSolved());
                                            }
                                        }
                                        default -> {
                                        }
                                    }
                                }
                            } else {
                                view.outputString("Invalid Puzzle.");
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

                            if (GameStateManager.save(command[1], gsm.getRoomsList(), player)) {
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

                            if (GameStateManager.load(command[1], gsm.getRoomsList(), gsm.getArtifactsList(), gsm.getMonstersList(), gsm.getPuzzlesList(), player, gsm)) {
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
            // Decrement cooldowns for consumables
            if (hasMovedRooms) {
                for (Artifact artifact : player.getInventory()) {
                    if (artifact instanceof Consumable) {
                        ((Consumable) artifact).decrementCooldown();
                    }
                }

            }
        }
    }

    public Room getRoom(int roomIndex) {
        return roomsList.get(roomIndex);
    }

    private int fightMonster(Monster monster) {
        Scanner sc = new Scanner(System.in);

        boolean isFlee = false;
        // Implement the logic for fighting the monster

        view.outputString("You engage in a fight with " + monster.getName() + "!");
        while (true) {
            while (monster.getHealth() > 0 && player.getHp() > 0) {
                System.out.println("Enter fight, block, use item, or flee:  ");
                String choice = sc.nextLine();
                if (choice.equalsIgnoreCase("fight")) {
                    monster.takeDamage(player.getStr());
                } else if (choice.equalsIgnoreCase("block")) {
                    monster.setPlayerBlocking(true);
                } else if (choice.contains("use")) {
                    //implement item usage
                    System.out.println("Enter name of item: ");
                    String itemName = sc.nextLine();
                    if (player.getInventoryArtifactByName(itemName) != null) {
                        //implement use item logic ONLY if item is consumable
                        player.getArtifactByType("Consumable");
                    } else {
                        System.out.println("Item is not in inventory.");
                    }
                } else if (choice.equalsIgnoreCase("flee")) {

                    //implement flee function

                    player.setRoom(getRoom(player.getPriorRoom()));
                    isFlee = true;
                    break;

                } else {
                    System.out.println("Invalid response. Enter fight, block, use item, or flee.");
                    continue;
                }
                // Assuming the monster has at least one attack
                monster.executeAttack(player);
                view.outputString("Monster health: " + monster.getHealth() + ", Player health: " + player.getHp());
                monster.setPlayerBlocking(false);
            }
            if (isFlee) {
                break;
            }
            if (monster.getHealth() <= 0) {
                view.outputString("You defeated the " + monster.getName() + "!");
                monster.setDefeated(true);
                for (Artifact a : player.getRoom().getLoot()) {
                    view.outputString("You received the following loot: " + a.getName());
                    if (a.getType().equals("key")) {
                        player.addToInventory(a);
                    } else {
                        player.getRoom().addArtifact(a);
                    }
                }
                break;
            } else {
                player.setHp(0);
                view.outputString("You were defeated by the " + monster.getName() + ".");
                Artifact a = player.getInventoryArtifactByName("crook of osiris");
                if (a != null) {
                    view.outputString("A bright light shines down from the heavens and you revive with 1/2 health.");
                    player.setHp(player.getBaseHealth() / 2);
                    player.removeFromInventory(a);
                } else {
                    view.outputString("Game over");
                    System.exit(0);
                    break;
                }
            }
        }
        return -1;
    }

    public boolean canProceed(Room currentRoom, Room nextRoom) {
        Puzzle p = currentRoom.getPuzzle();
        if (p != null && !p.getIsSolved() && nextRoom.getRoomId() != player.getPriorRoom()) {
            int roomID = currentRoom.getRoomId();
            if (roomID == 18) {
                view.outputString("You have not been proven worthy. Approach the statue.");
                return false;
            }
            if (roomID == 5 || roomID == 24) {
                view.outputString("There's a deep pit in the way. You need to interact with it carefully.");
                return false;
            }
            if (roomID == 12) {
                player.setHp(player.getHp() - 5);
                view.outputString("The floor is booby trapped! You took 5 damage!");
                return false;
            }
        }
        return true;
    }
}
