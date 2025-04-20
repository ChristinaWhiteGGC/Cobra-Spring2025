package Models;

import java.util.*;

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
            if (Objects.equals(locations[j], "-1")) {
                Random random = new Random();
                locations[j] = String.valueOf(random.nextInt(18) + 1);
            }
        }
    }

    public static Attack parseAttack(String attackDescription) {
        try {
            if (attackDescription.contains("deals") && attackDescription.contains("damage")) {
                String name = attackDescription.substring(0, attackDescription.indexOf(" (")).trim();
                int damage = Integer.parseInt(attackDescription.substring(attackDescription.indexOf("deals ") + 6, attackDescription.indexOf(" damage")).trim());

                if (attackDescription.contains("after") && attackDescription.contains("unless")) {
                    int delayTurns = Integer.parseInt(attackDescription.substring(attackDescription.indexOf("after ") + 6, attackDescription.indexOf(" turns")).trim());
                    String condition = attackDescription.substring(attackDescription.indexOf("unless") + 7).trim();
                    return new DelayedConditionalAttack(name, damage, delayTurns, condition);
                } else if (attackDescription.contains("after")) {
                    int delayTurns = Integer.parseInt(attackDescription.substring(attackDescription.indexOf("after ") + 6, attackDescription.indexOf(" turns")).trim());
                    return new DelayedAttack(name, damage, delayTurns);
                } else if (attackDescription.contains("unless")) {
                    String condition = attackDescription.substring(attackDescription.indexOf("unless") + 7).trim();
                    return new ConditionalAttack(name, damage, condition);
                } else {
                    return new ImmediateAttack(name, damage);
                }
            } else if (attackDescription.contains("kills the player after")) {
                String name = attackDescription.substring(0, attackDescription.indexOf(" (")).trim();
                int delayTurns = Integer.parseInt(attackDescription.substring(attackDescription.indexOf("after ") + 6, attackDescription.indexOf(" turns")).trim());
                return new DelayedAttack(name, Integer.MAX_VALUE, delayTurns);
            }
        } catch (Exception e) {
            System.err.println("Error parsing attack: " + attackDescription);
        }
        return null;
    }

    public static ArrayList<Attack> parseAttacks(String attacksDescription) {
        ArrayList<Attack> attacks = new ArrayList<>();
        if (attacksDescription == null || attacksDescription.trim().isEmpty()) {
            return attacks;
        }
        String[] attackDescriptions = attacksDescription.split("\\|\\|");
        for (String attackDescription : attackDescriptions) {
            if (attackDescription != null && !attackDescription.trim().isEmpty()) {
                Attack attack = parseAttack(attackDescription.trim());
                if (attack != null) {
                    attacks.add(attack);
                }
            }
        }
        return attacks;
    }


    public Attack chooseAttack(){
        if (attack == null || attack.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(attack.size());
        return attack.get(index);
    }

    public void executeAttack(Player player) {
        Attack attack = chooseAttack();
        if (attack == null) return;
        
        System.out.println(getName() + " uses " + attack.getName());
        if (isPlayerBlocking) {
            System.out.println("Player is blocking the attack!");
        }
        
        // Execute the attack directly - let the specific attack implementation handle its own logic
        attack.execute(player, isPlayerBlocking);
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