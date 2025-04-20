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

    public void resetGame(Player player) {
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

            // TODO: Remap monsters, and puzzles to rooms from data files.
            mapArtifactsToRooms(roomsList, artifactsList);
            mapMonstersToRooms(roomsList, monstersList);
            mapPuzzlesToRooms(roomsList, puzzlesList);

            // TODO: Verify what starting health is and set it here
            player.setHp(100);
            player.clearInventory();
            player.setRoom(roomsList.get(1));
        } catch (IOException ioe) {
            System.out.println("Error reading in data files: " + ioe.getMessage());
            System.exit(1);
        }
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

        monstersList.forEach((String key, Monster m) -> {
            for (String location : m.getLocations()) {
                if (location.equals("-1")) {
                    randomMonsters.put(key, m);
                } else {
                    staticMonsters.put(key, m);
                }
            }
        });

        // Do static monsters first, so we know which rooms can take random monsters
        staticMonsters.forEach((String key, Monster m) -> {
            for (String location : m.getLocations()) {
                roomsList.get(Integer.parseInt(location)).setMonster(m);
            }
        });

        Random random = new Random();
        for (Room r : roomsList.values()) {
            if (r.getHasMonster() && r.getMonster() == null) {
                int randomNumber = random.nextInt(randomMonsters.values().size() + 1);
                Monster[] monsters = randomMonsters.values().toArray(new Monster[0]);
                if (randomNumber == randomMonsters.size()) {
                    r.setMonster(monsters[randomNumber - 1]);
                } else {
                    r.setMonster(monsters[randomNumber]);
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
                }else {
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

    public static boolean save(String saveName, Map<Integer, Room> rooms, Map<String, Artifact> artifactsList, Player player) {
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
                StringBuilder roomLine = new StringBuilder(r.getRoomId() + "~" + (r.getIsVisited() ? "true" : "false" + "~"));
                for (Artifact a : r.getArtifacts()) {
                    roomLine.append(a.getId());
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

    public static boolean load(String loadName, Map<Integer, Room> roomsList, Map<String, Artifact> artifactsList, Player p) {
        try {
            Exception invalidSaveGameData = new Exception("Invalid saved game data. Data file is corrupt.");
            ArrayList<String> savedLines = readFile("src", "Data", "Saves", loadName);
            DataFileSections currentSection = DataFileSections.None;
            for (String line : savedLines) {
                switch (line) {
                    case "Player:" -> currentSection = DataFileSections.Player;
                    case "Player Inventory:" -> currentSection = DataFileSections.PlayerInventory;
                    case "Rooms:" -> currentSection = DataFileSections.Rooms;
                    case "Puzzles:" -> currentSection = DataFileSections.Puzzles;
                    case "Monsters:" -> currentSection = DataFileSections.Monsters;
                    case "Artifacts:" -> currentSection = DataFileSections.Artifacts;
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
                            String[] sections = line.split("~");
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
                        } else if (currentSection == DataFileSections.Rooms) {
                            // Load room data
                            String[] sections = line.split("~");
                            if (sections.length < 2) {
                                throw invalidSaveGameData;
                            }
                            int roomId = Integer.parseInt(sections[0]);
                            Room r = roomsList.get(roomId);
                            boolean isVisited = Boolean.parseBoolean(sections[1]);
                            if (isVisited) {
                                r.setVisited();
                            }
                            if (sections.length == 3) {
                                // We have artifacts in the room
                                String[] savedArtifactList = sections[2].split("\\|");

                                for (String artifact : savedArtifactList) {
                                    r.addLoot(artifactsList.get(artifact));
                                }
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
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return null;
    }
}
