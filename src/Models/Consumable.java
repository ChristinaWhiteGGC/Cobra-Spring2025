package Models;

// Subclass of Artifact for weapon items, now without magical STR boosts.
public class Weapon extends Artifact {
    private int strBoost; // Retained for flavor text, but not used for gameplay.

    // Constructor to initialize a weapon artifact.
    public Weapon(String id, String name, String description, int strBoost) {
        super(id, "weapon", name, description, "+" + strBoost + " STR");
        this.strBoost = strBoost;
    }

    // No magical effect applied.
    @Override
    public void applyEffects(Player player) {
        // No effect (previously increased STR)
    }

    // No magical effect removed.
    @Override
    public void removeEffects(Player player) {
        // No effect (previously decreased STR)
    }
}