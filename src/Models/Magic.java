package Models;

// Subclass of Artifact for magic items with unique effects (resurrection, puzzle-solving).
public class Magic extends Artifact {
    private boolean used;     // Tracks if resurrection has been used

    // Constructor to initialize a magic artifact.
    public Magic(String id, String name, String description, String effect, String textEffect) {
        super(id, "magic", name, description, effect, textEffect);
    }

    // Apply magic effect when equipped (e.g., prepare for resurrection).
    @Override
    public void applyEffects(Player player) {
    }

    // Remove magic effect when unequipped.
    @Override
    public void removeEffects(Player player) {
    }

    // Use the magic artifact (e.g., solve a puzzle).
    @Override
    public String useItem(Player player) {
        return "";
    }
}