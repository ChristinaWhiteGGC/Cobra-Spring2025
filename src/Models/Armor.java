package Models;

// Subclass of Artifact for armor items, now without magical DEF boosts.
public class Armor extends Artifact {
    private int defBoost; // Retained for flavor text, but not used for gameplay.

    // Constructor to initialize an armor artifact.
    public Armor(String id, String name, String description, int defBoost) {
        super(id, "armor", name, description, "+" + defBoost + " DEF");
        this.defBoost = defBoost;
    }

    // No magical effect applied.
    @Override
    public void applyEffects(Player player) {
        // No effect (previously increased DEF)
    }

    // No magical effect removed.
    @Override
    public void removeEffects(Player player) {
        // No effect (previously decreased DEF)
    }
}