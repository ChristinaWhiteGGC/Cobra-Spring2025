package Models;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

enum DataFileSections {
    None,
    Rooms,
    Player,
    PlayerInventory,
    Puzzles,
    Monsters,
    Artifacts
}

public class GameStateManager {

    private final Map<Integer, Room> roomsList = new HashMap<>();
    private final Map<String, Artifact> artifactsList = new HashMap<>();
    private final Map<String, Monster> monstersList = new HashMap<>();
    private final Map<String, Puzzle> puzzlesList = new HashMap<>();

    public Map<Integer, Room> getRoomsList() {
        return roomsList;
    }

    public Map<String, Artifact> getArtifactsList() {
        return artifactsList;
    }

    public Map<String, Monster> getMonstersList() {
        return monstersList;
    }

    public Map<String, Puzzle> getPuzzlesList() {
        return puzzlesList;
    }

    public void resetGame() {
        try {
            // First clear everything, then read the data files and parse
            roomsList.clear();
            artifactsList.clear();
            monstersList.clear();
            puzzlesList.clear();

            Map<Integer, Room> readRooms = Room.loadRooms(readFile("src", "Data", "Rooms.txt"));
            for (Room r : readRooms.values()) {
                roomsList.put(r.getRoomId(), r);
            }

            Map<String, Artifact> readArtifacts = Artifact.loadArtifacts(Paths.get("src", "Data", "Artifacts.txt").toAbsolutePath().toString());
            for (Artifact a : readArtifacts.values()) {
                artifactsList.put(a.getId(), a);
            }

            Map<String, Monster> readMonsters = Monster.loadMonsters(readFile("src", "Data", "Monsters.txt"));
            for (Monster m : readMonsters.values()) {
                monstersList.put(m.getName(), m);
            }

            Map<String, Puzzle> readPuzzles = Puzzle.loadPuzzles("src/Data/Puzzles.txt");
            for (Puzzle p : readPuzzles.values()) {
                puzzlesList.put(p.getName(), p);
            }
        } catch (IOException ioe) {
            System.out.println("Error reading in data files: " + ioe.getMessage());
            System.exit(1);
        }
    }

    public void mapGame(Player player) {
        // TODO: Remap monsters, and puzzles to rooms from data files.
        mapArtifactsToRooms(roomsList, artifactsList);
        mapMonstersToRooms(roomsList, monstersList);
        mapPuzzlesToRooms(roomsList, puzzlesList);

        // TODO: Verify what starting health is and set it here
        player.setHp(100);
        player.clearInventory();
        player.setRoom(roomsList.get(1));
    }

    private static void mapPuzzlesToRooms(Map<Integer, Room> roomsList, Map<String, Puzzle> puzzlesList) {
        puzzlesList.forEach((String key, Puzzle p) -> {
            for (int roomID : p.getRoomNumbers()) {
                roomsList.get(roomID).setPuzzle(p);
            }
        });
    }

    private static void mapMonstersToRooms(Map<Integer, Room> roomsList, Map<String, Monster> monstersList) {
        Map<String, Monster> staticMonsters = new HashMap<>();
        Map<String, Monster> randomMonsters = new HashMap<>();

        // Separate static and random monsters
        monstersList.forEach((String key, Monster m) -> {
            boolean isRandom = false;
            for (String location : m.getLocations()) {
                if (location.equals("-1")) {
                    isRandom = true;
                    break;
                }
            }
            if (isRandom) {
                randomMonsters.put(key, m);
            } else {
                staticMonsters.put(key, m);
            }
        });

        // Place static monsters in their designated rooms
        staticMonsters.forEach((String key, Monster m) -> {
            for (String location : m.getLocations()) {
                Room room = roomsList.get(Integer.parseInt(location));
                if (room != null) {
                    room.setMonster(m);
                }
            }
        });

        // Place random monsters only in rooms that don't have static monsters
        if (!randomMonsters.isEmpty()) {
            Random random = new Random();
            for (Room r : roomsList.values()) {
                if (r.getHasMonster() && r.getMonster() == null) {
                    Monster[] monsters = randomMonsters.values().toArray(new Monster[0]);
                    r.setMonster(monsters[random.nextInt(monsters.length)]);
                }
            }
        }
    }

