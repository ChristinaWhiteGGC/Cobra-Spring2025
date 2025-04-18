package Models;

// Subclass of Artifact for magic items with unique effects (resurrection, puzzle-solving).
public class Magic extends Artifact {
    private String effectType; // "resurrection" or "puzzle-solve"
    private int uses;         // Number of uses (1 for Crook, 3 for Amulet)
    private boolean used;     // Tracks if resurrection has been used

    // Constructor to initialize a magic artifact.
    public Magic(String id, String name, String description, String effectType, int uses) {
        super(id, "magic", name, description, effectType.equals("resurrection") ? "Resurrects once" : "Solves 3 puzzles");
        this.effectType = effectType;
        this.uses = uses;
        this.used = false;
    }

    // Apply magic effect when equipped (e.g., prepare for resurrection).
    @Override
    public void applyEffects(Player player) {
        if (effectType.equals("resurrection")) {
            // Mark player as resurrectable
            player.setResurrectable(true);
        }
    }

    // Remove magic effect when unequipped.
    @Override
    public void removeEffects(Player player) {
        if (effectType.equals("resurrection")) {
            player.setResurrectable(false);
        }
    }

    // Use the magic artifact (e.g., solve a puzzle).
    @Override
    public String useItem(Player player) {
        if (effectType.equals("puzzle-solve")) {
            if (uses > 0) {
                Room currentRoom = player.getGame().getCurrentRoom();
                Puzzle puzzle = currentRoom.getPuzzle();
                if (puzzle != null && !puzzle.isSolved()) {
                    puzzle.setSolved(true);
                    uses--;
                    return name + " solved the puzzle! " + uses + " uses left.";
                }
                return "No puzzle to solve here.";
            }
            return name + " has no uses left.";
        }
        return name + " cannot be used directly.";
    }

    // Handle resurrection (called by Player when HP <= 0).
    public boolean triggerResurrection(Player player) {
        if (effectType.equals("resurrection") && !used) {
            used = true;
            player.setHp(player.getMaxHp() / 2); // Resurrect at half health
            player.setResurrectable(false);
            return true;
        }
        return false;
    }

    public String getEffectType() { return effectType; }
    public int getUses() { return uses; }
}