package Models;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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

    public void resetGame(Player player) {
        try {
            // First clear everything, then read the data files and parse
            roomsList.clear();
            artifactsList.clear();

            Map<Integer, Room> readRooms = Room.loadRooms(readFile("src", "Data", "Rooms.txt"));
            for (Room r : readRooms.values()) {
                roomsList.put(r.getRoomId(), r);
            }

            Map<String, Artifact> readArtifacts = Artifact.loadArtifacts(Paths.get("src", "Data", "Artifacts.txt").toAbsolutePath().toString());
            for (Artifact a : readArtifacts.values()) {
                artifactsList.put(a.getId(), a);
            }

            // TODO: Remap monsters, and puzzles to rooms from data files.
            mapArtifactsToRooms(roomsList, artifactsList);
            mapMonstersToRooms(roomsList);
            mapPuzzlesToRooms(roomsList);

            // TODO: Verify what starting health is and set it here
            player.setHp(100);
            player.clearInventory();
            player.setRoom(roomsList.get(1));
        } catch (IOException ioe) {
            System.out.println("Error reading in data files: " + ioe.getMessage());
            System.exit(1);
        }
    }


    private static void mapPuzzlesToRooms(Map<Integer,Room> roomsList/* TODO: Add Puzzles list here */) {
        // TODO: Map puzzles to rooms when puzzles are complete
    }

    private static void mapMonstersToRooms(Map<Integer,Room> roomsList/* TODO: Add Monsters list here */) {
        // TODO: Map monsters to rooms
    }

    private static void mapArtifactsToRooms(Map<Integer,Room> roomsList, Map<String, Artifact> artifactList) {
        for (Room r : roomsList.values()) {
            String[] artifactIds = r.getInitialArtifactIds();
            for (String id : artifactIds) {
                Artifact artifact = artifactList.get(id);
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

    public boolean save(String saveName, Map<Integer,Room> rooms, Player p) {
        return false;
    }

    public boolean load(String loadName, Map<Integer,Room> roomsList, Player p, Map<String, Artifact> artifactsList) {
        try {
            Exception invalidSaveGameData = new Exception("Invalid saved game data. Data file is corrupt.");
            ArrayList<String> savedLines = readFile("Data", "Saves", loadName);
            DataFileSections currentSection = DataFileSections.None;
            for (String line : savedLines) {
                switch (line) {
                    case "Player":
                        currentSection = DataFileSections.Player;
                        break;
                    case "PlayerInventory":
                        currentSection = DataFileSections.PlayerInventory;
                        break;
                    case "Rooms:":
                        currentSection = DataFileSections.Rooms;
                        break;
                    case "Puzzles":
                        currentSection = DataFileSections.Puzzles;
                        break;
                    case "Monsters":
                        currentSection = DataFileSections.Monsters;
                        break;
                    case "Artifacts":
                        currentSection = DataFileSections.Artifacts;
                        break;
                    default:
                        if (line.trim().isEmpty()) {
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
                        }
                        if (currentSection == DataFileSections.PlayerInventory) {
                            // Artifact Name
                            Artifact a = artifactsList.get(line);
                            if (a != null) {
                                p.addToInventory(a);
                            } else {
                                throw invalidSaveGameData;
                            }
                        }
                        if (currentSection == DataFileSections.Rooms) {
                            // Load room data
                            String[] sections = line.split("~");
                            if (sections.length != 3) {
                                throw invalidSaveGameData;
                            }
                            int roomId = Integer.parseInt(sections[0]);
                            Room r = roomsList.get(roomId);
                            boolean isVisited = Boolean.parseBoolean(sections[1]);
                            if (isVisited) {
                                r.setVisited();
                            }
                            String[] savedArtifactList = sections[2].split("|");

                            for (String artifact : savedArtifactList) {
                                r.addLoot(artifactsList.get(artifact));
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
}