    private static void mapArtifactsToRooms(Map<Integer, Room> roomsList, Map<String, Artifact> artifactList) {
        for (Room r : roomsList.values()) {
            String[] artifactIds = r.getInitialArtifactIds();
            String[] lootIds = r.getInitialLootIds();
            Artifact artifact;
            for (String id : artifactIds) {
                if (id.equals("0")) {
                    Random random = new Random();
                    while (true) {
                        int index = random.nextInt(artifactList.size());
                        artifact = (Artifact) artifactList.values().toArray()[index];
                        if (!artifact.getType().equals("key") && !artifact.getType().equals("object")) {
                            break;
                        }
                    }
                } else if (id.equals("W0")) {
                    Random random = new Random();
                    while (true) {
                        int index = random.nextInt(artifactList.size());
                        artifact = (Artifact) artifactList.values().toArray()[index];
                        if (artifact.getType().equals("weapon")) {
                            break;
                        }
                    }
                } else {
                    artifact = artifactList.get(id);
                }

                if (artifact != null) {
                    r.addArtifact(artifact);
                }
            }
            for (String id : lootIds) {
                if (id.equals("0")) {
                    Random random = new Random();
                    while (true) {
                        int index = random.nextInt(artifactList.size());
                        artifact = (Artifact) artifactList.values().toArray()[index];
                        if (!artifact.getType().equals("key") && !artifact.getType().equals("object")) {
                            break;
                        }
                    }
                } else {
                    artifact = artifactList.get(id);
                }
                if (artifact != null) {
                    r.addLoot(artifact);
                }
            }
        }
    }

    public static ArrayList<String> readFile(String... filePath) throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        // this gets the relative path for the data file in the local directory
        Path path = Paths.get(filePath[0], java.util.Arrays.copyOfRange(filePath, 1, filePath.length));

