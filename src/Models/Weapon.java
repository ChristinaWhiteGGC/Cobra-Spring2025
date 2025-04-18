package Models;

// Subclass of Artifact for weapon items, now without magical STR boosts.
public class Weapon extends Artifact {
    private int strBoost; // Retained for flavor text, not used.

    public Weapon(String id, String name, String description, int strBoost) {
        super(id, "weapon", name, description, "+" + strBoost + " STR");
        this.strBoost = strBoost;
    }

    @Override
    public void applyEffects(Player player) {
        // No effect (non-magical)
    }

    @Override
    public void removeEffects(Player player) {
        // No effect
    }
}