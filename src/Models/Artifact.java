package Models;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// Abstract base class for all artifacts (armor, weapons, consumables).
// Artifacts are now collectible items without magical effects.
public abstract class Artifact {
    private String id;          // Unique identifier (e.g., "A1")
    private String type;        // Type of artifact (e.g., "armor", "weapon")
    private String name;        // Name (e.g., "Bronze Helmet")
    private String description; // Description (e.g., "A sturdy helm.")
    protected String effect;    // Effect description (for flavor, no gameplay impact)

    // Constructor to initialize an artifact.
    public Artifact(String id, String type, String name, String description, String effect) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.effect = effect;
    }

    // Loads artifacts from Artifacts.txt.
    public static Map<String, Artifact> loadArtifacts(String filePath) throws IOException {
        Map<String, Artifact> artifacts = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length < 5) continue;
                String id = parts[0];
                String type = parts[1];
                String name = parts[2];
                String description = parts[3];
                int effectValue = Integer.parseInt(parts[4]); // Still parsed for flavor text

                Artifact artifact;
                switch (type.toLowerCase()) {
                    case "armor":
                        artifact = new Armor(id, name, description, effectValue);
                        break;
                    case "weapon":
                        artifact = new Weapon(id, name, description, effectValue);
                        break;
                    case "consumable":
                        artifact = new Consumable(id, name, description, effectValue);
                        break;
                    default:
                        continue;
                }
                artifacts.put(id, artifact);
            }
        }
        return artifacts;
    }

    // Adds the artifact to the player's inventory without applying effects.
    public boolean pickup(Player player) {
        if (player.getArtifactByType(type) != null) {
            return false; // Still enforces one per type for inventory management
        }
        return player.addToInventory(this);
    }

    // Ignores the artifact.
    public String ignore() {
        return "You leave the " + name + " behind.";
    }

    // Swaps this artifact with an existing one of the same type in the inventory.
    public String swap(Player player, Room room) {
        Artifact existing = player.getArtifactByType(type);
        if (existing == null) {
            return "No " + type + " to swap with. Use 'Pickup' instead.";
        }
        player.getInventory().remove(existing);
        player.getInventory().add(this);
        room.addLoot(existing);
        return "Swapped " + existing.getName() + " for " + name + ".";
    }

    // Displays the artifact's details (effect is now just flavor text).
    public String seeItem() {
        return name + ": " + description + "\nEffect: " + effect + "\nOptions: Pickup, Swap, Ignore";
    }

    // No magical effect; just a placeholder message.
    public String useItem(Player player) {
        return name + " has no effect when used.";
    }

    // Getters
    public String getId() { return id; }
    public String getType() { return type; }
    public String getName() { return name; }
    public String getEffect() { return effect; }
}