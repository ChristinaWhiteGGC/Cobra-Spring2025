package Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Monster extends Character {
    private String description;

    private String[] locations;

    public Monster(String name, String description, int attack, int health, String[] locations) {
        super(name, health, attack, 0);
        this.description = description;
        this.locations = locations;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getLocations() {
        return locations;
    }

    public void setLocations(String[] locations) {
        this.locations = locations;
    }

    public void attack(Player player){
        player.setHp(player.getHp() - (getStr() - player.getDef()));
        System.out.println(player.getName() + " has taken " + getStr() + " from  " + name);
    }

    public static Map<String,Monster> loadMonsters(ArrayList<String> readLines) {
        Map<String,Monster> monstersList = new HashMap<>();
        int i = 0;
        for (String line : readLines) {
            // Skip first line
            if (i == 0) {
                i++;
                continue;
            }
            // splits each line into an array of sections parsed using delimiter '~'
            String[] sections = line.split("~");
            String name = sections[0];
            String description = sections[1].replace("\\n", "\n");
            String attacks = sections[2].replace("\\n", "\n");
            int health = Integer.parseInt(sections[3]);
            String[] locations = sections[4].split("\\|\\|");
            Monster monster = new Monster(name, description, 0, health, locations);
            monstersList.put(name, monster);
        }
        return monstersList;
    }
}
