package Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/*
The room class stores information and attributes about each individual room
with the following fields:
Models.Room ID
Models.Room Name
Models.Room Description
Boolean isVisited to keep track of previously visited rooms
Ints n,e,s, and w for directional navigation

 */
public class Room {
    // The ID of the room
    private final int roomId;

    // Flags whether we have visited this room before, or not
    private boolean isVisited;

    // The name of the room
    private final String name;

    // The description of the room
    private final String description;

    // Models.Room ID of room located to the north of this room, 0 if it does not exist
    private final int n;

    // Models.Room ID of room located to the east of this room, 0 if it does not exist
    private final int e;

    // Models.Room ID of room located to the south of this room, 0 if it does not exist
    private final int s;

    // Models.Room ID of room located to the west of this room, 0 if it does not exist
    private final int w;

    private final int lockConditions;


    // The item configured to this room
    private final ArrayList<Artifact> artifactList = new ArrayList<>();
    private final ArrayList<Artifact> lootList = new ArrayList<>();

    private final String[] initialArtifactIds;
    private final String[] initialLootIds;

    private boolean roomHasMonster;

    private Monster monster;

    private Puzzle puzzle;

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for description
    public String getDescription() {
        return description;
    }

    public int getRoomId() {
        return roomId;
    }

    public boolean getIsVisited() {
        return isVisited;
    }


    // This constructs the room instance from passed in parameters
    public Room(int roomId, String description, String name, boolean isVisited, int n, int e, int s, int w, String[] itemIDs, int lockConditions, String[] lootIDs, boolean roomHasMonster) {
        this.roomId = roomId;
        this.description = description;
        this.name = name;
        this.isVisited = isVisited;
        this.n = n;
        this.e = e;
        this.s = s;
        this.w = w;
        this.initialArtifactIds = itemIDs;
        this.initialLootIds = lootIDs;
        this.lockConditions = lockConditions;
        this.roomHasMonster = roomHasMonster;
    }


    // getExit method returns roomId from the read rooms from the data file, for the direction passed in
    // Returns 0 if the player cannot go that way (Invalid direction input, or there is not an adjacent room in that direction
    public int getExit(String direction) {
        return switch (direction) {
            case "N" -> n;
            case "E" -> e;
            case "S" -> s;
            case "W" -> w;
            default -> 0;
        };
    }

    public boolean canNavigateTo(Player player) {
        if (player.getKeys().size() >= lockConditions) {
            return true;
        }
        return false;
    }

    public int getID(){
        return roomId;
    }

    public int getLockConditions() {
        return lockConditions;
    }

    public void setPuzzle(Puzzle p) {
        this.puzzle = p;
    }

    public Puzzle getPuzzle() {
        return this.puzzle;
    }

    // Sets the isVisited flag to be used to determine if the player has visited this room before
    public void setVisited() {
        isVisited = true;
    }

    public boolean getHasMonster() {
        return roomHasMonster;
    }

    public Monster getMonster() {
        return monster;
    }
    public void setMonster(Monster m) {
        monster = m;
    }

    public String[] getInitialArtifactIds() {
        return initialArtifactIds;
    }

    public String[] getInitialLootIds() {
        return initialLootIds;
    }

    // Adds the artifact to the room instance
    public void addLoot(Artifact a) {
        lootList.add(a);
    }
    public void addArtifact(Artifact a) {
        artifactList.add(a);
    }
    public void removeArtifact(Artifact i) {
        artifactList.remove(i);
    }
    public void removeLoot(Artifact i) {
        lootList.remove(i);
    }

    public ArrayList<Artifact> getArtifacts() {
        return artifactList;
    }


    // TODO: To be called when monster is defeated and/or puzzle is solved
    public ArrayList<Artifact> getLoot() {
        return lootList;
    }

    public void playerGetsLoot(Player player) {
        for (Artifact a : lootList) {
            player.addToInventory(a);
        }
    }

    public static Map<Integer,Room> loadRooms(ArrayList<String> readLines) {
        Map<Integer,Room> roomsList = new HashMap<>();
        int i = 0;
        for (String line : readLines) {
            // Skip first line
            if (i == 0) {
                i++;
                continue;
            }
            // splits each line into an array of sections parsed using delimiter '~'
            String[] sections = line.split("~");
            int roomId = Integer.parseInt(sections[0]);
            boolean isVisited = Boolean.parseBoolean(sections[1]);
            String name = sections[2];
            String description = sections[3].replace("\\n", "\n");
            int n = Integer.parseInt(sections[4]);
            int e = Integer.parseInt(sections[5]);
            int s = Integer.parseInt(sections[6]);
            int w = Integer.parseInt(sections[7]);
            int lockConditions = Integer.parseInt(sections[8]);
            String[] items = sections[9].split(",");
            String[] loot = sections[10].split(",");
            boolean roomHasMonster = Boolean.parseBoolean(sections[11]);
            Room room = new Room(roomId, description, name, isVisited, n, e, s, w, items, lockConditions, loot, roomHasMonster);
            // create new room instance from data just read
            roomsList.put(roomId, room);
        }
        return roomsList;
    }
}
