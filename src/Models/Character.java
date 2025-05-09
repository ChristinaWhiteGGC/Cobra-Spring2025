package Models;

public abstract class Character {
    // current room the player is in.
    protected String name;

    protected Room currentRoom;
    protected int priorRoomId;

    protected int health;

    protected int defense;

    protected int strength;

    private int baseHealth;
    private int baseStrength;
    private int baseDefense;

    public void setBaseStrength(int str) {
        this.baseStrength = str;
    }

    public int getBaseStrength() {
        return baseStrength;
    }

    public void setBaseDefense(int def) {
        this.baseDefense = def;
    }

    public int getBaseDefense() {
        return baseDefense;
    }

    public void setBaseHealth(int hp) {
        this.baseHealth = hp;
    }

    public int getBaseHealth() {
        return baseHealth;
    }

    public void setDef(int defense) {
        this.defense = defense;
    }

    public int getDef() {
        return baseDefense + defense;
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
        return strength + baseStrength;
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

    public void takeDamage(int damage){
        health -= damage;
        if (health < 0){
            health = 0;
        }
        System.out.println(name + " has taken " + damage + " damage. " + name + " now has " + health + " health.");
    }

}
