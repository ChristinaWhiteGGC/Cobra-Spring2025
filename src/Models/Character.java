package Models;

public abstract class Character {
    // current room the player is in.
    protected String name;

    protected Room currentRoom;
    protected int priorRoomId;

    protected int health;

    protected int defense;

    protected int strength;


    public void setDef(int defense) {
        this.defense = defense;
    }

    public int getDef() {
        return defense;
    }

    public void setHp(int health) {
        this.health = health;
    }

    public int getHp() {
        return health;
    }

    public void setStr(int strength) {
        this.strength = strength;
    }

    public int getStr() {
        return strength;
    }

    public Character(String name, int baseHp, int baseStrength, int baseDefense) {
        this.name = name;
        this.health = baseHp;
        this.strength = baseStrength;
        this.defense = baseDefense;
    }


    public void setName(String name) {
        this.name = name;
    }

    // Getter for currentRoom
    public String getName() {
        return name;
    }

    // Setter for currentRoom
    public void setRoom(Room room) {
        if(this.currentRoom != null) {
            priorRoomId = currentRoom.getRoomId();
        }
        this.currentRoom = room;
    }
    public int getPriorRoom(){
        return this.priorRoomId;
    }


    // Getter for currentRoom
    public Room getRoom() {
        return this.currentRoom;
    }

}
