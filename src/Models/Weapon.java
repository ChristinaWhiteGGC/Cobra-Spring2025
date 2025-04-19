package Models;

// Subclass of Artifact for weapon items, now without magical STR boosts.
public class Weapon extends Artifact {
    private int strBoost; // Retained for flavor text, not used.

    public Weapon(String id, String name, String description, String effect, String textEffect) {
        super(id, "weapon", name, description, effect, textEffect);
        this.strBoost = Integer.parseInt(effect.replace("STR", ""));
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