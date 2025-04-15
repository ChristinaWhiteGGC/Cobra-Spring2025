package Models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
    private int roomId;

    // Flags whether we have visited this room before, or not
    private boolean isVisited;

    // The name of the room
    private String name;

    // The description of the room
    private String description;

    // Models.Room ID of room located to the north of this room, 0 if it does not exist
    private int n;

    // Models.Room ID of room located to the east of this room, 0 if it does not exist
    private int e;

    // Models.Room ID of room located to the south of this room, 0 if it does not exist
    private int s;

    // Models.Room ID of room located to the west of this room, 0 if it does not exist
    private int w;


    // The item configured to this room
    private ArrayList<Artifact> artifactList = new ArrayList<Artifact>();

    private String[] initialArtifactIds;


    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for description
    public String getDescription() {
        return description;
    }

    // Getter for roomId
    public int getRoomId() {
        return roomId;
    }

    // Getter for isVisisted
    public boolean getIsVisited() {
        return isVisited;
    }


    // This constructs the room instance from passed in parameters
    public Room(int roomId, String description, String name, boolean isVisited, int n, int e, int s, int w, String[] itemNames) {
        this.roomId = roomId;
        this.description = description;
        this.name = name;
        this.isVisited = isVisited;
        this.n = n;
        this.e = e;
        this.s = s;
        this.w = w;
        this.initialArtifactIds = itemNames;
    }


    // getExit method returns roomId from the read rooms from the data file, for the direction passed in
    // Returns 0 if the player cannot go that way (Invalid direction input, or there is not an adjacent room in that direction
    public int getExit(String direction) {
        switch (direction) {
            case "N":
                return n;
            case "E":
                return e;
            case "S":
                return s;
            case "W":
                return w;
            default:
                // Invalid direction received
                return 0;
        }
    }

    // Sets the isVisited flag to be used to determine if the player has visited this room before
    public void setVisited() {
        isVisited = true;
    }

    public String[] getInitialArtifactIds() {
        return initialArtifactIds;
    }

    // Adds the artifact to the room instance
    public void addLoot(Artifact a) {
        artifactList.add(a);
    }

    public void removeLoot(Artifact i) {
        artifactList.remove(i);
    }

    public void clearLoot() {
        artifactList.clear();
    }

    public ArrayList<Artifact> getArtifacts() {
        return artifactList;
    }

    public boolean doesRoomHaveArtifact() {
        return !artifactList.isEmpty();
    }

    public static Map<Integer,Room> loadRooms(ArrayList<String> readLines) throws IOException {
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
            String[] items = sections[8].split(",");
            Room room = new Room(roomId, description, name, isVisited, n, e, s, w, items);
            // create new room instance from data just read
            roomsList.put(roomId, room);
        }
        return roomsList;
    }
}
