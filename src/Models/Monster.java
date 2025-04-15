package Models;

public class Monster extends Character {
    private String description;
    private Room location;

    public Monster(String name, String description, int attack, int health, Room location) {
        super(name, health, attack, 0);
        this.description = description;
        this.location = location;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Room getLocation() {
        return location;
    }

    public void setLocation(Room location) {
        this.location = location;
    }

    public void attack(Player player){
        player.setHp(player.getHp() - (getStr() - player.getDef()));
        System.out.println(player.getName() + " has taken " + getStr() + " from  " + name);
    }
}
