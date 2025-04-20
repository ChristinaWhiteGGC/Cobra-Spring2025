package Models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Monster{
    private String name;
    private String description;

    private ArrayList<Attack> attack;
    private int health;

    private String[] locations;
    private boolean isDefeated;
    private boolean isPlayerBlocking;

    public Monster(String name, String description, ArrayList<Attack> attack, int health, String[] locations, boolean isDefeated, boolean isPlayerBlocking) {
        this.name = name;
        this.description = description;
        this.attack = attack;
        this.health = health;
        this.locations = locations;
        this.isDefeated = isDefeated;
        this.isPlayerBlocking = isPlayerBlocking;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Attack> getAttack() {
        return attack;
    }

    public void setAttack(ArrayList<Attack> attack) {
        this.attack = attack;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
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

    public boolean isDefeated() {
        return isDefeated;
    }

    public void setDefeated(boolean defeated) {
        isDefeated = defeated;
    }

    public boolean isPlayerBlocking() {
        return isPlayerBlocking;
    }

    public void setPlayerBlocking(boolean playerBlocking) {
        isPlayerBlocking = playerBlocking;
    }

    public static Map<String, Monster> loadMonsters(ArrayList<String> readLines) {
        Map<String, Monster> monstersList = new HashMap<>();
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
            String attacksDescription = sections[2].replace("\\n", "\n");
            int health = Integer.parseInt(sections[3]);
            String[] locations = sections[4].split("\\|\\|");

            // Parse attacks
            ArrayList<Attack> attacks = parseAttacks(attacksDescription);

            Monster monster = new Monster(name, description, attacks, health, locations, false, false);
            monster.assignRandomLocation();
            monstersList.put(name, monster);
        }
        return monstersList;
    }

    public void assignRandomLocation(){
        for (int j = 0; j < locations.length; j++) {
            if (locations[j] == "-1") {
                Random random = new Random();
                locations[j] = String.valueOf(random.nextInt(18) + 1);
            }
        }
    }

    public static ArrayList<Attack> parseAttacks(String attacksDescription) {
        ArrayList<Attack> attacks = new ArrayList<>();
        String[] attackDescriptions = attacksDescription.split("\\|\\|");

        for (String attackDescription : attackDescriptions) {
            Attack attack = parseAttack(attackDescription);
            if (attack != null) {
                attacks.add(attack);
            }
        }
        return attacks;
    }

    public static Attack parseAttack(String attackDescription) {
        if (attackDescription.contains("deals") && attackDescription.contains("damage")) {
            String name = attackDescription.substring(0, attackDescription.indexOf(" ("));
            int damage = Integer.parseInt(attackDescription.substring(attackDescription.indexOf("deals ") + 6, attackDescription.indexOf(" damage")));
            if (attackDescription.contains("after")) {
                int delayTurns = Integer.parseInt(attackDescription.substring(attackDescription.indexOf("after ") + 6, attackDescription.indexOf(" turns")));
                return new DelayedAttack(name, damage, delayTurns);
            } else if (attackDescription.contains("unless")) {
                String condition = attackDescription.substring(attackDescription.indexOf("unless") + 7);
                return new ConditionalAttack(name, damage, condition);
            } else {
                return new ImmediateAttack(name, damage);
            }
        }
        return null;
    }


    public Attack chooseAttack(){
        Random random = new Random();
        int index = random.nextInt(attack.size());
        return attack.get(index);
    }

    public void executeAttack(Player player) {
        Attack attack = chooseAttack();
        System.out.println(player.getName() + " uses " + attack.getName());

        if (isPlayerBlocking) {
            System.out.println("Player is blocking the attack!");
            if (attack instanceof ImmediateAttack) {
                ((ImmediateAttack) attack).execute(player, true); // Pass true to indicate blocking
            } else if (attack instanceof DelayedAttack) {
                DelayedAttack delayedAttack = (DelayedAttack) attack;
                if (delayedAttack.delayIsReady()) {
                    delayedAttack.execute(player, true); // Pass true to indicate blocking
                } else {
                    delayedAttack.decrementDelay();
                }
            } else if (attack instanceof ConditionalAttack) {
                ((ConditionalAttack) attack).execute(player, true); // Pass true to indicate blocking
            }
        } else {
            if (attack instanceof ImmediateAttack) {
                ((ImmediateAttack) attack).execute(player, false); // Pass false to indicate not blocking
            } else if (attack instanceof DelayedAttack) {
                DelayedAttack delayedAttack = (DelayedAttack) attack;
                if (delayedAttack.delayIsReady()) {
                    delayedAttack.execute(player, false); // Pass false to indicate not blocking
                } else {
                    delayedAttack.decrementDelay();
                }
            } else if (attack instanceof ConditionalAttack) {
                ((ConditionalAttack) attack).execute(player, false); // Pass false to indicate not blocking
            }
        }
    }



    public void takeDamage(int damage){
        if (isPlayerBlocking){
            health -= damage/2;
        }
        else {
            health -= damage;
        }
        if (health < 0){
            health = 0;
        }
        System.out.println(name + " has taken " + damage + " damage. " + name + " now has " + health + " health.");
    }
}
