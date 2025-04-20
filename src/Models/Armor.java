package Models;

// Subclass of Artifact for armor items, now without magical DEF boosts.
public class Armor extends Artifact {
    private int defBoost; // Retained for flavor text, not used.

    public Armor(String id, String name, String description, String effect, String textEffect) {
        super(id, "armor", name, description, effect, textEffect);
        this.defBoost = Integer.parseInt(effect.replace("DEF", ""));
    }

    @Override
    public void applyEffects(Player player) {
        if (this.getEffect().startsWith("DEF")) {
            player.setDef(Integer.parseInt(this.getEffect().replace("DEF", "")));
        }
    }

    @Override
    public void removeEffects(Player player) {
        if (this.getEffect().startsWith("DEF")) {
            player.setDef(Integer.parseInt(this.getEffect().replace("DEF", "")));
        }
    }
}