        // open file
        File dataFile = new File(path.toAbsolutePath().toString());
        Scanner fileReader = new Scanner(dataFile);
        // reads text file
        while (fileReader.hasNextLine()) {
            // reads each line from text file into a string
            lines.add(fileReader.nextLine());
        }
        fileReader.close();
        return lines;
    }

    public static boolean save(String saveName, Map<Integer, Room> rooms, Player player) {
        try {
            PrintWriter writer = new PrintWriter(Paths.get("src", "Data", "Saves", saveName).toAbsolutePath().toString(), StandardCharsets.UTF_8);


            // Write out player state
            writer.println("Player:");
            writer.println("\t" + player.getName() + "~" + player.getHp() + "~" + player.getStr() + "~" + player.getDef());

            // Write out player inventory
            writer.println("Player Inventory:");
            for (Artifact a : player.getInventory()) {
                writer.println("\t" + a.getId() + "~" + a.getType());
            }

            // Write out room state
            writer.println("Rooms:");
            for (Room r : rooms.values()) {
                StringBuilder roomLine = new StringBuilder();
                roomLine.append("\t" + r.getRoomId());
                roomLine.append("~" + (r.getIsVisited() ? "true" : "false"));
                StringBuilder itemLines = new StringBuilder();
                for (Artifact a : r.getArtifacts()) {
                    if (a != null) {
                        if (!itemLines.isEmpty()) {
                            itemLines.append(",");
                        }
                        itemLines.append(a.getId());
                    }
                }
                roomLine.append("~" + itemLines);
                itemLines = new StringBuilder();
                for (Artifact a : r.getLoot()) {
                    if (a != null) {
                        if (!itemLines.isEmpty()) {
                            itemLines.append(",");
                        }
                        itemLines.append(a.getId());
                    }
                }
                roomLine.append("~" + itemLines);
                if (r.getMonster() != null && !r.getMonster().isDefeated()) {
                    roomLine.append("~" + r.getMonster().getName());

                } else {
                    roomLine.append("~");
                }
                if (r.getPuzzle() != null && !r.getPuzzle().isSolved) {
                    roomLine.append("~" + r.getPuzzle().getName());
                } else {
                    roomLine.append("~");
                }
                writer.println("\t" + roomLine);
            }

            writer.close();
        } catch (IOException e) {
            System.out.println("Unable to save game: " + e.getMessage());
            return false;
        }
        return true;
    }

    public static boolean load(String loadName, Map<Integer, Room> roomsList, Map<String, Artifact> artifactsList, Map<String, Monster> monstersList, Map<String, Puzzle> puzzlesList, Player p, GameStateManager gsm) {
        gsm.resetGame();
        try {
            Exception invalidSaveGameData = new Exception("Invalid saved game data. Data file is corrupt.");
            ArrayList<String> savedLines = readFile("src", "Data", "Saves", loadName);
            DataFileSections currentSection = DataFileSections.None;
            for (String line : savedLines) {
                switch (line) {
                    case "Player:" -> currentSection = DataFileSections.Player;
                    case "Player Inventory:" -> currentSection = DataFileSections.PlayerInventory;
                    case "Rooms:" -> currentSection = DataFileSections.Rooms;
                    default -> {
                        line = line.trim();
                        if (line.isEmpty()) {
                            continue;
                        }
                        if (currentSection == DataFileSections.None) {
                            throw invalidSaveGameData;
                        }
                        if (currentSection == DataFileSections.Player) {
                            // Name~HP~STR~DEF
                            String[] sections = line.split("~", -1);
                            if (sections.length < 4) {
                                System.out.println("Invalid save game data. Corrupt.");
                            }
                            p.setName(sections[0]);
                            p.setHp(Integer.parseInt(sections[1]));
                            p.setStr(Integer.parseInt(sections[2]));
                            p.setDef(Integer.parseInt(sections[3]));
                        } else if (currentSection == DataFileSections.PlayerInventory) {
                            // Artifact Name
                            Artifact a = artifactsList.get(line);
                            if (a != null) {
                                p.addToInventory(a);
                            } else {
                                throw invalidSaveGameData;
                            }
                        } else {
                            // Load room data
                            String[] sections = line.split("~", -1);
                            if (sections.length < 3) {
                                throw invalidSaveGameData;
                            }

                            int roomId = Integer.parseInt(sections[0]);
                            Room r = roomsList.get(roomId);

                            boolean isVisited = Boolean.parseBoolean(sections[1]);

                            if (isVisited) {
                                r.setVisited();
                            }

                            String[] savedArtifactList = sections[2].split("\\|", -1);

                            for (String artifact : savedArtifactList) {
                                if (!artifact.isEmpty()) {
                                    r.addArtifact(artifactsList.get(artifact));
                                }
                            }
                            String[] savedLootList = sections[3].split("\\|", -1);

                            for (String artifact : savedLootList) {
                                if (!artifact.isEmpty()) {
                                    r.addLoot(artifactsList.get(artifact));
                                }
                            }
                            String monsterName = sections[4];
                            if (!monsterName.isEmpty()) {
                                Monster m = monstersList.get(monsterName);
                                r.setMonster(m);
                            }
                            String puzzleName = sections[5];
                            if (!puzzleName.isEmpty()) {
                                Puzzle puzzle = puzzlesList.get(puzzleName);
                                r.setPuzzle(puzzle);
                            }
                        }
                    }
                }
            }
        } catch (IOException ie) {
            System.out.println("Unable to read save file: " + loadName);
            return false;
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
        return true;
    }

    public Map<Integer, Room> getRooms() {
        return roomsList;
    }

    public Map<String, Artifact> getArtifacts() {
        return artifactsList;
    }

    public static Monster getMonsterInRoom(int roomIndex) {
        try {
            Map<String, Monster> monstersList = Monster.loadMonsters(GameStateManager.readFile("src", "data", "Monsters.txt"));
            // this could involve checking the monster's locations
            for (Monster monster : monstersList.values()) {
                for (String location : monster.getLocations()) {
                    if (Integer.parseInt(location) == roomIndex) {
                        return monster;
                    }
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }
}