package Models;

// Subclass of Artifact for armor items, now without magical DEF boosts.
public class Armor extends Artifact {
    private int defBoost; // Retained for flavor text, not used.

    public Armor(String id, String name, String description, int defBoost) {
        super(id, "armor", name, description, "+" + defBoost + " DEF");
        this.defBoost = defBoost;
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