package Models;

public class Key extends Artifact {
    public Key(String id, String type, String name, String description, String effect) {
        super(id, type, name, description, effect);
    }

    @Override
    public void applyEffects(Player player) { }

    @Override
    public void removeEffects(Player player) { }
}
