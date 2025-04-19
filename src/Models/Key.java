package Models;

public class Key extends Artifact {
    public Key(String id, String name, String description, String effect, String textEffect) {
        super(id, "key", name, description, effect, textEffect);
    }

    @Override
    public void applyEffects(Player player) { }

    @Override
    public void removeEffects(Player player) { }
}
