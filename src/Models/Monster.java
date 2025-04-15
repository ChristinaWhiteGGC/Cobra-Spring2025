package Models;

public class Monster {

    private String name;
    private String description;
    private String attack;
    private int health;
    private String location;

    public Monster(String name, String description, String attack, int health, String location) {
        this.name = name;
        this.description = description;
        this.attack = attack;
        this.health = health;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttack() {
        return attack;
    }

    public void setAttack(String attack) {
        this.attack = attack;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void attack(Player player){
        player.setHealth(player.getHealth - damage);
        System.out.println(player.getName() + " has taken " + damage + " from  " + name);
    }
}
