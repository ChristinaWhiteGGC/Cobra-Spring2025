package Models;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class Artifact {
    private String id;
    private String type;
    private String name;
    private String description;
    protected String effect;
    protected String textEffect;

    public Artifact(String id, String type, String name, String description, String effect, String textEffect) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.description = description;
        this.effect = effect;
        this.textEffect = textEffect;
    }

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
                String effectValue = parts[4];
                String textEffect = parts[5];

                Artifact artifact;
                switch (type.toLowerCase()) {
                    case "armor":
                        artifact = new Armor(id, name, description, effectValue, textEffect);
                        break;
                    case "weapon":
                        artifact = new Weapon(id, name, description, effectValue, textEffect);
                        break;
                    case "consumable":
                        artifact = new Consumable(id, name, description, effectValue, textEffect);
                        break;
                    case "key":
                        artifact = new Key(id, name, description, effectValue, textEffect);
                    default:
                        continue;
                }
                artifacts.put(id, artifact);
            }
        }
        return artifacts;
    }

    public boolean pickup(Player player) {
        Artifact existing = player.getArtifactByType(type);
        if (existing != null) {
            return false;
        }
        player.addToInventory(this);
        applyEffects(player);
        return true;
    }

    public String ignore() {
        return "You leave the " + name + " behind.";
    }

    public String swap(Player player, Room room) {
        Artifact existing = player.getArtifactByType(type);
        if (existing == null) {
            return "No " + type + " to swap with. Use 'Pickup' instead.";
        }
        player.removeArtifactEffects(existing);
        existing.removeEffects(player);
        player.getInventory().remove(existing);
        player.getInventory().add(this);
        applyEffects(player);
        room.removeArtifact(this);
        room.addArtifact(existing);
        return "Swapped " + existing.getName() + " for " + name + ".";
    }

    public String seeItem() {
        return name + ": " + description + "\nEffect: " + effect + "\nOptions: Pickup, Swap, Ignore";
    }

    public String useItem(Player player) {
        return "This artifact cannot be used.";
    }

    public abstract void applyEffects(Player player);
    public abstract void removeEffects(Player player);

    public String getId() { return id; }
    public String getType() { return type; }
    public String getName() { return name; }
    public String getEffect() { return effect; }

    public String getTextEffect() { return textEffect; }
}